<script setup>
import { ref, onMounted } from 'vue';
import { 
  DataLine, Monitor, Setting, Bell, Trophy,
  TrendCharts, User, ChatDotSquare
} from '@element-plus/icons-vue';
import router from '@/router';
import { apiAdminStats } from '@/net/api/admin';

// 当前日期时间
const currentDate = ref(new Date());
const username = ref('管理员');

// 系统概览数据
const stats = ref([
  { 
    title: '用户总数', 
    value: '0', 
    icon: User, 
    color: '#409EFF'
  },
  { 
    title: '帖子总数', 
    value: '0', 
    icon: ChatDotSquare, 
    color: '#67C23A'
  },
  { 
    title: '今日活跃', 
    value: '0', 
    icon: TrendCharts, 
    color: '#E6A23C'
  }
]);

// 更新时间
const updateTime = () => {
  currentDate.value = new Date();
};

// 加载统计数据
const loadStats = () => {
  apiAdminStats(data => {
    stats.value[0].value = data.userCount.toString();
    stats.value[1].value = data.topicCount.toString();
    stats.value[2].value = data.todayTopicCount.toString();
  });
};

// 导航到指定页面并添加标签
const navigateTo = (path) => {
  router.push(path);
};

// 定时更新时间并加载数据
onMounted(() => {
  setInterval(updateTime, 1000);
  loadStats();
});
</script>

<template>
  <div class="welcome-admin-container">
    <!-- 头部卡片 -->
    <div class="welcome-header">
      <div class="welcome-greeting">
        <h1>欢迎回来，{{ username }}</h1>
        <p class="date-time">{{ currentDate.toLocaleString() }}</p>
        <p class="welcome-quote">校园论坛管理系统 - 为校园信息交流提供便捷服务</p>
      </div>
      
      <div class="welcome-card">
        <el-image 
          src="https://www.jmu.edu.cn/images/logo.png"
          class="logo"
          fit="contain"
        />
      </div>
    </div>
    
    <!-- 数据概览 -->
    <h2 class="section-title">
      <el-icon><DataLine /></el-icon>
      系统概览
    </h2>
    
    <div class="stats-container">
      <el-card v-for="(stat, index) in stats" :key="index" class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-icon" :style="{ backgroundColor: stat.color + '15' }">
            <el-icon :style="{ color: stat.color }">
              <component :is="stat.icon" />
            </el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-title">{{ stat.title }}</div>
            <div class="stat-value">{{ stat.value }}</div>
          </div>
        </div>
      </el-card>
    </div>
    
    <!-- 快速操作区域 -->
    <h2 class="section-title">
      <el-icon><Setting /></el-icon>
      快速操作
    </h2>
    
    <div class="quick-actions">
      <el-button type="primary" class="action-button" @click="navigateTo('/admin/user')">
        <el-icon><User /></el-icon>
        用户管理
      </el-button>
      <el-button type="success" class="action-button" @click="navigateTo('/admin/forum')">
        <el-icon><ChatDotSquare /></el-icon>
        帖子管理
      </el-button>
    </div>
  </div>
</template>

<style scoped>
.welcome-admin-container {
  padding: 20px;
}

.welcome-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  background: linear-gradient(to right, #f0f7ff, #eaf6fc);
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.welcome-greeting h1 {
  margin: 0;
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.date-time {
  font-size: 16px;
  color: #606266;
  margin: 12px 0;
}

.welcome-quote {
  color: #909399;
  font-size: 14px;
}

.welcome-card {
  display: flex;
  justify-content: center;
  align-items: center;
}

.logo {
  height: 80px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 10px;
  margin: 30px 0 15px;
  font-size: 20px;
  color: #303133;
}

.stats-container {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.stat-card {
  height: 120px;
  transition: transform 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.stat-content {
  display: flex;
  height: 100%;
}

.stat-icon {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 60px;
  height: 60px;
  border-radius: 12px;
  font-size: 26px;
}

.stat-info {
  margin-left: 15px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.stat-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 5px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 5px;
  color: #303133;
}

.quick-actions {
  display: flex;
  gap: 20px;
  margin-bottom: 30px;
}

.action-button {
  flex: 1;
  height: 50px;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
}
</style>