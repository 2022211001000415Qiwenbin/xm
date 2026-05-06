<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useStore } from 'vuex'

const store = useStore()

onMounted(() => {
  // 检查本地存储的登录状态
  const token = localStorage.getItem('token')
  if (token) {
    store.commit('SET_TOKEN', token)
    store.commit('SET_USERNAME', localStorage.getItem('username') || '')
    store.commit('SET_ROLE', localStorage.getItem('role') || '')
    store.commit('SET_USER_ID', localStorage.getItem('userId') || '')
  }
})
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

#app {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}
</style>
