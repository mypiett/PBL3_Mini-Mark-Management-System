
<script setup>
import { onMounted, ref, computed } from 'vue'
import axios from 'axios'
import { useRoute } from 'vue-router'
import { useRouter } from 'vue-router';
import MainPage from '@/components/Mainpage.vue';

const route = useRoute()
const router = useRouter()
const order = ref(null)

const totalBeforeDiscount = computed(() => {
  return order.value?.order_detail.reduce((sum, item) => sum + item.subtotal, 0) || 0
})
const points = computed(() => {
  return Math.floor(order.value.total_amount / 10000); 
});

// const totalAfterDiscount = computed(() => {
//   if (!order.value?.voucher) return totalBeforeDiscount.value
//   return totalBeforeDiscount.value * (1 - order.value.voucher / 100)
// })

const formattedOrderDate = computed(() => {
  const date = new Date(order.value?.order_date)
  return date.toLocaleString()
})
const phone = route.query?.phone;
console.log(phone);
const goToAddCustomer = () => {
    router.push({
        path: '/detail/Customer/Create',
        query: {
            phone: phone,
            points: points.value
        }
    });
};
onMounted(async () => {
  const orderId = route.params.id;
  console.log(orderId);
  try {
     const response = await axios.get(`http://localhost:8083/api/orders/${orderId}`);
    const orderData = response.data;

    // Gọi thêm API sản phẩm tương ứng
   const productResponses = await Promise.all(
      orderData.order_detail.map(item =>
        axios.get(`http://localhost:8083/api/products/${item.product_id}`)
      )
    );

    // Gộp thông tin sản phẩm vào chi tiết đơn hàng
    orderData.order_detail = orderData.order_detail.map((item, index) => ({
      ...item,
      product_name: productResponses[index].data.name,
      price: item.subtotal / item.product_quantity,
      quantity: item.product_quantity
    }));

    order.value = orderData;
  } catch (err) {
    alert('Không thể tải hóa đơn.')
    console.error(err)
  }
})
</script>
<template>
  <div class = "container">
  <div class="sidebar">
      <MainPage />
    </div>
  <div class="invoice-container" v-if="order">
    <router-link to="/invoice" class="back-button" style="text-decoration: none; font-size: 30px;">⬅️</router-link>
    <div class="header">
      <h2>🧾 HÓA ĐƠN MUA HÀNG</h2>
      <p class="order-id">Mã đơn hàng: <span>{{ order.order_id }}</span></p>
      <p>Ngày đặt hàng: <strong>{{ formattedOrderDate }}</strong></p>
    </div>

    <div class="info">
      <p><strong>👤 Khách hàng:</strong> {{ order.customer?.name || 'Khách lẻ' }}</p>
      <p><strong>👨‍💼 Nhân viên:</strong> {{ order.employee?.name || 'Không có thông tin nhân viên' }}</p>
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
        <tr v-for="item in order.order_detail" :key="item.product_id">
          <td>{{ item.product_name }}</td> <!-- hoặc Linh gọi API product để lấy tên -->
          <td>{{ item.product_quantity }}</td>
          <td>{{ item.price.toLocaleString() }} đ</td>
          <td>{{ (item.subtotal || 0).toLocaleString() }} đ</td>
        </tr>
      </tbody>
    </table>

    <div class="totals">
      <p>Tổng cộng: <span>{{ totalBeforeDiscount.toLocaleString() }} đ</span></p>
      <p v-if="order.voucher">Giảm giá: <span>{{ order.voucher }}%</span></p>
      <p v-if="order.voucher">Tổng sau giảm: <span>{{ order.total_amount }} đ</span></p>
    </div>

    <p class="thank-you">🎉 Cảm ơn quý khách đã mua hàng!</p>
    <div class="action-container">
        <button v-if="phone" @click="goToAddCustomer" class="addCustomer">
          ➕ Tích điểm khách hàng mới
        </button>
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
  box-shadow: 0px 0px 16px rgba(0, 0, 0, 0.1);
  font-family: 'Segoe UI', sans-serif;
}
.container {
  display: flex;
  flex-direction: row;
  width: 100%;
  height: auto;
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

.order-id span {
  color: #e67e22;
  font-weight: bold;
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

.totals span {
  color: #27ae60;
  font-weight: bold;
}

.thank-you {
  text-align: center;
  margin-top: 40px;
  font-weight: bold;
  font-size: 16px;
  color: #2980b9;
}
.action-container {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}

.addCustomer {
  background-color: #3498db;
  border: none;
  border-radius: 8px;
  padding: 14px 30px;
  font-size: 16px;
  color: white;
  cursor: pointer;
  transition: background-color 0.3s;
}

.addCustomer:hover {
  background-color: #2c80b4;
}

</style>
