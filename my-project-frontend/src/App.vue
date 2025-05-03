<script setup>
import { useDark, useToggle } from '@vueuse/core'
import {onMounted, provide, ref} from "vue";
import {isUnauthorized} from "@/net";
import {apiUserInfo} from "@/net/api/user";
import zhCn from "element-plus/es/locale/lang/zh-cn";


useDark({
  selector: 'html',
  attribute: 'class',
  valueDark: 'dark',
  valueLight: 'light'
})

useDark({
  onChanged(dark) { useToggle(dark) }
})

const loading = ref(false)
//父传子,甚至继续往下存
provide('userLoading', loading)

onMounted(() => {
  if(!isUnauthorized()) {
    apiUserInfo(loading)
  }
})
</script>

<template>
    <!--设置语言 el -->
    <el-config-provider :locale="zhCn">
        <div class="wrapper">
            <router-view/>
        </div>
    </el-config-provider>
</template>

<style scoped>
header {
  line-height: 1.5;
}
</style>
