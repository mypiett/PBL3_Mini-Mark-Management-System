<script setup>
import { onMounted, ref, computed } from 'vue'
import axios from 'axios'
import { useRoute } from 'vue-router'
import { useRouter } from 'vue-router'
import MainPage from '@/components/Mainpage.vue'

const route = useRoute()
const router = useRouter()
const importOrder = ref(null)
const confirm = ref(null)

const totalCost = computed(() => {
  return importOrder.value?.stockImportDetail.reduce((sum, item) => sum + item.totalPrice, 0) || 0
})

const formattedImportDate = computed(() => {
  const date = new Date(importOrder.value?.importDate)
  return date.toLocaleString()
})
const confirmImport = () => {
  router.push({
    path: '/importProduct',
    query: { importId: route.params.id }
  })
}
function getStatusLabel(status) {
  switch (status) {
    case 'DRAFT':
      return 'Chờ duyệt'
    case 'ACTIVE':
      return 'Đã nhập'
    case 'CANCELLED':
      return 'Đã hủy'
    default:
      return status
  }
}
onMounted(async () => {
  confirm.value = route.query.confirm
  const importId = route.params.id
  try {
    const response = await axios.get(`http://localhost:8083/StockImport/${importId}`)
    const importData = response.data

    // Đổi tên đúng là stockImportDetail
    if (!Array.isArray(importData.stockImportDetail)) {
      importData.stockImportDetail = []
    }

    // Nếu có API để lấy thông tin sản phẩm theo importDetailId hoặc productId thì dùng, nếu không thì bỏ phần này
    // (Tạm bỏ nếu không có product_id)

    // Giả sử bạn có thông tin tên sản phẩm khác, bạn có thể thêm vào nếu cần

    importOrder.value = importData
  } catch (err) {
    alert('Không thể tải phiếu nhập.')
    console.error(err)
  }
})

</script>


<template>
  <div class="container">
    <div class="sidebar">
      <MainPage />
    </div>

    <div class="invoice-container" v-if="importOrder">
      <router-link to="/invoiceImport" class="back-button" style="text-decoration: none; font-size: 30px;">⬅️</router-link>
      <div class="header">
        <h2>📦 HÓA ĐƠN NHẬP HÀNG</h2>
        <p>Mã phiếu nhập: <strong>{{ importOrder.importId }}</strong></p>
        <p>Ngày nhập: <strong>{{ formattedImportDate }}</strong></p>
        <p>Trạng thái: <strong>{{ getStatusLabel(importOrder.status) }}</strong></p>
      </div>

      <div class="info">
        <p>Mã nhân viên nhập: <strong>{{ importOrder.employeeId }}</strong></p>
      </div>

      <table class="product-table">
        <thead>
          <tr>
            <th>Sản phẩm</th>
            <th>Số lượng</th>
            <th>Đơn giá</th>
            <th>Thành tiền</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in importOrder.stockImportDetail" :key="item.importDetailId">
            <td>{{ item.product.name}}</td>
            <td>{{ item.quantity }}</td>
            <td>{{ item.unitPrice.toLocaleString() }} đ</td>
            <td>{{ item.totalPrice.toLocaleString() }} đ</td>
            </tr>
        </tbody>
      </table>

      <div class="totals">
        <p>Tổng chi phí: <strong>{{ totalCost.toLocaleString() }} đ</strong></p>
      </div>
      <div class="actions">
        <button v-if="confirm" @click="confirmImport" class="confirm-button">✅ Xác nhận hóa đơn</button>
    </div>
    </div>
  </div>
</template>


<style scoped>
.invoice-container {
  display: flex;
  flex-direction: column;
  flex: 1;
  margin: 40px 30px;
  padding: 30px;
  background-color: #fff;
  border-radius: 16px;
  box-shadow: 0 0 16px rgba(0, 0, 0, 0.1);
  font-family: 'Segoe UI', sans-serif;
}
.container {
  display: flex;
  flex-direction: row;
  width: 100%;
  height: 100vh;
  position: relative;
  overflow-y: auto;
  flex-shrink: 0;
  font-family: 'Noto Sans', sans-serif;
}
.sidebar {
  height: 100vh;
  width: 18%;
  flex-shrink: 0;
}
.header {
  text-align: center;
  margin-bottom: 30px;
}
.header h2 {
  font-size: 24px;
  color: #2c3e50;
  margin-bottom: 10px;
}
.info {
  margin-bottom: 20px;
  background: #f9f9f9;
  padding: 12px 20px;
  border-radius: 8px;
  font-size: 15px;
}
.product-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 20px;
}
.product-table th,
.product-table td {
  border: 1px solid #ddd;
  padding: 12px;
  text-align: center;
}
.product-table th {
  background-color: #f0f0f0;
  font-weight: 600;
}
.totals {
  margin-top: 25px;
  text-align: right;
  font-size: 16px;
  color: #333;
}
.totals p {
  margin: 6px 0;
}
.totals strong {
  color: #27ae60;
}
.actions {
  margin-top: 30px;
  text-align: right;
}
.confirm-button {
  padding: 10px 20px;
  background-color: #27ae60;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.2s ease;
}
.confirm-button:hover {
  background-color: #219150;
}
</style>

