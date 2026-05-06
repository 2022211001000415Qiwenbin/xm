<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-icon" style="background-color: #409EFF;">
            <el-icon :size="30"><Box /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stats.totalPets }}</div>
            <div class="stat-label">宠物总数</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-icon" style="background-color: #67C23A;">
            <el-icon :size="30"><CircleCheck /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stats.adoptedPets }}</div>
            <div class="stat-label">已领养</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-icon" style="background-color: #E6A23C;">
            <el-icon :size="30"><Clock /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stats.pendingApplications }}</div>
            <div class="stat-label">待审核申请</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
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
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>最近领养趋势</span>
            </div>
          </template>
          <div ref="adoptionTrendChart" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>宠物类型分布</span>
            </div>
          </template>
          <div ref="petTypeChart" class="chart-container"></div>
        </el-card>
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
import { Box, CircleCheck, Clock, Calendar } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { getDashboardStats, getAdoptionTrend, getPetTypeDistribution } from '@/api/dashboard'

const stats = ref({
  totalPets: 0,
  adoptedPets: 0,
  pendingApplications: 0,
  todayAppointments: 0
})

const adoptionTrendChart = ref(null)
const petTypeChart = ref(null)

const recentActivities = ref([
  {
    time: '2024-01-15 10:30',
    type: '领养',
    description: '用户张三成功领养了一只名为"小白"的宠物'
  },
  {
    time: '2024-01-15 09:15',
    type: '预约',
    description: '用户李四预约了今天下午3点来参观宠物'
  },
  {
    time: '2024-01-14 16:45',
    type: '申请',
    description: '收到新的领养申请，待审核'
  },
  {
    time: '2024-01-14 14:20',
    type: '登记',
    description: '新登记了一只名为"小黑"的宠物'
  },
  {
    time: '2024-01-14 11:00',
    type: '领养',
    description: '用户王五成功领养了一只名为"花花"的宠物'
  }
])

const getActivityType = (type) => {
  const typeMap = {
    '领养': 'success',
    '预约': 'warning',
    '申请': 'info',
    '登记': 'primary'
  }
  return typeMap[type] || ''
}

const initAdoptionTrendChart = () => {
  const chart = echarts.init(adoptionTrendChart.value)
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['领养数量']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: ['1月', '2月', '3月', '4月', '5月', '6月']
    },
    yAxis: {
      type: 'value'
    },
    series: [{
      name: '领养数量',
      type: 'line',
      smooth: true,
      data: [12, 18, 25, 30, 42, 56]
    }]
  }
  chart.setOption(option)
}

const initPetTypeChart = () => {
  const chart = echarts.init(petTypeChart.value)
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        name: '宠物类型',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        label: {
          show: true,
          formatter: '{b}: {c} ({d}%)'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 20,
            fontWeight: 'bold'
          }
        },
        data: [
          { value: 45, name: '狗' },
          { value: 35, name: '猫' },
          { value: 20, name: '其他' }
        ]
      }
    ]
  }
  chart.setOption(option)
}

const loadStats = async () => {
  try {
    const res = await getDashboardStats()
    stats.value = res.data
  } catch (error) {
    console.error('加载统计数据失败', error)
  }
}

const loadAdoptionTrend = async () => {
  try {
    const res = await getAdoptionTrend()
    initAdoptionTrendChart()
  } catch (error) {
    console.error('加载领养趋势数据失败', error)
  }
}

const loadPetTypeDistribution = async () => {
  try {
    const res = await getPetTypeDistribution()
    initPetTypeChart()
  } catch (error) {
    console.error('加载宠物类型分布数据失败', error)
  }
}

const handleResize = () => {
  if (adoptionTrendChart.value) {
    adoptionTrendChart.value.resize()
  }
  if (petTypeChart.value) {
    petTypeChart.value.resize()
  }
}

onMounted(() => {
  loadStats()
  loadAdoptionTrend()
  loadPetTypeDistribution()
  window.addEventListener('resize', handleResize)
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
        margin-bottom: 8px;
      }

      .stat-label {
        font-size: 14px;
        color: #909399;
      }
    }
  }

  .chart-card {
    .card-header {
      font-size: 16px;
      font-weight: bold;
      color: #303133;
    }

    .chart-container {
      height: 300px;
      width: 100%;
    }
  }

  .mt-20 {
    margin-top: 20px;
  }
}
</style>
