<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue';
import { useRouter } from 'vue-router';

// Kiểm soát trạng thái dropdown tìm kiếm
const Check = ref(false);
const CheckRef = ref(null);
const storedUser = ref(null);

const userName = computed(() => storedUser.value?.name || 'Tài khoản');

// Tab hiện tại đang được chọn
const activeTab = ref('product');
const searchInput = ref('');
const searchResults = ref([]);
const isSearching = ref(false);

// Lấy thông tin tab tương ứng để hiển thị nội dung và icon
const bellTab = computed(() => tabs.find((tab) => tab.id === activeTab.value) || tabs[0]);
const currentTab = computed(
  () => listSearch.find((tab) => tab.id === activeTab.value) || listSearch[0]
);

const route = useRouter();

function toInformationAccount() {
  route.push({ path: '/detailEmployee', query: { employee_id: storedUser.value.employee_id } });
}
// Danh sách các tab tìm kiếm
const listSearch = [
  {
    id: 'product',
    name: 'Sản phẩm',
    message: 'Nhập từ khóa để tìm kiếm sản phẩm',
    icon: 'fa-solid fa-box',
  },
  // { id: 'order', name: 'Đơn hàng', message: 'Nhập từ khóa để tìm kiếm đơn hàng', icon: 'fa-solid fa-receipt' },
  {
    id: 'customer',
    name: 'Khách hàng',
    message: 'Nhập từ khóa để tìm kiếm khách hàng',
    icon: 'fa-solid fa-user',
  },
  {
    id: 'supplier',
    name: 'Nhà cung cấp',
    message: 'Nhập từ khóa để tìm kiếm nhà cung cấp',
    icon: 'fa-solid fa-truck',
  },
];

// Các trạng thái liên quan đến dropdown khác
const showDropdown = ref(false);
const profileRef = ref(null);
const showHelpDropdown = ref(false);
const helpRef = ref(null);
const showNotification = ref(false);
const notificationRef = ref(null);
const showSubMenu = ref(false);

function SearchDropdown(event) {
  event.stopPropagation();
  Check.value = !Check.value;
}

function handleClickOutsideSearch(event) {
  if (CheckRef.value && !CheckRef.value.contains(event.target)) {
    Check.value = false;
  }
}

function toggleDropdown(event) {
  event.stopPropagation();
  showDropdown.value = !showDropdown.value;
}

function toggleHelpDropdown(event) {
  event.stopPropagation();
  showHelpDropdown.value = !showHelpDropdown.value;
  if (!showHelpDropdown.value) {
    showSubMenu.value = false;
  }
}

function toggleSubMenu(event) {
  event.stopPropagation();
  showSubMenu.value = !showSubMenu.value;
}

function toggleNotification(event) {
  event.stopPropagation();
  showNotification.value = !showNotification.value;
}

function changeTab(tabId) {
  activeTab.value = tabId;
  fetchSearchResults();
}

function handleClickOutsideProfile(event) {
  if (profileRef.value && !profileRef.value.contains(event.target)) {
    showDropdown.value = false;
  }
}

function handleClickOutsideHelp(event) {
  if (helpRef.value && !helpRef.value.contains(event.target)) {
    showHelpDropdown.value = false;
    showSubMenu.value = false;
  }
}

function handleClickOutsideNotification(event) {
  if (notificationRef.value && !notificationRef.value.contains(event.target)) {
    showNotification.value = false;
  }
}

// Gọi API và lọc dữ liệu tìm kiếm theo từ khóa
async function fetchSearchResults() {
  isSearching.value = true;
  searchResults.value = [];

  const query = searchInput.value.trim();
  if (!query) {
    isSearching.value = false;
    return;
  }

  try {
    const fetchAndFilter = async (url) => {
      const res = await fetch(url);
      const data = await res.json();
      const list = Array.isArray(data) ? data : data.data || [];
      return list.filter((item) => item.name?.toLowerCase().includes(query.toLowerCase()));
    };

    let url = '';
    switch (activeTab.value) {
      case 'product':
        url = 'http://localhost:8083/api/products';
        break;
      case 'customer':
        url = 'http://localhost:8083/api/customers';
        break;
      case 'order':
        url = 'http://localhost:8083/api/orders';
        break;
      case 'supplier':
        url = 'http://localhost:8083/api/suppliers';
        break;
    }

    if (url) {
      const filtered = await fetchAndFilter(url);
      searchResults.value = filtered;
    }
  } catch (err) {
    console.error('Lỗi khi tìm kiếm:', err);
  } finally {
    isSearching.value = false;
  }
}

function logout() {
  sessionStorage.removeItem('token'); // Nếu bạn lưu token
  localStorage.removeItem('user'); // Nếu bạn lưu thông tin người dùng
  sessionStorage.removeItem('employee');
  // Điều hướng về trang đăng nhập
  route.push('/');
}

// Gán sự kiện click ngoài các dropdown để đóng chúng
onMounted(() => {
  document.addEventListener('click', handleClickOutsideProfile);
  document.addEventListener('click', handleClickOutsideHelp);
  document.addEventListener('click', handleClickOutsideNotification);
  document.addEventListener('click', handleClickOutsideSearch);
  const employee = JSON.parse(sessionStorage.getItem('employee'));
  storedUser.value = employee;
});

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutsideProfile);
  document.removeEventListener('click', handleClickOutsideHelp);
  document.removeEventListener('click', handleClickOutsideNotification);
  document.removeEventListener('click', handleClickOutsideSearch);
});
</script>
<template>
  <div class="header">
    <!-- Search Section -->
    <div class="header-left" ref="CheckRef" :class="{ active: Check }" @click="SearchDropdown">
      <i class="fa-solid fa-magnifying-glass" @click.stop="fetchSearchResults"></i>
      <input
        v-model="searchInput"
        @keyup.enter="fetchSearchResults"
        type="text"
        placeholder="Tìm kiếm (Ctrl + K)"
      />
      <div v-show="Check" class="notification-container" @click.stop>
        <div class="tabs">
          <div
            v-for="tab in listSearch"
            :key="tab.id"
            :class="['tab', { active: activeTab === tab.id }]"
            @click="changeTab(tab.id)"
          >
            {{ tab.name }}
          </div>
        </div>
        <div class="tab-content">
          <i :class="currentTab.icon"></i>
          <template v-if="isSearching">
            <p>Đang tìm kiếm...</p>
          </template>
          <template v-else-if="searchResults.length > 0">
            <ul style="text-align: left; padding: 0 20px">
              <li v-for="item in searchResults" :key="item.id" style="padding: 5px 0">
                {{ item.name }}
              </li>
            </ul>
          </template>
          <template v-else>
            <p>{{ currentTab.message }}</p>
          </template>
        </div>
      </div>
    </div>

    <!-- User & Icons Section -->
    <div class="header-right">
      <!-- Help Icon -->
      <!-- <div ref="helpRef" style="position: relative;">
              <i class="fa-regular fa-circle-question" @click="toggleHelpDropdown"></i>
              <div v-if="showHelpDropdown" class="dropdown-menu">
                <div class="dropdown-item">
                  <i class="fa-regular fa-circle-question"></i> Trung tâm trợ giúp
                </div>
                <div class="dropdown-item">
                  <i class="fa-regular fa-comment"></i> Đóng góp ý kiến
                </div>
                <div class="dropdown-item" @click="toggleSubMenu">
                  <i class="fa-solid fa-user"></i> Dành cho khách hàng mới
                  <i
                    class="fa-solid"
                    :class="showSubMenu ? 'fa-chevron-up' : 'fa-chevron-down'"
                    style="margin-left: auto;"
                  ></i>
                <transition name="fade">
                  <div v-if="showSubMenu" class="submenu">
                    <router-link to="/createProduct">
                      <div class="dropdown-item">Thêm sản phẩm</div>
                    </router-link>
                    <router-link to="/createOrder">
                      <div class="dropdown-item">Tạo đơn hàng</div>
                    </router-link>
                  </div>
                </transition>
                <div class="dropdown-item-spe">
                  <div>
                    <i class="fa-solid fa-phone"></i>
                    <span style="color: #007bff; margin-left: 8px;">1900 6750</span>
                  </div>
                  <div>
                    <i class="fa-solid fa-headset"></i>
                    <span style="color: #007bff; margin-left: 8px;">
                      <a href="https://support.sapo.vn/createticket.html">Gửi hỗ trợ</a></span>
                  </div>
                </div>
              </div>
            </div> -->
      <!-- User Profile -->
      <div class="user-profile" ref="profileRef" @click="toggleDropdown">
        <div class="user-info">
          <i class="fa-regular fa-user"></i>
          <span class="user-name">{{ userName }}</span>
          <i class="fa-solid fa-chevron-down"></i>
        </div>

        <!-- Dropdown -->
        <div v-if="showDropdown" class="dropdown-menu">
          <div class="dropdown-item" @click="toInformationAccount">
            <i class="fa-regular fa-user"></i> Tài khoản của bạn
          </div>
          <!-- <div class="dropdown-item">
                  <i class="fa-solid fa-box"></i> Thông tin gói dịch vụ
                </div> -->
          <div class="dropdown-item" @click="logout">
            <i class="fa-solid fa-arrow-right-from-bracket"></i> Đăng xuất
          </div>
          <hr />
          <!-- <div class="dropdown-item">Điều khoản dịch vụ</div>
                <div class="dropdown-item">Chính sách bảo mật</div> -->
        </div>
      </div>
    </div>
  </div>
</template>
<style scoped>
.header {
  width: 85%;
  position: fixed;
  z-index: 1000;
  display: flex;
  align-items: center;
  background-color: white;
  padding: 10px 0;
  box-shadow: 2px 2px 10px rgba(53, 53, 53, 0.1);
  font-family: Arial, Helvetica, sans-serif;
}

.header-left {
  margin: 10px;
  margin-left: 15px;
  display: flex;
  align-items: center;
  background-color: white;
  width: 50%;
  padding: 5px 10px;
  color: rgb(100, 100, 100);
  gap: 8px;
}

.header-left input {
  border: none;
  outline: none;
  background: transparent;
  font-size: 14px;
  width: 150px;
}

.header-left i {
  color: rgb(120, 120, 120);
}

.header-left.active {
  outline: 1px solid rgb(0, 139, 246);
  border-radius: 5px;
  color: rgb(120, 120, 120);
}

.header-right {
  padding: 5px;
  padding-right: 50px;
  flex: 1;
  display: flex;
  align-items: center;
  gap: 20px;
  justify-content: flex-end;
}

.header-right i {
  background: rgb(120, 120, 120);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  transition: all 0.3s;
  cursor: pointer;
}

.header-right i:hover {
  -webkit-text-fill-color: rgb(190, 187, 187);
}

.user-profile {
  position: relative;
  display: flex;
  align-items: center;
  cursor: pointer;
  margin-right: 10px;
}
.user-profile:hover {
  background-color: #f5f5f5;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 17.5px;
  /* font-weight: 600; */
}
.user-info i {
  font-size: 20px;
}
.avatar {
  background-color: #b0e1b0;
  color: green;
  border-radius: 50%;
  padding: 5px 8px;
  font-weight: bold;
}

.dropdown-menu {
  position: absolute;
  top: 40px;
  right: 0;
  width: 250px;
  background-color: white;
  border: 1px solid #ddd;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  z-index: 100;
  padding: 10px;
}

.dropdown-item {
  padding: 10px;
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
}

.dropdown-item:hover {
  background-color: #f5f5f5;
}

.submenu {
  padding-left: 20px;
}
.submenu .dropdown-item:hover {
  background-color: white;
  color: rgb(0, 139, 246);
}
.fade-enter-active,
.fade-leave-active {
  transition: all 0.3s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(-5px);
}
.dropdown-item-spe {
  display: flex;
  justify-content: space-between;
  padding: 10px;
}

.dropdown-item-spe a {
  text-decoration: none;
  color: inherit;
}

.dropdown-item-spe div:hover {
  text-decoration: underline;
  color: rgb(0, 139, 246);
}
.notification-container {
  position: absolute;
  top: 70px;
  width: 500px;
  left: 50px;
  border: 1px solid #ddd;
  border-radius: 12px;
  background-color: white;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  z-index: 1000;
}

.tabs {
  display: flex;
  border-bottom: 1px solid #ddd;
  background-color: #f9f9f9;
}
.tab {
  flex: 1;
  text-align: center;
  padding: 10px;
  cursor: pointer;
  font-weight: bold;
  color: gray;
}
.tab.active {
  color: #007bff;
  border-bottom: 2px solid #007bff;
  background-color: white;
}
.tab-content {
  padding: 20px;
  text-align: center;
}
.tab-content img {
  width: 100px;
}
.hidden {
  display: none;
}
a {
  text-decoration: none;
  color: black;
}
</style>
