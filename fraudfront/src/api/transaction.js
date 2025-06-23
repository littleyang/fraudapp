import axios from 'axios'

// 分页查询交易记录
export function getTransactionList(params) {
    return axios.get('/fraud/transaction/queryByPage', { params })
}

// 获取详情
export function getTransactionDetail(id) {
    return axios.get('/fraud/transaction/detail', { params: { id } })
}

// 删除交易记录
export function deleteTransaction(txId) {
    return axios.delete('/fraud/transaction/delete', { params: { txId } })
}
