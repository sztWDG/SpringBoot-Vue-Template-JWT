<script setup>

import LightCard from "@/components/LightCard.vue";
import {
  ArrowRightBold,
  Calendar, CircleCheck,
  Clock,
  CollectionTag,
  Compass,
  Document,
  Edit,
  EditPen, FolderOpened,
  Link,
  Microphone, Picture, Star
} from "@element-plus/icons-vue";
import Weather from "@/components/Weather.vue";
import {computed, onMounted, reactive, ref, watch} from "vue";
import {ElMessage} from "element-plus";
import TopicEditor from "@/components/TopicEditor.vue";
import {useStore} from "@/store";
import ColorDot from "@/components/ColorDot.vue";
import router from "@/router";
import TopicTag from "@/components/TopicTag.vue";
import TopicCollectList from "@/components/TopicCollectList.vue";
import {apiForumTopicList, apiForumTopTopics, apiForumWeather} from "@/net/api/forum";

const store = useStore()

const weather = reactive({
  location: {},
  now: {},
  hourly: [],
  success: false
})

//默认不呼出弹窗
const editor = ref(false)
const topics = reactive({
  list: [],
  type: 0,
  page: 0,
  end: false,
  searchText: '',
  totalCount: 0,  // Total count of topics for pagination
  checkingNextPage: false
})

// Track the current page for pagination
const currentPage = ref(1)

const collects = ref(false);

// const list = ref(null)
// const type = ref(0)

//控制点击type之后，进行分类，开始时也是
watch(() => topics.type, () => {
  topics.searchText = '';
  resetList();
}, {immediate: true})

// 计算日期
const today = computed(() => {
  const date = new Date();
  return `${date.getFullYear()} 年 ${date.getMonth() + 1} 月 ${date.getDate()} 日`
})

 //获取types新址，放在Forum，最外层
// get('/api/forum/types', data => {
//   const array = []
//   //彩色渐变效果，添加一个全部
//   array.push({name: '全部', id: 0, color: 'linear-gradient(45deg, white, red,orange,gold,green,blue)'})
//   data.forEach(d => array.push(d))
//   store.forum.types = array
// })

//请求帖子列表封成一个函数
function updateList() {
  //如果已经到最后，则返回
  if (topics.end && currentPage.value > 1) return;
  
  //记得写请求参数 - page从0开始，但后端需要从1开始，所以要+1
  apiForumTopicList(topics.page, topics.type, data => {
    //如果data有内容
    if (data) {
      // Filter topics based on search text if needed
      let filteredData = data;
      if (topics.searchText) {
        const searchLower = topics.searchText.toLowerCase();
        filteredData = data.filter(topic => 
          topic.title.toLowerCase().includes(searchLower) || 
          topic.text.toLowerCase().includes(searchLower)
        );
      }
      topics.list = filteredData;
      
      // 根据数据判断总页数
      if (topics.page === 0) {
        // 如果是第一页且数据不足10条，说明只有一页
        if (data.length < 10) {
          topics.totalCount = data.length;
          topics.end = true;
        } else {
          // 如果第一页数据是10条，请求第二页确认总数
          if (!topics.checkingNextPage) {
            topics.checkingNextPage = true;
            apiForumTopicList(1, topics.type, nextPageData => {
              topics.checkingNextPage = false;
              if (nextPageData && nextPageData.length > 0) {
                // 至少有两页
                if (nextPageData.length < 10) {
                  // 如果第二页不足10条，总数为第一页10条加第二页实际条数
                  topics.totalCount = 10 + nextPageData.length;
                } else {
                  // 如果第二页也是10条，保守估计30条（3页）
                  topics.totalCount = 30;
                }
              } else {
                // 只有一页10条数据
                topics.totalCount = 10;
                topics.end = true;
              }
            });
          } else {
            // 保守估计20条数据作为默认值
            topics.totalCount = 20;
          }
        }
      } else if (data.length < 10) {
        // 如果不是第一页且数据不足10条，说明到达最后一页
        topics.end = true;
        topics.totalCount = topics.page * 10 + data.length;
      }
    } else {
      topics.end = true;
      topics.list = [];
      if (topics.page === 0) {
        topics.totalCount = 0;
      }
    }
  })
}

//在一开始的时候调用一次，发帖成功后也调用一次
// updateList()

function onTopicCreate() {
  editor.value = false;
  resetList();
}

//重置List请求
function resetList(page = 1) {
  currentPage.value = page;
  topics.page = page - 1; // Backend expects page numbers starting from 1, but we store 0-based index
  topics.end = false;
  topics.list = [];
  updateList();
}

// Handle page change
function handlePageChange(page) {
  resetList(page);
}

//前端请求获取位置信息
navigator.geolocation.getCurrentPosition(position => {
  const longitude = position.coords.longitude;
  const latitude = position.coords.latitude;

  apiForumWeather(longitude, latitude, data => {
    Object.assign(weather, data)
    weather.success = true;
  })
}, error => {

  console.info(error);
  ElMessage.warning('位置信息获取超时，请检测网络设置');

  apiForumWeather(116.40529, 39.90499, data => {
    Object.assign(weather, data)
    weather.success = true;
  })
}, {
  //3秒钟还未获取到，则错误了
  timeout: 3000,
  enableHighAccuracy: true
})

// Listen for search events from IndexView
onMounted(() => {
  // Listen for search events
  window.addEventListener('forum-search', (event) => {
    topics.searchText = event.detail.text;
    resetList();
  });
  
  //获取置顶帖子信息
  apiForumTopTopics(data => topics.top = data)
})
</script>

<template>
  <div style="display: flex; margin: 20px auto; gap:20px; max-width: 900px">
    <!--左侧 -->
    <div style="flex: 1;">
      <light-card>
        <!--这边点击发表主题，单向绑定弹出编辑框 -->
        <div class="create-topic" @click="editor = true">
          <el-icon>
            <EditPen/>
          </el-icon>
          点击发表主题...
        </div>
        <div style="margin-top: 10px;display: flex;gap: 13px;font-size: 18px;color: grey">
          <el-icon><Edit/></el-icon>
          <el-icon><Document/></el-icon>
          <el-icon><Compass/></el-icon>
          <el-icon><Picture/></el-icon>
          <el-icon><Microphone/></el-icon>
        </div>
      </light-card>

      <light-card style="margin-top: 10px;display: flex;flex-direction: column;gap: 10px">
        <div v-for="item in topics.top" class="top-topic" @click="router.push(`/index/topic-detail/${item.id}`)">
          <el-tag type="info" size="small">置顶</el-tag>
          <div>{{ item.title }}</div>
          <div>{{ new Date(item.time).toLocaleDateString() }}</div>
        </div>

      </light-card>

      <!--置顶帖子 -->
      <light-card style="margin-top: 10px;display: flex;gap: 7px">
        <!--贴子分类 -->
        <div :class="`type-select-card ${topics.type === item.id ? 'active' : ''}`"
             v-for="item in store.forum.types"
             @click="topics.type = item.id">
          <color-dot :color="item.color"/>
          <span style="margin-left: 5px">{{ item.name }}</span>
        </div>
      </light-card>

      <!--套一层动画 -->
      <transition name="el-fade-in" mode="out-in">
        <div v-if="topics.list.length">
          <!--展示话题内容 -->
          <div style="margin-top: 10px;display: flex;flex-direction: column;gap: 10px">
            <light-card v-for="item in topics.list" class="topic-card"
                        @click="router.push('/index/topic-detail/'+item.id)">
              <!--头像 -->
              <div style="display: flex">
                <div>
                  <!--获取！怎么做到的？  修改BUG，解决新人没有头像的问题-->
            <!--  <el-avatar :size="30" :src="`${axios.defaults.baseURL}/images${item.avatar}`"/>-->
                  <el-avatar :size="30" :src="store.avatarUserUrl(item.avatar)"/>
                </div>
                <!--transform:translatY(-2px)上移 -->
                <div style="margin-left: 7px;transform: translateY(-2px) ">
                  <div style="font-size: 13px;font-weight: bold">{{ item.username }}</div>
                  <div style="font-size: 12px;color: gray">
                    <el-icon>
                      <Clock/>
                    </el-icon>
                    <div style="margin-left: 2px;display: inline-block;transform: translateY(-2px)">
                      {{ new Date(item.time).toLocaleString() }}
                    </div>
                  </div>
                </div>
              </div>

              <div style="margin-top: 5px">
                <!--EE（阿尔法值）让字体有点透明, 有可能类型是异步加载，帖子列表加载好了，数据没到。要加？-->
                <topic-tag :type="item.type"/>
                <span style="font-weight: bold">{{ item.title }}</span>
              </div>
              <div class="topic-content">{{ item.text }}</div>
              <!--展示图片,采用grid格子布局，并且最多展示3张-->
              <div style="display: grid;grid-template-columns: repeat(3,1fr);grid-gap: 10px">
                <el-image class="topic-image" v-for="img in item.images" :src="img"></el-image>
              </div>

              <!--展示点赞收藏数据-->
              <div style="display: flex;gap: 20px;font-size: 13px;margin-top: 10px;opacity: 0.8">
                <div>
                  <el-icon style="vertical-align: middle"><CircleCheck/></el-icon> {{item.like}}点赞
                </div>
                <div>
                  <el-icon style="vertical-align: middle"><Star/></el-icon> {{item.collect}}收藏
                </div>
              </div>

            </light-card>
          </div>
        </div>
      </transition>

      <!-- Add pagination at the bottom of the topic list -->
      <div style="width: fit-content;margin: 20px auto" v-if="topics.list.length">
        <el-pagination background layout="prev, pager, next"
                       :current-page="currentPage" @current-change="handlePageChange"
                       :total="topics.totalCount" :page-size="10"
                       hide-on-single-page/>
      </div>

    </div>
    <!--右侧 -->
    <div style="width: 280px">
      <div style="position: sticky;top: 20px">
        <!--收藏查看 -->
        <light-card>
          <div class="collect-list-button" @click="collects=true">
            <span><el-icon><FolderOpened/></el-icon>查看我的收藏</span>
            <el-icon style="transform: translateY(3px)"><ArrowRightBold/></el-icon>
          </div>
        </light-card>

        <light-card style="margin-top: 10px">
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

    <!--话题编辑 发文成功，要关闭编辑框。 关闭编辑框，要关闭编辑框-->
    <!--updateList()获取帖子列表，在发帖成功后也调用一次,以便刷新展示-->
    <topic-editor :show="editor" @success=onTopicCreate @close="editor = false"/>
    <!--收藏-->
    <topic-collect-list :show="collects" @close="collects=false"/>
  </div>

</template>

<style lang="less" scoped>

.collect-list-button {
  font-size: 14px;
  display: flex;
  justify-content: space-between;
  transition: .3s;

  &:hover {
    cursor: pointer;
    opacity: 0.6;
  }
}

//置顶样式
.top-topic {
  display: flex;

  div:first-of-type {
    font-size: 14px;
    margin-left: 10px;
    font-weight: bold;
    opacity: 0.8;
    transition: color .3s;

    &:hover {
      color: gray;
    }
  }

  div:nth-of-type(2) {
    flex: 1;
    color: gray;
    font-size: 13px;
    text-align: right;

  }

  &:hover {
    cursor: pointer;
  }
}




//展示帖子类型选择
.type-select-card {
  background-color: #f5f5f5;
  padding: 2px 7px;
  font-size: 14px;
  border-radius: 3px;
  box-sizing: border-box;
  transition: background-color .3s;

  &.active {
    border: solid 1px #ead4c4;
  }

  &:hover {
    cursor: pointer;
    background-color: #dadada;
  }

}

//处理话题展示样式
.topic-card {
  padding: 15px;
  /*当鼠标悬停在元素上时，元素会在 0.3 秒内逐渐放大到 1.1 倍。
  鼠标移开时，元素会在 0.3 秒内逐渐恢复到原来的大小。*/
  transition: scale .3s;

  //让鼠标悬浮有感觉
  &:hover {
    scale: 1.015; //放大1.01倍
    cursor: pointer; //鼠标指针悬停
  }


  .topic-content {
    font-size: 13px;
    color: gray;
    margin: 5px 0;
    //设置话题内容只展示部分（3行）
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 3;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  //.topic-type {
  //  display: inline-block;
  //  border: solid 0.5px gray;
  //  border-radius: 3px;
  //  font-size: 12px;
  //  padding: 0 5px;
  //  height: 18px;
  //  margin-right: 7px;
  //}

  .topic-image {
    width: 100%;
    height: 100%;
    max-height: 110px;
    border-radius: 5px;
  }

}

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

.dark {
  .create-topic {
    background-color: #232323;
  }

  .type-select-card {
    background-color: #282828;

    &.active {
      border: solid 1px #64594b;
    }

    &:hover {
      background-color: #5e5e5e;
    }
  }
}

</style>