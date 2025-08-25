<script setup>
import SupermarketChart from './SupermarketChart.vue';
import InventoryManagement from './InventoryManagement.vue';
import { ref, onMounted, computed } from 'vue';

// Danh sách công việc chỉ chứa giờ
const tasks = ref([
  { id: 1, task: 'Kiểm tra hàng tồn kho.', time: '08:00:00' },
  { id: 2, task: 'Sắp xếp lại quầy rau củ.', time: '08:30:00' },
  { id: 3, task: 'Kiểm tra hạn sử dụng sản phẩm.', time: '09:00:00' },
  { id: 4, task: 'Dọn dẹp khu vực thanh toán.', time: '09:30:00' },
  { id: 5, task: 'Nhập hàng mới vào kho.', time: '10:00:00' },
  { id: 6, task: 'Báo cáo doanh số bán hàng.', time: '11:00:00' },
  { id: 7, task: 'Đào tạo nhân viên mới.', time: '13:00:00' },
  { id: 8, task: 'Kiểm kê thực phẩm đông lạnh.', time: '13:45:00' },
  { id: 9, task: 'Vệ sinh tủ lạnh và thiết bị làm mát.', time: '14:30:00' },
  { id: 10, task: 'Cập nhật dữ liệu nhập xuất kho.', time: '15:00:00' },
  { id: 11, task: 'Kiểm tra hệ thống POS.', time: '15:45:00' },
  { id: 12, task: 'Dọn dẹp quầy hàng và khu vực thu ngân.', time: '16:30:00' },
]);

// Thời gian hiện tại
const now = ref(new Date());

// Hàm ghép ngày hôm nay + giờ từ task
function getFullDateFromTime(timeStr) {
  const today = new Date();
  const [hh, mm, ss] = timeStr.split(':');
  return new Date(today.getFullYear(), today.getMonth(), today.getDate(), hh, mm, ss);
}

// Lọc công việc chưa diễn ra
const filteredTasks = computed(() =>
  tasks.value.filter((task) => getFullDateFromTime(task.time) > now.value)
);

// Ngày hôm nay để hiển thị
const todayStr = computed(() => now.value.toLocaleDateString('vi-VN'));

// Cập nhật giờ hiện tại mỗi phút
onMounted(() => {
  setInterval(() => {
    now.value = new Date();
  }, 60000);
});

// Đơn hàng nhập kho
const draftImports = ref(0);
const activeImports = ref(0);
const totalOrders = ref(0);

onMounted(async () => {
  try {
    const orderRes = await fetch('http://localhost:8083/api/orders');
    const orderData = await orderRes.json();
    totalOrders.value = Array.isArray(orderData) ? orderData.length : 0;

    const res = await fetch('http://localhost:8083/StockImport');
    const data = await res.json();
    const imports = Array.isArray(data) ? data : data.data || [];

    draftImports.value = imports.filter((i) => i.status === 'DRAFT').length;
    activeImports.value = imports.filter((i) => i.status === 'ACTIVE').length;
  } catch (error) {
    console.error('Lỗi khi lấy đơn hàng:', error);
  }
});
</script>

<template>
  <div class="body">
    <div class="body-left">
      <div class="body-left-part">
        <div class="body-left-part-title">
          <h3 class="Title">Danh sách đơn hàng theo ngày</h3>
        </div>
        <div class="body-left-part-main">
          <div class="main-part with_20">
            <router-link to="/importProduct">
              <p class="main-part-title">Đơn hàng chờ xử lý</p>
              <div class="main-part-number">{{ draftImports }}</div>
            </router-link>
          </div>
          <div class="main-part with_20">
            <p class="main-part-title">Đơn hàng đã hoàn thành</p>
            <div class="main-part-number">{{ activeImports + totalOrders }}</div>
          </div>
        </div>
      </div>

      <div class="body-left-part">
        <div class="body-left-part-title">
          <h3 class="Title">Bán hàng trực tiếp</h3>
        </div>
        <div class="center">
          <router-link to="/createOrder" class="order-card">
            <div class="order-card-icon">🧾</div>
            <div class="order-card-text">
              <p class="order-card-title">Tạo đơn bán hàng</p>
            </div>
          </router-link>
        </div>
      </div>

      <div class="body-left-part">
        <h3 class="Title">Top sản phẩm bán chạy</h3>
        <div class="best-sell-title"></div>
        <SupermarketChart />
      </div>

      <div class="body-left-part">
        <h3 class="Title">Kiểm kho nhanh</h3>
        <div class="backgroundInventory"></div>
        <InventoryManagement />
      </div>
    </div>

    <div class="body-right">
      <div class="body-left-part task-list">
        <div class="body-left-part-title">
          <h3 class="Title">Danh sách công việc</h3>
        </div>

        <p style="text-align: center; color: gray; font-size: 14px">Ngày hôm nay: {{ todayStr }}</p>

        <div class="task-container">
          <div v-for="task in filteredTasks" :key="task.id" class="task-item">
            <div class="task-dot"></div>
            <div class="task-content">
              <p class="task-title">{{ task.task }}</p>
              <p class="task-time">{{ task.time }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<style scoped>
.body {
  padding: 75px 30px 30px 50px;
  display: flex;
  gap: 10px;
  background-color: #f5f5f5;
  font-family: Arial, Helvetica, sans-serif;
  top: 50px;
}
.body-left {
  display: flex;
  flex-direction: column;
  gap: 10px;
  width: 78%;
}
.body-left-part {
  display: flex;
  flex-direction: column;
  border-radius: 5px;
  box-shadow: 5px 5px 15px rgba(0, 0, 0, 0.15);
  background-color: #fff9f9;
  padding: 15px;
  gap: 15px;
  justify-content: center;
}
.body-left-part-title {
  color: #34495e;
  font-weight: 500;
  font-size: 16px;
}
.body-left-part-main {
  display: flex;
  gap: 8px;
  justify-content: space-evenly;
}
.main {
  width: 50%;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  justify-content: center;
}
.main-part {
  border-radius: 3px;
  background-color: rgb(227, 238, 247);
  padding: 15px;
  justify-content: center;
}
.with_45 {
  width: 100%;
}
.with_20 {
  width: 40%;
}
.with_15 {
  width: 23%;
}
.main-part-spe {
  display: flex;
  justify-content: space-between;
  border-radius: 3px;
  background-color: rgb(227, 238, 247);
  padding: 15px;
}
.main-part-title {
  justify-content: flex-start;
  align-items: flex-start;
  white-space: nowrap;
}
.main-part-spe-number {
  font-size: 20px;
  color: rgb(22, 193, 245);
  display: flex;
  align-items: center;
  font-weight: 600;
}
.main-part-number {
  font-size: 20px;
  font-weight: 600;
}
.order-card {
  display: flex;
  align-items: center;
  gap: 12px;
  background-color: #e3f6e8;
  border: 1px solid #16bc50;
  border-radius: 8px;
  padding: 10px 16px;
  width: fit-content;
  max-width: 100%;
  transition: all 0.3s ease;
  cursor: pointer;
  box-shadow: 1px 1px 6px rgba(0, 0, 0, 0.05);
  text-decoration: none;
  color: inherit;
}

.order-card:hover {
  background-color: #d1f3db;
  box-shadow: 3px 3px 12px rgba(0, 0, 0, 0.1);
}

.order-card-icon {
  font-size: 32px;
  color: #16bc50;
}

.order-card-text {
  display: flex;
  flex-direction: column;
}

.order-card-title {
  font-size: 18px;
  font-weight: bold;
  color: #16bc50;
  margin: 0;
}

.order-card-sub {
  font-size: 13px;
  color: #555;
  margin: 0;
}

.search-container {
  width: 100%;
  padding: 15px 0px;
  display: flex;
  align-items: center;
  position: relative; /* Thêm vị trí tương đối cho container */
}

.search-input {
  display: block;
  width: 100%;
  padding-left: 30px; /* Thêm khoảng cách cho icon */
  border: 1px solid rgb(227, 238, 247);
  border-radius: 5px;
  box-sizing: border-box;
  background-color: rgb(227, 238, 247);
  color: #34495e;
  justify-content: flex-start;
  align-items: flex-start;
}
.search-input::placeholder {
  color: #34495e; /* Đặt màu chữ placeholder thành đen */
  font-family: Arial, Helvetica, sans-serif;
  font-size: 16px;
}
.search-input:focus {
  outline: none;
  border-color: rgb(227, 238, 247); /* Thay đổi màu border khi focus */
}

.search-icon {
  position: absolute; /* Đặt icon ở vị trí tuyệt đối */
  left: 10px;
  top: 50%;
  transform: translateY(-50%); /* Căn giữa icon theo chiều dọc */
  color: #34495e;
  cursor: pointer;
}

.curson {
  cursor: pointer;
}
.best-sell-title {
  background-image: url('https://media.istockphoto.com/id/1089676120/vi/vec-to/s%E1%BA%A3n-ph%E1%BA%A9m-th%E1%BB%B1c-ph%E1%BA%A9m-%C4%91%C3%B3ng-g%C3%B3i-s%E1%BA%A3n-ph%E1%BA%A9m-n%E1%BA%A5u-%C4%83n-h%C3%A0ng-si%C3%AAu-th%E1%BB%8B-v%C3%A0-th%E1%BB%B1c-ph%E1%BA%A9m-%C4%91%C3%B3ng-h%E1%BB%99p-l%E1%BB%8D-b%C3%A1nh-quy.jpg?s=2048x2048&w=is&k=20&c=YncwKSyHYlLwcIFTMKrQR5TIkqDnYabmn2ymQH5cEPA=');
  height: 240px;
  background-size: cover;
  background-position: top;
  background-repeat: no-repeat;
  color: #34495e;
  display: flex;
  justify-content: center;
  align-items: flex-start;
  font-size: 20px;
  font-weight: 500;
  top: 15%;
}
.backgroundInventory {
  background-image: url('https://arito.vn/wp-content/uploads/2024/11/Screenshot-2024-11-26-at-23.00.12.png');
  height: 300px;
  background-size: cover;
  background-repeat: no-repeat;
  background-position: center;
}
.Title {
  text-transform: uppercase;
  font-weight: 600;
  font-size: 24px;
  color: #34495e;
  text-align: center;
  display: flex;
  justify-content: center;
  align-items: center;
}
.body-right {
  display: flex;
  flex: 1;
  flex-direction: column;
  height: auto;
}
.search-input:active {
  border: 1px solid rgb(227, 238, 247);
}
.task-list {
  padding: 15px;
  background: white;
  border-radius: 5px;
  box-shadow: 5px 5px 15px rgba(0, 0, 0, 0.15);
  height: auto;
}

.task-container {
  border-left: 3px solid #007bff;
  padding-left: 10px;
  margin-top: 10px;
}

.task-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
  position: relative;
}

.task-dot {
  width: 8px;
  height: 8px;
  background: #007bff;
  border-radius: 50%;
  position: absolute;
  left: -15px;
}

.task-content {
  margin-left: 10px;
}

.task-title {
  font-weight: bold;
  color: #333;
}

.task-time {
  font-size: 12px;
  color: gray;
}
a {
  text-decoration: none;
  color: black;
}
.center {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 12px 0;
  width: 100%;
}
</style>
