<template>
  <div class="notify-page">
    <h2>通知人员管理</h2>

    <div class="search-box">
      <input v-model="searchUser" placeholder="输入账号查询" />
      <button @click="search">查询</button>
      <router-link to="/notify/users/create"><button>新增</button></router-link>
    </div>

    <table>
      <thead>
      <tr>
        <th>账号</th>
        <th>姓名</th>
        <th>目标</th>
        <th>通知方式</th>
        <th>操作</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="item in list" :key="item.id">
        <td>{{ item.user }}</td>
        <td>{{ item.name }}</td>
        <td>{{ item.destination }}</td>
        <td>{{ item.type }}</td>
        <td>
          <router-link :to="`/notify/users/${item.id}`">详情</router-link>
          <button @click="remove(item)">删除</button>
        </td>
      </tr>
      </tbody>
    </table>

    <div class="pagination">
      <button :disabled="pageNo === 1" @click="pageNo-- && load()">上一页</button>
      <span>第 {{ pageNo }} 页</span>
      <button @click="pageNo++ && load()">下一页</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getNotifyList, deleteNotifyUser } from '@/api/notify'

const searchUser = ref('')
const list = ref([])
const pageNo = ref(1)

function load() {
  getNotifyList({ user: searchUser.value, pageNo: pageNo.value, pageSize: 10 }).then(res => {
    if (res.data?.code === 'SUCCESS') {
      list.value = res.data.data.items
    }
  })
}

function search() {
  pageNo.value = 1
  load()
}

function remove(item) {
  if (confirm(`确认删除 ${item.user} 吗？`)) {
    deleteNotifyUser(item.id).then(() => {
      alert('删除成功')
      load()
    })
  }
}

onMounted(load)
</script>

<style scoped>
.notify-page {
  max-width: 900px;
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
  background: #f4f4f4;
}
button {
  margin-right: 0.5rem;
  padding: 0.4rem 1rem;
}
.pagination {
  margin-top: 1rem;
  display: flex;
  gap: 1rem;
  justify-content: center;
}
</style>
