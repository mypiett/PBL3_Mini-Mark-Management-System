<script setup>
import { ref } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';

const UserName = ref('');
const Password = ref('');
const inform = ref(null); // null: chưa check, true: thành công, false: thất bại
const isChecked = ref(false);
const loading = ref(false);
const router = useRouter();

const Check = async () => {
  // Validate đơn giản
  if (!UserName.value || !Password.value) {
    inform.value = false;
    isChecked.value = true;
    return;
  }

  isChecked.value = true;
  loading.value = true;

  try {
    const response = await axios.post('http://localhost:8083/api/employees/login', {
      user_name: UserName.value,
      password: Password.value
    });

    sessionStorage.setItem('employee', JSON.stringify(response.data));
    inform.value = true;
    const role = response.data.position;
    if (role.includes('Quản lý')) {
      await router.push('/home'); 
    } else {
      await router.push('/adminHome'); 
    }
  } catch (error) {
    inform.value = false;
  } finally {
    loading.value = false;
  }
};
</script>

<template>
  <div class="Big-background">
    <div class="Top-background"></div>
    <div class="main-content">
      <img
        class="logo"
        src="https://cdn.dribbble.com/userupload/28456975/file/original-923692d84cf3a6905b017e91981ed0af.gif"
        alt="logo"
      />

      <div class="border-SignIn">
        <div class="input-container">
          <input
            v-model="UserName"
            class="input-box"
            type="text"
            placeholder="Email or Username"
            style="width: 100%"
          />
          <font-awesome-icon :icon="['fas', 'envelope']" class="input-icon" />
        </div>

        <div class="input-container">
          <input
            v-model="Password"
            class="input-box"
            type="password"
            placeholder="Password"
            style="width: 100%"
          />
          <font-awesome-icon :icon="['fas', 'lock']" class="input-icon" />
        </div>

    

        <button
          class="button-SignIn"
          @click="Check"
          :disabled="loading"
        >
          {{ loading ? 'Đang đăng nhập...' : 'Đăng Nhập' }}
        </button>

        <!-- <div v-if="isChecked">
          <p
            class="informText"
            :class="inform === true ? 'success' : 'error'"
          >
            {{ inform === true
              ? 'Đăng nhập thành công'
              : 'Đăng nhập thất bại hoặc vui lòng nhập đủ thông tin' }}
          </p>
        </div> -->
        <div v-if="isChecked && inform === false">
          <p class="informText error">
            Đăng nhập thất bại hoặc vui lòng nhập đủ thông tin
          </p>
        </div>

        <p style="margin: 0">
          Bạn chưa có tài khoản Abyss?
          <router-link to="/theRegister" class="SignUp">Đăng ký</router-link>
        </p>
      </div>
    </div>
  </div>
</template>

<style scoped>
body {
  width: 100%;
  height: 100%;
  font-size: 20px;
  margin: 0;
  font-family: 'Noto Sans', sans-serif;
}

.Big-background {
  display: flex;
  flex-direction: column;
  background-color: #f5f5f5;
  width: 100%;
  height: 100vh;
  margin: 0;
  overflow-x: hidden;
  color: black;
  justify-content: center;
  align-items: center;
}

.Top-background {
  position: absolute;
  overflow-x: hidden;
  top: -35vh;
  left: 50%;
  transform: translateX(-50%);
  width: 120vw;
  height: 80vh;
  border-radius: 45%;
  background-color: #388be3;
}

.main-content {
  z-index: 10;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 50px;
}

.logo {
  width: 430px;
  height: 95px;
  border-radius: 50px;
  z-index: 10;
  object-fit: cover;
}

.border-SignIn {
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 20px;
  align-items: center;
  background-color: #ffffff;
  color: #737373;
  width: 100%;
  font-size: 20px;
  text-align: center;
  border-radius: 10px;
  padding: 15% 10%;
}

.input-container {
  width: 100%;
  display: flex;
  align-items: center;
  background-color: #f5f5f5;
  border-color: transparent;
  border-radius: 40px;
  font-size: 20px;
  padding: 20px;
  color: #a6a6a6;
}

.input-icon {
  color: #737373;
  font-size: 20px;
}

.input-box {
  flex: 1;
  background-color: #f5f5f5;
  color: #a6a6a6;
  font-size: 25px;
  border: none;
  outline: none;
}

.forget {
  font-size: 20px;
  align-self: flex-start;
  text-decoration: none;
  color: #737373;
}

.button-SignIn {
  cursor: pointer;
  font-size: 20px;
  border-color: transparent;
  background-color: #388be3;
  color: #ffffff;
  border-radius: 30px;
  padding: 15px 30px;
}

/* .informText {
  color: red;
  margin: 0;
  padding: 0;
} */

.SignUp {
  color: #388be3;
  font-weight: 600;
  font-size: 21px;
  text-decoration: none !important;
}
.informText {
  margin: 0;
  padding: 0;
  font-weight: 600;
}

.informText.error {
  color: red;
}

.informText.success {
  color: green;
}
</style>
