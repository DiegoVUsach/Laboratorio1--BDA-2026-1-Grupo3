<script setup lang="ts">
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore, type Role } from '../stores/auth'

const username = ref('')
const password = ref('')
const role = ref<Role>('leader')
const errorMessage = ref('')

const router = useRouter()
const route = useRoute()
const auth = useAuthStore()

const handleLogin = () => {
  errorMessage.value = ''

  if (!username.value.trim() || !password.value) {
    errorMessage.value = 'Completa usuario y contrasena para continuar.'
    return
  }

  auth.login({
    username: username.value.trim(),
    role: role.value
  })

  const redirectTarget = route.query.redirect?.toString()
  if (redirectTarget) {
    router.push(redirectTarget)
    return
  }

  router.push(role.value === 'leader' ? '/admin/dashboard' : '/user/home')
}
</script>

<template>
  <div class="min-h-screen px-6 py-10">
    <div class="mx-auto flex min-h-[70vh] max-w-5xl flex-col items-center justify-center gap-10 lg:flex-row">
      <div class="max-w-lg space-y-6">
        <p class="text-xs uppercase tracking-[0.4em] text-amber-300">Liga de Clanes</p>
        <h1 class="font-display text-4xl font-bold text-slate-100 md:text-5xl">
          Unete a la batalla y conquista el mundo junto a tu clan.
        </h1>
        <p class="text-base text-slate-300">
          Acceso exclusivo para lideres y miembros.
        </p>
        <div class="flex flex-wrap gap-3 text-xs text-slate-400">
        </div>
      </div>

      <div class="w-full max-w-md rounded-2xl border border-slate-800 bg-slate-900/70 p-8 shadow-xl shadow-slate-950/60">
        <div class="space-y-2">
          <h2 class="font-display text-2xl font-semibold text-slate-100">Iniciar sesion</h2>
          <p class="text-sm text-slate-400">Escribe tus credenciales para continuar.</p>
        </div>

        <form class="mt-6 space-y-4" @submit.prevent="handleLogin">
          <div>
            <label class="text-xs uppercase tracking-widest text-slate-400">Usuario Unico</label>
            <input
              v-model="username"
              type="text"
              placeholder="kraken.leader"
              class="mt-2 w-full rounded-lg border border-slate-700 bg-slate-950/60 px-4 py-3 text-sm text-slate-100 placeholder:text-slate-600 focus:border-amber-300 focus:outline-none"
            />
          </div>

          <div>
            <label class="text-xs uppercase tracking-widest text-slate-400">Contrasena</label>
            <input
              v-model="password"
              type="password"
              placeholder="••••••••"
              class="mt-2 w-full rounded-lg border border-slate-700 bg-slate-950/60 px-4 py-3 text-sm text-slate-100 placeholder:text-slate-600 focus:border-amber-300 focus:outline-none"
            />
          </div>

          <div>
            <label class="text-xs uppercase tracking-widest text-slate-400">Rol</label>
            <select
              v-model="role"
              class="mt-2 w-full rounded-lg border border-slate-700 bg-slate-950/60 px-4 py-3 text-sm text-slate-100 focus:border-amber-300 focus:outline-none"
            >
              <option value="leader">Leader</option>
              <option value="member">Member</option>
            </select>
          </div>

          <p v-if="errorMessage" class="rounded-lg border border-rose-500/40 bg-rose-500/10 px-4 py-2 text-xs text-rose-200">
            {{ errorMessage }}
          </p>

          <button
            type="submit"
            class="w-full rounded-lg bg-amber-400 px-4 py-3 text-sm font-semibold text-slate-950 transition hover:bg-amber-300"
          >
            Entrar al clan
          </button>
        </form>
      </div>
    </div>
  </div>
</template>
