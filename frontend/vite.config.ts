import { defineConfig } from 'vitest/config'
import vue from '@vitejs/plugin-vue'
import tailwindcss from '@tailwindcss/vite'
export default defineConfig({plugins:[vue(),tailwindcss()],server:{proxy:{'/api':'http://localhost:8080','/actuator':'http://localhost:8080'}},test:{environment:'jsdom',setupFiles:['./src/test/setup.ts'],exclude:['e2e/**','node_modules/**']}})
