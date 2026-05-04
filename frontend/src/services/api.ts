// src/services/api.ts

const API_URL = '/api';

// --- Interfaces ---
export interface Usuario {
  idUsuario?: number;
  nombreUsuario: string;
  rol: string;
}

export interface Personaje {
  idPersonaje: number;
  idUsuario: number;
  idClan: number | null;
  nombrePersonaje: string;
  clase: string;
  nivel: number;
  faccion: string;
  rolClan: string;
  itemLevel: number;
  puntosDkpActuales: number;
}

export interface AuthResponse {
  token: string;
}

export interface Clan {
  idClan: number;
  idLider: number | null;
  nombreClan: string;
}

export interface Raid {
  idRaid: number;
  nombreRaid: string;
  fechaRaid: string;
  itemLevelMinimo: number;
  tanques: number;
  healers: number;
  dps: number;
  estado: string;
}

export interface RaidDTO {
  raid: Raid;
  cuposTanqueLibres: number;
  cuposHealerLibres: number;
  cuposDpsLibres: number;
}


export interface InventarioDTO {
  idInventario: number;
  idPersonaje: number;
  armaduraEquipado: number | null;
  armaEquipado: number | null;
  accesorioEquipado: number | null;
  nombreArmadura: string | null;
  nivelArmadura: number | null;   // <--- Añadir esto
  nombreArma: string | null;
  nivelArma: number | null;       // <--- Añadir esto
  nombreAccesorio: string | null;
  nivelAccesorio: number | null;  // <--- Añadir esto
}

export interface InventarioItemDTO {
  idInventario: number;
  idItem: number;
  nombreItem: string;
  rareza: string;
  tipo: string;
  nivel: number;
  costoDkp: number;
}

export interface Item {
  idItem: number;
  nombreItem: string;
  rareza: string;
  tipo: string;
  nivel: number;
  costoDkp: number;
}

export interface HistorialBotin {
  idEntrega: number;
  idPersonaje: number;
  idItem: number;
  idRaid: number;
  nombreItem: string;
  nombrePersonaje: string;
  fechaEntrega: string;
}

export interface RankingEntry {
  id_personaje: number;
  nombre_personaje: string;
  nombre_clan: string;
  raids_asistidas: number;
  puntos_dkp_actuales: number;
}

// --- Motor de peticiones ---
async function apiRequest<T>(endpoint: string, method: string = 'GET', body?: any): Promise<T> {
  const token = localStorage.getItem('auth_token');
  const headers: HeadersInit = { 'Content-Type': 'application/json' };
  if (token) headers['Authorization'] = `Bearer ${token}`;

  const response = await fetch(`${API_URL}${endpoint}`, {
    method,
    headers,
    body: body ? JSON.stringify(body) : undefined,
  });

  if (!response.ok) {
    if (response.status === 401) localStorage.removeItem('auth_token');
    const errorText = await response.text();
    throw new Error(errorText || 'Error en el servidor');
  }

  const contentType = response.headers.get('content-type');
  if (contentType && contentType.includes('application/json')) {
    return response.json();
  } else {
    return response.text() as unknown as T;
  }
}

// --- Servicios ---
export const authService = {
  async login(username: string, password: string) {
    const data = await apiRequest<AuthResponse>('/auth/login', 'POST', { username, password });
    localStorage.setItem('auth_token', data.token);
    return data;
  },
  register(data: { nombreUsuario: string; password: string }) {
    return apiRequest<string>('/auth/register', 'POST', data);
  },
  getMe() {
    return apiRequest<Usuario>('/usuarios/me');
  },
};

export const personajeService = {
  getMisPersonajes() {
    return apiRequest<Personaje[]>('/personajes/mis-personajes');
  },
  crearPersonaje(personaje: Partial<Personaje>) {
    return apiRequest<string>('/personajes', 'POST', personaje);
  },
  getById(id: number) {
    return apiRequest<Personaje>(`/personajes/${id}`);
  },
};

export const clanService = {
  getAll() {
    return apiRequest<Clan[]>('/clanes');
  },
  getById(id: number) {
    return apiRequest<Clan>(`/clanes/${id}`);
  },
  getMiembros(idClan: number) {
    // No hay endpoint directo, usamos personajes y filtramos
    return apiRequest<Personaje[]>(`/personajes?page=0&size=100`);
  },
  transferLeadership(idClan: number, idCurrentLeader: number, idNewLeader: number) {
    return apiRequest<string>(`/clanes/${idClan}/transfer-leadership`, 'PUT', {
      idCurrentLeader, idNewLeader
    });
  },
};

export const raidService = {
  getCalendario() {
    return apiRequest<RaidDTO[]>('/raids/calendario');
  },

  inscribirse(idRaid: number, idPersonaje: number, rolEnRaid: string) {
    return apiRequest<string>('/raids/inscribirse', 'POST', {
      idRaid,
      idPersonaje,
      rolEnRaid,
    });
  },

  // === AÑADE ESTA FUNCIÓN AQUÍ ===
  create(raidData: any) {
    return apiRequest<string>('/raids', 'POST', raidData);
  },
  desinscribirse(idRaid: number, idPersonaje: number) {
    return apiRequest<string>(`/raids/desinscribirse?idRaid=${idRaid}&idPersonaje=${idPersonaje}`, 'DELETE');
  },
};


export const inventarioService = {

  getByPersonaje(idPersonaje: number) {
    return apiRequest<InventarioDTO>(`/inventarios/por-personaje/${idPersonaje}`);
  },

  getItemsByPersonaje(idPersonaje: number) {
    return apiRequest<InventarioItemDTO[]>(`/inventarios/por-personaje/${idPersonaje}/items`);
  },

  update(idInventario: number, data: any) {
    return apiRequest<string>(`/inventarios/id/${idInventario}`, 'PUT', data);
  }
};

export const itemService = {
  getAll() {
    return apiRequest<Item[]>('/items?page=0&size=100');
  },
  getHistorial(idPersonaje: number) {
    return apiRequest<HistorialBotin[]>(`/items/historial/${idPersonaje}`);
  },
  getRanking() {
    return apiRequest<RankingEntry[]>('/items/ranking');
  },
  repartirBotin(idPersonaje: number, idItem: number, idRaid: number) {
    return apiRequest<string>('/items/repartir', 'POST', { idPersonaje, idItem, idRaid });
  },
  refrescarRanking() {
    return apiRequest<string>('/items/ranking/refrescar', 'POST');
  },
};

export const inscripcionService = {
  getByRaid(idRaid: number) {
    return apiRequest<any[]>(`/inscripciones/por-raid/${idRaid}`);
  },
  confirmarAsistencia(idInscripcion: number, idEjecutor: number) {
    return apiRequest<string>(`/inscripciones/${idInscripcion}/confirmar`, 'PUT', { idEjecutor });
  },
};

