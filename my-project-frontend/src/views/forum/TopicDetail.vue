<script setup>
import {useRoute} from "vue-router";
import {get} from "@/net";

const route = useRoute()

const tid = route.params.tid

get(`api/forum/topic?tid=${tid}`,data=>{
  console.log(data)
})
</script>

<template>

</template>

<style scoped>

</style>