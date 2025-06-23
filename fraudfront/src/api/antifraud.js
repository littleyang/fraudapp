import axios from 'axios'

// 交易检测接口
export function evaluateTransaction(data) {
    return axios.post('/fraud/tx/evaluate/check', data)
}
