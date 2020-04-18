<template>
	<div>
		<header class="mui-bar mui-bar-nav">
		  <a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left" @click="$router.go(-1)"></a>
		  <h1 class="mui-title">{{way=='send'?'写邮件':(way=='reply'?'回复邮件':'转发邮件')}}</h1>
     <span class="iconfont umail-send-box mms-font-main mui-pull-right" @click="sendMessage"
           style="font-size: 18px;position: relative;z-index: 20;padding:10px;"></span>
		  <!--<a class="mui-icon mui-icon-arrowthinright mui-pull-right" @click="sendMessage"></a>-->
		</header>
		<div class="mui-content" style="background: #ffffff;">
			<form class="mui-input-group">
				<div class="form-padding"></div>
			    <div class="mui-input-row">
			        <label>收件人</label>
					<input type="text" class="mui-input-clear" v-model="toInput"  @change="toInputChange" placeholder="收件人Email地址">
			    </div>
				<el-tag style="margin-right:10px;border-radius: 20px;"
					v-for="tag in msg.to" size="small"
					closable @close="handleToClose(tag)">{{tag}}</el-tag>
			    <div class="mui-input-row">
					<label>抄送</label>
					<input type="text" class="mui-input-clear" v-model="ccInput" @change="ccInputChange" placeholder="抄送Email地址">
				</div>
				<el-tag style="margin-right:10px;border-radius: 20px;"
					v-for="tag in msg.cc" size="small"
					closable @close="handleCcClose(tag)">{{tag}}</el-tag>
				<div class="mui-input-row">
					<label>密送</label>
					<input type="text" class="mui-input-clear" v-model="bccInput" @change="bccInputChange" placeholder="密送Email地址">
				</div>
				 <el-tag style="margin-right:10px;border-radius: 20px;"
					v-for="tag in msg.bcc" size="small"
					closable @close="handleBccClose(tag)">{{tag}}</el-tag>
				<div class="mui-input-row">
					<label>发件人</label>
					<input type="text" class="mui-input-clear" v-model="msg.from" :disabled="way!='send'" placeholder="发件人Email地址">
				</div>
				<div class="mui-input-row">
					<label>主题</label>
					<input type="text" class="mui-input-clear" v-model="msg.subject" placeholder="邮件主题">
				</div>
        <!--附件列表-->
        <div  v-for="(file, index) in msg.attachment"
              style="color:#84898a;padding:5px;margin-bottom:5px;display: flex;
            flex-direction: row;background: #f8f8f8;">
          <div style="text-align: left;white-space:nowrap;width:90%;text-overflow:ellipsis;">{{file.name}}</div>
          <div style="width:10%;color:#409eff;cursor: pointer;text-align: center;"><span  @click="removeAttach" :data-index="index">删除</span></div>
        </div>

        <div style="position:relative;left:10px;top:10px;height:24px;width:24px;overflow: hidden;">
          <el-upload
            class="upload-demo"
            :action="uploadUrl"
            :on-change="handleChange"
            :limit="10"
            :auto-upload="true"
            :http-request="uploadSectionFile"
            :file-list="fileList">
            <div style="box-shadow: 0 2px 4px rgba(0, 0, 0, .12);border-radius: 50%;width:24px;height:24px;line-height: 20px;">
              <span class="iconfont umail-attachment mms-font-main" style="font-size:20px;"></span>
            </div>
          </el-upload>
        </div>
				<div v-if="way=='send'">
					<textarea class="mui-input-clear" rows="5" v-model="msg.content"  placeholder="邮件正文"></textarea>
				</div>
				<div v-else>
					<textarea class="mui-input-clear" rows="5"  v-model="msg.content"  placeholder="邮件正文"></textarea>
					<div style="border-bottom:1px solid #d9d9d9;paddin-left:10px;">
						<el-checkbox v-model="useBlockquote">包含引用文字</el-checkbox>
					</div>
					<div v-html="content0" style="zoom:0.5;" v-if="useBlockquote"></div>
				</div>
			</form>
    </div>
	</div>
</template>

<script>
	export default {
			name: 'send',
			created() {
				var _this = this;
				this.way = this.$route.params.way;//回复、转发
				this.type = this.$route.params.type;//html、plain
				this.accounts = this.$route.params.accounts;
				this.msg = this.$route.params.msg;
				this.fileList = this.$route.params.fileList;

				if(this.msg.from == '' && this.accounts.length!=0){
					this.msg.from = this.accounts[0].account;
				}
				_this.content0 =  _this.msg.content[0].text;
				_this.msg.content= '\n\n\n发自UMail移动端';
        this.uploadUrl = localStorage.getItem('base_url')+'/upload';
				// this.msg.content = this.$route.params.msg.content[0].text;
				console.log('content:'+this.msg.content)
			},
			computed:{
			},
			mounted() {
			},
			data(){
				return {
						type:'',
						to:'',
						toInput:'',
						ccInput:'',
						bccInput:'',
						msg:{
							from:'',
							to:[],
							cc:[],
							bcc:[],
							subject:'',
							date:'',
							content:'',
							type:'',
              attachment:[]
						},
						fileList:[],
						useBlockquote:true,
						content0:'',
					}
				},
			methods:{
				handleToClose(tag){
					this.msg.to.splice(this.msg.to.indexOf(tag), 1);
				},
				handleCcClose(tag){
					this.msg.cc.splice(this.msg.cc.indexOf(tag), 1);
				},
				handleBccClose(tag){
					this.msg.bcc.splice(this.msg.bcc.indexOf(tag), 1);
				},
				toInputChange(){
					if (!this.toInput || this.toInput.trim() == "") {
						return false;
					}
					if (this.msg.to.length > 0) {
						for (var i in this.msg.to) {
							if (this.msg.to[i] == this.toInput) {
								return false;
							}
						}
					}
					this.msg.to.push(this.toInput + '<'+this.toInput+'>');
					this.toInput = '';
				},
				ccInputChange(){
					if (!this.ccInput || this.ccInput.trim() == "") {
						return false;
					}
					if (this.msg.cc.length > 0) {
						for (var i in this.msg.cc) {
							if (this.msg.cc[i] == this.ccInput) {
								return false;
							}
						}
					}
					this.msg.cc.push(this.ccInput + '<'+this.ccInput+'>');
					this.ccInput = '';
				},
				bccInputChange(){
					if (!this.bccInput || this.bccInput.trim() == "") {
						return false;
					}
					if (this.msg.bcc.length > 0) {
						for (var i in this.msg.bcc) {
							if (this.msg.bcc[i] == this.bccInput) {
								return false;
							}
						}
					}
					this.msg.bcc.push(this.bccInput + '<'+this.bccInput+'>');
					this.bccInput = '';
				},
        handleChange(file, fileList) {
          console.log(fileList)
          this.fileList = fileList.slice(-10);
        },
        removeAttach(event){
          var startIndex = parseInt(event.currentTarget.dataset.index);
          var attachs =  new Array();
          for(var i in this.msg.attachment){
            if(startIndex != i){
              attachs.push(this.msg.attachment[i]);
            }
          }
          this.msg.attachment = attachs;
        },
        uploadSectionFile(params){
          var _this = this;
          console.log(params)
          var formData = new FormData();
          formData.append("file", params.file);
          this.$axios.post(localStorage.getItem('base_url')+'/upload', formData, { headers: { 'Content-Type': 'multipart/form-data' } })
            .then(function (res) {
              if (res.data.code === '200') {
                console.log('返回结果')
                console.log(res.data)
                _this.msg.attachment.push(res.data.data[0]);
                _this.msg.subject = res.data.data[0].name;
                console.log(_this.msg)
                console.log('返回结果1')
              }
            })
            .catch(function (err) {
              console.error(err);
            });
        },
				sendMessage(){
					var _this = this;
					var _msg = JSON.parse(JSON.stringify(this.msg));
					var data = {
					};
					_msg.content = this.useBlockquote ? this.msg.content+this.content0:this.msg.content;
					_msg.type = this.type;
					data.msg = _msg;
					data.way = 0;
					var raw = JSON.stringify(data);
					console.log(raw)

					this.$axios({
					  method: 'post',
					  url: localStorage.getItem('base_url')+'/send',
						data:{
							raw: JSON.stringify(data)
						},
					  headers:{'Content-Type': 'application/json',
					  'token':localStorage.token,
						}
					}).then(function(res) {
						console.log(res.data)
						if(res.data.code == '200'){
							_this.$mui.toast('发送成功',{ duration:'long', type:'div' });
							_this.$router.go(-1);
						}else{
							_this.$mui.toast('发送失败',{ duration:'long', type:'div' });
						}
					});
				}
			}
		}
</script>

<style scoped>
	.form-padding{
		height:15px;
		background: #f8f8f8;
	}
</style>

