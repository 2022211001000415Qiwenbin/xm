import { createStore } from 'vuex'

export default createStore({
  state: {
    user: {
      token: localStorage.getItem('token') || '',
      username: localStorage.getItem('username') || '',
      role: localStorage.getItem('role') || '',
      userId: localStorage.getItem('userId') || ''
    },
    sidebarCollapsed: false
  },
  mutations: {
    SET_TOKEN(state, token) {
      state.user.token = token
      localStorage.setItem('token', token)
    },
    SET_USERNAME(state, username) {
      state.user.username = username
      localStorage.setItem('username', username)
    },
    SET_ROLE(state, role) {
      state.user.role = role
      localStorage.setItem('role', role)
    },
    SET_USER_ID(state, userId) {
      state.user.userId = userId
      localStorage.setItem('userId', userId)
    },
    CLEAR_USER(state) {
      state.user.token = ''
      state.user.username = ''
      state.user.role = ''
      state.user.userId = ''
      localStorage.removeItem('token')
      localStorage.removeItem('username')
      localStorage.removeItem('role')
      localStorage.removeItem('userId')
    },
    TOGGLE_SIDEBAR(state) {
      state.sidebarCollapsed = !state.sidebarCollapsed
    }
  },
  actions: {
    login({ commit }, { token, username, role, userId }) {
      commit('SET_TOKEN', token)
      commit('SET_USERNAME', username)
      commit('SET_ROLE', role)
      commit('SET_USER_ID', userId)
    },
    logout({ commit }) {
      commit('CLEAR_USER')
    }
  },
  getters: {
    isLoggedIn: state => !!state.user.token,
    isAdmin: state => state.user.role === 'admin',
    isUser: state => state.user.role === 'user'
  }
})
