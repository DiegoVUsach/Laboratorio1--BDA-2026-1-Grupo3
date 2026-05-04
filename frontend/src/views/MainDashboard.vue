<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import {
  type Personaje, type RaidDTO, type InventarioDTO, type InventarioItemDTO,
  type HistorialBotin, type RankingEntry, type Clan,
  raidService, inventarioService, itemService, clanService, personajeService
} from '../services/api';

const props = defineProps<{ character: Personaje; token: string }>();
const emit = defineEmits(['logout', 'change-char']);

const activeTab = ref('personaje');

// --- Personaje tab ---
const inventario = ref<InventarioDTO | null>(null);
const inventarioItems = ref<InventarioItemDTO[]>([]);
const historial = ref<HistorialBotin[]>([]);
const loadingPersonaje = ref(true);

// --- Clan tab ---
const clanInfo = ref<Clan | null>(null);
const clanMembers = ref<Personaje[]>([]);
const ranking = ref<RankingEntry[]>([]);
const loadingClan = ref(true);

// --- Raid tab ---
const raids = ref<RaidDTO[]>([]);
const loadingRaids = ref(true);
const inscribirMsg = ref('');
const inscribirError = ref('');

// --- Refresh character data ---
const currentChar = ref<Personaje>({ ...props.character });

const rarityColor = (r: string) => {
  const map: Record<string, string> = { Legendario: '#ff8000', Epico: '#a335ee', Raro: '#0070dd', Comun: '#9d9d9d' };
  return map[r] || '#9d9d9d';
};

const formatDate = (d: string) => {
  const date = new Date(d);
  const days = ['Dom', 'Lun', 'Mar', 'Mie', 'Jue', 'Vie', 'Sab'];
  return `${days[date.getDay()]} ${date.getDate()}/${date.getMonth() + 1} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`;
};

const loadPersonajeData = async () => {
  loadingPersonaje.value = true;
  try {
    const [inv, items, hist, updated] = await Promise.all([
      inventarioService.getByPersonaje(currentChar.value.idPersonaje).catch(() => null),
      inventarioService.getItemsByPersonaje(currentChar.value.idPersonaje).catch(() => []),
      itemService.getHistorial(currentChar.value.idPersonaje).catch(() => []),
      personajeService.getById(currentChar.value.idPersonaje).catch(() => null),
    ]);
    inventario.value = inv;
    inventarioItems.value = items;
    historial.value = hist;
    if (updated) currentChar.value = updated;
  } catch (e) { console.error(e); }
  finally { loadingPersonaje.value = false; }
};

const loadClanData = async () => {
  if (!currentChar.value.idClan) { loadingClan.value = false; return; }
  loadingClan.value = true;
  try {
    const [clan, allChars, rank] = await Promise.all([
      clanService.getById(currentChar.value.idClan),
      clanService.getMiembros(currentChar.value.idClan),
      itemService.getRanking().catch(() => []),
    ]);
    clanInfo.value = clan;
    clanMembers.value = allChars
      .filter((p: Personaje) => p.idClan === currentChar.value.idClan)
      .sort((a: Personaje, b: Personaje) => b.nivel - a.nivel);
    ranking.value = rank;
  } catch (e) { console.error(e); }
  finally { loadingClan.value = false; }
};

const loadRaidData = async () => {
  loadingRaids.value = true;
  try { raids.value = await raidService.getCalendario(); }
  catch (e) { console.error(e); }
  finally { loadingRaids.value = false; }
};

onMounted(() => {
  loadPersonajeData();
  loadClanData();
  loadRaidData();
});

const inscribirse = async (raid: RaidDTO, rol: string) => {
  inscribirMsg.value = ''; inscribirError.value = '';
  try {
    const msg = await raidService.inscribirse(raid.raid.idRaid, currentChar.value.idPersonaje, rol);
    inscribirMsg.value = `Inscrito como ${rol} en ${raid.raid.nombreRaid}`;
    await loadRaidData();
  } catch (err: any) {
    inscribirError.value = err.message || 'Error al inscribirse';
  }
};

const rolClanBadge = (rol: string) => {
  if (rol === 'Guild Master') return { text: 'GM', cls: 'badge-gm' };
  if (rol === 'Raider') return { text: 'Raider', cls: 'badge-raider' };
  return { text: 'Member', cls: 'badge-member' };
};
</script>

<template>
  <div class="layout">
    <!-- Sidebar -->
    <aside class="sidebar">
      <div class="char-header">
        <div class="char-class">{{ currentChar.clase }}</div>
        <h3 class="char-name">{{ currentChar.nombrePersonaje }}</h3>
        <div class="char-meta">
          <span :class="rolClanBadge(currentChar.rolClan).cls">{{ rolClanBadge(currentChar.rolClan).text }}</span>
          <span class="ilvl-pill">iLvl {{ currentChar.itemLevel }}</span>
        </div>
      </div>

      <nav class="nav">
        <button :class="{ active: activeTab === 'personaje' }" @click="activeTab = 'personaje'">
          <span class="nav-icon">⚔</span> Personaje
        </button>
        <button :class="{ active: activeTab === 'clan' }" @click="activeTab = 'clan'">
          <span class="nav-icon">🛡</span> Clan
        </button>
        <button :class="{ active: activeTab === 'raids' }" @click="activeTab = 'raids'">
          <span class="nav-icon">🐉</span> Raids
        </button>
      </nav>

      <div class="sidebar-footer">
        <button class="btn-out" @click="emit('change-char')">Cambiar Personaje</button>
        <button class="btn-out danger" @click="emit('logout')">Cerrar Sesión</button>
      </div>
    </aside>

    <!-- Content -->
    <main class="content">

      <!-- ===== PERSONAJE TAB ===== -->
      <section v-if="activeTab === 'personaje'" class="tab-content">
        <h2>Ficha de Personaje</h2>

        <div v-if="loadingPersonaje" class="loader">Cargando datos...</div>
        <template v-else>
          <!-- Stats cards -->
          <div class="stats-grid">
            <div class="stat-card">
              <span class="sc-label">Nivel</span>
              <span class="sc-value">{{ currentChar.nivel }}</span>
            </div>
            <div class="stat-card">
              <span class="sc-label">Item Level</span>
              <span class="sc-value gold">{{ currentChar.itemLevel }}</span>
            </div>
            <div class="stat-card">
              <span class="sc-label">DKP</span>
              <span class="sc-value gold">{{ currentChar.puntosDkpActuales }}</span>
            </div>
            <div class="stat-card">
              <span class="sc-label">Facción</span>
              <span class="sc-value fac">{{ currentChar.faccion }}</span>
            </div>
          </div>

          <!-- Equipo -->
          <h3 class="section-title">Equipamiento Actual</h3>
          <div v-if="inventario" class="equip-grid">
            <div class="equip-slot">
              <span class="slot-label">Armadura</span>
              <span v-if="inventario.nombreArmadura" class="slot-item">{{ inventario.nombreArmadura }}</span>
              <span v-else class="slot-empty">— vacío —</span>
            </div>
            <div class="equip-slot">
              <span class="slot-label">Arma</span>
              <span v-if="inventario.nombreArma" class="slot-item">{{ inventario.nombreArma }}</span>
              <span v-else class="slot-empty">— vacío —</span>
            </div>
            <div class="equip-slot">
              <span class="slot-label">Accesorio</span>
              <span v-if="inventario.nombreAccesorio" class="slot-item">{{ inventario.nombreAccesorio }}</span>
              <span v-else class="slot-empty">— vacío —</span>
            </div>
          </div>
          <p v-else class="empty-msg">Sin inventario asignado.</p>

          <!-- Items en inventario -->
          <h3 class="section-title" v-if="inventarioItems.length > 0">Items en Bolsa</h3>
          <div v-if="inventarioItems.length > 0" class="item-list">
            <div v-for="item in inventarioItems" :key="item.idItem" class="item-row">
              <span class="item-name" :style="{ color: rarityColor(item.rareza) }">{{ item.nombreItem }}</span>
              <span class="item-meta">{{ item.tipo }} · Nv.{{ item.nivel }}</span>
              <span class="item-rarity" :style="{ color: rarityColor(item.rareza) }">{{ item.rareza }}</span>
            </div>
          </div>

          <!-- Historial de botín -->
          <h3 class="section-title">Historial de Botín</h3>
          <div v-if="historial.length > 0" class="item-list">
            <div v-for="h in historial" :key="h.idEntrega" class="item-row">
              <span class="item-name">{{ h.nombreItem }}</span>
              <span class="item-meta">{{ formatDate(h.fechaEntrega) }}</span>
            </div>
          </div>
          <p v-else class="empty-msg">Sin botín registrado aún.</p>
        </template>
      </section>

      <!-- ===== CLAN TAB ===== -->
      <section v-if="activeTab === 'clan'" class="tab-content">
        <h2>{{ clanInfo ? clanInfo.nombreClan : 'Clan' }}</h2>

        <div v-if="loadingClan" class="loader">Cargando clan...</div>
        <div v-else-if="!currentChar.idClan" class="empty-msg">No perteneces a ningún clan.</div>
        <template v-else>
          <h3 class="section-title">Miembros ({{ clanMembers.length }})</h3>
          <div class="members-table">
            <div class="mt-header">
              <span>Personaje</span><span>Clase</span><span>Nivel</span><span>iLvl</span><span>Rol</span>
            </div>
            <div v-for="m in clanMembers" :key="m.idPersonaje" class="mt-row" :class="{ 'is-me': m.idPersonaje === currentChar.idPersonaje }">
              <span class="mt-name">{{ m.nombrePersonaje }}</span>
              <span>{{ m.clase }}</span>
              <span>{{ m.nivel }}</span>
              <span>{{ m.itemLevel }}</span>
              <span :class="rolClanBadge(m.rolClan).cls">{{ rolClanBadge(m.rolClan).text }}</span>
            </div>
          </div>

          <!-- Ranking -->
          <h3 class="section-title" v-if="ranking.length > 0">Ranking del Clan</h3>
          <div v-if="ranking.length > 0" class="members-table">
            <div class="mt-header">
              <span>#</span><span>Personaje</span><span>Clan</span><span>Raids</span><span>DKP</span>
            </div>
            <div v-for="(r, i) in ranking" :key="r.id_personaje" class="mt-row">
              <span class="rank-pos">{{ i + 1 }}</span>
              <span>{{ r.nombre_personaje }}</span>
              <span>{{ r.nombre_clan }}</span>
              <span>{{ r.raids_asistidas }}</span>
              <span class="gold">{{ r.puntos_dkp_actuales }}</span>
            </div>
          </div>
        </template>
      </section>

      <!-- ===== RAIDS TAB ===== -->
      <section v-if="activeTab === 'raids'" class="tab-content">
        <h2>Calendario de Raids</h2>

        <div v-if="loadingRaids" class="loader">Cargando raids...</div>
        <template v-else>
          <p v-if="inscribirMsg" class="msg success">{{ inscribirMsg }}</p>
          <p v-if="inscribirError" class="msg error">{{ inscribirError }}</p>

          <div v-if="raids.length === 0" class="empty-msg">No hay raids programadas.</div>

          <div v-for="rd in raids" :key="rd.raid.idRaid" class="raid-card">
            <div class="raid-top">
              <div>
                <h3 class="raid-name">{{ rd.raid.nombreRaid }}</h3>
                <span class="raid-date">{{ formatDate(rd.raid.fechaRaid) }}</span>
              </div>
              <div class="raid-ilvl">
                <span class="ilvl-req">iLvl mín: {{ rd.raid.itemLevelMinimo }}</span>
                <span :class="currentChar.itemLevel >= rd.raid.itemLevelMinimo ? 'check-ok' : 'check-fail'">
                  {{ currentChar.itemLevel >= rd.raid.itemLevelMinimo ? '✓ Cumples' : '✗ No cumples' }}
                </span>
              </div>
            </div>

            <div class="cupos-row">
              <div class="cupo">
                <span class="cupo-label">Tanques</span>
                <span class="cupo-bar"><span class="cupo-fill tank" :style="{ width: Math.max(0, (1 - rd.cuposTanqueLibres / Math.max(rd.raid.tanques, 1)) * 100) + '%' }"></span></span>
                <span class="cupo-num">{{ rd.cuposTanqueLibres }}/{{ rd.raid.tanques }}</span>
                <button v-if="rd.cuposTanqueLibres > 0 && currentChar.itemLevel >= rd.raid.itemLevelMinimo" class="btn-inscribir" @click="inscribirse(rd, 'TANQUE')">Inscribir</button>
              </div>
              <div class="cupo">
                <span class="cupo-label">Healers</span>
                <span class="cupo-bar"><span class="cupo-fill healer" :style="{ width: Math.max(0, (1 - rd.cuposHealerLibres / Math.max(rd.raid.healers, 1)) * 100) + '%' }"></span></span>
                <span class="cupo-num">{{ rd.cuposHealerLibres }}/{{ rd.raid.healers }}</span>
                <button v-if="rd.cuposHealerLibres > 0 && currentChar.itemLevel >= rd.raid.itemLevelMinimo" class="btn-inscribir" @click="inscribirse(rd, 'HEALER')">Inscribir</button>
              </div>
              <div class="cupo">
                <span class="cupo-label">DPS</span>
                <span class="cupo-bar"><span class="cupo-fill dps" :style="{ width: Math.max(0, (1 - rd.cuposDpsLibres / Math.max(rd.raid.dps, 1)) * 100) + '%' }"></span></span>
                <span class="cupo-num">{{ rd.cuposDpsLibres }}/{{ rd.raid.dps }}</span>
                <button v-if="rd.cuposDpsLibres > 0 && currentChar.itemLevel >= rd.raid.itemLevelMinimo" class="btn-inscribir" @click="inscribirse(rd, 'DPS')">Inscribir</button>
              </div>
            </div>
          </div>
        </template>
      </section>

    </main>
  </div>
</template>

<style scoped>
.layout { display: flex; height: 100vh; overflow: hidden; }

/* Sidebar */
.sidebar { width: 260px; background: var(--bg-card); border-right: 1px solid var(--border); display: flex; flex-direction: column; flex-shrink: 0; }
.char-header { padding: 24px 20px 20px; border-bottom: 1px solid var(--border); }
.char-class { font-size: 11px; color: var(--text-dim); text-transform: uppercase; letter-spacing: 1px; }
.char-name { font-size: 20px; margin: 4px 0 8px; }
.char-meta { display: flex; gap: 8px; }
.ilvl-pill { background: rgba(212,168,67,0.15); color: var(--gold); padding: 2px 10px; border-radius: 12px; font-size: 12px; font-weight: 700; }

.nav { flex: 1; padding: 12px; display: flex; flex-direction: column; gap: 4px; }
.nav button { background: none; border: none; color: var(--text-dim); text-align: left; padding: 12px 16px; cursor: pointer; border-radius: 8px; font-size: 14px; display: flex; align-items: center; gap: 10px; transition: all 0.15s; }
.nav button:hover { background: rgba(255,255,255,0.04); color: var(--text); }
.nav button.active { background: rgba(212,168,67,0.12); color: var(--gold); }
.nav-icon { font-size: 16px; width: 20px; text-align: center; }

.sidebar-footer { padding: 16px; border-top: 1px solid var(--border); display: flex; flex-direction: column; gap: 8px; }
.btn-out { background: none; border: 1px solid var(--border); color: var(--text-dim); padding: 8px; border-radius: 6px; cursor: pointer; font-size: 12px; transition: all 0.15s; }
.btn-out:hover { border-color: var(--text-dim); }
.btn-out.danger { color: var(--red); border-color: transparent; }
.btn-out.danger:hover { border-color: var(--red); }

/* Content */
.content { flex: 1; overflow-y: auto; }
.tab-content { padding: 40px; max-width: 900px; }
h2 { margin-bottom: 24px; font-size: 24px; }
.loader { color: var(--text-dim); padding: 40px 0; }
.empty-msg { color: var(--text-dim); font-size: 14px; padding: 20px 0; }

/* Badges */
.badge-gm { color: var(--purple); font-weight: 700; font-size: 12px; }
.badge-raider { color: var(--green); font-weight: 700; font-size: 12px; }
.badge-member { color: var(--text-dim); font-size: 12px; }

/* Stats grid */
.stats-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 12px; margin-bottom: 32px; }
.stat-card { background: var(--bg-card); border: 1px solid var(--border); border-radius: 10px; padding: 16px; text-align: center; }
.sc-label { display: block; font-size: 11px; color: var(--text-dim); text-transform: uppercase; letter-spacing: 0.5px; margin-bottom: 4px; }
.sc-value { font-size: 24px; font-weight: 700; }
.sc-value.gold { color: var(--gold); }
.sc-value.fac { font-size: 13px; font-weight: 400; color: var(--text); }

/* Section titles */
.section-title { font-size: 16px; margin: 24px 0 12px; padding-bottom: 8px; border-bottom: 1px solid var(--border); }

/* Equipment */
.equip-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 12px; }
.equip-slot { background: var(--bg-card); border: 1px solid var(--border); border-radius: 8px; padding: 16px; text-align: center; }
.slot-label { display: block; font-size: 11px; color: var(--text-dim); text-transform: uppercase; margin-bottom: 8px; }
.slot-item { font-size: 14px; font-weight: 600; color: var(--gold); }
.slot-empty { font-size: 13px; color: var(--border); }

/* Item list */
.item-list { display: flex; flex-direction: column; gap: 2px; }
.item-row { display: flex; align-items: center; gap: 12px; padding: 10px 12px; background: var(--bg-card); border-radius: 6px; }
.item-name { flex: 1; font-weight: 600; font-size: 14px; }
.item-meta { color: var(--text-dim); font-size: 12px; }
.item-rarity { font-size: 12px; font-weight: 700; }

/* Members table */
.members-table { background: var(--bg-card); border: 1px solid var(--border); border-radius: 10px; overflow: hidden; }
.mt-header, .mt-row { display: grid; grid-template-columns: 2fr 1fr 0.7fr 0.7fr 1fr; padding: 10px 16px; align-items: center; font-size: 13px; }
.mt-header { background: rgba(255,255,255,0.03); color: var(--text-dim); font-size: 11px; text-transform: uppercase; letter-spacing: 0.5px; border-bottom: 1px solid var(--border); }
.mt-row { border-bottom: 1px solid rgba(48,54,61,0.5); }
.mt-row:last-child { border-bottom: none; }
.mt-row.is-me { background: rgba(212,168,67,0.06); }
.mt-name { font-weight: 600; }
.rank-pos { color: var(--gold); font-weight: 700; }
.gold { color: var(--gold); font-weight: 700; }

/* Raid cards */
.raid-card { background: var(--bg-card); border: 1px solid var(--border); border-radius: 10px; padding: 20px; margin-bottom: 16px; }
.raid-top { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 16px; }
.raid-name { font-size: 18px; margin-bottom: 4px; }
.raid-date { color: var(--text-dim); font-size: 13px; }
.raid-ilvl { text-align: right; }
.ilvl-req { display: block; font-size: 13px; color: var(--text-dim); margin-bottom: 4px; }
.check-ok { color: var(--green); font-size: 13px; font-weight: 600; }
.check-fail { color: var(--red); font-size: 13px; font-weight: 600; }

.cupos-row { display: flex; gap: 16px; }
.cupo { flex: 1; }
.cupo-label { display: block; font-size: 11px; color: var(--text-dim); text-transform: uppercase; margin-bottom: 6px; }
.cupo-bar { display: block; height: 6px; background: var(--border); border-radius: 3px; overflow: hidden; margin-bottom: 4px; }
.cupo-fill { display: block; height: 100%; border-radius: 3px; }
.cupo-fill.tank { background: var(--blue); }
.cupo-fill.healer { background: var(--green); }
.cupo-fill.dps { background: var(--red); }
.cupo-num { font-size: 13px; color: var(--text-dim); }
.btn-inscribir { display: block; width: 100%; margin-top: 8px; padding: 6px; background: rgba(212,168,67,0.15); border: 1px solid var(--gold-dim); color: var(--gold); border-radius: 6px; cursor: pointer; font-size: 12px; font-weight: 600; transition: all 0.15s; }
.btn-inscribir:hover { background: var(--gold); color: #000; }

/* Messages */
.msg { font-size: 13px; padding: 10px 14px; border-radius: 8px; margin-bottom: 16px; }
.msg.success { color: var(--green); background: rgba(63,185,80,0.1); }
.msg.error { color: var(--red); background: rgba(248,81,73,0.1); }
</style>
