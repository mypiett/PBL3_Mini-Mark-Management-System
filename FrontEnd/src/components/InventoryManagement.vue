<template>
  <div class="inventory-container">
    <!-- Dòng 1: Sản phẩm sắp hết hàng và tồn kho nhiều -->
    <div class="stock-container">
      <div class="low-stock-warning">
        <h3>Sản phẩm sắp hết hàng</h3>
        <ul>
          <li v-for="product in lowStockProducts" :key="product.id">
            <span class="product-name">{{ product.name }}</span>
            <span class="stock-count">Số lượng còn: {{ product.quantity }}</span>
          </li>
        </ul>
      </div>


      <div class="high-stock-warning">
        <h3>Sản phẩm tồn kho nhiều</h3>
        <ul>
          <li v-for="product in highStockProducts" :key="product.id">
            <span class="product-name">{{ product.name }}</span>
            <span class="stock-count">Số lượng tồn: {{ product.quantity }}</span>
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>


<script setup>
import { ref, computed, onMounted } from 'vue';


// Khai báo danh sách sản phẩm sẽ lấy từ API
const products = ref([]);


// Gọi API để lấy dữ liệu sản phẩm
onMounted(async () => {
  try {
    const response = await fetch('http://localhost:8083/api/products');
    const data = await response.json();
    products.value = Array.isArray(data) ? data : data.data || [];
  } catch (error) {
    console.error('Lỗi khi tải sản phẩm từ API:', error);
  }
});


// Lọc sản phẩm có số lượng < 10
const lowStockProducts = computed(() => {
  return products.value.filter(product => parseInt(product.quantity) < 10);
});


// Lọc sản phẩm có số lượng > 100
const highStockProducts = computed(() => {
  return products.value.filter(product => parseInt(product.quantity) > 100);
});
</script>


<style scoped>
.inventory-container {
  padding: 10px;
  background-color: #f9fafb;
  border-radius: 12px;
  box-shadow: 0px 4px 20px rgba(0, 0, 0, 0.1);
  width: 95%;
  margin: 10px;
}


.stock-container {
  display: flex;
  justify-content: space-between;
  gap: 20px;
}


.low-stock-warning, .high-stock-warning {
  flex: 1;
}


.low-stock-warning h3, .high-stock-warning h3 {
  font-size: 18px;
  font-weight: 600;
  color: #e74c3c;
  text-transform: uppercase;
  margin-bottom: 12px;
  width: 100%;
}


.low-stock-warning ul, .high-stock-warning ul {
  list-style-type: none;
  padding: 0;
}


.low-stock-warning li, .high-stock-warning li {
  background-color: #fff;
  border-radius: 10px;
  padding: 12px;
  margin-bottom: 10px;
  display: flex;
  justify-content: space-between;
  box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.1);
}


.product-name {
  font-size: 16px;
  font-weight: 500;
  color: #333;
}


.stock-count {
  font-size: 14px;
  color: #555;
  background-color: #f7f7f7;
  padding: 6px 12px;
  border-radius: 8px;
}
</style>



