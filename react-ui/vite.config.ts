import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import path from 'path'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: {
      '@js-joda/core': path.resolve(__dirname, 'node_modules/@js-joda/core'),
      // Mock 'ws' for the browser environment to prevent Ktor from failing
      'ws': path.resolve(__dirname, 'node_modules/ws'),
    },
  },
  optimizeDeps: {
    // Explicitly include the Kotlin library for optimization
    include: ['HotelZagrous-app-shared'],
  },
  server: {
    fs: {
      allow: ['..'],
    },
  },
  build: {
    commonjsOptions: {
      include: [/HotelZagrous-app-shared/, /node_modules/],
    },
  },
})
