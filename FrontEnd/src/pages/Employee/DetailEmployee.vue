<script setup>
import Mainpage from '../../components/Mainpage.vue'
import TheHeader from '../../components/TheHeader.vue';
import Toast from '../../components/Toast.vue';
const toastRef = ref();
import { ref, onMounted} from 'vue';
import { useRoute } from 'vue-router';
import axios from 'axios';

const route = useRoute();
const employeeId = route.query.employee_id;

const employee = ref({
  name: '',
  phone: '',
  date_of_birth: '',
  position: '',
  user_name: '',
  password: '',
  salary: 0
});

const getEmployeeById = async () =>{
  try{
    const response = await axios.get(`http://localhost:8083/api/employees/${employeeId}`);
    employee.value = response.data;
  }catch (error){
    console.error('Lỗi khi lấy thông tin nhân viên:', error);
    // alert('Lỗi khi lấy thông tin nhân viên');
    toastRef.value.show('Lỗi khi lấy thông tin nhân viên', 'error');
  }
}


onMounted(() => {
  getEmployeeById();
});

</script>


<template>
    <div class="temp">
        <Mainpage />
        <div class="main-right">
            <TheHeader />
            <!-- <BackButton /> -->
            <div class="container">
                <div class="header2">
                    <h3>Thông tin chi tiết của nhân viên </h3>
                </div>
                <div class="add-employee-form">
                    <div>
                    <label for="employee_id">Id:</label>
                    <input v-model="employee.employee_id" type="text" id="employee_id" disabled />
                    </div>
                    <div>
                    <label for="name">Tên nhân viên :</label>
                    <input v-model="employee.name" type="text" id="name" disabled />
                    </div>
                    <div>
                    <label for="phone">Số điện thoại:</label>
                    <input v-model="employee.phone" type="text" id="phone" disabled />
                    </div>
                    <div>
                    <label for="date_of_birth">Ngày sinh:</label>
                    <input v-model="employee.date_of_birth" type="date" id="date_of_birth" disabled />
                    </div>
                    <div>
                    <label for="position">Chức vụ:</label>
                    <select v-model="employee.position" disabled>
                        <option value="Quản lý">Quản lý</option>
                        <option value="Nhân viên">Nhân viên</option>
                        <!-- <option value="Thu ngân">Thu ngân</option>
                        <option value="Quét dọn">Quét dọn</option>
                        <option value="Bảo vệ">Bảo vệ</option> -->
                    </select>
                    </div>
                    <div>
                    <label for="user_name">Tên tài khoản:</label>
                    <input v-model="employee.user_name" type="text" id="user_name" disabled />
                    </div>
                    <div>
                    <label for="password">Mật khẩu:</label>
                    <input v-model="employee.password" type="text" id="password" disabled />
                    </div>
                    <div>
                    <label for="salary">Lương:</label>
                    <input v-model="employee.salary" type="number" id="salary" disabled />
                    </div>
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
 body{
    width: 100%;
    height:100%;
}

.temp{
    position:absolute;
    width: 100%;
    height:100vh;
    /* font-size: 20px; */
    top:0;
    left:0;
    margin: 0;
    padding:0;
    font-family:'Noto Sans', sans-serif;
    display:flex;
}

.main-right{
    margin-left: 18%;
    top:0;
    background-color: #f5f5f5;
    flex:1;
    display:flex;
    flex-direction: column;
}

.container{
    width: 96%;
    background-color: none;
    display: flex;
    flex-direction: column;
    gap:10px;
    margin-top:90px;
    margin-left:2%;
}

.header2{
    width: 100%;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.8); 
  backdrop-filter: blur(2px);
  z-index: 999;
}

/* form */
.add-employee-form {
  /* position: fixed; */
  /* top: 50%; */
  /* left: 60%; */
  /* transform: translate(-50%, -50%); */
  max-height: 90vh;
  overflow-y: auto; 
  background: #f9f9f9;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.3);
  /* z-index: 1000;  */
  /* width: 50%; */
  box-sizing: border-box;
}

.add-employee-form input,
.add-employee-form select {
  width: 100%;
  padding: 8px;
  margin: 10px 0;
  border: 1px solid #ccc;
  border-radius: 5px;
}

.add-employee-form button {
  background-color: #4CAF50;
  color: white;
  padding: 10px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  margin-top: 10px;
}

.add-employee-form button[type="button"] {
  background-color: #f44336; /* Nút hủy */
}

.add-employee-form button:hover {
  background-color: #45a049;
}

.add-employee-form button[type="button"]:hover {
  background-color: #e53935;
}

.white-container{
    background-color: white;
    width: 100%;
    height:100vh;
    border-radius: 5px;
    box-shadow: 5px 10px 15px 0px rgba(0, 0, 0, 0.2);
    padding: 10px;
    box-sizing: border-box;
    display:flex;
    flex-direction: column;
    gap:20px;
}

.add-button{
    background-color: #0088ff;
    padding:10px 20px 10px 20px;
    /* padding:10px; */
    border-color: transparent;
    border-radius: 5px;
    font-weight: 600;
    color:white;
    cursor: pointer;
}

.search-name{
    display:flex;
    width: 40%;
    background-color:white;
    padding: 5px 10px;
    border-color: #828282;
    border-style: solid;
    border-width:1px;
    border-radius: 5px;
    align-items: center;
}

.search-box{
    width: 100%;
    font-size: 15px;
    outline: none;
    border-color: transparent;
    background-color: white;
}

.list-container{
    display:flex;
    width: 50%;
    background-color:white;
    /* padding: 5px 10px; */
    border-color: #828282;
    border-style: solid;
    border-width:1px;
    border-radius: 5px;
    align-items: center;
}

.list-box{
    width: 100%;
    font-size: 15px;
    outline: none;
    border-width: 1px;
    padding: 5px 10px;
    border-color:transparent #828282 transparent transparent;
    background-color: white;
    border-radius: 5px 0 0 5px;
    display: flex;
    justify-content: space-around;
    align-items: center;
}

.icon{
    font-size: 15px;
    color:#828282;
}

.employee-table {
  width: 100%;
  border: 1px solid #d1d5db; 
  border-collapse: collapse;
  font-size: 16px;
}

.employee-table thead {
  background-color: #f3f4f6; 
}

.employee-table th,
.employee-table td {
  border: 1px solid #d1d5db;
  padding: 8px 16px; 
  text-align: left;
}

/* Nút "Sửa" */
.btn-edit {
  background-color: #3b82f6; 
  color: white;
  padding: 4px 12px;
  border: none;
  border-radius: 5px;
  margin-right: 8px;
  cursor: pointer;
}

/* Nút "Xóa" */
.btn-delete {
  background-color: #ef4444;
  color: white;
  padding: 4px 12px;
  border: none;
  border-radius: 5px;
  margin-right: 8px;
  cursor: pointer;
}

.btn-view{
  background-color: darkorange;
  color: white;
  padding: 4px 12px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.dropdown-container {
  position: relative;
  display: inline-block;
  width: 100%;
}

.dropdown-menu {
  position: absolute;
  background-color: white;
  border: 1px solid #ccc;
  border-radius: 5px;
  width: 100%;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  z-index: 1;
}

.dropdown-menu div {
  padding: 10px;
  cursor: pointer;
}

.dropdown-menu div:hover {
  background-color: #f0f0f0;
}

.button-group {
  display: flex;
  gap: 20px;
  margin-top: 10px;
}

.display-jus-ali {
  display: flex;
  justify-content: center;
  align-items: center;
  border-color: transparent;
}

.product{
    width: 92.5%;
    color:#0884f1;
    font-weight: 400;
    border-color: transparent;
    gap: 10px;
    justify-content: flex-start;
    display: flex;
    align-items: center;
}
.date-of-creation {
    justify-content: center;
    display: flex;
    align-items: center;
}
.actions {
    justify-content: center;
    display: flex;
    align-items: center;
    width: 15%;
    white-space: nowrap;
    margin-left: 20px;
}
.product-img{
    border-radius: 5px;
    border: 1px solid rgb(148, 148, 148);
    padding: 2px;
    width: 50px;
    height: 50px;
    background-size: cover; 
    background-position: top;
    background-repeat: no-repeat;
}
.header-row {
    font-weight: bold;
    background-color: #f4f4f4;
}
.sell{
    width: 10%;
}
.type{
    width: 15%;
}
.trademark{
    width: 15%;
}
.date-of-creation{
    width: 20%;
}
</style>