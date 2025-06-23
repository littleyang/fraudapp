<template>
  <div class="tx-page">
    <h2>交易记录</h2>

    <div class="search-box">
      <input v-model="search.account" placeholder="搜索账号" />
      <button @click="searchList">查询</button>
    </div>

    <table>
      <thead>
      <tr>
        <th>交易编号</th>
        <th>账号</th>
        <th>金额</th>
        <th>时间</th>
        <th>操作</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="item in list" :key="item.id">
        <td>{{ item.transactionId }}</td>
        <td>{{ item.account }}</td>
        <td>{{ item.amount }}</td>
        <td>{{ formatTime(item.transactionTime) }}</td>
        <td>
          <button @click="view(item)">详情</button>
          <button @click="remove(item)">删除</button>
        </td>
      </tr>
      </tbody>
    </table>

    <div class="pagination">
      <button :disabled="page.pageNo === 1" @click="page.pageNo-- && load()">上一页</button>
      <span>第 {{ page.pageNo }} 页</span>
      <button :disabled="page.pageNo === page.totalPage" @click="page.pageNo++ && load()">下一页</button>
    </div>

    <div v-if="detailVisible" class="detail-box">
      <h3>交易详情</h3>
      <p><strong>交易ID：</strong>{{ detail.transactionId }}</p>
      <p><strong>账号：</strong>{{ detail.account }}</p>
      <p><strong>金额：</strong>{{ detail.amount }}</p>
      <p><strong>时间：</strong>{{ formatTime(detail.transactionTime) }}</p>
      <p><strong>描述：</strong>{{ detail.description || '无' }}</p>
      <button @click="detailVisible = false">关闭</button>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import {
  getTransactionList,
  getTransactionDetail,
  deleteTransaction
} from '@/api/transaction'

const search = reactive({ account: '' })
const list = ref([])
const page = reactive({ pageNo: 1, pageSize: 10, totalPage: 1 })

const detail = ref({})
const detailVisible = ref(false)

function load() {
  getTransactionList({ account: search.account, pageNo: page.pageNo, pageSize: page.pageSize }).then(res => {
    if (res.data?.code === 'SUCCESS') {
      list.value = res.data.data.items
      page.totalPage = res.data.data.page.totalPage
    }
  })
}

function searchList() {
  page.pageNo = 1
  load()
}

function formatTime(ts) {
  return new Date(ts).toLocaleString()
}

function view(item) {
  getTransactionDetail(item.id).then(res => {
    if (res.data?.code === 'SUCCESS') {
      detail.value = res.data.data
      detailVisible.value = true
    }
  })
}

function remove(item) {
  if (confirm(`确认删除交易 ${item.transactionId} 吗？`)) {
    deleteTransaction(item.transactionId).then(res => {
      if (res.data?.code === 'SUCCESS') {
        alert('删除成功')
        load()
      }
    })
  }
}

onMounted(load)
</script>

<style scoped>
.tx-page {
  max-width: 1000px;
  margin: auto;
}
.search-box {
  display: flex;
  gap: 1rem;
  margin-bottom: 1rem;
}
table {
  width: 100%;
  border-collapse: collapse;
}
th, td {
  padding: 0.75rem;
  border: 1px solid #ddd;
}
th {
  background-color: #f5f5f5;
}
.pagination {
  margin-top: 1rem;
  display: flex;
  justify-content: center;
  gap: 1rem;
}
.detail-box {
  margin-top: 2rem;
  padding: 1rem;
  background: #fafafa;
  border-left: 4px solid #42b983;
}
button {
  padding: 0.4rem 1rem;
}
</style>
