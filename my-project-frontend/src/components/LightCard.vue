<script setup>

</script>

<template>
  <div class="light-card">

    <slot/>
  </div>

</template>

<style scoped>
.light-card{
  /*这是？*/
  background-color: var(--el-bg-color);
  border-radius: 5px;
  padding: 10px;
}

</style>