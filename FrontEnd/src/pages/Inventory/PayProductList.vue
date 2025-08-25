<script setup>
import MainPage from '../../components/Mainpage.vue';
import TheHeader from '../../components/TheHeader.vue';
// import axios from 'axios';
// import {useRoute} from 'vue-router';

// const route = useRoute();
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
          <div>
            <table class="product-table">
              <thead>
                <tr>
                  <th>Mã nhập hàng</th>
                  <th>Mã sản phẩm</th>
                  <th>Tên sản phẩm</th>
                  <th>Số lượng nhập</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td>{{ selectedDetail.importDetailId }}</td>
                  <td>{{ selectedDetail.product.productId }}</td>
                  <td>{{ selectedDetail.product.name }}</td>
                  <td>{{ selectedDetail.quantity }}</td>
                </tr>
              </tbody>
            </table>
          </div>
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
.product-table {
  width: 100%;
  border-collapse: collapse;
  background-color: white;
  margin-top: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.product-table th,
.product-table td {
  padding: 12px 16px;
  border: 1px solid #ccc;
  text-align: left;
}

.product-table th {
  background-color: #f0f0f0;
  font-weight: 600;
}
</style>
