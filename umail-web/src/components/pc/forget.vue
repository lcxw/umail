<template>
  <div class="ums-page">
    <div class="form-box">
      <el-form ref="form" class="reg-form" :model="user">
        <el-form-item>
          <el-input v-model="user.email"  size="small"  placeholder="邮箱" style="width:250px">
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-input v-model="user.verify"  size="small"  placeholder="6位数字验证码" style="width:150px">
          </el-input>
          <el-button type="primary" size="small" @click="doVerify"  style="width:90px;margin-top:4px;">获取验证码</el-button>
        </el-form-item>
        <el-form-item>
          <el-input v-model="user.secret" size="small"  placeholder="新密码" style="width:250px" show-password></el-input>
        </el-form-item>
        <el-form-item>
          <el-button v-if="user.email.trim()==''||user.secret.trim()==''||user.verify.trim()=='' || !/[A-Za-z0-9\u4e00-\u9fa5]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+/.test(user.email)"
                     size="small" :disabled="true"
                     style="width:250px;height:40px;color: #c2c2c2;font-size:1.1em;background-color: #f5f5f5;border-color: transparent;"
                     @click="forget">修改</el-button>
          <el-button v-else
                     size="small"
                     :disabled="false"
                     style="width:250px;height:40px;font-size:1.1em;color: #ffffff;background-color: #409EFF;border-color: transparent;" @click="forget">修改</el-button>
        </el-form-item>
      </el-form>

      <div class="register-button" @click="toLogin">
        <span>已有账号，去登陆？</span>
      </div>
    </div>
  </div>
</template>

<script>
  export default {
    name: 'webin',
    created() {
    },
    computed:{
    },
    data(){
      return {
        user:{
          name:'',
          email:'',
          secret:'',
          verify:'',
          time:''
        }
      }
    },
    methods:{
      doVerify(){
        var that = this;
        that.$axios({
          method: 'get',
          url: localStorage.getItem('base_url')+'/verify?email='+that.user.email,
          headers:{'Content-Type': 'application/json'}
        }).then(function(res) {
          console.log(res.data)
          if(res.data.code == '200'){
            that.$message({
              type: 'success',
              message: '验证码已发送到邮箱，请注意查收!'
            });
          }else{
            that.$message({
              type: 'error',
              message: '邮箱不存在'
            });
          }
        });
      },
      forget(){
        var that = this;
        this.$axios.post(localStorage.getItem('base_url')+'/forget', this.user).then(function(res) {
          console.log(res.data)
          if(res.data.code == '200'){
            that.$message({
              type: 'success',
              message: '修改密码成功!'
            });
            that.$router.replace({
              name:'PCWebin',
            });
          }else if(res.data.code == '203'){
            that.$message({
              type: 'error',
              message: '邮箱不存在'
            });
          }else{
            that.$message({
              type: 'error',
              message: '验证码错误或验证码已过期'
            });
          }
        });
      },
      toLogin(){
        this.$router.replace({ name: 'PCWebin',});
      }
    }
  }
</script>

<style scoped>
  .form-box{
    margin:100px auto;
    height: 400px;
    width:400px;
    border: 1px solid #ccc;
    box-shadow: 0 1px 1px 0 rgba(0,0,0,.1);
  }
  .reg-form{
    padding:75px;
  }
  .register-button{
    margin-top:-70px;
    float:right;
    padding-right: 20px;
    color:#409EFF;
    cursor: pointer;
  }
</style>
