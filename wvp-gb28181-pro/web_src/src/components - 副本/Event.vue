<template>
  <div class="event-list-container">
    
    <main class="content">
      <header class="search-bar">
        <label for="time">时间</label>
        <input type="text" id="time">
        <label for="area">区域</label>
        <input type="text" id="area">
      </header>
      <section class="events">
        <div class="event-card" v-for="event in events" :key="event.id">
          <img :src="event.image" alt="Event Image">
          <div class="event-info">
            <span>{{ event.date }}</span>
            <span>{{ event.location }}</span>
            <span>{{ event.id }}</span>
            <button @click="viewEvent(event.id)">查看</button>
          </div>
        </div>
      </section>
    </main>
  </div>
</template>

<script>
export default {
  name: 'EventList',
  data() {
    return {
      events: [
        // 示例事件数据
        { date: '2024-04-01', location: '教室', camera:'0001',image: 'path-to-image1' },
        { date: '2024-04-01', location: '图书馆',camera:'0002', image: 'path-to-image2' },
		{ date: '2024-04-01', location: '教室', camera:'0001',image: 'path-to-image1' },
        { date: '2024-04-01', location: '图书馆',camera:'0002', image: 'path-to-image2' },
		{ date: '2024-04-01', location: '教室', camera:'0001',image: 'path-to-image1' },
        { date: '2024-04-01', location: '图书馆',camera:'0002', image: 'path-to-image2' },
		{ date: '2024-04-01', location: '教室', camera:'0001',image: 'path-to-image1' },
        { date: '2024-04-01', location: '图书馆',camera:'0002', image: 'path-to-image2' },
		{ date: '2024-04-01', location: '教室', camera:'0001',image: 'path-to-image1' },
        { date: '2024-04-01', location: '图书馆',camera:'0002', image: 'path-to-image2' },
		{ date: '2024-04-01', location: '教室', camera:'0001',image: 'path-to-image1' },
        { date: '2024-04-01', location: '图书馆',camera:'0002', image: 'path-to-image2' },
		{ date: '2024-04-01', location: '教室', camera:'0001',image: 'path-to-image1' },
        { date: '2024-04-01', location: '图书馆',camera:'0002', image: 'path-to-image2' },
		{ date: '2024-04-01', location: '教室', camera:'0001',image: 'path-to-image1' },
        { date: '2024-04-01', location: '图书馆',camera:'0002', image: 'path-to-image2' },
        // 更多事件...
      ]
    };
  },
  methods: {
    viewEvent(id) {
      // 查看事件详情的逻辑
      console.log('查看事件', id);
    }
  }
}
</script>

<style scoped>
.event-list-container {
  display: flex;
  height: 100vh;
}

.sidebar {
  width: 200px;
  background-color: #e0f7e9;
  padding: 20px;
}

.sidebar ul {
  list-style: none;
  padding: 0;
}

.sidebar ul li {
  margin: 20px 0;
  text-align: center;
}

.sidebar ul li.active {
  background-color: #9ede73;
  padding: 10px;
  border-radius: 5px;
}

.content {
  flex: 1;
  padding: 20px;
}

.search-bar {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.search-bar label {
  margin-right: 10px;
}

.search-bar input {
  margin-right: 20px;
  padding: 5px;
}

.events {
  display: flex;
  flex-wrap: wrap;
}

.event-card {
  width: calc(33.333% - 20px);
  margin: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.event-card img {
  width: 100%;
  height: 150px;
  object-fit: cover;
}

.event-info {
  padding: 10px;
  text-align: center;
}

.event-info span {
  display: block;
  margin: 5px 0;
}

.event-info button {
  background-color: #9ede73;
  border: none;
  padding: 10px;
  border-radius: 5px;
  cursor: pointer;
}
</style>
