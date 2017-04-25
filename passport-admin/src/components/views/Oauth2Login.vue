<template>
  <div class="login">
    <!-- BEGIN LOGO -->
    <div class="login login-title">
      <span class="glyphicon glyphicon-home">&nbsp;Passport统一认证中心登录</span>
    </div>
    <!-- END LOGO -->
    <!-- BEGIN LOGIN -->
    <div class="login container login-container">
      <el-card class="box-card">
          <!-- BEGIN LOGIN FORM -->
          <el-form :model="loginRuleForm" :rules="loginRules" ref="loginRuleForm" label-position="left" label-width="0px" class="loginform">
            <el-row>
              <el-col :span="24">
                  <div class="form-title">
                    <span class="form-title">Welcome.</span>
                    <span class="form-subtitle">Please login.</span>
                  </div>
              </el-col>
              <el-col :span="24">
                <el-form-item  prop="client_name" hidden="hidden">
                  <el-input v-model="loginRuleForm.client_name" auto-complete="off"></el-input>
                </el-form-item>
                <el-form-item  prop="client_id" hidden="hidden">
                    <el-input v-model="loginRuleForm.client_id" auto-complete="off"></el-input>
                </el-form-item>
                <el-form-item  prop="redirect_uri" hidden="hidden">
                  <el-input v-model="loginRuleForm.redirect_uri" auto-complete="off"></el-input>
                </el-form-item>
                <el-form-item  prop="response_type" hidden="hidden">
                  <el-input v-model="loginRuleForm.response_type" auto-complete="off" value="code"></el-input>
                </el-form-item>
              </el-col>
              <el-col :span="24">
                  <el-form-item prop="loginName">
                    <el-input type="text" v-model="loginRuleForm.loginName" auto-complete="off" placeholder="请输入账号"></el-input>
                  </el-form-item>
              </el-col>
              <el-col :span="24">
                  <el-form-item prop="password">
                    <el-input type="password" v-model="loginRuleForm.password" auto-complete="off" placeholder="请输入密码" @keyup.native.enter="loginSubmit"></el-input>
                  </el-form-item>
              </el-col>
              <el-col :span="24">
                  <el-form-item style="width:100%;">
                      <el-button type="primary" style="width:100%;" @click.native.prevent="loginSubmit" :loading="loadLogining">登&nbsp;&nbsp;&nbsp;&nbsp;录</el-button>
                  </el-form-item>
              </el-col>
            </el-row>
          </el-form>
          <!-- END LOGIN FORM -->
        </el-card>
    </div>
    <!-- END LOGIN -->
  </div>
</template>
<script>
  import { oAuth2Login } from '../../api/api';
  import { encryptPassword } from '../../common/md5';
  import qs from 'qs';
  export default {
    data() {
      return {
        loadLogining: false,
        loginRuleForm: {
          loginName: '',
          password: '',
          client_id: this.$route.query.client_id,
          client_name: this.$route.query.client_name,
          redirect_uri: this.$route.query.redirect_uri,
          response_type: this.$route.query.response_type,
        },
        loginRules: {
          loginName: [
            { required: true, message: '请输入账号', trigger: 'blur'}
          ],
          password: [
            { required: true, message: '请输入密码', trigger: 'blur'}
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
            this.loadLogining = true;
            let loginParams = {
              loginName: this.loginRuleForm.loginName,
              password:  encryptPassword(this.loginRuleForm.password),
              client_id: this.loginRuleForm.client_id,
              redirect_uri: this.loginRuleForm.redirect_uri,
              response_type: this.loginRuleForm.response_type
            };
            oAuth2Login(loginParams).then(result => {
              this.loadLogining = false;
              let { message } = result;
              if (result.code !== "200") {
                this.$message({
                  message: message,
                  type: 'warning'
                });
              } else {
                //登录成功跳转到回调首页
                 let url = result.data;
                 window.location = url;
                 //this.$router.push('/');
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
    margin-top: 8%;
    text-align: center;
    color: #50A2E7;
    font-size: 32px;
    font-weight: 400 !important;
    top: 0;
    left: 0;
    height: 100px;
  }

  .login .login-container {
    margin-top: 5px;
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

  .box-card{
     background-color: rgba(255, 255, 255, 0.13);
  }

</style>
