const { defineConfig } = require('@vue/cli-service');
const MonacoEditorWebpackPlugin = require('monaco-editor-webpack-plugin');

module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    port: 9000, // Altere a porta aqui, se necessário
    proxy: {
      '/api': {
        target: process.env.VUE_APP_BACKEND_URL, // Usa a URL dinâmica do backend
        changeOrigin: true,
        pathRewrite: { '^/api': '' },
        logLevel: 'debug',
      },
    },
  },
  configureWebpack: {
    plugins: [
      new MonacoEditorWebpackPlugin({
        languages: ['javascript', 'typescript', 'html', 'css', 'json'], // Especifique os idiomas necessários
        features: ['!gotoSymbol'], // Opcional: desative recursos que não serão usados
      }),
    ],
  },
});
