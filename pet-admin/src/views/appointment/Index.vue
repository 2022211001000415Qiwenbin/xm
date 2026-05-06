<template>
  <div class="appointment">
    <el-card class="search-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="预约人姓名">
          <el-input v-model="searchForm.appointmentName" placeholder="请输入预约人姓名" clearable />
        </el-form-item>
        <el-form-item label="预约日期">
          <el-date-picker
            v-model="searchForm.appointmentDate"
            type="date"
            placeholder="选择日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            clearable
          />
        </el-form-item>
        <el-form-item label="预约状态">
          <el-select v-model="searchForm.status" placeholder="请选择预约状态" clearable>
            <el-option label="待确认" value="待确认" />
            <el-option label="已确认" value="已确认" />
            <el-option label="已完成" value="已完成" />
            <el-option label="已取消" value="已取消" />
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
          <span>预约列表</span>
        </div>
      </template>
      <el-table :data="tableData" stripe border style="width: 100%">
        <el-table-column prop="reserveId" label="ID" width="80" />
        <el-table-column prop="userId" label="用户ID" width="100" />
        <el-table-column prop="petId" label="宠物ID" width="100" />
        <el-table-column prop="reserveTime" label="预约时间" width="160" />
        <el-table-column prop="reserveAddress" label="预约地址" width="200" show-overflow-tooltip />
        <el-table-column prop="contactPerson" label="联系人" width="120" />
        <el-table-column prop="contactPhone" label="联系电话" width="130" />
        <el-table-column prop="reserveStatus" label="预约状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.reserveStatus)">
              {{ scope.row.reserveStatus }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="confirmAdmin" label="确认管理员" width="120" />
        <el-table-column prop="reserveRemark" label="备注" show-overflow-tooltip />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button
              v-if="scope.row.reserveStatus === '待确认'"
              link
              type="success"
              size="small"
              @click="handleConfirm(scope.row)"
            >
              确认
            </el-button>
            <el-button
              v-if="scope.row.reserveStatus === '待确认'"
              link
              type="danger"
              size="small"
              @click="handleCancel(scope.row)"
            >
              取消
            </el-button>
            <el-button
              v-if="scope.row.reserveStatus === '已确认'"
              link
              type="primary"
              size="small"
              @click="handleComplete(scope.row)"
            >
              完成
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
      title="预约详情"
      width="600px"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="预约ID">{{ currentAppointment.reserveId }}</el-descriptions-item>
        <el-descriptions-item label="用户ID">{{ currentAppointment.userId }}</el-descriptions-item>
        <el-descriptions-item label="宠物ID">{{ currentAppointment.petId }}</el-descriptions-item>
        <el-descriptions-item label="预约时间">{{ currentAppointment.reserveTime }}</el-descriptions-item>
        <el-descriptions-item label="预约地址">{{ currentAppointment.reserveAddress }}</el-descriptions-item>
        <el-descriptions-item label="联系人">{{ currentAppointment.contactPerson }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentAppointment.contactPhone }}</el-descriptions-item>
        <el-descriptions-item label="预约状态">
          <el-tag :type="getStatusType(currentAppointment.reserveStatus)">
            {{ currentAppointment.reserveStatus }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="确认管理员">{{ currentAppointment.confirmAdmin }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ currentAppointment.reserveRemark || '无' }}</el-descriptions-item>
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
import { getAppointmentPage, confirmAppointment, completeAppointment, cancelAppointment } from '@/api/appointment'

const searchForm = reactive({
  appointmentName: '',
  status: ''
})

const tableData = ref([])
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const detailDialogVisible = ref(false)
const currentAppointment = ref({})

const getStatusType = (status) => {
  const typeMap = {
    '待确认': 'warning',
    '已确认': 'success',
    '已完成': 'info',
    '已取消': 'danger'
  }
  return typeMap[status] || ''
}

const loadData = async () => {
  try {
    const res = await getAppointmentPage({
      current: pagination.currentPage,
      size: pagination.pageSize,
      realName: searchForm.appointmentName,
      reserveStatus: searchForm.status
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
  searchForm.appointmentName = ''
  searchForm.status = ''
  handleSearch()
}

const handleConfirm = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要确认ID为${row.reserveId}的预约吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'success'
    })
    await confirmAppointment({
      reserveId: row.reserveId,
      confirmAdmin: 1
    })
    ElMessage.success('已确认该预约')
    loadData()
  } catch (error) {
    // 用户取消操作
  }
}

const handleCancel = async (row) => {
  try {
    const { value: cancelReason } = await ElMessageBox.prompt('请输入取消原因', '取消预约', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputPattern: /\S+/,
      inputErrorMessage: '取消原因不能为空'
    })
    
    await cancelAppointment({
      id: row.reserveId,
      cancelReason: cancelReason,
      cancelPerson: 'admin' // 可以从登录信息中获取
    })
    ElMessage.success('已取消该预约')
    loadData()
  } catch (error) {
    // 用户取消操作
  }
}

const handleComplete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要将ID为${row.reserveId}的预约标记为已完成吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    })
    await completeAppointment(row.reserveId)
    ElMessage.success('已标记为完成')
    loadData()
  } catch (error) {
    // 用户取消操作
  }
}

const handleView = (row) => {
  currentAppointment.value = { ...row }
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
.appointment {
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
