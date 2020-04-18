<template>
	<div>
		<header class="mui-bar mui-bar-nav">
		  <a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left" @click="$router.go(-1)"></a>
		  <h1 class="mui-title">{{message.from[0].substring(0,message.from[0].indexOf('<'))}}</h1>
		</header>
		<div class="mui-content" style="background: #ffffff;">
			<div class="message-body menu-padding-left menu-padding-right">
				<div class="border-line">
					<p class="mms-font-gray">收件人：<span v-for="address in message.to"  class="mms-font-main menu-padding-right">
					{{address.substring(address.indexOf('<')+1, address.indexOf('>'))}}</span></p>
				</div>
				<div class="border-line">
					<p v-html="message.subject" style="font-weight: bold;color:#000000;"><p>
					<p v-html="message.date" class="mms-font-gray"></p>
				</div>
				<div class="inline-html" ref="messageContent" v-html="message.content[0].text" style="zoom:0.5;"></div>
			</div>
			<div class="mms-fixed-button" ref="fixedButton">
				<div style="width:25%;" @click="replyMessage">
					<div><span slot="icon" class="iconfont umail-reply-mail mms-font-main"></span></div>
					<div><span style="font-size:0.6em">回复</span></div>
				</div>
				<div style="width:25%;" @click="replyAllMessage">
					<div><span slot="icon" class="iconfont umail-replyall-mail mms-font-main"></span></div>
					<div><span style="font-size:0.6em">全部回复</span></div>
				</div>
				<div style="width:25%;" @click="forwardMessage">
					<span slot="icon" class="iconfont umail-froward-mail mms-font-main"></span><br>
					<span style="font-size:0.6em">转发</span>
				</div>
				<div style="width:25%;" @click="deleteMessages">
					<span slot="icon" class="iconfont umail-deleted-box mms-font-main"></span><br>
					<span style="font-size:0.6em">删除</span>
				</div>
			</div>
		</div>
	</div>
</template>

<script>
	export default {
		name: 'msgdetail',
		created() {
		  this.handleWindowSlideChange();
			// 是从首页传参过来的
			if(this.$route.params.message){
				this.message = this.$route.params.message;
				this.currentAccount = this.$route.params.currentAccount;
				this.currentBox = this.$route.params.currentBox;
				sessionStorage.setItem("currentMesssage", JSON.stringify({
					message:this.message,
					currentAccount:this.currentAccount,
					currentBox:this.currentBox,
				}));
				console.log(this.$route.params.message)
				console.log(this.$route.params.currentAccount)
				console.log(this.$route.params.currentBox)
			}else{// 是按返回键，则取sessionStorage值
				console.log('返回键')
				var _currentMessageStr = sessionStorage.getItem("currentMesssage");
				var _currentMessage = JSON.parse(_currentMessageStr);
				this.message = _currentMessage.message;
				this.currentAccount = _currentMessage.currentAccount;
				this.currentBox = _currentMessage.currentBox;
			}
      this.handleContentWindowChange();
		},
		mounted() {
		},
		computed:{
		},
		data(){
			return {
				message:{},
				currentAccount:'',
				currentBox:'',
        preScrollTop:0,
			}
		},
		methods:{
		  handleWindowSlideChange(){
        var _this = this;
        // 浏览器滑动事件
        window.onscroll=function() {
            var _scrollTop = document.body.scrollTop || document.documentElement.scrollTop;
            // 获得当前滑动位置的高度
            var _scrollHeight = _scrollTop -  _this.preScrollTop;
            // 防止其他页面使用这个方法
            if(_this.$refs.fixedButton){
              if(_scrollHeight>0){
                // 隐藏fixed-button
                _this.$refs.fixedButton.style.display="none";
              }else{
                _this.$refs.fixedButton.style.display="flex";
              }
            }
        };
      },
      handleContentWindowChange(){
        // console.log("messageContent:"+this.$refs.messageContent)
      },
			deleteMessages(){
				var _this = this;
				this.$mui.confirm('确认删除邮件吗？','删除',['取消','确认'],function (e) {
					if(e.index==1){
						_this.$axios({
						  method: 'post',
						  url: localStorage.getItem('base_url')+'/delete',
						  data:{
								raw:{
									msg: _this.message,
									account: _this.currentAccount
								}
						  },
						  headers:{'Content-Type': 'application/json','token':localStorage.token},
						}).then(function(res) {
							console.log(res.data)
							if(res.data.code == '200'){
								_this.$mui.toast('删除成功',{ duration:'long', type:'div' });
								_this.$router.go(-1);
							}
						});
					}
				},'div')
			},
			replyMessage(){
				var _this = this;
				console.log('回复信件')
				_this.$axios({
				  method: 'get',
				  url: localStorage.getItem('base_url')+'/reply?account='+_this.currentAccount+'&box='+_this.currentBox+'&msgUid='+_this.message.uid+'&type=html',
				  headers:{'Content-Type': 'application/json','token':localStorage.token}
				}).then(function(res) {
					console.log(res.data)
					var _replyMsg = res.data.data;
					_replyMsg.from = _this.currentAccount;
					if(res.data.code == '200'){
						_this.$router.push({
						  name:'MSend',
						  params:{
							  way:'reply',
							  type:'html',
							  msg: _replyMsg,
						 }
						});
					}
				});
			},
			replyAllMessage(){
				var _this = this;
				console.log('回复信件')
				_this.$axios({
				  method: 'get',
				  url: localStorage.getItem('base_url')+'/replyAll?account='+_this.currentAccount+'&box='+_this.currentBox+'&msgUid='+_this.message.uid+'&type=html',
				  headers:{'Content-Type': 'application/json','token':localStorage.token}
				}).then(function(res) {
					console.log(res.data)
					var _replyMsg = res.data.data;
					_replyMsg.from = _this.currentAccount;
					if(res.data.code == '200'){
						_this.$router.push({
						  name:'MSend',
						  params:{
							  way:'reply',
							  type:'html',
							  msg: _replyMsg,
						 }
						});
					}
				});
			},
			forwardMessage(){
				var _this = this;
				console.log('回复信件')
				_this.$axios({
				  method: 'get',
				  url: localStorage.getItem('base_url')+'/forward?account='+_this.currentAccount+'&box='+_this.currentBox+'&msgUid='+_this.message.uid+'&type=html',
				  headers:{'Content-Type': 'application/json','token':localStorage.token}
				}).then(function(res) {
					console.log(res.data)
					var _replyMsg = res.data.data;
					_replyMsg.from = _this.currentAccount;
					if(res.data.code == '200'){
						_this.$router.push({
						  name:'MSend',
						  params:{
							  way:'forward',
							  type:'html',
							  msg: _replyMsg,
						 }
						});
					}
				});
			},
		}
	}
</script>

<style scoped>
	.menu-padding-right{
		padding-right:10px;
	}
	.menu-padding-left{
		padding-left:10px;
	}
	.message-body{
		margin-top:15px;
		font-size:0.8em;
	}
	.border-line{
		border-bottom: 1px solid #eee;
	}
</style>
