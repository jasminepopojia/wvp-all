<template>
  <div class="event-search">
    <h1>事件查询</h1>
	<div class="line" />
    <div class="content-wrapper">
      <div class="sidebar">
        <h2>筛选条件</h2>
        <div class="filter-group">
          <h4>时间</h4>
           <input type="text" id="time" v-model="filterTime" placeholder="请输入时间（格式：YYYY 或 YYYY-MM 或 YYYY-MM-DD 或 YYYY-MM-DD HH:mm:ss）" />
        </div>
        <div class="filter-group">
          <h4>地点</h4>
            <input type="text" id="location" v-model="filterLocation" placeholder="请输入地点" />
        </div>
        <div class="filter-group">
          <h4>摄像头编号</h4>
          <input type="text" id="other" placeholder="请输入摄像头编号" v-model="filterOther" />
        </div>
		<br>
        
         
        <div class="display-items">
		<div class="line" />
		<br>
		<div class="items">
          <h3>显示条目</h3>
		  
        <!--  <div>
            <input type="checkbox" id="preview" v-model="displayItems.preview" class="styled-checkbox" />
            <label for="preview">预览</label>
          </div> -->
		  <br>
          <div>
            <input type="checkbox" id="location" v-model="displayItems.location" class="styled-checkbox" />
            <label for="location">地点</label>
          </div>
		  <br>
          <div>
            <input type="checkbox" id="time" v-model="displayItems.time" class="styled-checkbox" />
            <label for="time">时间</label>
          </div>
		  <br>
          <div>
            <input type="checkbox" id="duration" v-model="displayItems.duration" class="styled-checkbox" />
            <label for="duration">处理情况</label>
          </div>
		  <br>
		  </div>
          <div class="select-all">
            <input type="checkbox" id="select-all" v-model="selectAll" style="top:8px;position:relative;" @change="toggleSelectAll" class="styled-checkbox" />
            <label for="select-all">全选</label>
          </div>
        </div>
      </div>
		
      <div class="main-content">
	    <h5>此筛选条件下共有{{filteredResults.length}}条结果</h5>
        <h2>查询结果</h2>
		<div class="sort-options">
      <select id="sort-by" v-model="sortBy">
          <option value="date">按时间排序</option>
           <option value="location">按地点排序</option>
       </select>
         </div>
        <div class="results">
          <div class="result-item" v-for="item in paginatedResults" :key="item.id">
           <!-- <img :src="item.image" alt="thumbnail" v-if="displayItems.preview" /> -->
            <div class="result-details">
			<div class="basic-info">
              <p>{{ item.alarmTime ? item.alarmTime :'未知时间' }}-{{ item.videoId }}</p>
              <p>摄像机编号 {{ item.deviceId ?item.deviceId:'未知设备编号' }}</p>
              <p v-if="displayItems.location">发生地点: {{ item.location ? item.location:'未知地点' }}</p>
              <p v-if="displayItems.time">发生时间: {{ formatDate(item.createTime) }}</p>
			</div>
              <div class="duration-and-button">
                <p v-if="displayItems.duration">{{ item.status === 1 ? '已处理' : '未处理' }}</p>
				<br>
				<br>
				<br>
				<br>
                <button @click="viewVideo(item.streamUrl)">查看视频</button>
              </div>
            </div>
          </div>
        </div>
        <div class="pagination">
          <button @click="prevPage" :disabled="currentPage === 1">上一页</button>
           <select v-model="currentPage" @change="goToPage(currentPage)">
    <option v-for="page in totalPages" :key="page" :value="page">
      {{ page }}
    </option>
  </select>
          <button @click="nextPage" :disabled="currentPage === totalPages">下一页</button>
        </div>
      </div>
    </div>
	<el-dialog
      :title="playerTitle"
      :visible.sync="showPlayer"
      width="50%">
      <easyPlayer ref="recordVideoPlayer" :videoUrl="videoUrl" :height="false"  ></easyPlayer>
    </el-dialog>
  </div>
</template>

<script>
import uiHeader from '../layout/UiHeader.vue'
import axios from "axios";
import easyPlayer from './common/easyPlayer.vue'
import moment  from 'moment'

export default {
components: {
    uiHeader,easyPlayer
  },
  data() {
    return {
      currentPage: 1,
      resultsPerPage: 3,
	   filterTime: '',
       filterLocation: '',
       filterOther: '',
	   loading: false,
	   showPlayer: false,
      playerTitle: '',
      videoUrl: '',
	  sortBy: 'date',
	   locations: [
      { value: '鼓楼', label: '鼓楼' },
      { value: '医务室', label: '医务室' },
      { value: '教室', label: '教室' },
	  { value: '走廊', label: '走廊' }
    ],
      displayItems: {
        preview: true,
        location: true,
        time: true,
        duration: true,
      },
      selectAll: true,
      results: [
        {
          id: "1",
          date: "2024-04-01",
          position: "教室",
          cameraId: "0007",
          time: "00:03:56",
          duration: "00:03:56",
          image: "classroom1.jpg",
        },
        {
          id: "2",
          date: "2024-04-01",
          position: "走廊",
          cameraId: "0002",
          time: "00:03:56",
          duration: "00:03:56",
          image: "corridor.jpg",
        },
        {
          id: "3",
          date: "2024-04-02",
          position: "教室",
          cameraId: "0011",
          time: "00:03:56",
          duration: "00:03:56",
          image: "classroom2.jpg",
        },
        {
          id: "4",
          date: "2024-04-03",
          position: "教室",
          cameraId: "0012",
          time: "00:03:56",
          duration: "00:03:56",
          image: "classroom3.jpg",
        },
        {
          id: "5",
          date: "2024-04-04",
          position: "教室",
          cameraId: "0013",
          time: "00:03:56",
          duration: "00:03:56",
          image: "classroom4.jpg",
        },
      ],
    };
  },
  computed: {
    totalPages() {
      return Math.ceil(this.filteredResults.length / this.resultsPerPage);
    },
	filteredResults() {
      return this.results.filter((item) => {
        const matchesTime = this.filterTime ? item.createTime.includes(this.filterTime) : true;
        const matchesLocation = this.filterLocation ? item.location === this.filterLocation : true;
        const matchesOther = this.filterOther ? item.deviceId === this.filterOther : true;
        return matchesTime && matchesLocation && matchesOther;
      });
    },
	 sortedResults() {
    let sorted = [...this.filteredResults];
    if (this.sortBy === 'date') {
      sorted.sort((a, b) => new Date(b.createTime) - new Date(a.createTime));
    } else if (this.sortBy === 'location') {
      sorted.sort((a, b) => {
        const locationA = a.location || ''; // 如果为空，设置为默认值空字符串
        const locationB = b.location || ''; // 如果为空，设置为默认值空字符串
        return locationA.localeCompare(locationB);
      });
    } else if (this.sortBy === 'duration') {
      sorted.sort((a, b) => {
        const statusA = a.status != null ? a.status.toString() : ''; // 确保转换为字符串
        const statusB = b.status != null ? b.status.toString() : ''; // 确保转换为字符串
        return statusA.localeCompare(statusB);
      });
    }
    return sorted;
  },
    paginatedResults() {
      const start = (this.currentPage - 1) * this.resultsPerPage;
      const end = start + this.resultsPerPage;
      return this.sortedResults.slice(start, end);
    },
  },
   mounted() {
    this.initData();
	 
  },
  methods: {
  initData: function () {
      this.getEventList();
    },
    formatDate(dateString) {
     const date = new Date(dateString);
      const options = { 
        year: 'numeric', 
        month: '2-digit', 
        day: '2-digit', 
        hour: '2-digit', 
        minute: '2-digit', 
        second: '2-digit'
      };
      // 不显示时区
      return date.toLocaleString('zh-CN', options);
    },
	 
    viewVideo(streamUrl) {
      // logic to view a video
	  
      this.showPlayer = true;
     this.videoUrl=streamUrl;
	 console.log(streamUrl);
	  
    },
	 getEventList: function () {
      this.$axios({
        method: 'get',
        url: `api/event/list`,
      }).then((res) => {
        console.log(res)
        if (res.data.code === 0) {
          this.results = res.data.data;
		  console.log(this.result);
        }
        this.loading = false;
      }).catch((error) => {
        console.log(error);
        this.loading = false;
      });
    },
    prevPage() {
      if (this.currentPage > 1) {
        this.currentPage--;
      }
    },
    nextPage() {
      if (this.currentPage < this.totalPages) {
        this.currentPage++;
      }
    },
    goToPage(page) {
      this.currentPage = page;
    },
    toggleSelectAll() {
      const value = this.selectAll;
      for (let key in this.displayItems) {
        this.displayItems[key] = value;
      }
    },
  },
};
</script>

<style scoped>
.event-search {
margin-left: 150px;
  display: flex;
  flex-direction: column;
  align-items: center;
   font-family:'Microsoft YaHei', sans-serif;
	font-weight: bold;
	background-color: #fff;
  width: 100%;
  height:100%;
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

h1 {
  text-align: left;
  width: 100%;
 
}
h2 {
  text-align: left;
  width: 100%;
  
}
h5 {
  text-align: left;
  width: 100%;
  
}
h3 {
  text-align: left;
  width: 100%;
  
}
h4 {
  text-align: left;
  width: 100%;
  
}

.content-wrapper {
  display: flex;
  width: 100%;
}

.sidebar {
  width: 250px;
  padding: 20px;
  background-color: #d4c4c4;
  display: flex;
  flex-direction: column;
  height:750px;
}

.main-content {
  flex-grow: 1;
  padding: 20px;
  background-color:#fff;
  position: relative;
}

.filter-group {
  margin-bottom: 20px;
  display: flex;
  flex-direction: column;
}

.filter-group label {
  margin-bottom: 5px;
}

.filter-group input {
  width: 100%;
  padding: 8px;
  box-sizing: border-box;
   font-family:'Microsoft YaHei', sans-serif;
}
.filter-group select {
  width: 100%;
  padding: 8px;
  box-sizing: border-box;
   font-family:'Microsoft YaHei', sans-serif;
}
button {
  background-color: #1890FF;
  color: white;
  border: none;
  padding: 10px 20px;
  cursor: pointer;
   font-family:'Microsoft YaHei', sans-serif;
	font-weight: bold;
}

.display-items {
  margin-top: 50px;
  position:relative;
  
}

.display-items h3 {
  margin-bottom: 10px;
}

.items div {
  display: flex;
  align-items: center;
}

.display-items input {
  margin-right: 15px;
}
.items{
top: 0px;
  left:20px;
  position:absolute;
  flex-direction: column;
}
.select-all {
  top: 20px;
  right:20px;
  position:absolute;
}
.sort-options{
    top: 50px;
	right:50px;
  box-sizing: border-box;
   font-family:'Microsoft YaHei', sans-serif;
   position: absolute;
   
}
.sort-options select{
	width:100px;
   height: 50px;
    font-family:'Microsoft YaHei', sans-serif;
}

.results {
  display: flex;
  flex-direction: column;
}

.result-item {
  display: flex;
  margin-bottom: 20px;
  border: 1px solid #ddd;
  padding: 10px;
  border-radius: 5px;
  position:relative;
  height: 150px;
   
  
}

.result-item img {
  width: 150px;
  height: 150px;
  object-fit: cover;
  margin-right: 20px;
}

.result-details {
  flex-grow: 1;
  display: flex;
  flex-direction: column;
  left: 0px;
    position:absolute;
	width:100%;
}
.basic-info{
	left: 20px;
    position:absolute;
}

.result-details h3 {
  margin: 0;
}

.result-details p {
  margin: 5px 0;
}

.duration-and-button {
  display: flex;
  justify-content: space-between;
  width:200px;
  align-items: center;
  margin-top: auto;
  right: 50px;
   position:absolute;
   flex-direction: column;
  
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 20px;
}

.pagination button {
  margin: 0 10px;
}

.pagination span {
  margin: 0 5px;
  cursor: pointer;
}

.pagination span.active {
  font-weight: bold;
  background-color: #1890FF; /* 设置高亮颜色 */
  color: white;
  border: 1px solid #1890FF; /* 可选：添加边框以增强对比 */
}
.styled-checkbox {
  position: relative;
  width: 20px;
  height: 20px;
  -webkit-appearance: none;
  -moz-appearance: none;
  appearance: none;
  border: 1px solid #ccc;
  border-radius: 3px;
  outline: none;
  cursor: pointer;
}

.styled-checkbox:checked {
  background-color: #1890FF;
}

.styled-checkbox:checked::before {
  content: '✓';
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: white;
  font-size: 14px;
}
.pagination select {
  font-size: 20px; /* 更大的字体 */
  font-weight: bold; /* 加粗字体 */
  padding: 10px 15px; /* 更大的内边距，使下拉框更大 */
  border: 3px solid #1890FF; /* 更加粗的边框 */
  border-radius: 5px; /* 使边框圆角 */
  
    font-family:'Microsoft YaHei', sans-serif;
}

.pagination-select option {
  font-weight: bold; /* 选项字体加粗 */
  width:100px;
   height: 50px;
    font-family:'Microsoft YaHei', sans-serif;
}
</style>
