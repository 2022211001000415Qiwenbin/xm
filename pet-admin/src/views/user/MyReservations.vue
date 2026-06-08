
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

    <el-card class="list-card">
      <template #header>
        <div class="card-header">
          <span>我的预约</span>
        </div>
      </template>

      <div v-if="tableData.length === 0" class="empty-tip">
        <el-empty description="暂无预约记录" />
      </div>

      <div v-else class="reservation-list">
        <div v-for="item in tableData" :key="item.reserveId" class="reservation-item">
          <div class="item-fields-row">
            <div class="item-field">
              <span class="field-label">预约ID</span>
              <span class="field-value">{{ item.reserveId }}</span>
            </div>
            <div class="item-field">
              <span class="field-label">宠物姓名</span>
              <span class="field-value">{{ item.petName || '--' }}</span>
            </div>
            <div class="item-field">
              <span class="field-label">预约时间</span>
              <span class="field-value">{{ formatDateTime(item.reserveTime) || '--' }}</span>
            </div>
            <div class="item-field">
              <span class="field-label">联系人</span>
              <span class="field-value">{{ item.contactPerson || '--' }}</span>
            </div>
            <div class="item-field">
              <span class="field-label">联系电话</span>
              <span class="field-value">{{ item.contactPhone || '--' }}</span>
            </div>
            <div class="item-field">
              <span class="field-label">预约状态</span>
              <el-tag :type="getStatusType(item.reserveStatus)" size="default">{{ item.reserveStatus }}</el-tag>
            </div>
            <div class="item-field">
              <span class="field-label">备注</span>
              <span class="field-value">{{ item.reserveRemark || '无' }}</span>
            </div>
          </div>
          <div class="item-actions">
            <el-button
              v-if="item.reserveStatus === '待确认'"
              type="danger"
              size="small"
              @click="handleCancel(item)"
            >
              取消预约
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
      title="预约详情"
      width="600px"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="预约ID">{{ currentReservation.reserveId }}</el-descriptions-item>
        <el-descriptions-item label="宠物姓名">{{ currentReservation.petName }}</el-descriptions-item>
        <el-descriptions-item label="预约时间" :span="2">{{ formatDateTime(currentReservation.reserveTime) }}</el-descriptions-item>
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
import { getAppointmentPage, cancelAppointment } from '@/api/appointment'
import { formatDateTime, getStatusTagType } from '@/utils/format'

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

const getStatusType = (status) => getStatusTagType(status, 'reservation')

const loadData = async () => {
  try {
    const res = await getAppointmentPage({
      current: pagination.currentPage,
      size: pagination.pageSize,
      reserveStatus: searchForm.status,
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
  currentReservation.value = { ...row }
  detailDialogVisible.value = true
}

const handleCancel = async (row) => {
  try {
    await ElMessageBox.confirm('确定要取消这个预约吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await cancelAppointment({
      id: row.reserveId,
      cancelReason: '用户主动取消',
      cancelPerson: store.state.user.realName || '用户'
    })
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

  .page-title {
    font-size: 24px;
    font-weight: 800;
    color: #1F2937;
    margin: 0 0 24px;
  }

  .search-card {
    margin-bottom: 24px;
    border-radius: 16px;
    border: 1px solid #F3F4F6;

    :deep(.el-card__body) { padding: 16px 20px; }
    :deep(.el-select .el-input__wrapper) { border-radius: 10px; }
    :deep(.el-button--primary) {
      background: linear-gradient(135deg, #FF8A65, #FF6B35);
      border: none;
      border-radius: 10px;
    }
    :deep(.el-button--default) { border-radius: 10px; }
  }

  .list-card {
    border-radius: 16px;
    border: 1px solid #F3F4F6;

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      font-size: 18px;
      font-weight: 700;
      color: #1F2937;
    }

    .empty-tip { padding: 40px 0; }

    .reservation-list {
      display: flex;
      flex-direction: column;
      gap: 14px;
    }

    .reservation-item {
      display: flex;
      flex-direction: column;
      gap: 12px;
      padding: 20px 24px;
      background: #FAFAF8;
      border-radius: 14px;
      border: 1px solid #F3F4F6;
      transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);

      &:hover {
        background: #FFF8F0;
        border-color: rgba(255,107,53,0.2);
        box-shadow: 0 4px 16px rgba(255,107,53,0.08);
      }

      .item-fields-row {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
        gap: 10px 24px;
      }

      .item-field {
        display: flex;
        align-items: center;
        gap: 10px;

        .field-label {
          color: #9CA3AF;
          font-size: 13px;
          white-space: nowrap;
          min-width: 70px;
          flex-shrink: 0;
        }

        .field-value {
          color: #374151;
          font-size: 14px;
          font-weight: 500;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }
      }

      .item-actions {
        display: flex;
        justify-content: flex-end;
        padding-top: 12px;
        border-top: 1px dashed #E5E7EB;

        .el-button { border-radius: 10px; font-weight: 600; }
      }
    }

    .pagination-container {
      margin-top: 24px;
      display: flex;
      justify-content: center;

      :deep(.el-pager li.is-active) {
        background: linear-gradient(135deg, #FF8A65, #FF6B35);
        border-radius: 8px;
      }
    }
  }
}
</style>
