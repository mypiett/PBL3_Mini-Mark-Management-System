<script setup>
import { ref, computed, onMounted } from 'vue';
import MainPage from '@/components/Mainpage.vue';
import RevenueChart from '@/components/RevenueChart.vue';
import axios from 'axios';

const invoices = ref([]);
const filterTime = ref('Tháng này');
const sortOrder = ref('Giảm dần');
const dropdowns = ref({ time: false, sort: false });
const fromDate = ref('');
const toDate = ref('');

function Dropdown(type) {
  dropdowns.value[type] = !dropdowns.value[type];
}

function filterByTime(time) {
  filterTime.value = time;
  dropdowns.value.time = false;
}

function matchTimeFilter(invoiceDate, filter) {
  const today = new Date();
  const date = new Date(invoiceDate);

  switch (filter) {
    case 'Hôm nay':
      return date.toDateString() === today.toDateString();
    case 'Tuần này': {
      const startOfWeek = new Date(today);
      startOfWeek.setDate(today.getDate() - today.getDay());
      const endOfWeek = new Date(startOfWeek);
      endOfWeek.setDate(startOfWeek.getDate() + 6);
      return date >= startOfWeek && date <= endOfWeek;
    }
    case 'Tháng này':
      return date.getMonth() === today.getMonth() && date.getFullYear() === today.getFullYear();
    case 'Tùy chọn':
      if (!fromDate.value || !toDate.value) return false;
      const start = new Date(fromDate.value);
      const end = new Date(toDate.value);
      end.setHours(23, 59, 59, 999);
      return date >= start && date <= end;
    default:
      return true;
  }
}

function sortByAmount(list) {
  if (sortOrder.value === 'Tăng dần') {
    return [...list].sort((a, b) => a.total - b.total);
  } else if (sortOrder.value === 'Giảm dần') {
    return [...list].sort((a, b) => b.total - a.total);
  } else {
    return [...list].sort((a, b) => a.id.localeCompare(b.id)); // mặc định theo mã đơn hàng
  }
}

const filteredInvoices = computed(() => {
  const from = fromDate.value;
  const to = toDate.value;

  const filtered = invoices.value.filter((inv) => matchTimeFilter(inv.date, filterTime.value));
  return sortByAmount(filtered);
});

const totalRevenue = computed(() =>
  filteredInvoices.value.reduce((sum, inv) => sum + inv.total, 0)
);
const totalOrders = computed(() => filteredInvoices.value.length);

function formatDate(dateStr) {
  const date = new Date(dateStr);
  return date.toLocaleDateString('vi-VN', { year: 'numeric', month: '2-digit', day: '2-digit' });
}
function sortBy(order) {
  sortOrder.value = order;
  dropdowns.value.sort = false;
}
function resetFilters() {
  filterTime.value = 'Tất cả';
  sortOrder.value = '';
  fromDate.value = '';
  toDate.value = '';
  dropdowns.value = { time: false, sort: false };
}

onMounted(async () => {
  try {
    const res = await axios.get('http://localhost:8083/api/orders');
    const orders = res.data;

    invoices.value = orders.map((order) => ({
      id: order.order_id,
      date: order.order_date,
      total: order.total_amount,
    }));
  } catch (error) {
    console.error('Lỗi khi tải đơn hàng:', error);
  }
});
</script>

<template>
  <div class="layout">
    <div class="sidebar">
      <MainPage />
    </div>
    <div class="content">
      <h2 class="title">📊 Báo cáo doanh thu</h2>
      <div class="report-summary">
        <div class="summary-box orders">
          <span class="summary-title">🧾 Đơn hàng</span>
          <span class="summary-value">{{ totalOrders }}</span>
        </div>
        <div class="summary-box revenue">
          <span class="summary-title">💰 Doanh thu</span>
          <span class="summary-value">{{ totalRevenue.toLocaleString('vi-VN') }} VNĐ</span>
        </div>
      </div>
      <div class="chart-wrapper">
        <RevenueChart />
      </div>
      <div class="filter-section">
        <button @click="Dropdown('time')" class="dropdown-btn">
          Thời gian: {{ filterTime }} <span>▼</span>
        </button>
        <div v-if="filterTime === 'Tùy chọn'" class="date-range">
          <label>Từ: <input type="date" v-model="fromDate" /></label>
          <label>Đến: <input type="date" v-model="toDate" /></label>
        </div>
        <div v-if="dropdowns.time" class="dropdown-menu">
          <div @click="filterByTime('Hôm nay')">Hôm nay</div>
          <div @click="filterByTime('Tuần này')">Tuần này</div>
          <div @click="filterByTime('Tháng này')">Tháng này</div>
          <div @click="filterByTime('Tùy chọn')">Tùy chọn</div>
          <div @click="filterByTime('Tất cả')">Tất cả</div>
        </div>

        <button @click="Dropdown('sort')" class="dropdown-btn ml-2">
          Sắp xếp: {{ sortOrder }} <span>▼</span>
        </button>
        <button
          @click="resetFilters"
          class="dropdown-btn ml-2"
          style="background-color: #fbe9e7; color: #d84315"
        >
          Xóa lọc
        </button>
        <div v-if="dropdowns.sort" class="dropdown-menu" style="left: 160px">
          <div @click="sortBy('Tăng dần')">Tăng dần</div>
          <div @click="sortBy('Giảm dần')">Giảm dần</div>
        </div>
      </div>
      <table class="report-table">
        <thead>
          <tr>
            <th>STT</th>
            <th>Mã đơn hàng</th>
            <th>Ngày đặt</th>
            <th>Tổng tiền (VNĐ)</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(inv, index) in filteredInvoices" :key="inv.id">
            <td>{{ index + 1 }}</td>
            <td>{{ inv.id }}</td>
            <td>{{ formatDate(inv.date) }}</td>
            <td>{{ inv.total.toLocaleString('vi-VN') }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<style scoped>
.layout {
  display: flex;
  font-family: 'Segoe UI', sans-serif;
  height: 100vh;
}

.sidebar {
  width: 18%;
  height: 100vh;
  background-color: #e6f4ff;
  border-right: 1px solid #cce0f4;
  /* padding: 10px; */
}

.content {
  width: 82%;
  padding: 30px;
  background-color: #f8fcff;
}

.title {
  font-size: 26px;
  color: #007acc;
  margin-bottom: 20px;
}

.filter-section {
  position: relative;
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.dropdown-btn {
  cursor: pointer;
  padding: 8px 14px;
  border: 1px solid #90c8f0;
  border-radius: 5px;
  background-color: #e6f4ff;
  color: #007acc;
  font-weight: bold;
  margin-right: 10px;
}

.ml-2 {
  margin-left: 10px;
}

.dropdown-menu {
  position: absolute;
  top: 38px;
  background: white;
  border: 1px solid #cce0f4;
  width: 150px;
  z-index: 10;
  border-radius: 5px;
  box-shadow: 0 3px 8px rgba(0, 122, 204, 0.1);
}
.dropdown-menu div {
  padding: 10px 14px;
  cursor: pointer;
  color: #007acc;
}
.dropdown-menu div:hover {
  background-color: #e6f4ff;
}
.date-range {
  display: flex;
  gap: 20px;
  margin: 10px 0 20px;
}

.date-range label {
  font-weight: bold;
  color: #007acc;
}
.date-range input[type='date'] {
  padding: 5px 8px;
  border: 1px solid #90c8f0;
  border-radius: 5px;
}

.report-summary {
  display: flex;
  justify-content: space-around;
  margin: 30px 0;
  gap: 20px;
}

.summary-box {
  background-color: #e6f4ff;
  border: 1px solid #90c8f0;
  border-radius: 12px;
  padding: 20px;
  flex: 1;
  text-align: center;
  box-shadow: 0 2px 6px rgba(0, 122, 204, 0.1);
}

.summary-title {
  font-size: 18px;
  color: #007acc;
  font-weight: bold;
  display: block;
  margin-bottom: 8px;
}

.summary-value {
  font-size: 24px;
  color: #003d66;
  font-weight: bold;
}
.report-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 20px;
}
.report-table th,
.report-table td {
  border: 1px solid #cce0f4;
  padding: 10px 14px;
  text-align: left;
}
.report-table thead {
  background-color: #d6efff;
  color: #003d66;
}
</style>
