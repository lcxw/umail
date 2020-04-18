<template>
	<div  class="ums-page">
		<div class="ums-header">
			<div>
				<span class="iconfont umail-zuojiantou-up return-row-padding" style="cursor: pointer;" @click="$router.go(-1)"></span>
				<span class="menu-padding" >账号管理</span>
			</div>
		</div>
		<div class="ums-container">
			<div class="ums-aside" style="width:480px;">
				<el-button @click="newAccount"
					style="width:100%;text-align: center;border: none;border-bottom: 1px solid #EBEEF5;color:#84898a;"
					v-if="accounts.length==0">
					<span class="iconfont umail-hao menu-padding"></span>新建账号
				</el-button>
				<el-table :data="accounts" :show-header="false">
				  <el-table-column
						label="账号"
						prop="account">
						<template slot-scope="scope">
							<el-tooltip effect="light" :content="scope.row.account" placement="top">
							  <span class="ums-ellipsis" >{{scope.row.account}}</span>
							</el-tooltip>
						</template>
				  </el-table-column>
					  <el-table-column
						  fixed="right"
						  label="操作"
						  width="50">
						  <template slot-scope="scope">
							<el-button @click="newAccount" type="text" size="small">新建</el-button>
						  </template>
					  </el-table-column>
					  <el-table-column
						  fixed="right"
						  label="操作"
						  width="50">
						  <template slot-scope="scope">
							<el-button @click="modifyAccount(scope.row)" type="text" size="small">编辑</el-button>
						  </template>
					  </el-table-column>
					  <el-table-column
						  fixed="right"
						  label="操作"
						  width="50">
						  <template slot-scope="scope">
							<el-button @click="deleteAccount(scope.row)" type="text" size="small">删除</el-button>
						  </template>
					  </el-table-column>
				</el-table>
			</div>
			<div class="ums-main">
				<!--修改状态-->
				<div v-if="status=='update'">
					<el-tabs v-model="activeName" type="border-card" style="min-height: 500px;">
						<el-tab-pane label="账号" name="first">
							<el-form ref="form" :model="account" class="account-form"
								label-width="120px">
							  <el-form-item label="Email地址:">
								<el-input v-model="account.account" size="small" style="width:400px"></el-input>
							  </el-form-item>
							  <el-form-item label="密码:">
								<el-input v-model="account.password" size="small" show-password  style="width:400px"></el-input>
							  </el-form-item>
							  <el-form-item label="发信名称:">
								<el-input v-model="account.alias" size="small" style="width:400px"></el-input>
							  </el-form-item>
							</el-form>
						</el-tab-pane>
						<el-tab-pane label="服务器" name="second">
							<el-form ref="form" :model="account" class="account-form"
								label-width="120px">
							  <el-form-item label="邮箱类型:">
								  <span style="color: #606266;">{{account.recprotocol.toLocaleUpperCase()}}</span>
							  </el-form-item>
							  <el-form-item label="收件服务器:">
								<el-input v-model="account.rechost" size="small"  style="width:200px"></el-input>
								<el-checkbox v-model="account.recisssl"  @change="accountRecSSLChange" style="margin-left:6px;margin-right:6px;color: #606266;">SSL</el-checkbox>
								<span style="color: #606266;">端口：</span><el-input v-model="account.recport" size="small"  style="width:70px"></el-input>
							  </el-form-item>
							  <el-form-item label="发件服务器:">
								<el-input v-model="account.sendhost" size="small" style="width:200px"></el-input>
								<el-checkbox v-model="account.sendisssl" @change="accountSendSSLChange" style="margin-left:6px;margin-right:6px;color: #606266;">SSL</el-checkbox>
								<span style="color: #606266;">端口：</span><el-input v-model="account.sendport" size="small"  style="width:70px"></el-input>
							  </el-form-item>
							</el-form>
						</el-tab-pane>
						<el-tab-pane label="设置" name="third">
							<el-form ref="form" :model="account" class="account-form"
								label-width="120px">
								<el-form-item label="定时收取:">
									 <el-checkbox v-model="account.isscheduled" @change="accountIsScheduledChange" style="margin-left:6px;margin-right:6px;">定时收取邮件</el-checkbox>
								</el-form-item>
								 <el-form-item label="时间间隔:">
									<el-input v-model="account.scheduledperiod" size="small" :disabled="!account.isscheduled" style="width:60px"></el-input>
									<span style="color: #606266;">分钟</span>
								 </el-form-item>
							</el-form>
						</el-tab-pane>
					</el-tabs>
					<div style="position:absolute;right:40px;bottom:40px;" >
						<el-button type="primary"  size="small" style="width:100px;"
						:disabled="account.password.trim()==''||!/[A-Za-z0-9\u4e00-\u9fa5]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+/.test(account.account)"
						@click="doModifyAccount">确定</el-button>
						<el-button  size="small" style="width:100px;" @click="$router.go(-1)">取消</el-button>
						<!-- <el-button  size="small" style="width:100px;" @click="status='init'">取消</el-button> -->
						<!-- <el-button type="primary"  size="small" style="width:80px;" @click="submitSign">保存</el-button> -->
						<!-- <el-button  size="small" style="width:80px;" @click="$router.go(-1)">取消</el-button> -->
					</div>
				</div>

				<!--新建状态-->
				<el-form :model="newaccount" v-if="status=='create'" class="newaccount-form"
					label-width="120px">
				 <el-form-item label="接收服务器类型:">
					<el-select v-model="newaccount.recprotocol" size="small" style="width:200px" @change="newAccountRecSSLChange">
						<el-option
							v-for="protocol in recProtocols"
							:key="protocol"
							:label="protocol.toLocaleUpperCase()"
							:value="protocol">
						</el-option>
					</el-select>
				 </el-form-item>
				 <el-form-item label="邮件账号:">
					<el-input v-model="newaccount.account" @change="accountInputChange" size="small" style="width:200px" ></el-input>
				 </el-form-item>
				 <el-form-item label="密码:">
					<el-input v-model="newaccount.password" size="small" show-password  style="width:200px"></el-input>
				 </el-form-item>
				 <el-form-item :label="newaccount.recprotocol.toLocaleUpperCase().concat('服务器:')">
					<el-input v-model="newaccount.rechost" size="small"  style="width:200px"></el-input>
					<el-checkbox v-model="newaccount.recisssl" @change="newAccountRecSSLChange"
					style="margin-left:6px;margin-right:6px;color: #606266;">SSL</el-checkbox>
					<span style="color: #606266;">端口：</span><el-input v-model="newaccount.recport" size="small"  style="width:70px"></el-input>
				 </el-form-item>
				 <el-form-item label="SMTP服务器:">
					<el-input v-model="newaccount.sendhost" size="small" style="width:200px"></el-input>
					<el-checkbox v-model="newaccount.sendisssl" @change="newAccountSendSSLChange"
					style="margin-left:6px;margin-right:6px;color: #606266;">SSL</el-checkbox>
					<span style="color: #606266;">端口：</span><el-input v-model="newaccount.sendport" size="small"  style="width:70px"></el-input>
				 </el-form-item>
				 <el-form-item>
					<el-button type="primary"  size="small" style="width:100px;"
					 :disabled="newaccount.password.trim()==''||!/[A-Za-z0-9\u4e00-\u9fa5]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+/.test(newaccount.account)"
					 @click="doNewAccount">创建</el-button>
					 <!-- <el-button  size="small" style="width:100px;" @click="status='init'">取消</el-button> -->
					<el-button  size="small" style="width:100px;" @click="$router.go(-1)">取消</el-button>
				 </el-form-item>
				</el-form>
			</div>
		</div>
	</div>
</template>

<script>
	export default {
		name: 'account',
		created() {
			this.loadAccount();
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
			console.log(this.servers)
		},
		computed:{
		},
		data(){
			return {
				status:'',
				recProtocols:['imap', 'pop3'],
				accounts:[],
				account:{},
				newaccount:{
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
				syncLoadingVisibled:false,
				activeName:'first',
			}
		},
		methods:{
			loadAccount(){
				var _this = this;
				this.$axios({
				  method: 'get',
				  url: localStorage.getItem('base_url')+'/accounts',
				  headers:{'Content-Type': 'application/json','token':localStorage.token}
				}).then(function(res) {
					console.log(res.data);
					var _accounts =  res.data.data;
					_this.accounts = _accounts;
				});
			},
			newAccount(){
				this.status = 'create';
				this.newaccount = JSON.parse(JSON.stringify(this.newaccount));
			},
			doNewAccount(){
				var _this = this;
				this.$axios.post(localStorage.getItem('base_url')+'/accounts', _this.newaccount,
				{headers:{'Content-Type': 'application/json', 'token': localStorage.token, 'type':'save'}}).then(function(res) {
					console.log(res.data)
					if(res.data.code == '200'){

						_this.$message({
							type: 'success',
							message: '添加账号成功!',
							duration:1000
						});
						_this.loadAccount();
						_this.status='init';
					}
					if(res.data.code == '202'){
						_this.$message({
							type: 'error',
							message: '验证账号失败，请检查账号是否正确!',
							duration:2000
						});
					}
					if(res.data.code == '203'){
						_this.$message({
							type: 'error',
							message: '您的账号密码可能已经泄露，请尽快修改!',
							duration:2000
						});
					}
				});
			},
			modifyAccount(row){
				console.log(row)
				this.status = "update";
				this.account = JSON.parse(JSON.stringify(row));
				this.account.recisssl = row.recisssl=='true'?true:false;
				this.account.sendisssl = row.sendisssl=='true'?true:false;
				this.account.isscheduled = row.isscheduled=='true'?true:false;
			},
			doModifyAccount(){
				var _this = this;
				this.$confirm('您确认要修改'+_this.account.account+'吗？', '确认', {
				  confirmButtonText: '确定',
				  cancelButtonText: '取消',
				  type: 'warning'
				}).then(() => {
					_this.$axios.post(localStorage.getItem('base_url')+'/accounts', _this.account,
					{headers:{'Content-Type': 'application/json', 'token': localStorage.token, 'type':'update'}}).then(function(res) {
						console.log(res.data)
						if(res.data.code == '200'){
							_this.$message({
								type: 'success',
								message: '修改账号成功!',
								duration:1000
							});
							_this.loadAccount();
							_this.status='init';
						}
						if(res.data.code == '202'){
							_this.$message({
								type: 'error',
								message: '验证账号失败，请检查账号是否正确!',
								duration:2000
							});
						}
					});
				}).catch(() => {
				});
			},
			deleteAccount(row){
				var _this = this;
				this.$confirm('您确认要永久删除'+row.account+'吗？', '确认', {
				  confirmButtonText: '确定',
				  cancelButtonText: '取消',
				  type: 'warning'
				}).then(() => {
					_this.$axios.post(localStorage.getItem('base_url')+'/accounts', row,
					{headers:{'Content-Type': 'application/json', 'token': localStorage.token, 'type':'delete'}}).then(function(res) {
						console.log(res.data)
						if(res.data.code == '200'){
							_this.$message({
								type: 'success',
								message: '删除账号成功!',
								duration:1000
							});
							_this.loadAccount();
							_this.status='init';
						}
					});
				}).catch(() => {
				});
			},
			addAccount(){
				var _this = this;
				this.$axios.post(localStorage.getItem('base_url')+'/accounts', this.account,
				{headers:{'Content-Type': 'application/json','token': localStorage.token, 'type':'save'}}).then(function(res) {
					console.log(res.data)
					if(res.data.code == '200'){
						_this.$message({
							type: 'success',
							message: '创建成功!'
						});
					}
				});
			},
			accountSendSSLChange(data){
				console.log("send"+data)
				this.account.sendport = data?'465':'25';
			},
			accountRecSSLChange(data){
				console.log("rec"+data)
				this.account.recport = data?(this.account.recprotocol=='imap'?'993':'995'):(this.account.recprotocol=='imap'?'143':'110');
			},
			accountIsScheduledChange(data){

			},
			newAccountSendSSLChange(data){
				console.log("send"+data)
				this.newaccount.sendport = data?'465':'25';
			},
			newAccountRecSSLChange(data){
				console.log("rec"+data)
				this.newaccount.recport = data?(this.newaccount.recprotocol=='imap'?'993':'995'):(this.newaccount.recprotocol=='imap'?'143':'110');
			},
			accountInputChange(data){
				var _this = this;
				console.log(data)
				if(data.indexOf('@') != -1){
					var host = data.substring(data.indexOf('@')+1);

					if(this.newaccount.recprotocol=='imap'){
						this.newaccount.rechost = 'imap.'+ host;
						this.newaccount.sendhost = 'smtp.'+ host;
					}else{
						this.newaccount.rechost = 'imap.'+ host;
						this.newaccount.sendhost = 'smtp.'+ host;
					}
					/* 常见互联网公司邮箱服务器 */
					this.servers.forEach(function(item, index){
						if(item.name==host){
							_this.newaccount.recprotocol = item.recprotocol;
							_this.newaccount.rechost = item.rechost;
							_this.newaccount.recport = item.recport;
							_this.newaccount.recisssl = item.recisssl=='true'?true:false;
							_this.newaccount.sendprotocol = item.sendprotocol;
							_this.newaccount.sendhost = item.sendhost;
							_this.newaccount.sendport = item.sendport;
							_this.newaccount.sendisssl = item.sendisssl=='true'?true:false;
						}
					});
				}
			}
		}
	}
</script>

<style>
	.account-form{
		margin:10px auto;
		/* width:340px; */
		width:560px;
		/* box-shadow: 0 2px 12px 0 rgba(0,0,0,.1); */
		padding: 10px;
	}
	.newaccount-form{
		margin:10px auto;
		/* width:340px; */
		width:560px;
		box-shadow: 0 2px 12px 0 rgba(0,0,0,.1);
		padding: 10px;
	}
</style>
