<template>
  <div class="login">
    <!-- BEGIN LOGO -->
    <div class="login login-title">
      <span class="glyphicon glyphicon-globe">&nbsp;Passport后台管理系统</span>
    </div>
    <!-- END LOGO -->
    <!-- BEGIN LOGIN -->
    <div class="login container login-container">
      <!-- BEGIN LOGIN FORM -->
      <el-form :model="loginRuleForm" :rules="loginRules" ref="loginRuleForm" label-position="left" label-width="0px"
               class="loginform">
        <div class="form-title">
          <span class="form-title">Welcome.</span>
          <span class="form-subtitle">Please login.</span>
        </div>
        <el-form-item prop="account">
          <el-input type="text" v-model="loginRuleForm.account" auto-complete="off" placeholder="请输入账号"></el-input>
        </el-form-item>
        <el-form-item prop="checkPass">
          <el-input type="password" v-model="loginRuleForm.checkPass" auto-complete="off"
                    placeholder="请输入密码" @keyup.native.enter="loginSubmit"></el-input>
        </el-form-item>
        <el-checkbox v-model="checked" class="rememberme">记住密码</el-checkbox>
        <el-form-item style="width:100%;">
          <el-button id="sub" type="primary" style="width:100%;" @click.native.prevent="loginSubmit" :loading="logining">登&nbsp;&nbsp;&nbsp;&nbsp;录</el-button>
        </el-form-item>
      </el-form>
      <!-- END LOGIN FORM -->
    </div>
    <!-- END LOGIN -->
  </div>
</template>

<script>
  import {login} from '../../api/api';
  import {encryptPassword} from '../../common/md5';
  import qs from 'qs';
  export default {
    data() {
      return {
        logining: false,
        loginRuleForm: {
          account: 'admin',
          checkPass: '123456'
        },
        loginRules: {
          account: [
            {required: true, message: '请输入账号', trigger: 'blur'}
          ],
          checkPass: [
            {required: true, message: '请输入密码', trigger: 'blur'}
          ]
        },
        checked: true
      };
    },
    methods: {
      loginSubmit(ev){
        var _this = this;
        this.$refs.loginRuleForm.validate((valid) => {
          if (valid) {
            this.logining = true;
            let loginParams = {
              username: this.loginRuleForm.account,
              password: encryptPassword(this.loginRuleForm.checkPass)
            };
            //user login
            login(loginParams).then(result => {
              this.logining = false;
              let {message} = result;
              if (result.code !== "S00001") {
                this.$message({
                  message: message,
                  type: 'warning'
                });
              } else {
                //登录成功存储用户登录信息，跳转到登录首页
                sessionStorage.setItem("currentUser", qs.stringify(result.data));
                this.$router.push('/applications');
                setTimeout(() => {
                  window.$.AdminLTE.layout.fix();
                }, 100)
              }
            }).catch(() => {
              this.loadLogining = false;
            });
          } else {
            console.log('validate param error .');
            return false;
          }
        });
      }
  }
  }
</script>

<style scoped>
  .login {
    padding: 30px 0 0;
  }

  .login .login-title {
    margin-top: 50px;
    text-align: center;
    color: #50A2E7;
    font-size: 32px;
    font-weight: 400 !important;
    top: 0;
    left: 0;
    height: 100px;
  }

  .login .login-container {
    margin-top: 50px;
    width: 35%;
    height: initial;
  }

  .login .login-bottom {
    text-align: center;
    color: #50A2E7;
    font-size: 32px;
    font-weight: 400 !important;
  }

  .login a {
    color: #edf4f8 !important;
  }

  .login .logo {
    margin-top: 50px;
    padding: 15px;
    text-align: center;
  }

  .login .form-title {
    margin-bottom: 20px;
    color: #edf4f8;
    font-size: 32px;
    font-weight: 400 !important;
  }

  .login .form-subtitle {
    color: #edf4f8;
    font-size: 17px;
    font-weight: 300 !important;
  }

  .login .rememberme {
    margin-top: 8px;
    margin-bottom: 20px;
    color: #c9dce9;
    font-size: 17px;
    font-weight: 300 !important;
  }
</style>
