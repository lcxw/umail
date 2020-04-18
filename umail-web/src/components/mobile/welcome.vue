<template>
	<div>
		<main class="main" ref="main">
			<section class="is-scrollable navbar">
					<div class="navbar-header">
						<span class="iconfont umail-touxiang" style="font-size:80px;position: relative;top:30px;"></span><br>
						<span style="position: relative;top:40px;">{{currentAccount}}</span>
						<div v-if="accounts.length>1" @click="changeAccount" style="position: absolute;top:30px;right:20px;"><span class="iconfont umail-touxiang" style="font-size:40px;"></span><br></div>
					</div>
					<div class="navbar-body">
						<ul class="nav-ul">
							<li class="nav-li" @click="loadMessages" data-box="INBOX"><span class="iconfont umail-inbox-box menu-padding-right"></span>收件箱</li>
							<li class="nav-li" @click="loadMessages" data-box="草稿箱"><span class="iconfont umail-draft-box menu-padding-right"></span>草稿箱</li>
							<li class="nav-li" @click="loadMessages" data-box="已发送"><span class="iconfont umail-sent-box menu-padding-right"></span>已发送</li>
							<li class="nav-li" @click="loadMessages" data-box="已删除"><span class="iconfont umail-deleted-box menu-padding-right"></span>已删除</li>
							<li class="nav-li" @click="loadMessages" data-box="垃圾箱"><span class="iconfont umail-lajixiang-box menu-padding-right"></span>垃圾箱</li>
							<li class="nav-li"@click="logout"><span class="iconfont umail-logout menu-padding-right"></span>退出</li>
						</ul>
					</div>
			</section>
			<section class="is-scrollable content">
			<div>
				<header class="mui-bar mui-bar-nav">
					<span class="iconfont umail-sanheng mui-pull-left"  style="font-size: 20px;padding-top:10px;" @click="showNav"></span>
				  <!-- <a class="mui-icon mui-icon-left-nav mui-pull-left"   @click="showNav"></a> -->
				  <h1 class="mui-title">{{currentBox}}</h1>
				</header>
				<div class="mui-content">
					<ul class="mui-table-view">
						<li class="mui-table-view-cell mui-media" @click="$router.push({name:'MMsgDetail',params:{message:msg, currentAccount:currentAccount,currentBox:currentBox=='收件箱'?'INBOX':currentBox}})" v-for="msg in messages">
							<a href="javascript:;">
								<span class="mui-icon mui-icon-contact mui-media-object mui-pull-left" style="font-size: 40px;color:#0062cc;"></span>
								<div class="mui-media-body">
									{{msg.from[0].substring(0,msg.from[0].indexOf('<'))}}
									<p class='mui-ellipsis'>{{msg.subject}}</p>
								</div>
							</a>
						</li>
					</ul>
					<div class="mms-fixed-button">
						<div style="width:50%;" @click="toSend">
							<div><span slot="icon" class="iconfont umail-bianji mms-font-main"></span></div>
							<div><span style="font-size:0.6em">写邮件</span></div>
						</div>
						<div style="width:50%;" @click="$router.push({name:'MAccount',params:{}})">
							<span slot="icon" class="iconfont umail-shezhi mms-font-main"></span><br>
							<span style="font-size:0.6em">设置</span>
						</div>
					</div>
				</div>
			</div>
			</section>
		</main>
	</div>
</template>

<script>
	export default {
		name: 'welcome',
		created() {
			var _this = this;
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
						name:'MEditAccount',
					    params:{
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
					    }
					})
				}else{
					_this.accounts = _accounts;
					_this.currentAccount = _accounts[0].account;
					_this.currentAccountNum = 0;
					_this.loadInboxMessage();
				}
			});
		},
		mounted() {
		},
		computed:{
		},
		data(){
			return {
				accounts:[],
				currentAccount:"",
				currentBox:"收件箱",
				messages:[],
				currentAccountNum:0,
			}
		},
		methods:{
			logout(){
				localStorage.removeItem("token");
				sessionStorage.removeItem("currentMessage");
				this.$router.replace({
					name:'MWebin',
					params:{
				    }
				});
			},
			changeAccount(){
				var _this = this;
				var len = _this.accounts.length;
				if(_this.currentAccountNum +1 >= len){
					_this.currentAccountNum = 0;
				}else{
					_this.currentAccountNum = _this.currentAccountNum+1;
				}
				_this.currentAccount = _this.accounts[_this.currentAccountNum].account;
				_this.loadInboxMessage();
			},
			loadInboxMessage(){
				var _this =this;
				this.$axios({
				  method: 'get',
				  url: localStorage.getItem('base_url')+'/messages?account='+this.currentAccount+'&box=INBOX',
				  headers:{'Content-Type': 'application/json','token':localStorage.token}
				}).then(function(res) {
					console.log(res.data)
					_this.messages = res.data.data;
					_this.currentMessageNum = -1;
					_this.$refs.main.classList.remove("slide-in-left");
				});
			},
			showNav(){
				if(this.$refs.main.classList.contains("slide-in-left")){
					this.$refs.main.classList.remove("slide-in-left");
				}else{
					this.$refs.main.classList.add("slide-in-left");
				}
			},
			loadMessages(event){
				var _this = this;
				var box = event.target.dataset.box;
				_this.currentBox = (box=='INBOX')?'收件箱':box;
				this.$axios({
				  method: 'get',
				  url: localStorage.getItem('base_url')+'/messages?account='+this.currentAccount+'&box='+box,
				  headers:{'Content-Type': 'application/json','token':localStorage.token}
				}).then(function(res) {
					console.log(res.data)
					_this.messages = res.data.data;
					_this.currentMessageNum = -1;
					_this.showNav();
				});
			},
			toSend(){
				var _this = this;
				_this.$router.push({
				  name:'MSend',
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
			},
		}
	}
</script>

<style scoped>
	.menu-more{
	    border-radius: 4px;
		border: 1px solid #fff;
		text-align: center;
	}
	.menu-padding-right{
		padding-right:10px;
	}
	.menu-padding-left{
		padding-left:10px;
	}
	.header{
		align-items: center;
		background-color: #26a2ff;
		box-sizing: border-box;
		color: #fff;
		display: flex;
		font-size: 14px;
		height: 40px;
		line-height: 1;
		padding: 0 10px;
		position: relative;
		text-align: center;
		white-space: nowrap;
	}
	.main {
		height: 100%;
		/* padding: 0 10px; */
		transition: -webkit-transform .3s ease-in-out;
		transition: transform .3s ease-in-out;
		transition: transform .3s ease-in-out,-webkit-transform .3s ease-in-out;
	}
	.is-scrollable {
		overflow: auto;
	}
	.slide-in-left{
		transform: translate3d(70%,0,0);
	}
	.navbar{
		/* background-color: #f9fafb; */
		left: 0;
		margin-left: -70%;
		margin-right: 0;
		opacity: 1;
		position: absolute;
		top: 0;
    bottom:0;
		transition: opacity .3s;
		width: 70%;
		z-index: 10;
		min-width: auto;
		padding: 0;
	}
	.navbar-header{
		padding:20px;
		height: 160px;
		border-bottom:1px solid #26a2ff;
		background-color: #26a2ff;
		color:#ffffff;
	}
	.navbar-body{
		/* padding-left:10px; */
	}
	.nav-ul {
		list-style: none outside none;
		margin: 0;
		padding-left:10px;
	}
	.nav-li {
		display: list-item;
		text-align: -webkit-match-parent;
		display: block;
		height:50px;
		line-height: 50px;
	}
	.slide-in-left .navbar {
		opacity: 1;
	}
	.content{
		width: 100%;
		/* padding: 45px 10px 0; */
	}
	.show-ellipsis{
		white-space:nowrap;/*禁止文字折行*/
	    overflow:hidden;/*溢出内容为隐藏*/
	    text-overflow:ellipsis;/*文本溢出时是否显示省略标记，clip：不显示， ellipsis：显示*/
	}
	.border-line{
		border-bottom: 1px solid #eee;
	}
</style>
