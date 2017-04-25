<template>
  <section>
    <!--工具条-->
    <el-col :span="24" class="toolbar search-toolbar">
      <el-form :inline="true" :model="filters">
        <div class="panel-box-left">
          <el-form-item>
            <el-input v-model="filters.loginName" placeholder=" 登录名"></el-input>
          </el-form-item>
          <el-form-item>
            <el-input v-model="filters.userName" placeholder=" 用户名"></el-input>
          </el-form-item>
          <el-form-item>
            <el-input v-model="filters.realName" placeholder=" 真实姓名"></el-input>
          </el-form-item>
          <el-form-item>
            <el-select v-model="filters.typeCode" placeholder="用户类型">
              <el-option v-for="item in typeCodeOptions" :label="item.label" :value="item.value"></el-option>
            </el-select>
          </el-form-item>
        </div>
        <div class="panel-box-right">
          <el-form-item>
            <el-button type="primary" size="small" v-on:click="getUsers">查询</el-button>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" size="small" v-on:click="rest">重置</el-button>
          </el-form-item>
        </div>
      </el-form>
    </el-col>

    <el-col :span="24">
      <el-form :inline="true" :model="filters" style="height:45px">
        <el-form-item>
          <el-button type="primary" size="small" @click="handleOpen">新增</el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="small" @click="handlePasswordReset">密码重置</el-button>
        </el-form-item>
      </el-form>
    </el-col>

    <!--数据列表-->
    <div class="table">
      <el-table :data="users" highlight-current-row v-loading="listLoading" :height="heightPx"
                @selection-change="handleSelectionChange" style="width: 100%;" stripe>
        <el-table-column inline-template :context="_self" fixed="left" label="操作" width="140"><span>
             <el-button :plain="true" type="info" size="small" @click="handleOpen(row)">编辑</el-button>
             <el-button type="danger" size="small" @click="handleDel(row)">删除</el-button></span>
        </el-table-column>
        <el-table-column type="selection" width="55"></el-table-column>
        <el-table-column label="用户名" width="120">
          <template scope="scope">
            <el-popover trigger="hover" placement="top" effect="light">
              <p>用户名: {{ scope.row.userName }}</p>
              <p>所属应用: {{ scope.row.clientName }}</p>
              <div slot="reference" class="name-wrapper">
                <el-tag>{{ scope.row.userName }}</el-tag>
              </div>
            </el-popover>
          </template>
        </el-table-column>
        <el-table-column prop="loginName" label="登录账号" width="120" sortable></el-table-column>
        <el-table-column prop="realName" label="真实名称" width="130" sortable></el-table-column>
        <el-table-column prop="typeCode" label="用户类型" :formatter="formatTypeCode" min-width="150" sortable>
          <template scope="scope">
            <el-tag :type="scope.row.typeCode === 'system' ? 'primary' : 'success'" :value="formatTypeCode"
                    close-transition>
              {{scope.row.typeCode === "system" ? '系统用户' : scope.row.typeCode === "client" ? '应用用户' : scope.row.typeCode}}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="isAdmin" label="管理员" :formatter="formatIsAdmin" min-width="150"
                         sortable></el-table-column>
        <el-table-column prop="booleanIsActive" label="是否激活" min-width="130" sortable>
          <template scope="scope">
          <el-switch
          v-model="scope.row.booleanIsActive"  on-color="#13ce66" off-color="#ff4949" on-text="ON" off-text="OFF"
          @change="handleSwitch(scope.row, $event)" :disabled="scope.row.isAdmin === 1 ? true : false" >
          </el-switch>
          </template>
        </el-table-column>
        <el-table-column prop="cardId" label="身份证号" min-width="170" sortable></el-table-column>
        <el-table-column prop="birthday" label="出生日期" :formatter="formatBirthday" width="170"
                         sortable></el-table-column>
        <el-table-column prop="email" label="邮箱" width="180" sortable></el-table-column>
        <el-table-column prop="mobilePhone" label="手机" min-width="150" sortable></el-table-column>
        <el-table-column prop="sex" label="性别" :formatter="formatSex" min-width="150" sortable></el-table-column>
        <el-table-column prop="createUser" label="创建人" min-width="120" sortable></el-table-column>
        <el-table-column prop="createTime" label="创建时间" :formatter="formatTime" min-width="170"
                         sortable></el-table-column>
        <el-table-column prop="updateUser" label="修改人" min-width="120" sortable></el-table-column>
        <el-table-column prop="updateTime" label="修改时间" :formatter="formatUpdateTime" min-width="170"
                         sortable></el-table-column>
      </el-table>
    </div>

    <!--分页-->
    <div class="pager">
      <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :page-size="pageSize"
                     :page-sizes="pageSizes"
                     layout="total, sizes, prev, pager, next, jumper" :total="total">
      </el-pagination>
    </div>

    <!--编辑页面-->
    <el-dialog :title="editFormTitle" v-model="editFormVisible" :close-on-click-modal="false">
      <el-form :model="editForm" label-width="100px" :rules="editFormRules" ref="editForm">
        <el-row>
          <el-col :span="12">
            <el-form-item label="登录账号" prop="loginName">
              <el-input v-model="editForm.loginName" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="用户名" prop="userName">
              <el-input v-model="editForm.userName" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="真实名称" prop="realName">
              <el-input v-model="editForm.realName" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="身份证号" prop="cardId">
              <el-input v-model="editForm.cardId" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="出生日期" prop="birthday">
              <el-date-picker type="date" placeholder="选择日期" v-model="editForm.birthday"></el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="sex">
              <el-radio-group v-model="editForm.sex">
                <el-radio class="radio" label="M">男</el-radio>
                <el-radio class="radio" label="F">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="mobilePhone">
              <el-input v-model="editForm.mobilePhone" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系邮箱" prop="email">
              <el-input v-model="editForm.email" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="用户类型" prop="typeCode">
              <el-select v-model="editForm.typeCode" placeholder="请选择用户类型" @change="typeCodeSelectionChange">
                <el-option v-for="item in typeCodeOptions" :label="item.label" :value="item.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="分配应用" prop="clientIds">
              <el-select v-model="editForm.clientIds" multiple placeholder="请选择" :disabled="flag">
                <el-option
                  v-for="item in clientOptions" :label="item.name" :value="item.id">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="管理员" prop="isAdmin">
              <el-switch on-text="" off-text="" :disabled="!flag" v-model="editForm.isAdmin" @change="handleIsAdminSwitch($event)"></el-switch>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="是否可用" prop="isActive">
              <el-switch on-text="" off-text="" v-model="editForm.isActive" :disabled="disabled"></el-switch>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click.native="editFormVisible = false">取 消</el-button>
        <el-button type="primary" @click.native="handleSave" :loading="editLoading">{{btnEditText}}</el-button>
      </div>
    </el-dialog>
  </section>
</template>


<script>
  import moment from 'moment'
  import {getUsersListPage,activeUser, getApplicationsListPage, addUser, editUser, removeUser, passwordReset} from '../../api/api';
  export default {
    data() {
      return {
        filters: {
          loginName: '',
          userName: '',
          realName: '',
          typeCode: '',
          page: '',
          pageSize: '',
        },
        users: [],
        total: 0,
        page: 1,
        pageSize: 15,
        pageSizes: [15, 30, 50, 100],
        listLoading: false,
        editFormVisible: false,//编辑界面显是否显示
        editFormTitle: '编辑',//编辑界面标题
        editForm: {
          id: 0,
          loginName: '',
          userName: '',
          realName: '',
          cardId: '',
          birthday: '',
          mobilePhone: '',
          sex: '',
          typeCode: '',
          isAdmin: false,
          isActive: true,
          password: '',
          email: '',
          clientIds: [],

        },
        editLoading: false,
        btnEditText: '提 交',
        editFormRules: {
          loginName: [{required: true, message: '请输入登录名', trigger: 'blur'}],
          userName: [{required: true, message: '请输入用户名', trigger: 'blur'}],
          realName: [{required: true, message: '请输入姓名', trigger: 'blur'}],
          cardId: [{required: true, message: '请输入身份证号', trigger: 'blur'}],
          mobilePhone: [{required: true, message: '请输入手机号', trigger: 'blur'}],
          typeCode: [{required: true, message: '请选择用户类型', trigger: 'blur'}],
        },
        typeCodeOptions: [{
          value: 'system',
          label: '系统用户'
        }, {
          value: 'client',
          label: '应用用户'
        }],
        clientOptions: [],
        multipleSelection: [],
        flag: true,

        btnAuthorityText: "启用",
        disabled: false,
        heightPx: 670,
      }
    },
    methods: {
      //性别显示转换
      formatSex: function (row, column) {
        return row.sex === "M" ? '男' : row.sex === "F" ? '女' : '未知';
      },
      formatIsActive: function (row, column) {
        return row.isActive === 1 ? '是' : '否';
      },
      formatIsAdmin: function (row, column) {
        return row.isAdmin === 1 ? '是' : '否';
      },
      formatTime: function (row, column) {
        return moment(row.createTime).format('YYYY-MM-DD HH:mm:ss');
      },
      formatUpdateTime: function (row, column) {
        return moment(row.updateTime).format('YYYY-MM-DD HH:mm:ss');
      },
      formatBirthday: function (row, column) {
        return moment(row.birthday).format('YYYY-MM-DD');
      },
      formatTypeCode: function (row, column) {
        return row.typeCode === "system" ? '系统用户' : row.typeCode === "client" ? '应用用户' : row.typeCode;
      },
      //启用OR禁用
      handleSwitch(row, val){
        let _this = this;
        let para = {
          id: row.id,
          isActive: val == true ? 1 : 0,
        }
        var message = "启用成功";
        if(row.isActive == 1){
          message = "禁用成功";
        }
        activeUser(para).then((res) => {
          if (res.code === "S00001") {
            _this.$notify({
              title: '成功',
              message: message,
              type: 'success'
            });
            _this.editFormVisible = false;
            _this.getUsers();
          } else {
            _this.$message({
              message: res.message,
              type: 'warning'
            });
            _this.getUsers();
          }
        }).catch(() => {
        });
      },
      handleIsAdminSwitch(val){
        let _this = this;
        if(val){
          _this.editForm.isActive = true;
          _this.disabled = true;
        }else{
          _this.disabled = false;
        }
      },
      // 获取数据列表
      getUsers() {
        let _this = this;
        _this.filters.page = _this.page,
          _this.filters.pageSize = _this.pageSize,
          _this.listLoading = true;
        getUsersListPage(_this.filters).then((res) => {
          _this.total = res.data.total;
          _this.users = res.data.rows;
          _this.listLoading = false;
        });
      },
      //删除操作
      handleDel: function (row) {
        var _this = this;
        this.$confirm('确认删除该记录吗？', '提示', {
          type: 'warning'
        }).then(() => {
          _this.listLodading = true;
          let para = {id: row.id};
          removeUser(para).then((res) => {
            _this.listLoading = false;
            if (res.code != "S00001") {
              this.$message({
                message: res.message,
                type: 'warning'
              });
            } else {
              _this.$notify({
                title: '成功',
                message: '删除成功',
                type: 'success'
              });
              _this.getUsers();
            }
          }).catch(() => {
          });
        }).catch(() => {
          }
        );
      },
      handleOpen: function (row) {
        var flags;
        let _this = this;
        if (row.isActive == 1) {
          flags = true;
        } else {
          flags = false;
        }
        var admin;
        if (row.isAdmin == 1) {
          admin = true
        } else {
          admin = false
        }
        if (row === undefined || row === null) {
          this.editFormVisible = true;
          this.editFormTitle = '新增';
          this.editForm.id = 0;
        } else {
          this.editFormVisible = true;
          this.editFormTitle = '编辑';
          this.editForm.id = row.id;
          this.disabled = true;
        }
        this.editForm.userName = row === null ? "" : row.userName;
        this.editForm.realName = row === null ? "" : row.realName;
        this.editForm.loginName = row === null ? "" : row.loginName;
        this.editForm.cardId = row === null ? "" : row.cardId;
        this.editForm.birthday = row === null ? "" : row.birthday;
        this.editForm.mobilePhone = row === null ? "" : row.mobilePhone;
        this.editForm.sex = row === null ? "" : row.sex;
        this.editForm.email = row === null ? "" : row.email;
        this.editForm.typeCode = row === null ? "" : row.typeCode;
        this.editForm.isAdmin = row === null ? false : admin;
        this.editForm.isActive = row === null ? true : flags;
        this.editForm.clientIds = row === null ? [] : row.clientIds;
        let para = {
          page: 0,
          pageSize: 0,
        }
        _this.typeCodeSelectionChange(row.typeCode);
        getApplicationsListPage(para).then((res) => {
          _this.clientOptions = res.data.rows;
        }).catch(() => {
        });
      },
      //编辑 或 新增
      handleSave: function () {
        let _this = this;
        _this.$refs.editForm.validate(function (vaild) {
          if (vaild) {
            _this.$confirm('确认提交吗？', '提示', {}).then(() => {
              _this.editLodaing = true;
              _this.btnEditText = '提交中';
              //新增
              var revokedValue;
              if (_this.editForm.isActive == true) {
                revokedValue = 1;
              } else {
                revokedValue = 0;
              }
              var isAdminValue;
              if (_this.editForm.isAdmin == true) {
                isAdminValue = 1;
              } else {
                isAdminValue = 0;
              }
              let para = {
                id: _this.editForm.id,
                loginName: _this.editForm.loginName,
                userName: _this.editForm.userName,
                realName: _this.editForm.realName,
                cardId: _this.editForm.cardId,
                birthday: _this.editForm.birthday,
                mobilePhone: _this.editForm.mobilePhone,
                sex: _this.editForm.sex,
                typeCode: _this.editForm.typeCode,
                isAdmin: isAdminValue,
                isActive: revokedValue,
                email: _this.editForm.email,
                clientIds: _this.editForm.clientIds
              };
              if (_this.editForm.id == 0 || _this.editForm.id == undefined) {
                addUser(para).then((res) => {
                  _this.editLoading = false;
                  _this.btnEditText = '提交';
                  if (res.code != "S00001") {
                    _this.$message({
                      message: res.message,
                      type: 'warning'
                    });
                    _this.editLodaing = false;
                    _this.btnEditText = '提交';
                  } else {
                    _this.$notify({
                      title: '成功',
                      message: '操作成功',
                      type: 'success'
                    });
                    _this.editFormVisible = false;
                    _this.getUsers();
                  }
                }).catch(() => {
                });
              } else {
                //编辑
                editUser(para).then((res) => {
                  _this.editLoading = false;
                  _this.btnEditText = '提交';
                  if (res.code === "S00001") {
                    _this.$notify({
                      title: '成功',
                      message: '操作成功',
                      type: 'success'
                    });
                    _this.editFormVisible = false;
                    _this.getUsers();
                  } else {
                    _this.$message({
                      message: res.message,
                      type: 'warning'
                    });
                    _this.editLodaing = false;
                    _this.btnEditText = '提交';
                  }
                }).catch(() => {
                });
              }
            }).catch(() => {
            });
          }
        })
      },
      handleCurrentChange: function (val) {
        this.page = val;
        this.getUsers();
      },
      handleSizeChange: function (val) {
        this.pageSize = val;
        this.getUsers();
      },
      rest: function () {
        this.filters.loginName = '';
        this.filters.realName = '';
        this.filters.userName = '';
        this.filters.typeCode = '';
      },
      //密码重置
      handlePasswordReset: function () {
        var _this = this;
        if (_this.multipleSelection.length == 0) {
          _this.$message({
            message: "请选择要重置的用户！",
            type: 'warning'
          });
          return;
        }
        if (_this.multipleSelection.length > 1) {
          _this.$message({
            message: "请选择一个用户！",
            type: 'warning'
          });
          return;
        }
        _this.$confirm("确定要重置[" + _this.multipleSelection[0].loginName + "]的密码吗？", '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          let param = {id: _this.multipleSelection[0].id};
          passwordReset(param).then((res) => {
            if (res.code != "S00001") {
              _this.$message({
                message: res.message,
                type: 'warning'
              });
            } else {
              _this.$notify({
                title: '成功',
                message: '操作成功',
                type: 'success'
              });
            }
          }).catch(() => {
            console.log(" password rest failure.");
          })
        }).catch(() => {
          console.log(" password rest cancel.");
        })
      },
      handleSelectionChange(val) {
        this.multipleSelection = val;
      },
      typeCodeSelectionChange(val){
        var _this = this;
        if (val === "client") {
          _this.editForm.isAdmin = false;
          _this.flag = false;
        } else {
          _this.flag = true;
          _this.editForm.clientIds = [];
        }
      },
    },
    mounted() {
      this.getUsers();
      this.heightPx = document.body.scrollHeight-300;
    }

  }


</script>

<style scoped>
  .search-toolbar {
    margin: 10px 0px 10px;
    border: 1px solid rgba(32, 160, 255, 0.19);
    box-sizing: border-box;
  }

  .panel-box-left {
    width: auto;
    float: left;
  }

  .panel-box-right {
    width: auto;
    float:right;
    padding-right: 5px;
  }

  .el-row {
    margin-bottom: 5px;

  &
  :last-child {
    margin-bottom: 0;
  }

  }
  .el-col {
    border-radius: 4px;
  }


</style>
