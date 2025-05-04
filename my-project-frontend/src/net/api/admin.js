import {get, post, del} from "@/net";

// Forum API
export const apiAdminForumTypes = (success) =>
    get('/api/admin/forum/types', success)

export const apiAdminAddForumType = (data, success) =>
    post('/api/admin/forum/type/add', data, success)

export const apiAdminUpdateForumType = (data, success) =>
    post('/api/admin/forum/type/update', data, success)

export const apiAdminDeleteForumType = (id, success) =>
    del(`/api/admin/forum/type/delete?id=${id}`, success)

// 统计数据API
export const apiAdminStats = (success) =>
    get('/api/admin/stats', success)