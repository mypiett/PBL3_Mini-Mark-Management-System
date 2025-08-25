<script setup>
import { useRoute, useRouter } from 'vue-router';
import { ref, onMounted, computed } from 'vue';
import axios from 'axios';
import MainPage from '../../components/Mainpage.vue';
import TheHeader from '../../components/Mainpage.vue';

const route = useRoute();
const router = useRouter();

const importId = route.query.importId;
const importDetailId = route.query.id;
const productIdFromQuery = route.query.productId;

const importDetails = ref([]);
const productMap = ref({}); // { productId: productObject }
const reasons = [
  'Hàng hóa bị lỗi hoặc hư hỏng',
  'Hàng không đúng chủng loại hoặc số lượng',
  'Hàng gần hết hạn hoặc hết hạn',
  'Khác',
];
const selectedReason = ref('');
const otherReasonText = ref('');
const message = ref('');
const messageType = ref('');

// Hàm format tiền tệ
const formatCurrency = (value) => {
  if (typeof value !== 'number') return value;
  return value.toLocaleString('vi-VN', { style: 'currency', currency: 'VND' });
};

onMounted(async () => {
  try {
    // 1. Lấy chi tiết nhập
    const resDetails = await axios.get(
      `http://localhost:8083/StockImportDetails/details/${importId}`
    );
    if (Array.isArray(resDetails.data)) {
      importDetails.value = resDetails.data;
      console.log('Chi tiết nhập:', importDetails.value);
    } else {
      console.error('Dữ liệu chi tiết không phải mảng:', resDetails.data);
      importDetails.value = [];
    }

    // 2. Lấy danh sách productId
    const resProductIds = await axios.get(
      `http://localhost:8083/StockImportDetails/productIds/${importId}`
    );
    const productIds = Array.isArray(resProductIds.data) ? resProductIds.data : [];
    console.log('Danh sách productIds:', productIds);

    // 3. Gán productId vào từng chi tiết
    importDetails.value = importDetails.value.map((detail, index) => ({
      ...detail,
      productId: productIds[index] || null,
    }));

    // 4. Lấy thông tin product theo productId và lưu vào productMap
    for (const pid of productIds) {
      try {
        const productRes = await axios.get(`http://localhost:8083/api/products/${pid}`);
        productMap.value[pid] = productRes.data;
      } catch (error) {
        console.error(`Lỗi lấy product với id ${pid}`, error);
      }
    }
  } catch (error) {
    console.error('Lỗi khi lấy chi tiết hoặc productIds:', error);
  }
});

// Tìm detail đang chọn (dựa trên importDetailId và productId)
const selectedDetail = computed(() => {
  return importDetails.value.find(
    (detail) =>
      String(detail.importDetailId) === String(importDetailId) &&
      String(detail.productId) === String(productIdFromQuery)
  );
});

const handleComback = () => {
  router.push('/importProduct');
};

const handleComplete = async () => {
  if (!selectedReason.value) {
    message.value = 'Vui lòng chọn lý do trả hàng.';
    messageType.value = 'error';
    return;
  }

  if (selectedReason.value === 'Khác' && !otherReasonText.value.trim()) {
    message.value = 'Vui lòng nhập lý do cụ thể khi chọn "Khác".';
    messageType.value = 'error';
    return;
  }

  const finalReason =
    selectedReason.value === 'Khác' ? otherReasonText.value : selectedReason.value;

  try {
    // Gọi API xóa chi tiết nhập hàng
    const deleteRes = await axios.delete(
      `http://localhost:8083/StockImportDetails/delete/${importDetailId}`
    );
    if (deleteRes.status === 200 || deleteRes.status === 204) {
      console.log('Xoá chi tiết thành công');

      // Gọi API cập nhật lại tổng tiền đơn nhập
      const updateTotalRes = await axios.put(
        `http://localhost:8083/StockImport/update-total/${importId}`
      );
      if (updateTotalRes.status === 200 || updateTotalRes.status === 204) {
        message.value = 'Trả hàng thành công với lý do: ' + finalReason;
        messageType.value = 'success';
      } else {
        console.error('Lỗi khi cập nhật tổng tiền đơn nhập');
        message.value = 'Trả hàng thành công nhưng không cập nhật được tổng tiền đơn nhập.';
        messageType.value = 'warning';
      }

      setTimeout(() => {
        router.push('/importProduct');
      }, 1000);
    } else {
      throw new Error('Xoá thất bại với status: ' + deleteRes.status);
    }
  } catch (error) {
    message.value = 'Đã xảy ra lỗi khi trả hàng.';
    messageType.value = 'error';
    console.error('Lỗi khi xử lý trả hàng:', error);
  }
};
</script>

<template>
  <div class="main-container">
    <MainPage />
    <div class="container">
      <div class="page">
        <TheHeader />
        <div class="page-main">
          <div class="title">
            <h3>Thông tin hàng bị trả</h3>
          </div>
          <div v-if="message" :class="['message-box', messageType]">
            {{ message }}
          </div>
          <div v-if="selectedDetail">
            <table class="product-table">
              <thead>
                <tr>
                  <th>Mã sản phẩm</th>
                  <th>Tên sản phẩm</th>
                  <th>Số lượng nhập</th>
                  <th>Đơn giá</th>
                  <th>Tổng tiền</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td>{{ selectedDetail.productId }}</td>
                  <td>{{ productMap[selectedDetail.productId]?.name || 'Đang tải...' }}</td>
                  <td>{{ selectedDetail.quantity }}</td>
                  <td>{{ formatCurrency(selectedDetail.unitPrice) }}</td>
                  <td>{{ formatCurrency(selectedDetail.totalPrice) }}</td>
                </tr>
              </tbody>
            </table>
          </div>
          <div v-else>
            <p>Không tìm thấy dữ liệu chi tiết phù hợp.</p>
          </div>
          <div class="reason-section">
            <h4>Chọn lý do trả hàng:</h4>
            <div class="reason-option" v-for="(reason, index) in reasons" :key="index">
              <input
                type="radio"
                :id="'reason-' + index"
                :value="reason"
                v-model="selectedReason"
              />
              <label :for="'reason-' + index">{{ reason }}</label>
            </div>

            <div v-if="selectedReason === 'Khác'" class="other-reason">
              <label for="otherReason">Nhập lý do khác:</label>
              <input
                id="otherReason"
                type="text"
                v-model="otherReasonText"
                placeholder="Nhập lý do cụ thể"
              />
            </div>
          </div>
          <div class="completeContainer">
            <button class="comback" @click="handleComback">Quay lại</button>
            <button class="complete" @click="handleComplete">Hoàn tất</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
body {
  padding: 0;
  margin: 0;
  overflow: hidden;
}
.main-container {
  display: flex;
  width: 100%;
  height: 100vh;
  position: relative;
}

.container {
  flex: 1;
  display: flex;
  flex-direction: column;
  background-color: #fff;
  margin-left: 18%;
  position: relative;
}

.page {
  flex: 1;
  display: flex;
  flex-direction: column;
  background-color: #fff;
  position: relative;
  font-family: Arial, Helvetica, sans-serif;
}
.page-main {
  display: flex;
  flex-direction: column;
  margin-top: 6%;
  padding: 0px 30px;
  background-color: #fff;
}
.title {
  display: flex;
  justify-content: space-between;
  padding: 10px;
  align-items: center;
}
.title h3 {
  font-weight: 600;
  color: rgb(50, 50, 50);
}
.addProduct {
  padding: 2px 15px;
  border-radius: 30px;
  background-color: #0884f1;
  color: #fff;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
}
.addProduct i {
  color: #fff;
  margin-right: 5px;
}
.addProduct:hover {
  background-color: #279aff;
  color: #f1f1f1;
}
.addProduct:hover i {
  color: #f1f1f1;
}
.addProduct:active {
  transform: scale(0.95);
}
a {
  text-decoration: none;
}
.product-table {
  width: 100%;
  border-collapse: collapse;
  background-color: white;
  margin-top: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.product-table th,
.product-table td {
  padding: 12px 16px;
  border: 1px solid #ccc;
  text-align: left;
}

.product-table th {
  background-color: #f0f0f0;
  font-weight: 600;
}
.reason-section {
  margin-top: 20px;
  background-color: #fff;
  padding: 16px;
  border-radius: 8px;
  border: 1px solid #ddd;
}

.reason-option {
  margin-bottom: 10px;
  display: flex;
  align-items: center;
}

.reason-option input[type='radio'] {
  margin-right: 10px;
}

.other-reason {
  margin-top: 10px;
}

.other-reason input {
  width: 100%;
  padding: 8px;
  margin-top: 5px;
  border-radius: 4px;
  border: 1px solid #ccc;
}
.other-reason input:active {
  border: 1px solid #ccc;
}
.completeContainer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 20px;
  padding-bottom: 20px;
}
.complete {
  background-color: #007bff;
  color: #fff;
  border: 1px solid #007bff;
  padding: 15px 20px;
  cursor: pointer;
  border-radius: 30px;
  font-size: 16px;
}
.complete:hover {
  background-color: #007bff;
}
.complete:active {
  transform: scale(0.95);
}
.comback {
  background-color: #6c757d;
  color: #fff;
  border: 1px solid #6c757d;
  padding: 15px 20px;
  cursor: pointer;
  border-radius: 30px;
  font-size: 16px;
}
.comback:hover {
  background-color: #5a6268;
}
.message-box {
  margin-top: 15px;
  padding: 12px;
  border-radius: 6px;
  font-weight: 500;
}

.message-box.success {
  background-color: #e0f7e9;
  color: #2e7d32;
  border: 1px solid #2e7d32;
}

.message-box.error {
  background-color: #fdecea;
  color: #c62828;
  border: 1px solid #c62828;
}
</style>
