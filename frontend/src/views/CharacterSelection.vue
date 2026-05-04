<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { personajeService, type Personaje } from '../services/api';

const MAX_PERSONAJES = 3;
const props = defineProps<{ token: string; username: string }>();
const emit = defineEmits(['char-selected', 'logout']);

const characters = ref<Personaje[]>([]);
const loading = ref(true);
const showCreate = ref(false);
const creating = ref(false);
const createError = ref('');

const newChar = ref({ nombrePersonaje: '', clase: '', faccion: '' });
const clases = ['Paladin', 'Mago', 'Chaman', 'Cazador', 'Brujo', 'Guerrero', 'Sacerdote', 'Picaro'];
const facciones = ['Los Primordiales de la Luz', 'Los Hijos del Gris', 'Los Marcados por el Abismo'];
const canCreate = computed(() => characters.value.length < MAX_PERSONAJES);

const faccionColors: Record<string, string> = {
  'Los Primordiales de la Luz': '#f0c040',
  'Los Hijos del Gris': '#8b949e',
  'Los Marcados por el Abismo': '#a335ee',
};

const loadChars = async () => {
  loading.value = true;
  try { characters.value = await personajeService.getMisPersonajes(); }
  catch (err) { console.error(err); }
  finally { loading.value = false; }
};

onMounted(loadChars);

const selectChar = (char: Personaje) => emit('char-selected', char);

const crearPersonaje = async () => {
  if (!newChar.value.nombrePersonaje || !newChar.value.clase || !newChar.value.faccion) {
    createError.value = 'Completa todos los campos'; return;
  }
  creating.value = true; createError.value = '';
  try {
    await personajeService.crearPersonaje(newChar.value);
    newChar.value = { nombrePersonaje: '', clase: '', faccion: '' };
    showCreate.value = false;
    await loadChars();
  } catch (err: any) {
    createError.value = err.message || 'Error al crear personaje';
  } finally { creating.value = false; }
};
</script>

<template>
  <div class="page">
    <div class="top-bar">
      <span class="user-tag">{{ username }}</span>
      <button class="btn-link" @click="emit('logout')">Cerrar sesión</button>
    </div>

    <h2>Elige tu Personaje</h2>
    <p class="hint">{{ characters.length }}/{{ MAX_PERSONAJES }} personajes</p>

    <div v-if="loading" class="loader">Cargando personajes...</div>

    <div v-else class="char-grid">
      <div v-for="char in characters" :key="char.idPersonaje" class="char-card" @click="selectChar(char)">
        <div class="card-top">
          <span class="class-badge">{{ char.clase }}</span>
          <span v-if="char.rolClan === 'Guild Master'" class="gm-tag">GM</span>
          <span v-else-if="char.rolClan === 'Raider'" class="raider-tag">Raider</span>
        </div>
        <h3>{{ char.nombrePersonaje }}</h3>
        <div class="faccion-line" :style="{ color: faccionColors[char.faccion] || '#8b949e' }">{{ char.faccion }}</div>
        <div class="stats-row">
          <div class="stat"><span class="stat-label">Nivel</span><span class="stat-val">{{ char.nivel }}</span></div>
          <div class="stat"><span class="stat-label">iLvl</span><span class="stat-val">{{ char.itemLevel }}</span></div>
          <div class="stat"><span class="stat-label">DKP</span><span class="stat-val">{{ char.puntosDkpActuales }}</span></div>
        </div>
      </div>

      <!-- Crear personaje card -->
      <div v-if="canCreate && !showCreate" class="char-card create-card" @click="showCreate = true">
        <div class="create-icon">+</div>
        <p>Crear personaje</p>
      </div>
    </div>

    <!-- Modal de creación -->
    <div v-if="showCreate" class="modal-overlay" @click.self="showCreate = false">
      <div class="modal">
        <h3>Nuevo Personaje</h3>
        <label>Nombre</label>
        <input v-model="newChar.nombrePersonaje" type="text" placeholder="Nombre del personaje" maxlength="50">

        <label>Clase</label>
        <div class="option-grid">
          <button v-for="c in clases" :key="c" :class="{ selected: newChar.clase === c }" @click="newChar.clase = c">{{ c }}</button>
        </div>

        <label>Facción</label>
        <div class="option-grid factions">
          <button v-for="f in facciones" :key="f" :class="{ selected: newChar.faccion === f }" :style="newChar.faccion === f ? { borderColor: faccionColors[f], color: faccionColors[f] } : {}" @click="newChar.faccion = f">{{ f }}</button>
        </div>

        <p v-if="createError" class="error-text">{{ createError }}</p>
        <div class="modal-actions">
          <button class="btn-cancel" @click="showCreate = false">Cancelar</button>
          <button class="btn-gold" :disabled="creating" @click="crearPersonaje">{{ creating ? 'Creando...' : 'Crear' }}</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page { padding: 40px; max-width: 900px; margin: 0 auto; min-height: 100vh; }
.top-bar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 32px; }
.user-tag { color: var(--text-dim); font-size: 14px; }
.btn-link { background: none; border: none; color: var(--red); cursor: pointer; font-size: 13px; }
h2 { text-align: center; margin-bottom: 4px; font-size: 28px; }
.hint { text-align: center; color: var(--text-dim); font-size: 13px; margin-bottom: 32px; }
.loader { text-align: center; color: var(--text-dim); padding: 60px; }

.char-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(240px, 1fr)); gap: 20px; }
.char-card { background: var(--bg-card); border: 1px solid var(--border); padding: 20px; border-radius: 10px; cursor: pointer; transition: all 0.25s; }
.char-card:hover { border-color: var(--gold); transform: translateY(-4px); box-shadow: 0 8px 24px rgba(0,0,0,0.3); }
.card-top { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; }
.class-badge { background: var(--border); padding: 3px 10px; border-radius: 4px; font-size: 11px; font-weight: 700; text-transform: uppercase; letter-spacing: 0.5px; }
.gm-tag { color: var(--purple); border: 1px solid var(--purple); padding: 2px 8px; border-radius: 4px; font-size: 10px; font-weight: 700; }
.raider-tag { color: var(--green); border: 1px solid var(--green); padding: 2px 8px; border-radius: 4px; font-size: 10px; font-weight: 700; }
h3 { font-size: 20px; margin-bottom: 4px; }
.faccion-line { font-size: 12px; margin-bottom: 16px; }
.stats-row { display: flex; justify-content: space-between; border-top: 1px solid var(--border); padding-top: 12px; }
.stat { text-align: center; }
.stat-label { display: block; font-size: 10px; color: var(--text-dim); text-transform: uppercase; }
.stat-val { font-size: 18px; font-weight: 700; color: var(--gold); }

.create-card { display: flex; flex-direction: column; align-items: center; justify-content: center; border-style: dashed; color: var(--text-dim); min-height: 180px; }
.create-icon { font-size: 48px; color: var(--gold); line-height: 1; }
.create-card p { margin-top: 8px; font-size: 14px; }

.modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.7); display: flex; align-items: center; justify-content: center; z-index: 100; }
.modal { background: var(--bg-card); border: 1px solid var(--border); border-radius: 12px; padding: 32px; width: 440px; max-height: 90vh; overflow-y: auto; }
.modal h3 { text-align: center; margin-bottom: 20px; }
.modal label { display: block; font-size: 11px; color: var(--text-dim); text-transform: uppercase; letter-spacing: 1px; margin-bottom: 6px; margin-top: 16px; }
.modal input { width: 100%; background: var(--bg-dark); border: 1px solid var(--border); color: var(--text); padding: 10px; border-radius: 6px; font-size: 14px; outline: none; }
.modal input:focus { border-color: var(--gold); }

.option-grid { display: flex; flex-wrap: wrap; gap: 8px; }
.option-grid button { background: var(--bg-dark); border: 1px solid var(--border); color: var(--text-dim); padding: 8px 14px; border-radius: 6px; cursor: pointer; font-size: 13px; transition: all 0.15s; }
.option-grid button:hover { border-color: var(--text-dim); }
.option-grid button.selected { border-color: var(--gold); color: var(--gold); background: rgba(212,168,67,0.1); }
.factions button { font-size: 12px; }

.error-text { color: var(--red); font-size: 13px; text-align: center; margin-top: 12px; }
.modal-actions { display: flex; gap: 12px; margin-top: 24px; }
.btn-cancel { flex: 1; padding: 10px; background: transparent; border: 1px solid var(--border); color: var(--text-dim); border-radius: 6px; cursor: pointer; font-size: 14px; }
.btn-gold { flex: 1; padding: 10px; background: var(--gold); color: #000; border: none; border-radius: 6px; cursor: pointer; font-weight: 700; font-size: 14px; }
.btn-gold:disabled { opacity: 0.4; cursor: not-allowed; }
</style>
