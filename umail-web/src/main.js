import Vue from "vue";
import App from "./App.vue";
import router from "./router";
import store from "./store";
import VueQuillEditor from "vue-quill-editor";
import "quill/dist/quill.core.css";
import "quill/dist/quill.snow.css";
import "quill/dist/quill.bubble.css";
import ElementUI from "element-ui";
import "element-ui/lib/theme-chalk/index.css";
import axios from "axios";
import "./assets/icon/iconfont.css";
Vue.config.productionTip = false;

Vue.use(ElementUI);
Vue.use(VueQuillEditor);
Vue.prototype.$axios = axios;
new Vue({
  router,
  store,
  render: h => h(App)
}).$mount("#app");
