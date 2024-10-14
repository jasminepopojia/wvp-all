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
		<div>电话 :{{ item.phoneNumber }}</div>
        <div>日期 :{{ formatDate(item, {property: 'createTime'}) }}</div>
		
		<br>
		<br>
		 <div v-if="selectedCardIndex === index" class="buttons-container">
          <el-button size="mini" type="success" @click.stop="openApproveDialog(item)">通过</el-button>
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
      <el-table-column min-width="160">
        <template slot-scope="scope">
          <div>
            {{ roleNameMapping[scope.row.role.name] || '未知角色' }}
          </div>
        </template>
      </el-table-column>
	  <el-table-column min-width="160">
        <template slot-scope="scope">
          <div class="pushkey-container">
            <span v-if="visibleKeys.includes(scope.row.id)">
              {{ scope.row.phoneNumber }}
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
         
           
              <el-button size="mini":disabled="!canEdit(scope.row)" v-if="activeFunctionMenu === scope.row.id" @click="edit(scope.row)">修改密码</el-button>
              <el-button size="mini":disabled="!canDelete(scope.row)"  v-if="activeFunctionMenu === scope.row.id"@click="deleteUser(scope.row)" style="color: #f56c6c">删除用户</el-button>
            
          
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
	 <el-dialog :visible.sync="approveDialogVisible" title="选择用户角色">
    <el-form>
      <el-form-item label="选择角色">
        <el-select v-model="selectedRole" placeholder="请选择角色">
         <!-- <el-option label="根管理员" v-if="userRoleId === 1" value="radmin"></el-option> -->
          <el-option label="管理员" v-if="userRoleId === 1" value="admin"></el-option>
          <el-option label="教育工作者" v-if="userRoleId === 1 || userRoleId === 2" value="educator"></el-option>
          <el-option label="学生家长" v-if="userRoleId === 1 || userRoleId === 2" value="parent"></el-option>
        </el-select>
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button @click="approveDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="confirmApproval">确认</el-button>
    </div>
  </el-dialog>
  </div>
</template>

<script>
import uiHeader from '../layout/UiHeader.vue'
import changePasswordForAdmin from './dialog/changePasswordForAdmin.vue'
import changePushKey from './dialog/changePushKey.vue'
import userService from '../components/service/UserService'
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
	   roleNameMapping: {
	    radmin:'根管理员',
        admin: '管理员',
        educator: '教育工作者',
        parent: '学生家长'
      },
      userList: [], //设备列表
	  requireList:[],
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
	  selectedCardIndex: null,
	  approveDialogVisible: false,
      selectedRole: '',
	  userRoleId: null,
      currentItemId: null,
	  currentphone: null,
	  message:'恭喜您，通过了审核'
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
	this.getUserRole();
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
	canEdit(row) {
      return this.userRoleId === 1 || (this.userRoleId === 2 && row.role.id > 2);
    },
    canDelete(row) {
      return this.userRoleId === 1 || (this.userRoleId === 2 && row.role.id > 2);
    },
	 getUserRole() {
      const user = userService.getUser();
      this.userRoleId = user.role.id;
    },
	 openApproveDialog(item) {
      this.currentItemId = item.id;
	  this.currentphone= item.phoneNumber;
      this.approveDialogVisible = true;
    },
	
  
    confirmApproval() {
      if (!this.selectedRole) {
        this.$message.error('请选择一个角色');
        return;
      }
	  
     this.$axios({
        method: 'post',
        url: `api/user/agree`,
        params: {
          id: this.currentItemId,
          name: this.selectedRole
        }
      }).then((res) => {
	    if(res.data.code === 0){
        this.$message.success('用户已通过审批');
        this.getUserList();
        this.getRequireList();
        this.approveDialogVisible = false;
		}else{
		console.log(res.data);
		console.log(this.userRoleId);
		}
      }).catch(error => {
        this.$message.error('审批失败');
        console.error(error);
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
          method: 'delete',
          url: `api/user/deny?id=${id}`
        }).then((res) => {
		 if(res.data.code === 0){
          this.getUserList();
		  this.getRequireList();
		  }
        }).catch((error) => {
          console.error(error);
        });
      }).catch(() => {

      });


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
        url: `api/user/all`,
        params: {
          page: that.currentPage,
          count: that.count
        }
      }).then(function (res) {
        if (res.data.code === 0) {
          that.total = res.data.data.total;
          that.userList = res.data.data.list;
		  console.log(that.userList);
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
        url: `api/user/all/regUsers`,
       
      }).then(function (res) {
        if (res.data.code === 0) {
          that.requireList = res.data.data;
		  console.log(that.requireList);
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
          url: `api/user/delete?id=${row.id}`
        }).then((res) => {
          this.getUserList();
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
	font-family:'Microsoft YaHei', sans-serif;
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
	font-family:'Microsoft YaHei', sans-serif;
	font-weight: bold;
}





</style>
