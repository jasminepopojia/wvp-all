<template>
  <div class="history-monitoring">
    <header class="header">
      <span @click="goBack" class="header-link">
        <i class="el-icon-time"></i> 历史监控
      </span>
    </header>
	<div class="line" />
    <div class="filters">
	<div class="filter-header">
      <label>点击增加筛选条件</label>
	 
      <div class="filter-options">
        <button @click="openDialog('date')">日期</button>
        <button @click="openDialog('position')">地点</button>
        <button @click="openDialog('camera')">摄像头</button>
      </div>
	  </div>
	   <el-dialog :visible.sync="showDialog">
      <span>请输入内容:</span>
      <el-input v-model="inputValue" placeholder="请输入内容"></el-input>
      <span slot="footer" class="dialog-footer">
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="confirm">确定</el-button>
      </span>
    </el-dialog>
	<div class="line" />
      <div class="selected-filters">
	  <label>已有筛选条件</label>
	  
		 <div class="filter-tag" v-for="(value, key) in filters" :key="key">
          {{ key }}
          <button @click="removeFilter(key)">×</button>
        </div>
      </div>
     
    </div>
	<div class="line" />
	 <div class="filter-datetime">
	 <br/>
        <label>选项</label>
		<p></p>
        <div class="radio-group">
          <label><input type="radio" value="按时间" v-model="filterOption">按时间排序</label>
		  <p></p>
          <label><input type="radio" value="按地点" v-model="filterOption">按地点排序</label>
		  <p></p>
          <label><input type="radio" value="按机位" v-model="filterOption">按机位排序</label>
        </div>
		<br/>
		<br/>
        <label>日期范围</label>
		<p></p>
        <input class="dates" type="date" v-model="startDate">
        <input class="dates" type="date" v-model="endDate">
		<p></p>
        <label>时间范围</label>
		<p></p>
        <input class="dates" type="time" v-model="startTime">
        <input class="dates" type="time" v-model="endTime">
		<p></p>
        <button class="query-button" @click="query">查询</button>
      </div>
    <div class="video-results">
      <div class="video-cards">
        <div class="video-card" v-for="video in paginatedVideos" :key="video.id">
          <img :src="video.thumbnail" alt="Video Thumbnail">
          <div class="video-info">
            <span>{{ video.date }}</span>
            <span>{{ video.position }}</span>
			<span>{{ video.camera }}</span>
            <button @click="viewVideo(video.id)" class="viewbutton">查看</button>
          </div>
        </div>
      </div>
	  </br>
	  </br>
	  </br>
      <button class="morebutton" v-if="showLoadMore" @click="loadMore">更多</button>
    </div>
  </div>
</template>

<script>
import uiHeader from '../layout/UiHeader.vue'
import axios from "axios";
export default {
	components: {
    uiHeader
  },
  data() {
    return {
      filters: {'操场':'position','教室':'position'},
      filterOption: '按时间',
      startDate: '2024-04-01',
      endDate: '',
      startTime: '00:00',
      endTime: '22:00',
	  showDialog: false,
	  buttonValue: '',
      inputValue: '',
	  videoUrl: '',
	  showPlayer: false,
      playerTitle: '',
	   playerStyle: {
          "margin": "auto",
          "margin-bottom": "20px",
          "width": window.innerWidth/2 + "px",
          "height": this.winHeight/2 + "px",
      },
	  loading: false,
      sortOption: '时间排序',
      videos: [
        { id: 1,camera: '0002', thumbnail: 'thumbnail1.jpg', date: '2024-04-02', position: '操场' },
        { id: 2,camera: '0001', thumbnail: 'thumbnail2.jpg', date: '2024-04-01', position: '教室' },
        { id: 3,camera: '0001', thumbnail: 'thumbnail3.jpg', date: '2024-04-01', position: '教室' },
        { id: 4,camera: '0001', thumbnail: 'thumbnail4.jpg', date: '2024-04-01', position: '教室' },
        { id: 5,camera: '0001', thumbnail: 'thumbnail5.jpg', date: '2024-04-01', position: '教室' },
        { id: 6,camera: '0001', thumbnail: 'thumbnail6.jpg', date: '2024-04-01', position: '教室' },
        { id: 7,camera: '0001', thumbnail: 'thumbnail7.jpg', date: '2024-04-01', position: '教室' },
        { id: 8,camera: '0001', thumbnail: 'thumbnail8.jpg', date: '2024-04-01', position: '教室' },
		{ id: 9,camera: '0003', thumbnail: 'thumbnail1.jpg', date: '2024-04-03', position: '操场' },
		
      ],
	   currentPage: 1,
      itemsPerPage: 8
    };
  },
   computed: {
      filteredVideos() {
      if (Object.keys(this.filters).length === 0) {
        return this.videos;
      }

      return this.videos.filter(video => {
        return Object.entries(video).some(([key, value]) => {
          return this.filters[value] && this.filters[value] === key;
        });
      });
  },
  
   sortedVideos() {
      let videos = this.filteredVideos;

      switch (this.filterOption) {
        case '按时间':
          videos = videos.slice().sort((a, b) => new Date(b.date) - new Date(a.date));
          break;
        case '按地点':
          videos = videos.slice().sort((a, b) => b.position.localeCompare(a.position));
          break;
        case '按机位':
          videos = videos.slice().sort((a, b) => b.camera.localeCompare(a.camera));
          break;
      }

      return videos;
    },
	 paginatedVideos() {
      const end = this.currentPage * this.itemsPerPage;
      return this.sortedVideos.slice(0, end);
    },
    showLoadMore() {
      return this.currentPage * this.itemsPerPage < this.sortedVideos.length;
    }
  },
   mounted() {
    this.initData();
  },
  methods: {
  initData: function () {
      // 获取影像列表
      this.getHistoryList();
    },
    goBack() {
      // logic to go back
    },
	 openDialog(value) {
      this.buttonValue = value;
      this.showDialog = true;
    },
	confirm() {
      console.log(this.inputValue);
	   this.$set(this.filters, this.inputValue, this.buttonValue);
      this.showDialog = false;
	  this.inputValue = '';
    },
	 getHistoryList: function () {
      this.$axios({
        method: 'get',
        url: `/api/history/list`,
        params: {
			SortOption:this.filterOption
		}
      }).then((res) => {
        console.log(res)
        if (res.data.code === 0) {
          this.videos = res.data.data.list;
        }
        this.loading = false;
      }).catch((error) => {
        console.log(error);
        this.loading = false;
      });
    },
	 removeFilter(key) {
      this.$delete(this.filters, key);
    },
    query() {
      this.$axios({
        method: 'get',
        url: `/api/history/searchlist`,
        params: {
         
          startTime: this.startTime,
          endTime: this.endTime,
		  startDate: this.startDate,
		  endDate: this.endDate
         
        }
      }).then((res) => {
        console.log(res)
        if (res.data.code === 0) {
          
          this.videos = res.data.data.list;
        }
        this.loading = false;
      }).catch((error) => {
        console.log(error);
        this.loading = false;
      });
    },
    viewVideo(videoId) {
      // logic to view a video
    },
    loadMore() {
      // logic to load more videos
	  this.currentPage++;
    }
  }
};
</script>

<style scoped>
.history-monitoring {
    width:100%;
	height:100%;
  font-family:Simsun;
  font-weight: bold;
  margin-left: 150px;
  background-color: #fff;
}

.header {
  display: flex;
  align-items: center;
  padding: 10px;
  
}
.header-link {
  display: flex;
  align-items: center;
  cursor: pointer;
  font-size: 16px;
}

.filters {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-left: 100px;
}
.filter-datetime{
	width: 300px;
	height: 500px;
	position:absolute;
	 text-align: left;
	 padding-left: 50px;
	 background-color: #e0e0e0;
	  margin-left: 100px;
	  position: absolute;
	
}

.selected-filters,
.filter-datetime {
  
  gap: 20px;
  
}
.filter-header {
  display: flex;
  align-items: center;
}
.filter-header label {
  margin-right: 20px;
}
.filter-options{
	
  display: flex;
  gap: 30px;
}
.filter-options button{
	background-color: #fff;
	font-family:Simsun;
  font-weight: bold;
  width: 75px;
  height:35px;
  cursor: pointer;
}
.selected-filters {
  /* Add your styles here */
  display: flex;
  flex-wrap: nowrap;
  gap: 10px;
  overflow-x: auto;
}
.selected-filters label{
	font-size:20px;
}
.filter-tag {
   background-color: #fff;
  border-radius: 5px;
  padding: 20px 40px;
  display: flex;
  align-items: center;
  white-space: nowrap;
}
.filter-tag button {
  margin-left: 5px;
  background: none;
  border: none;
  cursor: pointer;
  font-size: 16px;
  color: #888;
}
.query-button {
 font-family:Simsun;
  font-weight: bold;
   display: block;
  margin: 20px auto;
  padding: 10px 30px;
  border-radius: 25px;
  background-color: gray;
  color: white;
  border: none;
  cursor: pointer;
}
.radio-group {
  
  gap: 10px;
}

.video-results {
  margin-top: 20px;
  margin-left:600px;
  
  
}

.sort-dropdown {
  margin-bottom: 10px;
}

.video-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 10px;
}

.video-card {
  border: 1px solid #ccc;
  padding: 10px;
  text-align: center;
}

.video-card img {
  width: 100%;
  height: auto;
}
.dates{
	width:125px;
	height:25px;
}
.video-info {
  margin-top: 10px;
  display: flex;
  flex-direction: column;
  gap: 5px;
}
.morebutton{
	background-color: #fff;
	font-family:Simsun;
    width: 100px;
    height:50px;
	cursor: pointer;
}
.viewbutton{
background-color: #fff;
	font-family:Simsun;
	cursor: pointer;
}
.line{
float:right;
width: 100%;
height: 1px;
margin-top: -0.5em;
background:#d4c4c4;
position: relative;
text-align: center;
}
</style>