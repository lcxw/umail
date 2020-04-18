// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import router from './router'

import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'

import axios from 'axios'

import './assets/icon/iconfont.css'

import VueQuillEditor from 'vue-quill-editor'
import 'quill/dist/quill.core.css'
import 'quill/dist/quill.snow.css'
import 'quill/dist/quill.bubble.css'

// import Mint from 'mint-ui';
// import 'mint-ui/lib/style.css';

import mui from './assets/mui/js/mui.js';
// 这里全局应用mui的css文件会影响element-ui
import './assets/mui/css/mui.css';

Vue.config.productionTip = false

Vue.use(ElementUI)
Vue.use(VueQuillEditor)
// Vue.use(Mint);

Vue.prototype.$axios = axios;
Vue.prototype.$mui = mui;
/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  components: { App },
  template: '<App/>'
})
