<template>
	
  <div class="box">
  <img src="../assets/comera.png" class="alingleft">
    <div class="zhuce">
	<h1 align="center">注册 </h1>
	<h5 align="center">注册需管理员审批，结果会以短信方式通知</h5>
      <el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-width="100px" class="demo-ruleForm">
        <el-form-item label="账号名称" prop="user">
          <el-input v-model="ruleForm.user" clearable></el-input>
        </el-form-item>
        <el-form-item label="手机号码" prop="mobile">
          <el-input v-model="ruleForm.mobile" clearable></el-input>
        </el-form-item>
        <el-form-item label="电子邮箱" prop="email">
          <el-input v-model="ruleForm.email" clearable></el-input>
        </el-form-item>
        <el-form-item label="账号密码" prop="pass">
          <el-input type="password" v-model="ruleForm.pass" clearable></el-input>
        </el-form-item>
        <el-form-item label="确认密码" prop="checkPass">
          <el-input type="password" v-model="ruleForm.checkPass" clearable></el-input>
        </el-form-item>
		<label>
			<input type="checkbox" v-model="agree"> 我已阅读并同意协议
		</label>
        <el-form-item>
          <el-button type="primary" @click="submitForm" :disabled = "!agree" class="set-other-btn">注册</el-button>
          <el-button @click="returnForm">已有帐号,登录</el-button>
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
      } else if (value !== this.ruleForm.pass) {
        callback(new Error('两次输入密码不一致!'))
      } else {
        callback()
      }
    }
    return {
      ruleForm: {
        user: '',
        mobile: '',
        email: '',
        pass: '',
        checkPass: ''
      },
      rules: {
        user: [{ validator: checkUser, trigger: 'blur' }],
        mobile: [{ validator: checkMobile, trigger: 'blur' }],
        email: [{ validator: checkEmail, trigger: 'blur' }],
        pass: [{ validator: validatePass, trigger: 'blur' }],
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
        url:"/api/user/register",
        params: data
      }).then(function (res) {
        window.clearTimeout(timeoutTask)
        console.log(res);
        console.log("提交成功，等待审核");
          if (res.data.code === 0 ) {
            userService.setUser(res.data.data)
            //成功后
            that.cancelEnterkeyDefaultAction();
            that.$router.push('/login');
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
        that.$message.error(error.response.data.msg);
        that.isLoging = false;
      });
    }
  }
}
</script>
<style>
.box {
  height: 100%;
  background-color: #fff;
  font-family:Simsun;
  
}

.alignleft {
    display: inline;
    float: left;
}
.zhuce {
  position: absolute;
  top: 40%;
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
.set-other-btn.is-disabled{
  background-color: #40A44C;
  border-color: #FE83C6;
}
.set-other-btn.is-disabled:hover{
  background-color:  #40A44C;
  border-color:  #FE83C6;
}
</style>
