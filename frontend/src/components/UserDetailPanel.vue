<template>
  <aside class="detail-panel" :class="{ 'is-open': isOpen }">
    <div class="panel-container" v-if="user">
      <!-- Header with Avatar, Title, and Close Button -->
      <div class="panel-header">
        <div class="header-main">
          <div class="user-avatar-large">
            {{ getInitials(user.fullName || user.username) }}
          </div>
          <div class="header-titles">
            <h3 class="user-name">{{ user.fullName || user.username }}</h3>
            <span class="user-handle">@{{ user.username }}</span>
          </div>
        </div>
        <button class="btn-icon btn-ghost close-btn" @click="$emit('close')" title="Close Detail Panel">
          <X :size="18" />
        </button>
      </div>

      <!-- Status & Quick Stats Bar -->
      <div class="quick-status-bar">
        <span class="status-label">Account Status:</span>
        <span class="badge" :class="`badge-${(user.status || 'ACTIVE').toLowerCase()}`">
          <span class="status-dot"></span>
          {{ user.status || 'ACTIVE' }}
        </span>
      </div>

      <!-- Detail Info Sections -->
      <div class="panel-body">
        <div class="info-section">
          <h4 class="section-title">Personal Information</h4>
          <div class="info-grid">
            <div class="info-card">
              <span class="card-label">User ID</span>
              <span class="card-value font-mono">#{{ user.id }}</span>
            </div>

            <div class="info-card">
              <span class="card-label">Role</span>
              <span class="card-value">{{ user.role || 'USER' }}</span>
            </div>

            <div class="info-card">
              <span class="card-label">Email Address</span>
              <span class="card-value">{{ user.email }}</span>
            </div>

            <div class="info-card">
              <span class="card-label">Phone Number</span>
              <span class="card-value font-mono">{{ user.phoneNumber }}</span>
            </div>

            <div class="info-card">
              <span class="card-label">Date of Birth</span>
              <span class="card-value">{{ user.dob || '-' }}</span>
            </div>

            <div class="info-card full-width">
              <span class="card-label">Address</span>
              <span class="card-value">{{ user.address || '-' }}</span>
            </div>
          </div>
        </div>

        <div class="info-section audit-section">
          <h4 class="section-title">Audit Log</h4>
          <div class="info-grid">
            <div class="info-card">
              <span class="card-label">Created Date</span>
              <span class="card-value small">{{ formatDateTime(user.createdDate) }}</span>
            </div>
            <div class="info-card">
              <span class="card-label">Created By</span>
              <span class="card-value small">{{ user.createdBy || 'SYSTEM' }}</span>
            </div>
            <div class="info-card" v-if="user.lastModifiedDate">
              <span class="card-label">Last Modified</span>
              <span class="card-value small">{{ formatDateTime(user.lastModifiedDate) }}</span>
            </div>
            <div class="info-card" v-if="user.lastModifiedBy">
              <span class="card-label">Modified By</span>
              <span class="card-value small">{{ user.lastModifiedBy }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Action Footer: Edit & Delete Buttons -->
      <div class="panel-footer">
        <button class="btn btn-secondary edit-btn" @click="$emit('edit', user)">
          <Edit2 :size="15" />
          Edit Profile
        </button>
        <button class="btn btn-danger delete-btn" @click="$emit('delete', user)">
          <Trash2 :size="15" />
          Delete
        </button>
      </div>
    </div>
  </aside>
</template>

<script setup>
import { X, Edit2, Trash2 } from 'lucide-vue-next'

const props = defineProps({
  isOpen: {
    type: Boolean,
    default: false,
  },
  user: {
    type: Object,
    default: null,
  },
})

defineEmits(['close', 'edit', 'delete'])

const getInitials = (name) => {
  if (!name) return 'U'
  const parts = name.trim().split(' ')
  if (parts.length >= 2) {
    return (parts[0][0] + parts[1][0]).toUpperCase()
  }
  return name.slice(0, 2).toUpperCase()
}

const formatDateTime = (dateStr) => {
  if (!dateStr) return '-'
  try {
    const d = new Date(dateStr)
    return d.toLocaleString('id-ID', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    })
  } catch {
    return dateStr
  }
}
</script>

<style scoped>
.detail-panel {
  position: fixed;
  top: 0;
  right: 0;
  width: 380px;
  height: 100vh;
  background: var(--bg-surface);
  border-left: 1px solid var(--border-subtle);
  box-shadow: var(--shadow-drawer);
  transform: translateX(100%);
  transition: transform 0.26s cubic-bezier(0.16, 1, 0.3, 1);
  z-index: 100;
  display: flex;
  flex-direction: column;
}

.detail-panel.is-open {
  transform: translateX(0);
}

.panel-container {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.panel-header {
  padding: 20px;
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  border-bottom: 1px solid var(--border-subtle);
  background: rgba(255, 255, 255, 0.02);
}

.header-main {
  display: flex;
  align-items: center;
  gap: 14px;
}

.user-avatar-large {
  width: 48px;
  height: 48px;
  border-radius: var(--radius-lg);
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  color: #fff;
  font-weight: 800;
  font-size: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(99, 102, 241, 0.3);
  flex-shrink: 0;
}

.header-titles {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.user-name {
  font-size: 16px;
  font-weight: 700;
  color: #fff;
}

.user-handle {
  font-size: 13px;
  color: var(--text-muted);
}

.close-btn {
  color: var(--text-faint);
  border: 1px solid transparent;
}
.close-btn:hover {
  border-color: var(--border-subtle);
}

.quick-status-bar {
  padding: 10px 20px;
  background: rgba(0, 0, 0, 0.2);
  border-bottom: 1px solid var(--border-subtle);
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.status-label {
  font-size: 12px;
  font-weight: 600;
  color: var(--text-muted);
}

.status-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: currentColor;
}

.panel-body {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.section-title {
  font-size: 11px;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  color: var(--text-faint);
  margin-bottom: 10px;
}

.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}

.info-card {
  background: var(--bg-surface-elevated);
  border: 1px solid var(--border-subtle);
  border-radius: var(--radius-md);
  padding: 10px 12px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-card.full-width {
  grid-column: span 2;
}

.card-label {
  font-size: 11px;
  color: var(--text-muted);
  font-weight: 600;
}

.card-value {
  font-size: 13px;
  color: #fff;
  font-weight: 600;
  word-break: break-word;
}

.card-value.small {
  font-size: 11.5px;
}

.panel-footer {
  padding: 16px 20px;
  border-top: 1px solid var(--border-subtle);
  display: flex;
  gap: 10px;
  background: var(--bg-surface);
}

.panel-footer .btn {
  flex: 1;
}
</style>
