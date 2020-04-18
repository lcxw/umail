<template>
	<div>
		<header class="mui-bar mui-bar-nav">
		  <a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left"  @click="$router.go(-1)"></a>
		  <h1 class="mui-title">{{status=='update'?'编辑账号':'添加账号'}}</h1>
		</header>
		<div class="mui-content">
			<form class="mui-input-group" v-if="status=='update'">
				<div class="form-padding"></div>
				<div class="form-label"> 基础设置</div>
			    <div class="mui-input-row">
			        <label>邮箱账号</label>
			    <input type="text" class="mui-input-clear" v-model="account.account" placeholder="请输入Email地址">
			    </div>
			    <div class="mui-input-row">
			        <label>密码</label>
			        <input type="password" class="mui-input-password" v-model="account.password" placeholder="请输入密码">
			    </div>
				<!--接收设置-->
				<div class="form-padding"></div>
				<div class="form-label"> 接收设置</div>
				<div class="mui-input-row">
				    <label>接收服务器</label>
				<input type="text" class="mui-input-clear" v-model="account.rechost" placeholder="接收服务器">
				</div>
				<div class="mui-input-row">
				    <label>端口</label>
				    <input type="text" class="mui-input-clear" v-model="account.recport" placeholder="端口">
				</div>
				<div class="mui-input-row">
					<label>SSL</label>
					<input name="checkbox1" value="Item 1" type="checkbox" v-model="account.recisssl"
					@click="accountRecSSLChange" style="height:20px;margin-top: 10px;" checked>
				</div>
				<!--发送设置-->
				<div class="form-padding"></div>
				<div class="form-label"> 发送设置</div>
				<div class="mui-input-row">
				    <label>发送服务器</label>
				<input type="text" class="mui-input-clear" v-model="account.sendhost" placeholder="发送服务器">
				</div>
				<div class="mui-input-row">
				    <label>端口</label>
				    <input type="text" class="mui-input-clear" v-model="account.sendport" placeholder="端口">
				</div>
				<div class="mui-input-row">
					<label>SSL</label>
					<input name="checkbox1" value="Item 1" type="checkbox" v-model="account.sendisssl"
					@click="accountSendSSLChange" style="height:20px;margin-top: 10px;" checked>
				</div>
			   <div class="mui-button-row">
			        <button type="button" class="mui-btn mui-btn-primary"  @click="doModifyAccount">确认</button>
			        <button type="button" class="mui-btn mui-btn-danger"  @click="$router.go(-1)">取消</button>
			    </div>
			</form>

			<form class="mui-input-group" v-if="status=='create'">
				<div class="form-padding"></div>
				<div class="form-label"> 基础设置</div>
			    <div class="mui-input-row">
			        <label>邮箱账号</label>
					<input type="text" class="mui-input-clear" v-model="account.account" placeholder="请输入Email地址" @change="accountInputChange" >
			    </div>
			    <div class="mui-input-row">
			        <label>密码</label>
			        <input type="password" class="mui-input-password" v-model="account.password" placeholder="请输入密码">
			    </div>
				<!--接收设置-->
				<div class="form-padding"></div>
				<div class="form-label"> 接收设置</div>
				<div class="mui-input-row mui-radio">
					<label>IMAP</label>
					<input name="radio0" type="radio" @click="accountRecProtocolChange" data-recprotocol="imap" :checked="account.recprotocol=='imap'">
				</div>
				<div class="mui-input-row mui-radio">
					<label>POP</label>
					<input name="radio0" type="radio" @click="accountRecProtocolChange" data-recprotocol="pop3" :checked="account.recprotocol=='pop3'">
				</div>
				<div class="mui-input-row">
				    <label>接收服务器</label>
				<input type="text" class="mui-input-clear" v-model="account.rechost" placeholder="接收服务器">
				</div>
				<div class="mui-input-row">
				    <label>端口</label>
				    <input type="text" class="mui-input-clear" v-model="account.recport" placeholder="端口">
				</div>
				<div class="mui-input-row">
					<label>SSL</label>
					<input name="checkbox1" value="Item 1" type="checkbox" v-model="account.recisssl"
					 @click="accountRecSSLChange" style="height:20px;margin-top: 10px;">
				</div>
				<!--发送设置-->
				<div class="form-padding"></div>
				<div class="form-label"> 发送设置</div>
				<div class="mui-input-row">
				    <label>发送服务器</label>
				<input type="text" class="mui-input-clear" v-model="account.sendhost" placeholder="发送服务器">
				</div>
				<div class="mui-input-row">
				    <label>端口</label>
				    <input type="text" class="mui-input-clear" v-model="account.sendport" placeholder="端口">
				</div>
				<div class="mui-input-row">
					<label>SSL</label>
					<input name="checkbox1" value="Item 1" type="checkbox" v-model="account.sendisssl"
					  @click="accountSendSSLChange" style="height:20px;margin-top: 10px;" checked>
				</div>
			   <div class="mui-button-row">
			        <button type="button" class="mui-btn mui-btn-primary"  @click="addAccount">确认</button>
			        <button type="button" class="mui-btn mui-btn-danger"  @click="$router.go(-1)">取消</button>
			    </div>
			</form>
		</div>
		</div>
</template>

<script>
  import mui from '../../assets/mui/js/mui.js'
	export default {
		name: 'editaccount',
		created() {
			this.status = this.$route.params.status;
			this.account = this.$route.params.account;
			this.account.recisssl = this.$route.params.account.recisssl=='true'?true:false;
			this.account.sendisssl = this.$route.params.account.sendisssl=='true'?true:false;
		},
		mounted() {
			this.servers = [
				  {
					"name": "qq.com",
					"recprotocol": "pop3",
					"rechost": "pop.qq.com",
					"recport": "995",
					"recisssl": "true",
					"sendprotocol": "smtp",
					"sendhost": "smtp.qq.com",
					"sendport": "465",
					"sendisssl": "true"
				  },
				  {
					"name": "foxmail.com",
					"recprotocol": "pop3",
					"rechost": "pop.qq.com",
					"recport": "995",
					"recisssl": "true",
					"sendprotocol": "pop3",
					"sendhost": "smtp.qq.com",
					"sendport": "465",
					"sendisssl": "true"
				  },
				  {
					"name": "foxmail.com.cn",
					"recprotocol": "pop3",
					"rechost": "pop.qq.com",
					"recport": "995",
					"recisssl": "true",
					"sendprotocol": "smtp",
					"sendhost": "smtp.qq.com",
					"sendport": "465",
					"sendisssl": "true"
				  },
				  {
					"name": "vip.qq.com",
					"recprotocol": "pop3",
					"rechost": "pop.qq.com",
					"recport": "995",
					"recisssl": "true",
					"sendprotocol": "smtp",
					"sendhost": "smtp.qq.com",
					"sendport": "465",
					"sendisssl": "true"
				  },
				  {
					"name": "163.com",
					"recprotocol": "imap",
					"rechost": "imap.163.com",
					"recport": "143",
					"recisssl": "false",
					"sendprotocol": "smtp",
					"sendhost": "smtp.163.com",
					"sendport": "25",
					"sendisssl": "false"
				  },
				  {
					"name": "126.com",
					"recprotocol": "imap",
					"rechost": "imap.126.com",
					"recport": "143",
					"recisssl": "false",
					"sendprotocol": "smtp",
					"sendhost": "smtp.126.com",
					"sendport": "25",
					"sendisssl": "false"
				  },
				  {
					"name": "vip.163.com",
					"recprotocol": "imap",
					"rechost": "imap.vip.163.com",
					"recport": "143",
					"recisssl": "false",
					"sendprotocol": "smtp",
					"sendhost": "smtp.vip.163.com",
					"sendport": "25",
					"sendisssl": "false"
				  },
				  {
					"name": "sina.com",
					"recprotocol": "pop3",
					"rechost": "pop.sina.com",
					"recport": "110",
					"recisssl": "false",
					"sendprotocol": "smtp",
					"sendhost": "smtp.sina.com",
					"sendport": "25",
					"sendisssl": "false"
				  },
				  {
					"name": "vip.sina.com",
					"recprotocol": "pop3",
					"rechost": "pop.vip.sina.com",
					"recport": "110",
					"recisssl": "false",
					"sendprotocol": "smtp",
					"sendhost": "smtp.vip.sina.com",
					"sendport": "25",
					"sendisssl": "false"
				  },
				  {
					"name": "sohu.com",
					"recprotocol": "pop3",
					"rechost": "pop.sohu.com",
					"recport": "110",
					"recisssl": "false",
					"sendprotocol": "smtp",
					"sendhost": "smtp.sohu.com",
					"sendport": "25",
					"sendisssl": "false"
				  },
				  {
					"name": "yahoo.com.cn",
					"recprotocol": "pop3",
					"rechost": "pop.mail.yahoo.com.cn",
					"recport": "110",
					"recisssl": "false",
					"sendprotocol": "smtp",
					"sendhost": "smtp.mail.yahoo.com.cn",
					"sendport": "25",
					"sendisssl": "false"
				  },
				  {
					"name": "outlook.com",
					"recprotocol": "imap",
					"rechost": "imap-mail.outlook.com",
					"recport": "993",
					"recisssl": "true",
					"sendprotocol": "smtp",
					"sendhost": "smtp-mail.outlook.com",
					"sendport": "25",
					"sendisssl": "false"
				  },
				  {
					"name": "gmail.com",
					"recprotocol": "pop3",
					"rechost": "pop.gmail.com",
					"recport": "995",
					"recisssl": "true",
					"sendprotocol": "smtp",
					"sendhost": "smtp.gmail.com",
					"sendport": "465",
					"sendisssl": "true"
				  },
				  {
					"name": "hotmail.com",
					"recprotocol": "pop3",
					"rechost": "pop3.live.com",
					"recport": "995",
					"recisssl": "true",
					"sendprotocol": "smtp",
					"sendhost": "smtp.live.com",
					"sendport": "465",
					"sendisssl": "true"
				  },
				  {
					"name": "wo.cn",
					"recprotocol": "imap",
					"rechost": "imap.wo.cn",
					"recport": "143",
					"recisssl": "false",
					"sendprotocol": "smtp",
					"sendhost": "smtp.wo.cn",
					"sendport": "25",
					"sendisssl": "false"
				  },
				  {
					"name": "189.cn",
					"recprotocol": "imap",
					"rechost": "imap.189.cn",
					"recport": "143",
					"recisssl": "false",
					"sendprotocol": "smtp",
					"sendhost": "smtp.189.cn",
					"sendport": "25",
					"sendisssl": "false"
				  }];
		},
		computed:{
		},
		data(){
			return {
				account:{
					"account": "",
					"password": "",
					"recprotocol": "imap",
					"rechost": "",
					"recport": "143",
					"recisssl": "false",
					"sendprotocol": "smtp",
					"sendhost": "",
					"sendport": "25",
					"sendisssl": "false",
				},
				status:'create',
				recProtocols:[
				  {
					label: 'IMAP',
					value: 'imap',
				  },
				  {
					label: 'POP',
					value: 'pop3'
				  },
				],
			}
		},
		methods:{
			accountInputChange(){
				var _this = this;
				var data = _this.account.account;
				if(data.indexOf('@') != -1){
					var host = data.substring(data.indexOf('@')+1);

					if(this.account.recprotocol=='imap'){
						this.account.rechost = 'imap.'+ host;
						this.account.sendhost = 'smtp.'+ host;
					}else{
						this.account.rechost = 'imap.'+ host;
						this.account.sendhost = 'smtp.'+ host;
					}
					/* 常见互联网公司邮箱服务器 */
					this.servers.forEach(function(item, index){
						if(item.name==host){
							_this.account.recprotocol = item.recprotocol;
							_this.account.rechost = item.rechost;
							_this.account.recport = item.recport;
							_this.account.recisssl = item.recisssl=='true'?true:false;
							_this.account.sendprotocol = item.sendprotocol;
							_this.account.sendhost = item.sendhost;
							_this.account.sendport = item.sendport;
							_this.account.sendisssl = item.sendisssl=='true'?true:false;
						}
					});
				}
			},
			accountSendSSLChange(){
				this.account.sendisssl = !this.account.sendisssl;
				this.account.sendport = this.account.sendisssl?'465':'25';
			},
			accountRecSSLChange(){
				this.account.recisssl = !this.account.recisssl;
				this.account.recport = this.account.recisssl?(this.account.recprotocol=='imap'?'993':'995'):(this.account.recprotocol=='imap'?'143':'110');
			},
			accountRecProtocolChange(event){
				console.log(event.target.dataset.recprotocol)
				this.account.recprotocol = event.target.dataset.recprotocol;
				this.account.recport = this.account.recisssl?(this.account.recprotocol=='imap'?'993':'995'):(this.account.recprotocol=='imap'?'143':'110');
			},
			addAccount(){
				var _this = this;
				console.log(this.account)
				this.$axios.post(localStorage.getItem('base_url')+'/accounts', this.account,
				{headers:{'Content-Type': 'application/json','token': localStorage.token, 'type':'save'}}).then(function(res) {
					console.log(res.data)
					if(res.data.code == '200'){
						_this.$router.go(-1);
					}
				});
			},
			doModifyAccount(){
				var _this = this;
				this.$mui.confirm('确认修改账号？','修改',['取消','确认'],function (e) {
					if(e.index==1){
						_this.$axios.post(localStorage.getItem('base_url')+'/accounts', _this.account,
							{headers:{'Content-Type': 'application/json', 'token': localStorage.token, 'type':'update'}}).then(function(res) {
								console.log(res.data)
								if(res.data.code == '200'){
									mui.toast('修改账号成功',{ duration:'long', type:'div' });
									_this.$router.go(-1);
								}
								if(res.data.code == '202'){
									mui.toast('验证账号失败',{ duration:'long', type:'div' });
									_this.$router.go(-1);
								}
						});
					}
				},'div')
			},
		}
	}
</script>

<style scoped>
	.form-label{
		font-size: 0.8em;
		color: #888;
		height: 40px;
		line-height: 40px;
		padding: 0 10px;
		color:#84898a;
		border-bottom: 1px solid #eee;
		/* margin-top:15px; */
	}
	.form-padding{
		height:15px;
		background: #f8f8f8;
	}
</style>
