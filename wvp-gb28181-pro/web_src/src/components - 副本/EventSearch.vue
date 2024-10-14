<template>
  <div class="div1">
    <div>
	<h1>事件查询</h1>
	</div>
	<div class="div3">
	<div class="div4">
		<h1>筛选条件</h1>
		
		 <el-form :model="ruleForm"  ref="ruleForm" label-position="top" label-width="100px" class="demo-ruleForm">
        <el-form-item  label="时间" prop="time">
          <el-date-picker v-model="ruleForm.time" type="datetime" placeholder="选择日期时间" style="width: 250px;" value-format="yyyy-MM-dd HH:mm:ss">
    </el-date-picker>
        </el-form-item>
        <el-form-item label="地点" prop="position">
          <el-select
      v-model="ruleForm.position"
      placeholder="请选择地点"
      style="width: 250px;"
      filterable
      clearable
      @blur="getCurVal"
    >
      <el-option
        v-for="item in options"
        :key="item.value"
        :label="item.label"
        :value="item.value"
      >
      </el-option>
    </el-select>
        </el-form-item>
        <el-form-item label="其它条件" prop="other">
          <el-input v-model="ruleForm.other" style="width: 250px;" clearable></el-input>
        </el-form-item>
      
		
        <el-form-item>
          <el-button type="primary" @click="submitForm"  class="set-other-btn">查询</el-button>
       
        </el-form-item>
      </el-form>
	  </div>
	<div>
	<h3>显示条目</h3>
	<a v-on:click="allselect">全选</a>
	<p></p>
	  <el-checkbox v-model="showColumn.comeraid"  >编号</el-checkbox>
	  <p></p>
      <el-checkbox v-model="showColumn.position" >位置</el-checkbox>
	  <p></p>
      <el-checkbox v-model="showColumn.time" >时间</el-checkbox>
	  <p></p>
	  <el-checkbox v-model="showColumn.len" >时长</el-checkbox>
	  
	 
	</div>
	</div>
    <!--设备列表-->
	<div class="div2">
	 <div class="page-title">
	 <h5>查找到以下{{eventList.length}}条记录</h5>
        <div >查询结果</div>
      </div>
	  <p></p>
    <el-table :data="eventList" style="width: 100%" :height="winHeight">
	
	   
	  <el-table-column prop="videopath" label="视频" show-overflow-tooltip>
		 <template slot-scope="scope">
              <div class="video">
                <el-dialog
                  title="播放热点视频"
                  width="72%"
                  append-to-body
                  top="20px"
                  :visible.sync="dialogVisible"
                  @closed="closeDialog"
                >
                  <h3>{{ videopath }}</h3>

                  <video
                    width="100%"
                    autoplay="autoplay"
                    :src="playvideo"
                    :poster="playvideo"
                    controls="controls"

                    id="video"
                    preload
                  ></video>
                </el-dialog>
                <!-- 页面table显示的video标签 -->
                <video
                  :src="scope.row.videopath"
                  width="100"
                  preload
                ></video>
                <i
                  class="el-icon-video-play playIcon"
                  @click="handleCheck(scope.row)"
                ></i>
              </div>
            </template>
	  </el-table-column>
      <el-table-column prop="comeraid" label="摄像机编号" width="100" v-if="showColumn.comeraid">
      </el-table-column>
      <el-table-column prop="position" label="发生地点"  width="100" v-if="showColumn.position" sortable>
      </el-table-column>
	  <el-table-column prop="len" label="时长" width="100" v-if="showColumn.len" sortable>
                        </el-table-column>
      <el-table-column prop="time" label="发生时间" :formatter="formatDate"  v-if="showColumn.time" sortable>
                        </el-table-column> 
				
    </el-table>
	
    <el-pagination
      style="float: right"
      @size-change="handleSizeChange"
      @current-change="currentChange"
      :current-page="currentPage"
      :page-size="count"
      :page-sizes="[15, 25, 35, 50]"
      layout="total, sizes, prev, pager, next"
      :total="total">
    </el-pagination>
    <el-dialog
      :title="playerTitle"
      :visible.sync="showPlayer"
      width="50%">
      <easyPlayer ref="recordVideoPlayer" :videoUrl="videoUrl" :height="false"  ></easyPlayer>
    </el-dialog>
	</div>
  </div>
</template>

<script>
import uiHeader from '../layout/UiHeader.vue'
import MediaServer from './service/MediaServer'
import easyPlayer from './common/easyPlayer.vue'
import moment  from 'moment'
import axios from "axios";
import { videoPlayer } from 'vue-video-player'
import 'video.js/dist/video-js.css'

export default {
  name: 'app',
  components: {
    uiHeader,easyPlayer,videoPlayer
  },
  data() {
    return {
	  isShowColumn: false,
	  value:"",
      search: '',
      showPlayer: false,
      playerTitle: '',
      videoUrl: '',
	  ruleForm: {
        time: '',
        position: '',
        other: '',
        
      },
	  options: [
        {
          value: "选项1",
          label: "操场",
        },
        {
          value: "选项2",
          label: "医务室",
        },
        {
          value: "选项3",
          label: "教师",
        },
        {
          value: "选项4",
          label: "图书馆",
        },
        {
          value: "选项5",
          label: "活动室",
        },
      ],
	   checkList: {
		 comeraid: true,
        position: true,
        time: true,
        len: true,
	   },
      showColumn: {
	    
        comeraid: true,
        position: true,
        time: true,
		len:true,
      },


      playerStyle: {
          "margin": "auto",
          "margin-bottom": "20px",
          "width": window.innerWidth/2 + "px",
          "height": this.winHeight/2 + "px",
      },
      mediaServerList: [], // 滅体节点列表
      mediaServerId: "", // 媒体服务
      mediaServerPath: null, // 媒体服务地址
      eventList: [
	  {
	  
	  videopath:"https://cdn.theguardian.tv/webM/2015/07/20/150716YesMen_synd_768k_vp8.webm",
	  comeraid:"2",
	  position:"操场",
	  len:'1:1:1',
	  time:'2024/3/13',
	  },
	  {
	  
	  videopath:"",
	  comeraid:"2",
	  position:"医务室",
	  len:'2:2:2',
	  time:'2024/5/14',
	  },
	  ], // 事件列表
      chooseRecord: null, // 媒体服务
	  
	  dialogVisible: false, // 视频播放弹窗
      playvideo: null, // 存储用户点击的视频播放链接
      playvideoName: null, // 存储正在播放视频的名称
      updateLooper: 0, //数据刷新轮训标志
	  
      winHeight: window.innerHeight - 250,
      currentPage: 1,
      count: 15,
      total: 0,
      loading: false,
      mediaServerObj: new MediaServer(),

    };
  },
   watch: {
    // 监听复选框配置列所有的变化
    checkList: {
      handler: function (newnew, oldold) {
        // console.log(newnew); 
        this.showColumn = newnew;
        // 这里需要让表格重新绘制一下，否则会产生固定列错位的情况
        this.$nextTick(() => {
          this.$refs.table.doLayout();
        });
      },
      deep: true,
      immediate: true
    },
  },

  computed: {},
  mounted() {
    this.initData();
	 
  },
  destroyed() {
      this.$destroy('recordVideoPlayer');
  },
  methods: {
    initData: function () {
      // 获取媒体节点列表
      this.getMediaServerList();
      this.getEventList();
    },
	 getCurVal(val) {
      console.log(val);
      this.value = val.target.value;
    },
	formatDate(row, column) {
          // 获取单元格数据
          let data = row[column.property];
          if(data == null) {
              return null;
          }
          let dt = new Date(data);
          return dt.getFullYear() + '-' + (dt.getMonth() + 1) + '-' + dt.getDate()+' '+dt.getHours()+':' + dt.getMinutes() + ':' + dt.getSeconds();
        },
		
		
	handleCheck(row) {
      this.playvideo = row.videopath; // 存储用户点击的视频播放链接
      this.playvideoName = row.videopath; // 存储用户点击的视频播放链接
      this.dialogVisible = true;
    },
	closeDialog () {
      // 关闭弹框
      this.dialogVisible = false;
      this.playvideo = ''; // 清空数据 关闭视频播放
    },
    currentChange: function (val) {
      this.currentPage = val;
      this.getEventList();
    },
    handleSizeChange: function (val) {
      this.count = val;
      this.getEventList();
    },
    getMediaServerList: function () {
      let that = this;
      that.mediaServerObj.getOnlineMediaServerList((data) => {
        that.mediaServerList = data.data;
      })
    },
	submitForm:function() {
		var data = this.ruleForm;
		this.$axios({
        method: 'get',
        url: `/api/event/listsearch`,
        params: data
        
      }).then((res) => {
        console.log(res)
        if (res.data.code === 0) {
          this.total = res.data.data.total;
          this.eventList = res.data.data.list;
        }
        this.loading = false;
      }).catch((error) => {
        console.log(error);
        this.loading = false;
      });
    },
    setMediaServerPath: function (serverId) {
      let that = this;
      let i;
      for (i = 0; i < that.mediaServerList.length; i++) {
        if (serverId === that.mediaServerList[i].id) {
          break;
        }
      }
      let port = that.mediaServerList[i].httpPort;
      if (location.protocol === "https:" && that.mediaServerList[i].httpSSlPort) {
        port = that.mediaServerList[i].httpSSlPort
      }
      that.mediaServerPath = location.protocol + "//" + that.mediaServerList[i].streamIp + ":" + port
      console.log(that.mediaServerPath)
    },
    getEventList: function () {
      this.$axios({
        method: 'get',
        url: `/api/event/list`,
        params: {
          
          page: this.currentPage,
          count: this.count,
        }
      }).then((res) => {
        console.log(res)
        if (res.data.code === 0) {
          this.total = res.data.data.total;
          this.eventList = res.data.data.eventList;
        }
        this.loading = false;
      }).catch((error) => {
        console.log(error);
        this.loading = false;
      });
    },
    
    downloadFile(file){
      console.log(file)
      this.$axios({
        method: 'get',
        url: `/api/cloud/record/play/path`,
        params: {
          recordId: file.id,
        }
      }).then((res) => {
        console.log(res)
        const link = document.createElement('a');
        link.target = "_blank";
        if (res.data.code === 0) {
          if (location.protocol === "https:") {
            link.href = res.data.data.httpsPath + "&save_name=" + file.fileName;
          }else {
            link.href = res.data.data.httpPath + "&save_name=" + file.fileName;
          }
          link.click();
        }
      }).catch((error) => {
        console.log(error);
      });
    },
    deleteRecord() {
      // TODO
      let that = this;
      this.$axios({
        method: 'delete',
        url: `/record_proxy/api/record/delete`,
        params: {
          page: that.currentPage,
          count: that.count
        }
      }).then(function (res) {
        console.log(res)
        if (res.data.code === 0) {
          that.total = res.data.data.total;
          that.recordList = res.data.data.list;
        }
      }).catch(function (error) {
        console.log(error);
      });
    },
	 handleClick(row) {
      console.log(row);
    },
    showColumnOption() {
      this.isShowColumn = true;
    },
    saveColumn() {
      localStorage.setItem("columnSet",JSON.stringify(this.checkList))
      this.isShowColumn = false;
    },


    formatTime(time) {
      const h = parseInt(time / 3600 / 1000)
      const minute = parseInt((time - h * 3600 * 1000) / 60 / 1000)
      let second = Math.ceil((time - h * 3600 * 1000 - minute * 60 * 1000) / 1000)
      if (second < 0) {
        second = 0;
      }
      return (h > 0 ? h + `小时` : '') + (minute > 0 ? minute + '分' : '') + (second > 0 ? second + '秒' : '')
    },
    formatTimeStamp(time) {
      return moment.unix(time/1000).format('yyyy-MM-DD HH:mm:ss')
    },
	allselect() {
      this.checkList= {
		 comeraid: true,
        position: true,
        time: true,
        len: true,
	   };
    
    }

  }
};
</script>

<style>
.page-title{
	
}
.div1{
	width:100%;
	height:100%;
	font-family:Simsun;
	font-weight: bold;
	margin-left: 150px;
	background-color: #fff;
}
.div2{
    
	margin-left: 400px;
	width:65%;
	background-color: #fff;
	position:absolute;
}
.div3{
    
	width: 400px;
	position:absolute;
	
}
.div4{
	height:80%;
	margin-top: 50px;
	
}
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s;
}
.fade-enter,
.fade-leave-to {
  opacity: 0;
}

.el-checkbox{
  
  display:block;
  width: 400px;
}
.set-other-btn{
  background-color: #40A44C;
  border-color: #FE83C6;
  width: 190px;
}
.set-other-btn:hover{
  background-color:  #40A44C;
  border-color:  #FE83C6;
}
.a{
	right:0;
	top:0;
}
.el-form-item__label{
  text-align:left;
  float: none;
  word-break: break-word;
}

</style>