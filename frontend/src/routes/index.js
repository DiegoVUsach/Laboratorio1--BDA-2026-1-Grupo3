import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const routes = [
	{
		path: '/',
		redirect: '/login'
	},
	{
		path: '/login',
		name: 'login',
		component: () => import('../pages/Login.vue'),
		meta: { public: true }
	},
	{
		path: '/admin/dashboard',
		name: 'leader-dashboard',
		component: () => import('../pages/LeaderDashboard.vue'),
		meta: { requiresAuth: true, role: 'leader' }
	},
	{
		path: '/user/home',
		name: 'user-home',
		component: () => import('../pages/UserHome.vue'),
		meta: { requiresAuth: true, role: 'member' }
	}
]

const router = createRouter({
	history: createWebHistory(),
	routes
})

router.beforeEach((to) => {
	const auth = useAuthStore()

	if (to.meta?.public) {
		return true
	}

	if (to.meta?.requiresAuth && !auth.isAuthenticated) {
		return { path: '/login', query: { redirect: to.fullPath } }
	}

	if (to.meta?.role && auth.role !== to.meta.role) {
		return auth.role === 'leader' ? '/admin/dashboard' : '/user/home'
	}

	return true
})

export default router
