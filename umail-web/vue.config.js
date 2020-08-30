module.exports = {
  devServer: {
    host: "0.0.0.0",
    port: "3000",
    proxy: {
      "^/api": {
        target: "<url>",
        ws: true,
        changeOrigin: true
      },
      "^/foo": {
        target: "<other_url>"
      }
    }
  }
};
