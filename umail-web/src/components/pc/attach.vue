<template>
	<div  class="ums-page">
		<div class="ums-header">
			<div>
				<span class="iconfont umail-zuojiantou-up return-row-padding" style="cursor: pointer;" @click="$router.go(-1)"></span>
				<span class="menu-padding" >附件管理</span>
			</div>
		</div>
		<div class="ums-container">
			<div class="ums-aside">
				<el-tree :data="accountTree" accordion :highlight-current="true" node-key="id" @node-click="handleTreeNodeClick"
						 @node-expand="handleTreeNodeExpand" @node-collapse="handleTreeNodeExpand" :default-expanded-keys="expandKey"></el-tree>
			</div>
			<div class="ums-main">
				 <el-table :data="messagesWithAchment" style="width: 100%;font-size: 0.7rem;"
				 		  :show-header="true" :highlight-current-row="true" :default-sort = "{prop: 'date', order: 'descending'}">
				  <el-table-column
				 	label="附件名"
					prop="name"
					sortable>
				 	<template slot-scope="scope">
						<el-tooltip effect="light" :content="scope.row.attachment[0].name" placement="top">
						  <span class="ums-ellipsis" >{{scope.row.attachment[0].name}}</span>
						</el-tooltip>
				 	</template>
				   </el-table-column>
				   <el-table-column
				 		label="所在邮件"
						prop="inMsg"
						sortable>
				 		<template slot-scope="scope">
							<el-tooltip effect="light" :content="scope.row.subject" placement="top">
							  <span class="ums-ellipsis" v-html="scope.row.subject"></span>
							</el-tooltip>
				 		</template>
				   </el-table-column>
				   </el-popover>
				   <el-table-column
				 		label="发件人"
				 		prop="from"
						sortable>
				 		<template slot-scope="scope">
							<el-tooltip effect="light" :content="scope.row.from[0]" placement="top">
							  <span  class="ums-ellipsis" >{{scope.row.from[0].substring(scope.row.from[0].indexOf('<')+1,scope.row.from[0].indexOf('>'))}}</span>
							</el-tooltip>
				 		</template>
				   </el-table-column>
				    <el-table-column
						label="发件日期"
						prop="date"
						sortable>
						<template slot-scope="scope">
							<span  class="ums-ellipsis" >{{  scope.row.date  }}</span>
						</template>
				   </el-table-column>
				   <el-table-column
						label="大小"
						prop="size">
						<template slot-scope="scope">
							<span  class="ums-ellipsis" >{{  scope.row.attachment[0].size  }}</span>
						</template>
				   </el-table-column>
				   <el-table-column
						label="操作"
						prop="operator">
						<template slot-scope="scope">
							<a class="attach-operator"  :href="base_url+'/download?attach='+scope.row.attachment[0].path">下载</a>
							<span>|</span>
							<span class="attach-operator" @click="forwardAttach" :data-attach="JSON.stringify(scope.row.attachment[0])">转发</span>
						</template>
				   </el-table-column>
				</el-table>
			</div>
		</div>
	</div>
</template>

<script>
	export default{
		name: 'welcome',
		created() {
		var _this = this;
		_this.base_url = localStorage.getItem('base_url');
			this.$axios({
			  method: 'get',
			  url: localStorage.getItem('base_url')+'/accounts',
			  headers:{'Content-Type': 'application/json','token':localStorage.token}
			}).then(function(res) {
				console.log(res.data);
				var _accounts =  res.data.data;
				var _accountNode = {
					id: -1,
					label: '',
					value: '',
					children: [{label: '收件箱',value: 'INBOX', children:[]},
							{label: '草稿箱',value: '草稿箱',children:[]},
							{label: '已发送',value: '已发送',children:[]},
							{label: '已删除',value: '已删除',children:[]},
							{label: '垃圾箱',value: '垃圾箱',children:[]}]
				};
				var _accountTree = new Array();
				_accounts.forEach(function(item, index){
					_accountNode.id = index;
					_accountNode.label = _accountNode.value = item.account;
					_accountTree.push(JSON.parse(JSON.stringify(_accountNode)));
				});
				// 导航账户
				_this.accounts = _accounts;
				// 树节点账户
				_this.accountTree = _accountTree;
				// 加载首页面
				if(sessionStorage.getItem("currentAttachment")){
					var _currentAttachmentStr = sessionStorage.getItem("currentAttachment");
					var _currentAttachment = JSON.parse(_currentAttachmentStr);
					_this.currentAccount = _currentAttachment.currentAccount;
					_this.currentBox = _currentAttachment.currentBox;
					// tree默认展开节点
					_this.expandKey =  new Array();
					_accounts.forEach(function(item, index){
						if(_this.currentAccount == item.account){
							_this.expandKey.push(index);
						}
					});
					_this.loadMessages();
				}else{
					_this.currentAccount = _accounts[0].account;
					_this.currentBox = 'INBOX';
					_this.loadInboxMessage();
				}
			});
		},
		data(){
			return {
				accounts:[],
				accountTree:[],
				currentAccount:'',
				currentBox:'',
				messages:[],
				expandKey:[0],
        base_url:'',
			}
		},
		mounted() {
		},
		computed:{
			messagesWithAchment:function(){
				var res = new Array();
				this.messages.forEach(function(item, index){
					if(item.attachment == 1){
						res.push(JSON.parse(JSON.stringify(item)));
					}else{
						item.attachment.forEach(function(attach, index){
							var newMsg = JSON.parse(JSON.stringify(item));
							newMsg.attachment = new Array();
							newMsg.attachment.push(attach);
							res.push(newMsg);
						});
					}
				});
				console.log(res)
				return res;
			},
		},
		methods:{
			loadInboxMessage(){
				var _this =this;
				this.$axios({
				  method: 'get',
				  url: localStorage.getItem('base_url')+'/messages?account='+this.currentAccount+'&box=INBOX',
				  headers:{'Content-Type': 'application/json','token':localStorage.token}
				}).then(function(res) {
					console.log(res.data)
					_this.messages = res.data.data;
				});
			},
			loadMessages(){
				var _this = this;
				this.$axios({
				  method: 'get',
				  url: localStorage.getItem('base_url')+'/messages?account='+_this.currentAccount+'&box='+_this.currentBox,
				  headers:{'Content-Type': 'application/json','token':localStorage.token}
				}).then(function(res) {
					console.log(res.data)
					_this.messages = res.data.data;
					_this.saveCurrentAttachment();
				});
			},
			saveCurrentAttachment(){
				var _this = this;
				var _currentAttachment = {};
				_currentAttachment.currentAccount = _this.currentAccount;
				_currentAttachment.currentBox = _this.currentBox;
				sessionStorage.setItem("currentAttachment", JSON.stringify(_currentAttachment));
			},
			downloadAttach(event){
				var _this = this;
				this.$axios({
				  method: 'get',
				  url: localStorage.getItem('base_url')+'/download?path='+event.target.dataset.path,
				  headers:{'Content-Type': 'application/json','token':localStorage.token}
				}).then(function(res) {
					console.log(res.data)
				});
			},
			forwardAttach(event){
				var _this = this;
				// 存入sessionStorage中
				_this.saveCurrentAttachment();

				var attachs = new Array();
				attachs.push(JSON.parse(event.target.dataset.attach));
				_this.$router.push({
				  name:'PCSend',
				  params:{

					  way:'reply',
					  type:'html',
					  msg: {
						  from:_this.currentAccount,
						  to:[],
						  cc:[],
						  bcc:[],
						  subject:attachs[0].name,
						  attachment:attachs,
						  content:[{
						  charset:'',
						  type:'',
						  text:'',
					  }]},
					  fileList:[],
					  accounts:_this.accounts,
				 }
				});
			},
			handleTreeNodeClick(data) {
				var _this = this;
				console.log(data)
				if(data.children.length == 0){
					this.currentBox = data.value;
					this.loadMessages();
				}
		    },
			handleTreeNodeExpand(data){
				this.currentAccount = data.label;
				this.messages = [];
			}
		}
	}
</script>

<style scoped>
	.attach-operator{
		text-decoration: none;
		color:#409EFF;
		cursor: pointer;
	}
</style>
