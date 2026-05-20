
<template>
  <div class="pet-detail-container">
    <el-card v-loading="loading">
      <template #header>
        <div class="detail-header">
          <el-button icon="ArrowLeft" @click="goBack">返回</el-button>
          <h2>宠物详情</h2>
        </div>
      </template>

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

    <!-- 领养申请对话框 -->
    <el-dialog v-model="adoptDialogVisible" title="申请领养" width="500px">
      <el-form ref="adoptFormRef" :model="adoptForm" :rules="adoptRules" label-width="100px">
        <el-form-item label="宠物名称">
          <el-input v-model="pet.petName" disabled />
        </el-form-item>
        <el-form-item label="姓名" prop="applicantName">
          <el-input v-model="adoptForm.applicantName" placeholder="请输入您的姓名" />
        </el-form-item>
        <el-form-item label="联系方式" prop="applicantPhone">
          <el-input v-model="adoptForm.applicantPhone" placeholder="请输入您的联系方式" maxlength="11" />
        </el-form-item>
        <el-form-item label="职业" prop="applicantOccupation">
          <el-input v-model="adoptForm.applicantOccupation" placeholder="请输入您的职业" />
        </el-form-item>
        <el-form-item label="家庭地址" prop="applicantAddress">
          <el-input v-model="adoptForm.applicantAddress" placeholder="请输入您的家庭地址" />
        </el-form-item>
        <el-form-item label="养宠经验" prop="applicantExperience">
          <el-input v-model="adoptForm.applicantExperience" type="textarea" :rows="3" placeholder="请输入您的养宠经验" />
        </el-form-item>
        <el-form-item label="申请信息" prop="applyInfo">
          <el-input v-model="adoptForm.applyInfo" type="textarea" :rows="4" placeholder="请输入申请信息，说明您的领养意愿" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="adoptDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitAdopt">提交申请</el-button>
      </template>
    </el-dialog>

    <!-- 预约对话框 -->
    <el-dialog v-model="reserveDialogVisible" title="预约看宠" width="500px">
      <el-form ref="reserveFormRef" :model="reserveForm" :rules="reserveRules" label-width="100px">
        <el-form-item label="宠物名称">
          <el-input v-model="pet.petName" disabled />
        </el-form-item>
        <el-form-item label="预约时间" prop="reserveTime">
          <el-date-picker
            v-model="reserveForm.reserveTime"
            type="datetime"
            placeholder="选择预约时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DDTHH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="联系人" prop="contactPerson">
          <el-input v-model="reserveForm.contactPerson" placeholder="请输入联系人姓名" />
        </el-form-item>
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="reserveForm.contactPhone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="备注" prop="reserveRemark">
          <el-input v-model="reserveForm.reserveRemark" type="textarea" :rows="3" placeholder="请输入备注信息" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reserveDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitReserve">提交预约</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getPetDetail, submitAdoption, submitReservation } from '@/api/pet'
import { useStore } from 'vuex'

const route = useRoute()
const router = useRouter()
const store = useStore()

const loading = ref(false)
const pet = ref(null)

const adoptDialogVisible = ref(false)
const reserveDialogVisible = ref(false)

const adoptFormRef = ref(null)
const adoptForm = reactive({
  applicantName: '',
  applicantPhone: '',
  applicantOccupation: '',
  applicantAddress: '',
  applicantExperience: '',
  applyInfo: ''
})

const adoptRules = {
  applicantName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  applicantPhone: [
    { required: true, message: '请输入联系方式', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  applicantOccupation: [{ required: true, message: '请输入职业', trigger: 'blur' }],
  applicantAddress: [{ required: true, message: '请输入家庭地址', trigger: 'blur' }],
  applicantExperience: [{ required: true, message: '请输入养宠经验', trigger: 'blur' }],
  applyInfo: [
    { required: true, message: '请输入申请信息', trigger: 'blur' },
    { min: 10, message: '申请信息不能少于10个字符', trigger: 'blur' }
  ]
}

const reserveFormRef = ref(null)
const reserveForm = reactive({
  reserveTime: '',
  contactPerson: '',
  contactPhone: '',
  reserveRemark: ''
})

const reserveRules = {
  reserveTime: [{ required: true, message: '请选择预约时间', trigger: 'change' }],
  contactPerson: [{ required: true, message: '请输入联系人姓名', trigger: 'blur' }],
  contactPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ]
}

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
  adoptForm.applicantName = ''
  adoptForm.applicantPhone = ''
  adoptForm.applicantOccupation = ''
  adoptForm.applicantAddress = ''
  adoptForm.applicantExperience = ''
  adoptForm.applyInfo = ''
  adoptDialogVisible.value = true
}

const handleReserve = () => {
  reserveForm.reserveTime = ''
  reserveForm.contactPerson = ''
  reserveForm.contactPhone = ''
  reserveForm.reserveRemark = ''
  reserveDialogVisible.value = true
}

const handleSubmitAdopt = async () => {
  if (!adoptFormRef.value) return
  await adoptFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await submitAdoption({
          userId: store.state.user.userId,
          petId: pet.value.petId,
          applicantName: adoptForm.applicantName,
          applicantPhone: adoptForm.applicantPhone,
          applicantOccupation: adoptForm.applicantOccupation,
          applicantAddress: adoptForm.applicantAddress,
          applicantExperience: adoptForm.applicantExperience,
          applyInfo: adoptForm.applyInfo
        })
        ElMessage.success('领养申请提交成功，请等待审核')
        adoptDialogVisible.value = false
        loadPetDetail()
      } catch (error) {
        // 错误信息已在 request.js 的响应拦截器中处理
      }
    }
  })
}

const handleSubmitReserve = async () => {
  if (!reserveFormRef.value) return
  await reserveFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await submitReservation({
          userId: store.state.user.userId,
          petId: pet.value.petId,
          reserveTime: reserveForm.reserveTime,
          contactPerson: reserveForm.contactPerson,
          contactPhone: reserveForm.contactPhone,
          reserveRemark: reserveForm.reserveRemark
        })
        ElMessage.success('预约提交成功，请等待确认')
        reserveDialogVisible.value = false
      } catch (error) {
        // 错误信息已在 request.js 的响应拦截器中处理
      }
    }
  })
}

onMounted(() => {
  loadPetDetail()
})
</script>

<style scoped lang="scss">
.pet-detail-container {
  padding: 20px;

  .detail-header {
    display: flex;
    align-items: center;
    gap: 16px;

    h2 {
      margin: 0;
      font-size: 20px;
      color: #333;
    }
  }

  .pet-detail {
    .detail-image {
      border-radius: 8px;
      overflow: hidden;
    }

    .detail-info {
      .pet-name {
        font-size: 24px;
        color: #333;
        margin: 0 0 12px 0;
      }

      .status-tag {
        margin-bottom: 16px;
      }

      .action-buttons {
        margin-top: 24px;
        display: flex;
        gap: 12px;
      }
    }
  }
}
</style>
