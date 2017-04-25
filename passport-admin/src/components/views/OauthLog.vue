<template>
  <section>
    <!--search toolbar start -->
    <el-col :span="24" class="toolbar search-toolbar">
      <el-form :inline="true" :model="filters">
        <div class="panel-box-left">
          <el-form-item>
            <el-input v-model="filters.loginName" placeholder="登录名"></el-input>
          </el-form-item>
          <el-form-item>
            <el-date-picker type="datetime" placeholder="开始时间" v-model="filters.startTimeDate"></el-date-picker>
          </el-form-item>
          <el-form-item>
            <el-date-picker type="datetime" placeholder="结束时间" picker-options="pickerOptions" v-model="filters.endTimeDate"></el-date-picker>
          </el-form-item>
        </div>

        <div class="panel-box-right">
          <el-form-item>
            <el-button type="primary" size="small" icon="search" v-on:click="fetchData">查询</el-button>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" size="small" v-on:click="reset">重置</el-button>
          </el-form-item>
        </div>

      </el-form>
    </el-col>
    <!--search toolbar end-->

    <!--option toolbar start -->
    <el-col :span="24">
      <el-form :inline="true">
        <el-form-item>
          <!--<el-button type="primary" size="small" @click="add">新增</el-button>-->
          <div style="height: 30px"></div>
        </el-form-item>
      </el-form>
    </el-col>
    <!--option toolbar start -->

    <!-- data Grid start -->
    <div class="table">
      <!--<el-table :data="OAuthDatas" highlight-current-row border v-loading="listLoading" height="644" style="width:100%;">-->
      <el-table :data="OAuthDatas" highlight-current-row border v-loading="listLoading" :height="heightPx"
                style="width:100%;">
        <el-table-column lable-name="序号" type="index" width="60px"></el-table-column>
        <el-table-column prop="loginName" label="登录名" min-width="80px" sortable></el-table-column>
        <el-table-column prop="appKey" label="应用appkey" min-width="80px" sortable></el-table-column>
        <el-table-column prop="code" label="授权码" min-width="120px" sortable></el-table-column>
        <el-table-column prop="revoked" label="是否取消" min-width="80px" :formatter="formatRevoked"
                         sortable></el-table-column>
        <el-table-column prop="expiresTime" label="有效时间" min-width="100px" :formatter="formatExpiresTime"
                         sortable></el-table-column>
        <el-table-column prop="timestamps" label="授权时间" min-width="100px" :formatter="formatTimestamps"
                         sortable></el-table-column>
      </el-table>
    </div>
    <!-- dataGrid end -->

    <!-- page start -->
    <div class="pager">
      <el-pagination v-bind:page-size="pageSize" :page-sizes="pageSizes" :total="total"
                     layout="total,sizes,prev,pager,next,jumper"
                     v-on:size-change="sizeChange" v-on:current-change="pageIndexChange">
      </el-pagination>
    </div>
    <!-- page end -->

  </section>
</template>

<script>
  import moment from 'moment'
  import {getOauthLogListPage} from '../../api/api';
  export default {
    data() {
      return {
        listLoading: false,
        filters: {
          loginName: '',
          oauthDate: '',
          page: '',
          pageSize: '',
          startTime: '',
          endTime: '',
          startTimeDate: '',
          endTimeDate: '',
        },
        page: 1,
        pageSize: 15,
        pageSizes: [15, 30, 50, 100],
        total: 0,
        OAuthDatas: [],
        heightPx: 644,
      }
    },
    methods: {
      //格式化
      formatRevoked: function (row, column) {
        return row.revoked === 1 ? '是' : '否';
      },
      formatExpiresTime: function (row, column) {
        return row.expiresTime + 'ms';
      },
      formatTimestamps: function (row, column) {
        return moment(row.timestamps).format('YYYY-MM-DD HH:mm:ss');
      },
      // 获取数据列表
      fetchData: function () {
        let _this = this;
          _this.filters.page = _this.page,
          _this.filters.pageSize = _this.pageSize,
          _this.filters.startTime = !!_this.filters.startTimeDate ? _this.filters.startTimeDate.getTime() : null;
          _this.filters.endTime = !!_this.filters.endTimeDate ? _this.filters.endTimeDate.getTime() : null;
          _this.listLoading = true;
        getOauthLogListPage(_this.filters).then((res) => {
          _this.total = res.data.total;
          _this.OAuthDatas = res.data.rows;
          _this.listLoading = false;
        });
      },
      reset: function () {
        this.filters = {}
      },
      sizeChange: function (pageSize) {
        this.pageSize = pageSize;
        this.fetchData();
      },
      pageIndexChange: function (val) {
        this.page = val;
        this.fetchData();
      }
    },
    mounted() {
      this.fetchData();
      this.heightPx = document.body.scrollHeight-326;
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
</style>
