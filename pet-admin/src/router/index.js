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
        path: 'foster-manage',
        name: 'FosterManage',
        component: () => import('@/views/foster-manage/Index.vue'),
        meta: { title: '寄养管理', icon: 'Van', roles: ['admin'] }
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
    component: () => import('@/views/UserLayout.vue'),
    redirect: '/user/home',
    meta: { requiresAuth: true, roles: ['user'] },
    children: [
      {
        path: 'home',
        name: 'UserHome',
        component: () => import('@/views/user/Home.vue'),
        meta: { title: '发现宠物', icon: 'HomeFilled', roles: ['user'] }
      },
      {
        path: 'adoptions',
        name: 'MyAdoptions',
        component: () => import('@/views/user/MyAdoptions.vue'),
        meta: { title: '我的领养', icon: 'Document', roles: ['user'] }
      },
      {
        path: 'reservations',
        name: 'MyReservations',
        component: () => import('@/views/user/MyReservations.vue'),
        meta: { title: '我的预约', icon: 'Calendar', roles: ['user'] }
      },
      {
        path: 'foster',
        name: 'MyFoster',
        component: () => import('@/views/foster-manage/UserFoster.vue'),
        meta: { title: '我的寄养', icon: 'Van', roles: ['user'] }
      },
      {
        path: 'pet/:id',
        name: 'PetDetail',
        component: () => import('@/views/user/PetDetail.vue'),
        meta: { title: '宠物详情', roles: ['user'] }
      },
      {
        path: 'adopt/:id',
        name: 'AdoptApply',
        component: () => import('@/views/user/AdoptApply.vue'),
        meta: { title: '申请领养', roles: ['user'] }
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

  // 未登录，跳转登录页
  if (to.meta.requiresAuth !== false && !token) {
    next('/login')
    return
  }

  // 已登录访问登录页，按角色跳转
  if (to.path === '/login' && token) {
    next(userRole === 'admin' ? '/dashboard' : '/user/home')
    return
  }

  // 已登录但访问了不存在的路径，按角色跳转首页
  if (to.matched.length === 0 && token) {
    next(userRole === 'admin' ? '/dashboard' : '/user/home')
    return
  }

  // 权限检查
  if (to.meta.requiresAuth && token && to.meta.roles && !to.meta.roles.includes(userRole)) {
    next(userRole === 'admin' ? '/dashboard' : '/user/home')
    return
  }

  next()
})

export default router
