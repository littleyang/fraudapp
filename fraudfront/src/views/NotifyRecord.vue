<template>
  <div class="record-page">
    <h2>通知记录</h2>

    <div class="search-box">
      <input v-model="searchUser" placeholder="请输入通知账号" />
      <button @click="search">查询</button>
    </div>

    <table>
      <thead>
      <tr>
        <th>通知账号</th>
        <th>目标</th>
        <th>方式</th>
        <th>金额</th>
        <th>交易ID</th>
        <th>内容</th>
        <th>操作</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="item in list" :key="item.id">
        <td>{{ item.notifyUser }}</td>
        <td>{{ item.destination }}</td>
        <td>{{ item.type }}</td>
        <td>{{ item.amount }}</td>
        <td>{{ item.transactionId }}</td>
        <td style="max-width: 300px; white-space: pre-wrap;">{{ item.content }}</td>
        <td>
          <button @click="detail(item)">详情</button>
          <button @click="remove(item)">删除</button>
        </td>
      </tr>
      </tbody>
    </table>

    <div class="pagination">
      <button :disabled="pageNo === 1" @click="pageNo-- && load()">上一页</button>
      <span>第 {{ pageNo }} 页</span>
      <button :disabled="pageNo === totalPage" @click="pageNo++ && load()">下一页</button>
    </div>

    <div v-if="showDetail" class="detail-box">
      <h3>通知详情</h3>
      <p><strong>通知账号：</strong>{{ current.notifyUser }}</p>
      <p><strong>目标：</strong>{{ current.destination }}</p>
      <p><strong>方式：</strong>{{ current.type }}</p>
      <p><strong>交易账号：</strong>{{ current.account }}</p>
      <p><strong>交易金额：</strong>{{ current.amount }}</p>
      <p><strong>交易ID：</strong>{{ current.transactionId }}</p>
      <p><strong>内容：</strong></p>
      <pre>{{ current.content }}</pre>
      <button @click="showDetail = false">关闭</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import {
  getRecordList,
  getRecordDetail,
  deleteRecord
} from '@/api/record.js'

const searchUser = ref('')
const list = ref([])
const pageNo = ref(1)
const totalPage = ref(1)
const showDetail = ref(false)
const current = ref({})

function load() {
  getRecordList({ pageNo: pageNo.value, pageSize: 10, user: searchUser.value }).then(res => {
    if (res.data?.code === 'SUCCESS') {
      list.value = res.data.data.items
      totalPage.value = res.data.data.page.totalPage
    }
  })
}

function search() {
  pageNo.value = 1
  load()
}

function detail(item) {
  getRecordDetail(item.id).then(res => {
    if (res.data?.code === 'SUCCESS') {
      current.value = res.data.data
      showDetail.value = true
    }
  })
}

function remove(item) {
  if (confirm(`确认删除记录？`)) {
    deleteRecord(item.id).then(res => {
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
.record-page {
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
  border: 1px solid #ddd;
  padding: 0.75rem;
}
th {
  background-color: #f5f5f5;
}
.pagination {
  display: flex;
  justify-content: center;
  gap: 1rem;
  margin-top: 1rem;
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
pre {
  background: #eee;
  padding: 0.5rem;
  white-space: pre-wrap;
}
</style>
