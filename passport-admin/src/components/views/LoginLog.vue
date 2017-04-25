<template>
    <section>
        <!--search toolbar start -->
        <el-col :span="24" class="toolbar search-toolbar">
            <el-form :inline="true" :model="filters">
                <div class="panel-box-left">
                    <el-form-item>
                        <el-input v-model="filters.name" placeholder="登录名"></el-input>
                    </el-form-item>
                    <el-form-item>
                        <el-date-picker type="date" placeholder="登录时间" v-model="filters.oauthDate"></el-date-picker>
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
	        <el-table highlight-current-row border v-loading="listLoading" height="644" style="width:100%;">
	            <el-table-column lable-name="序号" type="index" width="60px"></el-table-column>
	            <el-table-column prop="loginName" label="登录名" min-width="80px" sortable></el-table-column>
              <el-table-column prop="appkey" label="应用appkey" min-width="80px" sortable></el-table-column>
              <el-table-column prop="loginIP" label="客户端IP" min-width="80px" sortable></el-table-column>
              <el-table-column prop="loginDateTiem" label="登录时间" min-width="120px" sortable></el-table-column>
	            <el-table-column prop="loginDateTiem" label="登出时间" min-width="120px" sortable></el-table-column>
	            <el-table-column prop="loginDateTiem" label="消息" min-width="120px" sortable></el-table-column>
	        </el-table>
        </div>
        <!-- dataGrid end -->

      <!-- page start -->
      <div class="pager">
        <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :page-size="pageSize" :page-sizes="pageSizes"
                       layout="total, sizes, prev, pager, next, jumper" :total="total">
        </el-pagination>
      </div>
      <!-- page end -->

    </section>
</template>

<script>
    export default {
        data() {
            return {
                listLoading: false,
                filters: {
                    name: '',
                    oauthDate: ''
                },
                page: 1,
                pageSize: 15,
                pageSizes:[15,30,50,100],
                total: 0,
            }
        },
        methods: {
            // 获取数据列表
            fetchData: function () {

            },
            reset: function () {
            },
            handleSizeChange: function (pageSize) {
                this.pageSize = pageSize;
                this.fetchData();
            },
            handleCurrentChange: function (val) {
                this.page = val;
                this.fetchData();
            }
        },
        mounted() {
            this.fetchData();
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
        width: 90%;
        float: left;
    }

    .panel-box-right {
        width: 10%;
        float: right;
        padding-right: 5px;
    }
</style>
