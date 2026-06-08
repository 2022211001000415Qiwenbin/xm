<template>
  <div class="login-container">
    <!-- 背景装饰 -->
    <div class="bg-shapes">
      <div class="shape shape-1"></div>
      <div class="shape shape-2"></div>
      <div class="shape shape-3"></div>
    </div>

    <div class="login-card">
      <div class="login-header">
        <div class="login-logo">
          <svg viewBox="0 0 56 56" fill="none">
            <circle cx="28" cy="28" r="26" fill="url(#lGrad)"/>
            <path d="M19 36c0-3 3-6 9-6s9 3 9 6" stroke="#fff" stroke-width="2.2" stroke-linecap="round"/>
            <circle cx="21" cy="26" r="2" fill="#fff"/>
            <circle cx="35" cy="26" r="2" fill="#fff"/>
            <ellipse cx="16" cy="19" rx="4" ry="5.5" fill="#fff" opacity="0.45"/>
            <ellipse cx="40" cy="19" rx="4" ry="5.5" fill="#fff" opacity="0.45"/>
            <defs><linearGradient id="lGrad" x1="0" y1="0" x2="56" y2="56"><stop stop-color="#FF8A65"/><stop offset="1" stop-color="#FF6B35"/></linearGradient></defs>
          </svg>
        </div>
        <h2>宠物管理平台</h2>
        <p>{{ activeTab === 'login' ? '欢迎回家，用爱领养每一个小生命' : '加入我们，开启温暖领养之旅' }}</p>
      </div>

      <el-tabs v-model="activeTab" class="login-tabs">
        <el-tab-pane label="登录" name="login">
          <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" class="login-form">
            <el-form-item prop="username">
              <el-input v-model="loginForm.username" placeholder="请输入用户名" prefix-icon="User" size="large" />
            </el-form-item>
            <el-form-item prop="password">
              <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" prefix-icon="Lock" size="large" show-password @keyup.enter="handleLogin" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" size="large" :loading="loading" class="login-button" @click="handleLogin">登 录</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="注册" name="register">
          <el-form ref="registerFormRef" :model="registerForm" :rules="registerRules" class="register-form">
            <el-form-item prop="username">
              <el-input v-model="registerForm.username" placeholder="请输入用户名" prefix-icon="User" size="large" />
            </el-form-item>
            <el-form-item prop="password">
              <el-input v-model="registerForm.password" type="password" placeholder="请输入密码" prefix-icon="Lock" size="large" show-password />
            </el-form-item>
            <el-form-item prop="confirmPassword">
              <el-input v-model="registerForm.confirmPassword" type="password" placeholder="请确认密码" prefix-icon="Lock" size="large" show-password />
            </el-form-item>
            <el-form-item prop="realName">
              <el-input v-model="registerForm.realName" placeholder="请输入真实姓名" prefix-icon="UserFilled" size="large" />
            </el-form-item>
            <el-form-item prop="phone">
              <el-input v-model="registerForm.phone" placeholder="请输入手机号" prefix-icon="Phone" size="large" />
            </el-form-item>
            <el-form-item prop="idCard">
              <el-input v-model="registerForm.idCard" placeholder="请输入身份证号" prefix-icon="CreditCard" size="large" />
            </el-form-item>
            <el-form-item prop="address">
              <el-input v-model="registerForm.address" placeholder="请输入地址" prefix-icon="Location" size="large" />
            </el-form-item>
            <el-form-item prop="petExperience">
              <el-input v-model="registerForm.petExperience" type="textarea" :rows="3" placeholder="请输入养宠经历" size="large" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" size="large" :loading="loading" class="login-button" @click="handleRegister">注 册</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'
import { login, register } from '@/api/auth'

const router = useRouter()
const store = useStore()

const activeTab = ref('login')
const loginFormRef = ref(null)
const registerFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({ username: '', password: '' })

const registerForm = reactive({
  username: '', password: '', confirmPassword: '',
  realName: '', phone: '', idCard: '', address: '', petExperience: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value === '') callback(new Error('请再次输入密码'))
  else if (value !== registerForm.password) callback(new Error('两次输入密码不一致'))
  else callback()
}

const validatePhone = (rule, value, callback) => {
  if (!value) callback(new Error('请输入手机号'))
  else if (!/^1[3-9]\d{9}$/.test(value)) callback(new Error('请输入正确的手机号'))
  else callback()
}

const validateIdCard = (rule, value, callback) => {
  if (!value) callback(new Error('请输入身份证号'))
  else if (!/^[1-9]\d{5}(18|19|20)\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])\d{3}[0-9Xx]$/.test(value)) callback(new Error('请输入正确的身份证号'))
  else callback()
}

const loginRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }, { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }]
}

const registerRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }, { min: 3, max: 20, message: '用户名长度在3到20个字符', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }, { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }],
  confirmPassword: [{ required: true, validator: validateConfirmPassword, trigger: 'blur' }],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  phone: [{ required: true, validator: validatePhone, trigger: 'blur' }],
  idCard: [{ required: true, validator: validateIdCard, trigger: 'blur' }],
  address: [{ required: true, message: '请输入地址', trigger: 'blur' }],
  petExperience: [{ required: true, message: '请输入养宠经历', trigger: 'blur' }]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res = await login(loginForm)
        await store.dispatch('login', {
          token: res.data.token,
          refreshToken: res.data.refreshToken,
          username: res.data.username,
          role: res.data.role,
          userId: res.data.userId || res.data.adminId,
          avatar: res.data.avatar
        })
        ElMessage.success('登录成功')
        if (res.data.role === 'admin') {
          router.push('/dashboard')
        } else {
          router.push('/user/home')
        }
      } catch (error) {
      } finally {
        loading.value = false
      }
    }
  })
}

const handleRegister = async () => {
  if (!registerFormRef.value) return
  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await register(registerForm)
        ElMessage.success('注册成功，请登录')
        activeTab.value = 'login'
        Object.keys(registerForm).forEach(key => { registerForm[key] = '' })
      } catch (error) {
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped lang="scss">
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #FFF3E0 0%, #FFE0B2 50%, #FFCC80 100%);
  padding: 20px;
  position: relative;
  overflow: hidden;
}

.bg-shapes {
  position: absolute;
  inset: 0;
  pointer-events: none;

  .shape {
    position: absolute;
    border-radius: 50%;
    opacity: 0.5;
  }

  .shape-1 {
    width: 400px; height: 400px;
    background: radial-gradient(circle, rgba(255,138,101,0.25), transparent 70%);
    top: -100px; right: -80px;
    animation: float1 8s ease-in-out infinite;
  }

  .shape-2 {
    width: 300px; height: 300px;
    background: radial-gradient(circle, rgba(255,107,53,0.2), transparent 70%);
    bottom: -50px; left: -60px;
    animation: float2 10s ease-in-out infinite;
  }

  .shape-3 {
    width: 200px; height: 200px;
    background: radial-gradient(circle, rgba(255,167,38,0.2), transparent 70%);
    top: 40%; left: 15%;
    animation: float3 12s ease-in-out infinite;
  }
}

@keyframes float1 { 0%, 100% { transform: translate(0, 0); } 50% { transform: translate(-30px, 20px); } }
@keyframes float2 { 0%, 100% { transform: translate(0, 0); } 50% { transform: translate(20px, -30px); } }
@keyframes float3 { 0%, 100% { transform: translate(0, 0); } 50% { transform: translate(-15px, -20px); } }

.login-card {
  width: 100%;
  max-width: 480px;
  padding: 44px 40px;
  background: rgba(255, 255, 255, 0.92);
  border-radius: 24px;
  box-shadow: 0 20px 60px rgba(255, 107, 53, 0.12), 0 4px 20px rgba(0, 0, 0, 0.06);
  position: relative;
  z-index: 1;
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.6);
}

.login-header {
  text-align: center;
  margin-bottom: 32px;

  .login-logo {
    margin: 0 auto 16px;
    width: 56px; height: 56px;
    svg { width: 100%; height: 100%; }
  }

  h2 {
    font-size: 28px;
    font-weight: 800;
    margin: 0 0 8px;
    background: linear-gradient(135deg, #FF8A65, #FF6B35);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
  }

  p {
    font-size: 14px;
    color: #9CA3AF;
    margin: 0;
  }
}

.login-tabs {
  :deep(.el-tabs__header) { margin-bottom: 24px; }
  :deep(.el-tabs__nav-wrap::after) { display: none; }
  :deep(.el-tabs__item) { font-size: 16px; font-weight: 600; }
  :deep(.el-tabs__active-bar) { background: linear-gradient(135deg, #FF8A65, #FF6B35); }
  :deep(.el-tabs__item.is-active) { color: #FF6B35; }
  :deep(.el-tabs__item:hover) { color: #FF8A65; }
}

.login-form, .register-form {
  .login-button {
    width: 100%;
    height: 48px;
    font-size: 16px;
    font-weight: 600;
    border-radius: 12px;
    background: linear-gradient(135deg, #FF8A65, #FF6B35);
    border: none;
    box-shadow: 0 4px 16px rgba(255, 107, 53, 0.3);
    transition: all 0.3s;

    &:hover {
      box-shadow: 0 6px 24px rgba(255, 107, 53, 0.4);
      transform: translateY(-1px);
    }
  }
}

:deep(.el-input__wrapper) {
  border-radius: 10px;
  box-shadow: 0 0 0 1px #E5E7EB inset;
  transition: box-shadow 0.25s;

  &:hover { box-shadow: 0 0 0 1px #FF8A65 inset; }
  &.is-focus { box-shadow: 0 0 0 1px #FF6B35 inset, 0 0 0 3px rgba(255,107,53,0.1); }
}
</style>
