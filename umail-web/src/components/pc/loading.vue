<template>
	<div>
		<div class="ums-loading-mask" v-if="visible">
			<div class="ums-loading-spinner">
				<span class="iconfont umail-jiazai ums-icon-loading"></span>
				<span class="ums-loading-text ums-ellipsis">{{msg}}{{value}}</span>
			</div>
		</div>
	</div>
</template>

<script>
	export default{
	  props:['visible', 'msg', 'value'],
	}
</script>

<style scoped>

	.ums-loading-mask {
	    position: absolute;
	    z-index: 2000;
	    background-color: rgba(255,255,255,.9);
	    margin: 0;
	    top: 0;
	    right: 0;
	    bottom: 0;
	    left: 0;
	    -webkit-transition: opacity .3s;
	    transition: opacity .3s;
		background-color: rgba(0, 0, 0, 0.2);
	}
	.ums-loading-spinner {
	    top: 10px;
		left: 50%;
		transform: translateX(-50%);
	    min-width: 380px;
		height: 40px;
	    text-align: center;
	    position: absolute;
		border-radius: 3px;
		background-color: #ffffff;
	}
	.ums-icon-loading {
		position:absolute;
		font-size: 20px;
		top:10px;
		left:20px;
		color: #409EFF;
	    -webkit-animation: rotating 2s linear infinite;
	    animation: rotating 2s linear infinite;
	}
	.ums-loading-text{
		line-height: 40px;
		color: #606266;
	}
</style>
