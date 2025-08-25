<script setup>
import MainPage from '../../components/Mainpage.vue';
import TheHeader from '../../components/TheHeader.vue';
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';

axios.defaults.baseURL = 'http://localhost:8083';
const router = useRouter();

const product = ref({
  name: '',
  price: 0,
  expiration_date: '',
  sell: 0,
  img: '',
  categoryId: '',
  supplierId: '',
});

const categories = ref([]);
const suppliers = ref([]);
const message = ref('');
const existingProducts = ref([]);

onMounted(async () => {
  const [res1, res2, res3] = await Promise.all([
    axios.get('/api/products/categories'),
    axios.get('/api/products/suppliers'),
    axios.get('/api/products'),
  ]);
  categories.value = res1.data;
  suppliers.value = res2.data;
  existingProducts.value = res3.data;
});

const checkProduct = () => {
  const existing = existingProducts.value.find(
    (prod) =>
      prod.name === product.value.name &&
      prod.category?.categoryId === product.value.categoryId &&
      prod.supplier?.supplierId === product.value.supplierId
  );

  if (existing) {
    message.value = 'Sản phẩm đã tồn tại. Vui lòng kiểm tra lại!';
  } else {
    message.value = '';
  }
};

const saveProduct = async () => {
  const payload = {
    product_id: product.value.productId,
    name: product.value.name,
    quantity: 0, // ✔ Số lượng mặc định là 0
    price: product.value.price,
    category_id: product.value.categoryId,
    supplier_id: product.value.supplierId,
    sell: product.value.sell,
    img: product.value.img,
    expirationDate: product.value.expiration_date,
  };

  try {
    await axios.post('/api/products/importProduct', payload);
    message.value = 'Sản phẩm đã được thêm thành công!';
    router.push('/importGoodList');
  } catch (error) {
    console.error('Lỗi khi thêm sản phẩm:', error);
    message.value = 'Thêm sản phẩm thất bại!';
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
            <h2>Thêm sản phẩm mới</h2>
            <div v-if="message" class="message">{{ message }}</div>
            <div class="form-grid">
              <div class="left-form">
                <div class="form-group">
                  <label>Danh mục <span class="required">*</span></label>
                  <select v-model="product.categoryId" @change="checkProduct">
                    <option disabled value="">Chọn danh mục</option>
                    <option v-for="cat in categories" :key="cat.categoryId" :value="cat.categoryId">
                      {{ cat.name }}
                    </option>
                  </select>
                </div>

                <div class="form-group">
                  <label>Tên sản phẩm <span class="required">*</span></label>
                  <input v-model="product.name" type="text" @change="checkProduct" />
                </div>

                <div class="form-group">
                  <label>Giá nhập</label>
                  <input type="number" v-model="product.price" />
                </div>
              </div>

              <div class="right-form">
                <div class="image-upload form-group">
                  <label>Hình đại diện (600x400)</label>
                  <input type="text" v-model="product.img" placeholder="URL ảnh hoặc chọn ảnh" />
                </div>
                <div class="form-group">
                  <label>Ngày hết hạn</label>
                  <input v-model="product.expiration_date" type="date" />
                </div>
                <div class="form-group">
                  <label>Nhà cung cấp</label>
                  <select v-model="product.supplierId" @change="checkProduct">
                    <option disabled value="">Chọn nhà cung cấp</option>
                    <option v-for="sup in suppliers" :key="sup.supplierId" :value="sup.supplierId">
                      {{ sup.name }}
                    </option>
                  </select>
                </div>
              </div>
            </div>
            <div class="button-group">
              <router-link to="/ProductCatalog" class="back-button">Quay lại</router-link>
              <button @click="saveProduct">Lưu sản phẩm</button>
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
  position: relative;
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

.image-upload input {
  padding: 8px;
  width: 100%;
  border: 1px solid #ccc;
  border-radius: 4px;
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
</style>
