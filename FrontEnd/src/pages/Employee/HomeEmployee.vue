<script setup>
import Mainpage from '../../components/Mainpage.vue'
import TheHeader from '../../components/TheHeader.vue';
import Toast from '../../components/Toast.vue';
const toastRef = ref();
import { ref, computed, onMounted} from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';

const router = useRouter();
const employees = ref([]);
const searchKeyword = ref('');
const filterPosition = ref('');
const showConfirmModal = ref(null); 
const dropdowns = ref({
  position: false
});

const showAddEmployeeForm = ref(false); 
const showEditEmployeeForm = ref(false);
const newEmployee = ref({
  name: '',
  phone: '',
  date_of_birth: '',
  position: '',
  user_name: '',
  password: '',
  salary: 0
});
const isEditMode = ref(false);
const editEmployee = ref({}); 

const fetchEmployees = async () => {
  try {
    const response = await axios.get('http://localhost:8083/api/employees');
    employees.value = response.data;
  } catch (error) {
    console.error('Lỗi khi tải dữ liệu nhân viên :', error);
  }
};

const addEmployee = async () => {
  try {
    const response = await axios.post('http://localhost:8083/api/employees', newEmployee.value);
    employees.value.push(response.data);  // Thêm nhân viên  vào danh sách hiện tại
    // alert('Thêm nhân viên  thành công!');
    toastRef.value.show('Thêm nhân viên thành công!', 'success');
    showAddEmployeeForm.value = false;  // Đóng form sau khi thêm thành công
    resetNewEmployee();  // Xóa form
  } catch (error) {
    console.error('Lỗi khi thêm nhân viên :', error);
    // alert('Lỗi khi thêm nhân viên '); 
    toastRef.value.show('Lỗi khi thêm nhân viên!', 'error');
  }
};

const resetNewEmployee = () => {
  newEmployee.value = {
    name: '',
    phone: '',
    date_of_birth: '',
    position: '',
    user_name: '',
    password: '',
    salary: 0
  };
};

const openEditForm = (employee) => {
  editEmployee.value = { ...employee }; // sao chép dữ liệu nhân viên  vào editemployee
  isEditMode.value = true;
  showEditEmployeeForm.value = true;
};
const validatePhone = (e) => {
  const value = e.target.value;
  if (/[a-zA-Z]/.test(value)) {
    toastRef.value.show("Số điện thoại không được chứa chữ cái!", "warning");
    newSupplier.value.phone = '';
    return;
  }
  newSupplier.value.phone = value.replace(/\D/g, '');
};

const editEmployeeById = async () => {
  try{
    const response = await axios.patch(`http://localhost:8083/api/employees/${editEmployee.value.employee_id}`, editEmployee.value);
    const index = employees.value.findIndex(employee => employee.employee_id === editEmployee.value.employee_id);
    if (index !=-1){
      employees.value[index] = response.data;
    }
    // alert("Cập nhật thành công");
    toastRef.value.show('Cập nhật nhân viên thành công!', 'success');
    showEditEmployeeForm.value = false;
  }catch (error){
    console.error('Lỗi khi cập nhật nhân viên:', error);
    // alert('Lỗi khi cập nhật nhân viên');
    toastRef.value.show('Lỗi khi cập nhật nhân viên!', 'error');
  }
};

const deleteEmployee = async (employeeId) => {
  showConfirmModal.value = employeeId;
};

// Handle delete confirmation
const confirmDelete = async () => {
  if (showConfirmModal.value !== null) {
    const employeeId = showConfirmModal.value;
    try {
      await deleteEmployeeById(employeeId);
      showConfirmModal.value = null;  // Close modal after delete
    } catch (error) {
      console.error('Lỗi khi xóa nhân viên:', error);
      // alert('Lỗi khi xóa nhân viên');
      toastRef.value.show('Lỗi khi xóa nhân viên!', 'error');
    }
  }
};

const deleteEmployeeById = async (employeeId) => {
  try {
    await axios.delete(`http://localhost:8083/api/employees/${employeeId}`);
    employees.value = employees.value.filter(employee => employee.employee_id !== employeeId);
    // alert('Xóa nhân viên  thành công!');
    toastRef.value.show('Xóa nhân viên thành công!', 'success');
  } catch (error) {
    console.error('Lỗi khi xóa nhân viên:', error);
    // alert('Lỗi khi xóa nhân viên');
    toastRef.value.show('Lỗi khi xóa nhân viên!', 'error');
  }
};
// Cancel delete action and close the modal
const cancelDelete = () => {
  showConfirmModal.value = null;  // Close modal without deleting
};

function Dropdown(type) {
  dropdowns.value[type] = !dropdowns.value[type];
  for (let key in dropdowns.value) {
    if (key !== type) dropdowns.value[key] = false;
  }
}

function filterByPosition(position) {
  filterPosition.value = position;
  dropdowns.value.position = false;
}

const filteredEmployees = computed(() => {
  return employees.value.filter(e => {
    const matchName = e.name.toLowerCase().includes(searchKeyword.value.toLowerCase());
    const matchPosition = filterPosition.value ? e.position === filterPosition.value : true;
    return matchName && matchPosition;
  });
});

function resetFilters() {
  // filterPoints.value = '';
  filterPosition.value = '';
  searchKeyword.value = '';
}


const viewDetail = (employee_id) =>{
  router.push({
    path: '/detailEmployee',
    query: {employee_id: employee_id}
  });
};
onMounted(() => {
  fetchEmployees();
});

</script>


<template>
    <div class="temp">
        <Mainpage />
        <div class="main-right">
            <TheHeader />
              <div class="overlay" v-if="showEditEmployeeForm || showAddEmployeeForm || showConfirmModal" @click="showEditEmployeeForm = false"></div>
            <!-- <BackButton /> -->
            <div class="container">
              
                <div class="header2">
                    <h3>Nhân viên </h3>
                    <button class="add-button" @click="showAddEmployeeForm = true">
                        <font-awesome-icon :icon="['fas', 'circle-plus']" />
                        Thêm nhân viên 
                    </button>
                </div>

                <!-- form add -->
                <div v-if="showAddEmployeeForm" class="add-employee-form">
                  <h4>Thêm nhân viên mới</h4>
                  <form @submit.prevent="addEmployee">
                    <div>
                      <label for="name">Tên nhân viên :</label>
                      <input v-model="newEmployee.name" type="text" id="name" required />
                    </div>
                    <div>
                      <label for="phone">Số điện thoại:</label>
                      <input
                        v-model="newEmployee.phone"
                        type="text"
                        id="phone"
                        required
                        placeholder="Nhập số điện thoại nhân viên"
                        maxlength="10"
                        @input="validatePhone"
                      />

                    </div>
                    <div>
                      <label for="date_of_birth">Ngày sinh:</label>
                      <input v-model="newEmployee.date_of_birth" type="date" id="date_of_birth" required />
                    </div>
                    <div>
                      <label for="position">Chức vụ:</label>
                      <select v-model="newEmployee.position" required>
                        <option value="Quản lý">Quản lý</option>
                        <option value="Nhân viên">Nhân viên</option>
                        <!-- <option value="Thu ngân">Thu ngân</option>
                        <option value="Quét dọn">Quét dọn</option>
                        <option value="Bảo vệ">Bảo vệ</option> -->
                      </select>
                    </div>
                    <div>
                      <label for="user_name">Tên đăng nhập:</label>
                      <input v-model="newEmployee.user_name" type="text" id="user_name" required />
                    </div>
                    <div>
                      <label for="password">Mật khẩu:</label>
                      <input v-model="newEmployee.password" type="text" id="password" required />
                    </div>
                    <div>
                      <label for="salary">Lương:</label>
                      <input v-model="newEmployee.salary" type="number" id="salary" required />
                    </div>
                    <div class="button-group">
                      <button type="submit">Lưu</button>
                      <button type="button" @click="showAddEmployeeForm = false">Hủy</button>
                    </div>
                  </form>
                </div>

                <div class="white-container">
                    <div class="header2">
                        <div class="search-name">
                            <font-awesome-icon class="icon" :icon="['fas', 'magnifying-glass']" />
                            <input v-model="searchKeyword" type="text" class="search-box" name="search" placeholder="Tìm kiếm nhân viên theo tên">
                        </div>
                        <div class="list-container">
                            <div class="dropdown-container">
                                <button class="list-box" @click="Dropdown('position')">
                                    Chức vụ 
                                    <font-awesome-icon :icon="['fas', 'sort-down']" />
                                </button>
                                <div v-if="dropdowns.position" class="dropdown-menu">
                                  <div @click="filterByPosition('Quản lý')">Quản lý</div> 
                                  <div @click="filterByPosition('Thu ngân')">Thu ngân</div> 
                                  <div @click="filterByPosition('Quét dọn')">Quét dọn</div> 
                                  <div @click="filterByPosition('Bảo vệ')">Bảo vệ</div>  
                                </div>
                            </div>
                            <button class="list-box" style="border-color:transparent;border-radius: 5px;width: 50%;" @click="resetFilters">Xóa lọc</button>
                        </div>
                    </div>
                    <table class="employee-table">
                        <thead>
                            <tr>
                            <th>Mã NV</th>
                            <th>Họ tên</th>
                            <th>SĐT</th>
                            <th>Ngày sinh</th>
                            <!-- <th></th> -->
                            <th>Chức vụ</th>
                            <th>Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr v-for="(employee, index) in filteredEmployees" :key="employee.employee_id">
                            <td>{{ employee.employee_id }}</td>
                            <td>{{ employee.name }}</td>
                            <td>{{ employee.phone }}</td>
                            <td>{{ employee.date_of_birth }}</td>
                            <!-- <td>{{ employee. }}</td> -->
                            <td>{{ employee.position }}</td>
                            <td>
                                <button class="btn-edit" @click="openEditForm(employee)">Sửa</button>
                                <button class="btn-delete" @click="deleteEmployee(employee.employee_id)">Xóa</button>
                                <button class="btn-view" @click="viewDetail(employee.employee_id)">Xem</button>
                            </td>
                            </tr>
                        </tbody>
                        
                    </table>
                    <div v-if="showConfirmModal !== null" class="modal-overlay">
                      <div class="modal-content">
                        <h3>Bạn có chắc chắn muốn xóa nhân viên này?</h3>
                        <button @click="confirmDelete" class="btn btn-confirm">Có</button>
                        <button @click="cancelDelete" class="btn btn-cancel">Không</button>
                      </div>
                    </div>


                    <!-- form edit -->
                    <div v-if="showEditEmployeeForm" class="add-employee-form">
                    <h4>Chỉnh sửa thông tin nhân viên </h4>
                    <form @submit.prevent="editEmployeeById">
                      <div>
                        <label for="employee_id">Id:</label>
                        <input v-model="editEmployee.employee_id" type="text" id="employee_id" disabled />
                      </div>
                      <div>
                        <label for="name">Tên nhân viên :</label>
                        <input v-model="editEmployee.name" type="text" id="name" required />
                      </div>
                      <div>
                        <label for="phone">Số điện thoại:</label>
                        <input v-model="editEmployee.phone" type="text" id="phone" disabled />
                      </div>
                      <div>
                        <label for="date_of_birth">Ngày sinh:</label>
                        <input v-model="editEmployee.date_of_birth" type="date" id="date_of_birth" required />
                      </div>
                      <div>
                        <label for="position">Chức vụ:</label>
                        <select v-model="editEmployee.position" required>
                          <option value="Quản lý">Quản lý</option>
                          <option value = "Nhân viên">Nhân viên</option>
                          <!-- <option value="Thu ngân">Thu ngân</option>
                          <option value="Quét dọn">Quét dọn</option>
                          <option value="Bảo vệ">Bảo vệ</option> -->
                        </select>
                      </div>
                      <div>
                        <label for="user_name">Tên tài khoản:</label>
                        <input v-model="editEmployee.user_name" type="text" id="user_name" required />
                      </div>
                      <div>
                        <label for="password">Mật khẩu:</label>
                        <input v-model="editEmployee.password" type="text" id="password" required />
                      </div>
                      <div>
                        <label for="salary">Lương:</label>
                        <input v-model="editEmployee.salary" type="number" id="salary" required />
                      </div>
                      <div class="button-group">
                        <button type="submit">Lưu</button>
                        <button type="button" @click="showEditEmployeeForm = false">Hủy</button>
                      </div>
                    </form>
                  </div>
                </div>
            </div>
        </div>
    </div>
    <Toast ref="toastRef" />
</template>

<style scoped>
body{
    width: 100%;
    height:100%;
    /* font-size: 20px;
    font-family:'Noto Sans', sans-serif; */
}

.temp{
    position:absolute;
    width: 100%;
    height:100%;
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
    /* height: auto; */
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
.header2 h3{
    font-size: 24px;
    font-weight: 600;
    color:rgb(50, 50, 50);
}

.overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.8); 
  backdrop-filter: blur(2px);
  z-index: 1000;
}

/* form */
.add-employee-form {
  position: fixed;
  top: 50%;
  left: 60%;
  transform: translate(-50%, -50%);
  max-height: 90vh;
  overflow-y: auto; 
  background: #f9f9f9;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.3);
  z-index: 1000; 
  width: 50%;
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
    height:auto;
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

.modal-content{
  position: fixed;
  top: 50%;
  left: 60%;
  transform: translate(-50%, -50%);
  max-height: 90vh;
  overflow-y: auto; 
  background: #f9f9f9;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.3);
  z-index: 1000; 
  width: 50%;
  box-sizing: border-box;
}

.btn {
  padding: 10px 20px;
  margin: 0 10px;
  border: none;
  border-radius: 5px;
  color: white;
  font-weight: bold;
  cursor: pointer;
}

.btn-confirm {
  background-color: #28a745; /* xanh lá */
}

.btn-cancel {
  background-color: #dc3545; /* đỏ */
}
</style>