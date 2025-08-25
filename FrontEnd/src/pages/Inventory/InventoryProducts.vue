<script setup>
import MainPage from '../../components/Mainpage.vue';
import TheHeader from '../../components/TheHeader.vue';
import ProductList from '../../components/ProductList.vue';
</script>

<template>
  <div class="main-container">
    <MainPage />
    <div class="container">
      <div class="page">
        <TheHeader />
        <div class="page-main">
          <div class="title">
            <h3>Quản lý kho</h3>
          </div>
          <ProductList :mode="'desc'" />
        </div>
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

.container {
  flex: 1;
  display: flex;
  flex-direction: column;
  background-color: #fff;
  margin-left: 18%;
  position: relative;
}

.page {
  flex: 1;
  display: flex;
  flex-direction: column;
  background-color: #fff;
  position: relative;
  font-family: Arial, Helvetica, sans-serif;
}
.page-main {
  display: flex;
  flex-direction: column;
  margin-top: 6%;
  padding: 0px 30px;
  background-color: #f5f5f5;
}
.title {
  display: flex;
  justify-content: space-between;
  padding: 10px;
  align-items: center;
}
.title h3 {
  font-size: 24px;
  font-weight: 600;
  color: rgb(50, 50, 50);
}
.addProduct {
  padding: 2px 15px;
  border-radius: 30px;
  background-color: #0884f1;
  color: #fff;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
}
.addProduct i {
  color: #fff;
  margin-right: 5px;
}
.addProduct:hover {
  background-color: #279aff;
  color: #f1f1f1;
}
.addProduct:hover i {
  color: #f1f1f1;
}
.addProduct:active {
  transform: scale(0.95);
}
a {
  text-decoration: none;
}
</style>
