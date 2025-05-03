<script setup>
import {EditPen, User} from "@element-plus/icons-vue";
import {apiUserDetailTotal, apiUserList, apiUserSave} from "@/net/api/user";
import {reactive, watchEffect} from "vue";
import {useStore} from "@/store";
import {ElMessage} from "element-plus";

const store = useStore()

const editor = reactive({
    id: 0,
    display: false,
    temp: {},
    loading: false
})

//默认的获取分页参数
const userTable = reactive({
    page: 1,
    size: 10,
    total: 0,
    data: []
})

function userStatus(user) {
    if (user.mute && user.banned)
        return '禁言中、封禁中'
    else if (user.mute)
        return '禁言中'
    else if (user.banned)
        return '封禁中'
    else
        return '正常'
}

function openUserEditor(user) {
    editor.id = user.id
    editor.display = true
    editor.loading = true
    apiUserDetailTotal(editor.id, data => {
        editor.temp = {...data, ...user}
        editor.loading = false
    })
}

function saveUserDetail() {
    editor.display = false
    apiUserSave(editor.temp, () => {
        const user = userTable.data.find(user => user.id === editor.id)
        Object.assign(user, editor.temp)
        ElMessage.success('数据保存成功')
    })
}

//实时监控
watchEffect(() => apiUserList(userTable.page, userTable.size, data => {
    userTable.total = data.total
    userTable.data = data.list
}))
</script>

<template>
    <div class="user-admin">
        <div class="title">
            <el-icon>
                <User/>
            </el-icon>
            论坛用户列表
        </div>
        <div class="desc">
            在这里管理论坛的所有用户，包括账号信息、封禁和禁言处理。
        </div>
        <el-table :data="userTable.data" height="320">
            <el-table-column prop="id" label="编号" width="80"/>
            <el-table-column label="用户名" width="180">
                <template #default="{ row }">
                    <div class="table-username">
                        <el-avatar :size="30" :src="store.avatarUserUrl(row.avatar)"/>
                        <div>{{ row.username }}</div>
                    </div>
                </template>
            </el-table-column>
            <el-table-column label="角色" width="100" align="center">
                <template #default="{ row }">
                    <el-tag type="danger" v-if="row.role === 'admin'">管理员</el-tag>
                    <el-tag v-else>普通用户</el-tag>
                </template>
            </el-table-column>
            <el-table-column prop="email" label="电子邮件"/>
            <el-table-column label="注册时间">
                <template #default="{ row }">
                    {{ new Date(row.registerTime).toLocaleString() }}
                </template>
            </el-table-column>
            <el-table-column label="状态" align="center">
                <template #default="{ row }">
                    {{ userStatus(row) }}
                </template>
            </el-table-column>
            <el-table-column label="操作" align="center">
                <template #default="{ row }">
                    <el-button type="primary" size="small" :icon="EditPen"
                               @click="openUserEditor(row)"
                               :disabled="row.role === 'admin'">编辑
                    </el-button>
                </template>
            </el-table-column>
        </el-table>
        <div class="pagination">
            <el-pagination :total="userTable.total"
                           v-model:current-page="userTable.page"
                           v-model:page-size="userTable.size"
                           layout="total, sizes, prev, pager, next, jumper"/>
        </div>
        <el-drawer v-model="editor.display">
            <template #header>
                <div>
                    <div style="font-weight: bold">
                        <el-icon>
                            <EditPen/>
                        </el-icon>
                        编辑用户信息
                    </div>
                    <div style="font-size: 13px">编辑完成后请点击下方保存按钮</div>
                </div>
            </template>
            <el-form label-position="top">
                <el-form-item label="用户名">
                    <el-input v-model="editor.temp.username"/>
                </el-form-item>
                <el-form-item label="电子邮件">
                    <el-input v-model="editor.temp.email"/>
                </el-form-item>
                <div style="display: flex;font-size: 14px;gap: 20px">
                    <div>
                        <span style="margin-right: 10px">禁言</span>
                        <el-switch v-model="editor.temp.mute"/>
                    </div>
                    <el-divider style="height: 30px" direction="vertical"/>
                    <div>
                        <span style="margin-right: 10px">账号封禁</span>
                        <el-switch v-model="editor.temp.banned"/>
                    </div>
                </div>
                <div style="margin-top: 10px;color: #606266;font-size: 14px">
                    注册时间: {{ new Date(editor.temp.registerTime).toLocaleString() }}
                </div>
                <el-divider/>
            </el-form>
            <template #footer>
                <div style="text-align: center">
                    <el-button type="success" @click="saveUserDetail">保存</el-button>
                    <el-button type="info" @click="editor.display = false">取消</el-button>
                </div>
            </template>
        </el-drawer>
    </div>
</template>

<style lang="less" scoped>
.user-admin {
    .title {
        font-weight: bold;
    }

    .desc {
        color: #bababa;
        font-size: 13px;
        margin-bottom: 20px;
    }

    .table-username {
        height: 30px;
        display: flex;
        align-items: center;
        gap: 15px;
    }

    .pagination {
        margin-top: 20px;
        display: flex;
        justify-content: right;
    }

    :deep(.el-drawer__header) {
        margin-bottom: 0;
    }
}
</style>
