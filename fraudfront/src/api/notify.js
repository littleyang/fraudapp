import axios from 'axios'

// 分页查询
export function getNotifyList(params) {
    return axios.get('/fraud/notify/user/queryByPage', { params })
}

// 单个详情
export function getNotifyDetail(id) {
    return axios.get('/fraud/notify/user/detail', { params: { id } })
}

// 创建
export function createNotifyUser(data) {
    return axios.post('/fraud/notify/user/create', data)
}

// 更新
export function updateNotifyUser(data) {
    return axios.put('/fraud/notify/user/update', data)
}

// 删除
export function deleteNotifyUser(id) {
    return axios.delete('/fraud/notify/user/delete', { params: { id } })
}
