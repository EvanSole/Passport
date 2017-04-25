<template>
    <div id = "pager-component" class="pager">
      <el-pagination v-bind:current-Page="pageIndex" v-bind:page-size="pageSize" :total="total"
                     layout="total,sizes,prev,pager,next,jumper" v-bind:page-sizes="pageSizes"
                     v-on:size-change="sizeChange" v-on:current-change="pageIndexChange">
      </el-pagination>
    </div>
</template>

