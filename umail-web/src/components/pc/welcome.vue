<template>
	<div class="ums-page">
		<div ref="header" class="ums-header">
			<div style="width:74%">
			<el-menu mode="horizontal"
			    @select="handleSelect"  background-color="#545c64"
				text-color="#fff" active-text-color="#ffd04b">
			  <el-submenu index="1">
				<template slot="title"><span class="iconfont umail-receive-mail menu-padding"></span>收取</template>
				<!-- <el-menu-item index="1-1">收取所有账号</el-menu-item> -->
				<el-menu-item v-for="(acc, index) in accounts" :index="acc.account">{{acc.account}}</el-menu-item>
			  </el-submenu>
			   <el-submenu index="2">
					<template slot="title"><span class="iconfont umail-write-mail menu-padding"></span>写邮件</template>
					<!-- <el-menu-item index="2-1">不使用模板</el-menu-item> -->
					<el-menu-item index="2-2">HTML邮件</el-menu-item>
					<el-menu-item index="2-3">纯文本邮件</el-menu-item>
					<!-- <el-menu-item index="2-4">模板管理</el-menu-item> -->
			  </el-submenu>
			   <el-submenu index="3" :disabled="currentMessageNum==-1">
					<template slot="title"><span class="iconfont umail-reply-mail menu-padding"></span>回复</template>
					<!-- <el-menu-item index="3-1">不使用模板</el-menu-item> -->
					<el-menu-item index="3-2">HTML邮件</el-menu-item>
					<el-menu-item index="3-3">纯文本邮件</el-menu-item>
					<!-- <el-menu-item index="3-4">模板管理</el-menu-item> -->
			  </el-submenu>
			   <el-submenu index="4" :disabled="currentMessageNum==-1">
					<template slot="title"><span class="iconfont umail-replyall-mail menu-padding"></span>回复全部</template>
					<!-- <el-menu-item index="4-1">不使用模板</el-menu-item> -->
					<el-menu-item index="4-2">HTML邮件</el-menu-item>
					<el-menu-item index="4-3">纯文本邮件</el-menu-item>
					<!-- <el-menu-item index="4-4">模板管理</el-menu-item> -->
			  </el-submenu>
			  <el-menu-item index="5" :disabled="currentMessageNum==-1"><span class="iconfont umail-froward-mail  menu-padding"></span>转发</el-menu-item>
			 <!--  <el-submenu index="5" :disabled="currentMessageNum==-1">
					<template slot="title"><span class="iconfont umail-froward-mail menu-padding"></span>转发</template>
					<el-menu-item index="5-1">不使用模板</el-menu-item>
					<el-menu-item index="5-2">HTML邮件</el-menu-item>
					<el-menu-item index="5-3">纯文本邮件</el-menu-item>
					<el-menu-item index="5-4">模板管理</el-menu-item>
			  </el-submenu> -->
			  <el-menu-item index="6" :disabled="currentMessageNum==-1"><span class="iconfont umail-delete-mail  menu-padding"></span>删除</el-menu-item>
			  <el-submenu index="7">
					<template slot="title"><span class="iconfont umail-shezhi menu-padding"></span>管理</template>
					<el-menu-item index="7-1"><span class="iconfont umail-attachment-manage submenu-padding"></span>附件管理</el-menu-item>
					<el-menu-item index="7-2"><span class="iconfont umail-sign-manager submenu-padding"></span>签名管理</el-menu-item>
					<!-- <el-menu-item index="7-3"><span class="iconfont umail-template-manager submenu-padding"></span>模板管理</el-menu-item> -->
					<el-menu-item index="7-4"><span class="iconfont umail-contact-manager submenu-padding"></span>联系人管理</el-menu-item>
					<el-menu-item index="7-5"><span class="iconfont umail-account-manager submenu-padding"></span>账号管理</el-menu-item>
					<el-menu-item index="7-6"><span class="iconfont umail-logout submenu-padding"></span>退出</el-menu-item>
			  </el-submenu>
			  <!-- <el-menu-item index="menu.index" v-for="menu in menues"><span :class="menu.icon"></span>{{menu.name}}</el-menu-item> -->
			</el-menu>
			</div>
			<div style="display: flex; flex-direction: column;">
				<el-input placeholder="邮件搜索" v-model="searchInput" clearable @change="searchMessages">
					<i slot="prefix" class="el-input__icon el-icon-search"></i>
				</el-input>

			</div>
		</div>
    <!--搜索提示-->
    <div class="search-items" :style="{'display':searchInput.trim()!=''?'flex':'none'}">
      <div class="ums-ellipsis search-item" @click="searchMsgByOrigin" data-origin="from">发件人：{{searchInput}}</div>
      <div class="ums-ellipsis search-item" @click="searchMsgByOrigin" data-origin="to">收件人：{{searchInput}}</div>
      <div class="ums-ellipsis search-item" @click="searchMsgByOrigin" data-origin="subject">主题：{{searchInput}}</div>
      <div class="ums-ellipsis search-item" @click="searchMsgByOrigin" data-origin="all">全文：{{searchInput}}</div>
      <div class="ums-ellipsis search-item" @click="searchMsgByOrigin" data-origin="attachment">附件名：{{searchInput}}</div>
    </div>
		<!-- <div class="el-loading-mask" style="background-color: rgba(0, 0, 0, 0.8);"><div class="el-loading-spinner"><i class="el-icon-loading"></i><p class="el-loading-text">拼命加载中</p></div></div> -->
		<ums-loading msg="正在同步邮件" :value="currentAccount" :visible="syncLoadingVisibled"></ums-loading>
		<ums-loading msg="正在删除邮件" value="" :visible="syncDeletingVisibled"></ums-loading>
		<div class="ums-container">
			<div class="ums-aside"  style="width:250px;">
				<el-tree :data="accountTree" accordion :highlight-current="true" node-key="id" @node-click="handleTreeNodeClick" ref="tree"
						 @node-expand="handleTreeNodeExpand" @node-collapse="handleTreeNodeExpand"  :default-expanded-keys="expandKey" ></el-tree>
			</div>

			<div class="ums-container">
				<div class="ums-aside" style="width:300px;">
					<el-table :data="messages" :height="620" @row-click="handleTableColumnClick" class="abstract-message"
								  :show-header="true" :highlight-current-row="true" :default-sort = "{prop: 'date', order: 'descending'}">
						   <el-table-column
								width="30">
								<template slot-scope="scope">
									<span class="iconfont umail-yuandian-copy" style="color:#409EFF;font-size:30px;line-height: 1;" v-if="scope.row.new==true"></span>
									<span class="iconfont umail-yuandian-copy" style="color:#c2c2c2;font-size:30px;line-height: 1;" v-else></span>
								</template>
						   </el-table-column>
						  <el-table-column
								label="主题"
								width="150"
								sortable>
								<template slot-scope="scope">
                  <span class="ums-ellipsis" v-html="scope.row.subject "></span>
									<!--<el-tooltip effect="light" :content="scope.row.subject" placement="top">-->
									 <!--<span class="ums-ellipsis" v-html="scope.row.subject "></span>-->
									<!--</el-tooltip>-->
								</template>
						  </el-table-column>
						  <el-table-column
								label="日期"
								prop="date"
								width="100"
								sortable>
								<template slot-scope="scope">
									<span>{{  scope.row.date  }}</span>
								</template>
						  </el-table-column>
						</el-table>
				</div>
				<div class="ums-main">
					<div v-if="currentMessageNum!=-1  && messages.length!=0">
						<!--添加联系人对话框设置-->
						<el-dialog title="联系人" :visible.sync="dialogFormVisible" width="400px">
							<el-form :model="newcontact" class="contact-form"
								label-width="80px">
								 <el-form-item label="姓名:">
									<el-input v-model="newcontact.name" size="small" style="width:220px" ></el-input>
								 </el-form-item>
								 <el-form-item label="邮箱:">
									<el-input v-model="newcontact.email" :disabled="true" size="small" style="width:220px"></el-input>
								 </el-form-item>
								 <el-form-item label="移动电话:">
									<el-input v-model="newcontact.movephone" size="small"   type="number" style="width:220px"></el-input>
								 </el-form-item>
								 <el-form-item label="公司:">
									<el-input v-model="newcontact.company" size="small" style="width:220px"></el-input>
								 </el-form-item>
								 <el-form-item label="工作电话:">
									<el-input v-model="newcontact.workphone" size="small"  type="number" style="width:220px"></el-input>
								 </el-form-item>
								 <el-form-item label="备注:">
									<el-input v-model="newcontact.remark" size="small" type="textarea" style="width:220px"></el-input>
								 </el-form-item>
							 </el-form>
							<div slot="footer" class="dialog-footer">
								<el-button type="primary"  size="small" style="width:100px;"
								:disabled="newcontact.name.trim()==''||!/[A-Za-z0-9\u4e00-\u9fa5]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+/.test(newcontact.email)"
								@click="doNewContact">创建</el-button>
								<el-button  size="small" style="width:100px;" @click="hideContactDialog">取消</el-button>
							 </div>
						</el-dialog>
						<!--邮件头部-->
						<div class="message-header">
							<p class="message-header-subject" v-html="messages[currentMessageNum].subject"></p>
							<p class="message-header-item">发件人：
								<el-popover
								  placement="bottom"
								  width="300"
								  trigger="hover" v-for="address in messages[currentMessageNum].from">
									<div style="cursor:pointer;">
										<el-row :gutter="20"  type="flex" align="middle" style="margin-top:20px;">
										  <el-col :span="24">
											<span style="font-size: 1.5em;font-weight: bold;">{{address.substring(0, address.indexOf('<'))}}</span>
										  </el-col>
										</el-row>
										<el-row :gutter="20"  type="flex" align="middle">
										  <el-col :span="24">
											<span style="fong-size:0.3em;color:#999999;">{{address.substring(address.indexOf('<')+1, address.indexOf('>'))}}</span>
										  </el-col>
										</el-row>
										<el-row :gutter="0"  type="flex" align="middle"  style="margin-top:20px;">
										  <el-col :span="12">
											<div style="text-align: center;color:#409EFF;border-top:1px solid #e0e0e0;border-right: 1px solid #e0e0e0;padding-top:10px;">
												<span @click="showContactDialog" :data-address="address" v-if="contactEmails.indexOf(address.substring(address.indexOf('<')+1, address.indexOf('>')))==-1">添加到联系人</span>
												<span v-else style="color:#e0e0e0;">添加到联系人</span>
											</div>
										  </el-col>
										  <el-col :span="12">
											<div style="text-align: center;color:#409EFF;border-top:1px solid #e0e0e0;padding-top:10px;">
												<span @click="searchMsgByAddress" :data-address="address.substring(address.indexOf('<')+1, address.indexOf('>'))">往来邮件</span>
											</div>
										  </el-col>
										</el-row>
									</div>
									<span class="message-header-address" slot="reference">{{address}}</span>
								</el-popover>
							</p>
							<p class="message-header-item">收件人：<span class="message-header-address" v-for="address in messages[currentMessageNum].to">{{address}}</span>
							</p>
							<p v-if="messages[currentMessageNum].cc.length != 0" class="message-header-item">
								&nbsp;&nbsp;&nbsp;&nbsp;抄送：<span v-for="address in messages[currentMessageNum].cc"
								 class="message-header-address">{{address}}</span>
							</p>
							<p v-if="messages[currentMessageNum].bcc.length != 0" class="message-header-item">
								&nbsp;&nbsp;&nbsp;&nbsp;密送：<span v-for="address in messages[currentMessageNum].bcc"
								 class="message-header-address">{{address}}</span>
							</p>
							<p class="message-header-item">  &nbsp;&nbsp;&nbsp;&nbsp;时间：{{messages[currentMessageNum].date}}</p>
						</div>
						<hr>
						<!--邮件附件-->
						<div class="message-header-attachs">
							<div class="message-header-attach" v-for="attach in attachmentWithUrls">
								<a class="attach" :href="attach.url"><i class="el-icon-document menu-padding"></i>{{attach.name}}</a>
							</div>
						</div>
						<hr v-if="attachmentWithUrls.length != 0">
						<div ref="content" v-html="content" style="text-align: left;min-width:500px;"></div>
					</div>
					<div v-else style="font-size:40px;color:#84898a;text-align: center;">{{currentAccount}}</div>
				</div>
			</div>
		</div>
	</div>
</template>

<script>
	/* 引入自定义组件*/
	import loading from './loading.vue'
	export default{
		name: 'welcome',
		components:{ 'ums-loading':loading },
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
				/* 如果不存在账号则跳转到账号新增页面*/
				if(_accounts.length==0){
					_this.$router.push({
						name:'PCAccount',
						params:{
					    }
					});
				}else{
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
					if(sessionStorage.getItem("currentMessage")){
						var _currentMessageStr = sessionStorage.getItem("currentMessage");
						var _currentMessage = JSON.parse(_currentMessageStr);
						_this.currentAccount = _currentMessage.currentAccount;
						_this.currentBox = _currentMessage.currentBox;
						// tree默认展开节点
						_this.expandKey =  new Array();
						_accounts.forEach(function(item, index){
							if(_this.currentAccount == item.account){
								_this.expandKey.push(index);
							}
						});
						_this.currentMessageNum = _currentMessage.currentMessageNum;
						_this.loadMessages();
						_this.loadContact();
					}else{
						_this.currentAccount = _accounts[0].account;
						_this.currentBox = 'INBOX';
						_this.currentMessageNum = -1;
						_this.loadInboxMessage();
						_this.loadContact();
					}
				}
			});
		},
		computed:{
			attachmentWithUrls:function(){
			  var _this = this;
				var _attachmments = this.messages[this.currentMessageNum].attachment;
				_attachmments.forEach(function(item, index){
					item.url = _this.base_url+"/download?attach="+item.path;
				});
				return _attachmments;
			}
		},
		mounted() {
		},
		data(){
			return {
				accounts:[],
				searchInput:'',
				accountTree:[],
				currentAccount:'',
				currentBox:'',
				currentMessageNum: -1,
				messages:[],
				content:'',
				expandKey:[0],
				dialogFormVisible: false,
				newcontact:{
					"name": "",
					"email": "",
					"movephone": "",
					"company": "",
					"workphone": "",
					"remark": ""
				},
				contactEmails:[],
				syncLoadingVisibled:false,
				syncDeletingVisibled:false,
			}
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
					if(_this.currentMessageNum!=-1 && _this.messages.length!=0){
						_this.content = _this.messages[_this.currentMessageNum].content[0].text;
					}
					_this.loadNewMessage();
				});
			},
			deleteMessages(){
				var _this = this;
				_this.syncDeletingVisibled = true;
				this.$axios({
				  method: 'post',
				  url: localStorage.getItem('base_url')+'/delete',
				  data:{
				  		raw:{
				  			msg: _this.messages[_this.currentMessageNum],
				  			account: _this.currentAccount
				  		}
				  },
				  headers:{'Content-Type': 'application/json','token':localStorage.token},
				}).then(function(res) {
					console.log(res.data)
					if(res.data.code == '200'){
						_this.syncDeletingVisibled = false;
						_this.$message({type: 'success', message: '删除成功!'});
						_this.currentMessageNum=-1
						_this.loadMessages();
					}
				});
			},
			readMessages(){
			  // debugger;
				var _this = this;
				_this.$axios({
				  method: 'get',
				  url: localStorage.getItem('base_url')+'/read?account='+_this.currentAccount+'&box='+_this.currentBox+'&msgUid='+_this.messages[_this.currentMessageNum].uid,
				  headers:{'Content-Type': 'application/json','token':localStorage.token}
				}).then(function(res) {
					console.log(res.data)
					if(res.data.code == '200'){
						// _this.loadMessages();
            _this.loadNewMessage();
					}
				});
			},
			searchMessages(){
				var _this = this;
				if(_this.currentAccount==''){
					return;
				}
				if(_this.searchInput.trim()==''){
					return;
				}
				this.$axios({
				  method: 'get',
				  url: localStorage.getItem('base_url')+'/search?account='+_this.currentAccount+'&queryKey='+_this.searchInput,
				  headers:{'Content-Type': 'application/json','token':localStorage.token},
				}).then(function(res) {
					console.log(res.data)
					var _messages = res.data.data;
					_this.messages = res.data.data;
					_messages.forEach(function(item, index){
						var _content = item.content[0].text;
						var _subject = item.subject;
						var reg = new RegExp(_this.searchInput, "g");
						// item.subject = _subject.replace( reg , '<font color=\'red\'>'+ _this.searchInput + '</font>');
						item.content[0].text = _content.replace( reg , '<font color=\'red\'>'+ _this.searchInput + '</font>');
					});
					_this.messages = _messages;
					_this.currentMessageNum = -1;
					_this.$message({type: 'success', message: '查询成功，共查到'+_this.messages.length+'封信件！'});
				});
			},
			searchMsgByAddress(event){
				var _this = this;
				this.$axios({
				  method: 'get',
				  url: localStorage.getItem('base_url')+'/search0?account='+_this.currentAccount+'&origin=from&queryKey='+event.target.dataset.address,
				  headers:{'Content-Type': 'application/json','token':localStorage.token}
				}).then(function(res) {
					console.log(res.data)
					_this.messages = res.data.data;
					_this.currentMessageNum = -1;
					_this.$message({type: 'success', message: '查询成功，共查到'+_this.messages.length+'封信件！'});
				});
			},
      searchMsgByOrigin(event){
        var _this = this;
        if(event.target.dataset.origin=='all'){
          console.log('origin:all')
          this.$axios({
            method: 'get',
            url: localStorage.getItem('base_url')+'/search?account='+_this.currentAccount+'&queryKey='+_this.searchInput,
            headers:{'Content-Type': 'application/json','token':localStorage.token}
          }).then(function(res) {
            console.log(res.data)
            var _messages = res.data.data;
            _this.messages = res.data.data;
            _messages.forEach(function(item, index){
              var _content = item.content[0].text;
              var _subject = item.subject;
              var reg = new RegExp(_this.searchInput, "g");
              item.subject = _subject.replace( reg , '<font color=\'red\'>'+ _this.searchInput + '</font>');
              item.content[0].text = _content.replace( reg , '<font color=\'red\'>'+ _this.searchInput + '</font>');
            });
            _this.messages = _messages;
            _this.currentMessageNum = -1;
            // 将currentBox重置
            _this.currentBox = 'INBOX';
            _this.searchInput ='';
            _this.$message({type: 'success', message: '查询成功，共查到'+_this.messages.length+'封信件！'});
          });
        }else{
          console.log('origin:other')
          this.$axios({
            method: 'get',
            url: localStorage.getItem('base_url')+'/search0?account='+_this.currentAccount+'&origin='+event.target.dataset.origin+'&queryKey='+_this.searchInput,
            headers:{'Content-Type': 'application/json','token':localStorage.token}
          }).then(function(res) {
            console.log(res.data)
            var _messages = res.data.data;
            _this.messages = res.data.data;
            _messages.forEach(function(item, index){
              var _content = item.content[0].text;
              var _subject = item.subject;
              var reg = new RegExp(_this.searchInput, "g");
              item.subject = _subject.replace( reg , '<font color=\'red\'>'+ _this.searchInput + '</font>');
              item.content[0].text = _content.replace( reg , '<font color=\'red\'>'+ _this.searchInput + '</font>');
            });
            _this.messages = _messages;
            _this.currentMessageNum = -1;
            _this.searchInput ='';
            _this.$message({type: 'success', message: '查询成功，共查到'+_this.messages.length+'封信件！'});
          });
        }
      },
			loadNewMessage(){
				var _this = this;
				_this.accounts.forEach(function(item, index){
					_this.calcNewMessage(item.account);
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
					if(_this.currentMessageNum!=-1  && _this.messages.length!=0){
						_this.content = _this.messages[_this.currentMessageNum].content[0].text;
					}else{
						_this.currentMessageNum = -1;
					}
					_this.loadNewMessage();
				});
			},
			syncMessage(accout){
				var _this = this;
				_this.syncLoadingVisibled = true;
				this.$axios({
				  method: 'get',
				  url: localStorage.getItem('base_url')+'/sync?account='+accout,
				  headers:{'Content-Type': 'application/json','token':localStorage.token}
				}).then(function(res) {
					console.log(res.data)
					_this.messages = [];
					_this.currentMessageNum = -1;
					_this.syncLoadingVisibled = false;
				});
			},
			downloadAttach(event){
				console.log(event.target.dataset.path);
				var _this = this;
				this.$axios({
				  method: 'get',
				  url: localStorage.getItem('base_url')+'/download?path='+event.target.dataset.path,
				  headers:{'Content-Type': 'application/json','token':localStorage.token}
				}).then(function(res) {
					console.log(res.data)
				});
			},
			saveCurrentMessage(){
				var _this = this;
				var _currentMessage = {};
				_currentMessage.currentAccount = _this.currentAccount;
				_currentMessage.currentBox = _this.currentBox;
				_currentMessage.currentMessageNum = _this.currentMessageNum;
				sessionStorage.setItem("currentMessage", JSON.stringify(_currentMessage));
			},
			loadContact(){
				var _this = this;
				this.$axios({
				  method: 'get',
				  url: localStorage.getItem('base_url')+'/contacts',
				  headers:{'Content-Type': 'application/json','token':localStorage.token}
				}).then(function(res) {
					console.log(res.data);
					var _contactEmails = new Array();
					var _contacts =  res.data.data;
					_contacts.forEach(function(item, index){
						_contactEmails.push(item.email);
					});
					_this.contactEmails = _contactEmails;
				});
			},
			showContactDialog(event){
				var _address = event.target.dataset.address;
				this.newcontact.name=_address.substring(0, _address.indexOf('<'));
				this.newcontact.email=_address.substring(_address.indexOf('<')+1, _address.indexOf('>'));
				this.dialogFormVisible = true;
			},
			hideContactDialog(){
				this.dialogFormVisible = false;
			},
			doNewContact(){
				var _this = this;
				this.$axios.post(localStorage.getItem('base_url')+'/contacts', _this.newcontact,
				{headers:{'Content-Type': 'application/json', 'token': localStorage.token, 'type':'save'}}).then(function(res) {
					console.log(res.data)
					if(res.data.code == '200'){
						_this.dialogFormVisible = false;
						_this.$message({
							type: 'success',
							message: '添加联系人成功!',
							duration:1000
						});
						_this.loadContact();
					}
				});
			},
			handleSelect(key, keyPath) {
				var _this = this;
				console.log(key, keyPath);
				// 存入sessionStorage中
				_this.saveCurrentMessage();
				//收取邮件
				if(keyPath[0] == '1'){
					_this.syncMessage(key);
				}
				//发信件
				if(key == '2-2'){
					console.log('发信件')
					_this.$router.push({
					  name:'PCSend',
					  params:{

						  way:'send',
						  type:'html',
						  msg: {
							  from:_this.currentAccount,
							  to:[],
							  cc:[],
							  bcc:[],
							  subject:'',
							  attachment:[],
							  content:[{
							  charset:'',
							  type:'',
							  text:'',
						  }]},
						  fileList:[],
						  accounts:_this.accounts,
					 }
					});
				}
				if(key == '2-3'){
					console.log('发信件')
					_this.$router.push({
					  name:'PCSend',
					  params:{

						  way:'send',
						  type:'plain',
						  msg: {
							  from:_this.currentAccount,
							  to:[],
							  cc:[],
							  bcc:[],
							  subject:'',
							  attachment:[],
							  content:[{
							  charset:'',
							  type:'',
							  text:'',
						  }]},
						  fileList:[],
						  accounts:_this.accounts,
					 }
					});
				}
				//回复信件
				if(key == '3-2'){
					console.log('回复信件')
					_this.$axios({
					  method: 'get',
					  url: localStorage.getItem('base_url')+'/reply?account='+_this.currentAccount+'&box='+_this.currentBox+'&msgUid='+_this.messages[_this.currentMessageNum].uid+'&type=html',
					  headers:{'Content-Type': 'application/json','token':localStorage.token}
					}).then(function(res) {
						console.log(res.data)
						var _replyMsg = res.data.data;
						_replyMsg.from = _this.currentAccount;
						if(res.data.code == '200'){
							_this.$router.push({
							  name:'PCSend',
							  params:{

								  way:'reply',
								  type:'html',
								  msg: _replyMsg,
								  accounts:_this.accounts
							 }
							});
						}
					});
				}
				//回复信件
				if(key == '3-3'){
					console.log('回复信件')
					_this.$axios({
					  method: 'get',
					  url: localStorage.getItem('base_url')+'/reply?account='+_this.currentAccount+'&box='+_this.currentBox+'&msgUid='+_this.messages[_this.currentMessageNum].uid+'&type=plain',
					  headers:{'Content-Type': 'application/json','token':localStorage.token}
					}).then(function(res) {
						console.log(res.data)
						var _replyMsg = res.data.data;
						_replyMsg.from = _this.currentAccount;
						if(res.data.code == '200'){
							_this.$router.push({
							  name:'PCSend',
							  params:{

								  way:'reply',
								  type:'plain',
								  msg: _replyMsg,
								  accounts:_this.accounts
							 }
							});
						}
					});
				}
				//回复全部信件
				if(key == '4-2'){
					console.log('回复全部信件')
					this.$axios({
					  method: 'get',
					  url: localStorage.getItem('base_url')+'/replyAll?account='+_this.currentAccount+'&box='+_this.currentBox+'&msgUid='+_this.messages[_this.currentMessageNum].uid+'&type=html',
					  headers:{'Content-Type': 'application/json','token':localStorage.token}
					}).then(function(res) {
						console.log(res.data)
						var _replyAllMsg = res.data.data;
						_replyAllMsg.from = _this.currentAccount;
						if(res.data.code == '200'){
							_this.$router.push({
							  name:'PCSend',
							  params:{

								  way:'reply',
								  type:'html',
								  msg: _replyAllMsg,
								  accounts:_this.accounts
							 }
							});
						}
					});
				}
				if(key == '4-3'){
					console.log('回复全部信件')
					this.$axios({
					  method: 'get',
					  url: localStorage.getItem('base_url')+'/replyAll?account='+_this.currentAccount+'&box='+_this.currentBox+'&msgUid='+_this.messages[_this.currentMessageNum].uid+'&type=plain',
					  headers:{'Content-Type': 'application/json','token':localStorage.token}
					}).then(function(res) {
						console.log(res.data)
						var _replyAllMsg = res.data.data;
						_replyAllMsg.from = _this.currentAccount;
						if(res.data.code == '200'){
							_this.$router.push({
							  name:'PCSend',
							  params:{

								  way:'reply',
								  type:'plain',
								  msg: _replyAllMsg,
								  accounts:_this.accounts
							 }
							});
						}
					});
				}
				//转发全部信件
				if(key == '5'){
					console.log('转发全部信件')
					this.$axios({
					  method: 'get',
					  url: localStorage.getItem('base_url')+'/forward?account='+_this.currentAccount+'&box='+_this.currentBox+'&msgUid='+_this.messages[_this.currentMessageNum].uid,
					  headers:{'Content-Type': 'application/json','token':localStorage.token}
					}).then(function(res) {
						console.log(res.data)
						var _forwardMsg = res.data.data;
						_forwardMsg.from = _this.currentAccount;
						if(res.data.code == '200'){
							_this.$router.push({
							  name:'PCSend',
							  params:{

								  way:'reply',
								  type:'html',
								  msg: _forwardMsg,
								  accounts:_this.accounts
							 }
							});
						}
					});
				}
				// 删除信封
				if(key == '6'){
					this.$confirm('您确认要永久删除这些邮件吗？', '确认', {
					  confirmButtonText: '确定',
					  cancelButtonText: '取消',
					  type: 'warning'
					}).then(() => {
					  _this.deleteMessages();
					}).catch(() => {
					});
				}

				// 附件管理
				if(key == '7-1'){
					console.log('附件管理')
					_this.$router.push({
						name:'PCAttach',
						params:{

						}
					});
				}
				// 签名管理
				if(key == '7-2'){
					console.log('签名管理')
					_this.$router.push({
						name:'PCSign',
						params:{

					    }
					});
				}
				//	联系人管理
				if(key == '7-4'){
					console.log('联系人管理')
					_this.$router.push({
						name:'PCContact',
						params:{

					    }
					});
				}
				//	账号管理
				if(key == '7-5'){
					console.log('账号管理')
					_this.$router.push({
						name:'PCAccount',
						params:{

					    }
					});
				}
				//	退出登陆
				if(key == '7-6'){
					console.log('账号管理')
					localStorage.removeItem("token");
					sessionStorage.removeItem("currentAttachment");
					sessionStorage.removeItem("currentMessage");
					_this.$router.replace({
						name:'PCWebin',
						params:{

					    }
					});
				}

			},
      handleTableColumnClick(data){
				var _this = this;
				console.log(data);
				// 草稿箱进入编辑邮件页面
				if(_this.currentBox == '草稿箱'){
          _this.$router.push({
            name:'PCSend',
            params:{
              way:'send',
              type:'html',
              msg: data,
              accounts:_this.accounts
            }
          });
        }else{
          _this.messages.forEach(function(item, index){
            if(item.id == data.id){
              _this.currentMessageNum = index;
            }
          });
          console.log(_this.currentMessageNum+"/"+_this.messages[_this.currentMessageNum].new)

          if(_this.messages[_this.currentMessageNum].new == true){
            /* 更新邮件为已读 */
            _this.messages[_this.currentMessageNum].new = false;
            /* 更新本地文件 */
            _this.readMessages();
          }
          _this.content = _this.messages[_this.currentMessageNum].content[0].text;
          _this.saveCurrentMessage();
        }
			},
			async calcNewMessage(account){
				var _this = this;
				var _results = new Array();
				var boxes = ['INBOX','草稿箱','已发送','已删除','垃圾箱'];
				boxes.forEach(function(item, index){
					var _res = _this.$axios({
					  method: 'get',
					  url: localStorage.getItem('base_url')+'/messages?account='+account+'&box='+item,
					  headers:{'Content-Type': 'application/json','token':localStorage.token}
					}).then(
						resolve => 	{
							var _newMessagesNum = 0;
							var _messages = resolve.data.data;
							_messages.forEach(function(item, index){
								if(item.new == true){
									_newMessagesNum++;
								}
							});
							return Promise.resolve(_newMessagesNum);
						}
					);
					_results.push(_res);
				});
				_this.accountTree.forEach(function(item, index){
					if(item.value == account){
						item.children.forEach(function(item0, index0){
							_results[index0].then(value=>{
								if(value != 0){
									item0.label=(item0.value=='INBOX')?'收件箱'+'('+value+')':item0.value+'('+value+')';
								}else{
									item0.label=(item0.value=='INBOX')?'收件箱':item0.value;
								}

							})
						})
					}
				});
				console.log(_results)
			},
			handleTreeNodeClick(data) {
				console.log(data)
				if(data.children.length == 0){
					this.currentBox = data.value;
					this.currentMessageNum = -1;
					this.saveCurrentMessage();
					this.loadMessages();
					this.searchInput = '';
				}
		    },
			handleTreeNodeExpand(data){
				this.currentAccount = data.label;
				this.messages = [];
				this.currentMessageNum = -1;
			}
		}
	}
</script>

<style scoped>
	.abstract-message{
		width: 100%;
		font-size: 0.7rem;
	}
	.message-header{

	}
	.message-header-subject{
		font-weight: bold;
		fong-size:1.5em;
		text-align: left;
    color:#000000;
	}
	.message-header-item{
		text-align: left;
		background-color:#ffffff;
		color:#84898a;
		font-size: small;
		margin-bottom: 10px;
	}
	.message-header-attachs{
		display: flex;
		flex-direction: row;
	}
	.message-header-address{
		padding:2px 10px;
		margin-right:10px;
		background-color:rgba(64,158,255,1);
		border-radius: 10px;
		color:#ffffff;
		cursor:pointer;/* 手型指针 */
	}
	.message-header-attach{
    color:#84898a;
    padding:4px;
    margin:4px;
    border-radius: 4px;
    background: #f2f2f2;
	}
	.message-content{

	}
	.menu-padding{
		padding-right: 6px;
	}
	.submenu-padding{
		padding-left:6px;
		padding-right: 10px;
	}
	.contact-form{
		margin:10px auto;
		width:300px;
		padding: 10px;
	}
  .search-items{
    position: absolute;
    top:60px;
    left:74%;
    background: #545c64;
    color:#ffffff;
    width:250px;
    flex-direction: column;
  }
  .search-item:hover{
    background-color: rgb(67, 74, 80);
  }
  .search-item{
    padding:6px;
    /*background-color: rgb(67, 74, 80);*/
  }
</style>
