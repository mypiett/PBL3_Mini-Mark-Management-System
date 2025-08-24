<script setup>
import Mainpage from '../components/Mainpage.vue'
import TheHeader from '@/components/TheHeader.vue';
import { RouterLink } from 'vue-router';
// const role = response.data.position;
// console.log(role);
</script>

<template>
  <div class="temp">
    <Mainpage class="main-left"/>
    <div class="main-right">
      <TheHeader />
      <div class="container">
        <h1 class="brand-title">Abyss Elite</h1>
        <div class="custom-border">
          <p><span>Cơ sở 1:</span> 58 Hà Huy Tập, Thanh Khê, Đà Nẵng</p>
          <p><span>Cơ sở 2:</span> 25 Lạc Long Quân, Liên Chiểu, Hòa Khánh Bắc, Đà Nẵng</p>
          <p><span>Cơ sở 3:</span> 94 Võ Chí Công, Hòa Hải, Ngũ Hành Sơn, Đà Nẵng</p>
          <p><span>Hotline:</span> 0123 456 789</p>
          <p><span>Facebook:</span> facebook.com/abysselite</p>
        </div>

        <div class="dashboard-grid">
           <router-link style="text-decoration: none; color:#fff" to="/importProduct">
              <div class="dashboard-card" style="background-color: #ff6b6b;">➕ Nhập hàng mới</div>
            </router-link>

            <router-link style="text-decoration: none; color:#333" to="/createProduct">
              <div class="dashboard-card" style="background-color: #ffd93d;">➕ Thêm sản phẩm</div>
            </router-link>

            <router-link style="text-decoration: none; color:#fff" to="/createOrder">
              <div class="dashboard-card" style="background-color: #4cd964;">🧾 Tạo đơn bán hàng</div>
            </router-link>

            <router-link style="text-decoration: none; color:#fff" to="/detail/Employee">
              <div class="dashboard-card" style="background-color: #c084fc;">👨‍💼 Quản lý nhân viên</div>
            </router-link>

        </div>

        <div class="custom-border">
          <img src="../img/pageadmin.png" alt="page admin" class="responsive-img" />
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.temp {
  display: flex;
  min-height: 100vh;
  font-family: 'Noto Sans', sans-serif;
}

.main-left {
  width: 18%;
  height: 100vh;
  position: fixed;
  top: 0;
  left: 0;
  /* background: white; */
  z-index: 10;
}

.main-right {
  margin-left: 18%; /* khớp với width của sidebar */
  flex: 1;
  background: #f5f5f5;
  height: 100%;
  display: flex;
  flex-direction: column;
  z-index: 1; /* thấp hơn sidebar */
}


.container {
  padding: 30px;
  display: flex;
  flex-direction: column;
  gap: 24px;
  margin-top: 50px;
}

.brand-title {
  align-self: center;
  color: #388be3;
  font-size: 42px;
  font-weight: bold;
}

.dashboard-grid {
  display: flex;
  /* gap: 20px; */
  justify-content: space-between;
}

.dashboard-card {
  flex: 1;
  padding: 20px;
  width: 220px;
  color: white;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  text-align: center;
  transition: transform 0.2s ease;
}

.dashboard-card:hover {
  transform: translateY(-4px);
  filter: brightness(1.05);
  transform: scale(1.02);
}

.custom-border {
  flex: 1 1 45%;
  background: white;
  padding: 24px;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  transition: box-shadow 0.3s ease;
}

.custom-border:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
}
.dashboard-card {
  flex: 1;
  padding: 20px;
  width: 220px;
  color: white;
  border-radius: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  text-align: center;
  transition: all 0.3s ease;
  font-size: 16px;
  font-weight: 600;
  display: flex;
  justify-content: center;
  align-items: center;
  line-height: 1.5;
}

.dashboard-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.12);
  filter: brightness(1.05);
}

span {
  color: #267ad4;
  font-weight: 800;
}

.responsive-img {
  max-width: 100%;
  height: auto;
  display: block;
  margin: 0 auto;
}
</style>