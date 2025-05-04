<script setup>
defineProps({
  color: String,
  size: {
    type: [String, Number],
    default: 10
  }
})

</script>

<template>
  <div class="dot" :style="{
    background: color,
    width: `${size}px`,
    height: `${size}px`,
    borderRadius: `${size / 2}px`
  }">

  </div>

</template>

<style scoped>
.dot{
  display: inline-block;
}

</style>