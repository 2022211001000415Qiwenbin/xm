<template>
  <div class="home-container">
    <!-- 顶部横幅 -->
    <div class="hero-banner">
      <div class="hero-content">
        <h1>给它们一个温暖的家</h1>
        <p>每一只小生命都值得被温柔以待，在这里遇见你的毛茸伙伴</p>
      </div>
      <div class="hero-filter">
        <el-select v-model="searchForm.breedType" placeholder="选择品种类型" clearable @change="handleSearch" size="large">
          <el-option label="🐶 狗狗" value="狗" />
          <el-option label="🐱 猫咪" value="猫" />
          <el-option label="🐹 其他" value="其他" />
        </el-select>
        <el-select v-model="searchForm.adoptStatus" placeholder="领养状态" clearable @change="handleSearch" size="large">
          <el-option label="待领养" value="待领养" />
          <el-option label="已领养" value="已领养" />
        </el-select>
        <el-button size="large" class="filter-btn" @click="handleReset">重置</el-button>
      </div>
    </div>

    <!-- 宠物卡片列表 -->
    <div class="pet-grid" v-loading="loading">
      <div v-for="pet in tableData" :key="pet.petId" class="pet-card" @click="handleViewDetail(pet)">
        <div class="pet-image">
          <el-image :src="pet.petPhoto || 'https://via.placeholder.com/400x300?text=' + pet.petName" fit="cover" />
          <div class="pet-badge" :class="pet.adoptStatus === '已领养' ? 'adopted' : 'available'">
            {{ pet.adoptStatus }}
          </div>
        </div>
        <div class="pet-body">
          <div class="pet-header">
            <h3>{{ pet.petName }}</h3>
            <span class="pet-type">{{ pet.breedType }}</span>
          </div>
          <div class="pet-meta">
            <span><el-icon><Clock /></el-icon>{{ pet.age }}岁</span>
            <span><el-icon><Male v-if="pet.gender==='公'" /><Female v-else /></el-icon>{{ pet.gender }}</span>
            <span :class="'health-' + pet.healthStatus">{{ pet.healthStatus }}</span>
          </div>
          <div class="pet-breed">{{ pet.breedName }}</div>
        </div>
        <div class="pet-actions" v-if="pet.adoptStatus === '待领养'">
          <el-button type="primary" class="action-btn adopt-btn" @click.stop="handleAdopt(pet)">申请领养</el-button>
          <el-button class="action-btn reserve-btn" @click.stop="handleReserve(pet)">预约看宠</el-button>
        </div>
      </div>
    </div>

    <el-empty v-if="!loading && tableData.length === 0" description="暂无宠物信息" />

    <div class="pagination-container">
      <el-pagination
        v-model:current-page="pagination.currentPage"
        v-model:page-size="pagination.pageSize"
        :page-sizes="[12, 24, 36]"
        :total="pagination.total"
        layout="prev, pager, next"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 预约对话框 -->
    <ReserveDialog v-model="reserveDialogVisible" :pet-id="currentPet.petId" :pet-name="currentPet.petName" @success="loadData" />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getPetPage } from '@/api/pet'
import { getBreedList } from '@/api/abandonedPet'
import ReserveDialog from '@/components/ReserveDialog.vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'

const store = useStore()
const router = useRouter()
const loading = ref(false)
const breedList = ref([])

const searchForm = reactive({ adoptStatus: '待领养', breedType: '' })
const tableData = ref([])
const pagination = reactive({ currentPage: 1, pageSize: 12, total: 0 })
const reserveDialogVisible = ref(false)
const currentPet = ref({})

const loadData = async () => {
  loading.value = true
  try {
    const res = await getPetPage({ current: pagination.currentPage, size: pagination.pageSize, adoptStatus: searchForm.adoptStatus, breedType: searchForm.breedType })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {} finally { loading.value = false }
}

const handleSearch = () => { pagination.currentPage = 1; loadData() }
const handleReset = () => { searchForm.adoptStatus = '待领养'; searchForm.breedType = ''; handleSearch() }
const handleViewDetail = (pet) => { router.push(`/user/pet/${pet.petId}`) }

const handleAdopt = (pet) => {
  router.push(`/user/adopt/${pet.petId}`)
}

const handleReserve = (pet) => {
  currentPet.value = { ...pet }
  reserveDialogVisible.value = true
}

const handleSizeChange = (val) => { pagination.pageSize = val; loadData() }
const handleCurrentChange = (val) => { pagination.currentPage = val; loadData() }

onMounted(() => { loadData() })
</script>

<style scoped lang="scss">
.home-container { min-height: 100%; }

.hero-banner {
  background: linear-gradient(135deg, #FF8A65, #FF6B35);
  border-radius: 20px;
  padding: 40px 36px 32px;
  margin-bottom: 28px;
  color: #fff;
  position: relative;
  overflow: hidden;

  &::after {
    content: '';
    position: absolute;
    width: 200px; height: 200px;
    border-radius: 50%;
    background: rgba(255,255,255,0.08);
    top: -60px; right: -30px;
  }

  .hero-content {
    position: relative;
    z-index: 1;
    margin-bottom: 24px;

    h1 {
      font-size: 28px;
      font-weight: 800;
      margin: 0 0 8px;
      letter-spacing: -0.5px;
    }
    p { font-size: 15px; margin: 0; opacity: 0.9; }
  }

  .hero-filter {
    display: flex;
    gap: 12px;
    position: relative;
    z-index: 1;

    :deep(.el-select) {
      .el-input__wrapper {
        background: rgba(255,255,255,0.2);
        border: none;
        box-shadow: none !important;
        border-radius: 10px;
        color: #fff;
      }
      .el-input__inner { color: #fff; }
      .el-input__inner::placeholder { color: rgba(255,255,255,0.7); }
      .el-select__caret { color: rgba(255,255,255,0.7); }
    }

    .filter-btn {
      background: rgba(255,255,255,0.2);
      border: none;
      color: #fff;
      border-radius: 10px;
      &:hover { background: rgba(255,255,255,0.3); }
    }
  }
}

.pet-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  min-height: 200px;
}

.pet-card {
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid #F3F4F6;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 12px 32px rgba(0,0,0,0.08);
    border-color: rgba(255,107,53,0.15);
  }

  .pet-image {
    height: 200px;
    overflow: hidden;
    position: relative;

    .el-image { width: 100%; height: 100%; }

    .pet-badge {
      position: absolute;
      top: 12px; right: 12px;
      padding: 4px 12px;
      border-radius: 20px;
      font-size: 12px;
      font-weight: 600;

      &.available { background: rgba(255,107,53,0.9); color: #fff; }
      &.adopted { background: rgba(34,197,94,0.9); color: #fff; }
    }
  }

  .pet-body {
    padding: 16px 20px;

    .pet-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 10px;

      h3 { font-size: 18px; font-weight: 700; color: #1F2937; margin: 0; }
      .pet-type {
        font-size: 12px;
        padding: 2px 10px;
        border-radius: 12px;
        background: #FFF3E0;
        color: #FF6B35;
        font-weight: 600;
      }
    }

    .pet-meta {
      display: flex;
      gap: 14px;
      margin-bottom: 8px;
      font-size: 13px;
      color: #6B7280;

      span { display: flex; align-items: center; gap: 4px; }
      .el-icon { font-size: 14px; }
      .health-良好 { color: #22C55E; font-weight: 600; }
      .health-一般 { color: #F59E0B; font-weight: 600; }
      .health-患病 { color: #EF4444; font-weight: 600; }
    }

    .pet-breed { font-size: 13px; color: #9CA3AF; }
  }

  .pet-actions {
    padding: 0 20px 16px;
    display: flex;
    gap: 8px;

    .action-btn {
      flex: 1;
      border-radius: 10px;
      font-weight: 600;
    }

    .adopt-btn {
      background: linear-gradient(135deg, #FF8A65, #FF6B35);
      border: none;
      &:hover { box-shadow: 0 4px 12px rgba(255,107,53,0.3); }
    }

    .reserve-btn {
      background: #F3F4F6;
      border: none;
      color: #6B7280;
      &:hover { background: #E5E7EB; color: #374151; }
    }
  }
}

.pagination-container {
  margin-top: 32px;
  display: flex;
  justify-content: center;

  :deep(.el-pagination) {
    .el-pager li.is-active {
      background: linear-gradient(135deg, #FF8A65, #FF6B35);
      border-radius: 8px;
    }
  }
}

@media (max-width: 768px) {
  .hero-banner {
    padding: 28px 20px 24px;
    .hero-content h1 { font-size: 22px; }
    .hero-filter { flex-wrap: wrap; }
  }
  .pet-grid { grid-template-columns: repeat(auto-fill, minmax(240px, 1fr)); gap: 14px; }
}
</style>
