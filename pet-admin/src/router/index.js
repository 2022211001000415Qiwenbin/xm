import { createRouter, createWebHistory } from 'vue-router'
import store from '@/store'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    component: () => import('@/views/Layout.vue'),
    redirect: '/dashboard',
    meta: { requiresAuth: true, roles: ['admin'] },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '首页', icon: 'HomeFilled', roles: ['admin'] }
      },
      {
        path: 'abandoned-pet',
        name: 'AbandonedPet',
        component: () => import('@/views/abandoned-pet/Index.vue'),
        meta: { title: '弃养宠物管理', icon: 'List', roles: ['admin'] }
      },
      {
        path: 'adoption-application',
        name: 'AdoptionApplication',
        component: () => import('@/views/adoption-application/Index.vue'),
        meta: { title: '领养申请管理', icon: 'DocumentChecked', roles: ['admin'] }
      },
      {
        path: 'appointment',
        name: 'Appointment',
        component: () => import('@/views/appointment/Index.vue'),
        meta: { title: '预约管理', icon: 'Calendar', roles: ['admin'] }
      },
      {
        path: 'user-manage',
        name: 'UserManage',
        component: () => import('@/views/user-manage/Index.vue'),
        meta: { title: '用户管理', icon: 'UserFilled', roles: ['admin'] }
      },
      {
        path: 'feedback-manage',
        name: 'FeedbackManage',
        component: () => import('@/views/feedback-manage/Index.vue'),
        meta: { title: '反馈管理', icon: 'ChatDotRound', roles: ['admin'] }
      },
      {
        path: 'knowledge-manage',
        name: 'KnowledgeManage',
        component: () => import('@/views/knowledge-manage/Index.vue'),
        meta: { title: '知识管理', icon: 'Reading', roles: ['admin'] }
      },
      {
        path: 'knowledge-manage/:id',
        name: 'KnowledgeManageDetail',
        component: () => import('@/views/knowledge-manage/Detail.vue'),
        meta: { title: '知识详情', roles: ['admin'] }
      }
    ]
  },
  {
    path: '/user',
    component: () => import('@/views/Layout.vue'),
    redirect: '/user/home',
    meta: { requiresAuth: true, roles: ['user'] },
    children: [
      {
        path: 'home',
        name: 'UserHome',
        component: () => import('@/views/user/Home.vue'),
        meta: { title: '首页', icon: 'HomeFilled', roles: ['user'] }
      },
      {
        path: 'adoptions',
        name: 'MyAdoptions',
        component: () => import('@/views/user/MyAdoptions.vue'),
        meta: { title: '我的领养申请', icon: 'Document', roles: ['user'] }
      },
      {
        path: 'reservations',
        name: 'MyReservations',
        component: () => import('@/views/user/MyReservations.vue'),
        meta: { title: '我的预约', icon: 'Calendar', roles: ['user'] }
      },
      {
        path: 'pet/:id',
        name: 'PetDetail',
        component: () => import('@/views/user/PetDetail.vue'),
        meta: { title: '宠物详情', roles: ['user'] }
      },
      {
        path: 'profile',
        name: 'UserProfile',
        component: () => import('@/views/user/Profile.vue'),
        meta: { title: '个人中心', icon: 'User', roles: ['user'] }
      },
      {
        path: 'feedback',
        name: 'UserFeedback',
        component: () => import('@/views/user/Feedback.vue'),
        meta: { title: '意见反馈', icon: 'ChatDotRound', roles: ['user'] }
      },
      {
        path: 'knowledge',
        name: 'UserKnowledge',
        component: () => import('@/views/user/KnowledgeList.vue'),
        meta: { title: '宠物知识', icon: 'Reading', roles: ['user'] }
      },
      {
        path: 'knowledge/:id',
        name: 'KnowledgeDetail',
        component: () => import('@/views/user/KnowledgeDetail.vue'),
        meta: { title: '知识详情', roles: ['user'] }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = store.state.user.token
  const userRole = store.state.user.role

  if (to.meta.requiresAuth !== false && !token) {
    next('/login')
  } else if (to.path === '/login' && token) {
    // 根据角色跳转到不同的首页
    if (userRole === 'admin') {
      next('/dashboard')
    } else {
      next('/user/home')
    }
  } else if (to.meta.requiresAuth && token) {
    // 检查角色权限
    if (to.meta.roles && !to.meta.roles.includes(userRole)) {
      // 如果角色不匹配，跳转到对应的首页
      if (userRole === 'admin') {
        next('/dashboard')
      } else {
        next('/user/home')
      }
    } else {
      next()
    }
  } else {
    next()
  }
})

export default router
