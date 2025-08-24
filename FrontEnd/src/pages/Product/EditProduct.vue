<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import { useRoute, useRouter } from 'vue-router';
import MainPage from '../../components/Mainpage.vue';
import TheHeader from '../../components/TheHeader.vue';


axios.defaults.baseURL = 'http://localhost:8083';


const route = useRoute();
const router = useRouter();
const productId = route.params.id;
console.log("Product ID:", productId);


const product = ref({
  productId: '',
  name: '',
  quantity: 0,
  price: 0,
  stock_price: 0,
  expirationDate: '',
  sell: 0,
  img: '',
  categoryId: '',
  supplierId: '',
  status: 'Hiển thị',  // trạng thái hiển thị ở giao diện
  displayOrder: 0
});


const categories = ref([]);
const suppliers = ref([]);
const message = ref('');


// Hàm format ngày yyyy-mm-dd để input date nhận
const formatDateOnly = (isoString) => {
  if (!isoString) return '';
  return isoString.split('T')[0];
};


// Map status backend <-> frontend
const mapStatusToLabel = (status) => {
  return status === 'ACTIVE' ? 'Hiển thị' : 'Ẩn';
};
const mapLabelToStatus = (label) => {
  return label === 'Hiển thị' ? 'ACTIVE' : 'INACTIVE';
};


onMounted(async () => {
  try {
    const res1 = await axios.get('/api/products/categories');
    const res2 = await axios.get('/api/products/suppliers');
    categories.value = res1.data;
    suppliers.value = res2.data;


    const productRes = await axios.get(`/api/products/${productId}`);
    const existingProduct = productRes.data;


    product.value = {
      productId: existingProduct.product_id,  // backend trả về snake_case
      name: existingProduct.name,
      quantity: existingProduct.quantity,
      price: existingProduct.stock_price,
      stock_price: existingProduct.stock_price,  // giá nhập
      sell: existingProduct.sell,
      img: existingProduct.img,
      expirationDate: formatDateOnly(existingProduct.expiration_date),
      categoryId: existingProduct.category?.categoryId?.toString() ?? '',
      supplierId: existingProduct.supplier?.supplierId?.toString() ?? '',
      status: mapStatusToLabel(existingProduct.status),
      displayOrder: existingProduct.displayOrder || 0
    };
  } catch (error) {
    console.error("Lỗi khi tải dữ liệu:", error.response?.data || error);
    message.value = 'Không thể tải dữ liệu sản phẩm hoặc danh mục!';
  }
});


const updateProduct = async () => {
  console.log('==== Bắt đầu cập nhật sản phẩm ====');
  console.log('Dữ liệu gửi lên server:', {
    product_id: product.value.productId,
    name: product.value.name,
    quantity: product.value.quantity,
    price: product.value.stock_price,
    expiration_date: product.value.expirationDate,
    sell: product.value.sell,
    img: product.value.img,
    category_id: product.value.categoryId,
    supplier_id: product.value.supplierId,
    status: mapLabelToStatus(product.value.status),
    displayOrder: product.value.displayOrder
  });


  if (!product.value.name || !product.value.categoryId || !product.value.supplierId) {
    message.value = 'Vui lòng điền đầy đủ các trường bắt buộc!';
    console.log('Lỗi: Thiếu trường bắt buộc');
    return;
  }


  try {
    const response = await axios.put(`/api/products/${productId}`, {
      product_id: product.value.productId,
      name: product.value.name,
      quantity: product.value.quantity,
      price: product.value.stock_price,
      expirationDate: product.value.expirationDate,
      sell: product.value.sell,
      img: product.value.img,
      category_id: product.value.categoryId,
      supplier_id: product.value.supplierId,
      status: mapLabelToStatus(product.value.status),
      displayOrder: product.value.displayOrder
    },
    {
  headers: {
    'Content-Type': 'application/json'
  }
});


    console.log('Server trả về sau khi cập nhật:', response.data);
    message.value = 'Sản phẩm đã được cập nhật';
    router.push('/ProductCatalog');
  } catch (error) {
    console.error('Lỗi khi cập nhật sản phẩm:', error.response?.data || error);
    message.value = 'Cập nhật sản phẩm thất bại!';
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
          <div class="form-container">
            <h2>Chỉnh sửa sản phẩm</h2>
            <div v-if="message" class="message">{{ message }}</div>
            <div class="form-grid">
              <!-- Left -->
              <div class="left-form">
                <div class="form-group">
                  <label>Danh mục <span class="required">*</span></label>
                  <select v-model="product.categoryId">
                    <option disabled value="">Chọn danh mục</option>
                    <option v-for="cat in categories" :key="cat.categoryId" :value="String(cat.categoryId)">
                      {{ cat.name }}
                    </option>
                  </select>
                </div>


                <div class="form-group">
                  <label>Tên sản phẩm <span class="required">*</span></label>
                  <input v-model="product.name" type="text" />
                </div>


                <div class="form-group">
                  <label>Giá nhập</label>
                  <input type="number" v-model="product.stock_price" />
                </div>
                <!-- <div class="form-group">
                  <label>Giá bán (tự động tính từ giá nhập)</label>
                  <span class="readonly-input">{{ product.price.toLocaleString() }} đ</span>
                </div> -->


                <div class="form-group">
                  <label>Số lượng</label>
                  <input type="number" v-model="product.quantity" />
                </div>


                <div class="form-group">
                  <label>Mã sản phẩm</label>
                  <span class="readonly-input">{{ productId }}</span>
                </div>


                <div class="form-group">
                  <label>Ngày hết hạn</label>
                  <input v-model="product.expirationDate" type="date" />
                </div>
              </div>


              <!-- Right -->
              <div class="right-form">
                <div class="form-group">
                  <label>Hình đại diện (600x400)</label>
                  <input type="text" v-model="product.img" placeholder="URL ảnh hoặc chọn ảnh" />
                  <div v-if="product.img" class="image-preview">
                    <img :src="product.img" alt="Ảnh sản phẩm" style="width: 100%; max-width: 300px; margin-top: 10px;" />
                  </div>
                </div>


                <div class="form-group">
                  <label>Nhà cung cấp <span class="required">*</span></label>
                  <select v-model="product.supplierId">
                    <option disabled value="">Chọn nhà cung cấp</option>
                    <option v-for="sup in suppliers" :key="sup.supplierId" :value="String(sup.supplierId)">
                      {{ sup.name }}
                    </option>
                  </select>
                </div>
              </div>
            </div>


            <!-- Buttons -->
            <div class="button-group">
              <router-link to="/ProductCatalog" class="back-button">Quay lại</router-link>
              <button @click="updateProduct">Lưu thay đổi</button>
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
  font-family: Arial, Helvetica, sans-serif;
}
.page-main {
  display: flex;
  flex-direction: column;
  margin-top: 6%;
  padding: 0px 30px;
  background-color: #f5f5f5;
}
.form-container {
  padding: 30px;
  font-family: Arial, sans-serif;
  background-color: #fff;
}
h2 {
  color: #0884f1;
  margin-bottom: 20px;
}
.message {
  margin-top: 20px;
  padding: 10px;
  background-color: #f2f2f2;
  color: #333;
  border: 1px solid #ccc;
  border-radius: 5px;
  text-align: center;
  font-weight: bold;
}
.form-grid {
  display: flex;
  gap: 30px;
}
.left-form,
.right-form {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 15px;
}
.form-group {
  display: flex;
  flex-direction: column;
}
.form-group label {
  font-weight: bold;
  margin-bottom: 5px;
}
.form-group input,
.form-group select {
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
}
.required {
  color: red;
}
.image-preview img {
  border-radius: 4px;
  box-shadow: 0 0 8px rgba(0, 0, 0, 0.1);
}
button {
  margin-top: 20px;
  background-color: #0884f1;
  color: white;
  padding: 10px 20px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}
button:hover {
  background-color: #279aff;
}
.button-group {
  display: flex;
  justify-content: space-between;
}
.back-button {
  margin-top: 20px;
  background-color: #0884f1;
  color: white;
  padding: 10px 20px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}
.back-button:hover {
  background-color: #279aff;
}
a {
  text-decoration: none;
}
.readonly-input {
  display: inline-block;
  width: 100%;
  padding: 6px 12px;
  font-size: 1rem;
  line-height: 1.5;
  color: #495057;
  background-color: #fff;
  border: 1px solid #ced4da;
  border-radius: 4px;
  box-sizing: border-box;
  user-select: text;
}
</style>



