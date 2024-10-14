<template>
  <div class="event-list">
    <div class="content">
      <div class="filter">
        <button @click="selectFilter('alarmTime')" :class="{'selected': selectedFilter === 'alarmTime'}">时间</button>
        <button @click="selectFilter('location')" :class="{'selected': selectedFilter === 'location'}">区域</button>
      </div>
     <div class="event-group" v-for="(group, key) in groupedEvents" :key="key">
	  <div class="group-header">
          
          <span>{{ key ? formatGroupHeader(key):'未知' }}</span>
		  <hr>
        </div>
        <div class="event-grid">
          <div v-for="(event, idx) in group" :key="event.id" class="event">
           
            <div class="details">
			  <p>{{ idx + 1 }}</p>
              <p>更新时间:{{ formatDate(event.updateTime) }}</p>
			  <p>报警时间:{{ event.alarmTime? event.alarmTime:'未知' }}</p>
              <p>地点：{{ event.location ? event.location : '无区域信息' }}/摄像头id:{{ event.deviceId ? event.deviceId : '暂无id信息'}} </p>
              <button @click="viewVideo(event.streamUrl)" class="event-button">查看</button>
            </div>
          </div>
        </div>
      </div>
    </div>
	<el-dialog
      :title="playerTitle"
      :visible.sync="showPlayer"
      width="70%">
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
	  showPlayer: false,
      playerTitle: '',
      videoUrl: '',
	  loading: false,
	  showDialog: false,
      selectedFilter: 'alarmTime',
      events: [
        { id: 1, date: '2024-04-01', position: '教室', camera:'0001',image: 'image1.jpg' },
        { id: 2, date: '2024-04-01', position: '图书馆',camera:'0001' ,image: 'image2.jpg' },
        { id: 3, date: '2024-04-01', position: '操场',camera:'0001' ,image: 'image3.jpg' },
        { id: 4, date: '2024-04-01', position: '教室',camera:'0001' ,image: 'image4.jpg' },
        { id: 5, date: '2024-04-01', position: '教室',camera:'0001' ,image: 'image5.jpg' },
        { id: 6, date: '2024-03-31', position: '教室',camera:'0001' ,image: 'image6.jpg' },
        { id: 7, date: '2024-03-31', position: '教室',camera:'0001' ,image: 'image7.jpg' },
        { id: 8, date: '2024-03-31', position: '教室',camera:'0001' ,image: 'image8.jpg' }
      ]
    };
  },
  computed: {
    sortedEvents() {
      return this.events.slice().sort((a, b) => {
        if (this.selectedFilter === 'alarmTime') {
          return new Date(b.alarmTime) - new Date(a.alarmTime);
        } else if (this.selectedFilter === 'location') {
         const locationA = a.location || '';
          const locationB = b.location || '';
          return locationB.localeCompare(locationA);
        }
      });

  },
   groupedEvents() {
      const groups = {};
      const key = this.selectedFilter;
      this.sortedEvents.forEach(event => {
        if (!groups[event[key]]) {
          groups[event[key]] = [];
        }
        groups[event[key]].push(event);
      });
      return groups;
    }
  },
  mounted() {
    this.initData();
	 
  },
  destroyed() {
      this.$destroy('recordVideoPlayer');
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
	formatGroupHeader(key) {
      // 当 selectedFilter 是 'updateTime' 时格式化日期
      if (this.selectedFilter === 'updateTime') {
        return moment(key).format('YYYY-MM-DD HH:mm:ss');
      }
      return key; // 其他情况下直接返回 key
    },
	 getEventList: function () {
      this.$axios({
        method: 'get',
        url: `api/event/list`,
       
      }).then((res) => {
        console.log(res)
        if (res.data.code === 0) {
          this.total = res.data.data.total;
          this.events = res.data.data;
		  console.log(this.events);
        }
        this.loading = false;
      }).catch((error) => {
        console.log(error);
        this.loading = false;
      });
    },
    selectFilter(filter) {
      this.selectedFilter = filter;
    },
	viewVideo(streamUrl) {
      // logic to view a video
	 
      this.showPlayer = true;
     this.videoUrl=streamUrl;
	 console.log(streamUrl);
	  
    },
  }
};
</script>

<style scoped>
body {
  display: flex;
  margin: 0;
  
}
.event-list{
	margin-left: 150px;
	background-color:#fff;
	 font-family:'Microsoft YaHei', sans-serif;
	font-weight: bold;
}


.profile {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  margin-bottom: 20px;
}

nav ul {
  list-style-type: none;
  padding: 0;
}

nav ul li {
  margin: 10px 0;
}

.settings ul {
  list-style-type: none;
  padding: 0;
  position: absolute;
  bottom: 20px;
}

.settings ul li {
  margin: 10px 0;
}

.content {
  flex: 1;
  padding: 20px;
}

.filter {
  display: flex;
  margin-bottom: 20px;
}

.filter button {
 padding: 10px 20px;
  border: 1px solid #ccc;
  background-color: white;
  cursor: pointer;
  border-radius: 0;
  width: 100px; /* 设置按钮宽度 */
  text-align: center;
   font-family:'Microsoft YaHei', sans-serif;
}

.filter button:first-child {
  border-top-left-radius: 5px;
  border-bottom-left-radius: 5px;
}

.filter button:last-child {
  border-top-right-radius: 5px;
  border-bottom-right-radius: 5px;
}

.filter button.selected {
  background-color: #1890FF;
  color: white;
}

.event-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 10px;
}

.event {
  border: 1px solid #ccc;
  padding: 10px;
  width:250px;
}

.event img {
  width: 100%;
  height: auto;
}
.event-button{
	background-color:#fff;
	width: 200px;
	
}
.group-header {
  display: flex;
  align-items: center;
}

.group-header hr {
  flex: 1;
  border: 0;
  border-top: 1px solid #ccc;
}

.group-header span {
  margin-left: 10px;
  font-weight: bold;
  background-color: white;
  padding: 0 10px;
}

.details {
  text-align: center;
}

.details p {
  margin: 5px 0;
}

.details button {
  padding: 5px 10px;
  cursor: pointer;
}
</style>

