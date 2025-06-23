import axios from 'axios'

// 分页查询通知记录
export function getRecordList(params) {
    return axios.get('/fraud/alert/record/queryByPage', { params })
}

// 查询详情
export function getRecordDetail(id) {
    return axios.get('/fraud/alert/record/detail', { params: { id } })
}

// 删除
export function deleteRecord(id) {
    return axios.delete('/fraud/alert/record/delete', { params: { id } })
}
