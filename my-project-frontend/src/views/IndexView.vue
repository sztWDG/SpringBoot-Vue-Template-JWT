<script setup>
import {get, logout} from '@/net'
import router from "@/router";
import {useStore} from "@/store";
import {ref} from "vue";

const store = useStore();
const loading = ref(true);

get('/api/user/info', (data)=>{
  store.user = data;
  loading.value = false;
})

function userLogout() {
  logout(() => router.push("/"))
}
</script>


<template>
  <div class="man-content" v-loading="loading" element-loading-text="正在进入，请稍后...">
    <el-container style="height: 100%" v-if="!loading">
      <el-header class="man-content-header" style="display:flex;justify-content: center;align-items: center">
        <el-image class="logo" src="https://element-plus.org/images/element-plus-logo.svg" style="margin-right: auto"/>
          <div class="user-info" style="font-size: 25px;color: indigo;text-align: center;flex: 1">

            <div class="profile">
              <div>{{store.user.username}}</div>
              <div>{{store.user.email}}</div>
            </div>
            <el-avatar src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"/>
            Annie
          </div>
      </el-header>
      <el-container>
        <el-aside width="200px">Aside</el-aside>
        <el-main>Main</el-main>
      </el-container>
    </el-container>
  </div>
</template>



<style scoped lang="less">
.man-content{
  height: 100vh;
  width: 100vw;
}

.man-content-header{
  border-bottom: solid 1px var(--el-border-color);
  height: 55px;
  display: flex;
  align-items: center;
  box-sizing: border-box;

  .logo{
    height: 32px;
  }

  .user-info{
    display: flex;
    justify-content: flex-end;
    align-items: center;
  }

  .profile{
    text-align: right;
    margin-right: 20px;

    :first-child{
      font-size: 18px;
      font-weight: bold;
      line-height: 20px;
    }

    :last-child{
      font-size: 10px;
      color: gray;
    }

  }
}
</style>
