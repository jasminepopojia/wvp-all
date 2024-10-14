<template>

  <div style="width: 100%" class="box">
	
    <div class="page-header">
      <div class="page-header-btn">
        <el-button icon="el-icon-plus" size="mini" style="margin-right: 1rem;" type="primary" @click="addUser" class="set-other-btn">
          添加用户
        </el-button>
      </div>
    </div>
 
	<div class="divc">
      <el-input prefix-icon="el-icon-search" v-model="searchText" placeholder="请输入要查询的内容" @input="searchUserList"></el-input>
    </div>
	<div class="divc">
	<h1 class="h1pos">待审批</h1>
		<el-table :data="requireList" style="width: 100%;font-size: 15px;min-height:100px" 
              header-row-class-name="table-header" row-class-name="eltable" size="mini">
	   
      <el-table-column   prop="username"  min-width="160"/>
      <el-table-column prop="time" label="申请时间" :formatter="formatDate" sortable>
                        </el-table-column> 
      <el-table-column  prop="userrole"  min-width="160"/>
      <el-table-column   min-width="450" fixed="right">
	 
        <template slot-scope="scope">
          <el-button size="medium" icon="el-icon-edit" type="text" @click="agree(scope.row)">通过</el-button>
          
          <el-button size="medium" icon="el-icon-delete" type="text" @click="deny(scope.row)"
                     style="color: #f56c6c">不通过
          </el-button>
        </template>
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
	</div>
	
	<div class="divc">
	<h1 class="otherh1">系统用户</h1>
    <el-table :data="userList" style="width: 100%;font-size: 15px;min-height:300px" 
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
      <el-table-column   prop="pushKey"  min-width="160"/>
      <el-table-column   prop="role.name"  min-width="160"/>
      <el-table-column    min-width="450" fixed="right">
	 
        <template slot-scope="scope">
          <el-button size="medium" icon="el-icon-edit" type="text" @click="edit(scope.row)">修改密码</el-button>
          
          <el-button size="medium" icon="el-icon-delete" type="text" @click="deleteUser(scope.row)"
                     style="color: #f56c6c">删除用户
          </el-button>
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
	  username:"金",

	  },
	  ], //设备列表
	  requireList:[
	  {
	  username:"金",
	  userrole:"老师",
	  time:'2024/3/13',
	  },
	  {
	  username:"李",
	  userrole:"大老师",
	  time:'2024/3/13',
	  },
	  ],
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
	  searchText:''
    };
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
    currentChange: function (val) {
      this.currentPage = val;
      this.getUserList();
	  this.getRequireList();
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
        params: {
          page: that.currentPage,
          count: that.count
        }
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
	searchUserList: function(){
		let that=this;
		this.getUserListLoading=true;
		console.log(this.searchText);
		 this.$axios({
        method: 'get',
        url: `/api/user/search`,
        params: {
			searchText:this.searchText
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
	 agree: function (row) {
      let msg = "确定通过吗？"
      if (row.online !== 0) {
        msg = "<strong>确定通过？</strong>"
      }
      this.$confirm(msg, '提示', {
        dangerouslyUseHTMLString: true,
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        center: true,
        type: 'warning'
      }).then(() => {
        this.$axios({
          method: 'put',
          url: `/api/user/agree?id=${row.id}`
        }).then((res) => {
          this.getUserList();
		  this.getRequireList();
        }).catch((error) => {
          console.error(error);
        });
      }).catch(() => {

      });


    },
	deny: function (row) {
      let msg = "确定拒绝吗？"
      if (row.online !== 0) {
        msg = "<strong>确定拒绝？</strong>"
      }
      this.$confirm(msg, '提示', {
        dangerouslyUseHTMLString: true,
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        center: true,
        type: 'warning'
      }).then(() => {
        this.$axios({
          method: 'put',
          url: `/api/user/deny?id=${row.id}`
        }).then((res) => {
          this.getUserList();
		  this.getRequireList();
        }).catch((error) => {
          console.error(error);
        });
      }).catch(() => {

      });


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
.divc{
	background-color:#fff;
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

</style>
