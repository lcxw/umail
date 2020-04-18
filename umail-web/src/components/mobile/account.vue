<template>
	<div>
		<header class="mui-bar mui-bar-nav">
		  <a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left"  @click="$router.go(-1)"></a>
		  <h1 class="mui-title">账号</h1>
		</header>
		<div class="mui-content">
			<ul class="mui-table-view">
				<li class="mui-table-view-cell mui-media" v-for="acc in accounts"
					@click="$router.push({name:'MEditAccount',params:{account:acc,status:'update'}})">
					<a  class="mui-navigate-right" href="javascript:;">
						<div class="mui-media-body">
							{{acc.alias}}
							<p class='mui-ellipsis'>{{acc.account}}</p>
						</div>
					</a>
				</li>
			</ul>
			<div class="mms-fixed-button">
				<div style="width:100%;" @click="addAccount">
					<span slot="icon" class="iconfont umail-hao mms-font-main"></span><br>
					<span style="font-size:0.6em">添加账号</span>
				</div>
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
		},
		computed:{
		},
		data(){
			return {
				isAccountDetailShow:false,
				accounts:[],
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
			showAccountDetail(event){
				console.log(event.target.dataset)
			},
			addAccount(){
				console.log('hello')
				this.$router.push({
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
			},
		}
	}
</script>

<style scoped>
</style>
