<template>
    <div style="width: 100vw;height: 100vh;overflow: hidden;display: flex">
        <div style="flex: 1">
            <el-image style="width: 100%;height: 100%" fit="cover"
                      src="https://img.pconline.com.cn/images/upload/upc/tx/photoblog/1911/26/c4/181365848_1574744813062.jpg"/>
        </div>
        <div class="welcome-title">
            <div style="font-size: 30px;font-weight: bold">欢迎来到集美大学校园论坛(五社区分坛)</div>
            <div style="margin-top: 10px">在这里你可以畅所欲言（合法的）</div>
            <div style="margin-top: 5px">在这里你可以交友、碰撞思维的火花</div>
        </div>
        <div class="right-card">
<!--          淡入淡出-->
            <router-view v-slot="{ Component }">
                <transition name="el-fade-in-linear" mode="out-in">
                    <component :is="Component" style="height: 100%"/>
                </transition>
            </router-view>
        </div>
    </div>
</template>

<script setup>

</script>

<style scoped lang="less">
.right-card {
  width: 400px;
  z-index: 1;
  background-color: var(--el-bg-color);
}

.welcome-title {
    position: absolute;
    bottom: 30px;
    left: 30px;
    color: white;
    text-shadow: 0 0 10px black;
}
</style>
