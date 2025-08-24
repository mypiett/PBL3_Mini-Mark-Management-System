<script setup>
import { ref, onMounted, watch } from 'vue';
import MainPage from '../components/Mainpage.vue'
import TheHeader from '../components/TheHeader.vue'
import axios from 'axios';

const months = ref([]);
const years = ref([]);
const selectedMonth = ref(null);
const selectedYear = ref(null);
const summary = ref(null);
const loading = ref(false);
const detailEntries = ref([]);
const isCurrentMonthYear = ref(false);
const isEndOfMonth = ref(false);
const showConfirm = ref(false);
const confirmMessage = ref("");
const confirmAction = ref(null);


const rent = ref(0);
const electricity = ref(0);
const water = ref(0);
const totalSalary = ref(0);

const fetchMonths = async () => {
  const res = await axios.get('http://localhost:8083/api/cashbook/months');
  months.value = res.data;
};

const fetchYears = async () => {
  const res = await axios.get('http://localhost:8083/api/cashbook/years');
  years.value = res.data;
};

const fetchDetails = async (cashBookId, month, year) => {
  try {
  console.log("CashBook ID:", cashBookId);
    const res = await axios.get(`http://localhost:8083/api/cashbook-details/${cashBookId}?month=${month}&year=${year}`);
    detailEntries.value = res.data;
  } catch (err) {
    console.error("Lỗi khi lấy chi tiết sổ quũ:", err);
  }
};

watch([selectedMonth, selectedYear], () => {
  if (selectedMonth.value && selectedYear.value) {
    fetchSummary();
  }
});

const fetchSummary = async () => {
  if (!selectedMonth.value || !selectedYear.value) return;
  loading.value = true;

  try {
    const res = await axios.get(`http://localhost:8083/api/cashbook/${selectedMonth.value}/${selectedYear.value}`);
    
    if (!res.data || Object.keys(res.data).length === 0) {
      summary.value = null;
      detailEntries.value = [];
      return;
    }
    isCurrentMonthYear.value = res.data.currentMonthYear;


if (isCurrentMonthYear.value) {
  const today = new Date();
  const year = today.getFullYear();
  const month = today.getMonth() + 1;

  const lastDay = new Date(year, month, 0).getDate(); // ngày cuối tháng
  isEndOfMonth.value = today.getDate() === lastDay;
}

    summary.value = res.data;
    rent.value = res.data.rent;
    electricity.value = res.data.electricity;
    water.value = res.data.water;
    totalSalary.value = res.data.salary;

    // ✅ Gọi fetchDetails nếu có id
    if (res.data.id) {
      await fetchDetails(res.data.id, selectedMonth.value, selectedYear.value);
    } else {
      detailEntries.value = [];
    }

  } catch (err) {
    console.error("Lỗi khi lấy sổ quỹ:", err);
    summary.value = null;
    detailEntries.value = [];
  } finally {
    loading.value = false;
  }
};
const onCurrencyInput = (event, field) => {
  const raw = event.target.value.replace(/\D/g, '');
  const number = Number(raw || 0);

  if (field === 'electricity') electricity.value = number;
  if (field === 'water') water.value = number;
  if (field === 'totalSalary') totalSalary.value = number;
};
const confirmSave = () => {
  confirmMessage.value = "Bạn có chắc muốn lưu kết sổ tháng này?";
  showConfirm.value = true;
  confirmAction.value = async () => {
    try {
      const res = await axios.put(`http://localhost:8083/api/cashbook/${summary.value.id}`, {
        ...summary.value,
        electricity: electricity.value,
        water: water.value,
        salary: totalSalary.value // Bao gồm lương trong cập nhật
      });
      showConfirm.value = false;
      await fetchSummary();
    } catch (err) {
      console.error("Lỗi khi lưu kết sổ:", err);
      confirmMessage.value = "❌ Có lỗi khi lưu kết sổ. Vui lòng thử lại.";
      showConfirm.value = true;
      confirmAction.value = null;
    }
  };
};
onMounted(async () => {
  try {
    await axios.post('http://localhost:8083/api/cashbook/generate-auto');
  } catch (err) {
    console.error("Lỗi khi gọi generate-auto:", err); // 👈 không để vue crash
  }

  try {
    await fetchMonths();
    await fetchYears();
    console.log("Months loaded:", months.value);
    if (months.value.length > 0) selectedMonth.value = months.value[0];
    if (years.value.length > 0) selectedYear.value = years.value[0];
  } catch (err) {
    console.error("Lỗi khi fetch months/years:", err);
  }
});




const handleSave = () => {
  if (!isEndOfMonth.value) {
    confirmMessage.value = "❌ Chưa đến cuối tháng, không thể kết sổ!";
    showConfirm.value = true;
    confirmAction.value = null;
    return;
  }

  confirmSave();
};


const formatCurrency = amount => {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(amount);
};

const costs = [
  { label: 'Tiền thuê nhà', model: rent },
  { label: 'Tiền điện', model: electricity },
  { label: 'Tiền nước', model: water }
];
</script>

<template>
  <div class="main-container">
    <MainPage />
    <div class="container">
      <div class="page">
        <TheHeader />
        <div class ="page-main">
            <div class="cashbook-page">
            <h2>Xem Sổ Qũy Theo Tháng</h2>

            <div class="filters" v-if="months.length && years.length">
              <select v-model="selectedMonth">
                <option disabled value="">Chọn tháng</option>
                <option v-for="m in months" :key="m" :value="m">Tháng {{ m }}</option>
              </select>
              <select v-model="selectedYear">
                <option disabled value="">Chọn năm</option>
                <option v-for="y in years" :key="y" :value="y">Năm {{ y }}</option>
              </select>
              <button @click="fetchSummary">Xem sổ qũy</button>
            </div>
            
            <div v-if="loading">\u0110ang tải dữ liệu...</div>

            <div v-if="summary">
              <div class="stats">
                <div class="card">Tổng thu: <span>{{ formatCurrency(summary.totalIncome) }}</span></div>
                <div class="card">Tổng chi: <span>{{ formatCurrency(summary.totalExpense) }}</span></div>
                <div class="card">Lợi nhuận trước chi phí: <span>{{ formatCurrency(summary.profitBeforeCost) }}</span></div>
                <div class="card highlight">Lợi nhuận thực tế: <span>{{ formatCurrency(summary.actualProfit) }}</span></div>
              </div>

              <table>
                <thead>
                  <tr>
                    <th>Loại</th>
                    <th>Mã liên kết</th>
                    <th>Giao dịch</th>
                  </tr>
                </thead>
                <tbody>
                  <tr
                    v-for="entry in detailEntries"
                    :key="entry.entryId"
                    :class="entry.direction === 'IN' ? 'row-in' : 'row-out'"
                  >
                    <td>{{ entry.type }}</td>
                    <td>{{ entry.sourceId }}</td>
                    <td>{{ entry.direction === 'IN' ? 'Thu' : 'Chi' }}</td>
                  </tr>
                </tbody>
              </table>

              <div class="cost-summary">
  <h3>Chi phí vận hành</h3>

  <div class="cost-row" v-if="rent">
    <span>Tiền thuê nhà:</span>
    <strong>{{ formatCurrency(rent) }}</strong>
  </div>

  <div class="cost-row">
    <span>Tiền điện:</span>
    <strong v-if="!isCurrentMonthYear">{{ formatCurrency(electricity) }}</strong>
    <input
      v-else
      type="text"
      :value="formatCurrency(electricity)"
      @input="onCurrencyInput($event, 'electricity')"
    />
  </div>

  <div class="cost-row">
    <span>Tiền nước:</span>
    <strong v-if="!isCurrentMonthYear">{{ formatCurrency(water) }}</strong>
    <input
      v-else
      type="text"
      :value="formatCurrency(water)"
      @input="onCurrencyInput($event, 'water')"
    />
  </div>

  <div class="cost-row" v-if="totalSalary">
  <span>Tổng lương nhân viên:</span>
  <strong v-if="!isCurrentMonthYear">{{ formatCurrency(totalSalary) }}</strong>
  <input
    v-else
    type="text"
    :value="formatCurrency(totalSalary)"
    @input="onCurrencyInput($event, 'totalSalary')"
  />
</div>

  <div class="cost-row total" v-if="rent || electricity || water || totalSalary">
    <span>Tổng chi phí vận hành:</span>
    <strong>{{ formatCurrency(rent + electricity + water + totalSalary) }}</strong>
  </div>

  <div class="cost-row profit" v-if="summary.actualProfit !== undefined">
    <span>Lợi nhuận thực tế:</span>
    <strong>{{ formatCurrency(summary.actualProfit) }}</strong>
  </div>

  <div v-if="isCurrentMonthYear" class="save-footer">
    <button @click="handleSave" class="save-button">Lưu kết sổ</button>
  </div>
</div>

            </div>

          </div>
        </div>
      </div>
    </div>
    <div v-if="showConfirm" class="modal-overlay">
  <div class="modal-box">
    <p>{{ confirmMessage }}</p>
    <div class="modal-buttons">
      <button v-if="confirmAction" @click="confirmAction" class="yes-button">Đóng</button>
      <button @click="showConfirm = false" class="no-button">Thoát</button>
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
  font-family:Arial, Helvetica, sans-serif;
}
.page-main{
    display: flex;
    flex-direction: column;
    margin-top: 6%;
    padding: 0px 30px;
}
.cashbook-page {
  padding: 24px;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.filters {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

select, button {
  padding: 6px 12px;
  font-size: 14px;
  border-radius: 6px;
  border: 1px solid #ccc;
}

.stats {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin: 20px 0;
}

.card {
  flex: 1;
  padding: 12px 20px;
  background: #f0f9ff;
  border-radius: 6px;
  border-left: 5px solid #3498db;
  font-weight: 600;
}

.card.highlight {
  background: #eaffea;
  border-left-color: #27ae60;
}

.card span {
  float: right;
  font-weight: bold;
}

table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 20px;
}

th, td {
  padding: 10px;
  border: 1px solid #ccc;
  text-align: left;
}

.cost-inputs {
  margin-top: 20px;
  padding: 16px;
  background-color: #f4f9ff;
  border-radius: 8px;
}

.cost-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.cost-item label {
  flex: 0 0 150px;
  font-weight: 500;
  color: #2c3e50;
}

.cost-item input {
  flex: 1;
  padding: 6px 12px;
  border: 1px solid #ccc;
  border-radius: 6px;
  font-size: 14px;
  background-color: #fff;
}

.summary p {
  font-size: 1.2em;
  margin: 10px 0;
  color: #2c3e50;
  font-weight: 700;
}
.row-in {
  background-color: #ffffff; 
}

.row-out {
  background-color: #fef3f2;
}

.cost-summary {
  margin-top: 24px;
  padding: 20px;
  background: #f9fcff;
  border-radius: 10px;
  border: 1px solid #d9e6f0;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.03);
}

.cost-summary h3 {
  margin-bottom: 16px;
  color: #2c3e50;
  font-size: 18px;
  border-bottom: 1px solid #d9e6f0;
  padding-bottom: 8px;
}

.cost-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  font-size: 15px;
}

.cost-row.total {
  font-weight: bold;
  color: #333;
  border-top: 1px dashed #ccc;
  padding-top: 10px;
  margin-top: 10px;
}

.cost-row.profit {
  color: #27ae60;
  font-weight: bold;
  font-size: 16px;
  border-top: 1px solid #b8e0cc;
  padding-top: 10px;
  margin-top: 14px;
}
.editable-costs {
  margin-top: 20px;
}
.input-row {
  margin-bottom: 10px;
  display: flex;
  gap: 10px;
}
.save-button {
  padding: 8px 16px;
  background-color: #2ecc71;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}
.cost-row input[type="number"] {
  border: none;
  background: transparent;
  font-weight: bold;
  text-align: right;
  width: 120px;
  font-size: 16px;
  color: black;
  padding-right: 0;
  outline: none;
}
.cost-row input[type="number"]::placeholder {
  color: #000;
}
.cost-row input[type="text"] {
  border: none;
  background: transparent;
  font-weight: bold;
  text-align: right;
  font-size: 16px;
  width: 120px;
  color: black;
  outline: none;
}
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.3);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
}

.modal-box {
  background: white;
  border-radius: 8px;
  padding: 24px;
  width: 340px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  text-align: center;
}

.modal-box p {
  margin-bottom: 16px;
  font-size: 16px;
}

.modal-buttons {
  display: flex;
  justify-content: center;
  gap: 16px;
}

.yes-button, .no-button {
  padding: 6px 14px;
  border: none;
  border-radius: 6px;
  font-weight: bold;
  cursor: pointer;
}

.yes-button {
  background-color: #2ecc71;
  color: white;
}

.no-button {
  background-color: #e74c3c;
  color: white;
}

</style>
