<template>
  <div v-if="isOpen" class="modal-backdrop" @click.self="$emit('close')">
    <div class="modal-dialog">
      <!-- Header -->
      <div class="modal-header">
        <div class="header-info">
          <div class="header-icon">
            <UserPlus v-if="mode === 'create'" :size="20" />
            <UserCheck v-else :size="20" />
          </div>
          <div>
            <h3 class="modal-title">{{ mode === 'create' ? 'Create New User' : 'Edit User Profile' }}</h3>
            <p class="modal-sub">
              {{ mode === 'create' ? 'Fill in account credentials and details' : `Updating account for @${form.username}` }}
            </p>
          </div>
        </div>
        <button class="btn-icon btn-ghost" @click="$emit('close')">
          <X :size="18" />
        </button>
      </div>

      <!-- General Form Error Alert -->
      <div v-if="errorMessage" class="error-banner">
        <AlertCircle :size="16" />
        <span>{{ errorMessage }}</span>
      </div>

      <!-- Form Body -->
      <form @submit.prevent="handleSubmit" class="modal-form">
        <div class="form-grid">
          <!-- Username -->
          <div class="form-group">
            <label class="form-label">Username *</label>
            <input 
              v-model="form.username" 
              type="text" 
              class="form-input" 
              placeholder="e.g. john_doe"
              :disabled="mode === 'edit' || loading"
              required
            />
          </div>

          <!-- Password -->
          <div class="form-group">
            <label class="form-label">
              {{ mode === 'create' ? 'Password *' : 'New Password (Optional)' }}
            </label>
            <input 
              v-model="form.password" 
              type="password" 
              class="form-input" 
              :placeholder="mode === 'create' ? '••••••••' : 'Leave empty to keep current'"
              :required="mode === 'create'"
              :disabled="loading"
            />
          </div>

          <!-- Full Name -->
          <div class="form-group">
            <label class="form-label">Full Name *</label>
            <input 
              v-model="form.fullName" 
              type="text" 
              class="form-input" 
              placeholder="e.g. John Doe"
              :disabled="loading"
              required
            />
          </div>

          <!-- Email -->
          <div class="form-group">
            <label class="form-label">Email Address *</label>
            <input 
              v-model="form.email" 
              type="email" 
              class="form-input" 
              placeholder="john@example.com"
              :disabled="loading"
              required
            />
          </div>

          <!-- Phone Number -->
          <div class="form-group">
            <label class="form-label">Phone Number *</label>
            <input 
              v-model="form.phoneNumber" 
              type="text" 
              class="form-input" 
              placeholder="08123456789 or +62..."
              :disabled="loading"
              required
            />
            <span class="helper-text">Format Indonesia: diawali 08xx atau +628xx</span>
          </div>

          <!-- Date of Birth -->
          <div class="form-group">
            <label class="form-label">Date of Birth</label>
            <input 
              v-model="form.dob" 
              type="date" 
              class="form-input" 
              :disabled="loading"
            />
          </div>

          <!-- Role -->
          <div class="form-group">
            <label class="form-label">Role</label>
            <select v-model="form.role" class="form-select" :disabled="loading">
              <option value="USER">USER</option>
              <option value="ADMIN">ADMIN</option>
              <option value="MANAGER">MANAGER</option>
            </select>
          </div>

          <!-- Status (Edit Mode only) -->
          <div class="form-group" v-if="mode === 'edit'">
            <label class="form-label">Account Status</label>
            <select v-model="form.status" class="form-select" :disabled="loading">
              <option value="ACTIVE">ACTIVE</option>
              <option value="INACTIVE">INACTIVE</option>
              <option value="BLOCKED">BLOCKED</option>
              <option value="DELETED">DELETED</option>
            </select>
          </div>

          <!-- Address -->
          <div class="form-group full-width">
            <label class="form-label">Address</label>
            <textarea 
              v-model="form.address" 
              class="form-textarea" 
              rows="2" 
              placeholder="Full street address..."
              :disabled="loading"
            ></textarea>
          </div>
        </div>

        <!-- Footer Buttons -->
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" :disabled="loading" @click="$emit('close')">
            Cancel
          </button>
          <button type="submit" class="btn btn-primary" :disabled="loading">
            <Loader2 v-if="loading" :size="16" class="spin" />
            <span>{{ mode === 'create' ? 'Create User' : 'Save Changes' }}</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { reactive, watch } from 'vue'
import { X, UserPlus, UserCheck, AlertCircle, Loader2 } from 'lucide-vue-next'

const props = defineProps({
  isOpen: {
    type: Boolean,
    default: false,
  },
  mode: {
    type: String,
    default: 'create', // 'create' | 'edit'
  },
  initialData: {
    type: Object,
    default: null,
  },
  loading: {
    type: Boolean,
    default: false,
  },
  errorMessage: {
    type: String,
    default: '',
  },
})

const emit = defineEmits(['close', 'submit'])

const form = reactive({
  username: '',
  password: '',
  fullName: '',
  email: '',
  phoneNumber: '',
  dob: '',
  role: 'USER',
  status: 'ACTIVE',
  address: '',
})

watch(
  () => props.isOpen,
  (newVal) => {
    if (newVal) {
      if (props.mode === 'edit' && props.initialData) {
        form.username = props.initialData.username || ''
        form.password = ''
        form.fullName = props.initialData.fullName || ''
        form.email = props.initialData.email || ''
        form.phoneNumber = props.initialData.phoneNumber || ''
        form.dob = props.initialData.dob || ''
        form.role = props.initialData.role || 'USER'
        form.status = props.initialData.status || 'ACTIVE'
        form.address = props.initialData.address || ''
      } else {
        form.username = ''
        form.password = ''
        form.fullName = ''
        form.email = ''
        form.phoneNumber = ''
        form.dob = ''
        form.role = 'USER'
        form.status = 'ACTIVE'
        form.address = ''
      }
    }
  }
)

const handleSubmit = () => {
  const payload = { ...form }
  // Omit empty password on edit
  if (props.mode === 'edit' && !payload.password) {
    delete payload.password
  }
  if (props.mode === 'create') {
    delete payload.status
  }
  emit('submit', payload)
}
</script>

<style scoped>
.modal-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.75);
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
  max-width: 560px;
  max-height: 90vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.modal-header {
  padding: 18px 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid var(--border-subtle);
  background: rgba(255, 255, 255, 0.02);
}

.header-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-icon {
  width: 40px;
  height: 40px;
  border-radius: var(--radius-md);
  background: rgba(99, 102, 241, 0.15);
  color: #818cf8;
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-title {
  font-size: 16px;
  font-weight: 700;
  color: #fff;
}

.modal-sub {
  font-size: 12.5px;
  color: var(--text-muted);
}

.error-banner {
  margin: 16px 24px 0;
  padding: 10px 14px;
  background: rgba(239, 68, 68, 0.12);
  border: 1px solid rgba(239, 68, 68, 0.25);
  border-radius: var(--radius-md);
  color: #fca5a5;
  font-size: 12.5px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.modal-form {
  padding: 20px 24px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14px;
}

.full-width {
  grid-column: span 2;
}

.helper-text {
  font-size: 11px;
  color: var(--text-faint);
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding-top: 10px;
  border-top: 1px solid var(--border-subtle);
}

.spin {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>
