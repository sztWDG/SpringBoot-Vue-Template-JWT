<script setup>

import LightCard from "@/components/LightCard.vue";
import {Calendar, CollectionTag, EditPen, Link} from "@element-plus/icons-vue";
import Weather from "@/components/Weather.vue";
import {computed, reactive, ref} from "vue";
import {get} from "@/net";
import {ElMessage} from "element-plus";
import TopicEditor from "@/components/TopicEditor.vue";

const weather = reactive({
  location: {},
  now: {},
  hourly: [],
  success: false
})

//默认不呼出弹窗
const editor = ref(false)

// 计算日期
const today = computed(() => {
  const date = new Date();
  return `${date.getFullYear()} 年 ${date.getMonth() + 1} 月 ${date.getDate()} 日`
})

//前端请求获取位置信息
navigator.geolocation.getCurrentPosition(position => {
  const longitude = position.coords.longitude;
  const latitude = position.coords.latitude;
  get(`api/forum/weather?longitude=${longitude}&latitude=${latitude}`, data => {
    Object.assign(weather, data)
    weather.success = true;
  })
}, error => {
  console.info(error);
  ElMessage.warning('位置信息获取超时，请检测网络设置');
  get(`api/forum/weather?longitude=116.40529&latitude=39.90499}`,data => {
    Object.assign(weather, data)
    weather.success = true;
  })
}, {
  //3秒钟还未获取到，则错误了
  timeout: 3000,
  enableHighAccuracy: true
})
</script>

<template>
  <div style="display: flex; margin: 20px auto; gap:20px; max-width: 900px">
    <!--左侧 -->
    <div style="flex: 1;">
      <light-card>
        <!--这边点击发表主题，单向绑定弹出编辑框 -->
        <div class="create-topic" @click="editor = true">
          <el-icon><EditPen/></el-icon>点击发表主题...
        </div>
      </light-card>

      <!--置顶帖子 -->
      <light-card style="margin-top: 10px;height: 30px">

      </light-card>

      <div style="margin-top: 10px;display: flex;flex-direction: column;gap: 10px">
        <light-card style="height: 150px" v-for="item in 10">


        </light-card>
      </div>
    </div>
    <!--右侧 -->
    <div style="width: 280px">
      <div style="position: sticky;top: 20px">
        <light-card>
          <div style="font-weight: bold">
            <el-icon>
              <CollectionTag/>
            </el-icon>
            论坛公告
          </div>
          <el-divider style="margin: 10px 0;"/>
          <div style="font-size: 14px;margin: 10px; color: gray">
            天地三清，道法无常
          </div>
        </light-card>
        <!--插入天气模块-->
        <light-card style="margin-top: 10px">
          <div>
            <el-icon>
              <Calendar/>
            </el-icon>
            天气信息
          </div>
          <el-divider style="margin: 10px 0;"/>
          <weather :data="weather"/>
        </light-card>
        <light-card style="margin-top: 10px">
          <div class="info-text">
            <div>当前日期</div>
            <div>{{ today }}</div>
          </div>
          <div class="info-text">
            <div>当前IP地址</div>
            <div>127.0.0.1</div>
          </div>
        </light-card>

        <div style="font-size: 14px;margin-top: 10px; color: gray">
          <el-icon>
            <Link/>
          </el-icon>
          友情链接
          <el-divider style="margin: 10px 0"/>
        </div>
        <!--招商链接 -->
        <div style="display: grid;grid-template-columns: repeat(2,1fr);grid-gap: 10px;margin-top: 10px">
          <div class="friend-link">
            <el-image style="height: 100%" src="https://element-plus.org/images/element-plus-logo.svg"/>
          </div>
          <div class="friend-link">
            <el-image style="height: 100%" src="https://element-plus.org/images/element-plus-logo.svg"/>
          </div>
          <div class="friend-link">
            <el-image style="height: 100%" src="https://element-plus.org/images/element-plus-logo.svg"/>
          </div>
        </div>

      </div>
    </div>

    <!--话题编辑 -->
    <topic-editor :show="editor" @close="editor = false"/>
  </div>

</template>

<style  lang="less" scoped>
.info-text {
  display: flex;
  justify-content: space-between;
  color: gray;
  font-size: 14px;
}

.friend-link {
  border-radius: 5px;
  overflow: hidden;
}

.create-topic {
  background-color: #efefef;
  border-radius: 5px;
  height: 40px;
  font-size: 14px;
  line-height: 40px;
  padding: 0 10px;

  &:hover {
    cursor: pointer;
  }
}
</style>