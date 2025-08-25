<script setup>
import { useRoute, useRouter } from 'vue-router';
import { ref, onMounted, computed } from 'vue';
import axios from 'axios';
import MainPage from '../../components/Mainpage.vue';
import TheHeader from '../../components/TheHeader.vue';

const route = useRoute();
const router = useRouter();

const importId = ref(route.query.importId); // ✅ Lấy từ query thay vì params
const message = ref('');
const importProduct = ref([]);
const importDetails = ref([]);
const productName = ref({});
const showDraftList = computed(() => !importId.value);

// Lấy danh sách các đơn nhập có trạng thái "DRAFT"
onMounted(async () => {
  try {
    const res = await axios.get('http://localhost:8083/StockImport');
    if (Array.isArray(res.data)) {
      importProduct.value = res.data.filter((item) => item.status === 'DRAFT');
    }

    // ✅ Nếu có importId truyền vào thì tự động gọi chi tiết
    if (importId.value) {
      await selectImportId(importId.value);
    }
  } catch (error) {
    console.error('Lỗi khi lấy danh sách nhập:', error);
  }
});

// Danh sách các đơn DRAFT
const draftImportList = computed(() => importProduct.value);

// Khi chọn đơn nhập để xem chi tiết
const selectImportId = async (id) => {
  importId.value = id;
  try {
    const resDetails = await axios.get(`http://localhost:8083/StockImport/${id}`);
    if (resDetails.data.stockImportDetail) {
      importDetails.value = resDetails.data.stockImportDetail;
    }

    const resProductIds = await axios.get(
      `http://localhost:8083/StockImportDetails/productIds/${id}`
    );
    const productIds = resProductIds.data || [];

    importDetails.value = importDetails.value.map((detail, idx) => ({
      ...detail,
      productId: productIds[idx] || null,
    }));

    for (const pid of productIds) {
      try {
        const productRes = await axios.get(`http://localhost:8083/api/products/${pid}`);
        productName.value[pid] = productRes.data.name;
      } catch (error) {
        console.error(`Không thể lấy tên sản phẩm với id = ${pid}`, error);
      }
    }
  } catch (error) {
    console.error('Lỗi khi lấy chi tiết nhập hàng hoặc productIds:', error);
  }
};

// Method để format tiền tệ
const formatCurrency = (value) => {
  if (value) {
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
  }
  return value;
};

// Hàm nhập kho
const importStock = async () => {
  try {
    const res = await axios.put(`http://localhost:8083/StockImport/${importId.value}/import-stock`);
    message.value = res.data;

    setTimeout(() => {
      router.push('/ProductCatalog');
    }, 5000); // 10 giây
  } catch (error) {
    console.error('Lỗi khi nhập kho:', error);
  }
};

// Chuyển đến trang trả hàng
const goToPayProduct = (importId, importDetailId, productId) => {
  router.push({
    path: '/payProduct',
    query: {
      importId: importId,
      id: importDetailId,
      productId: productId,
    },
  });
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
            <h3>Danh sách nhập hàng</h3>
          </div>
          <div v-if="message" class="message">{{ message }}</div>

          <div v-if="showDraftList && draftImportList.length > 0">
            <h3>Danh sách đơn nhập</h3>
            <ul>
              <li v-for="importItem in draftImportList" :key="importItem.importId">
                <a href="#" @click.prevent="selectImportId(importItem.importId)">
                  Nhập đơn {{ importItem.importId }}
                </a>
              </li>
            </ul>
          </div>

          <div v-if="importId && importDetails.length > 0" class="importProduct_container">
            <h2>Đơn nhập hàng: {{ importId }}</h2>
            <table class="product-table">
              <thead>
                <tr>
                  <th>Tên sản phẩm</th>
                  <th>Số lượng nhập</th>
                  <th>Đơn giá</th>
                  <th>Tổng tiền</th>
                  <th>Xem xét</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="detail in importDetails" :key="detail.importDetailId">
                  <td>{{ productName[detail.productId] || 'Không có tên sản phẩm' }}</td>
                  <td>{{ detail.quantity }}</td>
                  <td>{{ formatCurrency(detail.unitPrice) }}</td>
                  <td>{{ formatCurrency(detail.totalPrice) }}</td>
                  <td class="payProductContainer">
                    <button
                      class="payProduct"
                      @click="goToPayProduct(importId, detail.importDetailId, detail.productId)"
                    >
                      Trả hàng
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>

            <div class="button-group">
              <button class="back-button" @click="importId = null">
                ← Quay lại danh sách đơn nhập
              </button>
              <button @click="importStock" class="import-btn">Nhập kho</button>
            </div>
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
  background-color: #ffffff; /* Thay đổi nền của toàn bộ trang */
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
  background-color: #ffffff; /* Nền trắng cho khu vực chính */
  margin-left: 18%;
  position: relative;
}

.page {
  flex: 1;
  display: flex;
  flex-direction: column;
  background-color: #ffffff; /* Nền trắng cho khu vực chính */
  position: relative;
  font-family: Arial, Helvetica, sans-serif;
}

.page-main {
  display: flex;
  flex-direction: column;
  margin-top: 6%;
  padding: 0px 30px;
  background-color: #ffffff; /* Nền sáng cho phần nội dung chính */
}

.title {
  display: flex;
  justify-content: space-between;
  padding: 10px;
  align-items: center;
}

.title h3 {
  font-size: 24px;
  font-weight: 600;
  color: rgb(50, 50, 50);
}
a {
  text-decoration: none;
}
.importProduct_container {
  margin-top: 20px;
}

.product-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 20px;
}

.product-table th,
.product-table td {
  border: 1px solid #ddd;
  padding: 10px;
  text-align: left;
}

.product-table th {
  background-color: #e0e0e0; /* Nền sáng cho tiêu đề bảng */
}

.import-btn {
  padding: 10px 20px;
  background-color: #4caf50;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 16px;
}

.import-btn:hover {
  background-color: #45a049;
}
.import-btn:active {
  transform: scale(0.95);
}
.message {
  margin-top: 20px;
  padding: 10px;
  background-color: #0884f1;
  color: #fff;
  border: 1px solid #ccc;
  border-radius: 5px;
  text-align: center;
  font-weight: bold;
}
.payProductContainer {
  display: flex;
  justify-content: center;
  align-items: center;
}
.payProduct {
  background-color: #0884f1;
  color: #fff;
  padding: 5px;
  border: #0884f1;
  cursor: pointer;
}
.payProduct:active {
  transform: scale(0.95);
}
.button-group {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
}

.button-group button {
  margin-right: 10px;
}

.button-group .import-btn {
  margin-left: auto; /* đẩy sang bên phải */
}

.back-button {
  background-color: #0884f1;
  color: white;
  border: none;
  padding: 8px 15px;
  border-radius: 5px;
  cursor: pointer;
  font-size: 14px;
}

.back-button:hover {
  background-color: #006fd6;
}

.back-button:active {
  transform: scale(0.95);
}
</style>
