<script setup>
import { defineProps } from 'vue';
import { RouterLink } from 'vue-router';
import { ref } from 'vue';
const props = defineProps(['data']);
</script>

<template>
  <div class="taskbar">
    <div class="search">
      <font-awesome-icon class="icon" :icon="['fas', 'magnifying-glass']" />
      <input type="text" class="search-box" name="search" placeholder="Tìm kiếm (Ctrl + K)" />
    </div>
    <div class="infor">
      <font-awesome-icon class="icon" :icon="['fas', 'circle-question']" />
      <font-awesome-icon class="icon" :icon="['fas', 'bell']" />
      <button id="fullNameChoice">
        <img src="" alt="" />
        <!-- {{ name }}     -->
        Tran Thi Thuy Tien
        <font-awesome-icon class="icon" style="font-size: 14px" :icon="['fas', 'chevron-down']" />
        <ul id="dropdown" class="hidden">
          <div>Tài khoản</div>
          <div>Gói dịch vụ</div>
          <div>Điều khoản dịch vụ</div>
          <div>Chính sách bảo mật</div>
          <div>Hỗ trợ</div>
          <div style="color: red">Đăng xuất</div>
        </ul>
      </button>
    </div>
  </div>
</template>

<style scoped>
.taskbar {
  width: 100%;
  height: 70px;
  display: flex;
  position: fixed;
  background-color: #f5f5f5;
  justify-content: space-between;
  align-items: center;
  /* border: 1px solid #ddd; */
  box-shadow: 0px 2px 6px rgba(0, 0, 0, 0.2);
  z-index: 998;
}

.search {
  display: flex;
  flex: 60%;
  gap: 10px;
  margin-left: 30px;
}

.search-box {
  width: 100%;
  font-size: 18px;
  outline: none;
  border-color: transparent;
  background-color: #f5f5f5;
}

.infor {
  /* z-index: 999;
    background-color: red; */
  display: flex;
  flex: 35%;
  height: 100%;
  align-items: center;
  gap: 20px;
  margin-right: 30px;
}

.icon {
  font-size: 18px;
  color: #828282;
}

#fullNameChoice {
  height: 100%;
  font-size: 18px;
  display: flex;
  background-color: transparent;
  border-color: transparent;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  transition:
    color 0.15s ease-in-out,
    background-color 0.15s ease-in-out,
    border-color 0.15s ease-in-out,
    box-shadow 0.15s ease-in-out;
}

#fullNameChoice:hover {
  background-color: #e6f4ff;
  color: #2e9eff;
}
.hidden {
  display: none;
}

ul {
  top: 70px;
  list-style: none;
  padding: 10px;
  margin: 5px 0;
  background: white;
  border: 1px solid #ccc;
  position: absolute;
  text-align: left;
  width: 200px;
}

ul div {
  font-weight: 100;
  padding: 10px;
  cursor: pointer;
}

ul div:hover {
  background: #f2f9ff;
}
</style>
