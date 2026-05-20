<template>
  <div class="adoption-application">
    <el-card class="search-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="申请人姓名">
          <el-input v-model="searchForm.applicantName" placeholder="请输入申请人姓名" clearable />
        </el-form-item>
        <el-form-item label="申请状态">
          <el-select v-model="searchForm.status" placeholder="请选择申请状态" clearable>
            <el-option label="待审核" value="待审核" />
            <el-option label="已通过" value="通过" />
            <el-option label="已拒绝" value="拒绝" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>领养申请列表</span>
        </div>
      </template>
      <el-table :data="tableData" stripe border style="width: 100%">
        <el-table-column prop="applyId" label="ID" width="80" />
        <el-table-column prop="applicantName" label="申请人姓名" width="100" />
        <el-table-column prop="applicantPhone" label="联系方式" width="120" />
        <el-table-column prop="applicantOccupation" label="职业" width="100" />
        <el-table-column prop="applicantAddress" label="家庭地址" width="150" show-overflow-tooltip />
        <el-table-column prop="applicantExperience" label="养宠经验" width="150" show-overflow-tooltip />
        <el-table-column prop="petName" label="宠物名称" width="120" />
        <el-table-column prop="applyInfo" label="申请信息" width="200" show-overflow-tooltip />
        <el-table-column prop="auditStatus" label="审核状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.auditStatus)">
              {{ scope.row.auditStatus }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="auditRemark" label="审核备注" width="150" show-overflow-tooltip />
        <el-table-column prop="adminName" label="审核人" width="100" />
        <el-table-column prop="applyTime" label="申请时间" width="160" />
        <el-table-column prop="auditTime" label="审核时间" width="160" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button
              v-if="scope.row.auditStatus === '待审核'"
              link
              type="success"
              size="small"
              @click="handleApprove(scope.row)"
            >
              通过
            </el-button>
            <el-button
              v-if="scope.row.auditStatus === '待审核'"
              link
              type="danger"
              size="small"
              @click="handleReject(scope.row)"
            >
              拒绝
            </el-button>
            <el-button link type="primary" size="small" @click="handleView(scope.row)">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
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
        <el-descriptions-item label="宠物名称">{{ currentApplication.petName || '--' }}</el-descriptions-item>
        <el-descriptions-item label="申请信息" :span="2">{{ currentApplication.applyInfo }}</el-descriptions-item>
        <el-descriptions-item label="审核状态">
          <el-tag :type="getStatusType(currentApplication.auditStatus)">
            {{ currentApplication.auditStatus }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="审核备注">{{ currentApplication.auditRemark || '无' }}</el-descriptions-item>
        <el-descriptions-item label="审核人">{{ currentApplication.adminName || '--' }}</el-descriptions-item>
        <el-descriptions-item label="申请时间">{{ currentApplication.applyTime }}</el-descriptions-item>
        <el-descriptions-item label="审核时间">{{ currentApplication.auditTime || '未审核' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAdoptionApplicationPage, reviewAdoptionApplication } from '@/api/adoptionApplication'

const searchForm = reactive({
  applicantName: '',
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
    '拒绝': 'danger'
  }
  return typeMap[status] || ''
}

const loadData = async () => {
  try {
    const res = await getAdoptionApplicationPage({
      current: pagination.currentPage,
      size: pagination.pageSize,
      realName: searchForm.applicantName,
      auditStatus: searchForm.status
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
  searchForm.applicantName = ''
  searchForm.status = ''
  handleSearch()
}

const handleApprove = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要通过ID为${row.applyId}的领养申请吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'success'
    })
    await reviewAdoptionApplication({
      applyId: row.applyId,
      auditStatus: '通过',
      auditAdmin: 1
    })
    ElMessage.success('已通过该申请')
    loadData()
  } catch (error) {
    // 用户取消操作
  }
}

const handleReject = async (row) => {
  try {
    await ElMessageBox.prompt('请输入拒绝原因', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(({ value }) => {
      return reviewAdoptionApplication({
        applyId: row.applyId,
        auditStatus: '拒绝',
        auditAdmin: 1,
        auditRemark: value
      })
    }).then(() => {
      ElMessage.success('已拒绝该申请')
      loadData()
    })
  } catch (error) {
    // 用户取消操作
  }
}

const handleView = (row) => {
  currentApplication.value = { ...row }
  detailDialogVisible.value = true
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
.adoption-application {
  .search-card {
    margin-bottom: 20px;
  }

  .table-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .pagination-container {
      margin-top: 20px;
      display: flex;
      justify-content: flex-end;
    }
  }
}
</style>
