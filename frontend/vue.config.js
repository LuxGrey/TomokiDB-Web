const { defineConfig } = require('@vue/cli-service')
const path = require("path");

module.exports = defineConfig({
  transpileDependencies: true,
  outputDir: path.resolve(__dirname, "../backend/src/main/resources/public"),
  devServer: {
    port: 8081,
    // proxy all dev-server requests starting with /api
    // to backend (localhost:8080) using http-proxy-middleware
    // see https://cli.vuejs.org/config/#devserver-proxy
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        ws: true,
        changeOrigin: true
      }
    }
  }
})
