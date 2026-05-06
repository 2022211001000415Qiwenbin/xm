<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <h2>宠物管理系统</h2>
        <p>{{ activeTab === 'login' ? '欢迎登录' : '注册新账号' }}</p>
      </div>
      
      <el-tabs v-model="activeTab" class="login-tabs">
        <!-- 登录表单 -->
        <el-tab-pane label="登录" name="login">
          <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" class="login-form">
            <el-form-item prop="username">
              <el-input
                v-model="loginForm.username"
                placeholder="请输入用户名"
                prefix-icon="User"
                size="large"
              />
            </el-form-item>
            <el-form-item prop="password">
              <el-input
                v-model="loginForm.password"
                type="password"
                placeholder="请输入密码"
                prefix-icon="Lock"
                size="large"
                show-password
                @keyup.enter="handleLogin"
              />
            </el-form-item>
            <el-form-item>
              <el-button
                type="primary"
                size="large"
                :loading="loading"
                class="login-button"
                @click="handleLogin"
              >
                登 录
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        
        <!-- 注册表单 -->
        <el-tab-pane label="注册" name="register">
          <el-form ref="registerFormRef" :model="registerForm" :rules="registerRules" class="register-form">
            <el-form-item prop="username">
              <el-input
                v-model="registerForm.username"
                placeholder="请输入用户名"
                prefix-icon="User"
                size="large"
              />
            </el-form-item>
            <el-form-item prop="password">
              <el-input
                v-model="registerForm.password"
                type="password"
                placeholder="请输入密码"
                prefix-icon="Lock"
                size="large"
                show-password
              />
            </el-form-item>
            <el-form-item prop="confirmPassword">
              <el-input
                v-model="registerForm.confirmPassword"
                type="password"
                placeholder="请确认密码"
                prefix-icon="Lock"
                size="large"
                show-password
              />
            </el-form-item>
            <el-form-item prop="realName">
              <el-input
                v-model="registerForm.realName"
                placeholder="请输入真实姓名"
                prefix-icon="UserFilled"
                size="large"
              />
            </el-form-item>
            <el-form-item prop="phone">
              <el-input
                v-model="registerForm.phone"
                placeholder="请输入手机号"
                prefix-icon="Phone"
                size="large"
              />
            </el-form-item>
            <el-form-item prop="idCard">
              <el-input
                v-model="registerForm.idCard"
                placeholder="请输入身份证号"
                prefix-icon="CreditCard"
                size="large"
              />
            </el-form-item>
            <el-form-item prop="address">
              <el-input
                v-model="registerForm.address"
                placeholder="请输入地址（选填）"
                prefix-icon="Location"
                size="large"
              />
            </el-form-item>
            <el-form-item prop="petExperience">
              <el-input
                v-model="registerForm.petExperience"
                type="textarea"
                :rows="3"
                placeholder="请输入养宠经历（选填）"
                size="large"
              />
            </el-form-item>
            <el-form-item>
              <el-button
                type="primary"
                size="large"
                :loading="loading"
                class="login-button"
                @click="handleRegister"
              >
                注 册
              </el-button>
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

const loginForm = reactive({
  username: '',
  password: ''
})

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  realName: '',
  phone: '',
  idCard: '',
  address: '',
  petExperience: ''
})

// 验证确认密码是否一致
const validateConfirmPassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

// 验证手机号格式
const validatePhone = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入手机号'))
  } else if (!/^1[3-9]\d{9}$/.test(value)) {
    callback(new Error('请输入正确的手机号'))
  } else {
    callback()
  }
}

// 验证身份证号格式
const validateIdCard = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入身份证号'))
  } else if (!/^[1-9]\d{5}(18|19|20)\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])\d{3}[0-9Xx]$/.test(value)) {
    callback(new Error('请输入正确的身份证号'))
  } else {
    callback()
  }
}

const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
}

const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在3到20个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  phone: [
    { required: true, validator: validatePhone, trigger: 'blur' }
  ],
  idCard: [
    { required: true, validator: validateIdCard, trigger: 'blur' }
  ]
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
          username: res.data.username,
          role: res.data.role,
          userId: res.data.userId || res.data.adminId
        })

        ElMessage.success('登录成功')
        // 根据角色跳转到不同的页面
        if (res.data.role === 'admin') {
          router.push('/dashboard')
        } else {
          router.push('/user/home')
        }
      } catch (error) {
        // 错误信息已在 request.js 的响应拦截器中处理
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
        const res = await register(registerForm)
        
        ElMessage.success('注册成功，请登录')
        
        // 切换到登录标签
        activeTab.value = 'login'
        
        // 清空注册表单
        Object.keys(registerForm).forEach(key => {
          registerForm[key] = ''
        })
      } catch (error) {
        // 错误信息已在 request.js 的响应拦截器中处理
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
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.login-card {
  width: 100%;
  max-width: 500px;
  padding: 40px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.login-header {
  text-align: center;
  margin-bottom: 32px;

  h2 {
    font-size: 24px;
    color: #333;
    margin-bottom: 8px;
  }

  p {
    font-size: 14px;
    color: #999;
  }
}

.login-tabs {
  :deep(.el-tabs__header) {
    margin-bottom: 24px;
  }
  
  :deep(.el-tabs__nav-wrap::after) {
    display: none;
  }
  
  :deep(.el-tabs__item) {
    font-size: 16px;
  }
}

.login-form,
.register-form {
  .login-button {
    width: 100%;
  }
}
</style>
