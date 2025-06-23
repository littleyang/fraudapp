# 第一阶段：构建 Vue 应用
#FROM node:20-alpine AS build-stage
FROM swr.cn-north-4.myhuaweicloud.com/ddn-k8s/docker.io/node:20-alpine3.20 AS build-stage

WORKDIR /app

# 拷贝依赖文件
COPY package*.json ./
RUN npm install --registry=https://registry.npmmirror.com

# 拷贝项目文件并构建
COPY . .
RUN npm run build

# 第二阶段：用 nginx 部署编译后的静态文件
#FROM nginx:1.25-alpine AS production-stage
FROM swr.cn-north-4.myhuaweicloud.com/ddn-k8s/docker.io/nginx:1.25-alpine AS production-stage

# 删除默认配置
#RUN rm -rf /usr/share/nginx/html/*

# 复制构建产物到 nginx html 目录
COPY --from=build-stage /app/dist /usr/share/nginx/html

# 如果你有自定义 nginx 配置（可选）
COPY nginx.conf /etc/nginx/nginx.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
