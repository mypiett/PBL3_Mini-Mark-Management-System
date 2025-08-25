<script setup>
import Mainpage from '../../components/Mainpage.vue';
import Taskbar from '../../components/Taskbar.vue';
import { useRouter } from 'vue-router';
import { ref } from 'vue';

const router = useRouter();
const phone = ref('');
const points = ref('');

const goToAddCustomer = () => {
  router.push({
    path: '/detail/Customer/Create',
    query: {
      phone: phone.value,
      points: points.value,
    },
  });
};
</script>

<template>
  <div class="temp">
    <Mainpage />
    <div class="main-right">
      <Taskbar />
      <div class="container">
        <div class="header2">
          <input type="text" v-model="phone" placeholder="Số điện thoại" />
          <input type="text" v-model="points" placeholder="Điểm" />
          <button class="add-button" @click="goToAddCustomer">
            <font-awesome-icon :icon="['fas', 'circle-plus']" />
            Thêm khách hàng
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
body {
  width: 100%;
  height: 100%;
  font-size: 20px;
  font-family: 'Noto Sans', sans-serif;
}

.temp {
  position: absolute;
  width: 100%;
  height: 100%;
  font-size: 20px;
  top: 0;
  left: 0;
  margin: 0;
  padding: 0;
  font-family: 'Noto Sans', sans-serif;
  display: flex;
}

.main-right {
  margin-left: 18%;
  top: 0;
  background-color: #f5f5f5;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.container {
  width: 96%;
  background-color: none;
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-top: 90px;
  margin-left: 2%;
}

.header2 {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.add-button {
  background-color: #0088ff;
  padding: 10px 20px 10px 20px;
  /* padding:10px; */
  border-color: transparent;
  border-radius: 5px;
  font-weight: 600;
  color: white;
  cursor: pointer;
}
</style>
