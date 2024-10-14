<template>

  <div style="width: 100%" class="box">
	
    <div class="page-header">
      <div class="page-header-btn">
        
      </div>
    </div>
 
	<div class="divc">
      <el-input prefix-icon="el-icon-search" v-model="searchText" placeholder="查询人员" ></el-input>
    </div>
	<div class="divc">
	<h1 class="h1pos">待审批</h1>
		<div class="require-list-container">
      <div  v-for="(item, index) in filteredRequireList"
        :key="item.id"
        v-show="index < displayCount"
        class="require-card"
		@click="toggleButtons(index)">
        <div>姓名 :{{ item.username }}</div>
		<br>
        <div>日期 :{{ formatDate(item, {property: 'time'}) }}</div>
		<br>
		<br>
		 <div v-if="selectedCardIndex === index" class="buttons-container">
          <el-button size="mini" type="success" @click.stop="agree(item.id)">通过</el-button>
          <el-button size="mini" type="danger" @click.stop="deny(item.id)">不通过</el-button>
        </div>
      </div>
	  
	   <div
        v-if="displayCount < requireList.length"
        class="require-card more-card"
        @click="showMore"
      >
        更多
      </div>
    </div>
	
	</div>
	
	<div class="divc">
	<h1 class="otherh1">系统用户</h1>
    <el-table :data="filteredUserList" style="width: 100%;font-size: 15px;min-height:300px" 
              header-row-class-name="table-header" row-class-name="eltable" size="mini">
	   <el-table-column prop="logo" width="50">
            <template slot-scope="scope">
              <div>
                <span><img src="@/assets/user.png" alt=""></span>
                <span>{{ scope.row.logo }}</span>
              </div>
            </template>
        </el-table-column>

      <el-table-column   prop="username"  min-width="160">
	  </el-table-column>
      <el-table-column   prop="role.name"  min-width="160"/>
	  <el-table-column min-width="160">
        <template slot-scope="scope">
          <div class="pushkey-container">
            <span v-if="visibleKeys.includes(scope.row.id)">
              {{ scope.row.phonenumber }}
            </span>
           <span v-if="!visibleKeys.includes(scope.row.id)" @click="togglePushKey(scope.row.id)" class="view-icon">
            &#128065; <!-- Unicode for an eye icon -->
          </span>
          </div>
        </template>
      </el-table-column>
      <el-table-column    min-width="160" fixed="right">
	 
        <template slot-scope="scope">
          <div class="function-container">
          <span @click="toggleFunctionMenu(scope.row.id)" class="function-icon">
            &#9776; <!-- Unicode for a menu icon -->
          </span>
         
           
              <el-button size="mini" v-if="activeFunctionMenu === scope.row.id" @click="edit(scope.row)">修改密码</el-button>
              <el-button size="mini"  v-if="activeFunctionMenu === scope.row.id"@click="deleteUser(scope.row)" style="color: #f56c6c">删除用户</el-button>
            
          
        </div>
        </template>
      </el-table-column>
    </el-table>
	</div>
    <changePasswordForAdmin ref="changePasswordForAdmin"></changePasswordForAdmin>
    <changePushKey ref="changePushKey"></changePushKey>
    <addUser ref="addUser"></addUser>
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
  </div>
</template>

<script>
import uiHeader from '../layout/UiHeader.vue'
import changePasswordForAdmin from './dialog/changePasswordForAdmin.vue'
import changePushKey from './dialog/changePushKey.vue'
import addUser from '../components/dialog/addUser.vue'

export default {
  name: 'userManager',
  components: {
    uiHeader,
    changePasswordForAdmin,
    changePushKey,
    addUser
  },
  data() {
    return {
      userList: [
	  {
	  id: 1,
	  username:"金",
      phonenumber:"13567791014",
	  role: { name: "admin" },
	  },
	   {
	  id: 2,
	  username:"金",
      phonenumber:"15057314111",
	  role: { name: "visitor" },
	  },
	  ], //设备列表
	  requireList:[
	   {
	  id:1,
	  username:"金",
	 
	  time:'2024/3/13',
	  },
	  {
	  id:2,
	  username:"金",
	  
	  time:'2024/3/13',
	  },
	  {
	  id:3,
	  username:"李",
	  
	  time:'2024/3/13',
	  },
	   {
	   id:4,
	  username:"金",
	  
	  time:'2024/3/13',
	  },
	  {
	  id:5,
	  username:"李",
	  
	  time:'2024/3/13',
	  },
	   {
	   id:6,
	  username:"金",
	  
	  time:'2024/3/13',
	  },
	  {
	  id:7,
	  username:"李",
	  
	  time:'2024/3/13',
	  },
	   {
	   id:8,
	  username:"金",
	  
	  time:'2024/3/13',
	  },
	  {
	  id:9,
	  username:"李",
	 
	  time:'2024/3/13',
	  },
	  
	  ],
	  visibleKeys: [],
      currentUser: {}, //当前操作设备对象
      videoComponentList: [],
      updateLooper: 0, //数据刷新轮训标志
      currentUserLenth: 0,
      winHeight: window.innerHeight - 200,
      currentPage: 1,
      count: 15,
      total: 0,
	  rtotal: 0,
      getUserListLoading: false,
	  getRequireLoading:false,
	  searchText:'',
	  displayCount: 6,
	   activeFunctionMenu: null,
	  selectedCardIndex: null
	  
    };
  },
  computed: {
    filteredUserList() {
      const query = this.searchText.toLowerCase();
      return this.userList.filter(user => {
        return Object.values(user).some(val => 
          String(val).toLowerCase().includes(query)
        );
      });
    },
    filteredRequireList() {
      const query = this.searchText.toLowerCase();
      return this.requireList.filter(require => {
        return Object.values(require).some(val => 
          String(val).toLowerCase().includes(query)
        );
      });
    }
  },
  
  mounted() {
    this.initData();
    this.updateLooper = setInterval(this.initData, 10000);
  },
  destroyed() {
    this.$destroy('videojs');
    clearTimeout(this.updateLooper);
  },
  methods: {
    initData: function () {
      this.getUserList();
	  this.getRequireList();
	  
    },
	 togglePushKey(id) {
      const index = this.visibleKeys.indexOf(id);
      if (index === -1) {
        this.visibleKeys.push(id);
      } else {
        this.visibleKeys.splice(index, 1);
      }
    },
	 
    currentChange: function (val) {
      this.currentPage = val;
      this.getUserList();
	  this.getRequireList();
    },
	toggleFunctionMenu(username) {
      if (this.activeFunctionMenu === username) {
        this.activeFunctionMenu = null;
      } else {
        this.activeFunctionMenu = username;
      }
    },
    handleSizeChange: function (val) {
      this.count = val;
      this.getUserList();
    },
	formatDate(row, column) {
          // 获取单元格数据
          let data = row[column.property];
          if(data == null) {
              return null;
          }
          let dt = new Date(data);
          return dt.getFullYear() + '-' + (dt.getMonth() + 1) + '-' + dt.getDate();
        },
    getUserList: function () {
      let that = this;
      this.getUserListLoading = true;
      this.$axios({
        method: 'get',
        url: `/api/user/users`,
        params: {
          page: that.currentPage,
          count: that.count
        }
      }).then(function (res) {
        if (res.data.code === 0) {
          that.total = res.data.data.total;
          that.userList = res.data.data.list;
		 
        }
        that.getUserListLoading = false;
      }).catch(function (error) {
        that.getUserListLoading = false;
      });

    },
	getRequireList: function () {
      let that = this;
      this.getRequireLoading = true;
      this.$axios({
        method: 'get',
        url: `/api/user/require`,
       
      }).then(function (res) {
        if (res.data.code === 0) {
          that.rtotal = res.data.data.total;
          that.requireList = res.data.data.list;
		  
        }
        that.getRequireListLoading = false;
      }).catch(function (error) {
        that.getRequireListLoading = false;
      });

    },
	
    edit: function (row) {
      this.$refs.changePasswordForAdmin.openDialog(row, () => {
        this.$refs.changePasswordForAdmin.close();
        this.$message({
          showClose: true,
          message: "密码修改成功",
          type: "success",
        });
        setTimeout(this.getUserList, 200)

      })
    },
    deleteUser: function (row) {
      let msg = "确定删除此用户？"
      if (row.online !== 0) {
        msg = "<strong>确定删除此用户？</strong>"
      }
      this.$confirm(msg, '提示', {
        dangerouslyUseHTMLString: true,
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        center: true,
        type: 'warning'
      }).then(() => {
        this.$axios({
          method: 'delete',
          url: `/api/user/delete?id=${row.id}`
        }).then((res) => {
          this.getUserList();
        }).catch((error) => {
          console.error(error);
        });
      }).catch(() => {

      });


    },
	 agree: function (id) {
      let msg = "确定通过吗？"
      
      this.$confirm(msg, '提示', {
        dangerouslyUseHTMLString: true,
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        center: true,
        type: 'warning'
      }).then(() => {
        this.$axios({
          method: 'put',
          url: `/api/user/agree?id=${id}`
        }).then((res) => {
          this.getUserList();
		  this.getRequireList();
        }).catch((error) => {
          console.error(error);
        });
      }).catch(() => {

      });


    },
	deny: function (id) {
      let msg = "确定拒绝吗？"
      this.$confirm(msg, '提示', {
        dangerouslyUseHTMLString: true,
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        center: true,
        type: 'warning'
      }).then(() => {
        this.$axios({
          method: 'put',
          url: `/api/user/deny?id=${id}`
        }).then((res) => {
          this.getUserList();
		  this.getRequireList();
        }).catch((error) => {
          console.error(error);
        });
      }).catch(() => {

      });


    },
	 toggleButtons(index) {
      this.selectedCardIndex = this.selectedCardIndex === index ? null : index;
    },

    changePushKey: function (row) {
      this.$refs.changePushKey.openDialog(row, () => {
        this.$refs.changePushKey.close();
        this.$message({
          showClose: true,
          message: "pushKey修改成功",
          type: "success",
        });
        setTimeout(this.getUserList, 200)

      })
    },
    addUser: function () {
      // this.$refs.addUser.openDialog()
      this.$refs.addUser.openDialog( () => {
        this.$refs.addUser.close();
        this.$message({
          showClose: true,
          message: "用户添加成功",
          type: "success",
        });
        setTimeout(this.getUserList, 200)

      })
    },
    showUserApiKeyManager: function (row) {
      this.$router.push(`/userApiKeyManager/${row.id}`)
    },
	showMore() {
      this.displayCount += 6;
    }
  }
  
  
}
</script>
<style>
.box{
	height: 100%;
    background-color: #fff;
	font-family:Simsun;
	font-weight: bold;
	margin-left: 150px;
}
.set-other-btn{
  background-color: #40A44C;
  border-color: #FE83C6;
}
.h1pos{
	top:200px;
	text-align: left;
	font-size: 40px;
	background-color: #fff;
	margin-left: 50px;

}
.otherh1{
	top:1000px;
	text-align: left;
	font-size: 25px;
	background-color: #fff;
	margin-left: 50px;
}
.eltable{
	background:#F0F0F0!important; 
	
}
 .el-table__body {
    
    -webkit-border-vertical-spacing: 15px; 
  }
.divc{
	background-color:#fff;
	margin-left: 50px;
	width: 95%;
	
}
.pushkey-container {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.view-icon {
  width: 40px;
  height: 20px;
  cursor: pointer;
}

.videoList {
  display: flex;
  flex-wrap: wrap;
  align-content: flex-start;
  background-color:#fff;
}

.video-item {
  position: relative;
  width: 15rem;
  height: 10rem;
  margin-right: 1rem;
  background-color:#fff;
}

.video-item-img {
  position: absolute;
  top: 0;
  bottom: 0;
  left: 0;
  right: 0;
  margin: auto;
  width: 100%;
  height: 100%;
  background-color:#fff;
}

.video-item-img:after {
  content: "";
  display: inline-block;
  position: absolute;
  z-index: 2;
  top: 0;
  bottom: 0;
  left: 0;
  right: 0;
  margin: auto;
  width: 3rem;
  height: 3rem;
  background-image: url("../assets/loading.png");
  background-size: cover;
  background-color:#fff;
}


.video-item-title {
  position: absolute;
  bottom: 0;
  color: #000000;
  background-color:#fff;
  line-height: 1.5rem;
  padding: 0.3rem;
  width: 14.4rem;
  background-color:#fff;
}
.require-list-container {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
}

.require-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 150px;
  padding: 1rem;
  background-color: #f5f5f5;
  border-radius: 8px;
  text-align: center;
  cursor: pointer;
  position: relative;
}

.require-card .avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  margin-bottom: 0.5rem;
}

.buttons-container {
  display: flex;
  gap: 0.5rem;
  position: absolute;
  bottom: 1rem;
}

.more-card {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 150px;
  padding: 1rem;
  background-color: #f5f5f5;
  border-radius: 8px;
  text-align: center;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
}
.function-icon {
  cursor: pointer;
}
.function-container {
  position: relative;
}

.el-button{
	font-family:Simsun;
	font-weight: bold;
}





</style>
