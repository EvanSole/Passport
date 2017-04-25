<template>
  <section>
    <div id="layout-navbar">
      <!-- Navbar Right Menu -->
      <div class="navbar-custom-menu">
        <ul class="nav navbar-nav">
          <!-- User Account Menu -->
          <li class="dropdown user user-menu">
            <!-- Menu Toggle Button -->
            <a href="#" class="dropdown-toggle" data-toggle="dropdown">
              <!-- hidden-xs hides the username on small devices so only the image appears. -->
              <span class="hidden-xs">{{ currentLoginName }}</span>
            </a>
            <ul class="dropdown-menu">
              <!-- The user image in the menu -->
              <li class="user-header">
                <img id="user-header" src="../../assets/logo/user.png" class="img-circle" alt="User Image">
                <p>{{ userName }}</p>
              </li>
              <!-- Menu Footer-->
              <li class="user-footer">
                <div class="pull-left">
                  <a id="systemsettingBtn" href="javascript:void(0)" @click="changePass"
                     class="btn btn-default btn-flat">修改密码</a>
                </div>
              </li>
            </ul>
          </li>
          <!-- Control Sidebar Toggle Button -->
          <li>
            <a href="#" @click="handleLogout()" data-toggle="control-sidebar"><i class="fa fa-sign-out"></i></a>
          </li>
        </ul>
      </div>
    </div>

    <el-dialog :title="PwdFormTitle" v-model="PwdFormVisible" :close-on-click-modal="false">
      <el-form :model="PwdForm" label-width="100px" :rules="PwdFormRules" ref="PwdForm">
      <el-form-item label="旧密码：" prop="oldPassword">
      <el-input type="password" v-model="PwdForm.oldPassword" auto-complete="off"></el-input>
      </el-form-item>
      <el-form-item label="新密码：" prop="newPassword">
      <el-input type="password" v-model="PwdForm.newPassword" auto-complete="off"></el-input>
      </el-form-item>
      <el-form-item label="确认密码：" prop="newPasswordAgain">
      <el-input type="password" v-model="PwdForm.newPasswordAgain" auto-complete="off"></el-input>
      </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
      <el-button @click.native="reset">重 置</el-button>
      <el-button type="primary" @click.native="handleChangSave" :loading="PwdChangeLoading">{{btnPassChangeText}}
      </el-button>
      </div>
    </el-dialog>

  </section>
</template>

<script>
  import * as Auth from '../../common/auth'
  import { encryptPassword } from '../../common/md5'
  import { pwsChange } from '../../api/api';

  import qs from 'qs';
  export default {
    data() {
      let currentUser = qs.parse(sessionStorage.getItem("currentUser"));
      return {
        currentLoginName: currentUser.loginName,
        userName: currentUser.userName,
        dialogVisible: true,
        PwdFormVisible: false,
        PwdFormTitle: "密码修改",
        PwdForm:{
          id: currentUser.id,
          oldPassword: '',
          newPassword: '',
          newPasswordAgain: '',
        },
        btnPassChangeText: '提交',
        PwdChangeLoading: false,
        PwdFormRules: {
          oldPassword: [{required: true, message: '请输入原密码', trigger: 'blur'}],
          newPassword: [{required: true, message: '请输入新密码', trigger: 'blur'}],
          newPasswordAgain: [{required: true, message: '请输入确认密码', trigger: 'blur'}],
        },
      }
    },
    methods: {
      reset: function () {
        var _this = this;
        _this.PwdForm.oldPassword = '';
        _this.PwdForm.newPassword = '';
        _this.PwdForm.newPasswordAgain = '';
      },
      handleChangSave: function () {
        var _this = this;
        _this.$refs.PwdForm.validate(function (valid) {
          if (valid) {
            if (_this.PwdForm.newPassword !== _this.PwdForm.newPasswordAgain) {
              _this.$message({ message: '两次输入的新密码不一致!请重新输入',type:'warning',showClose: true});
              _this.PwdForm.newPassword = '';
              _this.PwdForm.newPasswordAgain = '';
              return
            }
            let para= {
              id: _this.PwdForm.id,
              oldPassword: encryptPassword(_this.PwdForm.oldPassword),
              newPassword: encryptPassword(_this.PwdForm.newPassword),
            };
            pwsChange(para).then((res)=> {
              if (res.code == "S00001") {
              _this.$message({
                title: '成功',
                message: '密码修改成功,请退出系统重新登录!',
                type: 'success'
              });
              _this.PwdFormVisible = false;
            } else {
              _this.$message({
                message: res.message,
                type: 'warning'
              });
            };
          }).catch(() => {
          })
          }
        })
      },
      handleLogout: function () {
        var _this = this;
        this.$confirm('确认退出系统吗？', '提示', {
          type: 'warning'
        }).then(() => {
          Auth.handleLogout()
        }).catch(
        );
      },
      changePass: function () {
         let _this = this
         _this.PwdFormVisible = true;
      }
    }
  }


</script>
