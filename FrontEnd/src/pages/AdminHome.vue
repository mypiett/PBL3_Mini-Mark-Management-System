<script setup>
import MainPage from '../components/Mainpage.vue';
import TheHeader from '../components/TheHeader.vue';
import EmployeeMain from '../components/EmployeeMain.vue';
</script>

<template>
  <div class="main-container">
    <MainPage class="main-left" />
    <div class="container">
      <div class="page">
        <TheHeader class="header" />
        <EmployeeMain />
      </div>
    </div>
  </div>
</template>

<style scoped>
body {
  padding: 0;
  margin: 0;
  overflow: hidden;
}
.main-container {
  display: flex;
  width: 100%;
  height: 100vh;
  position: relative;
}
.main-left {
  width: 18%;
  height: 100vh;
  position: fixed;
  top: 0;
  left: 0;
  /* background: #fff; */
  z-index: 1000;
}
.header {
  position: relative;
  width: 100%;
  z-index: 1;
}

.container {
  flex: 1;
  display: flex;
  flex-direction: column;
  background-color: #fff;
  margin-left: 18%;
  /* position: relative; */
}

.page {
  flex: 1;
  display: flex;
  flex-direction: column;
  background-color: #fff;
}
</style>
