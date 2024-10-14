<template>
  <div class="login" id="login">
    <div class="lim">
	<img src="../assets/bullying.png" class="alingleft">
      <div class="container-login100">
        <div class="wrap-login100">
          <span class="login100-form-title p-b-26" style="font-size: 36px;">校园霸凌监控平台</span>
          <span class="login100-form-title p-b-48">
            <i class="fa fa-video-camera"></i>
			
          </span>

          <div class="wrap-input100 validate-input" data-validate="Valid email is: a@b.c">
            <input :class="'input100 ' + (username == '' ? '' : 'has-val')" type="text" v-model="username" name="username">
            <span class="focus-input100" data-placeholder="用户名"></span>
          </div>

          <div class="wrap-input100 validate-input" data-validate="Enter password">
            <span class="btn-show-pass">
              <i :class="'fa ' + (!showPassword ? 'fa-eye' : 'fa-eye-slash')" @click="showPassword = !showPassword"></i>
            </span>
            <input :class="'input100 ' + (password == '' ? '' : 'has-val')" :type="(!showPassword ? 'password' : 'text')" v-model="password" name="password">
            <span class="focus-input100" data-placeholder="密码"></span>
          </div>

          <div class="container-login100-form-btn">
            <div class="wrap-login100-form-btn" :class="{'login-loading': isLoging}" v-loading="isLoging" element-loading-background="rgb(0 0 0 / 0%);" element-loading-custom-class="login-loading-class">
              <div class="login100-form-bgbtn"></div>
              <button class="loginbtn" @click="login">登录</button>
            </div>
          </div>

          <div class="text-center p-t-115">
            <span class="txt1">
              没有账号？
            </span>
            <a class="txt2" @click="register">
              注册一个
            </a>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import crypto from 'crypto'
import userService from "./service/UserService";
export default {
  name: 'Login',
  data() {
    return {
      isLoging: false,
      showPassword: false,
      loginLoading: false,
      username: '',
      password: ''
    }
  },
  created() {
    var that = this;
    document.onkeydown = function(e) {
      var key = window.event.keyCode;
      if (key == 13) {
        that.login();
      }
    }
  },
  methods: {
    login() {
      if (this.username != '' && this.password != '') {
        this.toLogin();
      }
    },
    register() { // 跳转到注册界面
      this.$router.push('/register')
    },
    toLogin() {
      let loginParam = {
        username: this.username,
        password: crypto.createHash('md5').update(this.password, "utf8").digest('hex')
		
      }
      var that = this;
      this.isLoging = true;
      let timeoutTask = setTimeout(() => {
        that.$message.error("登录超时");
        that.isLoging = false;
      }, 1000)
      this.$axios({
        method: 'get',
        url: "api/user/login",
        params: loginParam
      }).then(function(res) {
        window.clearTimeout(timeoutTask)
        if (res.data.code === 0) {
          userService.setUser(res.data.data)
          that.cancelEnterkeyDefaultAction();
		  if(userService.getUser().role.id === 1 || userService.getUser().role.id === 2){
          that.$router.push('/');
		  } else {
		  that.isLoging = false;
		   that.$message({
            showClose: true,
            message: '您没有权限访问网页',
            type: 'error'
          });
		  }
        } else {
		  console.log(res.data);
          that.isLoging = false;
          that.$message({
            showClose: true,
            message: '登录失败，用户名或密码错误',
            type: 'error'
          });
        }
      }).catch(function(error) {
        window.clearTimeout(timeoutTask)
        that.$message.error("登录失败,用户名或密码错误");
        that.isLoging = false;
      });
    },
    cancelEnterkeyDefaultAction: function() {
      document.onkeydown = function(e) {
        var key = window.event.keyCode;
        if (key == 13) {
          return false;
        }
      }
    }
  }
}
</script>

<style scoped>
body {
  font-family:'Microsoft YaHei', sans-serif;
}

.login {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  font-family:'Microsoft YaHei', sans-serif;
}

.lim {
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.container-login100 {
  width: 100%;
  max-width: 800px;
  display: flex;
  flex-direction: row;
  background-color: #fff;
  box-shadow: 0 5px 10px rgba(0, 0, 0, 0.1);
}

.wrap-login100 {
  width: 100%;
  padding: 20px;
}

.login100-form-title {
  font-size: 36px; /* 放大字体 */
  font-weight: bold;
  text-align: center;
  margin-bottom: 10px;
   font-family:'Microsoft YaHei', sans-serif;
}

.wrap-input100 {
  position: relative;
  width: 100%;
  margin-bottom: 20px;
}

.input100 {
  width: 100%;
  border: 1px solid #ccc;
  padding: 10px;
  border-radius: 5px;
  font-size: 16px;
   font-family:'Microsoft YaHei', sans-serif;
}

.focus-input100 {
  position: absolute;
  top: 50%;
  left: 10px;
  transform: translateY(-50%);
  font-size: 16px;
  color: #999;
  pointer-events: none;
  transition: all 0.3s;
   font-family:'Microsoft YaHei', sans-serif;
}



.btn-show-pass {
  position: absolute;
  top: 50%;
  right: 10px;
  transform: translateY(-50%);
  cursor: pointer;
}

.container-login100-form-btn {
  display: flex;
  justify-content: space-between; /* 将按钮和注册链接放在同一行 */
  margin-top: 30px;
}

.wrap-login100-form-btn {
  position: relative;
  width: auto; /* 让按钮宽度根据内容自动调整 */
}

.login100-form-bgbtn {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: #1890FF;
  border-radius: 5px;
  z-index: -1;
}

.loginbtn {
  padding: 10px 30px; /* 调整按钮内边距以缩短宽度 */
  border: none;
  background: none;
  color: #fff;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
}

.text-center {
  text-align: center;
  margin-top: 15px;
}

.txt1 {
  font-size: 14px;
}

.txt2 {
  font-size: 14px;
  color: #4caf50;
  cursor: pointer;
  margin-left: 5px; /* 给注册链接添加左边距 */
}

.p-t-115 {
  padding-top: 15px;
}
.alignleft {
    display: inline;
	 width: 100px !important; 
  height: 100px !important; 
   object-fit: cover;
}
</style>
