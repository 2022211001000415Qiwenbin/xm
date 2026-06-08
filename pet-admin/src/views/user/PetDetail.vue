
<template>
  <div class="pet-detail-container">
    <div class="back-btn" @click="goBack">
        <el-icon><ArrowLeft /></el-icon>返回列表
      </div>
    <el-card class="detail-card" v-loading="loading">

      <div v-if="pet" class="pet-detail">
        <el-row :gutter="30">
          <el-col :xs="24" :sm="24" :md="12">
            <div class="detail-image">
              <el-image
                :src="pet.petPhoto || 'https://via.placeholder.com/500x400?text=' + pet.petName"
                fit="cover"
                style="width: 100%; border-radius: 8px;"
              />
            </div>
          </el-col>
          <el-col :xs="24" :sm="24" :md="12">
            <div class="detail-info">
              <h2 class="pet-name">{{ pet.petName }}</h2>
              <el-tag :type="pet.adoptStatus === '已领养' ? 'success' : 'warning'" size="large" class="status-tag">
                {{ pet.adoptStatus }}
              </el-tag>

              <el-descriptions :column="1" border style="margin-top: 20px;">
                <el-descriptions-item label="品种">{{ pet.breedType }} - {{ pet.breedName }}</el-descriptions-item>
                <el-descriptions-item label="年龄">{{ pet.age }}岁</el-descriptions-item>
                <el-descriptions-item label="性别">{{ pet.gender }}</el-descriptions-item>
                <el-descriptions-item label="健康状况">{{ pet.healthStatus }}</el-descriptions-item>
                <el-descriptions-item label="性格">{{ pet.personality || '暂无' }}</el-descriptions-item>
                <el-descriptions-item label="经历">{{ pet.experience || '暂无' }}</el-descriptions-item>
                <el-descriptions-item label="备注">{{ pet.remark || '暂无' }}</el-descriptions-item>
              </el-descriptions>

              <div class="action-buttons" v-if="pet.adoptStatus === '待领养'">
                <el-button type="success" size="large" @click="handleAdopt">申请领养</el-button>
                <el-button type="info" size="large" @click="handleReserve">预约看宠</el-button>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>

      <el-empty v-else-if="!loading" description="未找到宠物信息" />
    </el-card>

    <!-- 预约对话框 -->
    <ReserveDialog v-model="reserveDialogVisible" :pet-id="pet?.petId" :pet-name="pet?.petName" @success="onReserveSuccess" />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getPetDetail } from '@/api/pet'
import { useStore } from 'vuex'
import ReserveDialog from '@/components/ReserveDialog.vue'

const route = useRoute()
const router = useRouter()
const store = useStore()

const loading = ref(false)
const pet = ref(null)

const reserveDialogVisible = ref(false)

const loadPetDetail = async () => {
  const petId = route.params.id
  if (!petId) return
  loading.value = true
  try {
    const res = await getPetDetail(petId)
    pet.value = res.data
  } catch (error) {
    ElMessage.error('获取宠物信息失败')
  } finally {
    loading.value = false
  }
}

const goBack = () => {
  router.back()
}

const handleAdopt = () => {
  router.push(`/user/adopt/${pet.value.petId}`)
}

const handleReserve = () => {
  reserveDialogVisible.value = true
}

const onReserveSuccess = () => {
  loadPetDetail()
}

onMounted(() => {
  loadPetDetail()
})
</script>

<style scoped lang="scss">
.pet-detail-container {
  .back-btn {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    padding: 8px 16px;
    border-radius: 10px;
    background: #fff;
    border: 1px solid #E5E7EB;
    color: #6B7280;
    font-size: 14px;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.2s;
    margin-bottom: 20px;

    &:hover {
      color: #FF6B35;
      border-color: rgba(255,107,53,0.3);
      background: rgba(255,107,53,0.04);
    }
  }

  .detail-card {
    border-radius: 20px;
    border: 1px solid #F3F4F6;
    overflow: hidden;

    :deep(.el-card__body) { padding: 0; }
  }

  .pet-detail {
    .detail-image {
      border-radius: 16px;
      overflow: hidden;
      max-height: 420px;

      .el-image {
        width: 100%;
        border-radius: 16px;
      }
    }

    .detail-info {
      padding: 8px 0;

      .pet-name {
        font-size: 28px;
        color: #1F2937;
        margin: 0 0 12px;
        font-weight: 800;
      }

      .status-tag {
        margin-bottom: 20px;
        border-radius: 20px;
        font-weight: 600;
      }

      :deep(.el-descriptions) {
        .el-descriptions__label {
          font-weight: 600;
          color: #6B7280;
        }
        .el-descriptions__content {
          color: #1F2937;
        }
      }

      .action-buttons {
        margin-top: 28px;
        display: flex;
        gap: 12px;

        .el-button {
          border-radius: 12px;
          font-weight: 600;
          height: 44px;
          padding: 0 28px;
          font-size: 15px;
        }

        .el-button--success {
          background: linear-gradient(135deg, #FF8A65, #FF6B35);
          border: none;
          &:hover { box-shadow: 0 4px 16px rgba(255,107,53,0.35); }
        }

        .el-button--info {
          background: #F3F4F6;
          border: none;
          color: #6B7280;
          &:hover { background: #E5E7EB; color: #374151; }
        }
      }
    }
  }
}
</style>
