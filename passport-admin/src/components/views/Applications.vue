<template>
  <section>
    <!-- search toolbar start-->
    <el-col :span="24" class="toolbar search-toolbar">
      <el-form :inline="true" :model="filters">
        <div class="panel-box-left">
          <el-form-item>
            <el-input v-model="filters.name" placeholder="应用名称"></el-input>
          </el-form-item>
          <el-form-item>
            <el-input v-model="filters.appKey" placeholder="应用key"></el-input>
          </el-form-item>
          <el-form-item>
            <el-input v-model="filters.appSecret" placeholder="应用secret"></el-input>
          </el-form-item>
        </div>
        <div class="panel-box-right">
          <el-form-item>
            <el-button type="primary" size="small" v-on:click="getApplicationLists">查询</el-button>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" size="small" v-on:click="rest">重置</el-button>
          </el-form-item>
        </div>
      </el-form>
    </el-col>
    <!-- search toolbar end -->

    <!--option toolbar start -->
    <el-col :span="24">
      <el-form :inline="true" :model="filters" style="height:45px">
        <el-form-item>
          <el-button type="primary" size="small" @click="handleEditForm(null)">新增</el-button>
        </el-form-item>
      </el-form>
    </el-col>
    <!--option toolbar end -->

    <!-- DataGrid start-->
    <div class="table">
      <el-table :data="applicationsData" highlight-current-row v-loading="listLoading" :height="heightPx"
                style="width: 100%;" stripe>
        <el-table-column inline-template :context="_self" fixed="left" label="操作" min-width="220">
          <span>
              <el-button size="small" @click="handleEditForm(row)">编辑</el-button>
              <el-button size="small" type="danger" @click="handleDel(row)">删除</el-button>
              <el-button size="small" :plain="true" type="warning" @click="handleAssigningUsers(row)">分配用户</el-button>
            </span>
        </el-table-column>
        <el-table-column prop="name" label="应用名称" min-width="180" sortable></el-table-column>
        <el-table-column prop="appKey" label="应用key" min-width="150" sortable></el-table-column>
        <el-table-column prop="appSecret" label="应用secret" min-width="320"></el-table-column>
        <el-table-column prop="redirectUri" label="回调地址" min-width="300" sortable></el-table-column>
        <el-table-column prop="authType" label="认证类型" min-width="130" :formatter="formatAuthType"
                         sortable></el-table-column>
        <el-table-column prop="revoked" label="是否有效" :formatter="formatRevoked" min-width="130"
                         sortable></el-table-column>
        <el-table-column prop="appIcon" label="应用图标" min-width="120" sortable></el-table-column>
        <el-table-column prop="description" label="应用描述" min-width="200" sortable></el-table-column>
        <el-table-column prop="owner" label="应用所有人" min-width="150" sortable></el-table-column>
        <el-table-column prop="ownerEmail" label="联系邮箱" min-width="150" sortable></el-table-column>
        <el-table-column prop="ownerContact" label="联系电话" min-width="150" sortable></el-table-column>
        <el-table-column prop="createUser" label="创建人" min-width="120" sortable></el-table-column>
        <el-table-column prop="createTime" label="创建时间" :formatter="formatTime" min-width="160"
                         sortable></el-table-column>
        <el-table-column prop="updateUser" label="修改人" min-width="120" sortable></el-table-column>
        <el-table-column prop="updateTime" label="修改时间" :formatter="formatUpdateTime" min-width="160"
                         sortable></el-table-column>
      </el-table>
    </div>
    <!-- DataGrid end-->

    <!-- pager -->
    <div class="pager">
      <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :page-size="pageSize"
                     :page-sizes="pageSizes"
                     layout="total, sizes, prev, pager, next, jumper" :total="totals">
      </el-pagination>
    </div>

    <!-- edit template start-->
    <el-dialog :title="editFormTitle" v-model="editFormVisible" :close-on-click-modal="false">
      <el-form :model="editForm" label-width="120px" :rules="editFormRules" ref="editForm">
        <el-form-item label="应用名称" prop="name">
          <el-input v-model="editForm.name" auto-complete="off"></el-input>
        </el-form-item>
        <el-form-item label="回调地址" prop="redirectUri">
          <el-input placeholder="请输入内容" v-model="editForm.redirectUri"></el-input>
        </el-form-item>
        <el-form-item label="认证类型" prop="authType">
          <el-select v-model="editForm.authType" placeholder="请选择认证类型">
            <el-option v-for="item in options" :label="item.label" :value="item.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="应用描述" prop="description">
          <el-input type="textarea" v-model="editForm.description" auto-complete="off"></el-input>
        </el-form-item>
        <el-form-item label="应用图标" prop="appIcon">
          <el-input v-model="editForm.appIcon" auto-complete="off"></el-input>
        </el-form-item>
        <el-form-item label="应用所有人" prop="owner">
          <el-input v-model="editForm.owner" auto-complete="off"></el-input>
        </el-form-item>
        <el-form-item label="联系电话" prop="ownerContact">
          <el-input v-model="editForm.ownerContact" auto-complete="off"></el-input>
        </el-form-item>
        <el-form-item label="联系邮箱" prop="ownerEmail">
          <el-input v-model="editForm.ownerEmail" auto-complete="off"></el-input>
        </el-form-item>
        <el-form-item label="是否有效" prop="revoked">
          <el-switch on-text="" off-text="" v-model="editForm.revoked" :formatter="formatRevoked"></el-switch>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click.native="editFormVisible = false">取 消</el-button>
        <el-button type="primary" @click.native="handleSave" :loading="editLoading">{{btnEditText}}</el-button>
      </div>
    </el-dialog>

    <!--permission assignment-->
    <el-dialog title="用户分配" v-model="permissionFormVisible">
      <el-form :model="permission" label-width="100px" :rules="permissionRules" ref="permission">
        <el-form-item lable="应用id" prop="id" hidden="hidden">
          <el-input v-model="permission.id" auto-complete="off"></el-input>
        </el-form-item>
        <el-form-item label="选择用户：" prop="revoked">
          <el-select v-model="permission.permissionUser" multiple filterable allow-create placeholder="请选择要授权的用户">
            <el-option v-for="item in Users" :label="item.loginName" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click.native="permissionFormVisible = false">取 消</el-button>
        <el-button type="primary" @click.native="handleAssigningSave" :loading="permissionLoading">
          {{btnPermissionText}}
        </el-button>
      </div>
    </el-dialog>

  </section>
</template>


<script>
  import moment from 'moment'
  import {
    getUsersListPage,
    getApplicationsListPage,
    addApplication,
    editApplication,
    removeApplication,
    clientAndUser,
    findClientUser
  } from '../../api/api';
  export default {
    data() {
      return {
        filters: {
          name: '',
          appKey: '',
          appSecret: '',
          page: '',
          pageSize: '',
        },
        applicationsData: [],
        totals: 0,
        page: 1,
        pageSize: 15,
        pageSizes: [15, 30, 50, 100],
        listLoading: false,
        editFormVisible: false,//编辑界面显是否显示
        editFormTitle: '编辑',//编辑界面标题
        editForm: {
          id: 0,
          name: '',
          appKey: '',
          appSecret: '',
          redirectUri: '',
          authType: '',
          description: '',
          appIcon: '',
          owner: '',
          ownerEmail: '',
          ownerContact: '',
          revoked: '',
        },
        editLoading: false,
        btnEditText: '提 交',
        editFormRules: {
          name: [{required: true, message: '请输入用户名', trigger: 'blur'}],
          redirectUri: [{required: true, message: '请输入回调地址', trigger: 'blur'}],
          authType: [{required: true, message: '请选择认证类型', trigger: 'blur'}],
          owner: [{required: true, message: '请输入应用所有人', trigger: 'blur'}],
          ownerContact: [{required: true, message: '请输入联系电话', trigger: 'blur'}],
        },
        options: [{
          value: 'authorization_code',
          label: '授权码模式'
        }],
        clientUsers: '',
        permissionFormVisible: false,//授权界面是否显示
        permissionLoading: false,
        btnPermissionText: '提交',
        permission: {
          id: '',
          permissionUser: '',
        },
        Users: [],
        permissionRules: {
          permissionUser: [
            {required: true, message: '请选择用户', trigger: 'blur'}
          ]
        },
        heightPx: 670,

      }
    },
    methods: {
      //格式化
      formatRevoked: function (row, column) {
        return row.revoked === 1 ? '是' : '否';
      },
      formatTime: function (row, column) {
        return moment(row.createTime).format('YYYY-MM-DD HH:mm:ss');
      },
      formatUpdateTime: function (row, column) {
        return moment(row.updateTime).format('YYYY-MM-DD HH:mm:ss');
      },
      formatAuthType: function (row, column) {
        return row.authType === "authorization_code" ? "授权码模式" : row.authType === "password" ? "密码模式" : row.authType;
      },

      // 获取数据列表
      getApplicationLists: function () {
        let _this = this;
        _this.listLoading = true;
        _this.filters.page = _this.page;
        _this.filters.pageSize = _this.pageSize;
        getApplicationsListPage(_this.filters).then((res) => {
          _this.applicationsData = res.data.rows;
          _this.totals = res.data.total;
          _this.listLoading = false;
        }).catch(() => {
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
          removeApplication(para).then((res) => {
            console.log(res);
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
            }
            _this.getApplicationLists();
          }).catch(() => {
          });
        }).catch(() => {
          }
        );
      },
      handleEditForm: function (row) {
        this.editFormVisible = true;
        var flags;
        if (row === undefined || row === null) {
          this.editFormTitle = '新增';
          this.editForm.id = 0;
          this.editForm.revoked = true;
          this.editForm.authType = "authorization_code";
        } else {
          this.editFormTitle = '编辑';
          this.editForm.id = row.id;
          if (row.revoked == 1) {
            flags = true;
          } else {
            flags = false;
          }
          this.editForm.revoked = flags;
          this.editForm.authType = row === null ? "" : row.authType;
        }
        this.editForm.appKey = row === null ? "" : row.appKey;
        this.editForm.appSecret = row === null ? "" : row.appSecret;
        this.editForm.name = row === null ? "" : row.name;
        this.editForm.appIcon = row === null ? "" : row.appIcon;
        this.editForm.redirectUri = row === null ? "" : row.redirectUri;
        this.editForm.description = row === null ? "" : row.description;
        this.editForm.owner = row === null ? "" : row.owner;
        this.editForm.ownerEmail = row === null ? "" : row.ownerEmail;
        this.editForm.ownerContact = row === null ? "" : row.ownerContact;
      },
      //编辑 或 新增
      handleSave: function () {
        let _this = this;
        _this.$refs.editForm.validate(function (valid) {
          if (valid) {
            _this.$confirm('确认提交吗？', '提示', {}).then(() => {
              _this.editLodaing = true;
              _this.btnEditText = '提交中';
              //新增
              var revokedValue;
              if (_this.editForm.revoked == true) {
                revokedValue = 1;
              } else {
                revokedValue = 0;
              }
              let para = {
                name: _this.editForm.name,
                appKey: _this.editForm.appKey,
                appSecret: _this.editForm.appSecret,
                redirectUri: _this.editForm.redirectUri,
                authType: _this.editForm.authType,
                description: _this.editForm.description,
                owner: _this.editForm.owner,
                ownerEmail: _this.editForm.ownerEmail,
                ownerContact: _this.editForm.ownerContact,
                appIcon: _this.editForm.appIcon,
                revoked: revokedValue,
              };
              if (_this.editForm.id === 0 || _this.editForm.id === undefined) {
                addApplication(para).then((res) => {
                  _this.editLoading = false;
                  _this.btnEditText = '提交';
                  if (res.code != "S00001") {
                    _this.$message({
                      message: message,
                      type: 'warning'
                    });
                    _this.editLodaing = false;
                    _this.btnEditText = '提交';
                  } else {
                    _this.$notify({
                      title: '成功',
                      message: '提交成功',
                      type: 'success'
                    });
                    _this.editFormVisible = false;
                    _this.getApplicationLists();
                  }
                }).catch(() => {
                });
              } else {
                para["id"] = _this.editForm.id;
                editApplication(para).then((res) => {
                  _this.editLoading = false;
                  _this.btnEditText = '提交';
                  if (res.code != "S00001") {
                    _this.$message({
                      message: res.message,
                      type: 'warning'
                    });
                  } else {
                    _this.$notify({
                      title: '成功',
                      message: '提交成功',
                      type: 'success'
                    });
                  }
                  _this.editFormVisible = false;
                  _this.getApplicationLists();
                }).catch(() => {
                });
              }
            }).catch(() => {
            });
          }
        });
      },
      handleCurrentChange: function (val) {
        this.page = val;
        this.getApplicationLists();
      },
      handleSizeChange: function (val) {
        this.pageSize = val;
        this.getApplicationLists();
      },
      rest: function () {
        this.filters = {};
      },

      //分配用户
      handleAssigningUsers: function (row) {
        this.permissionFormVisible = true;
        var _this = this;
        _this.permission.id = row.id;
        let param = {
          id: row.id,
          typeCode: 'client'
        };
        //已分配的用户
        findClientUser(param).then(res => {
          //已分配用户集合
          _this.permission.permissionUser = res.data.allocatedUsers;
          //未分配用户集合
          _this.Users = res.data.unallocatedUsers;
        }).catch(() => {
        });
      },
      //授权提交
      handleAssigningSave: function () {
        let _this = this;
        _this.$refs.permission.validate(function (valid) {
          if (valid) {
            _this.$confirm('确认提交吗？', '提示', {}).then(() => {
              _this.paLoading = true;
              _this.btnPermissionText = '提交中';
              let para = {
                clientId: _this.permission.id,
                user: _this.permission.permissionUser,
              };
              _this.permission.permissionUser = '';
              clientAndUser(para).then((res) => {
                _this.permissionLoading = false;
                if (res.code != "S00001") {
                  this.$message({
                    message: res.message,
                    type: 'warning'
                  });
                } else {
                  _this.$notify({
                    title: '成功',
                    message: '提交成功',
                    type: 'success'
                  });
                }
              }).catch(() => {
              });
              _this.permissionFormVisible = false;
              _this.permissionLoading = false;
            }).catch(() => {
            });
          }
        });
      }
    },

    mounted() {
      this.getApplicationLists();
      this.heightPx = document.body.scrollHeight - 300;
    },

  }
  window.onresize = function () {
    if (document.body.scrollWidth < 768) {
      this.heightPx = document.body.scrollHeight - 400;
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
    float: right;
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
