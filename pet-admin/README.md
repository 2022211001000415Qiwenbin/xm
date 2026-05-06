
# 宠物管理平台 - 前端整合版

这是一个整合了管理端和用户端的宠物管理平台前端项目，通过角色权限控制实现不同用户访问不同功能。

## 项目特点

- ✅ 统一的前端项目，无需维护多个前端代码库
- ✅ 基于角色（admin/user）的权限控制
- ✅ 共享API和组件，减少代码重复
- ✅ 响应式设计，支持多种设备

## 技术栈

- Vue 3
- Vue Router 4
- Vuex 4
- Element Plus
- Axios

## 项目结构

```
pet-admin/
├── src/
│   ├── api/              # API接口
│   │   ├── abandonedPet.js
│   │   ├── adoptionApplication.js
│   │   ├── appointment.js
│   │   ├── auth.js
│   │   ├── dashboard.js
│   │   └── pet.js
│   ├── router/           # 路由配置
│   │   └── index.js
│   ├── store/            # Vuex状态管理
│   │   └── index.js
│   ├── styles/           # 全局样式
│   │   └── index.scss
│   ├── utils/            # 工具函数
│   │   └── request.js
│   └── views/            # 页面组件
│       ├── Layout.vue     # 布局组件
│       ├── Login.vue     # 登录/注册页面
│       ├── Dashboard.vue # 管理员仪表板
│       ├── abandoned-pet/ # 弃养宠物管理
│       ├── adoption-application/ # 领养申请管理
│       ├── appointment/   # 预约管理
│       └── user/        # 用户端页面
│           ├── Home.vue          # 用户首页
│           ├── MyAdoptions.vue   # 我的领养申请
│           ├── MyReservations.vue # 我的预约
│           └── Profile.vue       # 个人中心
```

## 功能模块

### 管理员功能

- 首页仪表板
- 弃养宠物管理（增删改查）
- 领养申请管理（审核通过/拒绝）
- 预约管理（确认/取消）

### 普通用户功能

- 首页（浏览待领养宠物）
- 我的领养申请（查看申请状态）
- 我的预约（查看和管理预约）
- 个人中心（个人信息管理）

## 路由权限控制

系统通过Vuex存储用户角色信息，路由守卫根据角色控制访问权限：

- `/dashboard` - 仅管理员可访问
- `/abandoned-pet` - 仅管理员可访问
- `/adoption-application` - 仅管理员可访问
- `/appointment` - 仅管理员可访问
- `/user/home` - 仅普通用户可访问
- `/user/adoptions` - 仅普通用户可访问
- `/user/reservations` - 仅普通用户可访问
- `/user/profile` - 仅普通用户可访问

## 快速开始

### 安装依赖

```bash
npm install
```

### 开发环境运行

```bash
npm run dev
```

### 生产环境构建

```bash
npm run build
```

## 登录说明

系统支持两种角色登录：

1. **管理员**
   - 用户名：admin
   - 密码：123456
   - 登录后跳转到：`/dashboard`

2. **普通用户**
   - 需要先注册账号
   - 登录后跳转到：`/user/home`

## API配置

API基础路径配置在 `src/utils/request.js` 中，根据后端部署情况修改：

```javascript
const service = axios.create({
  baseURL: 'http://localhost:8080', // 修改为你的后端地址
  timeout: 5000
})
```

## 注意事项

1. 首次使用前请确保后端服务已启动
2. 管理员账号已在数据库初始化脚本中创建
3. 普通用户需要先注册才能使用
4. 所有API请求都需要携带token，token在登录后自动存储

## 开发建议

1. 添加新功能时，注意区分角色权限
2. 共享的组件和工具函数放在合适的位置
3. 遵循现有的代码风格和目录结构
4. API接口统一放在 `api` 目录下管理

## 许可证

MIT License
