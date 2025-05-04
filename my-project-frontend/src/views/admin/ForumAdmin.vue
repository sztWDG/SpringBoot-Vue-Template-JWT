<script setup>
import {
  apiAdminForumTypes,
  apiAdminAddForumType,
  apiAdminUpdateForumType,
  apiAdminDeleteForumType
} from "@/net/api/admin";
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Plus, Edit, Delete } from '@element-plus/icons-vue';
import ColorDot from '@/components/ColorDot.vue';

// 表单引用
const formRef = ref(null);

// 帖子类型数据
const types = ref([]);
const loading = ref(false);

// 弹窗相关
const typeDialog = ref(false);
const dialogTitle = ref('添加帖子类型');
const isEdit = ref(false);

// 表单数据
const formData = ref({
  id: null,
  name: '',
  description: '',
  color: 'linear-gradient(45deg, #ff9a9e 0%, #fad0c4 99%, #fad0c4 100%)'
});

// 表单规则
const rules = {
  name: [
    { required: true, message: '请输入类型名称', trigger: 'blur' },
    { min: 1, max: 20, message: '长度在 1 到 20 个字符', trigger: 'blur' }
  ],
  color: [
    { required: true, message: '请选择颜色', trigger: 'change' }
  ]
};

// 颜色选项
const colorOptions = [
  { name: '粉红渐变', value: 'linear-gradient(45deg, #ff9a9e 0%, #fad0c4 99%, #fad0c4 100%)' },
  { name: '蓝色渐变', value: 'linear-gradient(45deg, #2196F3 0%, #4FC3F7 99%, #4FC3F7 100%)' },
  { name: '绿色渐变', value: 'linear-gradient(45deg, #4CAF50 0%, #8BC34A 99%, #8BC34A 100%)' },
  { name: '橙色渐变', value: 'linear-gradient(45deg, #FF9800 0%, #FFC107 99%, #FFC107 100%)' },
  { name: '紫色渐变', value: 'linear-gradient(45deg, #9C27B0 0%, #E040FB 99%, #E040FB 100%)' },
  { name: '红色渐变', value: 'linear-gradient(45deg, #F44336 0%, #FF5722 99%, #FF5722 100%)' },
  { name: '青色渐变', value: 'linear-gradient(45deg, #00BCD4 0%, #4DD0E1 99%, #4DD0E1 100%)' },
  { name: '灰色渐变', value: 'linear-gradient(45deg, #9E9E9E 0%, #BDBDBD 99%, #BDBDBD 100%)' }
];

// 获取帖子类型列表
const loadTypes = () => {
  loading.value = true;
  apiAdminForumTypes(data => {
    types.value = data;
    loading.value = false;
  });
};

// 打开添加类型弹窗
const openAddDialog = () => {
  isEdit.value = false;
  dialogTitle.value = '添加帖子类型';
  formData.value = {
    id: null,
    name: '',
    description: '',
    color: 'linear-gradient(45deg, #ff9a9e 0%, #fad0c4 99%, #fad0c4 100%)'
  };
  typeDialog.value = true;
};

// 打开编辑类型弹窗
const openEditDialog = (row) => {
  isEdit.value = true;
  dialogTitle.value = '编辑帖子类型';
  formData.value = {
    id: row.id,
    name: row.name,
    description: row.description || '',
    color: row.color
  };
  typeDialog.value = true;
};

// 删除类型
const handleDelete = (row) => {
  ElMessageBox.confirm(
    '确定要删除这个帖子类型吗？如果该类型下存在帖子将无法删除',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(() => {
    apiAdminDeleteForumType(row.id, message => {
      if (!message) {
        ElMessage.success('删除成功');
        loadTypes();
      } else {
        ElMessage.error(message);
      }
    });
  });
};

// 提交表单
const submitForm = (formEl) => {
  if (!formEl) return;
  formEl.validate((valid) => {
    if (valid) {
      if (isEdit.value) {
        // 编辑模式
        apiAdminUpdateForumType(formData.value, message => {
          if (!message) {
            ElMessage.success('更新成功');
            typeDialog.value = false;
            loadTypes();
          } else {
            ElMessage.error(message);
          }
        });
      } else {
        // 添加模式
        apiAdminAddForumType(formData.value, message => {
          if (!message) {
            ElMessage.success('添加成功');
            typeDialog.value = false;
            loadTypes();
          } else {
            ElMessage.error(message);
          }
        });
      }
    }
  });
};

// 重置表单
const resetForm = (formEl) => {
  if (!formEl) return;
  formEl.resetFields();
  typeDialog.value = false;
};

// 组件挂载时加载数据
onMounted(() => {
  loadTypes();
});
</script>

<template>
  <div class="forum-admin-container">
    <el-card class="types-card">
      <template #header>
        <div class="card-header">
          <span>帖子类型管理</span>
          <el-button type="primary" :icon="Plus" @click="openAddDialog">添加类型</el-button>
        </div>
      </template>
      
      <el-table :data="types" style="width: 100%" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="类型名称" width="120" />
        <el-table-column prop="description" label="描述" />
        <el-table-column label="颜色" width="150">
          <template #default="{ row }">
            <div class="color-preview">
              <color-dot :color="row.color" size="20" />
              <span class="color-name">{{ row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center">
          <template #default="{ row }">
            <el-button 
              type="primary" 
              :icon="Edit" 
              circle 
              plain
              @click="openEditDialog(row)" 
            />
            <el-button 
              type="danger" 
              :icon="Delete" 
              circle 
              plain
              @click="handleDelete(row)" 
            />
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <!-- 添加/编辑帖子类型弹窗 -->
    <el-dialog 
      v-model="typeDialog" 
      :title="dialogTitle" 
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form 
        ref="formRef" 
        :model="formData" 
        :rules="rules" 
        label-width="100px"
      >
        <el-form-item label="类型名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入类型名称" />
        </el-form-item>
        
        <el-form-item label="描述" prop="description">
          <el-input 
            v-model="formData.description" 
            type="textarea" 
            placeholder="请输入类型描述"
            :rows="3"
          />
        </el-form-item>
        
        <el-form-item label="颜色" prop="color">
          <el-select v-model="formData.color" placeholder="请选择颜色" style="width: 100%">
            <el-option 
              v-for="(option, index) in colorOptions" 
              :key="index" 
              :label="option.name" 
              :value="option.value"
            >
              <div class="color-option">
                <div class="color-preview-box" :style="{ background: option.value }"></div>
                <span>{{ option.name }}</span>
              </div>
            </el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="颜色预览">
          <div class="color-preview">
            <color-dot :color="formData.color" size="20" />
            <span v-if="formData.name" class="color-name">{{ formData.name }}</span>
            <span v-else class="color-name gray">类型名称预览</span>
          </div>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="resetForm(formRef)">取消</el-button>
          <el-button type="primary" @click="submitForm(formRef)">
            {{ isEdit ? '更新' : '添加' }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.forum-admin-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 16px;
  font-weight: bold;
}

.types-card {
  margin-bottom: 20px;
}

.color-preview {
  display: flex;
  align-items: center;
  gap: 8px;
}

.color-name {
  font-size: 14px;
}

.color-name.gray {
  color: #909399;
}

.color-option {
  display: flex;
  align-items: center;
  gap: 8px;
}

.color-preview-box {
  width: 20px;
  height: 20px;
  border-radius: 50%;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
}
</style>