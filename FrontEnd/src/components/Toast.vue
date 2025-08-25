<template>
  <div v-if="visible" :class="['toast', type, { hidden: isHiding }]">
    {{ message }}
    <div class="progress-bar" :style="{ animationDuration: duration + 'ms' }"></div>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const visible = ref(false);
const isHiding = ref(false);
const message = ref('');
const type = ref('success');
const duration = ref(3000);

function show(msg, msgType = 'success', displayDuration = 3000) {
  message.value = msg;
  type.value = msgType;
  duration.value = displayDuration;
  visible.value = true;
  isHiding.value = false;

  setTimeout(() => {
    isHiding.value = true;
    setTimeout(() => {
      visible.value = false;
    }, 300); // hiệu ứng ẩn mượt
  }, displayDuration);
}

defineExpose({ show });
</script>

<style scoped>
.toast {
  position: fixed;
  top: 20px;
  right: 20px;
  transform: none;
  padding: 14px 24px 20px 24px;
  border-radius: 8px;
  font-weight: bold;
  z-index: 9999;
  color: white;
  box-shadow: 0 0 12px rgba(0, 0, 0, 0.2);
  opacity: 1;
  transition:
    opacity 0.3s ease,
    transform 0.3s ease;
  min-width: 240px;
}

.toast.success {
  background-color: #4caf50;
}

.toast.error {
  background-color: #f44336;
}

.toast.info {
  background-color: #2196f3;
}

.toast.warning {
  background-color: #ff9800;
}

.toast.hidden {
  opacity: 0;
  transform: translateY(-10px);
}

/* Thanh progress chạy ngang từ trái sang phải */
.progress-bar {
  position: absolute;
  bottom: 0;
  left: 0;
  height: 4px;
  background-color: rgba(255, 255, 255, 0.8);
  border-bottom-left-radius: 8px;
  border-bottom-right-radius: 8px;
  width: 100%;
  animation-name: progressRun;
  animation-timing-function: linear;
  animation-fill-mode: forwards;
}

@keyframes progressRun {
  from {
    width: 100%;
  }
  to {
    width: 0%;
  }
}
</style>
