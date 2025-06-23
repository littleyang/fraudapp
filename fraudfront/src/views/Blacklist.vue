<template>
  <div class="blacklist-page">
    <h2>黑名单管理</h2>

    <div class="search-box">
      <input v-model="searchForm.account" placeholder="输入账号搜索" />
      <button @click="search">查询</button>
      <router-link to="/blacklist/create">
        <button>新增黑名单</button>
      </router-link>
    </div>

    <table class="blacklist-table">
      <thead>
      <tr>
        <th>账号</th>
        <th>姓名</th>
        <th>原因</th>
        <th>操作</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="item in list" :key="item.id">
        <td>{{ item.account }}</td>
        <td>{{ item.name }}</td>
        <td>{{ item.reason }}</td>
        <td>
          <router-link :to="`/blacklist/${item.id}`">详情</router-link>
          <button @click="remove(item)">删除</button>
        </td>
      </tr>
      </tbody>
    </table>

    <div class="pagination">
      <button :disabled="page.pageNo === 1" @click="page.pageNo-- && loadList()">上一页</button>
      <span>第 {{ page.pageNo }} 页，共 {{ page.totalPage }} 页</span>
      <button :disabled="page.pageNo === page.totalPage" @click="page.pageNo++ && loadList()">下一页</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getBlacklistList, deleteBlacklist } from '@/api/blacklist.js'

const searchForm = ref({ account: '' })
const list = ref([])
const page = ref({
  pageNo: 1,
  pageSize: 10,
  totalPage: 1
})

function loadList() {
  getBlacklistList({
    account: searchForm.value.account,
    pageNo: page.value.pageNo,
    pageSize: page.value.pageSize
  }).then(res => {
    if (res.data?.code === 'SUCCESS') {
      list.value = res.data.data.items
      page.value.totalPage = res.data.data.page.totalPage
    }
  }).catch(err => {
    alert('查询失败')
    console.error(err)
  })
}

function search() {
  page.value.pageNo = 1
  loadList()
}

function remove(item) {
  if (confirm(`确认删除账号 ${item.account} 吗？`)) {
    deleteBlacklist(item.id).then(res => {
      if (res.data?.code === 'SUCCESS') {
        alert('删除成功')
        loadList()
      }
    }).catch(() => alert('删除失败'))
  }
}

onMounted(loadList)
</script>

<style scoped>
.blacklist-page {
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
  margin-top: 1rem;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 1rem;
}
button {
  padding: 0.5rem 1rem;
  cursor: pointer;
}
</style>
