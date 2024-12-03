<script setup>

defineProps({
  icon: Object,
  title: String,
  desc: String
})

</script>

<template>
  <div class="card">
<!--    有title再加-->
    <div class="card-header" v-if="title">
      <div>
        <!--        translate：移动元素位置-->
        <el-icon style="margin-right: 3px;translate:0 2px">
          <component :is="icon"/>
        </el-icon>
        {{title}}
        <div>{{ desc }}</div>
      </div>
    </div>
    <slot/>
  </div>
</template>

<style scoped>

.card {
  border-radius: 5px;
  border: solid 1px var(--el-border-color);
  background-color: var(--el-bg-color);
  box-sizing: border-box;
  min-height: 20px;
  padding: 10px;
}

/*此处需要加>div, 否则first-child样式无法正常赋到元素*/
.card-header > div  {
  border-bottom: solid 1px var(--el-border-color);
  padding-bottom: 5px;
  margin-bottom: 10px;

  & > :first-child {
    font-size: 18px;
    font-weight: bold;
  }

  & > :last-child {
    font-size: 13px;
    color: gray;
  }
}

</style>