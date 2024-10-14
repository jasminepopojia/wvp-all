<template>
  <div class="pox">
  <img src="../assets/create.png" class="alingright">
    <div class="czhuce">
	 
      <h1 align="center">创建用户</h1>
      <el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-width="100px" class="demo-ruleForm">
        <el-form-item label="账号名称" prop="username">
          <el-input v-model="ruleForm.username" clearable></el-input>
        </el-form-item>
		<el-form-item label="电子邮箱" prop="email">
          <el-input v-model="ruleForm.email" clearable></el-input>
        </el-form-item>
        <el-form-item label="手机号码" prop="phoneNumber">
          <el-input v-model="ruleForm.phoneNumber" clearable></el-input>
        </el-form-item>
        <el-form-item label="账号密码" prop="password">
          <el-input type="password" v-model="ruleForm.password" clearable></el-input>
        </el-form-item>
        <el-form-item label="选择角色" prop="name">
          <el-select v-model="ruleForm.name" placeholder="请选择角色"> 
          <!--  <el-option v-if="userRoleId === 1" label="根管理员" value="radmin"></el-option> -->
            <el-option v-if="userRoleId === 1" label="管理员" value="admin"></el-option>
            <el-option v-if="userRoleId === 1 || userRoleId === 2" label="教育工作者" value="educator"></el-option>
            <el-option v-if="userRoleId === 1 || userRoleId === 2" label="学生家长" value="parent"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button  @click="submitForm" class="cset-other-btn" style="background-color: #1890FF;font-family:'Microsoft YaHei', sans-serif">创建用户</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import userService from '../components/service/UserService'

export default {
  data() {
    var checkUser = (rule, value, callback) => {
      const regUser = /^[a-zA-Z0-9_-]{3,16}$/
      if (regUser.test(value)) {
        return callback()
      }
      callback(new Error('用户名不能为空'))
    }
    var checkMobile = (rule, value, callback) => {
      const regMobile = /^((0\d{2,3}-\d{7,8})|(1[34578]\d{9}))$/
      if (regMobile.test(value)) {
        return callback()
      }
      callback(new Error('手机号码不能为空'))
    }
    var validatePass = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入密码'));
      } else if (value.length < 6) {
        callback(new Error('密码长度不能小于6位'));
      } else {
        callback();
      }
    }

    return {
      ruleForm: {
        username: '',
        phoneNumber: '',
        password: '',
        name: '',
		email:''
      },
      rules: {
        username: [{ validator: checkUser, trigger: 'blur' }],
        phoneNumber: [{ validator: checkMobile, trigger: 'blur' }],
        password: [{ validator: validatePass, trigger: 'blur' }],
        name: [{ required: true, message: '请选择一个角色', trigger: 'change' }]
      },
      userRoleId: null // 初始化 userRoleId
    }
  },
  mounted() {
	
    this.getUserRole();
	
  },
  methods: {
    getUserRole() {
      // Replace with actual user role fetching logic
      const user = userService.getUser();
      if (user && user.role) {
        this.userRoleId = user.role.id;
      } else {
        this.userRoleId = 0; // Default role id if user role is not available
      }
    },
    submitForm() {
      this.$refs.ruleForm.validate((valid) => {
        if (valid) {
          console.log(this.ruleForm);
          var data = this.ruleForm;
          var that = this;
          this.isLoging = true;
          let timeoutTask = setTimeout(() => {
            that.$message.error("创建超时");
            that.isLoging = false;
          }, 1000);

          this.$axios({
            method: 'post',
            url: "/api/user/add",
            params: data
          }).then(function (res) {
            clearTimeout(timeoutTask);
            if (res.data.code === 0) {
               that.$message({
                showClose: true,
                message: '创建成功',
               
              });
            } else {
              that.isLoging = false;
              that.$message({
                showClose: true,
                message: '创建失败，请检查填写的内容',
                type: 'error'
              });
            }
          }).catch(function (error) {
            console.log(error);
            clearTimeout(timeoutTask);
            that.$message.error(error.response.data.msg);
            that.isLoging = false;
          });
        } else {
          console.log('错误创建');
          return false;
        }
      });
    }
  }
}
</script>

<style>
.pox {
  height: 100%;
  background-color: #fff;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-left: 150px;
  font-family: 'Microsoft YaHei', sans-serif;
}
.czhuce {
  width: 600px;
  padding: 20px;
  background-color: #fff;
  border-radius: 5px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
 
}
.alignright {
    display: inline;
    float: right;
	 width: 300px; 
  height: 300px; 
   object-fit: cover;
}
.czhuce h1 {
  font-size: 36px;
  margin-bottom: 20px;
}

.el-form-item {
  margin-bottom: 20px;
}

.el-input {
  width: 100%;
}

.cset-other-btn {
  width: 100%;
  color:#fff;
}
.cset-other-btn:hover{
	color:#fff;
}

.select-center .el-select {
  display: block;
  margin: 0 auto;
}

.dialog-footer {
  text-align: right;
}

.dialog-footer .el-button {
  margin-left: 10px;
}
</style>
