<template>
  <div id="app">
    <!-- <img src="./assets/logo.png"> -->
		<!-- <router-link to="/welcome">Go to welcome</router-link>
		<router-link to="/main">Go to main</router-link> -->
    <router-view/>
  </div>
</template>

<script>
export default {
  name: 'App',
  mounted(){
		var _this = this;
    /* base_url */
    const base_url = localStorage.getItem('base_url');
    if(!base_url){
      // localStorage.setItem('base_url', "http://39.106.20.40:8080/api")
      localStorage.setItem('base_url', "http://localhost:8080/api")
    }
    console.log(base_url)
		/* 先验证token是否有效,无效则跳转到登陆页面 */
		this.$axios({
		  method: 'get',
		  url: localStorage.getItem('base_url')+'/valid',
		  headers:{'Content-Type': 'application/json','token':localStorage.token}
		}).then(function(res) {
			console.log(res.data);
			let flag = navigator.userAgent.match(/(phone|pad|pod|iPhone|iPod|ios|iPad|Android|Mobile|BlackBerry|IEMobile|MQQBrowser|JUC|Fennec|wOSBrowser|BrowserNG|WebOS|Symbian|Windows Phone)/i);
			if(res.data.code == '200'){
				/* 这里对PC和Mobile端写不同的ui*/
				if(flag){
					_this.$router.replace({ name: 'MWelcome',});
					console.log('mobile')
				}else{
					_this.$router.replace({ name: 'PCWelcome',});
					console.log('pc')
				}
			}else{
				if(flag){
					_this.$router.replace({ name: 'MWebin',});
					console.log('mobile')
				}else{
					_this.$router.replace({ name: 'PCWebin',});
					console.log('pc')
				}
			}

		});

  }
}
</script>

<style>
#app {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
/*  text-align: center;
  color: #2c3e50; */
  margin-top: 0;
}
/* Mobile端公共样式 */
.mms-inline-html{
	/* zoom: 0.5;
	display: inline-block;
	overflow-x: auto; */
}
.mms-font-gray{
	color:#999;
}
.mms-font-main{
	color:#26a2ff;
}
.mms-fixed-button{
	display: flex;
	flex-direction: row;
	justify-content: center;
	align-items: center;
	position: fixed;
	bottom:20px;
	left:50%;
	transform: translate(-50%, -50%);
	text-align: center;
	box-shadow: 0 2px 4px rgba(0, 0, 0, .12);
	border-radius: 30px;
	height:40px;
	width:80%;
	color: #888;
	background-color: #ffffff;
}
/* PC端公共样式 */
.ums-page{
	min-width: 1080px;
}
.ums-header{
	height:60px;
	display: flex;
	flex-direction: row;
	justify-content: left;
	align-items: center;
	background-color: #545c64;
	color:#ffffff;
}
.ums-container{
	display: flex;
	flex-direction: row;
	justify-content: left;
	width:100%;
	background-color: rgb(255, 255, 255);
	height:650px;
}
.ums-aside{
	width:250px;
	border: 10px solid rgb(238, 241, 246);
}
.ums-main{
	display: block;
	overflow: auto;
	width:100%;
	border: 10px solid rgb(238, 241, 246);
	padding: 20px;
}
.return-row-padding{
	padding:0 20px;
}
.ums-ellipsis{
	white-space:nowrap;
	overflow:hidden;
	text-overflow:ellipsis;
}
</style>
