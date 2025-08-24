<script setup>
import { ref, onMounted, onUnmounted, computed, watch, defineProps} from 'vue';
import axios from 'axios';
const showTypeDropdown = ref(false);
const typeRef = ref(null);
const selectedType = ref('');


const showTagDropdown = ref(false);
const tagRef = ref(null);
const selectedTag = ref('');


const showSupplierDropdown = ref(false);
const supplierRef = ref(null);
const selectedSupplier = ref('');


const showFilterDropdown = ref(false);
const filterRef = ref(null);
const selectedFilter= ref('');


const isFilterChanged = ref(false);
const selectedProduct = ref([]);
const searchQuery = ref('');


const tempSelectedType = ref('');
const tempSelectedTag = ref('');
const tempSelectedSupplier = ref('');
const tempSelectedFilter = ref('');


const productTypes = ref([]);
const categories = ref([]);
const tags = ['Bán chạy', 'Hết hàng', 'Khuyến mãi', 'Hàng mới'];
const suppliers = ref([]);
const supplier_first = ref([]);
const filters = ['Còn hàng', 'Sắp hết hàng'];
const products = ref([]);
const message = ref('');
const filterPriority = ref('');
onMounted(() => {
  axios.get('http://localhost:8083/api/products')
    .then(response => {
      const all = Array.isArray(response.data) ? response.data : response.data.data || [];
      products.value = all.filter(p => p.status !== 'DRAFT'); // 👉 bỏ sản phẩm DRAFT
    })
    .catch(error => {
      console.error('Lỗi khi gọi API:', error);
      products.value = [];
    });

  axios.get('http://localhost:8083/api/products/categories')
    .then(response => {
      categories.value = response.data;
      productTypes.value = categories.value.map(category => category.name);
    })
    .catch(error => {
      console.log('Lỗi khi get category: ', error);
      productTypes.value = [];
    });

  axios.get('http://localhost:8083/api/products/suppliers')
    .then(response => {
      supplier_first.value = response.data;
      suppliers.value = supplier_first.value.map(supplier => supplier.name);
    })
    .catch(error => {
      console.log('Lỗi khi get supplier: ', error);
      suppliers.value = [];
    });
});





const currentPage = ref(1);
const itemsPerPage = 20;


const props = defineProps({
    mode:{
        type: String,
        default: 'normal'
    }
});


function toggleType(event){
    event.stopPropagation();
    showTypeDropdown.value = !showTypeDropdown.value;
    showTagDropdown.value = false;
    showSupplierDropdown.value = false;
    showFilterDropdown.value = false;
}
function handleClickType(event){
    if (typeRef.value && !typeRef.value.contains(event.target)){
        showTypeDropdown.value = false;
    }
}


function toggleTag(event){
    event.stopPropagation();
    showTagDropdown.value = !showTagDropdown.value;
    showTypeDropdown.value = false;
    showSupplierDropdown.value = false;
    showFilterDropdown.value = false;
}
function handleClickTag(event){
    if(tagRef.value && !tagRef.value.contains(event.target)){
        showTagDropdown.value = false;
    }
}


function toggleSupplier(event){
    event.stopPropagation();
    showSupplierDropdown.value = !showSupplierDropdown.value;
    showTypeDropdown.value = false;
    showTagDropdown.value = false;
    showFilterDropdown.value = false;
}
function handleClickSupplier(event){
    if (supplierRef.value && !supplierRef.value.contains(event.target)){
        showSupplierDropdown.value = false;
    }
}


function toggleFilter(event){
    event.stopPropagation();
    showFilterDropdown.value = !showFilterDropdown.value;
    showTypeDropdown.value = false;
    showTagDropdown.value = false;
    showSupplierDropdown.value = false;
}
function handleClickFilter(event){
    if (filterRef.value && !filterRef.value.contains(event.target)){
        showFilterDropdown.value = false;
    }
}


function selectFilter(type, value) {
  filterPriority.value = type;
  switch (type) {
    case 'type':
      tempSelectedType.value = value;
      break;
    case 'tag':
      tempSelectedTag.value = value;
      break;
    case 'supplier':
      tempSelectedSupplier.value = value;
      break;
    case 'filter':
      tempSelectedFilter.value = value;
      break;
  }
}




function hasTag(product, tag){
    switch(tag){
        case 'Bán chạy':
            return product.sell > 500;
        case 'Hết hàng':
            return product.quantity < 20;
        case 'Khuyến mãi':
            return product.discount === true;
        case 'Hàng mới':
            return product.isNew === true;
        default:
            return true;
    }
}
function applyFilter() {
  isFilterChanged.value = true;
  selectedType.value = tempSelectedType.value;
  selectedTag.value = tempSelectedTag.value;
  selectedSupplier.value = tempSelectedSupplier.value;
  selectedFilter.value = tempSelectedFilter.value;
}


const filterProducts = computed(() => {
  const productList = Array.isArray(products.value) ? products.value : [];


  const matchSearch = (product) =>
    !searchQuery.value ||
    product.name?.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
    product.code?.toLowerCase().includes(searchQuery.value.toLowerCase());


  const filters = {
    type: selectedType.value ? (p) => p.category?.name === selectedType.value : null,
    tag: selectedTag.value ? (p) => hasTag(p, selectedTag.value) : null,
    supplier: selectedSupplier.value ? (p) => p.supplier?.name === selectedSupplier.value : null,
    filter: selectedFilter.value ? (p) => applyCustomFilter(p, selectedFilter.value) : null
  };


  if (!selectedType.value && !selectedTag.value && !selectedSupplier.value && !selectedFilter.value) {
    return productList.filter(matchSearch);
  }


  // Nếu user chọn ưu tiên điều kiện nào, chỉ dùng điều kiện đó
  if (filterPriority.value && filters[filterPriority.value]) {
    return productList.filter(p => matchSearch(p) && filters[filterPriority.value](p));
  }


  // Không có ưu tiên rõ ràng → thử từng cái (fallback)
  for (const key in filters) {
    const fn = filters[key];
    if (!fn) continue;
    const result = productList.filter(p => matchSearch(p) && fn(p));
    if (result.length > 0) return result;
  }


  return [];
});








function formatDate(dateString) {
  if (!dateString) return 'Không có';
  const date = new Date(dateString);
  if (isNaN(date)) return 'Không hợp lệ';
  return date.toLocaleDateString('vi-VN'); // Hoặc dùng format khác nếu muốn
}
const displayEdit = computed(() => {
  return props.mode !== 'desc';
});
const displayEdit_2 = computed (() =>{
    return props.mode !== 'priceList';
});
const displayEdit_3 = computed(() => {
    return props.mode !== 'importGoods';
});
const pagedProducts = computed(() => {
  const today = new Date();
  const in15Days = new Date();
  in15Days.setDate(today.getDate() + 15);


  let filtered;
if (props.mode === 'importGoods') {
  filtered = products.value.filter(u => {
    if (!u.expirationDate) {
      return false;
    }
    const expiry = new Date(u.expirationDate);
    if (isNaN(expiry)) {
      return false;
    }


    const isLowStock = u.quantity < 30;
    const isNearOrPastExpiry = expiry.getTime() <= in15Days.getTime();


    // Chỉ cần thỏa mãn một trong hai điều kiện
    return isLowStock || isNearOrPastExpiry;
  });
} else {
  filtered = filterProducts.value;
}


  let sorted;
  if (props.mode === 'desc') {
    sorted = [...filtered].sort((a, b) => b.quantity - a.quantity);
  } else {
    sorted = [...filtered];
  }


  const start = (currentPage.value - 1) * itemsPerPage;
  const end = start + itemsPerPage;
  return sorted.slice(start, end);
});


const totalItems = computed(() => {
  const today = new Date();
  const in15Days = new Date();
  in15Days.setDate(today.getDate() + 15);


  if (props.mode === 'importGoods') {
    return products.value.filter(u => {
      if (!u.expirationDate) return false;
      const expiry = new Date(u.expirationDate);
      if (isNaN(expiry)) return false;


      const isLowStock = u.quantity < 30;
      const isNearOrPastExpiry = expiry.getTime() <= in15Days.getTime();


      return isLowStock || isNearOrPastExpiry;
    }).length;
  } else {
    return filterProducts.value.length;
  }
});


const totalPages = computed(() => {
  return Math.ceil(totalItems.value / itemsPerPage);
});


function changePage(page) {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page;
  }
}


watch(
  [searchQuery, selectedType, selectedTag, selectedSupplier, selectedFilter],
  () => {
    currentPage.value = 1;
  }
);


const deleteProduct = async (productId) => {
    try {
        const response = await axios.delete(`http://localhost:8083/api/products/${productId}`);
       
        if (response.status === 200) {
            message.value = 'Sản phẩm đã được xóa.';
            // Gọi lại API để cập nhật danh sách sản phẩm
            fetchProducts();
        }
    } catch (error) {
        console.error("Có lỗi xảy ra khi xóa sản phẩm:", error);
       
        if (error.response && error.response.status === 404) {
            message.value = 'Sản phẩm không tồn tại.';
        } else {
            message.value = 'Không thể xóa sản phẩm.';
        }
    }
};


// Hàm gọi API để lấy lại danh sách sản phẩm
const fetchProducts = () => {
  axios.get('http://localhost:8083/api/products')
    .then(response => {
      const all = Array.isArray(response.data) ? response.data : response.data.data || [];
      products.value = all.filter(p => p.status !== 'DRAFT'); 
    })
    .catch(error => {
      console.error('Failed to fetch products:', error);
      products.value = [];
    });
};


function applyCustomFilter(product, filter){
    switch(filter){
        case 'Còn hàng':
            return parseInt(product.quantity) > 0;
        case 'Sắp hết hàng':
            return parseInt(product.quantity) < 10;
        default:
            return true;
    }
}


const clearFilters = () => {
  searchQuery.value = '';
  selectedType.value = '';
  selectedTag.value = '';
  selectedSupplier.value = '';
  selectedFilter.value = '';
  currentPage.value = 1;
};


onMounted(() =>{
    document.addEventListener('click', handleClickType);
    document.addEventListener('click', handleClickTag);
    document.addEventListener('click', handleClickSupplier);
    document.addEventListener('click', handleClickFilter);
});
onUnmounted(() =>{
    document.removeEventListener('click', handleClickType);
    document.removeEventListener('click', handleClickTag);
    document.removeEventListener('click', handleClickSupplier);
    document.removeEventListener('click', handleClickFilter);
});


</script>
<template>
    <div v-if="message" class="message">{{ message }}</div>
    <div class="container-part">
        <!-- <div class="title-container">
            <div class="title">
                <span class="underline-text">Tất cả</span>
            </div>
        </div> -->
        <div class="content">
            <div v-if ="displayEdit_3" class="classify">
                <div class="search">
                    <i class="fa-solid fa-magnifying-glass"></i>
                    <input v-model="searchQuery" type="text" placeholder="Tìm kiếm theo mã sản tên sản phẩm" style="padding: 10px">
                </div>
                <div class="informationProduct">
                    <div class="dropdown-wrapper" ref="typeRef">
                        <div class="Sale-Type-Tag" @click="toggleType">
                        <p>Loại sản phẩm</p>
                        <i class="fa-solid fa-caret-down"></i>
                        </div>
                        <div v-if="showTypeDropdown" class="dropdown">
                        <ul>
                            <li v-for="item in productTypes" :key="item" @click="selectFilter('type', item)"
                            :class="{ selected: selectedType === item }">
                            {{ item }}
                            </li>
                        </ul>
                        </div>
                    </div>


                    <!-- <div class="dropdown-wrapper" ref="tagRef">
                        <div class="Sale-Type-Tag" @click="toggleTag">
                        <p>Tag</p>
                        <i class="fa-solid fa-caret-down"></i>
                        </div>
                        <div v-if="showTagDropdown" class="dropdown">
                        <ul>
                            <li v-for="item in tags" :key="item" @click="selectFilter('tag', item)"
                            :class="{ selected: selectedTag === item }">
                            {{ item }}
                            </li>
                        </ul>
                        </div>
                    </div> -->


                    <!-- <div class="dropdown-wrapper" ref="supplierRef">
                        <div class="Sale-Type-Tag" @click="toggleSupplier">
                        <p>Nhà cung cấp</p>
                        <i class="fa-solid fa-caret-down"></i>
                        </div>
                        <div v-if="showSupplierDropdown" class="dropdown">
                        <ul>
                            <li v-for="item in suppliers" :key="item" @click="selectFilter('supplier', item)"
                            :class="{ selected: selectedSupplier === item }">
                            {{ item }}
                            </li>
                        </ul>
                        </div>
                    </div> -->


                    <div class="dropdown-wrapper" ref="filterRef" style="border-right: none;">
                        <div class="Sale-Type-Tag-dis" @click="toggleFilter">
                        <p>Bộ lọc khác</p>
                        <i class="fa-solid fa-filter"></i>
                        </div>
                        <div v-if="showFilterDropdown" class="dropdown">
                        <ul>
                            <li v-for="item in filters" :key="item" @click="selectFilter('filter', item)"
                            :class="{ selected: selectedFilter === item }">
                            {{ item }}
                            </li>
                        </ul>
                        </div>
                    </div>
                </div>
                <div>
                <button :class="['save-filter-btn', isFilterChanged = 'active']" @click="applyFilter">
                    Lưu bộ lọc
                </button>
                <button @click="clearFilters" class="save-filter-btn active">
                    Bỏ bộ lọc
                </button>
                </div>
            </div>
        </div>
        <div class="list-product">
            <table class="product-table">
                <thead>
                <tr>
                    <!-- <th></th> -->
                    <th>Sản phẩm</th>
                    <th>Số lượng</th>
                    <th>Loại</th>
                    <!-- <th v-if="displayEdit_2">Nhà cung cấp</th> -->
                    <th v-if="displayEdit_2">Hạn sử dụng</th>
                    <th v-if="displayEdit_2 && displayEdit">Hành động</th>
                    <th v-if="!displayEdit_2">Giá</th>
                </tr>
                </thead>
                <tbody>
                <tr v-for="product in pagedProducts" :key="product.product_id">
                    <!-- <td><input type="checkbox" :value="product.product_id" v-model="selectedProduct" /></td> -->
                    <td class="product-cell">
                    <img :src="product.img" alt="product image" class="product-img" />
                    <span>{{ product?.name || 'Tên sản phẩm không có' }}</span>
                    </td>
                    <td>{{ product.quantity }}</td>
                    <td>{{ product.category?.name || 'Loại không có' }}</td>
                    <!-- <td v-if="displayEdit_2">{{ product.supplier?.name || 'Không có' }}</td> -->
                    <td v-if="displayEdit_2">{{ formatDate(product.expirationDate) }}</td>
                    <td v-if="displayEdit_2 && displayEdit">
                        <div class="action-buttons">
                            <router-link :to="`/editProduct/${product.product_id}`" class="edit-btn">Chỉnh sửa</router-link>
                            <!-- <button @click="deleteProduct(product.product_id)" class="delete-btn">Xóa</button> -->
                        </div>
                    </td>
                    <td v-if="!displayEdit_2">{{ product.price }}</td>
                </tr>
                </tbody>
            </table>
            </div>

        <div class="fitPage">
            <span>
                Từ {{ (currentPage - 1) * itemsPerPage + 1 }} đến
                {{ Math.min(currentPage * itemsPerPage, totalItems) }}
                trên tổng {{ totalItems }}
            </span>
            <div class="pagination">
                <button @click="changePage(currentPage - 1)" :disabled="currentPage === 1">‹</button>
                <button v-for="page in totalPages" :key="page"
                :class="{ active: page === currentPage }"
                @click="changePage(page)">
                {{ page }}
                </button>
                <button @click="changePage(currentPage + 1)" :disabled="currentPage === totalPages">›</button>
            </div>
        </div>
    </div>
</template>


<style scoped>
.container-part{
    display: flex;
    flex-direction: column;
    background-color: #ffffff;
    border-radius: 5px;
    border: 1px solid rgb(210, 210, 210);
    box-shadow: rgb(196, 196, 196);
    padding: 10px;
}
.title-container{
    width: 100%;
    border-bottom: rgb(210, 210, 210);
}
.title{
    box-sizing: border-box;
    color: #0884f1;
    padding: 5px;
    border-bottom: 1px solid rgb(210, 210, 210);
}
.underline-text {
    border-bottom: 2px solid #0884f1;
    padding: 5px;
}
.content{
    display: flex;
    flex-direction: column;
    justify-content:space-between;
}
.classify {
    padding: 15px 10px;
    display: flex;
    justify-content: space-between;
    align-items: stretch;
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
.search {
    display: flex;
    padding: 15px;
    border-radius: 5px;
    border: 1px solid  rgb(148, 148, 148);
    color: rgb(100, 100, 100);
    gap: 8px;
    width: 35%;
    height: 25px;
    display: flex;
    align-items: center;
}
.search input{
    border: none;
    outline: none;
    background: transparent;
    width: 100%;
    padding: 10px;
}
.informationProduct {
    display: flex;
    border-radius: 8px;
    border: 1px solid  rgb(148, 148, 148);
    height: 100%;
    height: 35px;
    font-weight: 300;
    align-items: center;
}


.Sale-Type-Tag,
.Sale-Type-Tag-dis {
    padding: 5px 12px;
    display: flex;
    align-items: center;
    gap: 5px;
    cursor: pointer;
    white-space: nowrap;
    transition: background-color 0.3s ease;
    box-sizing: border-box;
    transition: all 0.2s ease;
}
.Sale-Type-Tag i,
.Sale-Type-Tag-dis i {
    color: rgb(148, 148, 148);
    font-size: 12px;
}
.dropdown-wrapper {
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  border-right: 1px solid rgb(200, 200, 200);
  transition: all 0.2s ease;
}
.dropdown-wrapper:hover{
    background-color: #f0f0f0;
}
.dropdown-wrapper:active{
    transform: scale(0.95);
}
.dropdown {
  width: auto;
  position: absolute;
  top: 100%;
  left: 0;
  background-color: white;
  border: 1px solid rgb(200, 200, 200);
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
  border-radius: 5px;
  margin-top: 5px;
  z-index: 10;
  min-width: 150px;
}
.dropdown ul{
    list-style: none;
    padding: 5px 0;
    margin: 0;
}
.dropdown li{
    padding: 8px 16px;
    cursor: pointer;
    transition: background-color 0.2s ease;
}
.dropdown li:hover{
    background-color: #f5f5f5;
}
.dropdown li.selected {
  background-color: #f5f5f5;
}
.save-filter-btn{
    margin-left: 12px;
    height: 35px;
    padding: 6px 16px;
    border: none;
    border-radius: 5px;
    font-weight: 500;
    cursor: pointer;
    transition: background-color 0.3s ease;
}
.save-filter-btn.inactive{
    background-color: #81c1fa;
    color: rgb(200, 200, 200);
}
.save-filter-btn.active{
    background-color: #0884f1;
    color: white;
}
.save-filter-btn:active{
    transform: scale(0.95);
}
.list-product{
    display: flex;
    flex-direction: column;
    padding-left: 8px;
    padding-right: 8px;
}
.list-product-row {
  display: grid;
  grid-template-columns: 50px 20% 12% 15% 15% 20% auto;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #e0e0e0;
}
.display-jus-ali {
    display: flex;
    justify-content: center;
    align-items: center;
}
.checkbox{
    width: 5%;
    border: 1px solid rgb(148, 148, 148);
}
.product{
    width: 30%;
    color:#0884f1;
    font-weight: 400;
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
.product-table {
  width: 100%;
  border-collapse: collapse;
   border: 1px solid #ccc;
  padding: 12px;
}

.product-table th, .product-table td {
  padding: 12px;
  /* border: none; */
  border: 1px solid #ccc;
  vertical-align: middle;
  text-align: left;
}

.product-cell {
  display: flex;
  align-items: center;
  gap: 10px;
  text-align: left;
}

.product-img {
  width: 40px;
  height: 40px;
  object-fit: cover;
  vertical-align: middle;
  /* border: 1px solid #ccc; */
  border-radius: 4px;
}



/* Bọc nút hành động gọn gàng */
.action-buttons {
  display: flex;
  gap: 8px;
  justify-content: center;
  align-items: center;
}

/* Nút nhỏ gọn */
.edit-btn,
.delete-btn {
  padding: 5px 8px;
  font-size: 13px;
  border: none;
  border-radius: 4px;
  color: white;
  text-decoration: none;
  cursor: pointer;
  min-width: 70px;
  text-align: center;
}

.edit-btn {
  background-color: #007bff;
}

.delete-btn {
  background-color: #dc3545;
}

.edit-btn:hover,
.delete-btn:hover {
  opacity: 0.8;
}


.product-img{
    border-radius: 5px;
    /* border: 1px solid rgb(148, 148, 148); */
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
    width: 15%;
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
.fitPage{
    margin-top: 20px;
    padding: 10px;
    display: flex;
    justify-content: space-between;
    align-items: center;
}
.pagination{
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 5px;
    transition: all 0.2s ease;
}
.pagination button.active {
    background-color: #007bff;
    color: white;
}
.action-buttons {
    display: flex;
    gap: 10px;
    align-items: center;
}
.no-product {
  padding: 20px;
  text-align: center;
  color: #888;
  font-size: 16px;
  font-style: italic;
}


.edit-btn, .delete-btn {
    padding: 5px 10px;
    cursor: pointer;
    border: none;
    border-radius: 4px;
    background-color: #007bff;
    color: white;
    text-decoration: none;
}


.delete-btn {
    background-color: #dc3545;
}


.edit-btn:hover, .delete-btn:hover {
    opacity: 0.8;
}
</style>

