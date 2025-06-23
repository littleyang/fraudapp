import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src')
    }
  },
  server: {
    proxy: {
      '/fraud': {
        target: 'http://115.29.243.179:8080',
        changeOrigin: true,
        rewrite: path => path.replace(/^\/fraud/, '/fraud') // 保持路径
      }
    }
  }
})
