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

// 置顶帖子管理API
export const apiAdminTopics = (success) =>
    get('/api/admin/forum/topics', success)

export const apiAdminTopTopics = (success) =>
    get('/api/admin/forum/top-topics', success)

export const apiAdminSetTopicTop = (tid, success) =>
    post(`/api/admin/forum/topic/set-top?tid=${tid}`, {}, success)

export const apiAdminCancelTopicTop = (tid, success) =>
    post(`/api/admin/forum/topic/cancel-top?tid=${tid}`, {}, success)

// 统计数据API
export const apiAdminStats = (success) =>
    get('/api/admin/stats', success)