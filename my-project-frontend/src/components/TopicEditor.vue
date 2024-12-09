<script setup>
import {Check, Document} from "@element-plus/icons-vue";
import {reactive} from "vue";
import {QuillEditor} from "@vueup/vue-quill";
import '@vueup/vue-quill/dist/vue-quill.snow.css'

defineProps({
  show: Boolean
})

//关闭弹窗时，标记为close，通知外部，得到结果
const emit = defineEmits(['close'])

//存储数据
const editor = reactive({
  type: null,
  title: '',
  text: '',
  loading: false
})

const types = [
  {id:1, name:'日常闲聊',desc:'在这里分享你的各种日常'},
  {id:2, name:'真诚交友',desc:'在校园里寻找与自己志同道合的朋友'},
  {id:3, name:'问题反馈',desc:'反馈你在校园里遇到的问题'},
  {id:4, name:'恋爱官宣',desc:'向大家展示你的恋爱成果'},
  {id:5, name:'踩坑记录',desc:'把你遇到的坑分享给大家，防止其他人再次入坑'},
]


</script>

<template>
  <div>
    <!--弹出框，设置从btt也就是从底部弹出，并限制只有点击关闭可以退出，不然随便点击框外退出很烦人-->
    <el-drawer @close="emit('close')" :model-value="show" direction="btt"
               :close-on-click-modal="false" :size="650">
      <template #header>
        <div>
          <div style="font-weight: bold">发表新的帖子</div>
          <div style="font-size: 13px">发表内容之前，请遵守相关法律法规，不得出现骂人等不良行为</div>
        </div>
      </template>

      <div style="display: flex; gap: 10px">
        <div style="width: 120px">
          <el-select placeholder="选择主题类型..." v-model="editor.type">
            <el-option v-for="item in types" :value="item.id" :label="item.name"></el-option>
          </el-select>
        </div>
        <div style="flex: 1;">
          <el-input v-model="editor.title" placeholder="请输入帖子标题..." :prefix-icon="Document"
                    style="height: 100%"/>
        </div>
      </div>

      <!--使用富文本编辑器quill
      QUETION:这里加了overflow:hidden之后就是显示不出来下边框
      SOLVED: 原来calc计算函数里面要严格使用空格-->
      <div style="margin-top: 15px;height: 460px;overflow: hidden">
        <!-- 记得需要去导入css,在node-modules里面找css样式，减去toolbar的高度-->
        <quill-editor v-model:content="editor.text"  style="height: calc(100% - 45px)"
                      placeholder="今天想分享一些什么呢？"/>
      </div>

      <div style="display: flex;justify-content: space-between;margin-top: 5px">
        <div style="color: gray;font-size: 13px">
          当前字数666（最大支持2000字）
        </div>
        <div>
          <el-button type="success" :icon="Check" plain>立即发表主题</el-button>
        </div>
      </div>

    </el-drawer>
  </div>

</template>

<style scoped>
/*样式穿刺，设置drawer弹出框样式，让其大、形状合适*/
:deep(.el-drawer) {
  width: 800px;
  margin: auto;
  border-radius: 10px 10px 0 0;
}

:deep(.el-drawer__header){
  margin: 0;
}

:deep(.ql-toolbar){
  border-radius: 5px 5px 0 0;
  border-color: var(--el-border-color);
}
:deep(.ql-container){
  border-radius: 0 0 5px 5px;
  border-color: var(--el-border-color);
}
:deep(.ql-editor){
  font-size: 16px;
}
:deep(.ql-editor.ql-blank::before){
  /*！！！*/
  color: var(--el-text-color-placeholder);
  font-style: normal;
}
</style>