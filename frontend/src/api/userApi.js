import axios from 'axios'

const api = axios.create({
  baseURL: '', // Using Vite proxy to http://localhost:8081
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
})

export const userApi = {
  // Get paginated users
  getUsers(page = 0, pageSize = 10, sortBy = 'createdDate', sortDirection = 'DESC') {
    return api.get('/users', {
      params: {
        page,
        pageSize,
        sortBy,
        sortDirection,
      },
    })
  },

  // Get user details by username
  getUserByUsername(username) {
    return api.get(`/users/${encodeURIComponent(username)}`)
  },

  // Create new user
  createUser(userData) {
    return api.post('/users', userData)
  },

  // Update existing user
  updateUser(userData) {
    return api.patch('/users', userData)
  },

  // Delete user (soft delete)
  deleteUser(username) {
    return api.delete(`/users/${encodeURIComponent(username)}`)
  },
}

export default api
