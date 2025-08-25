import { createWebHistory, createRouter } from 'vue-router';
import App from '../App.vue';
import startPage from '../pages/startPage.vue';
import SignIn from '../pages/signIn.vue';
import Home from '../pages/Home.vue';
import Customer from '../pages/Customer/IndexCustomer.vue';
import CreateCustomer from '@/pages/Customer/CreateCustomer.vue';
import HomeEmployee from '@/pages/Employee/HomeEmployee.vue';
import HomeSupplier from '@/pages/Supplier/HomeSupplier.vue';
import DetailSupplier from '@/pages/Supplier/DetailSupplier.vue';
import CreateOrder from '@/pages/Order/createOrder.vue';
import Mainpage from '@/components/Mainpage.vue';
import Invoice from '@/pages/Order/Invoice.vue';
import InvoiceOrder from '@/pages/Order/InvoiceOrder.vue';
import ProductCatalog from '@/pages/Product/ProductCatalog.vue';
import CreateProduct from '@/pages/Product/CreateProduct.vue';
import EditProduct from '@/pages/Product/EditProduct.vue';
import TheRegister from '@/pages/TheRegister.vue';
import AdminHome from '@/pages/AdminHome.vue';
import InventoryProducts from '@/pages/Inventory/InventoryProducts.vue';
import NewInventoryProduct from '@/pages/Inventory/NewInventoryProduct.vue';
import ImportGoodList from '@/pages/Inventory/ImportGoodList.vue';
import PriceProductList from '@/pages/Product/PriceProductList.vue';
import ImportProduct from '@/pages/Inventory/ImportProduct.vue';
import PayProduct from '@/pages/Inventory/PayProduct.vue';
import PayProductList from '@/pages/Inventory/PayProductList.vue';
import TheConfiguration from '@/pages/theConfiguration.vue';
import InvoiceImportIndex from '@/pages/InvoiceStockImport/InvoiceImportIndex.vue';
import InvoiceImport from '@/pages/InvoiceStockImport/InvoiceImport.vue';
import RevenueReport from '@/pages/RevenueReport.vue.vue';
import CashBook from '@/pages/CashBook.vue';
import DetailEmployee from '@/pages/Employee/DetailEmployee.vue';

const routes = [
  { path: '/', component: startPage },
  { path: '/SignIn', component: SignIn },
  { path: '/theRegister', component: TheRegister },
  { path: '/home', component: Home, meta: { requiresAuth: true } },
  { path: '/detail/Customer/Home', component: Customer, meta: { requiresAuth: true } },
  { path: '/detail/Customer/Create', component: CreateCustomer, meta: { requiresAuth: true } },
  { path: '/invoice', component: Invoice, meta: { requiresAuth: true } },
  { path: '/detail/Employee', component: HomeEmployee, meta: { requiresAuth: true } },
  { path: '/detail/Supplier', component: HomeSupplier, meta: { requiresAuth: true } },
  { path: '/detail/SupplierDetail', component: DetailSupplier, meta: { requiresAuth: true } },
  { path: '/createOrder', component: CreateOrder, meta: { requiresAuth: true } },
  { path: '/orders/:id', component: InvoiceOrder, meta: { requiresAuth: true } },
  { path: '/mainPage', component: Mainpage, meta: { requiresAuth: true } },
  { path: '/ProductCatalog', component: ProductCatalog, meta: { requiresAuth: true } },
  { path: '/createProduct', component: CreateProduct, meta: { requiresAuth: true } },
  { path: '/editProduct/:id', component: EditProduct, meta: { requiresAuth: true } },
  { path: '/adminHome', component: AdminHome, meta: { requiresAuth: true } },
  { path: '/inventoryProduct', component: InventoryProducts, meta: { requiresAuth: true } },
  {
    path: '/ProductCatalog/importProduct',
    component: NewInventoryProduct,
    meta: { requiresAuth: true },
  },
  { path: '/importGoodList', component: ImportGoodList, meta: { requiresAuth: true } },
  { path: '/priceProductList', component: PriceProductList, meta: { requiresAuth: true } },
  { path: '/importProduct', component: ImportProduct, meta: { requiresAuth: true } },
  { path: '/payProduct', component: PayProduct, meta: { requiresAuth: true } },
  { path: '/payProductList', component: PayProductList, meta: { requiresAuth: true } },
  { path: '/theConfiguration', component: TheConfiguration, meta: { requiresAuth: true } },
  { path: '/invoiceImport', component: InvoiceImport, meta: { requiresAuth: true } },
  { path: '/invoiceImportIndex/:id', component: InvoiceImportIndex, meta: { requiresAuth: true } },
  { path: '/revenueReport', component: RevenueReport, meta: { requiresAuth: true } },
  { path: '/cashBook', component: CashBook, meta: { requiresAuth: true } },
  { path: '/detailEmployee', component: DetailEmployee, meta: { requiresAuth: true } },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach((to, from, next) => {
  let employee = null;
  try {
    employee = JSON.parse(sessionStorage.getItem('employee'));
  } catch (e) {
    employee = null;
  }

  const requiresAuth = to.matched.some((record) => record.meta.requiresAuth);

  if (requiresAuth && (!employee || typeof employee !== 'object')) {
    next('/SignIn');
  } else {
    next();
  }
});

export default router;
