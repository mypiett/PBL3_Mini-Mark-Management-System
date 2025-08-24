<script setup>
import Mainpage from '../../components/Mainpage.vue'
import TheHeader from '../../components/TheHeader.vue';
import Toast from '../../components/Toast.vue';
const toastRef = ref();
import { ref, computed, onMounted} from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';

const suppliers = ref([]);
const searchKeyword = ref('');
const showConfirmModal = ref(null); 
const router = useRouter();

const showAddSupplierForm = ref(false); 
const showEditSupplierForm = ref(false);
const newSupplier = ref({
  name: '',
  phone: '',
  address: ''
});
const isEditMode = ref(false);
const editSupplier = ref({}); 

const fetchSuppliers = async () => {
  try {
    const response = await axios.get('http://localhost:8083/api/suppliers');
    suppliers.value = response.data;
  } catch (error) {
    console.error('Lỗi khi tải dữ liệu nhà cung cấp:', error);
  }
};

const addSupplier = async () => {
  try {
    const response = await axios.post('http://localhost:8083/api/suppliers', newSupplier.value);
    suppliers.value.push(response.data);  
    // alert('Thêm nhà cung cấp thành công!');
    toastRef.value.show('Thêm nhà cung cấp thành công!', 'success');
    showAddSupplierForm.value = false;  
    resetNewSupplier(); 
  } catch (error) {
    console.error('Lỗi khi thêm nhà cung cấp:', error);
    // alert('Lỗi khi thêm nhà cung cấp '); 
    toastRef.value.show('Lỗi khi thêm nhà cung cấp!', 'error');
  }
};

const resetNewSupplier = () => {
  newSupplier.value = {
    name: '',
    phone: '',
    address: ''
  };
};

const openEditForm = (supplier) => {
  editSupplier.value = { ...supplier }; 
  isEditMode.value = true;
  showEditSupplierForm.value = true;
};

const editSupplierById = async () => {
  try{
    const response = await axios.patch(`http://localhost:8083/api/suppliers/${editSupplier.value.supplier_id}`, editSupplier.value);
    const index = suppliers.value.findIndex(supplier => supplier.supplier_id === editSupplier.value.supplier_id);
    if (index !=-1){
      suppliers.value[index] = response.data;
    }
    // alert("Cập nhật thành công");
    toastRef.value.show('Cập nhật nhà cung cấp thành công!', 'success');
    showEditSupplierForm.value = false;
  }catch (error){
    console.error('Lỗi khi cập nhật nhà cung cấp:', error);
    // alert('Lỗi khi cập nhật nhà cung cấp');
    toastRef.value.show('Lỗi khi cập nhật nhà cung cấp!', 'error');
  }
};

const validatePhone = (e) => {
  const value = e.target.value;

  // Nếu có chữ cái → báo lỗi và xóa toàn bộ ô nhập
  if (/[a-zA-Z]/.test(value)) {
    toastRef.value.show("Số điện thoại không được chứa chữ cái!", "warning");
    customerPhone.value = ''; // ← set thành rỗng (null)
    return;
  }

  // Nếu hợp lệ, chỉ giữ số
  customerPhone.value = value.replace(/\D/g, '');
};
const deleteSupplier = async (supplierId) => {
  showConfirmModal.value = supplierId;
};

// Handle delete confirmation
const confirmDelete = async () => {
  if (showConfirmModal.value !== null) {
    const supplierId = showConfirmModal.value;
    try {
      await deleteSupplierById(supplierId);
      showConfirmModal.value = null;  // Close modal after delete
    } catch (error) {
      console.error('Lỗi khi xóa nhà cung cấp:', error);
      // alert('Lỗi khi xóa nhà cung cấp');
      toastRef.value.show('Lỗi khi xóa nhà cung cấp!', 'error');
    }
  }
};

const deleteSupplierById = async (supplierId) => {
  try {
    await axios.delete(`http://localhost:8083/api/suppliers/${supplierId}`);
    suppliers.value = suppliers.value.filter(supplier => supplier.supplier_id !== supplierId);
    // alert('Xóa nhà cung cấp thành công!');
    toastRef.value.show('Xóa nhà cung cấp thành công!', 'success');
  } catch (error) {
    console.error('Lỗi khi xóa nhà cung cấp:', error);
    // alert('Lỗi khi xóa nhà cung cấp');
    toastRef.value.show('Lỗi khi xóa nhà cung cấp!', 'error');
  }
};
// Cancel delete action and close the modal
const cancelDelete = () => {
  showConfirmModal.value = null;  // Close modal without deleting
};

const viewDetail = (supplier_id) =>{
  router.push({
    path: '/detail/SupplierDetail',
    query: {supplier_id: supplier_id}
  });
};



const filteredSuppliers = computed(() => {
  return suppliers.value.filter(e => {
    const matchName = e.name.toLowerCase().includes(searchKeyword.value.toLowerCase());

    return matchName;
  });
});
onMounted(() => {
  fetchSuppliers();
});

</script>


<template>
    <div class="temp">
        <Mainpage />
        <div class="main-right">
            <TheHeader />
            <div class="overlay" v-if="showEditSupplierForm || showAddSupplierForm || showConfirmModal" @click="showEditSupplierForm = false"></div>
            <div class="container">
                <!-- <div class="overlay" v-if="showEditSupplierForm || showAddSupplierForm || showConfirmModal" @click="showEditSupplierForm = false"></div> -->
                <div class="header2">
                    <h3>Nhà cung cấp </h3>
                    <button class="add-button" @click="showAddSupplierForm = true">
                        <font-awesome-icon :icon="['fas', 'circle-plus']" />
                        Thêm nhà cung cấp 
                    </button>
                </div>

                <!-- form add -->
                <div v-if="showAddSupplierForm" class="add-supplier-form">
                  <h4>Thêm nhà cung cấp mới</h4>
                  <form @submit.prevent="addSupplier">
                    <div>
                      <label for="name">Tên nhà cung cấp :</label>
                      <input v-model="newSupplier.name" type="text" id="name" required />
                    </div>
                    <div>
                      <label for="phone" >Số điện thoại:</label>
                      <input
                        v-model="newSupplier.phone"
                        type="text"
                        id="phone"
                        required
                        placeholder="Nhập số điện thoại nhà cung cấp"
                        maxlength="10"
                        @input="validatePhone"
                      />

                    </div>
                    <div>
                      <label for="user_name">Địa chỉ:</label>
                      <input v-model="newSupplier.address" type="text" id="address" required />
                    </div>
                    <div class="button-group">
                      <button type="submit">Lưu</button>
                      <button type="button" @click="showAddSupplierForm = false">Hủy</button>
                    </div>
                  </form>
                </div>

                <div class="white-container">
                    <div class="header2">
                        <div class="search-name">
                            <font-awesome-icon class="icon" :icon="['fas', 'magnifying-glass']" />
                            <input v-model="searchKeyword" type="text" class="search-box" name="search" placeholder="Tìm kiếm nhà cung cấp theo tên">
                        </div>
                    </div>
                    <table class="supplier-table">
                        <thead>
                            <tr>
                            <th>Mã NCC</th>
                            <th>Tên nhà cung cấp</th>
                            <th>SĐT</th>
                            <th>Địa chỉ</th>
                            <th>Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr v-for="(supplier, index) in filteredSuppliers" :key="supplier.supplier_id">
                            <td>{{ supplier.supplier_id }}</td>
                            <td>{{ supplier.name }}</td>
                            <td>{{ supplier.phone }}</td>
                            <td>{{ supplier.address }}</td>
                            <td>
                                <button class="btn-edit" @click="openEditForm(supplier)">Sửa</button>
                                <button class="btn-delete" @click="deleteSupplier(supplier.supplier_id)">Xóa</button>
                                <button class="btn-view" @click="viewDetail(supplier.supplier_id)">Xem</button>
                            </td>
                            </tr>
                        </tbody>
                        
                    </table>
                    <div v-if="showConfirmModal !== null" class="modal-overlay">
                      <div class="modal-content">
                        <h3>Bạn có chắc chắn muốn xóa nhà cung cấp này?</h3>
                        <button @click="confirmDelete" class="btn btn-confirm">Có</button>
                        <button @click="cancelDelete" class="btn btn-cancel">Không</button>
                      </div>
                    </div>


                    <!-- form edit -->
                    <div v-if="showEditSupplierForm" class="add-supplier-form">
                    <h4>Chỉnh sửa thông tin nhà cung cấp </h4>
                    <form @submit.prevent="editSupplierById">
                      <div>
                        <label for="supplier_id">Id:</label>
                        <input v-model="editSupplier.supplier_id" type="text" id="supplier_id" disabled />
                      </div>
                      <div>
                        <label for="name">Tên nhà cung cấp :</label>
                        <input v-model="editSupplier.name" type="text" id="name" required />
                      </div>
                      <div>
                        <label for="phone">Số điện thoại:</label>
                        <input v-model="editSupplier.phone" type="text" id="phone" disabled />
                      </div>
                      <div>
                        <label for="address">Địa chỉ:</label>
                        <input v-model="editSupplier.address" type="text" id="address" required />
                      </div>
                      <div class="button-group">
                        <button type="submit">Lưu</button>
                        <button type="button" @click="showEditSupplierForm = false">Hủy</button>
                      </div>
                    </form>
                  </div>
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
}

.temp{
    position:absolute;
    width: 100%;
    height:100vh;
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

.header2 h3{
    font-size: 24px;
    font-weight: 600;
    color:rgb(50, 50, 50);
}

.overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.8); 
  backdrop-filter: blur(2px);
  z-index: 1000;
}

/* form */
.add-supplier-form {
  position: fixed;
  top: 50%;
  left: 60%;
  transform: translate(-50%, -50%);
  max-height: 90vh;
  overflow-y: auto; 
  background: #f9f9f9;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.3);
  z-index: 1000; 
  width: 50%;
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
    height:auto;
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

.modal-content{
  position: fixed;
  top: 50%;
  left: 60%;
  transform: translate(-50%, -50%);
  max-height: 90vh;
  overflow-y: auto; 
  background: #f9f9f9;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.3);
  z-index: 1000; 
  width: 50%;
  box-sizing: border-box;
}

.btn {
  padding: 10px 20px;
  margin: 0 10px;
  border: none;
  border-radius: 5px;
  color: white;
  font-weight: bold;
  cursor: pointer;
}

.btn-confirm {
  background-color: #28a745; /* xanh lá */
}

.btn-cancel {
  background-color: #dc3545; /* đỏ */
}
</style>