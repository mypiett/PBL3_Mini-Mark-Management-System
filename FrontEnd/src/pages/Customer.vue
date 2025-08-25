<script setup>
import Mainpage from '../components/Mainpage.vue';
import Taskbar from '../components/Taskbar.vue';
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

const showAddCustomerForm = ref(false); // Trạng thái hiển thị form
const showEditCustomerForm = ref(false);
const newCustomer = ref({
  // customer_id: '',
  name: '',
  phone: '',
  date_of_birth: '',
  points: 0,
  // membership_type: ''
});
const isEditMode = ref(false);
const editCustomer = ref({}); // ban đầu rỗng

const fetchCustomers = async () => {
  try {
    const response = await axios.get('http://localhost:8082/api/customers');
    customers.value = response.data;
  } catch (error) {
    console.error('Lỗi khi tải dữ liệu khách hàng:', error);
  }
};

const addCustomer = async () => {
  try {
    const response = await axios.post('http://localhost:8082/api/customers', newCustomer.value);
    customers.value.push(response.data); // Thêm khách hàng vào danh sách hiện tại
    alert('Thêm khách hàng thành công!');
    showAddCustomerForm.value = false; // Đóng form sau khi thêm thành công
    resetNewCustomer(); // Xóa form
  } catch (error) {
    console.error('Lỗi khi thêm khách hàng:', error);
    alert('Lỗi khi thêm khách hàng');
  }
};

const resetNewCustomer = () => {
  newCustomer.value = {
    // customer_id: '',
    name: '',
    phone: '',
    date_of_birth: '',
    points: 0,
    // membership_type: ''
  };
};

const openEditForm = (customer) => {
  editCustomer.value = { ...customer }; // sao chép dữ liệu khách hàng vào editCustomer
  isEditMode.value = true;
  showEditCustomerForm.value = true;
};

const editCustomerById = async () => {
  try {
    const response = await axios.patch(
      `http://localhost:8082/api/customers/${editCustomer.value.customer_id}`,
      editCustomer.value
    );
    const index = customers.value.findIndex(
      (customer) => customer.customer_id === editCustomer.value.customer_id
    );
    if (index != -1) {
      customers.value[index] = response.data;
    }
    alert('Cập nhật thành công');
    showEditCustomerForm.value = false;
  } catch (error) {
    console.error('Lỗi khi cập nhật khách hàng:', error);
    alert('Lỗi khi cập nhật khách hàng');
  }
};

const deleteCustomer = async (customerId) => {
  showConfirmModal.value = customerId;
};

// Handle delete confirmation
const confirmDelete = async (customerId) => {
  if (showConfirmModal.value !== null) {
    const customerId = showConfirmModal.value;
    try {
      await deleteCustomerById(customerId);
      showConfirmModal.value = null; // Close modal after delete
    } catch (error) {
      console.error('Lỗi khi xóa khách hàng:', error);
      alert('Lỗi khi xóa khách hàng');
    }
  }
};

const deleteCustomerById = async (customerId) => {
  try {
    await axios.delete(`http://localhost:8082/api/customers/${customerId}`);
    customers.value = customers.value.filter((customer) => customer.customer_id !== customerId);
    alert('Xóa khách hàng thành công!');
  } catch (error) {
    console.error('Lỗi khi xóa khách hàng:', error);
    alert('Lỗi khi xóa khách hàng');
  }
};
// Cancel delete action and close the modal
const cancelDelete = () => {
  showConfirmModal.value = null; // Close modal without deleting
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

// function filterBycustomer_group(customer_group) {
//   filtercustomer_group.value = customer_group;
//   dropdowns.value.customer_group = false;
// }

function getPointRange(range, points) {
  switch (range) {
    case '0-200':
      return points >= 0 && points <= 200;
    case '201-500':
      return points > 200 && points <= 500;
    case 'Trên 500':
      return points > 500;
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
      <Taskbar />
      <div class="container">
        <div class="header2">
          <h3>Khách hàng</h3>
          <button class="add-button" @click="showAddCustomerForm = true">
            <font-awesome-icon :icon="['fas', 'circle-plus']" />
            Thêm khách hàng
          </button>
        </div>

        <!-- form add -->
        <div v-if="showAddCustomerForm" class="add-customer-form">
          <h4>Thêm khách hàng mới</h4>
          <form @submit.prevent="addCustomer">
            <div>
              <label for="name">Tên khách hàng:</label>
              <input v-model="newCustomer.name" type="text" id="name" required />
            </div>
            <div>
              <label for="phone">Số điện thoại:</label>
              <input v-model="newCustomer.phone" type="text" id="phone" required />
            </div>
            <div>
              <label for="date_of_birth">Ngày sinh:</label>
              <input v-model="newCustomer.date_of_birth" type="date" id="date_of_birth" required />
            </div>
            <div>
              <label for="points">Điểm tích lũy:</label>
              <input v-model="newCustomer.points" type="number" id="points" required />
            </div>
            <!-- <div>
                      <label for="membership_type">Loại thành viên:</label>
                      <select v-model="newCustomer.membership_type" required>
                        <option value="Kim cương">Kim cương</option>
                        <option value="Vàng">Vàng</option>
                        <option value="Bạc">Bạc</option>
                        <option value="Đồng">Đồng</option>
                      </select>
                    </div> -->
            <button type="submit">Lưu</button>
            <button type="button" @click="showAddCustomerForm = false">Hủy</button>
          </form>
        </div>

        <div class="white-container">
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
                  <div @click="filterByPoints('0-200')">0 - 200</div>
                  <div @click="filterByPoints('201-500')">201 - 500</div>
                  <div @click="filterByPoints('Trên 500')">Trên 500</div>
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
                <th>STT</th>
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
              <button @click="confirmDelete">Có</button>
              <button @click="cancelDelete">Không</button>
            </div>
          </div>

          <!-- form edit -->
          <div v-if="showEditCustomerForm" class="add-customer-form">
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
                </select>
              </div>
              <button type="submit">Lưu</button>
              <button type="button" @click="showEditCustomerForm = false">Hủy</button>
            </form>
          </div>
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
</style>
