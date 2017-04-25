<template>
  <div id="layout-sidebar">
    <aside class="main-sidebar">
      <!-- sidebar: style can be found in sidebar.less -->
      <section class="sidebar">
        <el-menu :default-active="$router.path" class="el-menu-vertical" theme="dark" unique-opened router>
          <template v-for="(item,index) in $router.options.routes[2].children" v-if="!item.hidden">
              <el-submenu :index="index+''" v-if="!!item.children">
                <template slot="title"><i :class="item.iconCls"></i><span>{{item.name}}</span></template>
                <el-menu-item v-for="child in item.children" :index="child.path" v-if="!child.hidden"><span>{{child.name}}</span></el-menu-item>
              </el-submenu>
              <el-menu-item v-if="!!!item.children" :index="item.path">
                 <i :class="item.iconCls"></i><span>{{item.name}}</span>
              </el-menu-item>
          </template>
        </el-menu>
      </section>
      <!-- /.sidebar -->
    </aside>
  </div>
</template>


