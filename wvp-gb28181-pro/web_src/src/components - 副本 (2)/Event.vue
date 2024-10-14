<template>
  <div class="event-list">
    <div class="content">
      <div class="filter">
        <button @click="selectFilter('date')" :class="{'selected': selectedFilter === 'date'}">时间</button>
        <button @click="selectFilter('position')" :class="{'selected': selectedFilter === 'position'}">区域</button>
      </div>
     <div class="event-group" v-for="(group, key) in groupedEvents" :key="key">
	  <div class="group-header">
          
          <span>{{ key }}</span>
		  <hr>
        </div>
        <div class="event-grid">
          <div v-for="(event, idx) in group" :key="event.id" class="event">
            <img :src="event.image" alt="Event Image">
            <div class="details">
              <p>{{ event.date }}</p>
              <p>{{ event.position }}/{{ event.camera }}         {{ idx + 1 }}</p>
              <button @click="viewVideo(event.id)" class="event-button">查看</button>
            </div>
          </div>
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
	  showPlayer: false,
      playerTitle: '',
      videoUrl: '',
	  loading: false,
	  showDialog: false,
      selectedFilter: 'date',
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
        if (this.selectedFilter === 'date') {
          return b.date.localeCompare(a.date);
        } else if (this.selectedFilter === 'position') {
          return b.position.localeCompare(a.position);
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
 
  methods: {
   initData: function () {
      this.getEventList();
    },
	 getEventList: function () {
      this.$axios({
        method: 'get',
        url: `/api/event/list`,
       
      }).then((res) => {
        console.log(res)
        if (res.data.code === 0) {
          this.total = res.data.data.total;
          this.events = res.data.data.eventList;
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
	viewVideo(videoId) {
      // logic to view a video
	  console.log(videoId)
      this.showPlayer = true;
      this.$axios({
        method: 'get',
        url: `/api/event/play/path`,
        params: {
          videoId: videoId,
        }
      }).then((res) => {
        console.log(res)
        if (res.data.code === 0) {
          if (location.protocol === "https:") {
            this.videoUrl = res.data.data.httpsPath;
          }else {
            this.videoUrl = res.data.data.httpPath;
          }
          console.log(222 )
          console.log(this.videoUrl )
        }
      }).catch((error) => {
        console.log(error);
      });
	  
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
	font-family:Simsun;
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
  font-family:Simsun;
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
  background-color: green;
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

