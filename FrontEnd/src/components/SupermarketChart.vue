<script setup>
import { ref, onMounted, onUnmounted } from "vue";
import { Chart, registerables } from "chart.js";


Chart.register(...registerables);


const chartRef = ref(null);
let myChart = null;


// Hàm dựng biểu đồ với dữ liệu truyền vào
function createChart(labels, data) {
  if (chartRef.value) {
    myChart = new Chart(chartRef.value, {
      type: "bar",
      data: {
        labels: labels,
        datasets: [
          {
            label: "Số lượng bán ra (đơn vị: sản phẩm)",
            data: data,
            backgroundColor: [
              "#4CAF50", "#FF0000", "#FFD700", "#FFA500", "#008080",
              "#800000", "#00CED1", "#C0C0C0", "#A52A2A", "#FF69B4"
            ],
            borderRadius: 5,
          },
        ],
      },
      options: {
        indexAxis: "y",
        responsive: true,
        plugins: {
          legend: { display: false },
          title: {
            display: true,
            text: "Top 10 sản phẩm bán chạy",
            font: { size: 16, weight: "bold" },
          },
        },
        scales: {
          x: {
            beginAtZero: true,
            ticks: { color: "#000" },
          },
          y: {
            ticks: { color: "#000" },
          },
        },
      },
    });
  }
}


onMounted(async () => {
  try {
    const response = await fetch("http://localhost:8083/api/products");
    const result = await response.json();
    const products = Array.isArray(result) ? result : result.data || [];


    // Lấy 10 sản phẩm có lượng bán cao nhất
    const topProducts = [...products]
      .sort((a, b) => b.sell - a.sell)
      .slice(0, 10);


    const labels = topProducts.map(p => p.name);
    const data = topProducts.map(p => p.sell);


    createChart(labels, data);
  } catch (error) {
    console.error("Lỗi khi lấy dữ liệu sản phẩm:", error);
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
  max-width: 700px;
  margin: auto;
}
</style>



