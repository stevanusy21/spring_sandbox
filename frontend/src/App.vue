<template>
  <div class="app-layout">
    <!-- Left Resizable & Collapsible Sidebar -->
    <Sidebar :user-count="totalData" />

    <!-- Main Content Area -->
    <main class="main-content">
      <!-- Top Header Navigation -->
      <header class="app-header">
        <div class="header-left">
          <div class="breadcrumb">
            <span class="crumb-parent">Dashboard</span>
            <span class="crumb-separator">/</span>
            <span class="crumb-active">Users</span>
          </div>
          <div class="title-row">
            <h1 class="page-title">User Directory</h1>
            <div class="header-status-badge" :class="backendConnected ? 'status-online' : 'status-offline'">
              <span class="pulse-indicator"></span>
              <span>{{ backendConnected ? 'API Live (Port 8081)' : 'API Disconnected' }}</span>
            </div>
          </div>
        </div>

        <div class="header-right">
          <button 
            class="btn btn-secondary refresh-btn" 
            :disabled="loading" 
            @click="fetchUsers"
            title="Reload user data"
          >
            <RotateCw :size="16" :class="{ spin: loading }" />
            <span>Refresh</span>
          </button>

          <button class="btn btn-primary" @click="handleOpenCreate">
            <UserPlus :size="16" />
            <span>Create User</span>
          </button>
        </div>
      </header>

      <!-- Dashboard Quick Stats Bar -->
      <section class="stats-overview">
        <div class="stat-card">
          <div class="stat-icon-box stat-icon-blue">
            <Users :size="20" />
          </div>
          <div class="stat-info">
            <span class="stat-title">Total Users</span>
            <span class="stat-number">{{ totalData }}</span>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon-box stat-icon-green">
            <ShieldCheck :size="20" />
          </div>
          <div class="stat-info">
            <span class="stat-title">Active Accounts</span>
            <span class="stat-number">{{ activeUserCount }}</span>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon-box stat-icon-purple">
            <FileText :size="20" />
          </div>
          <div class="stat-info">
            <span class="stat-title">Current Page</span>
            <span class="stat-number">{{ currentPage + 1 }} / {{ Math.max(1, totalPages) }}</span>
          </div>
        </div>
      </section>

      <!-- Main Table Container -->
      <section class="content-body">
        <UserTable 
          :users="users"
          :selected-user="selectedUser"
          :loading="loading"
          :current-page="currentPage"
          :page-size="pageSize"
          :total-data="totalData"
          :total-pages="totalPages"
          :sort-by="sortBy"
          :sort-direction="sortDirection"
          @select-user="handleSelectUser"
          @open-create="handleOpenCreate"
          @page-change="handlePageChange"
          @page-size-change="handlePageSizeChange"
          @sort-change="handleSortChange"
        />
      </section>
    </main>

    <!-- Detail Panel Backdrop (Closes on Click) -->
    <div 
      v-if="isDetailOpen" 
      class="detail-backdrop" 
      @click="handleCloseDetail"
    ></div>

    <!-- Right Side-over User Detail Panel -->
    <UserDetailPanel 
      :is-open="isDetailOpen"
      :user="selectedUser"
      @close="handleCloseDetail"
      @edit="handleOpenEdit"
      @delete="handleOpenDelete"
    />

    <!-- User Create / Edit Modal -->
    <UserFormModal 
      :is-open="isFormModalOpen"
      :mode="formModalMode"
      :initial-data="formModalData"
      :loading="formModalLoading"
      :error-message="formErrorMessage"
      @close="isFormModalOpen = false"
      @submit="handleFormSubmit"
    />

    <!-- Delete Confirmation Modal -->
    <ConfirmModal 
      :is-open="isConfirmModalOpen"
      :loading="deleteLoading"
      title="Delete User Account"
      :message="deleteConfirmMessage"
      @cancel="isConfirmModalOpen = false"
      @confirm="handleConfirmDelete"
    />

    <!-- Toast Notification Banner -->
    <Transition name="toast-slide">
      <div v-if="toast.show" class="toast-card" :class="`toast-${toast.type}`">
        <div class="toast-icon">
          <CheckCircle2 v-if="toast.type === 'success'" :size="18" />
          <AlertCircle v-else-if="toast.type === 'error'" :size="18" />
          <Info v-else :size="18" />
        </div>
        <div class="toast-text">{{ toast.message }}</div>
        <button class="toast-close" @click="toast.show = false">
          <X :size="14" />
        </button>
      </div>
    </Transition>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { 
  Users, 
  UserPlus, 
  RotateCw, 
  ShieldCheck, 
  FileText, 
  CheckCircle2, 
  AlertCircle, 
  Info, 
  X 
} from 'lucide-vue-next'
import Sidebar from './components/Sidebar.vue'
import UserTable from './components/UserTable.vue'
import UserDetailPanel from './components/UserDetailPanel.vue'
import UserFormModal from './components/UserFormModal.vue'
import ConfirmModal from './components/ConfirmModal.vue'
import { userApi } from './api/userApi'

// --- State Management ---
const users = ref([])
const totalData = ref(0)
const totalPages = ref(1)
const currentPage = ref(0) // 0-indexed for backend Spring Boot
const pageSize = ref(10)
const sortBy = ref('createdDate')
const sortDirection = ref('DESC')
const loading = ref(false)
const backendConnected = ref(true)

// Detail Panel state
const selectedUser = ref(null)
const isDetailOpen = ref(false)

// Form Modal state (Create / Edit)
const isFormModalOpen = ref(false)
const formModalMode = ref('create')
const formModalData = ref(null)
const formModalLoading = ref(false)
const formErrorMessage = ref('')

// Delete Modal state
const isConfirmModalOpen = ref(false)
const deleteTargetUser = ref(null)
const deleteLoading = ref(false)

// Toast state
const toast = ref({
  show: false,
  message: '',
  type: 'success', // 'success' | 'error' | 'info'
})
let toastTimeout = null

const showToast = (message, type = 'success') => {
  if (toastTimeout) clearTimeout(toastTimeout)
  toast.value = { show: true, message, type }
  toastTimeout = setTimeout(() => {
    toast.value.show = false
  }, 4000)
}

// Compute active user count in current view
const activeUserCount = computed(() => {
  return users.value.filter(u => u.status === 'ACTIVE').length
})

const deleteConfirmMessage = computed(() => {
  if (!deleteTargetUser.value) return 'Are you sure you want to delete this user?'
  const username = deleteTargetUser.value.username
  return `Are you sure you want to delete user @${username}? This action will mark the account as DELETED.`
})

// --- Data Fetching ---
const fetchUsers = async () => {
  loading.value = true
  try {
    const res = await userApi.getUsers(
      currentPage.value,
      pageSize.value,
      sortBy.value,
      sortDirection.value
    )
    
    // Spring Boot PageResponse structure: { page, pageSize, totalData, totalPages, data }
    const pageData = res.data
    users.value = pageData.data || []
    totalData.value = pageData.totalData || 0
    totalPages.value = pageData.totalPages || 1
    backendConnected.value = true

    // Keep selectedUser updated if already open
    if (selectedUser.value) {
      const found = users.value.find(u => u.username === selectedUser.value.username)
      if (found) {
        selectedUser.value = found
      }
    }
  } catch (err) {
    console.error('Failed to load users from API:', err)
    backendConnected.value = false
    const msg = err.response?.data?.message || err.message || 'Cannot connect to User Service on port 8081.'
    showToast(`Error: ${msg}`, 'error')
  } finally {
    loading.value = false
  }
}

// --- User Interaction Handlers ---
const handleSelectUser = (user) => {
  selectedUser.value = user
  isDetailOpen.value = true
}

const handleCloseDetail = () => {
  isDetailOpen.value = false
}

const handleOpenCreate = () => {
  formModalMode.value = 'create'
  formModalData.value = null
  formErrorMessage.value = ''
  isFormModalOpen.value = true
}

const handleOpenEdit = (user) => {
  formModalMode.value = 'edit'
  formModalData.value = { ...user }
  formErrorMessage.value = ''
  isFormModalOpen.value = true
}

const handleOpenDelete = (user) => {
  deleteTargetUser.value = user
  isConfirmModalOpen.value = true
}

// Confirm Delete Handler (Automated close of Detail Panel)
const handleConfirmDelete = async () => {
  if (!deleteTargetUser.value) return
  deleteLoading.value = true
  const deletedUsername = deleteTargetUser.value.username

  try {
    await userApi.deleteUser(deletedUsername)
    showToast(`User @${deletedUsername} has been deleted successfully.`, 'success')
    
    // Close modal
    isConfirmModalOpen.value = false
    deleteTargetUser.value = null

    // CRITICAL USER REQUIREMENT:
    // "kemudian jika berhasil maka panel detail akan tertutup."
    isDetailOpen.value = false
    selectedUser.value = null

    // Reload users list
    await fetchUsers()
  } catch (err) {
    console.error('Failed to delete user:', err)
    const errorMsg = err.response?.data?.message || err.message || 'Could not delete user account'
    showToast(`Failed to delete user: ${errorMsg}`, 'error')
  } finally {
    deleteLoading.value = false
  }
}

// Form Submit Handler (Create / Edit)
const handleFormSubmit = async (payload) => {
  formModalLoading.value = true
  formErrorMessage.value = ''

  try {
    if (formModalMode.value === 'create') {
      const res = await userApi.createUser(payload)
      showToast(`User @${payload.username} created successfully!`, 'success')
      isFormModalOpen.value = false
      await fetchUsers()

      // Automatically highlight the newly created user
      if (res.data?.data) {
        selectedUser.value = res.data.data
        isDetailOpen.value = true
      }
    } else {
      const res = await userApi.updateUser(payload)
      showToast(`User @${payload.username} updated successfully!`, 'success')
      isFormModalOpen.value = false
      
      // Update selectedUser if open
      if (selectedUser.value && selectedUser.value.username === payload.username) {
        selectedUser.value = res.data?.data || { ...selectedUser.value, ...payload }
      }
      await fetchUsers()
    }
  } catch (err) {
    console.error('Form submission failed:', err)
    formErrorMessage.value = err.response?.data?.message || err.message || 'Operation failed. Check your input values.'
  } finally {
    formModalLoading.value = false
  }
}

// Pagination & Sorting Handlers
const handlePageChange = (newPage) => {
  if (newPage >= 0 && newPage < totalPages.value) {
    currentPage.value = newPage
    fetchUsers()
  }
}

const handlePageSizeChange = (newSize) => {
  pageSize.value = newSize
  currentPage.value = 0
  fetchUsers()
}

const handleSortChange = ({ sortBy: newSortBy, sortDirection: newSortDirection }) => {
  sortBy.value = newSortBy
  sortDirection.value = newSortDirection
  currentPage.value = 0
  fetchUsers()
}

onMounted(() => {
  fetchUsers()
})
</script>

<style scoped>
.app-layout {
  display: flex;
  height: 100vh;
  width: 100vw;
  overflow: hidden;
  background-color: var(--bg-app);
}

.main-content {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
  height: 100vh;
  background: radial-gradient(circle at 50% 0%, rgba(99, 102, 241, 0.05) 0%, transparent 70%);
}

/* Header */
.app-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24px 32px 18px;
  border-bottom: 1px solid var(--border-subtle);
  background: rgba(17, 24, 39, 0.4);
  backdrop-filter: blur(8px);
}

.header-left {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.breadcrumb {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: var(--text-faint);
  font-weight: 500;
}

.crumb-active {
  color: var(--text-muted);
}

.title-row {
  display: flex;
  align-items: center;
  gap: 14px;
}

.page-title {
  font-size: 24px;
  font-weight: 800;
  color: #fff;
  letter-spacing: -0.025em;
}

.header-status-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 4px 10px;
  border-radius: var(--radius-full);
  font-size: 11.5px;
  font-weight: 600;
}

.status-online {
  background: rgba(16, 185, 129, 0.1);
  color: #34d399;
  border: 1px solid rgba(16, 185, 129, 0.25);
}

.status-offline {
  background: rgba(239, 68, 68, 0.1);
  color: #f87171;
  border: 1px solid rgba(239, 68, 68, 0.25);
}

.pulse-indicator {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: currentColor;
  box-shadow: 0 0 8px currentColor;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.spin {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* Quick Stats Bar */
.stats-overview {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 20px;
  padding: 24px 32px 8px;
}

.stat-card {
  background: var(--bg-surface);
  border: 1px solid var(--border-subtle);
  border-radius: var(--radius-lg);
  padding: 16px 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  transition: all 0.2s ease;
}

.stat-card:hover {
  border-color: var(--border-medium);
  box-shadow: var(--shadow-sm);
  transform: translateY(-1px);
}

.stat-icon-box {
  width: 44px;
  height: 44px;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-icon-blue {
  background: rgba(99, 102, 241, 0.12);
  color: #818cf8;
}

.stat-icon-green {
  background: rgba(16, 185, 129, 0.12);
  color: #34d399;
}

.stat-icon-purple {
  background: rgba(168, 85, 247, 0.12);
  color: #c084fc;
}

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-title {
  font-size: 12.5px;
  color: var(--text-muted);
  font-weight: 500;
}

.stat-number {
  font-size: 20px;
  font-weight: 800;
  color: #fff;
  letter-spacing: -0.02em;
}

/* Content Body */
.content-body {
  padding: 16px 32px 32px;
  flex: 1;
}

/* Detail Panel Backdrop */
.detail-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.45);
  backdrop-filter: blur(2px);
  z-index: 90;
  animation: fadeIn 0.2s ease-out;
}

/* Toast Notification */
.toast-card {
  position: fixed;
  bottom: 28px;
  right: 28px;
  z-index: 300;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 18px;
  border-radius: var(--radius-md);
  background: var(--bg-surface-elevated);
  border: 1px solid var(--border-medium);
  box-shadow: var(--shadow-lg);
  max-width: 420px;
}

.toast-success {
  border-left: 4px solid #10b981;
}
.toast-success .toast-icon {
  color: #34d399;
}

.toast-error {
  border-left: 4px solid #ef4444;
}
.toast-error .toast-icon {
  color: #f87171;
}

.toast-info {
  border-left: 4px solid #6366f1;
}
.toast-info .toast-icon {
  color: #818cf8;
}

.toast-text {
  font-size: 13.5px;
  color: #fff;
  font-weight: 500;
  line-height: 1.4;
}

.toast-close {
  background: transparent;
  border: none;
  color: var(--text-faint);
  cursor: pointer;
  padding: 4px;
  margin-left: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  transition: color 0.15s;
}
.toast-close:hover {
  color: #fff;
}

/* Toast Transition */
.toast-slide-enter-active,
.toast-slide-leave-active {
  transition: all 0.25s cubic-bezier(0.16, 1, 0.3, 1);
}

.toast-slide-enter-from {
  opacity: 0;
  transform: translateY(20px) scale(0.95);
}

.toast-slide-leave-to {
  opacity: 0;
  transform: translateY(10px);
}
</style>
