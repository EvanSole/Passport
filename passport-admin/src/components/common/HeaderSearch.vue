<template>
  <div id="header-search" class="panel-box-right">
    <el-form-item>
      <el-button type="primary" size="small" icon="search" :click="fetchData">查询</el-button>
    </el-form-item>
    <el-form-item>
      <el-button type="primary" size="small" :click="reset">重置</el-button>
    </el-form-item>
  </div>
</template>
