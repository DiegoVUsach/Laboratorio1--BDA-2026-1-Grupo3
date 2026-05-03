<script setup lang="ts">
import { useAuthStore } from '../stores/auth'

const auth = useAuthStore()
</script>

<template>
  <div class="px-6 py-10">
    <div class="mx-auto max-w-4xl space-y-6">
      <p class="text-xs uppercase tracking-[0.4em] text-sky-300">Zona de miembro</p>
      <h1 class="font-display text-3xl font-semibold text-slate-100 md:text-4xl">
        Bienvenido, {{ auth.user?.username || 'aventurero' }}
      </h1>
      <p class="text-sm text-slate-400">
        Aqui veras tus DKP, tus items y el calendario de eventos. Esta vista es un placeholder para el rol Member.
      </p>

      <div class="grid gap-4 md:grid-cols-2">
        <div class="rounded-2xl border border-slate-800 bg-slate-900/70 p-6 shadow-lg shadow-slate-950/60">
          <h2 class="font-display text-lg text-slate-100">Mis DKP</h2>
          <p class="mt-3 text-3xl font-semibold text-amber-200">1,250</p>
          <p class="mt-2 text-xs text-slate-400">Sincroniza con la API para datos reales.</p>
        </div>
        <div class="rounded-2xl border border-slate-800 bg-slate-900/70 p-6 shadow-lg shadow-slate-950/60">
          <h2 class="font-display text-lg text-slate-100">Proximo raid</h2>
          <p class="mt-3 text-sm text-slate-300">Cripta Sombria - 06/05/2026</p>
          <p class="mt-2 text-xs text-slate-400">Check-in disponible 1h antes.</p>
        </div>
      </div>
    </div>
  </div>
</template>
