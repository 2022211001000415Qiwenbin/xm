<template>
  <div class="profile-container">
    <el-card class="user-info-card">
      <template #header>
        <div class="card-header">
          <span>个人信息</span>
        </div>
      </template>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="用户名">{{ userInfo.username }}</el-descriptions-item>
        <el-descriptions-item label="真实姓名">{{ userInfo.realName }}</el-descriptions-item>
        <el-descriptions-item label="身份证号">{{ userInfo.idCard }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ userInfo.phone }}</el-descriptions-item>
        <el-descriptions-item label="地址" :span="2">{{ userInfo.address }}</el-descriptions-item>
        <el-descriptions-item label="养宠经验" :span="2">{{ userInfo.petExperience }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card class="tabs-card">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="我的领养申请" name="adoptions">
          <el-table :data="adoptions" stripe border style="width: 100%">
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
            <el-table-column prop="auditRemark" label="审核备注" width="150" show-overflow-tooltip />
            <el-table-column prop="applyTime" label="申请时间" width="160" />
            <el-table-column prop="auditTime" label="审核时间" width="160" />
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="我的预约" name="reservations">
          <el-table :data="reservations" stripe border style="width: 100%">
            <el-table-column prop="reserveId" label="预约ID" width="80" />
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
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useStore } from 'vuex'
import { getMyAdoptions, getMyReservations } from '@/api/pet'

const store = useStore()
const activeTab = ref('adoptions')

const userInfo = reactive({
  username: store.state.user.username,
  realName: '',
  idCard: '',
  phone: '',
  address: '',
  petExperience: ''
})

const adoptions = ref([])
const reservations = ref([])

const getStatusType = (status) => {
  const typeMap = {
    '待审核': 'warning',
    '通过': 'success',
    '拒绝': 'danger',
    '待确认': 'warning',
    '已确认': 'success',
    '已完成': 'info',
    '已取消': 'danger'
  }
  return typeMap[status] || ''
}

const loadAdoptions = async () => {
  try {
    const res = await getMyAdoptions({
      current: 1,
      size: 100,
      userId: store.state.user.userId
    })
    adoptions.value = res.data.records
  } catch (error) {
    // 错误信息已在 request.js 的响应拦截器中处理
  }
}

const loadReservations = async () => {
  try {
    const res = await getMyReservations({
      current: 1,
      size: 100,
      userId: store.state.user.userId
    })
    reservations.value = res.data.records
  } catch (error) {
    // 错误信息已在 request.js 的响应拦截器中处理
  }
}

onMounted(() => {
  loadAdoptions()
  loadReservations()
})
</script>

<style scoped lang="scss">
.profile-container {
  padding: 20px;

  .user-info-card {
    margin-bottom: 20px;
  }

  .tabs-card {
    .el-table {
      margin-top: 20px;
    }
  }
}
</style>
