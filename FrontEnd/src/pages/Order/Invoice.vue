<script setup>
import Mainpage from '../../components/Mainpage.vue';
import TheHeader from '../../components/TheHeader.vue';
import { ref, computed, onMounted } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';

const router = useRouter();

const searchKeyword = ref('');
const invoices = ref([]);
const filterMoney = ref('');
const filterTime = ref('');
const dropdowns = ref({
  money: false,
  time: false,
});

function Dropdown(type) {
  dropdowns.value[type] = !dropdowns.value[type];
  for (let key in dropdowns.value) {
    if (key !== type) dropdowns.value[key] = false;
  }
}

function filterByMoney(money) {
  filterMoney.value = money;
  dropdowns.value.money = false;
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

function getMoneyRange(range, money) {
  money = parseFloat(money);
  switch (range) {
    case 'Dưới 500000':
      return money >= 0 && money < 500000;
    case '500000-2000000':
      return money >= 500000 && money <= 2000000;
    case 'Trên 2000000':
      return money > 2000000;
    default:
      return true;
  }
}

const filteredInvoices = computed(() => {
  return invoices.value.filter((inv) => {
    const matchName = inv.name.toLowerCase().includes(searchKeyword.value.toLowerCase());
    const matchMoney = filterMoney.value ? getMoneyRange(filterMoney.value, inv.total) : true;
    const matchTime = filterTime.value ? matchTimeFilter(inv.date, filterTime.value) : true;
    return matchName && matchMoney && matchTime;
  });
});

function resetFilters() {
  filterMoney.value = '';
  filterTime.value = '';
  searchKeyword.value = '';
}
function formatDate(dateStr) {
  const date = new Date(dateStr);
  return date.toLocaleDateString('vi-VN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
  });
}

function invoiceDetail(orderId) {
  router.push(`/orders/${orderId}`);
}
onMounted(async () => {
  try {
    const res = await axios.get('http://localhost:8083/api/orders');
    const orders = res.data;

    for (const order of orders) {
      const productIds = order.order_detail
        .filter((item) => item && item.product_id)
        .map((item) => item.product_id);

      try {
        const productRequests = productIds.map((id) =>
          axios.get(`http://localhost:8083/api/products/${id}`).then((res) => res.data.name)
        );

        const productNames = await Promise.all(productRequests);
        order.productNames = productNames;
      } catch (error) {
        console.warn('Lỗi khi tải sản phẩm:', error);
        order.productNames = [];
      }
    }

    invoices.value = orders.map((order) => ({
      id: order.order_id,
      name: order.customer?.name || 'Khách lẻ',
      date: order.order_date,
      total: order.total_amount,
      products: order.productNames,
      product_id: order.order_detail.map((item) => item.product_id),
    }));
  } catch (error) {
    console.error('Lỗi khi tải đơn hàng:', error);
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
          <h3>Danh sách đơn hàng</h3>
          <router-link to="/createOrder" class="router">
            <button class="add-button">
              <font-awesome-icon :icon="['fas', 'circle-plus']" />
              Tạo đơn hàng
            </button>
          </router-link>
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
              <div class="dropdown-container">
                <button class="list-box" @click="Dropdown('money')">
                  Tổng tiền
                  <font-awesome-icon :icon="['fas', 'sort-down']" />
                </button>
                <div v-if="dropdowns.money" class="dropdown-menu">
                  <div @click="filterByMoney('Dưới 500000')">Dưới 500.000</div>
                  <div @click="filterByMoney('500000-2000000')">500.000 - 2.000.000</div>
                  <div @click="filterByMoney('Trên 2000000')">Trên 2.000.000</div>
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
                <th>Mã đơn</th>
                <th>Khách hàng</th>
                <th>Ngày đặt</th>
                <!-- <th>Mã sản phẩm</th> -->
                <th>Tổng tiền</th>
                <th>Thao tác</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(invoice, index) in filteredInvoices" :key="invoice.id">
                <td>{{ index + 1 }}</td>
                <td>{{ invoice.id }}</td>
                <td>{{ invoice.name }}</td>
                <td>{{ formatDate(invoice.date) }}</td>
                <!-- <td>{{ invoice.product_id.join(', ') }}</td> -->
                <td>{{ invoice.total }}</td>
                <td>
                  <button @click="invoiceDetail(invoice.id)" class="btn-view">Xem</button>
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
body {
  width: 100%;
  height: 100%;
  /* font-size: 20px;
    font-family:'Noto Sans', sans-serif; */
}

.temp {
  position: absolute;
  width: 100%;
  height: 100%;
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
  font-size: 20px;
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

.white-container {
  background-color: white;
  width: 100%;
  height: auto;
  border-radius: 5px;
  box-shadow: 5px 10px 15px 0px rgba(0, 0, 0, 0.2);
  padding: 10px;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.add-button {
  background-color: #16bc50;
  padding: 10px 20px;
  border: none;
  border-radius: 5px;
  font-weight: 600;
  color: white;
  cursor: pointer;
  transition: background-color 0.3s ease;
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

.btn-view {
  background-color: #3b82f6;
  color: white;
  padding: 4px 12px;
  border: none;
  border-radius: 5px;
  margin-right: 8px;
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
