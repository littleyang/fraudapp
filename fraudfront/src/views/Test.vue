<template>
  <div class="test-page">
    <h2>反欺诈测试</h2>

    <form class="form" @submit.prevent="submit">
      <label>
        交易ID：
        <input v-model="form.transactionId" placeholder="请输入交易ID" required />
      </label>

      <label>
        账号：
        <input v-model="form.account" placeholder="请输入账号" required />
      </label>

      <label>
        金额：
        <input type="number" v-model="form.amount" placeholder="请输入交易金额" required />
      </label>

      <label>
        时间戳（毫秒）：
        <input type="number" v-model="form.transactionTime" placeholder="请输入时间戳" required />
      </label>

      <label>
        描述：
        <input v-model="form.description" placeholder="请输入交易描述" />
      </label>

      <button type="submit">提交测试</button>
    </form>

    <div class="result" v-if="result">
      <h3>测试结果</h3>
      <p><strong>是否欺诈：</strong>
        <span :style="{ color: result.checkResult ? 'red' : 'green' }">
          {{ result.checkResult ? '是' : '否' }}
        </span>
      </p>
      <p><strong>检测说明：</strong></p>
      <pre>{{ result.reasons }}</pre>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import axios from 'axios'
import { evaluateTransaction } from '@/api/antifraud.js'

const form = reactive({
  transactionId: '',
  account: '',
  amount: '',
  transactionTime: '',
  description: ''
})

const result = ref(null)

// 提交函数
async function submit() {
  try {
    const res = await evaluateTransaction({ ...form })
    if (res.data?.code === 'SUCCESS') {
      let resultval = res.data.data
      console.log(resultval)
      result.value = res.data.data === undefined ? {} : res.data.data
    } else {
      alert('检测失败：' + res.data.message)
    }
  } catch (e) {
    alert('接口请求失败')
    console.error(e)
  }
}
</script>

<style scoped>
.test-page {
  max-width: 700px;
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
input {
  padding: 0.5rem;
  font-size: 1rem;
}
button {
  width: fit-content;
  padding: 0.5rem 1.2rem;
  background: #42b983;
  color: white;
  border: none;
  cursor: pointer;
}
.result {
  margin-top: 2rem;
  background: #f9f9f9;
  padding: 1rem;
  border-left: 4px solid #42b983;
}
pre {
  white-space: pre-wrap;
  line-height: 1.4;
  background: #eee;
  padding: 0.75rem;
  border-radius: 4px;
}
</style>
