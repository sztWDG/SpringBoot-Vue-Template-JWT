<script setup>

import {
    Bell,
    ChatDotSquare,
    Collection, DataLine,
    Document, Files, Location,
    Lock,
    Monitor, Notification,
    Operation,
    Position,
    School, Umbrella,
    User,
    TrendCharts,
    HomeFilled
} from "@element-plus/icons-vue";
import {inject, onMounted, ref} from "vue";
import {useRoute} from "vue-router";
import router from "@/router";

const adminMenu = [
    {
        title: '校园论坛管理', icon: Location, sub: [
            // {title: '管理概览', icon: TrendCharts, index: '/admin/welcome'},
            {title: '用户管理', icon: User, index: '/admin/user'},
            {title: '帖子广场管理', icon: ChatDotSquare, index: '/admin/forum'},
            // { title: '失物招领管理', icon: Bell },
            // { title: '校园活动管理', icon: Notification },
            // { title: '表白墙管理', icon: Umbrella },
            // { title: '合作机构管理', icon: School }
        ]
    },
    //   {
    //   title: '探索与发现管理', icon: Position, sub: [
    //     { title: '成绩管理', icon: Document },
    //     { title: '课程表管理', icon: Files },
    //     { title: '教务通知管理', icon: Monitor },
    //     { title: '在线图书馆管理', icon: Collection },
    //     { title: '预约教室管理', icon: DataLine }
    //   ]
    // }
]

const route = useRoute()
const loading = inject('userLoading')
const pageTabs = ref([])

//点击顶部tab栏的各个项目实现跳转到相应页面
function handleTabClick({props}) {
    router.push(props.name)
}

//关闭标签之后要执行的逻辑
function handleTabClose(name) {
    const index = pageTabs.value.findIndex(tab => tab.name === name)
    //若name===当前路径
    const isCurrent = name === route.fullPath
    pageTabs.value.splice(index, 1)
    if (pageTabs.value.length > 0) {
        /*QxkQuestion-关闭标签-5.3：
        删除后，标签列表中还有剩余的Tab且关闭的是当前的，则自动进行切换，默认切换到上一个，
        如果没有上一个，则切换到下一个  */
        if (isCurrent) {
            router.push(pageTabs.value[Math.max(0, index - 1)].name)
        }
    } else {
        router.push('/admin')
    }
}

//增加标签逻辑
function addAdminTab(menu) {
    if (!menu.index) return //点击没有index的标签就直接返回即可
    //做一个判断，若点击的页面是新页面则添加至tab栏
    if (pageTabs.value.findIndex(tab => tab.name === menu.index) < 0) {
        pageTabs.value.push({
            title: menu.title,
            name: menu.index
        })
    }
}

//通过路径进入也可以正常加入标签
onMounted(() => {
    const initPage = adminMenu
        .flatMap(menu => menu.sub)
        .find(sub => sub.index === route.fullPath)
    if (initPage) {
        addAdminTab(initPage)
    }
})
</script>

<template>
    <div class="admin-content" v-loading="loading" element-loading-text="正在进入，请稍后...">
        <el-container style="height: 100%">
            <el-aside width="230px" class="admin-content-aside">
                <div class="logo-box">
                    <el-image class="logo" src="https://www.jmu.edu.cn/images/logo.png"/>
                </div>
                <el-scrollbar style="height: calc(100vh - 57px)">
                    <el-menu
                        router
                        :default-active="$route.path"
                        :default-openeds="['1', '2','3']"
                        style="min-height: calc(100vh - 57px);border: none">
                        <el-sub-menu :index="(index + 1).toString()"
                                     v-for="(menu, index) in adminMenu">
                            <template #title>
                                <el-icon>
                                    <component :is="menu.icon"/>
                                </el-icon>
                                <span><b>{{ menu.title }}</b></span>
                            </template>
                            <el-menu-item :index="subMenu.index"
                                          @click="addAdminTab(subMenu)"
                                          v-for="subMenu in menu.sub">
                                <template #title>
                                    <el-icon>
                                        <component :is="subMenu.icon"/>
                                    </el-icon>
                                    {{ subMenu.title }}
                                </template>
                            </el-menu-item>
                        </el-sub-menu>
                    </el-menu>
                </el-scrollbar>
            </el-aside>
            <el-container>
                <el-header class="admin-content-header">
                    <div style="flex: 1">
                        <el-tabs type="card"
                                 :model-value="route.fullPath"
                                 closable
                                 @tab-remove="handleTabClose"
                                 @tab-click="handleTabClick">
                            <el-tab-pane v-for="tab in pageTabs"
                                         :label="tab.title"
                                         :name="tab.name"
                                         :key="tab.name"/>
                        </el-tabs>
                    </div>
                    <user-info/>
                </el-header>
                <el-main>
                    <router-view v-slot="{ Component }">
                        <keep-alive>
                            <component :is="Component"/>
                        </keep-alive>
                    </router-view>
                </el-main>
            </el-container>
        </el-container>
    </div>
</template>

<style scoped>
.admin-content {
    height: 100vh;
    width: 100vw;

    .admin-content-aside {
        border-right: solid 1px var(--el-border-color);

        .logo-box {
            text-align: center;
            padding: 15px 0 10px;
            height: 32px;

            .logo {
                height: 32px;
            }
        }
    }

    .admin-content-header {
        border-bottom: solid 1px var(--el-border-color);
        height: 55px;
        display: flex;
        align-items: center;
        box-sizing: border-box;

        :deep(.el-tabs__header) {
            height: 32px;
            margin-bottom: 0;
            border-bottom: none;
        }

        :deep(.el-tabs__nav) {
            gap: 10px;
            border: none;
        }

        :deep(.el-tabs__item) {
            height: 32px;
            padding: 0 15px;
            border-radius: 6px;
            border: solid 1px var(--el-border-color);
        }
    }
}
</style>