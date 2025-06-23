<template>
  <div class="notify-detail">
    <h2>{{ isEdit ? '通知人员详情' : '新增通知人员' }}</h2>

    <form @submit.prevent="submit">
      <label>
        账号：
        <input v-model="form.user" :readonly="isEdit" required />
      </label>

      <label>
        姓名：
        <input v-model="form.name" required :readonly="readonly" />
      </label>

      <label>
        通知目标：
        <input v-model="form.destination" required :readonly="readonly" />
      </label>

      <label>
        通知方式：
        <select v-model="form.type" :disabled="readonly">
          <option value="phone">phone</option>
          <option value="email">email</option>
          <option value="sms">sms</option>
        </select>
      </label>

      <div class="btns">
        <button type="button" @click="goBack">返回</button>
        <button v-if="!readonly" type="submit">{{ isEdit ? '保存修改' : '提交新增' }}</button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { useRoute, useRouter } from 'vue-router'
import { ref, reactive, onMounted } from 'vue'
import {
  getNotifyDetail,
  createNotifyUser,
  updateNotifyUser
} from '@/api/notify'

const route = useRoute()
const router = useRouter()
const id = route.params.id
const isEdit = id && id !== 'create'
const readonly = ref(isEdit)

const form = reactive({
  id: '',
  user: '',
  name: '',
  destination: '',
  type: 'phone'
})

onMounted(() => {
  if (isEdit) {
    getNotifyDetail(id).then(res => {
      if (res.data?.code === 'SUCCESS') {
        Object.assign(form, res.data.data)
      }
    })
  }
})

function submit() {
  const fn = isEdit ? updateNotifyUser : createNotifyUser
  fn({ ...form }).then(res => {
    if (res.data?.code === 'SUCCESS') {
      alert(isEdit ? '更新成功' : '创建成功')
      router.push('/notify/users')
    }
  })
}

function goBack() {
  router.push('/notify/users')
}
</script>

<style scoped>
.notify-detail {
  max-width: 600px;
  margin: auto;
}
form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}
label {
  font-weight: bold;
  display: flex;
  flex-direction: column;
}
input, select {
  padding: 0.5rem;
  font-size: 1rem;
}
.btns {
  display: flex;
  gap: 1rem;
}
button {
  padding: 0.5rem 1.2rem;
  background: #42b983;
  color: white;
  border: none;
  cursor: pointer;
}
</style>
