<script setup>
import { defineProps, ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const props = defineProps(['data']);

const isOpened = ref(null);
const user = ref(null);

const clickMenu = (menu) => {
    isOpened.value = isOpened.value === menu ? null : menu;
}

const isAdmin = () => {
  return user.value && user.value.position === 'Quản lý';
};
const goToHome = () => {
  const user = JSON.parse(sessionStorage.getItem('employee') || '{}');
  const role = user.position?.toLowerCase() || '';

  if (role.includes('quản lý')) {
    router.push('/home');
  } else {
    router.push('/adminHome');
  }
};

onMounted(() => {
  const employeeData = sessionStorage.getItem('employee');
  if (employeeData) {
    user.value = JSON.parse(employeeData);
  } else {
    router.push('/login');
  }
});
</script>


<template>
    <div class="nav-menu">
        <img class="logo" src="https://cdn.dribbble.com/userupload/28456975/file/original-923692d84cf3a6905b017e91981ed0af.gif">
        <hr style="border-color: antiquewhite;">
        <div class="menu">
            <a href="#" @click.prevent="goToHome">
                <i class="fa fa-home"></i> Trang chủ
            </a>

            <div class="product" @click.prevent="clickMenu('product')">
                <router-link to="/productCatalog" class="router">
                    <i class="fa fa-shopping-cart"></i> Sản phẩm
                </router-link>
                <div class="hidden-product" v-show="isOpened === 'product'">
                    <router-link to="/productCatalog">
                        Danh sách sản phẩm
                    </router-link>
                    <router-link to="/priceProductList">
                        Bảng giá
                    </router-link>
                </div>
            </div>

            <div class="warehouse_management" @click.prevent="clickMenu('warehouse_management')">
                <a href="#"><i class="fa fa-warehouse"></i> Quản lý kho</a>
                <div class="hidden-warehouse_management" v-show="isOpened === 'warehouse_management'">
                    <router-link to="/inventoryProduct">
                        Tồn kho
                    </router-link>
                    <router-link to="/importGoodList">
                        Danh sách nhập
                    </router-link>
                    <router-link to="/importProduct">
                        Nhập hàng
                    </router-link>
                    <!-- <router-link to="/payProductList">
                        Trả hàng
                    </router-link> -->
                </div>
            </div>

            <div class="bill" @click.prevent="clickMenu('bill')">
                <router-link to="/invoice" class="router">
                    <i class="fa fa-receipt"></i> Hóa đơn
                </router-link>
                <div class="hidden-bill" v-show="isOpened === 'bill'">
                    <router-link to="/createOrder" class="router">
                        <span>Hóa đơn bán hàng</span>
                    </router-link>
                    <router-link to="/invoiceImport" class="router">
                        <span>Hóa đơn nhập hàng</span>
                    </router-link>
                </div>
            </div>

            <div class="customer" @click.prevent="clickMenu('customer')">
                <router-link to="/detail/Customer/Home" class="router">
                    <i class="fa fa-users"></i> Khách hàng
                </router-link>
                <!-- <div class="hidden-customer" v-show="isOpened === 'customer'">
                    <a href="">Khách hàng thân thiết</a>
                    <a href="">Nhóm khách hàng</a>
                </div> -->
            </div>

            <div>
                <router-link to="/detail/Supplier" class="router">
                    <i class="fa fa-truck"></i> Nhà cung cấp
                </router-link>
            </div>

            <div v-if="isAdmin()">
                <router-link to="/detail/Employee" class="router">
                    <i class="fa fa-user-tie"></i> Nhân viên
                </router-link>
            </div>
            <!-- <a href="#"><i class="fa fa-gift"></i> Khuyến mãi</a> -->

            <div v-if="isAdmin()">
              
                <router-link to="/revenueReport" class="router">
                    <i class="fa fa-chart-line"></i> Báo cáo doanh thu
                </router-link>
                <!-- <div class="hidden-report" v-show="isOpened === 'report'">
                    <a href="">Tổng quan báo cáo</a>
                    <a href="">Danh sách báo cáo</a>
                </div> -->
            </div>
                <router-link to="/cashBook" class="router" v-if="isAdmin()">
                    <i class="fa fa-book"></i> Sổ quỹ
                </router-link>
                <router-link to="/theConfiguration" class="router">
                    <i class="fa fa-cogs"></i> Cấu hình
                </router-link>
            </div>
    </div>
</template>

<style scoped>
body{
    width: 100%;
    height: 100%;
    font-family: 'Noto Sans', sans-serif;
}
/* .container{
    margin: 0;
    padding: 0;
    box-sizing: border-box;
    background-color: #f5f5f5;
    height: 100vh;
    width: 100%;
    font-family: 'Noto Sans', sans-serif;
}
.main-section{
    display: flex;
    flex-direction: row;
    position:relative;
    width: 100%;
    height: 100%;
} */
/* .router{
    display: flex;  
    align-items: center; 
    text-align:left;
    padding: 10px;
    color: #f5f5f5;
    font-weight: 400;
    transition: all 0.2s ease-in-out;
    text-decoration: none;
    color: #f5f5f5;
} */
.router{
    padding: 0;
    margin: 0;
}
.router a{
    padding: 0;
    margin: 0;
}
.router i{
    padding: 0;
    margin: 0;
}
.nav-menu{
    position:fixed;
    left: 0;
    width: 18%;
    height: 100%;
    /* background-color: #0884f1; */
    background-color: #388be3;
    font-family: 'Noto Sans', sans-serif;
}
.logo{
    width: 100%;
    height: 70px;
    object-fit: cover;
}
.nav-menu hr{
    height: 1px;         
    background-color: #d3cece; 
    border: none;         
}
.menu{
    display: flex;
    flex-direction: column;
    justify-content: flex-start;
    gap: 10px;
    white-space: nowrap;
    list-style: none;
    padding: 0;
    margin: 0 10px;
    font-family: 'Noto Sans', sans-serif;
    overflow-y:inherit;
}
/* .menu li {
    display: flex;  
    align-items: center; 
    text-align:left;
    padding: 10px;
    color: #f5f5f5;
    font-weight: 400;
    transition: background-color 0.1s ease-in-out;
} */
/* .menu li i {
    margin-right: 10px;
    font-size: 19px;  
    width: 24px;  
    text-align: center;
    font-weight: 600;
} */
/* .menu li:hover{
    border-radius: 10px;
    background-color: #d9e6e6;
} */
.menu a{
    display: flex;  
    align-items: center; 
    text-align:left;
    padding: 10px;
    color: #f5f5f5;
    font-weight: 400;
    transition: all 0.2s ease-in-out;
    text-decoration: none;
    color: #f5f5f5;
}
.menu a i {
    margin-right: 10px;
    font-size: 19px;  
    width: 24px;  
    text-align: center;
    font-weight: 600;
}
.menu a:hover,
.bill.active > a,
.product.active > a,
.warehouse_management.active > a,
.customer.active > a,
.report.active > a {
    /* color: #0884f1; */
   color: #388be3;
    border-radius: 10px;
    background-color: #d9e6e6;
}


.hidden-bill a,
.hidden-product a,
.hidden-warehouse_management a,
.hidden-customer a,
.hidden-report a {
    margin-top: 10px;
    margin-left: 30px;
    padding: 5px 10px;
    color: #f5f5f5;
    text-decoration: none;
}

.hidden-bill a:hover,
.hidden-product a:hover,
.hidden-warehouse_management a:hover,
.hidden-customer a:hover,
.hidden-report a:hover {
    background-color: #ddd;
    border-radius: 5px;
}
</style>