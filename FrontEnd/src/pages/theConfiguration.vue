<script setup>
import { ref, computed, onMounted } from 'vue';
import Mainpage from '../components/Mainpage.vue';
import TheHeader from '@/components/TheHeader.vue';

// Giả lập dữ liệu người dùng từ localStorage
const storedUser = ref(JSON.parse(localStorage.getItem('employee')) || {});
const isManager = computed(() => storedUser.value?.position?.toLowerCase().includes('quản lý'));

// Dữ liệu cửa hàng (đồng bộ với trang home)
const storeInfo = ref({
  branch1: '58 Hà Huy Tập, Thanh Khê, Đà Nẵng',
  branch2: '25 Lạc Long Quân, Liên Chiểu, Hòa Khánh Bắc, Đà Nẵng',
  branch3: '94 Võ Chí Công, Hòa Hải, Ngũ Hành Sơn, Đà Nẵng',
  hotline: '0123 456 789',
  facebook: 'facebook.com/abysselite',
});

const editing = ref(false);

function enableEdit() {
  if (isManager.value) editing.value = true;
}

function saveChanges() {
  editing.value = false;
  // TODO: Gọi API hoặc lưu vào localStorage nếu cần
}
</script>

<template>
  <div class="temp">
    <Mainpage class="main-left" />
    <div class="main-right">
      <TheHeader />
      <div class="container">
        <h1 class="brand-title">Cấu hình cửa hàng</h1>

        <div class="custom-border">
          <div class="store-section">
            <label>Cơ sở 1:</label>
            <input v-model="storeInfo.branch1" :disabled="!editing" />

            <label>Cơ sở 2:</label>
            <input v-model="storeInfo.branch2" :disabled="!editing" />

            <label>Cơ sở 3:</label>
            <input v-model="storeInfo.branch3" :disabled="!editing" />

            <label>Hotline:</label>
            <input v-model="storeInfo.hotline" :disabled="!editing" />

            <label>Facebook:</label>
            <input v-model="storeInfo.facebook" :disabled="!editing" />
          </div>
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
  z-index: 10;
}

.main-right {
  margin-left: 18%;
  flex: 1;
  background: #f5f5f5;
  height: 100%;
  display: flex;
  flex-direction: column;
  z-index: 1;
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

.custom-border {
  background: white;
  padding: 24px;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  transition: box-shadow 0.3s ease;
}

.custom-border:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
}

.store-section {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.store-section label {
  font-weight: bold;
  color: #267ad4;
}

.store-section input {
  padding: 8px 12px;
  border: 1px solid #ccc;
  border-radius: 8px;
  font-size: 14px;
}

.button-group {
  margin-top: 20px;
  display: flex;
  gap: 12px;
}

.edit-btn,
.save-btn {
  padding: 10px 16px;
  font-weight: bold;
  border: none;
  border-radius: 8px;
  cursor: pointer;
}

.edit-btn {
  background-color: #f7c706;
  color: #333;
}

.save-btn {
  background-color: #16bc50;
  color: white;
}

.responsive-img {
  max-width: 100%;
  height: auto;
  display: block;
  margin: 0 auto;
}
</style>
