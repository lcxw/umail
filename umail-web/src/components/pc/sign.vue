<template>
	<div  class="ums-page">
		<div class="ums-header">
			<div>
				<span class="iconfont umail-zuojiantou-up return-row-padding" style="cursor: pointer;" @click="$router.go(-1)"></span>
				<span class="menu-padding" >签名管理</span>
			</div>
		</div>
		<div class="ums-container">
			<div class="ums-aside" style="width:400px;">
				<el-table :data="signs" :show-header="false">
				  <el-table-column
						label="账号"
						prop="account">
						<template slot-scope="scope">
							<el-tooltip effect="light" :content="scope.row.name" placement="top">
							  <span class="ums-ellipsis" >{{scope.row.name}}</span>
							</el-tooltip>
						</template>
				  </el-table-column>
				   <el-table-column
					  fixed="right"
					  label="操作"
					  width="50">
					  <template slot-scope="scope">
						<el-button @click="newSign" type="text" size="small">新建</el-button>
					  </template>
				  </el-table-column>
				  <el-table-column
					  fixed="right"
					  label="操作"
					  width="50">
					  <template slot-scope="scope">
						<el-button @click="modifySign(scope.row)" type="text" size="small">编辑</el-button>
					  </template>
				  </el-table-column>
				  <el-table-column
					  fixed="right"
					  label="操作"
					  width="50">
					  <template slot-scope="scope">
						<el-button @click="deleteSign(scope.row)" type="text" size="small" :disabled="accountNames.indexOf(scope.row.name)!==-1">删除</el-button>
					  </template>
				  </el-table-column>
				</el-table>
			</div>
			<div class="ums-main">
				<quill-editor v-if="sign != null" v-model="sign.content" style="height:360px;"
							  ref="myQuillEditor" @change="onEditorChange($event)"
							  :options="editorOption">
				</quill-editor>

				<el-row :gutter="2"  type="flex" align="middle" style="margin-top:80px;">
				  <el-col :span="2"><div>账号：</div></el-col>
				  <el-col :span="6"><div>
					  <el-select v-model="accountSign.account" size="small" @change="accountChange">
						<el-option
							v-for="acc in accounts"
							:key="acc"
							:label="acc"
							:value="acc">
						</el-option>
					  </el-select>
				  </div></el-col>
				  <el-col :span="2"><div>新邮件：</div></el-col>
				  <el-col :span="6"><div>
					  <el-select v-model="accountSign.sendsign" size="small" @change="sendsignChange">
						<el-option
							v-for="sign in signs"
							:key="sign.name"
							:label="sign.name"
							:value="sign.name">
						</el-option>
					  </el-select>
				  </div></el-col>
				  <el-col :span="3"><div>回复/转发：</div></el-col>
				  <el-col :span="5"><div>
					  <el-select v-model="accountSign.replysign" size="small" @change="replysignChange">
						<el-option
							v-for="sign in signs"
							:key="sign.name"
							:label="sign.name"
							:value="sign.name">
						</el-option>
					  </el-select>
				  </div></el-col>
				</el-row>

				<div style="position:absolute;right:40px;bottom:40px;" >
					<el-button type="primary"  size="small" style="width:80px;" @click="submitSign">保存</el-button>
					<el-button  size="small" style="width:80px;" @click="$router.go(-1)">取消</el-button>
				</div>
			</div>
		</div>
	</div>
</template>

<script>
	import quillConfig from '../../quill-config.js'
	export default {
		name: 'account',
		created() {
			var _this = this;
			this.$axios({
			  method: 'get',
			  url: localStorage.getItem('base_url')+'/signs',
			  headers:{'Content-Type': 'application/json','token':localStorage.token}
			}).then(function(res) {
				console.log(res.data)
				var _signs =  res.data.data;
				_this.signs = _signs;
			});

			this.$axios({
			  method: 'get',
			  url: localStorage.getItem('base_url')+'/accountSigns',
			  headers:{'Content-Type': 'application/json','token':localStorage.token}
			}).then(function(res) {
				console.log(res.data)
				_this.accountSigns =  res.data.data;
				if(_this.accountSigns.length >0){
					_this.accountSign = JSON.parse(JSON.stringify(_this.accountSigns[0]));
					var _accounts = new Array();
					for(var i in _this.accountSigns){
						_accounts.push(_this.accountSigns[i].account);
					}
					_this.accounts = _accounts;
				}
			});
		},
		mounted() {
			quillConfig.initButton();
		},
		computed:{
			accountNames:function(){
				var _this = this;
				var _accountName = new Array();
				this.accounts.forEach(function(item, index){
					_accountName.push(item.substring(0, item.indexOf('@')));
				});
				return _accountName;
			}
		},
		data(){
			return {
				editorOption:quillConfig,
				accounts:[],
				signs:[],
				sign:{
					name:'',
					content:'',
					userId:''
				},
				accountSigns:[],
				accountSign:{}
			}
		},
		methods:{
			loadInboxAttach(){
				var _this =this;
				this.$axios({
				  method: 'get',
				  url: localStorage.getItem('base_url')+'/messages?account='+this.currentAccount+'&box=INBOX',
				  headers:{'Content-Type': 'application/json','token':localStorage.token}
				}).then(function(res) {
					console.log(res.data)
					_this.messages = res.data.data;
					_this.currentMessageNum = -1;
				});
			},
			newSign(){
				var _this = this;
				this.$prompt('名称', '设置签名名称', {
				  confirmButtonText: '确定',
				  cancelButtonText: '取消',
				}).then(({ value }) => {
					if(value.trim() == ''){
						_this.$message({type: 'error',message: '签名为空'});
						return;
					}
					var _sign = {
						name:value,
						content:'<p><br/></p><p><br/></p>----------------------------<br/><p>'+value+'<br/></p>',
					};
					var exist = false;
					_this.signs.forEach(function(item, index){
						if(_sign.name == item.name){
							exist= true;
						}
					});
					if(exist){
						_this.$message({type: 'error',message: '该签名已存在'});
					}else{
						_this.$axios.post(localStorage.getItem('base_url')+'/signs', _sign,
						{headers:{'Content-Type': 'application/json', 'token': localStorage.token, 'type':'save'}}).then(function(res) {
							console.log(res.data)
							if(res.data.code == '200'){
								_this.$message({
									type: 'success',
									message: '添加签名成功!',
									duration:1000
								});
								_this.loadSign();
								_this.loadAccountSign();
							}
						});
					}
				}).catch(() => {});
				this.sign = {
					name:'',
					content:'',
					userId:''
				};
			},
			loadSign(){
				var _this = this;
				var config = {
				  method: 'get',
				  url: localStorage.getItem('base_url')+'/signs',
				  headers:{'Content-Type': 'application/json',
				  'token':localStorage.token
				  }
				};
				// Send a POST request
				this.$axios(config).then(function(res) {
					console.log(res.data)
					_this.signs =  res.data.data;
				});
			},
			loadAccountSign(){
				var _this = this;
				this.$axios({
				  method: 'get',
				  url: localStorage.getItem('base_url')+'/accountSigns',
				  headers:{'Content-Type': 'application/json','token':localStorage.token}
				}).then(function(res) {
					console.log(res.data)
					_this.accountSigns =  res.data.data;
					if(_this.accountSigns.length >0){
						_this.accountSign = JSON.parse(JSON.stringify(_this.accountSigns[0]));
						var _accounts = new Array();
						_this.accountSigns.forEach(function(item, index){
							_accounts.push(item.account);
						});
						_this.accounts = _accounts;
					}
				});
			},
			modifySign(row){
				this.sign = row;
			},
			deleteSign(row){
				var _this = this;
				this.$confirm('您确认要永久删除签名'+row.name+'吗？', '确认', {
				  confirmButtonText: '确定',
				  cancelButtonText: '取消',
				  type: 'warning'
				}).then(() => {
					_this.$axios.post(localStorage.getItem('base_url')+'/signs', row,
					{headers:{'Content-Type': 'application/json', 'token': localStorage.token, 'type':'delete'}}).then(function(res) {
						console.log(res.data)
						if(res.data.code == '200'){
							_this.$message({
								type: 'success',
								message: '删除签名成功!',
								duration:1000
							});
							_this.loadSign();
							_this.loadAccountSign();
						}
					});
				}).catch(() => {
				});
			},
			accountChange(value){
				var _this = this;
				_this.accountSigns.forEach(function(item, index){
					if(item.account == value){
						_this.accountSign = JSON.parse(JSON.stringify(item));
					}
				});
			},
			sendsignChange(value){
				var _this = this;
				_this.accountSigns.forEach(function(item, index){
					if(item.account == _this.accountSign.account){
						item.sendsign = value;
					}
				});
				console.log(this.accountSigns)
			},
			replysignChange(value){
				var _this = this;
				_this.accountSigns.forEach(function(item, index){
					if(item.account == _this.accountSign.account){
						item.replysign = value;
					}
				});
			},
			onEditorChange(){//内容改变事件
				var _this = this;
				_this.signs.forEach(function(item, index){
					if(item.name == _this.sign.name){
						item.content = _this.sign.content;
					}
				});
				console.log(this.signs)
			},
			submitSign(){
				var _this = this;
				var _sign = {
					signs: this.signs,
					accountSigns: this.accountSigns
				};
				this.$axios({
				  method: 'post',
				  url: localStorage.getItem('base_url')+'/accountSigns',
				  data:{
					raw: JSON.stringify(_sign)
				  },
				  headers:{'Content-Type': 'application/json','token':localStorage.token}
				}).then(function(res) {
					console.log(res.data)
					if(res.data.code == '200'){
						_this.$message({
							type: 'success',
							message: '修改签名成功!',
							duration:1000
						});
						_this.$router.go(-1);
					}
				});
			},
		}
	}
</script>

<style scoped>
</style>
