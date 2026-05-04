<script setup lang="ts">

import { ref, onMounted, computed } from 'vue';

import {
  type Personaje, type RaidDTO, type InventarioDTO, type InventarioItemDTO,
  type HistorialBotin, type RankingEntry, type Clan,
  raidService, inventarioService, itemService, clanService, personajeService,
  inscripcionService
} from '../services/api';



const props = defineProps<{ character: Personaje; token: string }>();
const emit = defineEmits(['logout', 'change-char']);
const activeTab = ref('personaje');



// --- Estado Reactivo ---
const currentChar = ref<Personaje>({ ...props.character });
const inventario = ref<InventarioDTO | null>(null);
const inventarioItems = ref<InventarioItemDTO[]>([]);
const historial = ref<HistorialBotin[]>([]);
const loadingPersonaje = ref(true);


const clanInfo = ref<Clan | null>(null);
const clanMembers = ref<Personaje[]>([]);
const ranking = ref<RankingEntry[]>([]);
const loadingClan = ref(true);



const raids = ref<RaidDTO[]>([]);
const loadingRaids = ref(true);
const inscribirMsg = ref('');
const inscribirError = ref('');



const showCreateModal = ref(false);
const newRaid = ref({
  nombreRaid: '',
  fechaRaid: '',
  itemLevelMinimo: 0,
  tanques: 2,
  healers: 5,
  dps: 18

});



// --- Funciones de Utilidad y Normalización ---

const normalizarTexto = (texto: string) => {

  if (!texto) return "";

  return texto.toUpperCase()

    .normalize("NFD").replace(/[\u0300-\u036f]/g, "") // Elimina tildes (Á -> A)

    .trim();

};



const puedeCumplirRol = (rolBuscado: string): boolean => {
  if (!currentChar.value || !currentChar.value.clase) return false;

  const clase = normalizarTexto(currentChar.value.clase);
  const rol = normalizarTexto(rolBuscado);

  // Lógica espejo de validarClaseYRol en Java (Versión TypeScript)
  switch (clase) {
    case "MAGO":
    case "BRUJO":
      return rol === "DPS" || rol === "HEALER";
    case "SACERDOTE":
      return rol === "HEALER";
    case "PICARO":
    case "CAZADOR":
      return rol === "DPS";
    case "PALADIN":
      return rol === "TANQUE";
    case "CHAMAN":
    case "DRUIDA":
      return rol === "HEALER";
    case "GUERRERO":
      return rol === "TANQUE" || rol === "DPS";
    default:
      return true;
  }
};


const rarityColor = (r: string) => {

  const map: Record<string, string> = { Legendario: '#ff8000', Epico: '#a335ee', Raro: '#0070dd', Comun: '#9d9d9d' };

  return map[r] || '#9d9d9d';

};



const formatDate = (d: string) => {

  if (!d) return 'S/F';

  const date = new Date(d);

  const days = ['Dom', 'Lun', 'Mar', 'Mie', 'Jue', 'Vie', 'Sab'];

  return `${days[date.getDay()]} ${date.getDate()}/${date.getMonth() + 1} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`;

};



const rolClanBadge = (rol: string) => {

  if (rol === 'Guild Master') return { text: 'GM', cls: 'badge-gm' };

  if (rol === 'Raider') return { text: 'Raider', cls: 'badge-raider' };

  return { text: 'Member', cls: 'badge-member' };

};



// --- Carga de Datos (Unificada) ---

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

  } catch (e) { console.error("Error cargando personaje:", e); }

  finally { loadingPersonaje.value = false; }

};



const loadRaidData = async () => {

  loadingRaids.value = true;

  try { raids.value = await raidService.getCalendario(); }

  catch (e) { console.error("Error cargando raids:", e); }

  finally { loadingRaids.value = false; }

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

    // --- CAMBIO AQUÍ: Filtrar el ranking por el nombre del clan actual ---
    ranking.value = rank.filter((entry: RankingEntry) => 
      entry.nombre_clan === clan.nombreClan
    );

  } catch (e) { console.error("Error cargando clan:", e); }
  finally { loadingClan.value = false; }
};



// --- Acciones ---

const abrirModalCrear = () => {

  inscribirMsg.value = '';

  inscribirError.value = '';

  showCreateModal.value = true;

};



const crearRaid = async () => {

  try {

    // 1. Validación manual antes de enviar

    if (!newRaid.value.fechaRaid) {

      inscribirError.value = "Debes seleccionar una fecha y hora.";

      return;

    }



    // 2. Formateo de fecha (Asegura formato ISO que entiende SQL)

    // El input datetime-local devuelve "YYYY-MM-DDTHH:mm", lo pasamos a Date y luego a ISO

    const raidParaEnviar = {

      ...newRaid.value,

      fechaRaid: new Date(newRaid.value.fechaRaid).toISOString() 

    };



    await raidService.create(raidParaEnviar);

    

    showCreateModal.value = false;

    await loadRaidData();

    inscribirMsg.value = "Raid creada con éxito.";

    

    // Reset del formulario

    newRaid.value = { nombreRaid: '', fechaRaid: '', itemLevelMinimo: 0, tanques: 2, healers: 5, dps: 18 };

  } catch (err: any) {

    // Si el backend devuelve un error de constraint, lo atrapamos aquí

    inscribirError.value = "Error del servidor: Verifique el formato de fecha.";

    console.error(err);

  }

};



const inscribirse = async (raid: RaidDTO, rol: string) => {

  inscribirMsg.value = ''; inscribirError.value = '';

  try {

    await raidService.inscribirse(raid.raid.idRaid, currentChar.value.idPersonaje, rol);

    inscribirMsg.value = `Inscrito como ${rol} en ${raid.raid.nombreRaid}`;

    await loadRaidData();

  } catch (err: any) {

    inscribirError.value = humanizarError(err.message) || 'Error al inscribirse';

  }

};



const equiparItem = async (item: InventarioItemDTO) => {

  if (!inventario.value) return;

  const payload = {

    idInventario: inventario.value.idInventario,

    idPersonaje: currentChar.value.idPersonaje,

    armaduraEquipado: item.tipo === 'ARMADURA' ? item.idItem : inventario.value.armaduraEquipado,

    armaEquipado: item.tipo === 'ARMA' ? item.idItem : inventario.value.armaEquipado,

    accesorioEquipado: item.tipo === 'ACCESORIO' ? item.idItem : inventario.value.accesorioEquipado

  };

  try {

    await inventarioService.update(inventario.value.idInventario, payload);

    await loadPersonajeData();

  } catch (e) { console.error("Error al equipar:", e); }

};



const desequiparItem = async (tipo: string) => {

  if (!inventario.value) return;

  const payload = {

    idInventario: inventario.value.idInventario,

    idPersonaje: currentChar.value.idPersonaje,

    armaduraEquipado: tipo === 'ARMADURA' ? null : inventario.value.armaduraEquipado,

    armaEquipado: tipo === 'ARMA' ? null : inventario.value.armaEquipado,

    accesorioEquipado: tipo === 'ACCESORIO' ? null : inventario.value.accesorioEquipado

  };

  try {

    await inventarioService.update(inventario.value.idInventario, payload);

    await loadPersonajeData();

  } catch (e) { console.error("Error al desequipar:", e); }

};



const itemsEnBolsaFiltrados = computed(() => {

  if (!inventario.value || !inventarioItems.value.length) return inventarioItems.value;

  const inv = inventario.value as any;

  const equipadosIds = [

    inv.armaduraEquipado, inv.armadura_equipado,

    inv.armaEquipado, inv.arma_equipado,

    inv.accesorioEquipado, inv.accesorio_equipado

  ].filter(id => id != null);

  return inventarioItems.value.filter(item => !equipadosIds.includes(item.idItem));

});



// --- Ciclo de Vida ---

onMounted(() => {

  loadPersonajeData();

  loadClanData();

  loadRaidData();

});

// --- Humanizar errores de backend ---
const humanizarError = (msg: string): string => {
  if (!msg) return 'Ocurrió un error inesperado.';
  if (msg.includes('nivel de equipo') || msg.includes('insuficiente'))
    return 'Tu nivel de equipamiento es insuficiente para esta raid. Equipa mejores items.';
  if (msg.includes('DKP insuficientes'))
    return 'No tienes suficientes puntos DKP para reclamar este botín.';
  if (msg.includes('No tienes permiso') || msg.includes('no pertenece'))
    return 'No tienes permisos para realizar esta acción.';
  if (msg.includes('ya existe') || msg.includes('duplicate'))
    return 'Ya existe un registro con esos datos.';
  if (msg.includes('Incoherencia de facción'))
    return 'Este personaje no puede unirse al clan porque pertenece a otra facción.';
  if (msg.includes('cupos disponibles'))
    return 'No quedan cupos para ese rol en esta raid.';
  return msg.replace(/^ERROR:\s*/i, '').replace(/^Error:\s*/i, '');
};

// --- Transferencia de liderazgo (Req 6: Trigger 2) ---
const transferTarget = ref<number | null>(null);
const transferMsg = ref('');
const transferError = ref('');

const transferirLiderazgo = async () => {
  if (!transferTarget.value || !currentChar.value.idClan) return;
  transferMsg.value = ''; transferError.value = '';
  try {
    await clanService.transferLeadership(
      currentChar.value.idClan,
      currentChar.value.idPersonaje,
      transferTarget.value
    );
    transferMsg.value = 'Liderazgo transferido. Este personaje ya no es Guild Master.';
    await loadClanData();
    await loadPersonajeData();
  } catch (err: any) {
    transferError.value = humanizarError(err.message);
  }
};

// --- Repartir botín (Req 3: SP1) ---
const showBotinModal = ref(false);
const botinItemId = ref<number | null>(null);
const botinRaidId = ref<number | null>(null);
const botinMsg = ref('');
const botinError = ref('');
const allItems = ref<any[]>([]);

const abrirBotinModal = async () => {
  botinMsg.value = ''; botinError.value = '';
  botinItemId.value = null; botinRaidId.value = null;
  try { allItems.value = await itemService.getAll(); } catch (e) { console.error(e); }
  showBotinModal.value = true;
};

const repartirBotin = async () => {
  if (!botinItemId.value || !botinRaidId.value) { botinError.value = 'Selecciona item y raid.'; return; }
  botinMsg.value = ''; botinError.value = '';
  try {
    await itemService.repartirBotin(currentChar.value.idPersonaje, botinItemId.value, botinRaidId.value);
    botinMsg.value = 'Botín asignado y DKP descontados.';
    showBotinModal.value = false;
    await loadPersonajeData();
  } catch (err: any) {
    botinError.value = humanizarError(err.message);
  }
};

// --- Confirmar asistencia (Req 2: GM confirma) ---
const showInscritos = ref<number | null>(null);
const inscritosRaid = ref<any[]>([]);

const verInscritos = async (idRaid: number) => {
  if (showInscritos.value === idRaid) { showInscritos.value = null; return; }
  try {
    inscritosRaid.value = await inscripcionService.getByRaid(idRaid);
    showInscritos.value = idRaid;
  } catch (e) { console.error(e); }
};

const confirmarAsistencia = async (idInscripcion: number) => {
  try {
    await inscripcionService.confirmarAsistencia(idInscripcion, currentChar.value.idPersonaje);
    inscribirMsg.value = 'Asistencia confirmada.';
    if (showInscritos.value) await verInscritos(showInscritos.value);
  } catch (err: any) {
    inscribirError.value = humanizarError(err.message);
  }
};

// --- Refrescar ranking (Req 7) ---
const refrescarRanking = async () => {
  try {
    await itemService.refrescarRanking();
    await loadClanData();
  } catch (e) { console.error(e); }
};

const estaInscrito = (rd: any) => {
  // 1. Validaciones básicas
  if (!rd || !rd.idsParticipantes || !currentChar.value) return false;

  // 2. Obtenemos tu ID (asegurándonos de que sea número)
  const miId = Number(currentChar.value.idPersonaje);

  // 3. Comprobamos si tu ID está en la lista que ahora sí envía el DTO
  return rd.idsParticipantes.includes(miId);
};

const anularInscripcion = async (raid: RaidDTO) => {

  try {

    await raidService.desinscribirse(raid.raid.idRaid, currentChar.value.idPersonaje);

    inscribirMsg.value = "Inscripción anulada correctamente.";

    await loadRaidData(); // Recargamos para ver los cupos libres de nuevo

  } catch (err: any) {

    inscribirError.value = humanizarError(err.message);

  }

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
              <span class="sc-label">Clase</span>
              <span class="sc-value">{{ currentChar.clase }}</span>
            </div>

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



<!-- ===== EQUIPAMIENTO ACTUAL ===== -->

<h3 class="section-title">Equipamiento Actual</h3>

<div v-if="inventario" class="equip-grid">

  

<!-- Slot Armadura -->

<div class="equip-slot">

  <span class="slot-label">Armadura</span>

  <template v-if="inventario.armaduraEquipado"> <!-- Cambiado de nombreArmadura a id para mayor seguridad -->

    <span class="slot-item">{{ inventario.nombreArmadura }}</span>

    <span class="slot-lvl">Nv. {{ inventario.nivelArmadura }}</span> <!-- Ahora viene directo del DTO -->

    <button class="btn-quitar" @click="desequiparItem('ARMADURA')">Quitar</button>

  </template>

  <span v-else class="slot-empty">— vacío —</span>

</div>



<!-- Slot Arma -->

<div class="equip-slot">

  <span class="slot-label">Arma</span>

  <template v-if="inventario.armaEquipado">

    <span class="slot-item">{{ inventario.nombreArma }}</span>

    <span class="slot-lvl">Nv. {{ inventario.nivelArma }}</span> <!-- Ahora viene directo del DTO -->

    <button class="btn-quitar" @click="desequiparItem('ARMA')">Quitar</button>

  </template>

  <span v-else class="slot-empty">— vacío —</span>

</div>



<!-- Slot Accesorio -->

<div class="equip-slot">

  <span class="slot-label">Accesorio</span>

  <template v-if="inventario.accesorioEquipado">

    <span class="slot-item">{{ inventario.nombreAccesorio }}</span>

    <span class="slot-lvl">Nv. {{ inventario.nivelAccesorio }}</span> <!-- Ahora viene directo del DTO -->

    <button class="btn-quitar" @click="desequiparItem('ACCESORIO')">Quitar</button>

  </template>

  <span v-else class="slot-empty">— vacío —</span>

</div>

</div>



<!-- ===== ITEMS EN BOLSA ===== -->

<h3 class="section-title">Items en Bolsa</h3>

<!-- Quitamos el v-if del h3 para que siempre veas si la sección existe -->

<div v-if="itemsEnBolsaFiltrados.length > 0" class="item-list">

  <div v-for="item in itemsEnBolsaFiltrados" :key="item.idItem" class="item-row">

    <span class="item-name" :style="{ color: rarityColor(item.rareza) }">{{ item.nombreItem }}</span>

    <span class="item-meta">{{ item.tipo }} · Nv.{{ item.nivel }}</span>

    <button class="btn-equipar" @click="equiparItem(item)">Equipar</button>

  </div>

</div>

<div v-else class="empty-msg">

  No hay más items en tu bolsa.

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

          <!-- Boton para repartir botin (demuestra SP1: sp_repartir_botin) -->
          <div style="margin-top: 16px;">
            <button class="btn-crear-raid" @click="abrirBotinModal">🎁 Repartir Botín (SP1)</button>
          </div>

          <!-- Modal repartir botin -->
          <div v-if="showBotinModal" class="modal-overlay" @click.self="showBotinModal = false">
            <div class="modal-content">
              <h3>Repartir Botín</h3>
              <p style="font-size: 13px; color: var(--text-dim); margin-bottom: 16px;">
                Se descontarán los DKP de <strong>{{ currentChar.nombrePersonaje }}</strong>
                ({{ currentChar.puntosDkpActuales }} DKP disponibles)
              </p>
              <div class="form-group">
                <label>Item</label>
                <select v-model="botinItemId" class="select-input">
                  <option :value="null" disabled>Seleccionar item...</option>
                  <option v-for="item in allItems" :key="item.idItem" :value="item.idItem">
                    {{ item.nombreItem }} ({{ item.rareza }}, {{ item.costoDkp }} DKP)
                  </option>
                </select>
              </div>
              <div class="form-group">
                <label>Raid donde se obtuvo</label>
                <select v-model="botinRaidId" class="select-input">
                  <option :value="null" disabled>Seleccionar raid...</option>
                  <option v-for="rd in raids" :key="rd.raid.idRaid" :value="rd.raid.idRaid">
                    {{ rd.raid.nombreRaid }}
                  </option>
                </select>
              </div>
              <p v-if="botinError" class="msg error">{{ botinError }}</p>
              <p v-if="botinMsg" class="msg success">{{ botinMsg }}</p>
              <div class="modal-actions">
                <button class="btn-out" @click="showBotinModal = false">Cancelar</button>
                <button class="btn-inscribir" @click="repartirBotin">Asignar Botín</button>
              </div>
            </div>
          </div>

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

          <!-- Transferencia de liderazgo (Req 6: demuestra Trigger 2 trg_auditoria_liderazgo) -->
          <div v-if="currentChar.rolClan === 'Guild Master'" style="margin-top: 20px;">
            <h3 class="section-title">Transferir Liderazgo</h3>
            <div style="display: flex; gap: 12px; align-items: center;">
              <select v-model="transferTarget" class="select-input" style="flex: 1;">
                <option :value="null" disabled>Seleccionar nuevo líder...</option>
                <option v-for="m in clanMembers.filter(m => m.idPersonaje !== currentChar.idPersonaje)"
                        :key="m.idPersonaje" :value="m.idPersonaje">
                  {{ m.nombrePersonaje }} ({{ m.clase }})
                </option>
              </select>
              <button class="btn-inscribir" style="width: auto; padding: 10px 20px;" @click="transferirLiderazgo" :disabled="!transferTarget">
                Transferir Mando
              </button>
            </div>
            <p v-if="transferMsg" class="msg success" style="margin-top: 12px;">{{ transferMsg }}</p>
            <p v-if="transferError" class="msg error" style="margin-top: 12px;">{{ transferError }}</p>
          </div>

          <!-- Ranking (Req 7: Vista materializada) -->
          <div v-if="ranking.length > 0" class="section-header-flex" style="margin-top: 24px;">
            <h3 class="section-title" style="border: none; margin: 0;">Ranking del Clan</h3>
            <button class="btn-crear-raid" @click="refrescarRanking">⟳ Refrescar</button>
          </div>

          <div v-if="ranking.length > 0" class="members-table">

            <div class="rank-header">

              <span>#</span><span>Personaje</span><span>Clan</span><span>Raids</span><span>DKP</span>

            </div>

            <div v-for="(r, i) in ranking" :key="r.id_personaje" class="rank-row">

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
  <div class="section-header-flex">
    <h2>Calendario de Raids</h2>
    <button v-if="currentChar.rolClan === 'Guild Master'" class="btn-crear-raid" @click="abrirModalCrear">+ Programar Raid</button>
  </div>

  <div v-if="loadingRaids" class="loader">Cargando raids...</div>
  
  <template v-else>
    <div v-for="rd in raids" :key="rd.raid.idRaid" class="raid-card">
      
      <!-- CABECERA ÚNICA -->
      <div class="raid-top">
        <div>
          <h3 class="raid-name">{{ rd.raid.nombreRaid }}</h3>
          <span class="raid-date">{{ formatDate(rd.raid.fechaRaid) }}</span>
        </div>
        
        <!-- ESTADO DE INSCRIPCIÓN -->
        <div v-if="estaInscrito(rd)" class="raid-status-active">
          <span class="badge-inscrito" style="color: #2ecc71; font-weight: bold; margin-right: 10px;">✓ INSCRITO</span>
          <button class="btn-anular" @click="anularInscripcion(rd)" style="background-color: #e74c3c; color: white; border: none; padding: 5px 10px; border-radius: 4px; cursor: pointer;">
            Anular Participación
          </button>
        </div>
        
        <div v-else class="raid-ilvl">
          <span class="ilvl-req">iLvl mín: {{ rd.raid.itemLevelMinimo }}</span>
        </div>
      </div>

      <!-- FILA DE CUPOS (Solo se muestran botones si NO está inscrito) -->
      <div class="cupos-row" style="display: flex; gap: 20px; margin-top: 15px; justify-content: space-around;">
        <!-- TANQUES -->
        <div class="cupo" style="text-align: center;">
          <div class="cupo-label">Tanques</div>
          <div class="cupo-num">{{ rd.cuposTanqueLibres }}/{{ rd.raid.tanques }}</div>
          <button 
            v-if="!estaInscrito(rd) && rd.cuposTanqueLibres > 0 && puedeCumplirRol('TANQUE') && currentChar.itemLevel >= rd.raid.itemLevelMinimo" 
            class="btn-inscribir" @click="inscribirse(rd, 'TANQUE')"
          > Inscribir </button>
        </div>

        <!-- HEALERS -->
        <div class="cupo" style="text-align: center;">
          <div class="cupo-label">Healers</div>
          <div class="cupo-num">{{ rd.cuposHealerLibres }}/{{ rd.raid.healers }}</div>
          <button 
            v-if="!estaInscrito(rd) && rd.cuposHealerLibres > 0 && puedeCumplirRol('HEALER') && currentChar.itemLevel >= rd.raid.itemLevelMinimo" 
            class="btn-inscribir" @click="inscribirse(rd, 'HEALER')"
          > Inscribir </button>
        </div>

        <!-- DPS -->
        <div class="cupo" style="text-align: center;">
          <div class="cupo-label">DPS</div>
          <div class="cupo-num">{{ rd.cuposDpsLibres }}/{{ rd.raid.dps }}</div>
          <button 
            v-if="!estaInscrito(rd) && rd.cuposDpsLibres > 0 && puedeCumplirRol('DPS') && currentChar.itemLevel >= rd.raid.itemLevelMinimo" 
            class="btn-inscribir" @click="inscribirse(rd, 'DPS')"
          > Inscribir </button>
        </div>
      </div>

      <!-- Ver inscritos y confirmar asistencia (Req 2: GM confirma) -->
      <div style="margin-top: 12px; text-align: center;">
        <button class="btn-out" @click="verInscritos(rd.raid.idRaid)">
          {{ showInscritos === rd.raid.idRaid ? 'Ocultar inscritos' : 'Ver inscritos' }}
        </button>
      </div>
      <div v-if="showInscritos === rd.raid.idRaid && inscritosRaid.length > 0" class="inscritos-list">
        <div v-for="ins in inscritosRaid" :key="ins.id_inscripcion" class="inscrito-row">
          <span class="mt-name">{{ ins.nombre_personaje }}</span>
          <span class="item-meta">{{ ins.rol_en_raid }}</span>
          <span :style="{ color: ins.confirmado ? 'var(--green)' : 'var(--text-dim)' }">
            {{ ins.confirmado ? '✓ Confirmado' : 'Pendiente' }}
          </span>
          <button
            v-if="!ins.confirmado && currentChar.rolClan === 'Guild Master'"
            class="btn-equipar" @click="confirmarAsistencia(ins.id_inscripcion)">
            Confirmar
          </button>
        </div>
      </div>
      <div v-if="showInscritos === rd.raid.idRaid && inscritosRaid.length === 0" class="empty-msg" style="margin-top: 8px; padding: 12px;">
        No hay inscritos en esta raid.
      </div>

    </div> <!-- Cierre de raid-card -->
  </template>

  <!-- MODAL DE CREACIÓN -->
  <div v-if="showCreateModal" class="modal-overlay" @click.self="showCreateModal = false">
    <div class="modal-content">
      <h3>Programar Nueva Raid</h3>
      <div class="form-group">
        <label>Nombre</label>
        <input v-model="newRaid.nombreRaid" />
      </div>
      <div class="form-group">
        <label>Fecha</label>
        <input type="datetime-local" v-model="newRaid.fechaRaid" />
      </div>
      <div class="modal-actions">
        <button class="btn-out" @click="showCreateModal = false">Cancelar</button>
        <button class="btn-inscribir" @click="crearRaid">Crear</button>
      </div>
    </div>
  </div>
</section>



    </main>
  </div>
</template>


 

<style scoped>
  /* --- Layout y Estructura Principal --- */
  .layout { display: flex; height: 100vh; overflow: hidden; }
  .sidebar { width: 260px; background: var(--bg-card); border-right: 1px solid var(--border); display: flex; flex-direction: column; flex-shrink: 0; }
  .content { flex: 1; overflow-y: auto; background: var(--bg-main); }
  .tab-content { padding: 40px; max-width: 1000px; margin: 0 auto; } /* Expandido para dar aire al ranking */

  /* --- Sidebar: Encabezado de Personaje --- */
  .char-header { padding: 24px 20px 20px; border-bottom: 1px solid var(--border); }
  .char-class { font-size: 11px; color: var(--text-dim); text-transform: uppercase; letter-spacing: 1px; }
  .char-name { font-size: 20px; margin: 4px 0 8px; }
  .char-meta { display: flex; gap: 8px; }

  /* --- Badges y Pills --- */
  .badge-gm { background: rgba(255, 128, 0, 0.15); color: #ff8000; padding: 2px 8px; border-radius: 4px; font-size: 11px; font-weight: bold; border: 1px solid rgba(255, 128, 0, 0.3); }
  .badge-raider { background: rgba(163, 53, 238, 0.15); color: #a335ee; padding: 2px 8px; border-radius: 4px; font-size: 11px; font-weight: bold; border: 1px solid rgba(163, 53, 238, 0.3); }
  .badge-member { background: rgba(255, 255, 255, 0.1); color: var(--text-dim); padding: 2px 8px; border-radius: 4px; font-size: 11px; }
  .ilvl-pill { background: rgba(212,168,67,0.15); color: var(--gold); padding: 2px 10px; border-radius: 12px; font-size: 12px; font-weight: 700; }

  /* --- Navegación Lateral --- */
  .nav { flex: 1; padding: 12px; display: flex; flex-direction: column; gap: 4px; }
  .nav button { background: none; border: none; color: var(--text-dim); text-align: left; padding: 12px 16px; cursor: pointer; border-radius: 8px; font-size: 14px; display: flex; align-items: center; gap: 10px; transition: all 0.15s; }
  .nav button.active { background: rgba(212,168,67,0.12); color: var(--gold); }
  .nav-icon { font-size: 16px; }

  .sidebar-footer { padding: 16px; border-top: 1px solid var(--border); display: flex; flex-direction: column; gap: 8px; }
  .btn-out { background: none; border: 1px solid var(--border); color: var(--text-dim); padding: 8px; border-radius: 6px; cursor: pointer; font-size: 12px; transition: 0.2s; }
  .btn-out:hover { background: rgba(255,255,255,0.05); color: var(--text); }
  .btn-out.danger:hover { border-color: var(--red); color: var(--red); }

  /* --- Grid de Estadísticas --- */
  .stats-grid { display: grid; grid-template-columns: repeat(5, 1fr); gap: 16px; margin-bottom: 32px; }
  .stat-card { background: var(--bg-card); border: 1px solid var(--border); border-radius: 10px; padding: 20px; text-align: center; }
  .sc-label { display: block; font-size: 11px; color: var(--text-dim); text-transform: uppercase; margin-bottom: 4px; }
  .sc-value { font-size: 24px; font-weight: 700; display: block; }
  .sc-value.gold { color: var(--gold); }

  /* --- Secciones y Títulos --- */
  .section-title { font-size: 16px; margin: 24px 0 12px; padding-bottom: 8px; border-bottom: 1px solid var(--border); color: var(--text-dim); }
  .section-header-flex { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }

  /* --- Equipamiento (Evitando solapamiento) --- */
  .equip-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 20px; margin-bottom: 24px; }
  .equip-slot { 
    background: var(--bg-card); border: 1px solid var(--border); border-radius: 8px; padding: 20px; 
    text-align: center; position: relative; min-height: 140px; 
    display: flex; flex-direction: column; align-items: center; justify-content: center;
  }
  .slot-label { display: block; font-size: 11px; color: var(--text-dim); text-transform: uppercase; margin-bottom: 8px; }
  .slot-item { font-size: 14px; font-weight: 600; color: var(--gold); display: block; }
  .slot-lvl { display: block; font-size: 12px; color: var(--text-dim); margin-top: 4px; }
  .slot-empty { color: var(--text-dim); font-style: italic; font-size: 13px; }

  .btn-quitar {
    background: rgba(220, 53, 69, 0.1); border: 1px solid rgba(220, 53, 69, 0.4);
    color: #ff8585; padding: 6px 14px; border-radius: 4px; font-size: 11px;
    font-weight: bold; cursor: pointer; transition: all 0.2s ease;
    margin-top: 12px; text-transform: uppercase; display: inline-block;
  }
  .btn-quitar:hover { background: #dc3545; color: white; border-color: #dc3545; box-shadow: 0 0 10px rgba(220, 53, 69, 0.3); transform: translateY(-1px); }

  /* --- Inventario e Items --- */
  .item-list { display: flex; flex-direction: column; gap: 4px; }
  .item-row { display: flex; align-items: center; gap: 12px; padding: 10px 16px; background: var(--bg-card); border: 1px solid var(--border); border-radius: 8px; transition: 0.2s; }
  .item-row:hover { border-color: var(--gold); }
  .item-name { flex: 1; font-weight: 600; font-size: 14px; }
  .item-meta { font-size: 12px; color: var(--text-dim); }
  .item-rarity { font-size: 11px; font-weight: bold; text-transform: uppercase; width: 80px; text-align: right; }
  .btn-equipar { background: rgba(212,168,67,0.1); border: 1px solid var(--gold); color: var(--gold); padding: 4px 12px; border-radius: 4px; font-size: 11px; cursor: pointer; font-weight: bold; }
  .btn-equipar:hover { background: var(--gold); color: #000; }

  /* --- Tabla de Ranking (Ajuste de columnas para espacio) --- */
  .members-table { background: var(--bg-card); border: 1px solid var(--border); border-radius: 10px; overflow: hidden; }
  .mt-header, .mt-row { 
    display: grid; 
    grid-template-columns: 0.6fr 2fr 2.5fr 1fr 1fr; /* Proporciones optimizadas */
    padding: 16px 20px; align-items: center; font-size: 13px; 
  }
  .mt-header { background: rgba(255,255,255,0.03); color: var(--text-dim); border-bottom: 1px solid var(--border); font-weight: bold; }
  .mt-row { border-bottom: 1px solid var(--border); }
  .mt-row:last-child { border-bottom: none; }
  .mt-row.is-me { background: rgba(212,168,67,0.05); }
  .mt-name { font-weight: bold; color: var(--text); }
  .rank-pos { font-weight: bold; color: var(--gold); }

  /* --- Calendario de Raids --- */
  .raid-card { background: var(--bg-card); border: 1px solid var(--border); border-radius: 12px; padding: 24px; margin-bottom: 20px; }
  .raid-top { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 20px; }
  .raid-name { font-size: 18px; margin: 0 0 4px 0; color: var(--gold); }
  .raid-date { font-size: 13px; color: var(--text-dim); }
  .raid-ilvl { text-align: right; display: flex; flex-direction: column; gap: 4px; }
  .ilvl-req { font-size: 12px; color: var(--text-dim); }
  .check-ok { color: var(--green); font-size: 12px; font-weight: bold; }
  .check-fail { color: var(--red); font-size: 12px; font-weight: bold; }

  .cupos-row { display: grid; grid-template-columns: repeat(3, 1fr); gap: 20px; padding-top: 16px; border-top: 1px solid var(--border); }
  .cupo { display: flex; flex-direction: column; gap: 8px; align-items: center; }
  .cupo-label { font-size: 11px; color: var(--text-dim); font-weight: bold; }

  .btn-inscribir { width: 100%; background: var(--gold); color: #000; border: none; padding: 10px; border-radius: 6px; font-weight: bold; cursor: pointer; transition: 0.2s; }
  .btn-inscribir:hover { opacity: 0.9; transform: translateY(-1px); }
  .btn-anular { background: rgba(220, 53, 69, 0.2); border: 1px solid #dc3545; color: #ff8585; padding: 8px 12px; border-radius: 4px; font-weight: bold; cursor: pointer; transition: 0.2s; font-size: 12px; margin-top: 12px; }
  .btn-anular:hover { background: rgba(220, 53, 69, 0.4); color: #fff; }

  /* --- Modales y Formularios --- */
  .modal-overlay { position: fixed; top: 0; left: 0; width: 100vw; height: 100vh; background: rgba(0, 0, 0, 0.85); backdrop-filter: blur(4px); display: flex; align-items: center; justify-content: center; z-index: 1000; }
  .modal-content { background: var(--bg-card); border: 1px solid var(--gold); border-radius: 12px; padding: 32px; width: 100%; max-width: 500px; box-shadow: 0 20px 50px rgba(0,0,0,0.5); }
  .modal-content h3 { color: var(--gold); margin: 0 0 24px; text-transform: uppercase; letter-spacing: 1px; border-bottom: 1px solid rgba(212, 168, 67, 0.2); padding-bottom: 12px; }

  .form-group { margin-bottom: 20px; display: flex; flex-direction: column; gap: 8px; }
  .form-group label { font-size: 12px; color: var(--text-dim); text-transform: uppercase; font-weight: bold; }
  
  input { background: rgba(0, 0, 0, 0.3); border: 1px solid var(--border); border-radius: 6px; padding: 10px 14px; color: var(--text); font-size: 14px; transition: border-color 0.2s; }
  input:focus { outline: none; border-color: var(--gold); background: rgba(0, 0, 0, 0.5); }

  .cupos-input-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 15px; margin: 24px 0; align-items: end; }
  .cupos-input-grid div { display: flex; flex-direction: column; gap: 8px; }
  .cupos-input-grid label { font-size: 10px; white-space: nowrap; text-align: center; }
  .cupos-input-grid input { width: 100%; text-align: center; padding: 10px 5px; }

  .modal-actions { display: flex; gap: 12px; margin-top: 32px; }
  .modal-actions button { flex: 1; padding: 12px; font-size: 14px; font-weight: bold; cursor: pointer; border-radius: 6px; transition: 0.2s; }
  .btn-crear-raid { background: none; border: 1px solid var(--gold); color: var(--gold); padding: 6px 16px; border-radius: 4px; font-weight: bold; cursor: pointer; transition: 0.2s; }
  .btn-crear-raid:hover { background: rgba(212, 168, 67, 0.1); }

  /* Input Fecha/Hora Especial */
  input[type="datetime-local"]::-webkit-calendar-picker-indicator {
    cursor: pointer;
    background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="%23d4a843" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect><line x1="16" y1="2" x2="16" y2="6"></line><line x1="8" y1="2" x2="8" y2="6"></line><line x1="3" y1="10" x2="21" y2="10"></line></svg>');
    background-size: contain; width: 18px; height: 18px;
  }

  /* Estados y Mensajes */
  .loader { text-align: center; padding: 40px; color: var(--text-dim); font-style: italic; }
  .empty-msg { text-align: center; padding: 40px; color: var(--text-dim); background: rgba(255,255,255,0.02); border-radius: 8px; border: 1px dashed var(--border); }
  .msg { padding: 12px 16px; border-radius: 8px; margin-bottom: 20px; font-size: 14px; font-weight: 500; }
  .msg.success { background: rgba(74, 222, 128, 0.1); border: 1px solid var(--green); color: var(--green); }
  .msg.error { background: rgba(248, 113, 113, 0.1); border: 1px solid var(--red); color: var(--red); }

  /* Ranking con columnas propias (evita desalineacion) */
  .rank-header, .rank-row {
    display: grid;
    grid-template-columns: 0.4fr 2fr 2fr 0.8fr 0.8fr;
    padding: 16px 20px; align-items: center; font-size: 13px; text-align: center;
  }
  .rank-header { background: rgba(255,255,255,0.03); color: var(--text-dim); border-bottom: 1px solid var(--border); font-weight: bold; }
  .rank-row { border-bottom: 1px solid var(--border); }
  .rank-row:last-child { border-bottom: none; }

  /* Select input para modales */
  .select-input {
    width: 100%; background: rgba(0,0,0,0.3); border: 1px solid var(--border);
    border-radius: 6px; padding: 10px 14px; color: var(--text); font-size: 14px;
    appearance: none; cursor: pointer;
  }
  .select-input:focus { outline: none; border-color: var(--gold); }
  .select-input option { background: var(--bg-card); color: var(--text); }

  /* Lista de inscritos a raid */
  .inscritos-list { margin-top: 12px; background: rgba(0,0,0,0.2); border-radius: 8px; padding: 8px; }
  .inscrito-row { display: flex; align-items: center; gap: 12px; padding: 8px 12px; border-bottom: 1px solid var(--border); }
  .inscrito-row:last-child { border-bottom: none; }
</style>