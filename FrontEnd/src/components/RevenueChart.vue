<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { Chart, registerables } from 'chart.js';
import axios from 'axios';
import dayjs from 'dayjs';

Chart.register(...registerables);

const chartRef = ref(null);
let myChart = null;

// Hàm xử lý dữ liệu doanh thu từ API
const getRevenuePerDay = (orders, weekOffset = 0) => {
  const revenue = {
    'Thứ 2': 0,
    'Thứ 3': 0,
    'Thứ 4': 0,
    'Thứ 5': 0,
    'Thứ 6': 0,
    'Thứ 7': 0,
    'Chủ Nhật': 0,
  };

  const weekStart = dayjs().startOf('week').add(1, 'day').subtract(weekOffset, 'week'); // Thứ 2
  const weekEnd = weekStart.add(6, 'day');

  for (const order of orders) {
    const orderDate = dayjs(order.order_date);
    if (orderDate.isAfter(weekEnd) || orderDate.isBefore(weekStart)) continue;

    const day = orderDate.format('dddd'); // e.g., "Monday"
    const vnDay = {
      Monday: 'Thứ 2',
      Tuesday: 'Thứ 3',
      Wednesday: 'Thứ 4',
      Thursday: 'Thứ 5',
      Friday: 'Thứ 6',
      Saturday: 'Thứ 7',
      Sunday: 'Chủ Nhật',
    }[day];

    if (vnDay) {
      revenue[vnDay] += order.total_amount / 1000; // đơn vị: triệu
    }
  }

  return Object.values(revenue); // [Thứ 2, Thứ 3, ..., Chủ Nhật]
};

onMounted(async () => {
  try {
    const response = await axios.get('http://localhost:8083/api/orders');
    const orders = response.data;

    const labels = ['Thứ 2', 'Thứ 3', 'Thứ 4', 'Thứ 5', 'Thứ 6', 'Thứ 7', 'Chủ Nhật'];
    const lastWeekRevenue = getRevenuePerDay(orders, 1); // Tuần trước
    const thisWeekRevenue = getRevenuePerDay(orders, 0); // Tuần này

    myChart = new Chart(chartRef.value, {
      type: 'bar',
      data: {
        labels,
        datasets: [
          {
            label: 'Tuần trước',
            data: lastWeekRevenue,
            backgroundColor: 'rgba(255, 99, 132, 0.6)',
          },
          {
            label: 'Tuần này',
            data: thisWeekRevenue,
            backgroundColor: 'rgba(54, 162, 235, 0.6)',
          },
        ],
      },
      options: {
        responsive: true,
        plugins: {
          legend: { display: true },
          title: {
            display: true,
            text: 'Doanh Thu Siêu Thị Mini - So Sánh Tuần Này & Tuần Trước',
            font: { size: 16, weight: 'bold' },
          },
        },
        scales: {
          x: { ticks: { color: '#000' } },
          y: {
            beginAtZero: true,
            ticks: {
              color: '#000',
              callback: (value) => `${value}`,
            },
          },
        },
      },
    });
  } catch (error) {
    console.error('Lỗi khi lấy dữ liệu:', error);
  }
});

onUnmounted(() => {
  if (myChart) {
    myChart.destroy();
    myChart = null;
  }
});
</script>

<template>
  <div class="chart-container">
    <canvas ref="chartRef"></canvas>
  </div>
</template>

<style scoped>
.chart-container {
  width: 100%;
  max-width: 800px;
  margin: auto;
}
</style>
