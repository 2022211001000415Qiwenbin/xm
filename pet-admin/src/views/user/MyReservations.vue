
<template>
  <div class="my-reservations-container">
    <el-card class="search-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="预约状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="待确认" value="待确认" />
            <el-option label="已确认" value="已确认" />
            <el-option label="已取消" value="已取消" />
            <el-option label="已完成" value="已完成" />
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
          <span>我的预约</span>
        </div>
      </template>
      <el-table :data="tableData" stripe border style="width: 100%">
        <el-table-column prop="reserveId" label="预约ID" width="80" />
        <el-table-column prop="petId" label="宠物ID" width="100" />
        <el-table-column prop="reserveTime" label="预约时间" width="180">
          <template #default="scope">
            {{ formatDateTime(scope.row.reserveTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="reserveAddress" label="预约地址" show-overflow-tooltip />
        <el-table-column prop="contactPerson" label="联系人" width="100" />
        <el-table-column prop="contactPhone" label="联系电话" width="120" />
        <el-table-column prop="reserveStatus" label="预约状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.reserveStatus)">
              {{ scope.row.reserveStatus }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reserveRemark" label="备注" show-overflow-tooltip />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button link type="primary" size="small" @click="handleView(scope.row)">查看详情</el-button>
            <el-button
              v-if="scope.row.reserveStatus === '待确认'"
              link
              type="danger"
              size="small"
              @click="handleCancel(scope.row)"
            >
              取消预约
            </el-button>
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
      title="预约详情"
      width="600px"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="预约ID">{{ currentReservation.reserveId }}</el-descriptions-item>
        <el-descriptions-item label="宠物ID">{{ currentReservation.petId }}</el-descriptions-item>
        <el-descriptions-item label="预约时间" :span="2">{{ formatDateTime(currentReservation.reserveTime) }}</el-descriptions-item>
        <el-descriptions-item label="预约地址" :span="2">{{ currentReservation.reserveAddress }}</el-descriptions-item>
        <el-descriptions-item label="联系人">{{ currentReservation.contactPerson }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentReservation.contactPhone }}</el-descriptions-item>
        <el-descriptions-item label="预约状态">
          <el-tag :type="getStatusType(currentReservation.reserveStatus)">
            {{ currentReservation.reserveStatus }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="备注">{{ currentReservation.reserveRemark || '无' }}</el-descriptions-item>
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
import { getMyReservations, cancelReservation } from '@/api/pet'

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
const currentReservation = ref({})

const getStatusType = (status) => {
  const typeMap = {
    '待确认': 'warning',
    '已确认': 'success',
    '已取消': 'info',
    '已完成': 'primary'
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
    const res = await getMyReservations({
      current: pagination.currentPage,
      size: pagination.pageSize,
      reserveStatus: searchForm.status,
      userId: store.state.user.userId  // 只查询当前用户的预约
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
  currentReservation.value = { ...row }
  detailDialogVisible.value = true
}

const handleCancel = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要取消这个预约吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await cancelReservation(row.reserveId)
    ElMessage.success('预约已取消')
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
.my-reservations-container {
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
