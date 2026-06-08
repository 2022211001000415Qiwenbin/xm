<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="8">
        <div class="stat-card" @click="navigateTo('/abandoned-pet')">
          <div class="stat-icon" style="background-color: #409EFF;">
            <el-icon :size="30"><Box /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stats.totalPets }}</div>
            <div class="stat-label">宠物总数</div>
          </div>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="stat-card" @click="navigateTo('/adoption-application')">
          <div class="stat-icon" style="background-color: #E6A23C;">
            <el-icon :size="30"><Clock /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stats.pendingApplications }}</div>
            <div class="stat-label">待审核申请</div>
          </div>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="stat-card" @click="navigateTo('/appointment')">
          <div class="stat-icon" style="background-color: #F56C6C;">
            <el-icon :size="30"><Calendar /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stats.todayAppointments }}</div>
            <div class="stat-label">今日预约</div>
          </div>
        </div>
      </el-col>
    </el-row>



    <el-row :gutter="20" class="mt-20">
      <el-col :span="24">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>最新动态</span>
            </div>
          </template>
          <el-table :data="recentActivities" stripe style="width: 100%">
            <el-table-column prop="time" label="时间" width="180" />
            <el-table-column prop="type" label="类型" width="120">
              <template #default="scope">
                <el-tag :type="getActivityType(scope.row.type)">
                  {{ scope.row.type }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="description" label="描述" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Box, CircleCheck, Clock, Calendar } from '@element-plus/icons-vue'
import { getDashboardStats, getRecentActivities } from '@/api/dashboard'
import { getStatusTagType } from '@/utils/format'

const router = useRouter()

const stats = ref({
  totalPets: 0,
  adoptedPets: 0,
  pendingApplications: 0,
  todayAppointments: 0
})

const navigateTo = (path) => {
  router.push(path)
}

const recentActivities = ref([])

const getActivityType = (type) => getStatusTagType(type, 'activity')

const loadStats = async () => {
  try {
    const res = await getDashboardStats()
    if (res.data) {
      stats.value = {
        totalPets: res.data.totalPets || 0,
        adoptedPets: res.data.adoptedPets || 0,
        pendingApplications: res.data.pendingApplications || 0,
        todayAppointments: res.data.todayAppointments || 0
      }
    }
  } catch (error) {
    // 错误信息已在 request.js 的响应拦截器中处理
  }
}

const loadRecentActivities = async () => {
  try {
    const res = await getRecentActivities()
    if (res.data) {
      recentActivities.value = res.data
    }
  } catch (error) {
    // 错误信息已在 request.js 的响应拦截器中处理
  }
}

onMounted(() => {
  loadStats()
  loadRecentActivities()
})
</script>

<style scoped lang="scss">
.dashboard {
  .stat-card {
    background-color: #fff;
    border-radius: 8px;
    padding: 20px;
    display: flex;
    align-items: center;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    cursor: pointer;
    transition: all 0.3s;
    
    &:hover {
      transform: translateY(-5px);
      box-shadow: 0 7px 16px 0 rgba(0, 0, 0, 0.15);
    }

    .stat-icon {
      width: 60px;
      height: 60px;
      border-radius: 8px;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #fff;
      margin-right: 20px;
    }

    .stat-content {
      flex: 1;

      .stat-value {
        font-size: 28px;
        font-weight: bold;
        color: #303133;
      }

      .stat-label {
        font-size: 14px;
        color: #909399;
        margin-top: 4px;
      }
    }
  }

  .chart-card {
    .card-header {
      font-size: 16px;
      font-weight: bold;
      color: #303133;
    }

    .chart-placeholder {
      height: 300px;
      display: flex;
      align-items: center;
      justify-content: center;
    }
  }

  .mt-20 {
    margin-top: 20px;
  }
}
</style>
