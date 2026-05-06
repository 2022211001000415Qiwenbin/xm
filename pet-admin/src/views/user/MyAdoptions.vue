
<template>
  <div class="my-adoptions-container">
    <el-card class="search-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="申请状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="待审核" value="待审核" />
            <el-option label="通过" value="通过" />
            <el-option label="拒绝" value="拒绝" />
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
          <span>我的领养申请</span>
        </div>
      </template>
      <el-table :data="tableData" stripe border style="width: 100%">
        <el-table-column prop="applyId" label="申请ID" width="80" />
        <el-table-column prop="petId" label="宠物ID" width="100" />
        <el-table-column prop="applyInfo" label="申请信息" show-overflow-tooltip />
        <el-table-column prop="auditStatus" label="审核状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.auditStatus)">
              {{ scope.row.auditStatus }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="auditRemark" label="审核备注" show-overflow-tooltip />
        <el-table-column prop="applyTime" label="申请时间" width="180">
          <template #default="scope">
            {{ formatDateTime(scope.row.applyTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="auditTime" label="审核时间" width="180">
          <template #default="scope">
            {{ formatDateTime(scope.row.auditTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="scope">
            <el-button link type="primary" size="small" @click="handleView(scope.row)">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>

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
        <el-descriptions-item label="宠物ID">{{ currentApplication.petId }}</el-descriptions-item>
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
import { getMyAdoptions } from '@/api/pet'

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
    '拒绝': 'danger'
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
      userId: store.state.user.userId  // 只查询当前用户的申请
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
