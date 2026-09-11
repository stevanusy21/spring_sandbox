<template>
  <div class="table-container">
    <!-- Top Action Toolbar -->
    <div class="toolbar">
      <div class="toolbar-left">
        <div class="search-input-wrapper">
          <Search :size="16" class="search-icon" />
          <input 
            v-model="searchQuery" 
            type="text" 
            class="search-input" 
            placeholder="Filter by username / name..." 
          />
        </div>

        <div class="sort-controls">
          <span class="sort-label">Sort by:</span>
          <select v-model="localSortBy" class="form-select sort-select" @change="handleSortChange">
            <option value="createdDate">Created Date</option>
            <option value="username">Username</option>
            <option value="fullName">Full Name</option>
            <option value="email">Email</option>
          </select>

          <button class="btn btn-secondary btn-icon" @click="toggleSortDirection" :title="`Direction: ${localSortDirection}`">
            <ArrowDownWideNarrow v-if="localSortDirection === 'DESC'" :size="16" />
            <ArrowUpNarrowWide v-else :size="16" />
          </button>
        </div>
      </div>

      <div class="toolbar-right">
        <button class="btn btn-primary create-btn" @click="$emit('open-create')">
          <UserPlus :size="16" />
          <span>Create User</span>
        </button>
      </div>
    </div>

    <!-- Data Table Box -->
    <div class="table-card">
      <div class="table-wrapper">
        <table class="data-table">
          <thead>
            <tr>
              <th>USER</th>
              <th>CONTACT</th>
              <th>ROLE</th>
              <th>STATUS</th>
              <th>CREATED AT</th>
              <th class="text-right">ACTION</th>
            </tr>
          </thead>
          <tbody>
            <!-- Loading Skeletons -->
            <tr v-if="loading" v-for="n in 5" :key="n" class="skeleton-row">
              <td colspan="6">
                <div class="skeleton-bar"></div>
              </td>
            </tr>

            <!-- Empty State -->
            <tr v-else-if="filteredUsers.length === 0">
              <td colspan="6" class="empty-cell">
                <div class="empty-state">
                  <UserX :size="36" class="empty-icon" />
                  <h4>No Users Found</h4>
                  <p v-if="searchQuery">No matching user records for "{{ searchQuery }}"</p>
                  <p v-else>No user records available in the database yet.</p>
                  <button v-if="!searchQuery" class="btn btn-primary btn-sm" @click="$emit('open-create')">
                    Create the First User
                  </button>
                </div>
              </td>
            </tr>

            <!-- Data Rows -->
            <tr 
              v-else 
              v-for="user in filteredUsers" 
              :key="user.id"
              class="data-row"
              :class="{ 'is-selected': selectedUser && selectedUser.username === user.username }"
              @click="$emit('select-user', user)"
            >
              <td>
                <div class="user-profile-cell">
                  <div class="user-avatar">
                    {{ getInitials(user.fullName || user.username) }}
                  </div>
                  <div class="user-names">
                    <span class="name">{{ user.fullName || user.username }}</span>
                    <span class="handle">@{{ user.username }}</span>
                  </div>
                </div>
              </td>

              <td>
                <div class="contact-cell">
                  <span class="email">{{ user.email }}</span>
                  <span class="phone font-mono">{{ user.phoneNumber }}</span>
                </div>
              </td>

              <td>
                <span class="role-badge">{{ user.role || 'USER' }}</span>
              </td>

              <td>
                <span class="badge" :class="`badge-${(user.status || 'ACTIVE').toLowerCase()}`">
                  <span class="dot"></span>
                  {{ user.status || 'ACTIVE' }}
                </span>
              </td>

              <td>
                <span class="date-cell">{{ formatDate(user.createdDate) }}</span>
              </td>

              <td class="text-right" @click.stop>
                <button 
                  class="btn btn-ghost btn-sm view-btn" 
                  @click="$emit('select-user', user)"
                  title="View User Details"
                >
                  <span>Details</span>
                  <ChevronRight :size="14" />
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Pagination Bar -->
      <div class="pagination-footer">
        <div class="pagination-info">
          <span>Showing </span>
          <span class="font-bold">{{ startIndex }}</span>
          <span> to </span>
          <span class="font-bold">{{ endIndex }}</span>
          <span> of </span>
          <span class="font-bold">{{ totalData }}</span>
          <span> users</span>

          <div class="page-size-selector">
            <span class="size-label">Per page:</span>
            <select :value="pageSize" class="form-select size-select" @change="onPageSizeChange">
              <option :value="5">5</option>
              <option :value="10">10</option>
              <option :value="25">25</option>
              <option :value="50">50</option>
            </select>
          </div>
        </div>

        <div class="pagination-nav">
          <button 
            class="btn btn-secondary btn-icon" 
            :disabled="currentPage <= 0 || loading"
            @click="$emit('page-change', currentPage - 1)"
            title="Previous Page"
          >
            <ChevronLeft :size="16" />
          </button>

          <span class="page-indicator">
            Page <strong class="text-white">{{ currentPage + 1 }}</strong> of {{ Math.max(1, totalPages) }}
          </span>

          <button 
            class="btn btn-secondary btn-icon" 
            :disabled="currentPage >= totalPages - 1 || loading"
            @click="$emit('page-change', currentPage + 1)"
            title="Next Page"
          >
            <ChevronRight :size="16" />
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { 
  Search, 
  UserPlus, 
  UserX, 
  ChevronRight, 
  ChevronLeft, 
  ArrowDownWideNarrow, 
  ArrowUpNarrowWide 
} from 'lucide-vue-next'

const props = defineProps({
  users: {
    type: Array,
    default: () => [],
  },
  selectedUser: {
    type: Object,
    default: null,
  },
  loading: {
    type: Boolean,
    default: false,
  },
  currentPage: {
    type: Number,
    default: 0,
  },
  pageSize: {
    type: Number,
    default: 10,
  },
  totalData: {
    type: Number,
    default: 0,
  },
  totalPages: {
    type: Number,
    default: 1,
  },
  sortBy: {
    type: String,
    default: 'createdDate',
  },
  sortDirection: {
    type: String,
    default: 'DESC',
  },
})

const emit = defineEmits(['select-user', 'open-create', 'page-change', 'page-size-change', 'sort-change'])

const searchQuery = ref('')
const localSortBy = ref(props.sortBy)
const localSortDirection = ref(props.sortDirection)

const filteredUsers = computed(() => {
  if (!searchQuery.value) return props.users
  const q = searchQuery.value.toLowerCase().trim()
  return props.users.filter(u => 
    (u.username && u.username.toLowerCase().includes(q)) ||
    (u.fullName && u.fullName.toLowerCase().includes(q)) ||
    (u.email && u.email.toLowerCase().includes(q))
  )
})

const startIndex = computed(() => {
  if (props.totalData === 0) return 0
  return props.currentPage * props.pageSize + 1
})

const endIndex = computed(() => {
  return Math.min((props.currentPage + 1) * props.pageSize, props.totalData)
})

const getInitials = (name) => {
  if (!name) return 'U'
  const parts = name.trim().split(' ')
  if (parts.length >= 2) return (parts[0][0] + parts[1][0]).toUpperCase()
  return name.slice(0, 2).toUpperCase()
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  try {
    const d = new Date(dateStr)
    return d.toLocaleDateString('id-ID', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
    })
  } catch {
    return dateStr
  }
}

const handleSortChange = () => {
  emit('sort-change', { sortBy: localSortBy.value, sortDirection: localSortDirection.value })
}

const toggleSortDirection = () => {
  localSortDirection.value = localSortDirection.value === 'ASC' ? 'DESC' : 'ASC'
  handleSortChange()
}

const onPageSizeChange = (e) => {
  emit('page-size-change', parseInt(e.target.value, 10))
}
</script>

<style scoped>
.table-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
  height: 100%;
}

/* Toolbar */
.toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14px;
  flex-wrap: wrap;
}

.toolbar-left {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
}

.search-input-wrapper {
  position: relative;
  width: 280px;
}

.search-icon {
  position: absolute;
  left: 11px;
  top: 50%;
  transform: translateY(-50%);
  color: var(--text-faint);
  pointer-events: none;
}

.search-input {
  width: 100%;
  padding: 8px 12px 8px 34px;
  background: var(--bg-surface);
  border: 1px solid var(--border-subtle);
  border-radius: var(--radius-md);
  color: var(--text-main);
  font-size: 13.5px;
  outline: none;
  transition: all 0.18s ease;
}

.search-input:focus {
  border-color: var(--border-focus);
  background: var(--bg-surface-elevated);
}

.sort-controls {
  display: flex;
  align-items: center;
  gap: 8px;
}

.sort-label {
  font-size: 12.5px;
  color: var(--text-muted);
}

.sort-select {
  padding: 6px 10px;
  width: auto;
  font-size: 13px;
  background: var(--bg-surface);
}

.create-btn {
  padding: 8px 18px;
}

/* Table Card */
.table-card {
  background: var(--bg-surface);
  border: 1px solid var(--border-subtle);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-md);
  display: flex;
  flex-direction: column;
  flex: 1;
  overflow: hidden;
}

.table-wrapper {
  flex: 1;
  overflow-y: auto;
  overflow-x: auto;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
  text-align: left;
}

.data-table thead {
  position: sticky;
  top: 0;
  background: var(--bg-surface);
  z-index: 10;
  box-shadow: 0 1px 0 var(--border-subtle);
}

.data-table th {
  padding: 12px 18px;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.06em;
  color: var(--text-faint);
  text-transform: uppercase;
  white-space: nowrap;
}

.data-table td {
  padding: 14px 18px;
  font-size: 13.5px;
  border-bottom: 1px solid var(--border-subtle);
  color: var(--text-main);
}

.data-row {
  cursor: pointer;
  transition: background 0.12s ease;
}

.data-row:hover {
  background: var(--bg-surface-hover);
}

.data-row.is-selected {
  background: rgba(99, 102, 241, 0.12);
}

/* User Profile Cell */
.user-profile-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-avatar {
  width: 36px;
  height: 36px;
  border-radius: var(--radius-md);
  background: linear-gradient(135deg, #4f46e5 0%, #7c3aed 100%);
  color: #fff;
  font-size: 13px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.user-names {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.user-names .name {
  font-weight: 600;
  color: #fff;
}

.user-names .handle {
  font-size: 12px;
  color: var(--text-muted);
}

/* Contact Cell */
.contact-cell {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.contact-cell .email {
  color: var(--text-main);
  font-size: 13px;
}

.contact-cell .phone {
  font-size: 12px;
  color: var(--text-muted);
}

.role-badge {
  font-size: 11px;
  font-weight: 700;
  background: var(--bg-surface-elevated);
  border: 1px solid var(--border-subtle);
  padding: 3px 8px;
  border-radius: var(--radius-sm);
  color: #cbd5e1;
}

.date-cell {
  color: var(--text-muted);
  font-size: 13px;
}

.text-right {
  text-align: right;
}

.view-btn {
  font-size: 12.5px;
  padding: 4px 10px;
}

/* Empty State */
.empty-cell {
  padding: 60px 20px !important;
  text-align: center;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.empty-icon {
  color: var(--text-faint);
  margin-bottom: 4px;
}

.empty-state h4 {
  font-size: 16px;
  color: #fff;
}

.empty-state p {
  font-size: 13px;
  color: var(--text-muted);
}

/* Skeleton Loading */
.skeleton-row td {
  padding: 20px 18px;
}
.skeleton-bar {
  height: 18px;
  background: linear-gradient(90deg, var(--bg-surface-elevated) 25%, var(--bg-surface-hover) 50%, var(--bg-surface-elevated) 75%);
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
  border-radius: var(--radius-sm);
}

@keyframes shimmer {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}

/* Pagination Footer */
.pagination-footer {
  padding: 12px 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-top: 1px solid var(--border-subtle);
  background: var(--bg-surface);
  flex-wrap: wrap;
  gap: 12px;
}

.pagination-info {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: var(--text-muted);
}

.font-bold {
  font-weight: 700;
  color: #fff;
}

.page-size-selector {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-left: 16px;
  padding-left: 16px;
  border-left: 1px solid var(--border-subtle);
}

.size-label {
  font-size: 12px;
}

.size-select {
  padding: 3px 8px;
  width: auto;
  font-size: 12.5px;
  background: var(--bg-surface-elevated);
}

.pagination-nav {
  display: flex;
  align-items: center;
  gap: 12px;
}

.page-indicator {
  font-size: 13px;
  color: var(--text-muted);
}
</style>
