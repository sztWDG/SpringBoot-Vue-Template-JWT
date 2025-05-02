<script setup>
import {get} from '@/net'
import {inject, reactive, ref} from "vue";
import {
  Bell,
  ChatDotSquare, Check, Collection, DataLine, Document, Files,
  Location, Lock, Message,
  Monitor,
  Notification, Operation,
  Position,
  School, Search,
  Umbrella, User
} from "@element-plus/icons-vue";
import LightCard from "@/components/LightCard.vue";

const userMenu = [
  {
    title: '校园论坛', icon: Location, sub: [
      { title: '帖子广场', icon: ChatDotSquare, index: '/index' },
      { title: '失物招领', icon: Bell },
      { title: '校园活动', icon: Notification },
      { title: '表白墙', icon: Umbrella },
      { title: '海文考研', icon: School }
    ]
  }, {
    title: '探索与发现', icon: Position, sub: [
      { title: '成绩查询', icon: Document },
      { title: '班级课程表', icon: Files },
      { title: '教务通知', icon: Monitor },
      { title: '在线图书馆', icon: Collection },
      { title: '预约教室', icon: DataLine }
    ]
  }, {
    title: '个人设置', icon: Operation, sub: [
      { title: '个人信息设置', icon: User, index: '/index/user-setting' },
      { title: '账号安全设置', icon: Lock, index: '/index/privacy-setting' }
    ]
  }
]

//通过inject来完成store了
// const store = useStore();
const loading = inject('userLoading')

const searchInput = reactive({
  type: '1',
  text: ''
})

const notification = ref([])
//此处为了避免频繁切换导致的不必要的重新加载，于是放入App.Vue中，优化系统
// get('/api/user/info', (data) => {
//   store.user = data;
//   loading.value = false;
// })

const loadNotification =
    () => get('/api/notification/list', data => notification.value = data)
loadNotification()


// function userLogout() {
//   logout(() => router.push("/"))
// }

function confirmNotification(id, url) {
  get(`/api/notification/delete?id=${id}`, () => {
    loadNotification()
    window.open(url)
  })
}

function deleteAllNotification() {
  get(`/api/notification/delete-all`, loadNotification)
}
</script>

<template>
  <div class="main-content" v-loading="loading" element-loading-text="正在进入，请稍后...">
    <el-container style="height: 100%" v-if="!loading">
      <el-header class="main-content-header">
        <div style="width: 320px; height: 32px">
          <el-image class="logo" src="https://element-plus.org/images/element-plus-logo.svg" style="margin-right: auto"/>
        </div>

        <div style="flex: 1;padding: 0 20px;text-align: center ">
          <el-input style="width: 100%;max-width: 450px" placeholder="搜索论坛相关内容...">
            <template #prefix>
              <el-icon>
                <Search/>
              </el-icon>
            </template>
            <template #append>
              <el-select style="width: 120px" v-model="searchInput.type">
                <el-option value="1" label="帖子广场"/>
                <el-option value="2" label="校园活动"/>
                <el-option value="3" label="表白墙"/>
                <el-option value="4" label="教务通知"/>
              </el-select>
            </template>
          </el-input>
        </div>

        <user-info>
          <el-popover placement="bottom" :width="350" trigger="click">
            <template #reference>
              <el-badge is-dot :hidden="!notification.length">
                <div class="notification">
                  <el-icon><Bell/></el-icon>
                  <div style="font-size: 10px">消息</div>
                </div>
              </el-badge>
            </template>

            <!--如果没有消息 -->
            <el-empty :image-size="80" description="暂时没有未读消息哦~" v-if="!notification.length"/>
            <!--有消息的话 -->
            <el-scrollbar :max-height="500" v-else>
              <light-card v-for="item in notification" class="notification-item"
                          @click="confirmNotification(item.id, item.url)">
                <div>
                  <el-tag size="small" :type="item.type">消息</el-tag>&nbsp;
                  <span style="font-weight: bold">{{item.title}}</span>
                </div>
                <el-divider style="margin: 7px 0 3px 0"/>
                <div style="font-size: 13px;color: grey">
                  {{item.content}}
                </div>
              </light-card>
            </el-scrollbar>
            <!--清除未读消息 -->
            <div style="margin-top: 10px">
              <el-button size="small" type="info" :icon="Check" @click="deleteAllNotification"
                         style="width: 100%" plain>清除全部未读消息</el-button>
            </div>
          </el-popover>
        </user-info>

      </el-header>
      <el-container>
        <el-aside width="230px">
          <!--设置侧边栏滚动条-->
          <el-scrollbar style="min-height: calc(100vh - 55px)">
            <!-- min-height和height的区别看不出来啊 QUESTION-->
            <!-- 100vh-55px，55px为header高度。导航菜单高度拉满，占据整个左边-->
            <!-- 默认展示子菜单中的1-1，也就是下面设置的index为1-1的校园论坛-->
            <!-- 动态地使用当前路由作为path -->
            <!--设置初始全部展开（否则界面略显单调） -->
            <el-menu
                router
                :default-active="$route.path"
                :default-openeds="['1','2','3']"
                style="height: calc(100vh - 55px)">
              <el-sub-menu :index="(index+1).toString()"
                           v-for="(menu, index) in userMenu">
                <template #title>
                  <el-icon>
                    <component :is="menu.icon"/>
                  </el-icon>
                  <span><b>{{ menu.title }}</b></span>
                </template>
                <el-menu-item :index="subMenu.index" v-for="subMenu in menu.sub">
                  <template #title>
                    <el-icon>
                      <component :is="subMenu.icon"/>
                    </el-icon>
                    {{subMenu.title}}
                  </template>
                </el-menu-item>
              </el-sub-menu>
            </el-menu>
          </el-scrollbar>
        </el-aside>

        <el-main class="main-content-page" style="padding: 0">
            <el-scrollbar style="height: calc(100vh - 55px)">
<!--             <router-view v-slot="{ Component }">-->
<!--               <transition name="el-fade-in-linear" mode="out-in">-->
<!--                 <component :is="Component" :key="Component.key" style="height: 100%"/>-->
<!--               </transition>-->
<!--             </router-view>-->
              <router-view>
              </router-view>
            </el-scrollbar>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>


<style scoped lang="less">

.notification-item {
  transition: .3s;
  &:hover {
    cursor: pointer;
    opacity: 0.7;
  }
}


.notification {
  font-size: 22px;
  line-height: 14px;
  text-align: center;

  &:hover {
    color: gray;
    cursor: pointer;
  }

}

.main-content-page {
  padding: 0;
  background-color: #f7f8fa;
}

.dark .main-content-page {
  background-color: #212225;
}

.main-content {
  height: 100vh;
  width: 100vw;
}

.main-content-header {
  border-bottom: solid 1px var(--el-border-color);
  height: 55px;
  display: flex;
  align-items: center;
  box-sizing: border-box;

  .logo {
    height: 32px;
  }

  .user-info {
    display: flex;
    justify-content: flex-end;
    align-items: center;

    .el-avatar:hover {
      cursor: pointer;
    }
  }

  .profile {
    text-align: right;
    margin-right: 20px;

    :first-child {
      font-size: 18px;
      font-weight: bold;
      line-height: 20px;
    }

    :last-child {
      font-size: 10px;
      color: gray;
    }

  }
}
</style>
