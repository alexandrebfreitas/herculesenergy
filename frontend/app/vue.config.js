const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    port: 9000, // CHANGE YOUR PORT HERE!
    proxy: {
      '/api': {
        target: process.env.VUE_APP_BACKEND_URL,  // Usa a URL dinâmica do backend
        changeOrigin: true,
        pathRewrite: { '^/api': '' },
        logLevel: 'debug'
      }
    }
  }
})
