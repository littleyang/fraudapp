<template>
  <div class="rule-page">
    <h2>检测规则设置</h2>

    <form @submit.prevent="submit">
      <label>
        欺诈交易金额：
        <input type="number" v-model="form.amount" placeholder="金额（单位元）" />
      </label>

      <label>
        欺诈交易时段（0-24格式）：
        <input v-model="form.offHours" placeholder="如 2-3" />
      </label>

      <label>
        是否开启黑名单：
        <select v-model="form.blackCheck">
          <option :value="true">开启</option>
          <option :value="false">关闭</option>
        </select>
      </label>

      <button type="submit">{{ isUpdate ? '保存修改' : '首次创建' }}</button>
    </form>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getRule, createRule, updateRule } from '@/api/rule.js'

const form = reactive({
  amount: '',
  offHours: '',
  blackCheck: true
})

const isUpdate = ref(false)

onMounted(async () => {
  try {
    const res = await getRule()
    if (res.data?.code === 'SUCCESS' && res.data.data) {
      Object.assign(form, res.data.data)
      isUpdate.value = true
    }
  } catch (e) {
    console.error('规则获取失败', e)
  }
})

async function submit() {
  try {
    const action = isUpdate.value ? updateRule : createRule
    const res = await action({ ...form })
    if (res.data?.code === 'SUCCESS') {
      alert(isUpdate.value ? '修改成功' : '创建成功')
      isUpdate.value = true
    }
  } catch (err) {
    alert('提交失败')
    console.error(err)
  }
}
</script>

<style scoped>
.rule-page {
  max-width: 600px;
  margin: auto;
}
form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}
input, select {
  padding: 0.5rem;
  font-size: 1rem;
}
button {
  padding: 0.5rem 1.5rem;
  background: #42b983;
  color: white;
  border: none;
  cursor: pointer;
}
</style>
