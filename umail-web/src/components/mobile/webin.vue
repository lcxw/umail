<template>
	<div  class="form-box" style="background: #ffffff;margin-top:100px;">
		<el-form ref="form" class="login-form" :model="user">
		  <el-form-item>
			<el-input v-model="user.name" placeholder="账号" style="width:220px;">
			</el-input>
		  </el-form-item>
			<el-form-item>
			<el-input v-model="user.secret" placeholder="密码" style="width:220px" show-password></el-input>
		  </el-form-item>
		  <el-form-item>
			<el-button v-if="user.name.trim()==''||user.secret.trim()==''"
			:disabled="true"
			style="width:220px;height:40px;color: #c2c2c2;font-size:1.1em;background-color: #f5f5f5;border-color: transparent;"
			 @click="login">登录</el-button>
			<el-button v-else
			:disabled="false"
			 style="width:220px;height:40px;font-size:1.1em;color: #ffffff;background-color: #409EFF;border-color: transparent;" @click="login">登录</el-button>
		  </el-form-item>
		</el-form>
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
					secret:'',
				}
			}
		},
		methods:{
			login(){
				var _this = this;
				this.$axios.post(localStorage.getItem('base_url')+'/token', this.user).then(function(res) {
					console.log(res.data)
					if(res.data.code == '200'){
						if(typeof(Storage) !== "undefined") {
							// if(!localStorage.token){
								localStorage.token = res.data.data;
								console.log(localStorage.token)
							// }
							_this.$mui.toast('登录成功',{ duration:'long', type:'div' });
							_this.$router.push({
							  name:'MWelcome',
							})
						} else {
							_this.$mui.toast('抱歉！您的浏览器不支持 Web Storage ',{ duration:'long', type:'div' });
						}
					}else{
            _this.$mui.toast('用户名或密码错误',{ duration:'long', type:'div' });
          }
				});

			}
		}
	}
</script>

<style scoped>
	.form-box{
		margin:40px auto;
		height: 300px;
		width:300px;
		border: 1px solid #ccc;
		box-shadow: 0 1px 1px 0 rgba(0,0,0,.1);
	}
	.login-form{
		padding:40px;
	}
</style>
