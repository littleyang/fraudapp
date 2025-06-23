import axios from 'axios'

export function getRule() {
    return axios.get('/fraud/rule/current/rule')
}

export function createRule(data) {
    return axios.post('/fraud/rule/create', data)
}

export function updateRule(data) {
    return axios.put('/fraud/rule/update', data)
}
