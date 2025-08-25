<script setup>
import Mainpage from '../../components/Mainpage.vue';
// import Taskbar from '../../components/Taskbar.vue'
import TheHeader from '../../components/TheHeader.vue';
import { ref, computed, onMounted } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';

const router = useRouter();
const searchKeyword = ref('');
const imports = ref([]);
const filterStatus = ref('');
const filterTime = ref('');
const dropdowns = ref({
  status: false,
  time: false,
});

function Dropdown(type) {
  dropdowns.value[type] = !dropdowns.value[type];
  for (let key in dropdowns.value) {
    if (key !== type) dropdowns.value[key] = false;
  }
}

function filterByStatus(status) {
  filterStatus.value = status;
  dropdowns.value.status = false;
}

function filterByTime(time) {
  filterTime.value = time;
  dropdowns.value.time = false;
}

function matchTimeFilter(dateStr, filter) {
  const today = new Date();
  const date = new Date(dateStr);

  switch (filter) {
    case 'Hôm nay':
      return (
        date.getDate() === today.getDate() &&
        date.getMonth() === today.getMonth() &&
        date.getFullYear() === today.getFullYear()
      );
    case 'Tuần này': {
      const startOfWeek = new Date(today);
      startOfWeek.setDate(today.getDate() - today.getDay());
      const endOfWeek = new Date(startOfWeek);
      endOfWeek.setDate(startOfWeek.getDate() + 6);
      return date >= startOfWeek && date <= endOfWeek;
    }
    case 'Tháng này':
      return date.getMonth() === today.getMonth() && date.getFullYear() === today.getFullYear();
    default:
      return true;
  }
}

const filteredImports = computed(() => {
  return imports.value.filter((imp) => {
    const idStr = imp.importId ? imp.importId.toString().toLowerCase() : '';
    const searchStr = searchKeyword.value.toLowerCase();
    const matchId = idStr.includes(searchStr);
    const matchStatus = filterStatus.value ? imp.status === filterStatus.value : true;
    const matchTime = filterTime.value ? matchTimeFilter(imp.import_date, filterTime.value) : true;
    return matchId && matchStatus && matchTime;
  });
});

function resetFilters() {
  filterStatus.value = '';
  filterTime.value = '';
  searchKeyword.value = '';
}

function formatDate(dateStr) {
  const date = new Date(dateStr);
  return date.toLocaleString('vi-VN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  });
}

function viewImportDetail(importId) {
  router.push(`/invoiceImportIndex/${importId}`);
}

onMounted(async () => {
  try {
    const res = await axios.get('http://localhost:8083/StockImport'); // URL API backend
    imports.value = res.data.map((item) => ({
      importId: item.importId || item.import_id,
      import_date: item.importDate || item.import_date,
      status: item.status,
      total_cost: item.totalCost || item.total_cost,
      employee_id: item.employeeId || item.employee_id,
    }));
  } catch (error) {
    console.error('Lỗi khi tải hóa đơn nhập hàng:', error);
  }
});
</script>

<template>
  <div class="temp">
    <Mainpage />
    <div class="main-right">
      <TheHeader />
      <div class="container">
        <div class="header2">
          <h3>Danh sách hóa đơn nhập hàng</h3>
        </div>

        <div class="white-container">
          <div class="header2">
            <div class="search-name">
              <font-awesome-icon class="icon" :icon="['fas', 'magnifying-glass']" />
              <input
                v-model="searchKeyword"
                type="text"
                class="search-box"
                placeholder="Tìm kiếm theo mã hóa đơn"
              />
            </div>

            <div class="list-container">
              <div class="dropdown-container">
                <button class="list-box" @click="Dropdown('time')">
                  Thời gian
                  <font-awesome-icon :icon="['fas', 'sort-down']" />
                </button>
                <div v-if="dropdowns.time" class="dropdown-menu">
                  <div @click="filterByTime('Hôm nay')">Hôm nay</div>
                  <div @click="filterByTime('Tuần này')">Tuần này</div>
                  <div @click="filterByTime('Tháng này')">Tháng này</div>
                </div>
              </div>

              <!-- <div class="dropdown-container">
                <button class="list-box" @click="Dropdown('status')">
                  Trạng thái
                  <font-awesome-icon :icon="['fas', 'sort-down']" />
                </button>
                <div v-if="dropdowns.status" class="dropdown-menu">
                  <div @click="filterByStatus('ACTIVE')">Đã duyệt</div>
                  <div @click="filterByStatus('DRAFT')">Chờ duyệt</div>
                  <div @click="filterByStatus('Đã hủy')">Đã hủy</div> 
                </div>
              </div> -->

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
                <th>Mã hóa đơn</th>
                <th>Ngày nhập</th>
                <!-- <th>Trạng thái</th> -->
                <th>Tổng chi phí</th>
                <th>Mã nhân viên</th>
                <th>Thao tác</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(imp, index) in filteredImports" :key="imp.import_id">
                <td>{{ index + 1 }}</td>
                <td>{{ imp.importId }}</td>
                <td>{{ formatDate(imp.import_date) }}</td>
                <!-- <td>{{ imp.status }}</td> -->
                <td>{{ imp.total_cost }}</td>
                <td>{{ imp.employee_id }}</td>
                <td>
                  <button @click="viewImportDetail(imp.importId)" class="btn-view">Xem</button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* Style tương tự như trang danh sách đơn hàng bạn đã gửi */
.temp {
  position: absolute;
  width: 100%;
  height: 100%;
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
  font-size: 20px;
}

.container {
  width: 96%;
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

.white-container {
  background-color: white;
  width: 100%;
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
  padding: 10px 20px;
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
  border: 1px solid #828282;
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
  border: 1px solid #828282;
  border-radius: 5px;
  align-items: center;
}

.list-box {
  width: 100%;
  font-size: 15px;
  outline: none;
  padding: 5px 10px;
  border-color: transparent;
  background-color: white;
  cursor: pointer;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.dropdown-container {
  position: relative;
  width: 33%;
}

.dropdown-menu {
  position: absolute;
  background-color: white;
  border: 1px solid #828282;
  width: 100%;
  z-index: 10;
  box-shadow: 3px 3px 10px rgba(0, 0, 0, 0.15);
  border-radius: 3px;
}

.dropdown-menu div {
  padding: 5px 10px;
  cursor: pointer;
  font-size: 15px;
}

.dropdown-menu div:hover {
  background-color: #eee;
}

.custom-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 16px;
}

.custom-table th,
.custom-table td {
  padding: 8px;
  border: 1px solid #ccc;
  text-align: center;
}

.btn-view {
  background-color: #0088ff;
  border: none;
  border-radius: 3px;
  padding: 5px 10px;
  color: white;
  cursor: pointer;
}

.icon {
  margin-right: 5px;
}
</style>
