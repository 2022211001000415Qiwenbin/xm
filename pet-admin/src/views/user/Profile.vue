<template>
  <div class="profile-container">
    <el-card class="user-info-card">
      <template #header>
        <div class="card-header">
          <span>个人信息</span>
          <el-button type="primary" @click="isEditing = !isEditing">
            {{ isEditing ? '取消' : '编辑' }}
          </el-button>
        </div>
      </template>

      <el-form v-if="isEditing" ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="用户名">
              <el-input v-model="form.username" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="真实姓名" prop="realName">
              <el-input v-model="form.realName" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="身份证号" prop="idCard">
              <el-input v-model="form.idCard" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="form.phone" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="地址" prop="address">
          <el-input v-model="form.address" />
        </el-form-item>
        <el-form-item label="养宠经验" prop="petExperience">
          <el-input v-model="form.petExperience" type="textarea" :rows="4" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSave">保存</el-button>
          <el-button @click="isEditing = false">取消</el-button>
        </el-form-item>
      </el-form>

      <el-descriptions v-else :column="2" border>
        <el-descriptions-item label="用户名">{{ form.username }}</el-descriptions-item>
        <el-descriptions-item label="真实姓名">{{ form.realName }}</el-descriptions-item>
        <el-descriptions-item label="身份证号">{{ form.idCard }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ form.phone }}</el-descriptions-item>
        <el-descriptions-item label="地址" :span="2">{{ form.address }}</el-descriptions-item>
        <el-descriptions-item label="养宠经验" :span="2">{{ form.petExperience }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'
import { getUserInfo, updateUserInfo } from '@/api/auth'

const store = useStore()
const isEditing = ref(false)
const formRef = ref(null)

const form = reactive({
  username: store.state.user.username,
  realName: '',
  idCard: '',
  phone: '',
  address: '',
  petExperience: ''
})

const rules = {
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  idCard: [
    { required: true, message: '请输入身份证号', trigger: 'blur' },
    { pattern: /^[1-9]\d{5}(18|19|20)\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])\d{3}[0-9Xx]$/, message: '请输入正确的身份证号', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

const loadUserInfo = async () => {
  try {
    const res = await getUserInfo()
    Object.assign(form, res.data)
  } catch (error) {
    ElMessage.error('获取用户信息失败')
  }
}

const handleSave = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await updateUserInfo(form)
        ElMessage.success('保存成功')
        isEditing.value = false
        loadUserInfo()
      } catch (error) {
        ElMessage.error('保存失败')
      }
    }
  })
}

onMounted(() => {
  loadUserInfo()
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
