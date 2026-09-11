<template>
  <aside 
    class="sidebar" 
    :class="{ 'is-collapsed': isCollapsed, 'is-resizing': isResizing }"
    :style="{ width: isCollapsed ? '72px' : `${currentWidth}px` }"
  >
    <!-- Top Brand / Logo -->
    <div class="sidebar-header">
      <div class="logo-box">
        <div class="logo-icon">
          <Layers :size="20" class="logo-svg" />
        </div>
        <div v-if="!isCollapsed" class="logo-text">
          <span class="brand-title">Sandbox</span>
          <span class="brand-badge">CLOUD</span>
        </div>
      </div>

      <!-- Quick collapse toggle button -->
      <button 
        class="collapse-btn" 
        :title="isCollapsed ? 'Expand Sidebar' : 'Collapse Sidebar'"
        @click="toggleCollapse"
      >
        <ChevronLeft v-if="!isCollapsed" :size="16" />
        <ChevronRight v-else :size="16" />
      </button>
    </div>

    <!-- Navigation Menus -->
    <nav class="sidebar-nav">
      <div v-if="!isCollapsed" class="nav-section-label">MAIN SERVICES</div>

      <a 
        href="#" 
        class="nav-item active" 
        :title="isCollapsed ? 'User Management' : ''"
      >
        <Users :size="18" class="nav-icon" />
        <span v-if="!isCollapsed" class="nav-label">User Service</span>
        <span v-if="!isCollapsed && userCount !== null" class="nav-pill">{{ userCount }}</span>
      </a>

      <a 
        href="#" 
        class="nav-item disabled" 
        :title="isCollapsed ? 'Order Service (Planned)' : ''"
      >
        <ShoppingBag :size="18" class="nav-icon" />
        <span v-if="!isCollapsed" class="nav-label">Order Service</span>
        <span v-if="!isCollapsed" class="nav-tag">Upcoming</span>
      </a>

      <a 
        href="#" 
        class="nav-item disabled" 
        :title="isCollapsed ? 'Inventory Service (Planned)' : ''"
      >
        <Box :size="18" class="nav-icon" />
        <span v-if="!isCollapsed" class="nav-label">Inventory</span>
        <span v-if="!isCollapsed" class="nav-tag">Upcoming</span>
      </a>

      <div v-if="!isCollapsed" class="nav-section-label system-label">SYSTEM & CONFIG</div>

      <a 
        href="#" 
        class="nav-item disabled" 
        :title="isCollapsed ? 'RabbitMQ Events' : ''"
      >
        <Activity :size="18" class="nav-icon" />
        <span v-if="!isCollapsed" class="nav-label">Message Broker</span>
      </a>

      <a 
        href="http://localhost:8081/swagger-ui.html" 
        target="_blank" 
        class="nav-item external-link"
        :title="isCollapsed ? 'Swagger OpenAPI Docs' : ''"
      >
        <ExternalLink :size="18" class="nav-icon" />
        <span v-if="!isCollapsed" class="nav-label">Swagger UI</span>
      </a>
    </nav>

    <!-- Bottom Status Card -->
    <div class="sidebar-footer" v-if="!isCollapsed">
      <div class="service-health-card">
        <div class="health-indicator">
          <span class="dot"></span>
          <span class="health-text">Port 8081 Connected</span>
        </div>
        <p class="service-version">Spring Boot 4.1.1</p>
      </div>
    </div>

    <!-- Draggable Resize Handle on Right Border -->
    <div 
      class="resizer-handle"
      :class="{ active: isResizing }"
      @mousedown="startResizing"
      title="Drag to resize sidebar"
    >
      <div class="handle-bar"></div>
    </div>
  </aside>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { 
  Layers, 
  Users, 
  ShoppingBag, 
  Box, 
  Activity, 
  ExternalLink, 
  ChevronLeft, 
  ChevronRight 
} from 'lucide-vue-next'

const props = defineProps({
  userCount: {
    type: Number,
    default: null,
  },
})

// State for sidebar width & resizing
const isCollapsed = ref(false)
const currentWidth = ref(260)
const lastExpandedWidth = ref(260)
const isResizing = ref(false)

const MIN_WIDTH = 190
const MAX_WIDTH = 420

const toggleCollapse = () => {
  if (isCollapsed.value) {
    isCollapsed.value = false
    currentWidth.value = lastExpandedWidth.value
  } else {
    lastExpandedWidth.value = currentWidth.value
    isCollapsed.value = true
  }
}

// Mouse drag resizing logic
const startResizing = (e) => {
  if (isCollapsed.value) {
    isCollapsed.value = false
  }
  isResizing.value = true
  document.body.style.cursor = 'col-resize'
  document.body.style.userSelect = 'none'

  window.addEventListener('mousemove', handleMouseMove)
  window.addEventListener('mouseup', stopResizing)
}

const handleMouseMove = (e) => {
  if (!isResizing.value) return
  let newWidth = e.clientX
  if (newWidth < MIN_WIDTH) {
    newWidth = MIN_WIDTH
  } else if (newWidth > MAX_WIDTH) {
    newWidth = MAX_WIDTH
  }
  currentWidth.value = newWidth
  lastExpandedWidth.value = newWidth
}

const stopResizing = () => {
  if (!isResizing.value) return
  isResizing.value = false
  document.body.style.cursor = ''
  document.body.style.userSelect = ''

  window.removeEventListener('mousemove', handleMouseMove)
  window.removeEventListener('mouseup', stopResizing)
}

onBeforeUnmount(() => {
  window.removeEventListener('mousemove', handleMouseMove)
  window.removeEventListener('mouseup', stopResizing)
})
</script>

<style scoped>
.sidebar {
  position: relative;
  height: 100vh;
  background: var(--bg-surface);
  border-right: 1px solid var(--border-subtle);
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
  transition: width 0.15s cubic-bezier(0.4, 0, 0.2, 1);
  user-select: none;
  z-index: 20;
}

.sidebar.is-resizing {
  transition: none; /* Disable transition during drag for 60fps smoothness */
}

.sidebar-header {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  border-bottom: 1px solid var(--border-subtle);
}

.logo-box {
  display: flex;
  align-items: center;
  gap: 12px;
  overflow: hidden;
}

.logo-icon {
  width: 36px;
  height: 36px;
  border-radius: var(--radius-md);
  background: linear-gradient(135deg, #6366f1 0%, #4338ca 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  box-shadow: 0 2px 10px rgba(99, 102, 241, 0.35);
  flex-shrink: 0;
}

.logo-text {
  display: flex;
  align-items: center;
  gap: 6px;
  white-space: nowrap;
}

.brand-title {
  font-size: 16px;
  font-weight: 800;
  letter-spacing: -0.03em;
  color: #fff;
}

.brand-badge {
  font-size: 9px;
  font-weight: 700;
  background: rgba(99, 102, 241, 0.2);
  color: #a5b4fc;
  padding: 2px 6px;
  border-radius: var(--radius-sm);
  letter-spacing: 0.08em;
}

.collapse-btn {
  background: var(--bg-surface-elevated);
  border: 1px solid var(--border-subtle);
  color: var(--text-muted);
  width: 26px;
  height: 26px;
  border-radius: var(--radius-sm);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.18s ease;
  flex-shrink: 0;
}

.collapse-btn:hover {
  color: var(--text-main);
  background: var(--bg-surface-hover);
  border-color: rgba(255, 255, 255, 0.2);
}

.sidebar.is-collapsed .collapse-btn {
  margin: 0 auto;
}

/* Navigation */
.sidebar-nav {
  flex: 1;
  padding: 16px 10px;
  display: flex;
  flex-direction: column;
  gap: 4px;
  overflow-y: auto;
  overflow-x: hidden;
}

.nav-section-label {
  font-size: 10.5px;
  font-weight: 700;
  color: var(--text-faint);
  letter-spacing: 0.08em;
  padding: 10px 10px 4px;
  white-space: nowrap;
}

.system-label {
  margin-top: 14px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 9px 12px;
  border-radius: var(--radius-md);
  color: var(--text-muted);
  text-decoration: none;
  font-size: 13.5px;
  font-weight: 600;
  transition: all 0.15s ease;
  white-space: nowrap;
  position: relative;
}

.nav-item:hover:not(.disabled) {
  background: var(--bg-surface-hover);
  color: var(--text-main);
}

.nav-item.active {
  background: rgba(99, 102, 241, 0.15);
  color: #818cf8;
}

.nav-item.active::before {
  content: '';
  position: absolute;
  left: 0;
  top: 6px;
  bottom: 6px;
  width: 3px;
  background: var(--primary);
  border-radius: 0 var(--radius-sm) var(--radius-sm) 0;
}

.nav-item.disabled {
  opacity: 0.45;
  cursor: not-allowed;
}

.nav-icon {
  flex-shrink: 0;
}

.nav-pill {
  margin-left: auto;
  background: rgba(99, 102, 241, 0.2);
  color: #a5b4fc;
  font-size: 11px;
  font-weight: 700;
  padding: 2px 7px;
  border-radius: var(--radius-full);
}

.nav-tag {
  margin-left: auto;
  font-size: 10px;
  color: var(--text-faint);
  background: rgba(255, 255, 255, 0.05);
  padding: 2px 6px;
  border-radius: var(--radius-sm);
}

/* Footer Card */
.sidebar-footer {
  padding: 14px;
  border-top: 1px solid var(--border-subtle);
}

.service-health-card {
  background: var(--bg-surface-elevated);
  border: 1px solid var(--border-subtle);
  border-radius: var(--radius-md);
  padding: 10px 12px;
}

.health-indicator {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  font-weight: 600;
  color: #34d399;
}

.health-indicator .dot {
  width: 7px;
  height: 7px;
  background: #10b981;
  border-radius: 50%;
  box-shadow: 0 0 8px #10b981;
  animation: pulseGlow 2s infinite ease-in-out;
}

.service-version {
  font-size: 10.5px;
  color: var(--text-faint);
  margin-top: 4px;
}

/* Resizer Handle (Draggable Right Border) */
.resizer-handle {
  position: absolute;
  top: 0;
  right: -5px;
  width: 10px;
  height: 100%;
  cursor: col-resize;
  z-index: 30;
  display: flex;
  align-items: center;
  justify-content: center;
}

.handle-bar {
  width: 2px;
  height: 32px;
  background: transparent;
  border-radius: 9999px;
  transition: all 0.2s ease;
}

.resizer-handle:hover .handle-bar,
.resizer-handle.active .handle-bar {
  background: var(--primary);
  box-shadow: 0 0 8px var(--primary);
  height: 48px;
  width: 3px;
}

/* Collapsed Mode Overrides */
.sidebar.is-collapsed {
  align-items: center;
}
.sidebar.is-collapsed .sidebar-header {
  padding: 0;
  justify-content: center;
}
.sidebar.is-collapsed .logo-box {
  display: none;
}
.sidebar.is-collapsed .nav-item {
  justify-content: center;
  padding: 10px;
}
</style>
