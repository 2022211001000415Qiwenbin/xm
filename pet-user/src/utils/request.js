import axios from 'axios'
import { ElMessage } from 'element-plus'
import store from '@/store'
import router from '@/router'

// 创建 axios 实例
const service = axios.create({
  baseURL: '/api',
  timeout: 15000
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 在请求发送前做些什么
    if (store.state.user.token) {
      // 让每个请求携带 token
      config.headers['Authorization'] = `Bearer ${store.state.user.token}`
    }
    return config
  },
  error => {
    // 对请求错误做些什么
    console.error('Request error:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    // 对响应数据做点什么
    const res = response.data

    // 如果返回的状态码不是 200，说明接口请求失败
    if (res.code !== 200) {
      ElMessage({
        message: res.message || '请求失败',
        type: 'error',
        duration: 5 * 1000
      })

      // 401: 未登录或 token 过期
      if (res.code === 401) {
        // 重新登录
        store.dispatch('logout')
        router.push('/login')
      }

      return Promise.reject(new Error(res.message || '请求失败'))
    } else {
      return res
    }
  },
  error => {
    // 对响应错误做点什么
    console.error('Response error:', error)

    let message = '网络错误'
    if (error.response) {
      switch (error.response.status) {
        case 400:
          message = '请求错误'
          break
        case 401:
          message = '未授权，请登录'
          store.dispatch('logout')
          router.push('/login')
          break
        case 403:
          message = '拒绝访问'
          break
        case 404:
          message = '请求地址不存在'
          break
        case 500:
          message = '服务器内部错误'
          break
        default:
          message = error.response.data.message || '请求失败'
      }
    } else if (error.message.includes('timeout')) {
      message = '请求超时'
    } else if (error.message.includes('Network')) {
      message = '网络连接异常'
    }

    ElMessage({
      message,
      type: 'error',
      duration: 5 * 1000
    })

    return Promise.reject(error)
  }
)

export default service
