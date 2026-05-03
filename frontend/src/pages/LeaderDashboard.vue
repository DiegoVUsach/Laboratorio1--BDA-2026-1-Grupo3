<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { useMembersStore, type MemberProfile } from '../stores/members'

const router = useRouter()
const auth = useAuthStore()
const membersStore = useMembersStore()

const members = computed(() => membersStore.members)

const modalOpen = ref(false)
const modalType = ref<'item' | 'dkp'>('item')
const modalMember = ref<MemberProfile | null>(null)
const itemName = ref('')
const dkpDelta = ref(50)

const raidForm = reactive({
  title: '',
  date: '',
  notes: ''
})

const eventForm = reactive({
  title: '',
  date: '',
  notes: ''
})

const systemMessage = ref('')

const openModal = (member: MemberProfile, type: 'item' | 'dkp') => {
  modalMember.value = member
  modalType.value = type
  modalOpen.value = true
  itemName.value = ''
  dkpDelta.value = 50
}

const closeModal = () => {
  modalOpen.value = false
  modalMember.value = null
  itemName.value = ''
  dkpDelta.value = 50
}

const confirmModal = () => {
  if (!modalMember.value) return

  if (modalType.value === 'item') {
    membersStore.assignItem(modalMember.value.id, itemName.value.trim())
  } else {
    membersStore.addDkp(modalMember.value.id, Number(dkpDelta.value))
  }

  closeModal()
}

const removeMember = (member: MemberProfile) => {
  if (window.confirm(`Expulsar a ${member.name}?`)) {
    membersStore.removeMember(member.id)
  }
}

const submitRaid = () => {
  if (!raidForm.title || !raidForm.date) {
    systemMessage.value = 'Completa nombre y fecha del raid.'
    return
  }
  systemMessage.value = `Raid "${raidForm.title}" programado para ${raidForm.date}.`
  raidForm.title = ''
  raidForm.date = ''
  raidForm.notes = ''
}

const submitEvent = () => {
  if (!eventForm.title || !eventForm.date) {
    systemMessage.value = 'Completa nombre y fecha del evento.'
    return
  }
  systemMessage.value = `Evento "${eventForm.title}" creado para ${eventForm.date}.`
  eventForm.title = ''
  eventForm.date = ''
  eventForm.notes = ''
}

const logout = () => {
  auth.logout()
  router.push('/login')
}
</script>

<template>
  <div class="px-6 py-8">
    <header class="mx-auto flex max-w-6xl flex-col gap-6 lg:flex-row lg:items-center lg:justify-between">
      <div>
        <p class="text-xs uppercase tracking-[0.4em] text-amber-300">Panel de Lider</p>
        <h1 class="font-display text-3xl font-semibold text-slate-100 md:text-4xl">Dashboard de la hermandad</h1>
        <p class="text-sm text-slate-400">Gestion de miembros, DKP y botin en tiempo real.</p>
      </div>
      <div class="flex items-center gap-4">
        <button
          class="rounded-lg border border-slate-700 px-4 py-2 text-xs uppercase tracking-widest text-slate-300 hover:border-amber-300 hover:text-amber-200"
          @click="logout"
        >
          Cerrar sesion
        </button>
      </div>
    </header>

    <section class="mx-auto mt-10 grid max-w-6xl gap-8 lg:grid-cols-[1.6fr,1fr]">
      <div class="space-y-6">
        <div class="rounded-2xl border border-slate-800 bg-slate-900/70 p-6 shadow-lg shadow-slate-950/60">
          <div class="flex items-center justify-between">
            <h2 class="font-display text-xl text-slate-100">Miembros activos</h2>
            <span class="text-xs text-slate-400">{{ members.length }} jugadores</span>
          </div>

          <div class="mt-4 overflow-hidden rounded-xl border border-slate-800">
            <table class="w-full text-left text-sm">
              <thead class="bg-slate-900 text-xs uppercase tracking-widest text-slate-400">
                <tr>
                  <th class="px-4 py-3">Nombre</th>
                  <th class="px-4 py-3">Nivel</th>
                  <th class="px-4 py-3">Items equipados</th>
                  <th class="px-4 py-3">DKP</th>
                  <th class="px-4 py-3">Acciones</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="member in members" :key="member.id" class="border-t border-slate-800">
                  <td class="px-4 py-4 font-semibold text-slate-100">{{ member.name }}</td>
                  <td class="px-4 py-4 text-slate-300">{{ member.level }}</td>
                  <td class="px-4 py-4">
                    <div class="flex flex-wrap gap-2">
                      <span
                        v-for="item in member.items"
                        :key="item"
                        class="rounded-full border border-slate-700 px-3 py-1 text-xs text-slate-300"
                      >
                        {{ item }}
                      </span>
                    </div>
                  </td>
                  <td class="px-4 py-4 text-amber-200">{{ member.dkp }}</td>
                  <td class="px-4 py-4">
                    <div class="flex flex-wrap gap-2">
                      <button
                        class="rounded-md bg-sky-500/20 px-3 py-1 text-xs text-sky-200 hover:bg-sky-500/40"
                        @click="openModal(member, 'item')"
                      >
                        Asignar item
                      </button>
                      <button
                        class="rounded-md bg-emerald-500/20 px-3 py-1 text-xs text-emerald-200 hover:bg-emerald-500/40"
                        @click="openModal(member, 'dkp')"
                      >
                        Sumar DKP
                      </button>
                      <button
                        class="rounded-md bg-rose-500/20 px-3 py-1 text-xs text-rose-200 hover:bg-rose-500/40"
                        @click="removeMember(member)"
                      >
                        Expulsar
                      </button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <aside class="space-y-6">
        <div class="rounded-2xl border border-slate-800 bg-slate-900/70 p-6 shadow-lg shadow-slate-950/60">
          <h2 class="font-display text-xl text-slate-100">Gestion de raids</h2>
          <p class="text-sm text-slate-400">Planea las proximas incursiones.</p>

          <form class="mt-4 space-y-3" @submit.prevent="submitRaid">
            <input
              v-model="raidForm.title"
              type="text"
              placeholder="Nombre del raid"
              class="w-full rounded-lg border border-slate-700 bg-slate-950/60 px-3 py-2 text-sm text-slate-100 placeholder:text-slate-600 focus:border-amber-300 focus:outline-none"
            />
            <input
              v-model="raidForm.date"
              type="date"
              class="w-full rounded-lg border border-slate-700 bg-slate-950/60 px-3 py-2 text-sm text-slate-100 focus:border-amber-300 focus:outline-none"
            />
            <textarea
              v-model="raidForm.notes"
              rows="3"
              placeholder="Notas estrategicas"
              class="w-full rounded-lg border border-slate-700 bg-slate-950/60 px-3 py-2 text-sm text-slate-100 placeholder:text-slate-600 focus:border-amber-300 focus:outline-none"
            ></textarea>
            <button
              type="submit"
              class="w-full rounded-lg bg-amber-400 px-3 py-2 text-sm font-semibold text-slate-950 hover:bg-amber-300"
            >
              Crear raid
            </button>
          </form>
        </div>

        <div class="rounded-2xl border border-slate-800 bg-slate-900/70 p-6 shadow-lg shadow-slate-950/60">
          <h2 class="font-display text-xl text-slate-100">Gestion de eventos</h2>
          <p class="text-sm text-slate-400">Organiza torneos y farm sessions.</p>

          <form class="mt-4 space-y-3" @submit.prevent="submitEvent">
            <input
              v-model="eventForm.title"
              type="text"
              placeholder="Nombre del evento"
              class="w-full rounded-lg border border-slate-700 bg-slate-950/60 px-3 py-2 text-sm text-slate-100 placeholder:text-slate-600 focus:border-amber-300 focus:outline-none"
            />
            <input
              v-model="eventForm.date"
              type="date"
              class="w-full rounded-lg border border-slate-700 bg-slate-950/60 px-3 py-2 text-sm text-slate-100 focus:border-amber-300 focus:outline-none"
            />
            <textarea
              v-model="eventForm.notes"
              rows="3"
              placeholder="Descripcion breve"
              class="w-full rounded-lg border border-slate-700 bg-slate-950/60 px-3 py-2 text-sm text-slate-100 placeholder:text-slate-600 focus:border-amber-300 focus:outline-none"
            ></textarea>
            <button
              type="submit"
              class="w-full rounded-lg border border-slate-700 px-3 py-2 text-sm font-semibold text-slate-200 hover:border-amber-300 hover:text-amber-200"
            >
              Crear evento
            </button>
          </form>
        </div>

        <p v-if="systemMessage" class="rounded-xl border border-emerald-500/30 bg-emerald-500/10 px-4 py-3 text-xs text-emerald-200">
          {{ systemMessage }}
        </p>
      </aside>
    </section>

    <div v-if="modalOpen" class="fixed inset-0 z-30 flex items-center justify-center bg-slate-950/70 px-6">
      <div class="w-full max-w-md rounded-2xl border border-slate-800 bg-slate-900 p-6 shadow-xl shadow-slate-950/60">
        <div class="flex items-start justify-between">
          <div>
            <p class="text-xs uppercase tracking-[0.4em] text-slate-400">Distribucion de botin</p>
            <h3 class="font-display text-xl text-slate-100">
              {{ modalType === 'item' ? 'Asignar item' : 'Sumar DKP' }}
            </h3>
          </div>
          <button class="text-slate-400 hover:text-slate-100" @click="closeModal">Cerrar</button>
        </div>

        <div class="mt-4 space-y-3">
          <p class="text-sm text-slate-300">
            Miembro: <span class="font-semibold text-slate-100">{{ modalMember?.name }}</span>
          </p>

          <div v-if="modalType === 'item'">
            <label class="text-xs uppercase tracking-widest text-slate-400">Nombre del item</label>
            <input
              v-model="itemName"
              type="text"
              placeholder="Espada de la noche"
              class="mt-2 w-full rounded-lg border border-slate-700 bg-slate-950/60 px-3 py-2 text-sm text-slate-100 placeholder:text-slate-600 focus:border-amber-300 focus:outline-none"
            />
          </div>

          <div v-else>
            <label class="text-xs uppercase tracking-widest text-slate-400">Cantidad DKP</label>
            <input
              v-model.number="dkpDelta"
              type="number"
              min="1"
              class="mt-2 w-full rounded-lg border border-slate-700 bg-slate-950/60 px-3 py-2 text-sm text-slate-100 focus:border-amber-300 focus:outline-none"
            />
          </div>

          <div class="flex gap-3 pt-4">
            <button
              class="flex-1 rounded-lg border border-slate-700 px-3 py-2 text-sm text-slate-200 hover:border-slate-500"
              @click="closeModal"
            >
              Cancelar
            </button>
            <button
              class="flex-1 rounded-lg bg-amber-400 px-3 py-2 text-sm font-semibold text-slate-950 hover:bg-amber-300"
              @click="confirmModal"
            >
              Confirmar
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
