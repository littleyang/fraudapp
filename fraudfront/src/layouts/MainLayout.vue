<template>
  <div class="layout">
    <aside class="sidebar">
      <h3>反欺诈检测</h3>
      <ul>
        <li><router-link to="/rule">检测规则设置</router-link></li>
        <li><router-link to="/blacklist">黑名单管理</router-link></li>
        <li><router-link to="/notify/users">通知人员管理</router-link></li>
        <li><router-link to="/notify/records">通知查询</router-link></li>
        <li><router-link to="/transaction">交易记录</router-link></li>
        <li><router-link to="/test">反欺诈测试</router-link></li>
      </ul>
    </aside>
    <main class="content">
      <router-view />
    </main>
  </div>
</template>

<style scoped>
.layout {
  display: flex;
  height: 100vh;
}
.sidebar {
  width: 220px;
  background: #f5f5f5;
  padding: 1rem;
}
.content {
  flex: 1;
  padding: 1rem;
  overflow: auto;
}
ul {
  list-style: none;
  padding: 0;
}
li {
  margin: 0.5rem 0;
}
a.router-link-active {
  font-weight: bold;
  color: #42b983;
}
</style>
<script setup lang="ts">
</script>
