import Vue from "vue";
import Router from "vue-router";
import PCWelcome from "@/components/pc/welcome";
import PCSend from "@/components/pc/send";
import PCAttach from "@/components/pc/attach";
import PCWebin from "@/components/pc/webin";
import PCWebreg from "@/components/pc/webreg";
import PCWebForget from "@/components/pc/forget";
import PCAccount from "@/components/pc/account";
import PCSign from "@/components/pc/sign";
import PCContact from "@/components/pc/contact";
Vue.use(Router);

export default new Router({
  routes: [
    {
      path: "/pc/webin",
      name: "PCWebin",
      component: PCWebin
    },
    {
      path: "/pc/webreg",
      name: "PCWebreg",
      component: PCWebreg
    },
    {
      path: "/pc/forget",
      name: "PCWebForget",
      component: PCWebForget
    },
    {
      path: "/pc/welcome",
      name: "PCWelcome",
      component: PCWelcome
    },
    {
      path: "/pc/account",
      name: "PCAccount",
      component: PCAccount
    },
    {
      path: "/pc/send",
      name: "PCSend",
      component: PCSend,
      props: true
    },
    {
      path: "/pc/attach",
      name: "PCAttach",
      component: PCAttach,
      props: true
    },
    {
      path: "/pc/sign",
      name: "PCSign",
      component: PCSign,
      props: true
    },
    {
      path: "/pc/contact",
      name: "PCContact",
      component: PCContact,
      props: true
    }
  ]
});
