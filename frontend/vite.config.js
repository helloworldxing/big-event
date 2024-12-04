import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'node:path'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server:{
    proxy:{
      '/api':{//获取路径中包含了api的请求
        target: 'http://localhost:8080',//代理的目标地址，后台服务所在的源
        changeOrigin: true,//是否改变源
        rewrite:(path)=>path.replace(/^\/api/,'')//重写路径，api替换为''
        }
    }
  }
})
