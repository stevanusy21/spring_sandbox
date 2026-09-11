<template>
  <div v-if="isOpen" class="modal-backdrop" @click.self="$emit('cancel')">
    <div class="modal-dialog">
      <div class="modal-icon-wrapper">
        <AlertTriangle :size="24" class="warning-icon" />
      </div>

      <div class="modal-content">
        <h3 class="modal-title">{{ title || 'Confirm Action' }}</h3>
        <p class="modal-description">
          {{ message }}
        </p>
      </div>

      <div class="modal-actions">
        <button class="btn btn-secondary" :disabled="loading" @click="$emit('cancel')">
          Cancel
        </button>
        <button class="btn btn-danger" :disabled="loading" @click="$emit('confirm')">
          <Loader2 v-if="loading" :size="16" class="spin" />
          <span v-else>Confirm Delete</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { AlertTriangle, Loader2 } from 'lucide-vue-next'

defineProps({
  isOpen: {
    type: Boolean,
    default: false,
  },
  title: {
    type: String,
    default: 'Delete User Account',
  },
  message: {
    type: String,
    default: 'Are you sure you want to proceed? This action will mark this user account as DELETED.',
  },
  loading: {
    type: Boolean,
    default: false,
  },
})

defineEmits(['confirm', 'cancel'])
</script>

<style scoped>
.modal-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.7);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 200;
  padding: 16px;
  animation: fadeIn 0.15s ease-out;
}

.modal-dialog {
  background: var(--bg-surface);
  border: 1px solid var(--border-medium);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-lg);
  width: 100%;
  max-width: 420px;
  padding: 24px;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  gap: 16px;
}

.modal-icon-wrapper {
  width: 48px;
  height: 48px;
  border-radius: var(--radius-full);
  background: rgba(239, 68, 68, 0.12);
  color: #ef4444;
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-title {
  font-size: 17px;
  font-weight: 700;
  color: #fff;
  margin-bottom: 6px;
}

.modal-description {
  font-size: 13.5px;
  color: var(--text-muted);
  line-height: 1.5;
}

.modal-actions {
  display: flex;
  gap: 12px;
  width: 100%;
  margin-top: 8px;
}

.modal-actions .btn {
  flex: 1;
}

.spin {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>
