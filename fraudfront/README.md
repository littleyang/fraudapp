# 🚨 反欺诈管理系统

基于 Vue 3 + Vite 构建的前端管理平台，用于管理反欺诈相关数据，包括检测规则、黑名单、交易数据、通知用户及告警记录等。

---

## 📦 项目结构
````
zy_ai/ 
├── src/ │ 
├── api/ # 所有 API 接口封装 
│ ├── views/ # 页面组件（Rule, Blacklist, NotifyUser, Transaction 等） 
│ ├── router/ # Vue Router 配置 
│ ├── layouts/ # 全局布局组件（如 MainLayout.vue） 
│ ├── App.vue # 根组件 
│ └── main.js # 应用入口 
├── vite.config.js # Vite 配置（含代理） 
├── package.json # 依赖管理 └── README.md # 项目说明
````

---

## 🚀 快速开始

### 安装依赖
````
npm install
````
## 启动本地开发服务
````
npm run dev
````
默认地址：http://localhost:5173

## 构建生产环境
````
npm run build
````

