<script setup>
import Mainpage from '../../components/Mainpage.vue';
import TheHeader from '../../components/TheHeader.vue';
import Toast from '../../components/Toast.vue';
const toastRef = ref();
import { ref, computed, onMounted } from 'vue';
import axios from 'axios';

const customers = ref([]);
const searchKeyword = ref('');
const filterPoints = ref('');
const filtermembership_type = ref('');
const showConfirmModal = ref(null);
const dropdowns = ref({
  points: false,
  membership_type: false,
});
const showEditCustomerForm = ref(false);
const isEditMode = ref(false);
const editCustomer = ref({});

const fetchCustomers = async () => {
  try {
    const response = await axios.get('http://localhost:8083/api/customers');
    customers.value = response.data;
  } catch (error) {
    console.error('Lỗi khi tải dữ liệu khách hàng:', error);
  }
};

const openEditForm = (customer) => {
  editCustomer.value = { ...customer }; // sao chép dữ liệu khách hàng vào editCustomer
  isEditMode.value = true;
  showEditCustomerForm.value = true;
};

const editCustomerById = async () => {
  try {
    const response = await axios.patch(
      `http://localhost:8083/api/customers/${editCustomer.value.customer_id}`,
      editCustomer.value
    );
    const index = customers.value.findIndex(
      (customer) => customer.customer_id === editCustomer.value.customer_id
    );
    if (index != -1) {
      customers.value[index] = response.data;
    }
    // alert("Cập nhật thành công");
    toastRef.value.show('Cập nhật thành công!', 'success');
    showEditCustomerForm.value = false;
  } catch (error) {
    console.error('Lỗi khi cập nhật khách hàng:', error);
    // alert('Lỗi khi cập nhật khách hàng');
    toastRef.value.show('Lỗi khi cập nhật khách hàng', 'error');
  }
};

const deleteCustomer = async (customerId) => {
  showConfirmModal.value = customerId;
};

// Handle delete confirmation
const confirmDelete = async () => {
  if (showConfirmModal.value !== null) {
    const customerId = showConfirmModal.value;
    try {
      await deleteCustomerById(customerId);
      showConfirmModal.value = null; // Close modal after delete
    } catch (error) {
      console.error('Lỗi khi xóa khách hàng:', error);
      // alert('Lỗi khi xóa khách hàng');
      toastRef.value.show('Lỗi khi xóa khách hàng', 'error');
    }
  }
};

const deleteCustomerById = async (customerId) => {
  try {
    await axios.delete(`http://localhost:8083/api/customers/${customerId}`);
    customers.value = customers.value.filter((customer) => customer.customer_id !== customerId);
    // alert('Xóa khách hàng thành công!');
    toastRef.value.show('Xóa khách hàng thành công!', 'success');
  } catch (error) {
    console.error('Lỗi khi xóa khách hàng:', error);
    // alert('Lỗi khi xóa khách hàng');
    toastRef.value.show('Lỗi khi xóa khách hàng', 'error');
  }
};
// Cancel delete action and close the modal
const cancelDelete = () => {
  showConfirmModal.value = null;
};

function Dropdown(type) {
  dropdowns.value[type] = !dropdowns.value[type];
  for (let key in dropdowns.value) {
    if (key !== type) dropdowns.value[key] = false;
  }
}

function filterByPoints(range) {
  filterPoints.value = range;
  dropdowns.value.points = false;
}

function filterBymembership_type(membership_type) {
  filtermembership_type.value = membership_type;
  dropdowns.value.membership_type = false;
}

function getPointRange(range, points) {
  switch (range) {
    case '0-500':
      return points >= 0 && points <= 500;
    case '501-1000':
      return points > 500 && points <= 1000;
    case '1001-2000':
      return points > 1000 && points <= 2000;
    case '2001-5000':
      return points > 2000 && points <= 5000;
    case '5001-10000':
      return points > 5000 && points <= 10000;
    case 'Trên 10000':
      return points > 10000;
    default:
      return true;
  }
}

const filteredCustomers = computed(() => {
  return customers.value.filter((c) => {
    const matchName = c.name.toLowerCase().includes(searchKeyword.value.toLowerCase());
    const matchPoints = filterPoints.value ? getPointRange(filterPoints.value, c.points) : true;
    const matchmembership_type = filtermembership_type.value
      ? c.membership_type === filtermembership_type.value
      : true;
    return matchName && matchPoints && matchmembership_type;
  });
});
const isOverlayActive = computed(
  () => showEditCustomerForm.value || showConfirmModal.value !== null
);

function resetFilters() {
  filterPoints.value = '';
  filtermembership_type.value = '';
  searchKeyword.value = '';
}
onMounted(() => {
  fetchCustomers();
});
</script>

<template>
  <div class="temp">
    <Mainpage />
    <div class="main-right">
      <TheHeader />
      <div class="overlay" v-if="showEditCustomerForm || showConfirmModal"></div>
      <div class="container">
        <div class="white-container">
          <!-- <div class="overlay" v-if="showEditCustomerForm || showConfirmModal"></div> -->
          <div class="header2">
            <div class="search-name">
              <font-awesome-icon class="icon" :icon="['fas', 'magnifying-glass']" />
              <input
                v-model="searchKeyword"
                type="text"
                class="search-box"
                name="search"
                placeholder="Tìm kiếm khách hàng theo tên"
              />
            </div>
            <div class="list-container">
              <div class="dropdown-container">
                <button class="list-box" @click="Dropdown('points')">
                  Điểm tích lũy
                  <font-awesome-icon :icon="['fas', 'sort-down']" />
                </button>
                <div v-if="dropdowns.points" class="dropdown-menu">
                  <div @click="filterByPoints('0-500')">0 - 500</div>
                  <div @click="filterByPoints('501-1000')">501 - 1000</div>
                  <div @click="filterByPoints('1001-2000')">1001 - 2000</div>
                  <div @click="filterByPoints('2001-5000')">2001 - 5000</div>
                  <div @click="filterByPoints('5001-10000')">5001 - 1000</div>
                  <div @click="filterByPoints('Trên 10000')">Trên 10000</div>
                </div>
              </div>
              <div class="dropdown-container">
                <button class="list-box" @click="Dropdown('membership_type')">
                  Loại thành viên
                  <font-awesome-icon :icon="['fas', 'sort-down']" />
                </button>
                <div v-if="dropdowns.membership_type" class="dropdown-menu">
                  <div @click="filterBymembership_type('Kim cương')">Kim cương</div>
                  <!-- trên 1000 -->
                  <div @click="filterBymembership_type('Vàng')">Vàng</div>
                  <!-- //trên 500 -->
                  <div @click="filterBymembership_type('Bạc')">Bạc</div>
                  <!-- // trên 200 -->
                  <div @click="filterBymembership_type('Đồng')">Đồng</div>
                  <div @click="filterBymembership_type('Chưa có hạng')">Chưa có hạng</div>
                </div>
              </div>
              <button
                class="list-box"
                style="border-color: transparent; border-radius: 5px; width: 50%"
                @click="resetFilters"
              >
                Xóa lọc
              </button>
            </div>
          </div>
          <table class="custom-table">
            <thead>
              <tr>
                <th>Mã KH</th>
                <th>Họ tên</th>
                <th>SĐT</th>
                <th>Ngày sinh</th>
                <th>Điểm tích lũy</th>
                <th>Loại thành viên</th>
                <th>Thao tác</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(customer, index) in filteredCustomers" :key="customer.customer_id">
                <td>{{ customer.customer_id }}</td>
                <td>{{ customer.name }}</td>
                <td>{{ customer.phone }}</td>
                <td>{{ customer.date_of_birth }}</td>
                <td>{{ customer.points }}</td>
                <td>{{ customer.membership_type }}</td>
                <td>
                  <button class="btn-edit" @click="openEditForm(customer)">Sửa</button>
                  <button class="btn-delete" @click="deleteCustomer(customer.customer_id)">
                    Xóa
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
          <div v-if="showConfirmModal !== null" class="modal-overlay">
            <div class="modal-content">
              <h3>Bạn có chắc chắn muốn xóa khách hàng này?</h3>
              <button @click="confirmDelete" class="btn btn-confirm">Có</button>
              <button @click="cancelDelete" class="btn btn-cancel">Không</button>
            </div>
          </div>

          <!-- form edit -->
          <div v-if="showEditCustomerForm" class="edit-customer-form">
            <h4>Chỉnh sửa thông tin khách hàng</h4>
            <form @submit.prevent="editCustomerById">
              <div>
                <label for="customer_id">Id:</label>
                <input v-model="editCustomer.customer_id" type="text" id="customer_id" disabled />
              </div>
              <div>
                <label for="name">Tên khách hàng:</label>
                <input v-model="editCustomer.name" type="text" id="name" required />
              </div>
              <div>
                <label for="phone">Số điện thoại:</label>
                <input v-model="editCustomer.phone" type="text" id="phone" disabled />
              </div>
              <div>
                <label for="date_of_birth">Ngày sinh:</label>
                <input
                  v-model="editCustomer.date_of_birth"
                  type="date"
                  id="date_of_birth"
                  required
                />
              </div>
              <div>
                <label for="points">Điểm tích lũy:</label>
                <input v-model="editCustomer.points" type="number" id="points" disabled />
              </div>
              <div>
                <label for="membership_type">Loại thành viên:</label>
                <select v-model="editCustomer.membership_type" disabled>
                  <option value="Kim cương">Kim cương</option>
                  <option value="Vàng">Vàng</option>
                  <option value="Bạc">Bạc</option>
                  <option value="Đồng">Đồng</option>
                  <option value="Chưa có hạng">Chưa có hạng</option>
                </select>
              </div>
              <div class="button-group">
                <button type="submit">Lưu</button>
                <button type="button" @click="showEditCustomerForm = false">Hủy</button>
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
.edit-customer-form {
  position: fixed;
  top: 50%;
  left: 60%;
  transform: translate(-50%, -50%);
  background: #f9f9f9;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.3);
  z-index: 1000;
  width: 50%;
  box-sizing: border-box;
}

.edit-customer-form input,
.edit-customer-form select {
  width: 100%;
  padding: 8px;
  margin: 10px 0;
  border: 1px solid #ccc;
  border-radius: 5px;
}

.edit-customer-form button {
  background-color: #4caf50;
  color: white;
  padding: 10px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  margin-top: 10px;
}

.edit-customer-form button[type='button'] {
  background-color: #f44336; /* Nút hủy */
}

.edit-customer-form button:hover {
  background-color: #45a049;
}

.edit-customer-form button[type='button']:hover {
  background-color: #e53935;
}

.white-container {
  background-color: white;
  width: 100%;
  height: 100vh;
  border-radius: 5px;
  box-shadow: 5px 10px 15px 0px rgba(0, 0, 0, 0.2);
  padding: 10px;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  gap: 20px;
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

.search-name {
  display: flex;
  width: 40%;
  background-color: white;
  padding: 5px 10px;
  border-color: #828282;
  border-style: solid;
  border-width: 1px;
  border-radius: 5px;
  align-items: center;
}

.search-box {
  width: 100%;
  font-size: 15px;
  outline: none;
  border-color: transparent;
  background-color: white;
}

.list-container {
  display: flex;
  width: 50%;
  background-color: white;
  /* padding: 5px 10px; */
  border-color: #828282;
  border-style: solid;
  border-width: 1px;
  border-radius: 5px;
  align-items: center;
}

.list-box {
  width: 100%;
  font-size: 15px;
  outline: none;
  border-width: 1px;
  padding: 5px 10px;
  border-color: transparent #828282 transparent transparent;
  background-color: white;
  border-radius: 5px 0 0 5px;
  display: flex;
  justify-content: space-around;
  align-items: center;
}

.icon {
  font-size: 15px;
  color: #828282;
}

.custom-table {
  width: 100%;
  border: 1px solid #d1d5db;
  border-collapse: collapse;
  font-size: 16px;
}

.custom-table thead {
  background-color: #f3f4f6;
}

.custom-table th,
.custom-table td {
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

.modal-content {
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
