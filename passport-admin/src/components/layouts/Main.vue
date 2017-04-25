<template>
  <div id="layout-main">
    <div>
      <!--header-->
      <layout-header></layout-header>

      <!-- Left side column. contains the logo and sidebar -->
      <layout-sidebar></layout-sidebar>

       <!-- Content Wrapper. Contains page content -->
        <div class="content-wrapper" id="main">
          <div class="outborder">
          <strong style="width:200px;float:left;color: #475669;">{{$route.name}}</strong>
          <el-breadcrumb separator="/" style="float:right;">
            <el-breadcrumb-item v-for="item in $route.matched">
              {{ item.name }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <router-view></router-view>

      </div>
      <!--footer-->
      <!--<layout-footer></layout-footer>-->
    </div>
  </div>
</template>


<script>
  import layoutHeader from 'components/layouts/Header'
  import layoutSidebar from 'components/layouts/Sidebar'
  import layoutFooter from 'components/layouts/Footer'

  export default {
    components: {
      layoutHeader,
      layoutSidebar,
      layoutFooter
    }
  }

</script>
<style>
.outborder{
  height: 30px;
  align-content: center;
}
#main{
  padding:20px;
}
.toolbar .el-form-item {
		margin-bottom: 10px;
	}

	.toolbar {
		background: #fff;
		padding: 10px 10px 0px 10px;
	}
</style>
