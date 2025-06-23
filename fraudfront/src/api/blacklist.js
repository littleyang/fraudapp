import axios from 'axios'

// 分页查询黑名单
export function getBlacklistList(params) {
    return axios.get('/fraud/blacklist/queryByPage', { params })
}

// 获取黑名单详情
export function getBlacklistDetail(id) {
    return axios.get('/fraud/blacklist/detail', { params: { id } })
}

// 创建黑名单
export function createBlacklist(data) {
    return axios.post('/fraud/blacklist/create', data)
}

// 更新黑名单
export function updateBlacklist(data) {
    return axios.put('/fraud/blacklist/update', data)
}

// 删除黑名单
export function deleteBlacklist(id) {
    return axios.delete('/fraud/blacklist/delete', { params: { id } })
}
