<template>
  <div class="layout-container">
    <el-container>
      <el-aside :width="isCollapse ? '64px' : '220px'" class="sidebar">
        <div class="logo-container">
          <span v-if="!isCollapse" class="logo-text">{{ isUser ? '宠物领养平台' : '宠物管理系统' }}</span>
          <span v-else class="logo-text-short">{{ isUser ? 'Pet' : 'CMS' }}</span>
        </div>
        <el-menu
          :default-active="activeMenu"
          :collapse="isCollapse"
          :unique-opened="true"
          router
          class="sidebar-menu"
        >
          <el-menu-item
            v-for="route in menuRoutes"
            :key="route.path"
            :index="route.path"
          >
            <el-icon><component :is="route.meta.icon" /></el-icon>
            <template #title>{{ route.meta.title }}</template>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <el-container>
        <el-header class="header">
          <div class="header-left">
            <el-icon class="collapse-icon" @click="toggleSidebar">
              <component :is="isCollapse ? 'Expand' : 'Fold'" />
            </el-icon>
            <el-breadcrumb separator="/">
              <el-breadcrumb-item :to="{ path: isUser ? '/user/home' : '/dashboard' }">首页</el-breadcrumb-item>
              <el-breadcrumb-item v-if="currentRoute.meta.title">
                {{ currentRoute.meta.title }}
              </el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          <div class="header-right">
            <el-dropdown @command="handleCommand">
              <span class="user-info">
                <el-icon><User /></el-icon>
                <span class="username">{{ username }}</span>
                <el-icon class="el-icon--right"><CaretBottom /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>

        <el-main class="main-content">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const store = useStore()

const isCollapse = computed(() => store.state.sidebarCollapsed)
const isUser = computed(() => store.getters.isUser)
const username = computed(() => store.state.user.username)
const currentRoute = computed(() => route)

// 获取当前激活的菜单
const activeMenu = computed(() => {
  const matched = route.matched
  return matched.length > 1 ? matched[1].path : route.path
})

// 菜单路由配置
const menuRoutes = computed(() => {
  if (isUser.value) {
    // 用户端菜单
    return [
      {
        path: '/user/home',
        meta: { title: '首页', icon: 'HomeFilled' }
      },
      {
        path: '/user/adoptions',
        meta: { title: '我的领养申请', icon: 'Document' }
      },
      {
        path: '/user/reservations',
        meta: { title: '我的预约', icon: 'Calendar' }
      },
      {
        path: '/user/knowledge',
        meta: { title: '宠物知识', icon: 'Reading' }
      },
      {
        path: '/user/profile',
        meta: { title: '个人中心', icon: 'User' }
      },
      {
        path: '/user/feedback',
        meta: { title: '意见反馈', icon: 'ChatDotRound' }
      }
    ]
  } else {
    // 管理端菜单
    return [
      {
        path: '/dashboard',
        meta: { title: '首页', icon: 'HomeFilled' }
      },
      {
        path: '/abandoned-pet',
        meta: { title: '弃养宠物管理', icon: 'List' }
      },
      {
        path: '/adoption-application',
        meta: { title: '领养申请管理', icon: 'DocumentChecked' }
      },
      {
        path: '/appointment',
        meta: { title: '预约管理', icon: 'Calendar' }
      },
      {
        path: '/knowledge-manage',
        meta: { title: '知识管理', icon: 'Reading' }
      },
      {
        path: '/user-manage',
        meta: { title: '用户管理', icon: 'UserFilled' }
      },
      {
        path: '/feedback-manage',
        meta: { title: '反馈管理', icon: 'ChatDotRound' }
      }
    ]
  }
})

const toggleSidebar = () => {
  store.commit('TOGGLE_SIDEBAR')
}

const handleCommand = async (command) => {
  if (command === 'logout') {
    try {
      await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })

      await store.dispatch('logout')
      ElMessage.success('已退出登录')
      router.push('/login')
    } catch (error) {
      // 用户取消操作
    }
  }
}
</script>

<style scoped lang="scss">
.layout-container {
  height: 100vh;

  .el-container {
    height: 100%;
  }
}

.sidebar {
  background-color: #304156;
  transition: width 0.3s;
  overflow-x: hidden;

  .logo-container {
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: #2b3a4d;

    .logo-text {
      color: #fff;
      font-size: 18px;
      font-weight: bold;
      white-space: nowrap;
    }

    .logo-text-short {
      color: #fff;
      font-size: 20px;
      font-weight: bold;
    }
  }

  .sidebar-menu {
    border-right: none;
    background-color: #304156;

    &:not(.el-menu--collapse) {
      width: 220px;
    }

    :deep(.el-menu-item) {
      color: #bfcbd9;

      &:hover {
        background-color: #263445;
      }

      &.is-active {
        background-color: #409EFF;
        color: #fff;
      }
    }
  }
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #fff;
  border-bottom: 1px solid #e6e6e6;
  padding: 0 20px;

  .header-left {
    display: flex;
    align-items: center;

    .collapse-icon {
      font-size: 20px;
      cursor: pointer;
      margin-right: 20px;

      &:hover {
        color: #409EFF;
      }
    }
  }

  .header-right {
    .user-info {
      display: flex;
      align-items: center;
      cursor: pointer;

      .username {
        margin: 0 8px;
      }
    }
  }
}

.main-content {
  background-color: #f5f7fa;
  padding: 20px;
  overflow-y: auto;
}
</style>
