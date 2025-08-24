<script setup>
import { ref, watch } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';

const passwordInput = ref(null);
const confirmInput = ref(null);
const showPassword = ref(false);
const employees = ref([]);
const router= useRouter();

const newEmployee = ref({
  name: '',
  phone: '',
  date_of_birth: '',
  position: '',
  user_name: '',
  password: '',
  salary: 0
});

const confirmPassword = ref('');
const passwordMismatch = ref(false);

// Hàm toggle show/hide mật khẩu
function togglePassword(){
    if(passwordInput.value && confirmInput.value) {
        const type = showPassword.value ? "text" : "password";
        passwordInput.value.type = type;
        confirmInput.value.type = type;
    }
}

// Kiểm tra mật khẩu có khớp không
function checkPasswordMatch() {
  passwordMismatch.value = newEmployee.value.password !== confirmPassword.value;
}

// Watch 2 biến password và confirmPassword để kiểm tra liên tục
watch(() => newEmployee.value.password, checkPasswordMatch);
watch(confirmPassword, checkPasswordMatch);

const addEmployee = async () => {
  if(passwordMismatch.value) {
    alert('Mật khẩu không khớp, vui lòng kiểm tra lại!');
    return;
  }
  try {
    const response = await axios.post('http://localhost:8083/api/employees', newEmployee.value);
    employees.value.push(response.data);
    alert('Thêm nhân viên thành công!');
    router.push('/');
  } catch (error) {
    console.error('Lỗi khi thêm nhân viên:', error);
    alert('Lỗi khi thêm nhân viên');
  }
};
</script>

<template>
  <div class="header">
    <div class="cricle-design">
      <div class="logo">
        <img src="https://cdn.dribbble.com/userupload/28456975/file/original-923692d84cf3a6905b017e91981ed0af.gif" alt="Logo Sieu thi">
      </div>
      <div class="title-part">
        <div class="title">Đăng Ký Tài Khoản</div>
      </div>
    </div>

    <div class="main-part">
      <div class="main">
        <div class="main-item">
          <input v-model="newEmployee.name" type="text" placeholder="Tên">
          <i class="fa-solid fa-user"></i>
        </div>
        <div class="main-item">
          <input v-model="newEmployee.phone" type="tel" placeholder="Số điện thoại">
          <i class="fa-solid fa-phone"></i>
        </div>
        <div class="main-item">
          <select v-model="newEmployee.position" required>
            <option disabled value="">Chọn chức vụ</option>
            <!-- <option value="Quản lý">Quản lý</option>
            <option value="Thu ngân">Thu ngân</option>
            <option value="Quét dọn">Quét dọn</option> -->
            <option value="Nhân viên">Nhân viên</option>
          </select>
        </div>
        <div class="main-item">
          <input v-model="newEmployee.user_name" type="text" placeholder="User Name">
          <i class="fa-solid fa-user"></i>
        </div>
        <div class="main-item">
          <input
            v-model="newEmployee.password"
            type="password"
            ref="passwordInput"
            placeholder="Mật khẩu"
          >
          <i class="fa-solid fa-lock"></i>
        </div>
        <div class="main-item">
          <input
            v-model="confirmPassword"
            type="password"
            ref="confirmInput"
            placeholder="Xác nhận mật khẩu"
          >
          <i class="fa-solid fa-unlock"></i>
        </div>
        <div v-if="passwordMismatch" style="color: red; font-size: 12px; margin-left: 15px;">
          Mật khẩu không khớp!
        </div>
        <!-- <div class="main-item">
          <input v-model="newEmployee.salary" type="number" placeholder="Lương">
          <i class="fa-solid fa-lock"></i>
        </div> -->
        <div class="checkBox">
          <label>
            <input type="checkbox" v-model="showPassword" @change="togglePassword">
            Hiển thị mật khẩu
          </label>
        </div>
        <button class="Button-Res" @click="addEmployee" :disabled="passwordMismatch">
          Đăng ký
        </button>
        <div>
          <span class="text">Bạn đã có tài khoản Abyss? </span>
          <router-link to="signIn" class="text-res">Đăng nhập</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
body {
    background-color: #f5f5f5;
    font-family: "Noto Sans", sans-serif;
    margin: 0;
    padding: 0;
    overflow: hidden;
    width: 100%;
    height: 100%;
}

.header {
    width: 100%;
    position: relative;
    font-family: "Noto Sans", sans-serif;
}

.cricle-design {
    position: relative;
    display: flex;
    align-items: flex-start;
    justify-content: space-between;
    width: 100%;
    height: 220px; /* tăng chiều cao để vừa với kích thước mới */
    background-color: #388be3;
    border-bottom-left-radius: 50% 110px;
    border-bottom-right-radius: 50% 110px;
    padding: 60px 30px;
}

.logo {
    width: 140px; /* to hơn */
    height: 60px; /* to hơn */
    display: flex;
    align-items: center;
    justify-content: flex-start;
}

.logo img {
    width: 100%;
    height: 100%;
    border-radius: 10px;
    object-fit: cover;
}

.title-part {
    flex-grow: 1;
    display: flex;
    justify-content: center;
}

.title {
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 15px 0;
    width: 35%;
    height: 60px; /* tăng chiều cao */
    background-color: #fff;
    color: #388be3;
    font-size: 22px; /* to chữ hơn */
    font-weight: 600;
    border-radius: 35px;
    text-align: center;
    box-shadow: 2px 2px 12px rgba(0, 0, 0, 0.15);
    transform: translateX(-90px);
}

.main-part {
    width: 100%;
    max-width: 450px; /* to hơn */
    position: absolute;
    box-sizing: border-box;
    top: 170px; /* đẩy xuống chút */
    left: 50%;
    transform: translateX(-50%);
    display: flex;
    justify-content: center;
    align-items: center;
    background-color: #fff;
    padding: 30px 30px 35px;
    border-radius: 20px;
    box-shadow: 2px 2px 15px rgba(0, 0, 0, 0.15);
}

.main {
    display: flex;
    flex-direction: column;
    align-items: center;
    width: 100%;
    gap: 15px; /* tăng khoảng cách giữa các input */
    padding-bottom: 20px;
}

.main-item {
    width: 95%;
    height: 45px; /* tăng chiều cao input */
    display: flex;
    align-items: center;
    background-color: #ebddd7;
    color: #382a40;
    border-radius: 30px;
    padding: 10px 20px;
    gap: 20px;
}

.main-item input {
    flex: 1;
    border: none;
    background: none;
    font-size: 18px; /* tăng cỡ chữ */
    outline: none;
}

.main-item select {
    flex: 1;
    border: none;
    background: none;
    font-size: 18px; /* to chữ */
    outline: none;
    appearance: none;
    padding: 10 15px;    /* tăng padding để chữ không bị che */
    height: 40px;         /* tăng chiều cao select */
    line-height: 1.2;     /* điều chỉnh line height cho text */
    border-radius: 30px;  /* giữ border radius giống input */
    height: 100%;
}

.main-item select:focus {
    outline: none;
}

.main-item select option {
    background-color: white;
    color: #333;
}

.main-item i {
    font-size: 20px; /* icon to hơn */
    color: #382a40;
}

.Button-Res {
    background-color: #388be3;
    color: #fff;
    display: flex;
    justify-content: center;
    align-items: center;
    width: 50%; /* to hơn */
    height: 45px; /* to hơn */
    outline: none;
    padding: 10px 0;
    border: none;
    font-size: 18px; /* to chữ */
    border-radius: 30px;
    cursor: pointer;
    margin-top: 10px;
}

.Button-Res:disabled {
    background-color: #a0b6e0;
    cursor: not-allowed;
}

.text {
    color: #737373;
    font-size: 14px; /* to chữ */
}

.text-res {
    color: #388be3;
    font-size: 15px; /* to chữ */
    margin-left: 12px;
    cursor: pointer;
    text-decoration: none;
}

.checkBox {
    font-size: 14px; /* to chữ */
    color: #737373;
    margin-top: 10px;
    width: 95%;
}

.checkBox label {
    display: flex;
    justify-content: flex-start;
    align-items: center;
    margin-left: -10px;
    color: #737373;
}

.checkBox input[type="checkbox"] {
    width: 18px; /* to checkbox */
    height: 18px;
    margin-right: 8px;
}

.btn {
  padding: 12px 24px;
  margin: 0 12px;
  border: none;
  border-radius: 5px;
  color: white;
  font-weight: bold;
  cursor: pointer;
}

.btn-confirm {
  background-color: #28a745; /* xanh lá */
}

/* .btn-cancel {
  background-color: #dc3545; 
} */


</style>
