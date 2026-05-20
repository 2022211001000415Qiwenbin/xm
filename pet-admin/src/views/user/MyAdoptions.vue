
<template>
  <div class="my-adoptions-container">
    <el-card class="search-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="申请状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="待审核" value="待审核" />
            <el-option label="通过" value="通过" />
            <el-option label="拒绝" value="拒绝" />
            <el-option label="已取消" value="已取消" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="list-card">
      <template #header>
        <div class="card-header">
          <span>我的领养申请</span>
        </div>
      </template>

      <div v-if="tableData.length === 0" class="empty-tip">
        <el-empty description="暂无领养申请记录" />
      </div>

      <div v-else class="adoption-list">
        <div v-for="item in tableData" :key="item.applyId" class="adoption-item">
          <div class="item-fields-row">
            <div class="item-field">
              <span class="field-label">申请ID</span>
              <span class="field-value">{{ item.applyId }}</span>
            </div>
            <div class="item-field">
              <span class="field-label">申请人</span>
              <span class="field-value">{{ item.applicantName || '--' }}</span>
            </div>
            <div class="item-field">
              <span class="field-label">联系方式</span>
              <span class="field-value">{{ item.applicantPhone || '--' }}</span>
            </div>
            <div class="item-field">
              <span class="field-label">职业</span>
              <span class="field-value">{{ item.applicantOccupation || '--' }}</span>
            </div>
            <div class="item-field">
              <span class="field-label">家庭地址</span>
              <span class="field-value">{{ item.applicantAddress || '--' }}</span>
            </div>
            <div class="item-field">
              <span class="field-label">养宠经验</span>
              <span class="field-value">{{ item.applicantExperience || '无' }}</span>
            </div>
            <div class="item-field">
              <span class="field-label">申请信息</span>
              <span class="field-value">{{ item.applyInfo || '--' }}</span>
            </div>
            <div class="item-field">
              <span class="field-label">审核状态</span>
              <el-tag :type="getStatusType(item.auditStatus)" size="default">{{ item.auditStatus }}</el-tag>
            </div>
            <div class="item-field">
              <span class="field-label">审核备注</span>
              <span class="field-value">{{ item.auditRemark || '无' }}</span>
            </div>
            <div class="item-field">
              <span class="field-label">申请时间</span>
              <span class="field-value">{{ formatDateTime(item.applyTime) || '--' }}</span>
            </div>
            <div class="item-field">
              <span class="field-label">审核时间</span>
              <span class="field-value">{{ formatDateTime(item.auditTime) || '未审核' }}</span>
            </div>
          </div>
          <div class="item-actions">
            <el-button
              v-if="item.auditStatus === '待审核'"
              type="danger"
              size="small"
              @click="handleCancel(item)"
            >
              取消申请
            </el-button>
          </div>
        </div>
      </div>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="申请详情"
      width="600px"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="申请ID">{{ currentApplication.applyId }}</el-descriptions-item>
        <el-descriptions-item label="申请人姓名">{{ currentApplication.applicantName || '--' }}</el-descriptions-item>
        <el-descriptions-item label="联系方式">{{ currentApplication.applicantPhone || '--' }}</el-descriptions-item>
        <el-descriptions-item label="职业">{{ currentApplication.applicantOccupation || '--' }}</el-descriptions-item>
        <el-descriptions-item label="家庭地址" :span="2">{{ currentApplication.applicantAddress || '--' }}</el-descriptions-item>
        <el-descriptions-item label="养宠经验" :span="2">{{ currentApplication.applicantExperience || '无' }}</el-descriptions-item>
        <el-descriptions-item label="申请信息" :span="2">{{ currentApplication.applyInfo }}</el-descriptions-item>
        <el-descriptions-item label="审核状态">
          <el-tag :type="getStatusType(currentApplication.auditStatus)">
            {{ currentApplication.auditStatus }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="审核备注">{{ currentApplication.auditRemark || '无' }}</el-descriptions-item>
        <el-descriptions-item label="申请时间">{{ formatDateTime(currentApplication.applyTime) }}</el-descriptions-item>
        <el-descriptions-item label="审核时间">{{ formatDateTime(currentApplication.auditTime) || '未审核' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useStore } from 'vuex'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMyAdoptions, cancelAdoption } from '@/api/pet'

const store = useStore()

const searchForm = reactive({
  status: ''
})

const tableData = ref([])
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const detailDialogVisible = ref(false)
const currentApplication = ref({})

const getStatusType = (status) => {
  const typeMap = {
    '待审核': 'warning',
    '通过': 'success',
    '拒绝': 'danger',
    '已取消': 'info'
  }
  return typeMap[status] || ''
}

const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  const date = new Date(dateTime)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

const loadData = async () => {
  try {
    const res = await getMyAdoptions({
      current: pagination.currentPage,
      size: pagination.pageSize,
      auditStatus: searchForm.status,
      userId: store.state.user.userId
    })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    // 错误信息已在 request.js 的响应拦截器中处理
  }
}

const handleSearch = () => {
  pagination.currentPage = 1
  loadData()
}

const handleReset = () => {
  searchForm.status = ''
  handleSearch()
}

const handleView = (row) => {
  currentApplication.value = { ...row }
  detailDialogVisible.value = true
}

const handleCancel = async (row) => {
  try {
    await ElMessageBox.confirm('确定要取消这个领养申请吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await cancelAdoption(row.applyId)
    ElMessage.success('领养申请已取消')
    loadData()
  } catch (error) {
    // 用户取消操作或请求失败
  }
}

const handleSizeChange = (val) => {
  pagination.pageSize = val
  loadData()
}

const handleCurrentChange = (val) => {
  pagination.currentPage = val
  loadData()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped lang="scss">
.my-adoptions-container {
  padding: 20px;

  .search-card {
    margin-bottom: 20px;
  }

  .list-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .empty-tip {
      padding: 40px 0;
    }

    .adoption-list {
      display: flex;
      flex-direction: column;
      gap: 16px;
    }

    .adoption-item {
      display: flex;
      flex-direction: column;
      gap: 12px;
      padding: 20px 24px;
      background: #fafafa;
      border-radius: 8px;
      border: 1px solid #ebeef5;
      transition: all 0.3s;

      &:hover {
        background: #f0f7ff;
        border-color: #d0e3ff;
        box-shadow: 0 2px 8px rgba(64, 158, 255, 0.1);
      }

      .item-fields-row {
        display: flex;
        flex-direction: column;
        gap: 14px;
      }

      .item-field {
        display: flex;
        align-items: center;
        gap: 12px;

        .field-label {
          color: #909399;
          font-size: 16px;
          white-space: nowrap;
          min-width: 90px;
          text-align: right;
        }

        .field-value {
          color: #303133;
          font-size: 17px;
          font-weight: 500;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }
      }

      .item-actions {
        display: flex;
        justify-content: flex-end;
        padding-top: 10px;
        border-top: 1px dashed #e4e7ed;
      }
    }

    .pagination-container {
      margin-top: 20px;
      display: flex;
      justify-content: flex-end;
    }
  }
}
</style>
