<script setup>
import {get, logout} from '@/net'
import router from "@/router";
import {useStore} from "@/store";
import {ref} from "vue";
import {
  Bell,
  ChatDotSquare, Collection, DataLine, Document, Files,
  Location, Lock,
  Monitor,
  Notification, Operation,
  Position,
  School,
  Umbrella, User
} from "@element-plus/icons-vue";

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
        <el-aside width="230px">
            <!--设置侧边栏滚动条-->
          <el-scrollbar style="min-height: calc(100vh - 55px)">
            <!-- min-height和height的区别看不出来啊 QUESTION-->
            <!-- 100vh-55px，55px为header高度。导航菜单高度拉满，占据整个左边-->
            <!-- 默认展示子菜单中的1-1，也就是下面设置的index为1-1的校园论坛-->
            <el-menu
                default-active="1-1"
                style="height: calc(100vh - 55px)">
              <el-sub-menu index="1">
                <template #title>
                  <el-icon><location/></el-icon>
                  <span><b>校园论坛</b></span>
                </template>
                <el-menu-item index="1-1">
                  <template #title>
                    <el-icon><chat-dot-square/></el-icon>
                    帖子广场
                  </template>
                </el-menu-item>
                <el-menu-item>
                  <template #title>
                    <el-icon><bell/></el-icon>
                    失物招领
                  </template>
                </el-menu-item>
                <el-menu-item>
                  <template #title>
                    <el-icon><Notification/></el-icon>
                    校园活动
                  </template>
                </el-menu-item>
                <el-menu-item>
                  <template #title>
                    <el-icon><Umbrella/></el-icon>
                    表白墙
                  </template>
                </el-menu-item>
                <el-menu-item>
                  <template #title>
                    <el-icon><School/></el-icon>
                    海文考研
                    <el-tag style="margin-left: 10px" size="small">合作机构</el-tag>
                  </template>
                </el-menu-item>
              </el-sub-menu>

              <el-sub-menu index="2">
                <template #title>
                  <el-icon><Position/></el-icon>
                  <span><b>探索与发现</b></span>
                </template>
                <el-menu-item>
                  <template #title>
                    <el-icon><Document/></el-icon>
                    成绩查询
                  </template>
                </el-menu-item>
                <el-menu-item>
                  <template #title>
                    <el-icon><Files /></el-icon>
                    班级课程表
                  </template>
                </el-menu-item>
                <el-menu-item>
                  <template #title>
                    <el-icon><Monitor /></el-icon>
                    校务通知
                  </template>
                </el-menu-item>
                <el-menu-item>
                  <template #title>
                    <el-icon><Collection/></el-icon>
                    在线图书馆
                  </template>
                </el-menu-item>
                <el-menu-item>
                  <template #title>
                    <el-icon><DataLine/></el-icon>
                    预约教室
                  </template>
                </el-menu-item>
              </el-sub-menu>

              <el-sub-menu index="3">
                <template #title>
                  <el-icon><Operation/></el-icon>
                  <span><b>个人设置</b></span>
                </template>
                <el-menu-item>
                  <template #title>
                    <el-icon><User/></el-icon>
                    个人信息设置
                  </template>
                </el-menu-item>
                <el-menu-item>
                  <template #title>
                    <el-icon><Lock/></el-icon>
                    账号安全设置
                  </template>
                </el-menu-item>
              </el-sub-menu>
            </el-menu>
          </el-scrollbar>


        </el-aside>
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
