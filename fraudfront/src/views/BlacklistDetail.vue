<template>
  <div class="blacklist-detail">
    <h2>{{ isEdit ? '黑名单详情' : '新增黑名单' }}</h2>

    <form @submit.prevent="handleSubmit">
      <label>
        账号：
        <input v-model="form.account" :readonly="isEdit" required />
      </label>

      <label>
        姓名：
        <input v-model="form.name" :readonly="isEditMode" required />
      </label>

      <label>
        原因：
        <textarea v-model="form.reason" :readonly="isEditMode" required rows="3" />
      </label>

      <div class="btns">
        <button type="button" @click="goBack">返回</button>
        <button v-if="!isEditMode" type="submit">{{ isEdit ? '保存修改' : '提交新增' }}</button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  getBlacklistDetail,
  createBlacklist,
  updateBlacklist
} from '@/api/blacklist.js'

const route = useRoute()
const router = useRouter()
const id = route.params.id
const isEdit = id && id !== 'create'
const isEditMode = ref(false)

const form = reactive({
  id: '',
  account: '',
  name: '',
  reason: '',
  status: 0
})

onMounted(async () => {
  if (isEdit) {
    isEditMode.value = true
    const res = await getBlacklistDetail(id)
    if (res.data?.code === 'SUCCESS') {
      Object.assign(form, res.data.data)
    } else {
      alert('获取详情失败')
    }
  }
})

async function handleSubmit() {
  try {
    const action = isEdit ? updateBlacklist : createBlacklist
    const res = await action({ ...form })
    if (res.data?.code === 'SUCCESS') {
      alert(isEdit ? '更新成功' : '新增成功')
      router.push('/blacklist')
    }
  } catch (err) {
    alert('提交失败')
    console.error(err)
  }
}

function goBack() {
  router.push('/blacklist')
}
</script>

<style scoped>
.blacklist-detail {
  max-width: 600px;
  margin: auto;
}
form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}
label {
  display: flex;
  flex-direction: column;
  font-weight: bold;
}
input,
textarea {
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
