import { defineStore } from 'pinia'

export type Role = 'leader' | 'member'

export interface UserProfile {
  username: string
  role: Role
}

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null as UserProfile | null
  }),
  getters: {
    isAuthenticated: (state) => Boolean(state.user),
    role: (state) => state.user?.role ?? 'member'
  },
  actions: {
    login(payload: { username: string; role: Role }) {
      this.user = {
        username: payload.username,
        role: payload.role
      }
    },
    logout() {
      this.user = null
    }
  }
})
