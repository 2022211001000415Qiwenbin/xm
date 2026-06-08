<template>
  <div class="profile-container">
    <el-card class="user-info-card">
      <div class="avatar-section">
        <div class="avatar-wrapper" @click="triggerAvatarUpload">
          <el-avatar :size="100" :src="form.avatar" class="user-avatar">
            <el-icon :size="50"><User /></el-icon>
          </el-avatar>
          <div class="avatar-overlay">
            <el-icon :size="24"><Camera /></el-icon>
            <span>更换头像</span>
          </div>
          <input
            ref="avatarInputRef"
            type="file"
            accept="image/jpeg,image/png"
            style="display: none"
            @change="handleAvatarChange"
          />
        </div>
        <h2 class="user-name">{{ form.realName || form.username }}</h2>
        <el-tag :type="form.role === 'admin' ? 'danger' : 'primary'" size="large">
          {{ form.role === 'admin' ? '管理员' : '普通用户' }}
        </el-tag>
      </div>

      <el-divider />

      <div class="info-section">
        <div class="info-item">
          <span class="info-label">用户名</span>
          <span class="info-value">{{ form.username }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">真实姓名</span>
          <span class="info-value">{{ form.realName || '未填写' }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">身份证号</span>
          <span class="info-value">{{ form.idCard || '未填写' }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">联系电话</span>
          <span class="info-value">{{ form.phone || '未填写' }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">地址</span>
          <span class="info-value">{{ form.address || '未填写' }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">养宠经验</span>
          <span class="info-value">{{ form.petExperience || '未填写' }}</span>
        </div>
      </div>

      <div class="action-section">
        <el-button type="primary" size="large" @click="isEditing = true">编辑信息</el-button>
      </div>
    </el-card>

    <!-- 编辑对话框 -->
    <el-dialog v-model="isEditing" title="编辑个人信息" width="500px" @close="isEditing = false">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="用户名">
          <el-input v-model="form.username" disabled />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="form.realName" />
        </el-form-item>
        <el-form-item label="身份证号" prop="idCard">
          <el-input v-model="form.idCard" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="form.address" />
        </el-form-item>
        <el-form-item label="养宠经验" prop="petExperience">
          <el-input v-model="form.petExperience" type="textarea" :rows="4" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="isEditing = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'
import { User, Camera } from '@element-plus/icons-vue'
import { getUserInfo, updateUserInfo } from '@/api/auth'
import request from '@/utils/request'

const store = useStore()
const isEditing = ref(false)
const formRef = ref(null)
const avatarInputRef = ref(null)

const triggerAvatarUpload = () => {
  avatarInputRef.value?.click()
}

const handleAvatarChange = async (e) => {
  const file = e.target.files[0]
  if (!file) return

  const isImage = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('头像只能是 JPG/PNG 格式!')
    return
  }
  if (!isLt2M) {
    ElMessage.error('头像大小不能超过 2MB!')
    return
  }

  const formData = new FormData()
  formData.append('file', file)

  try {
    const res = await request({
      url: '/upload',
      method: 'post',
      data: formData,
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    if (res.code === 200) {
      form.avatar = res.data
      await updateUserInfo({ ...form, avatar: res.data })
      store.commit('SET_AVATAR', res.data)
      ElMessage.success('头像更新成功')
      loadUserInfo()
    } else {
      ElMessage.error(res.msg || '头像上传失败')
    }
  } catch (error) {
    ElMessage.error('头像上传失败')
  }

  // 清空input，允许重复选择同一文件
  e.target.value = ''
}

const form = reactive({
  username: store.state.user.username,
  realName: '',
  idCard: '',
  phone: '',
  address: '',
  petExperience: '',
  avatar: store.state.user.avatar || '',
  role: store.state.user.role,
  userId: store.state.user.userId
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
    const data = res.data
    form.username = data.username || ''
    form.realName = data.realName || ''
    form.idCard = data.idCard || ''
    form.phone = data.phone || ''
    form.address = data.address || ''
    form.petExperience = data.petExperience || ''
    form.avatar = data.avatar || store.state.user.avatar || ''
    form.role = data.role || store.state.user.role
    form.userId = data.userId || store.state.user.userId
  } catch (error) {
    console.error('获取用户信息失败:', error)
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
  max-width: 640px;
  margin: 0 auto;

  .page-title {
    font-size: 24px;
    font-weight: 800;
    color: #1F2937;
    margin: 0 0 24px;
  }

  .user-info-card {
    border-radius: 20px;
    border: 1px solid #F3F4F6;

    .avatar-section {
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: 32px 0 16px;
      position: relative;

      &::before {
        content: '';
        position: absolute;
        top: 0; left: 0; right: 0;
        height: 100px;
        background: linear-gradient(135deg, #FF8A65, #FF6B35);
        border-radius: 20px 20px 0 0;
        z-index: 0;
      }

      .avatar-wrapper {
        position: relative;
        cursor: pointer;
        margin-bottom: 16px;
        z-index: 1;

        .user-avatar {
          border: 4px solid #FF8A65;
          box-shadow: 0 6px 20px rgba(255, 107, 53, 0.25);
        }

        .avatar-overlay {
          position: absolute;
          top: 4px;
          left: 4px;
          width: 100px;
          height: 100px;
          border-radius: 50%;
          background: rgba(0, 0, 0, 0.45);
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;
          color: #fff;
          opacity: 0;
          transition: opacity 0.3s;
          font-size: 12px;
          gap: 4px;
          backdrop-filter: blur(4px);
        }

        &:hover .avatar-overlay { opacity: 1; }
      }

      .user-name {
        margin: 0 0 10px;
        font-size: 24px;
        color: #1F2937;
        font-weight: 700;
        z-index: 1;
      }
    }

    .info-section {
      display: flex;
      flex-direction: column;
      gap: 14px;
      padding: 12px 0;

      .info-item {
        display: flex;
        align-items: center;
        gap: 16px;
        padding: 8px 12px;
        border-radius: 10px;
        transition: background 0.2s;

        &:hover { background: #FAFAF8; }

        .info-label {
          color: #9CA3AF;
          font-size: 14px;
          min-width: 80px;
          flex-shrink: 0;
        }

        .info-value {
          color: #374151;
          font-size: 15px;
          font-weight: 500;
          word-break: break-all;
        }
      }
    }

    .action-section {
      display: flex;
      justify-content: center;
      padding-top: 20px;

      .el-button {
        border-radius: 12px;
        font-weight: 600;
        height: 44px;
        padding: 0 32px;
        background: linear-gradient(135deg, #FF8A65, #FF6B35);
        border: none;

        &:hover { box-shadow: 0 4px 16px rgba(255,107,53,0.35); }
      }
    }
  }
}
</style>
