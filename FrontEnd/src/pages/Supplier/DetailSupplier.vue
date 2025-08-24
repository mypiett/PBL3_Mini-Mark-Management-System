<script setup>
import Mainpage from '../../components/Mainpage.vue'
import TheHeader from '../../components/TheHeader.vue';
import Toast from '../../components/Toast.vue';
const toastRef = ref();
import { ref, computed, onMounted} from 'vue';
import { useRoute } from 'vue-router';
import axios from 'axios';

const suppliers = ref([]);
const products = ref([]);
const route = useRoute();
const supplierId = route.query.supplier_id;
const supplier = ref({
  name: '',
  phone: '',
  address: ''
});


const getSupplierById = async () =>{
  try{
    const response = await axios.get(`http://localhost:8083/api/suppliers/${supplierId}`);
    supplier.value = response.data;
    // alert("Lấy thông tin nhà cung cấp thành công");
  }catch (error){
    console.error('Lỗi khi lấy thông tin nhà cung cấp:', error);
    // alert('Lỗi khi lấy thông tin nhà cung cấp');
    toastRef.value.show('Lỗi khi lấy thông tin nhà cung cấp', 'error');
  }
}

const fetchProducts = async () => {
  try {
    const response = await axios.get(`http://localhost:8083/api/products/supplier/${supplierId}`);
    console.log("Dữ liệu sản phẩm nhận được:", response.data);
    products.value = response.data;
  } catch (error) {
    console.error('Lỗi khi tải dữ liệu nhà cung cấp:', error);
    // alert('Lỗi khi tải dữ liệu sản phẩm');
    toastRef.value.show('Lỗi khi tải dữ liệu sản phẩm', 'error');

  }
};

onMounted(() => {
  fetchProducts();
  getSupplierById();
});

</script>


<template>
    <div class="temp">
        <Mainpage />
        <div class="main-right">
            <TheHeader />
            <div class="container">
                <div class="header2">
                    <h3>Nhà cung cấp</h3>
                </div>
                <div class="add-supplier-form">
                      <div>
                        <label for="supplier_id">Id:</label>
                        <input v-model="supplier.supplier_id" type="text" id="supplier_id" disabled />
                      </div>
                      <div>
                        <label for="name">Tên nhà cung cấp :</label>
                        <input v-model="supplier.name" type="text" id="name" disabled/>
                      </div>
                      <div>
                        <label for="phone">Số điện thoại:</label>
                        <input v-model="supplier.phone" type="text" id="phone" disabled />
                      </div>
                      <div>
                        <label for="address">Địa chỉ:</label>
                        <input v-model="supplier.address" type="text" id="address" disabled />
                      </div>
                </div>
                <div class="white-container">
                    <table class="supplier-table">
                        <thead>
                          <tr>
                            <th>Sản phẩm</th>
                            <th>Số lượng</th>
                            <th>Giá nhập</th>
                            <!-- <th>Loại</th> -->
                            <th>Hạn sử dụng</th>
                            <!-- <th>Thao tác</th> -->
                          </tr>
                        </thead>
                        <tbody>
                          <tr v-for="(product, index) in products" :key="product.product_id">
                            <td class="product display-jus-ali" style="justify-content: flex-start;">
                              <img :src="product.img" alt="product image" class="product-img">
                              <span>{{ product && product.name ? product.name : 'Tên sản phẩm không có' }}</span>
                            </td>
                            <td>{{ product.quantity }}</td>
                            <td>{{ product.price.toLocaleString() }} đ</td>
                            <!-- <td>{{ product.category && product.category.name ? product.category.name : 'Loại không có' }}</td> -->
                            <td>{{ product.expiration_date }}</td>
                          </tr>
                        </tbody>
                    </table>                    
                </div>
            </div>
        </div>
    </div>
    <Toast ref="toastRef" />
</template>

<style scoped>
body{
    width: 100%;
    height:100%;
    /* font-size: 20px;
    font-family:'Noto Sans', sans-serif; */
}

.temp{
    position:absolute;
    width: 100%;
    height:100%;
    /* font-size: 20px; */
    top:0;
    left:0;
    margin: 0;
    padding:0;
    font-family:'Noto Sans', sans-serif;
    display:flex;
}

.main-right{
    margin-left: 18%;
    top:0;
    background-color: #f5f5f5;
    flex:1;
    display:flex;
    flex-direction: column;
}

.container{
    width: 96%;
    background-color: none;
    display: flex;
    flex-direction: column;
    gap:10px;
    margin-top:90px;
    margin-left:2%;
}

.header2{
    width: 100%;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.8); 
  backdrop-filter: blur(2px);
  z-index: 999;
}

/* form */
.add-supplier-form {
  /* position: fixed; */
  /* top: 50%; */
  /* left: 60%; */
  /* transform: translate(-50%, -50%); */
  max-height: 90vh;
  overflow-y: auto; 
  background: #f9f9f9;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.3);
  /* z-index: 1000;  */
  /* width: 50%; */
  box-sizing: border-box;
}

.add-supplier-form input,
.add-supplier-form select {
  width: 100%;
  padding: 8px;
  margin: 10px 0;
  border: 1px solid #ccc;
  border-radius: 5px;
}

.add-supplier-form button {
  background-color: #4CAF50;
  color: white;
  padding: 10px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  margin-top: 10px;
}

.add-supplier-form button[type="button"] {
  background-color: #f44336; /* Nút hủy */
}

.add-supplier-form button:hover {
  background-color: #45a049;
}

.add-supplier-form button[type="button"]:hover {
  background-color: #e53935;
}

.white-container{
    background-color: white;
    width: 100%;
    height:100vh;
    border-radius: 5px;
    box-shadow: 5px 10px 15px 0px rgba(0, 0, 0, 0.2);
    padding: 10px;
    box-sizing: border-box;
    display:flex;
    flex-direction: column;
    gap:20px;
}

.add-button{
    background-color: #0088ff;
    padding:10px 20px 10px 20px;
    /* padding:10px; */
    border-color: transparent;
    border-radius: 5px;
    font-weight: 600;
    color:white;
    cursor: pointer;
}

.search-name{
    display:flex;
    width: 40%;
    background-color:white;
    padding: 5px 10px;
    border-color: #828282;
    border-style: solid;
    border-width:1px;
    border-radius: 5px;
    align-items: center;
}

.search-box{
    width: 100%;
    font-size: 15px;
    outline: none;
    border-color: transparent;
    background-color: white;
}

.list-container{
    display:flex;
    width: 50%;
    background-color:white;
    /* padding: 5px 10px; */
    border-color: #828282;
    border-style: solid;
    border-width:1px;
    border-radius: 5px;
    align-items: center;
}

.list-box{
    width: 100%;
    font-size: 15px;
    outline: none;
    border-width: 1px;
    padding: 5px 10px;
    border-color:transparent #828282 transparent transparent;
    background-color: white;
    border-radius: 5px 0 0 5px;
    display: flex;
    justify-content: space-around;
    align-items: center;
}

.icon{
    font-size: 15px;
    color:#828282;
}

.supplier-table {
  width: 100%;
  border: 1px solid #d1d5db; 
  border-collapse: collapse;
  font-size: 16px;
}

.supplier-table thead {
  background-color: #f3f4f6; 
}

.supplier-table th,
.supplier-table td {
  border: 1px solid #d1d5db;
  padding: 8px 16px; 
  text-align: left;
}

/* Nút "Sửa" */
.btn-edit {
  background-color: #3b82f6; 
  color: white;
  padding: 4px 12px;
  border: none;
  border-radius: 5px;
  margin-right: 8px;
  cursor: pointer;
}

/* Nút "Xóa" */
.btn-delete {
  background-color: #ef4444;
  color: white;
  padding: 4px 12px;
  border: none;
  border-radius: 5px;
  margin-right: 8px;
  cursor: pointer;
}

.btn-view{
  background-color: darkorange;
  color: white;
  padding: 4px 12px;
  border: none;
  border-radius: 5px;
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

.button-group {
  display: flex;
  gap: 20px;
  margin-top: 10px;
}

.display-jus-ali {
  display: flex;
  justify-content: center;
  align-items: center;
  border-color: transparent;
}

.product{
    width: 92.5%;
    color:#0884f1;
    font-weight: 400;
    border-color: transparent;
    gap: 10px;
    justify-content: flex-start;
    display: flex;
    align-items: center;
}
.date-of-creation {
    justify-content: center;
    display: flex;
    align-items: center;
}
.actions {
    justify-content: center;
    display: flex;
    align-items: center;
    width: 15%;
    white-space: nowrap;
    margin-left: 20px;
}
.product-img{
    border-radius: 5px;
    border: 1px solid rgb(148, 148, 148);
    padding: 2px;
    width: 50px;
    height: 50px;
    background-size: cover; 
    background-position: top;
    background-repeat: no-repeat;
}
.header-row {
    font-weight: bold;
    background-color: #f4f4f4;
}
.sell{
    width: 10%;
}
.type{
    width: 15%;
}
.trademark{
    width: 15%;
}
.date-of-creation{
    width: 20%;
}
</style>