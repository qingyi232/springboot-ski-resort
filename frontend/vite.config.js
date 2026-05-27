import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  },
  server: {
    port: 3000,
    proxy: {
      // 代理所有后端请求到8080端口
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/uploads': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/doc.html': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/swagger-resources': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/v2': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/webjars': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})



