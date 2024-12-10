<script setup>
import {Check, Document} from "@element-plus/icons-vue";
import {reactive} from "vue";
import {Quill, QuillEditor} from "@vueup/vue-quill";
import ImageResize from "quill-image-resize-vue";
import {ImageExtend, QuillWatch} from "quill-image-super-solution-module";
import '@vueup/vue-quill/dist/vue-quill.snow.css'
import axios from "axios";
import {ElMessage} from "element-plus";
import {accessHeader, get} from "@/net";

// import {Check, Document} from "@element-plus/icons-vue";
// import {computed, reactive, ref} from "vue";
// import {Delta, Quill, QuillEditor} from "@vueup/vue-quill";
// import ImageResize from "quill-image-resize-vue";
// import { ImageExtend, QuillWatch } from "quill-image-super-solution-module";
// import '@vueup/vue-quill/dist/vue-quill.snow.css';
// import axios from "axios";
// import {accessHeader, get, post} from "@/net";
// import {ElMessage} from "element-plus";
// import ColorDot from "@/components/ColorDot.vue";
// import {useStore} from "@/store";

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
  loading: false,
  types: []
})

//迁移到数据库
// const types = [
//   {id:1, name:'日常闲聊',desc:'在这里分享你的各种日常'},
//   {id:2, name:'真诚交友',desc:'在校园里寻找与自己志同道合的朋友'},
//   {id:3, name:'问题反馈',desc:'反馈你在校园里遇到的问题'},
//   {id:4, name:'恋爱官宣',desc:'向大家展示你的恋爱成果'},
//   {id:5, name:'踩坑记录',desc:'把你遇到的坑分享给大家，防止其他人再次入坑'},
// ]

Quill.register('modules/imageResize', ImageResize)
Quill.register('modules/ImageExtend', ImageExtend)

function submitTopic() {
  console.info(editor.text);
}

//返回后端获取到的数据
get('/api/forum/types', data => editor.types = data)


const editorOption = {
  modules: {
    toolbar: {
      container: [
        "bold", "italic", "underline", "strike", "clean",
        {color: []}, {'background': []},
        {size: ["small", false, "large", "huge"]},
        {header: [1, 2, 3, 4, 5, 6, false]},
        {list: "ordered"}, {list: "bullet"}, {align: []},
        "blockquote", "code-block", "link", "image",
        {indent: '-1'}, {indent: '+1'}
      ],
      //调用后端接口，去上传图片
      handlers: {
        'image': function () {
          QuillWatch.emit(this.quill.id)
        }
      }
    },
    imageResize: {
      modules: ['Resize', 'DisplaySize']
    },
    ImageExtend: {
      //上传的图片地址,文件参数,上传状态为loading，避免一直上传
      action: axios.defaults.baseURL + '/api/image/cache',
      name: 'file',
      size: 5,
      loading: true,
      accept: 'image/png, image/jpeg',
      response: (resp) => {
        if (resp.data) {
          return axios.defaults.baseURL + '/images' + resp.data
        } else {
          return null
        }
      },
      methods: 'POST',
      headers: xhr => {
        xhr.setRequestHeader('Authorization', accessHeader().Authorization);
      },
      start: () => editor.loading = true,
      success: () => {
        ElMessage.success('图片上传成功!')
        editor.loading = false
      },
      error: () => {
        ElMessage.warning('图片上传失败，请联系管理员!')
        editor.loading = false
      }
    }
  }
}
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
          <!--先判断有没有获取到数据库里面的types，没有的话就不能使用 -->
          <el-select placeholder="选择主题类型..." v-model="editor.type"
                     :disabled="!editor.types.length">
            <el-option v-for="item in editor.types" :value="item.id" :label="item.name"></el-option>
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
      <div style="margin-top: 15px;height: 460px;overflow: hidden;border-radius: 5px"
           v-loading="editor.loading"
           element-loading-text="正在上传图片，请稍后...">
        <!-- 记得需要去导入css,在node-modules里面找css样式，减去toolbar的高度-->
        <!--content=delta让前端数据更加安全 -->
        <quill-editor v-model:content="editor.text" style="height: calc(100% - 45px)"
                      content-type="delta"
                      placeholder="今天想分享一些什么呢？" :options="editorOption"/>
      </div>

      <div style="display: flex;justify-content: space-between;margin-top: 5px">
        <div style="color: gray;font-size: 13px">
          当前字数666（最大支持2000字）
        </div>
        <div>
          <el-button type="success" @click="submitTopic" :icon="Check" plain>立即发表主题</el-button>
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

:deep(.el-drawer__header) {
  margin: 0;
}

:deep(.ql-toolbar) {
  border-radius: 5px 5px 0 0;
  border-color: var(--el-border-color);
}

:deep(.ql-container) {
  border-radius: 0 0 5px 5px;
  border-color: var(--el-border-color);
}

:deep(.ql-editor) {
  font-size: 16px;
}

:deep(.ql-editor.ql-blank::before) {
  /*！！！*/
  color: var(--el-text-color-placeholder);
  font-style: normal;
}
</style>