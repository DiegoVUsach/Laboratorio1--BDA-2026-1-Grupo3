import axios from 'axios'

const apiClient = axios.create({
	baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080/api',
	headers: {
		'Content-Type': 'application/json'
	}
})

export const membersApi = {
	getAll: () => apiClient.get('/members'),
	getById: (id) => apiClient.get(`/members/${id}`),
	create: (payload) => apiClient.post('/members', payload),
	update: (id, payload) => apiClient.put(`/members/${id}`, payload),
	remove: (id) => apiClient.delete(`/members/${id}`)
}

export const raidsApi = {
	getAll: () => apiClient.get('/raids'),
	getById: (id) => apiClient.get(`/raids/${id}`),
	create: (payload) => apiClient.post('/raids', payload),
	update: (id, payload) => apiClient.put(`/raids/${id}`, payload),
	remove: (id) => apiClient.delete(`/raids/${id}`)
}

export const eventsApi = {
	getAll: () => apiClient.get('/events'),
	getById: (id) => apiClient.get(`/events/${id}`),
	create: (payload) => apiClient.post('/events', payload),
	update: (id, payload) => apiClient.put(`/events/${id}`, payload),
	remove: (id) => apiClient.delete(`/events/${id}`)
}

export const itemsApi = {
	getAll: () => apiClient.get('/items'),
	getById: (id) => apiClient.get(`/items/${id}`),
	create: (payload) => apiClient.post('/items', payload),
	update: (id, payload) => apiClient.put(`/items/${id}`, payload),
	remove: (id) => apiClient.delete(`/items/${id}`)
}

export const dkpApi = {
	getHistory: (memberId) => apiClient.get(`/dkp/${memberId}`),
	add: (memberId, payload) => apiClient.post(`/dkp/${memberId}`, payload)
}

export default apiClient
