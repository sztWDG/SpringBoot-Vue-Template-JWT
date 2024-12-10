<script setup>
defineProps({
  data: Object,
})
</script>

<template>

  <div style="height: 160px" v-loading="!data.success" element-loading-text="正在加载天气信息...">
    <div style="display: flex;justify-content: space-between;margin: 10px 20px"
         v-if="data.success">
      <div style="font-size: 45px;color: gray">
        <i :class="`qi-${data.now.icon}-fill`"></i>
      </div>
      <div style="font-weight: bold;text-align: center">
        <div style="font-size: 25px">{{ data.now.temp }}℃</div>
        <div style="font-size: 15px">{{ data.now.text }}</div>
      </div>
      <div style="margin-top: 13px">
        <div style="font-size: 15px">{{`${data.location.country} ${data.location.adm1}`}}</div>
        <div style="font-size: 15px;color: gray">{{`${data.location.adm2} ${data.location.name}区`}}</div>
      </div>
    </div>
    <el-divider style="margin: 10px 0;"/>
    <!--天气显示骚操作get-->
    <div style="display: grid;grid-template-columns: repeat(5, 1fr); text-align: center">
      <div v-for="item in data.hourly">
        <!--获取hourly里面的时间 -->
        <div style="font-size: 13px">{{new Date(item.fxTime).getHours()}}时</div>
        <div style="font-size: 23px">
          <i :class="`qi-${item.icon}-fill`"></i>
        </div>
        <div style="font-size: 12px">{{item.temp}}℃</div>

      </div>
    </div>

  </div>
</template>

<style scoped>

</style>