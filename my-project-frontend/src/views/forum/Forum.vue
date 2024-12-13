<script setup>
import {useStore} from "@/store";
import {get} from "@/net";


const store = useStore()

//获取types新址
get('/api/forum/types', data => {
  const array = []
  //彩色渐变效果，添加一个全部
  array.push({name: '全部', id: 0, color: 'linear-gradient(45deg, white, red,orange,gold,green,blue)'})
  data.forEach(d => array.push(d))
  store.forum.types = array
})
</script>

<template>
  <div>
    <router-view v-slot="{ Component }">
      <transition name="el-fade-in-linear" mode="out-in">
        <!--保持页面不需要重复刷新keep-alive -->
        <keep-alive include="TopicList">
          <component :is="Component"/>
        </keep-alive>
      </transition>
    </router-view>
    <el-backtop target=".main-content-page .el-scrollbar__wrap" :right="20" :bottom="70" />
  </div>
</template>

<style scoped>

</style>