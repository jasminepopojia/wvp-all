<template>
	
  <div class="rbox">
  <img src="../assets/bullying.png" class="alingleft">
    <div class="zhuce">
	<h1 align="center">注册 </h1>
	<h5 align="center">注册需管理员审批，结果会以短信方式通知</h5>
      <el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-width="100px" class="demo-ruleForm">
        <el-form-item label="账号名称" prop="username">
          <el-input v-model="ruleForm.username" clearable></el-input>
        </el-form-item>
        <el-form-item label="手机号码" prop="phoneNumber">
          <el-input v-model="ruleForm.phoneNumber" clearable></el-input>
        </el-form-item>
        <el-form-item label="电子邮箱" prop="email">
          <el-input v-model="ruleForm.email" clearable></el-input>
        </el-form-item>
        <el-form-item label="账号密码" prop="password">
          <el-input type="password" v-model="ruleForm.password" clearable></el-input>
        </el-form-item>
        <el-form-item label="确认密码" prop="checkPass">
          <el-input type="password" v-model="ruleForm.checkPass" clearable></el-input>
        </el-form-item>
		<label>
			<input type="checkbox" v-model="agree"> 我已阅读并同意协议
		</label>
        <el-form-item>
          <el-button type="primary" @click="submitForm" :disabled = "!agree" class="set-other-btn">注册</el-button>
          <el-button @click="returnForm" style="font-family:Microsoft YaHei;">已有帐号,登录</el-button>
        </el-form-item>
		

      </el-form>
    </div>
  </div>
</template>
<script>
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
      const regUser = /^((0\d{2,3}-\d{7,8})|(1[34578]\d{9}))$/
      if (regUser.test(value)) {
        return callback()
      }
      callback(new Error('手机号码不能为空'))
    }
    var checkEmail = (rule, value, callback) => {
      const regUser = /^([a-zA-Z0-9]+[-_]?)+@[a-zA-Z0-9]+\.[a-z]+$/
      if (regUser.test(value)) {
        return callback()
      }
      callback(new Error('邮箱不能为空'))
    }
    var validatePass = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入密码'))
      } else {
        if (this.ruleForm.checkPass !== '') {
          this.$refs.ruleForm.validateField('checkPass')
        }
        callback()
      }
    }
    var validatePass2 = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入密码'))
      } else if (value !== this.ruleForm.password) {
        callback(new Error('两次输入密码不一致!'))
      } else {
        callback()
      }
    }
    return {
      ruleForm: {
        username: '',
        phoneNumber: '',
        email: '',
        password: '',
        
      },
      rules: {
        username: [{ validator: checkUser, trigger: 'blur' }],
        phoneNumber: [{ validator: checkMobile, trigger: 'blur' }],
        email: [{ validator: checkEmail, trigger: 'blur' }],
        password: [{ validator: validatePass, trigger: 'blur' }],
        checkPass: [{ validator: validatePass2, trigger: 'blur' }]
      },
	  agree:false
    }
  },
  methods: {
    returnForm() {
      // 返回login界面
      this.$router.push('/login')
    },
    submitForm(formName) {
	  console.log('123');
      console.log(this.ruleForm);
      var data = this.ruleForm;
      var that = this;
      //设置在登录状态
      this.isLoging = true;
      let timeoutTask = setTimeout(()=>{
        that.$message.error("注册超时");
        that.isLoging = false;
      }, 1000)

      this.$axios({
      	method: 'post',
        url:"api/user/register",
        params: data
      }).then(function (res) {
        window.clearTimeout(timeoutTask)
        console.log(res);
          if (res.data.code === 0 ) {
            that.$router.push('/login');
			console.log("提交成功，等待审核");
			console.log(data);
          }else{
            that.isLoging = false;
            that.$message({
                  showClose: true,
                  message: '注册失败，请检查填写的内容',
                  type: 'error'
              });
          }
      }).catch(function (error) {
        console.log(error)
        window.clearTimeout(timeoutTask)
        that.$message.error("注册失败，用户名重复");
        that.isLoging = false;
      });
    }
  }
}
</script>
<style>
.rbox {
  height: 100%;
  background-color: #fff;
  font-family:'Microsoft YaHei', sans-serif;
  
}

.alignleft {
    display: inline;
    float: left;
	 width: 300px; 
  height: 300px; 
   object-fit: cover;
}
.zhuce {
  position: absolute;
  top: 35%;
  left: 70%;
  transform: translate(-50%, -50%);
  width: 450px;
  height: 600px;
  background-color: #fff;
  border-radius: 3px;
}
.zhuce h1{
	font-size: 50px;
}
.el-form-item {
  margin-top: 50px;
  width: 500px;
  height: 50px;
  
}
.set-other-btn:hover{
 background-color: #1890FF;
  border-color: #FE83C6;
   font-family:'Microsoft YaHei', sans-serif;
}
.set-other-btn.is-disabled{
  background-color: #1890FF;
  border-color: #FE83C6;
   font-family:'Microsoft YaHei', sans-serif;
}
.set-other-btn.is-disabled:hover{
  background-color:  #1890FF;
  border-color:  #FE83C6;
   font-family:'Microsoft YaHei', sans-serif;
}
</style>
