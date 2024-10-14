<template>
  <div id="UiHeader">
		
    <el-menu router :default-active="activeIndex" menu-trigger="click" background-color="#1E282C" text-color="#fff"
             active-text-color="#1890FF" class="elmenu" >
			 
			 
			
      <!-- <el-menu-item class="elemenu">
    <img src="../assets/bullying-small.png" alt="avatar" class="menu-avatar"  active-text-color="#1890FF">
	  
    </el-menu-item>-->
      
	  
	  <el-menu-item index="/console">控制台</el-menu-item>
      <el-menu-item index="/live">实时监控</el-menu-item>
	  <el-menu-item index="/deviceList">国标设备</el-menu-item>
	  <!-- <el-menu-item index="/event">事件列表</el-menu-item> -->
	  <el-menu-item index="/eventsearch">事件搜索</el-menu-item>
	  <el-menu-item index="/history">历史监控</el-menu-item>
      <el-menu-item v-if="editUser" index="/userManager">人员管理</el-menu-item>
	  <el-menu-item index="/pushVideoList">推流列表</el-menu-item>
      <el-menu-item index="/streamProxyList">拉流代理</el-menu-item>
	  <el-menu-item index="/parentPlatformList/15/1">国标级联</el-menu-item>
	  <el-menu-item index="/mediaServerManger">节点管理</el-menu-item>
	  <el-menu-item index="/usercreate">用户创建</el-menu-item>

      <!--            <el-submenu index="/setting">-->
      <!--              <template slot="title">系统设置</template>-->
      <!--              <el-menu-item index="/setting/web">WEB服务</el-menu-item>-->
      <!--              <el-menu-item index="/setting/sip">国标服务</el-menu-item>-->
      <!--              <el-menu-item index="/setting/media">媒体服务</el-menu-item>-->
      <!--            </el-submenu>-->
      <!--            <el-menu-item style="float: right;" @click="loginout">退出</el-menu-item>-->
	  
      <br>
	 
       
	    <el-menu-item class="elemenu">
			 <template slot="title">欢迎，{{ username }}</template>
			  </el-menu-item>
        <el-menu-item @click="changePassword">修改密码</el-menu-item>
        
        <el-menu-item @click="loginout">注销</el-menu-item>
		
      
    </el-menu>
    <changePasswordDialog ref="changePasswordDialog"></changePasswordDialog>
	<el-dialog
  :visible.sync="dialogVisible"
  title="警告，出现霸凌事件"
  width="50%"
  @close="handleDialogClose">
  <div v-html="processMessage"></div>
  <el-button type="primary" style="font-weight: bold;" @click="view">点击查看相关视频</el-button>
  <span slot="footer" class="dialog-footer">
    <el-button type="primary" style="font-weight: bold;" @click="dialogVisible = false">关闭</el-button>
  </span>
</el-dialog>

<el-dialog
      :title="playerTitle"
      :visible.sync="showPlayer"
      width="50%">
      <easyPlayer ref="recordVideoPlayer" :videoUrl="videoUrl" :height="false"  ></easyPlayer>
    </el-dialog>
  </div>
</template>

<script>
import changePasswordDialog from '../components/dialog/changePassword.vue'
import userService from '../components/service/UserService'
import {Notification} from 'element-ui';
import easyPlayer from '../components/common/easyPlayer.vue'

export default {
  name: "UiHeader",
  components: {Notification, changePasswordDialog,easyPlayer},
  data() {
    return {
      alarmNotify: true,
      sseSource: null,
      username: userService.getUser().username,
      activeIndex: this.$route.path,
      editUser: true,
	  dialogVisible: false, // 控制对话框显示的布尔值
	  showPlayer:false,
	   playerTitle: '',
      videoUrl: '',
	  processMessage:'',
      serverMessage: `收到信息：<strong>设备编号：</strong> <i>34020000001320000002</i><br><strong>通道编号：</strong> <i>33010132001320000001</i><br><strong>报警描述：</strong> <i>识别到霸凌行为，请及时关注处理！</i><br><strong>报警时间：</strong> <i>2024年08月18日19点21分26秒</i><br><strong>报警视频流地址：</strong><br><i>http://49.232.189.216:8000/videos/49.232.189.216_1935_rtp_34020000001320000002_33010132001320000001/final_output-20240812-112710.mp4</i>`, // 存储从服务器接收到的消息
    };
  },
  created() {
    console.log(JSON.stringify(userService.getUser()))
    if (this.$route.path.startsWith("/channelList")) {
      this.activeIndex = "/deviceList"
    }
  },
  mounted() {
  if(userService.getUser().role.id !== 1 && userService.getUser().role.id !== 2){
	this.$message({
            showClose: true,
            message: '您没有权限访问网页',
            type: 'error'
			
          });
		this.$router.push('/login');  
	}
    window.addEventListener('beforeunload', e => this.beforeunloadHandler(e))
	//window.open(!!process.env.BASE_API ? process.env.BASE_API + "/doc.html" : "/doc.html");
    //this.alarmNotify = this.getAlarmSwitchStatus() === "true";
	this.alarmNotify="true";
    // TODO: 此处延迟连接 sse, 避免 sse 连接时 browserId 还未生成, 后续待优化
	
    setTimeout(() => {
      this.sseControl();
    }, 3000);
	
  },
  created() {
    console.log(JSON.stringify(userService.getUser()))
    if (this.$route.path.startsWith("/channelList")) {
      this.activeIndex = "/deviceList"
    }
  },
  methods: {
    view() {
      // logic to view a video
	  console.log(1);
      this.showPlayer = true;
      const regex = /https?:\/\/[^\s<]+/;
      const match = this.serverMessage.match(regex);
      if (match) {
        this.videoUrl = match[0]; // 提取到的 URL 保存到 videoUrl
      }
	  console.log(this.videoUrl);
	  //this.videoUrl='http://49.232.189.216:8000/videos/admin_zanyhat741@10.0.0.11_554_h264_ch1_main_av_stream/final_output-20240810-170414-1.mp4';
	  //this.videoUrl='https://www.w3schools.com/html/mov_bbb.mp4';
    },
    loginout() {
      this.$axios({
        method: 'get',
        url: "/api/user/logout"
      }).then((res) => {
        // 删除用户信息，回到登录页面
        userService.clearUserInfo()
        this.$router.push('/login');
        if (this.sseSource != null) {
          this.sseSource.close();
        }

      }).catch((error) => {
        console.error("登出失败")
        console.error(error)
      });
    },
	  
    changePassword() {
      this.$refs.changePasswordDialog.openDialog()
    },
    openDoc() {
      console.log(process.env.BASE_API)
      window.open(!!process.env.BASE_API ? process.env.BASE_API + "/doc.html" : "/doc.html")
    },
    beforeunloadHandler() {
      this.sseSource.close();
    },
    alarmNotifyChannge() {
      this.setAlarmSwitchStatus()
      this.sseControl()
    },
    sseControl() {
      let that = this;
      if (this.alarmNotify) {
        console.log("申请SSE推送API调用，浏览器ID: " + this.$browserId);
        this.sseSource = new EventSource('/debug/api/emit?browserId=' + this.$browserId);
        this.sseSource.addEventListener('message', function (evt) {
		that.serverMessage = evt.data;
        that.dialogVisible = true;
		const regex = /<strong>报警视频流地址：<\/strong><br><i>.*?<\/i>/g;
	
      that.processMessage = that.serverMessage.replace(regex, '');
		/*
          that.$notify({
            title: '报警信息',
            dangerouslyUseHTMLString: true,
            message: evt.data,
            type: 'warning',
            position: 'bottom-right',
            duration: 3000
          });
		  */
          console.log("收到信息：" + evt.data);
		  
        });
        this.sseSource.addEventListener('open', function (e) {
          console.log("SSE连接打开.");
        }, false);
		 
        this.sseSource.addEventListener('error', function (e) {
          if (e.target.readyState == EventSource.CLOSED) {
            console.log("SSE连接关闭");
			 
          } else {
            console.log(e.target.readyState);
          }
        }, false);
      } else {
        if (this.sseSource != null) {
          this.sseSource.removeEventListener('open', null);
          this.sseSource.removeEventListener('message', null);
          this.sseSource.removeEventListener('error', null);
          this.sseSource.close();
        }

      }
    },
    getAlarmSwitchStatus() {
      if (localStorage.getItem("alarmSwitchStatus") == null) {
        localStorage.setItem("alarmSwitchStatus", false);
      }
      return localStorage.getItem("alarmSwitchStatus");
    },
    setAlarmSwitchStatus() {
      localStorage.setItem("alarmSwitchStatus", this.alarmNotify);
    }
  },
  destroyed() {
    window.removeEventListener('beforeunload', e => this.beforeunloadHandler(e))
    if (this.sseSource != null) {
      this.sseSource.removeEventListener('open', null);
      this.sseSource.removeEventListener('message', null);
      this.sseSource.removeEventListener('error', null);
      this.sseSource.close();
    }
  },

}

</script>
<style>
#UiHeader{
	background-color:#CDF2D1;
}
#UiHeader .el-switch__label {
  color: white;
  
}


.el-menu--popup .el-menu-item .el-switch .el-switch__label {
  color: white !important;
}

#UiHeader .el-switch__label.is-active {
  color: #87CEEB
}
#UiHeader{
	width:150px;
	height:100%;
	 font-family:'Microsoft YaHei', sans-serif;
	font-weight: bold;
}
.elmenu{
	  width: 150px;
      height: 100vh;
      color: #fff;
      position: fixed;
}
#UiHeader .el-menu-item.is-active {
  color: #fff !important;
  background-color: #1890FF !important;
 
  
}
.menu-avatar {
  width: 100px; 
  height: 100px; 
  border-radius: 50%; 
 outline: none !important;
  border: none !important;
}
.el-menu-item:hover {
  background: #000 !important;
  color: #fff !important;
}
.events-button{
	background-color:#1890FF;
	color:#fff;
	border: none !important;
	 border-radius: 10%; 
	width:100px;
	height: 30px; 
	
}

</style>
