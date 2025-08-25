<script setup>
import Mainpage from '../../components/Mainpage.vue';
import TheHeader from '../../components/TheHeader.vue';
import Toast from '../../components/Toast.vue';
const toastRef = ref();
import { ref, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';

const router = useRouter();
const route = useRoute();

import axios from 'axios';

const customers = ref([]);
const phone = ref(route.query.phone || '');
const points = ref(route.query.points || '');

const showAddCustomerForm = ref(false); // Trạng thái hiển thị form
const newCustomer = ref({
  name: '',
  phone: '',
  date_of_birth: '',
  points: 0,
});

const fetchCustomers = async () => {
  try {
    const response = await axios.get('http://localhost:8083/api/customers');
    customers.value = response.data;
  } catch (error) {
    console.error('Lỗi khi tải dữ liệu khách hàng:', error);
  }
};

const addCustomer = async () => {
  try {
    const response = await axios.post('http://localhost:8083/api/customers', newCustomer.value);
    customers.value.push(response.data);
    // alert('Thêm khách hàng thành công!');
    toastRef.value.show('Thêm khách hàng thành công!', 'success');
    showAddCustomerForm.value = false;
    resetNewCustomer(); // Xóa form
    router.push('/detail/Customer/Home');
  } catch (error) {
    console.error('Lỗi khi thêm khách hàng:', error);
    // alert('Lỗi khi thêm khách hàng');
    toastRef.value.show('Lỗi khi thêm khách hàng!', 'error');
  }
};

const resetNewCustomer = () => {
  newCustomer.value = {
    name: '',
    phone: '',
    date_of_birth: '',
    points: 0,
  };
};

onMounted(() => {
  fetchCustomers();
  newCustomer.value.phone = phone.value;
  newCustomer.value.points = Number(points.value);
});
</script>

<template>
  <div class="temp">
    <Mainpage />
    <div class="main-right">
      <TheHeader />
      <div class="container">
        <!-- form add -->
        <div class="add-customer-form">
          <h4>Thêm khách hàng mới</h4>
          <form @submit.prevent="addCustomer">
            <div>
              <label for="name">Tên khách hàng:</label>
              <input v-model="newCustomer.name" type="text" id="name" required />
            </div>
            <div>
              <label for="phone">Số điện thoại:</label>
              <input
                v-model="newCustomer.phone"
                type="text"
                id="phone"
                readonly
                class="readonly-input"
              />
            </div>
            <div>
              <label for="date_of_birth">Ngày sinh:</label>
              <input v-model="newCustomer.date_of_birth" type="date" id="date_of_birth" required />
            </div>
            <div>
              <label for="points">Điểm tích lũy:</label>
              <input
                v-model="newCustomer.points"
                type="number"
                id="points"
                readonly
                class="readonly-input"
              />
            </div>
            <div class="button-group">
              <button type="submit">Lưu</button>

              <router-link to="/invoice" class="cancel-button">Hủy</router-link>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
  <Toast ref="toastRef" />
</template>

<style scoped>
/* body{
    width: 100%;
    height:100%;
    font-size: 20px;
    font-family:'Noto Sans', sans-serif;
}

.temp{
    position:absolute;
    width: 100%;
    height:100%;
    font-size: 20px;
    top:0;
    left:0;
    margin: 0;
    padding:0;
    font-family:'Noto Sans', sans-serif;
    display:flex;
} */
body {
  width: 100%;
  height: 100%;
}

.temp {
  position: absolute;
  width: 100%;
  height: 100vh;
  /* font-size: 20px; */
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

/* form */
.add-customer-form {
  background-color: #f9f9f9;
  padding: 20px;
  border-radius: 5px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  margin-top: 20px;
}

.add-customer-form input,
.add-customer-form select {
  width: 100%;
  padding: 8px;
  margin: 10px 0;
  border: 1px solid #ccc;
  border-radius: 5px;
}

.add-customer-form button {
  background-color: #4caf50;
  color: white;
  padding: 10px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  margin-top: 10px;
}

.add-customer-form button[type='button'] {
  background-color: #f44336; /* Nút hủy */
}

.add-customer-form button:hover {
  background-color: #45a049;
}

.add-customer-form button[type='button']:hover {
  background-color: #e53935;
}

.cancel-button {
  display: inline-block;
  background-color: #f44336;
  color: white;
  padding: 10px;
  border-radius: 5px;
  text-decoration: none;
  margin-top: 10px;
  text-align: center;
  transition: background-color 0.3s;
}

.cancel-button:hover {
  background-color: #e53935;
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
.readonly-input {
  pointer-events: none; /* Không cho click hay focus */
  background-color: #f0f0f0; /* Tùy chỉnh để trông giống input bị khóa */
  color: #000;
}

.button-group {
  display: flex;
  gap: 20px;
  margin-top: 10px;
}
</style>
