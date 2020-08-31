<template>
  <div class="ums-page">
    <div class="form-box">
      <el-form ref="form" class="login-form" :model="user">
        <el-form-item>
          <el-input
            v-model="user.name"
            size="small"
            placeholder="用户名/邮箱"
            style="width:250px"
          >
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-input
            v-model="user.secret"
            size="small"
            placeholder="密码"
            style="width:250px"
            show-password
          ></el-input>
        </el-form-item>
        <el-form-item>
          <el-button
            v-if="user.name.trim() == '' || user.secret.trim() == ''"
            size="small"
            :disabled="true"
            style="width:250px;height:40px;color: #c2c2c2;font-size:1.1em;background-color: #f5f5f5;border-color: transparent;"
            @click="login"
            >登录</el-button
          >
          <el-button
            v-else
            size="small"
            :disabled="false"
            style="width:250px;height:40px;font-size:1.1em;color: #ffffff;background-color: #409EFF;border-color: transparent;"
            @click="login"
            >登录</el-button
          >
        </el-form-item>
      </el-form>
      <div class="find-button" @click="toForget">
        <span>邮箱找回密码</span>
      </div>
      <div class="register-button" @click="toRegister">
        <span>新用户注册</span>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "webin",
  created() {},
  computed: {},
  data() {
    return {
      user: {
        name: "",
        email: "",
        secret: "",
        verify: "",
        time: ""
      }
    };
  },
  methods: {
    login() {
      var that = this;
      this.$axios
        .post(localStorage.getItem("base_url") + "/token", this.user)
        .then(function(res) {
          console.log(res.data);
          if (res.data.code == "200") {
            if (typeof Storage !== "undefined") {
              // if(!localStorage.token){
              localStorage.token = res.data.data;
              console.log(localStorage.token);
              // }
              that.$message({
                type: "success",
                message: "登录成功!"
              });
              that.$router.replace({
                name: "PCWelcome"
              });
            } else {
              that.$message({
                type: "success",
                message: "抱歉！您的浏览器不支持 Web Storage ..."
              });
            }
          }
          if (res.data.code == "201") {
            that.$message({
              type: "error",
              message: "登录失败，请检查用户名或密码!"
            });
          }
        });
    },
    toRegister() {
      this.$router.replace({ name: "PCWebreg" });
    },
    toForget() {
      this.$router.replace({ name: "PCWebForget" });
    }
  }
};
</script>

<style scoped>
.form-box {
  margin: 100px auto;
  height: 400px;
  width: 400px;
  border: 1px solid #ccc;
  box-shadow: 0 1px 1px 0 rgba(0, 0, 0, 0.1);
}
.login-form {
  padding: 75px;
}
.find-button {
  float: left;
  padding-left: 20px;
  color: #409eff;
  cursor: pointer;
}
.register-button {
  float: right;
  padding-right: 20px;
  color: #409eff;
  cursor: pointer;
}
</style>
