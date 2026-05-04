<script setup lang="ts">
import { ref } from 'vue';
import LoginView from './views/LoginView.vue';
import CharacterSelection from './views/CharacterSelection.vue';
import MainDashboard from './views/MainDashboard.vue';

const currentStep = ref<'login' | 'select' | 'main'>('login');

const session = ref({
  token: '',
  username: '',
  rol: '',
  character: null as any
});

const onLogin = (data: { token: string; user: string; rol: string }) => {
  session.value.token = data.token;
  session.value.username = data.user;
  session.value.rol = data.rol;
  currentStep.value = 'select';
};

const onCharacterActive = (char: any) => {
  session.value.character = char;
  currentStep.value = 'main';
};

const onBackToSelect = () => {
  currentStep.value = 'select';
};

const onLogout = () => {
  localStorage.removeItem('auth_token');
  session.value = { token: '', username: '', rol: '', character: null };
  currentStep.value = 'login';
};
</script>

<template>
  <div id="app-wrapper">
    <LoginView
      v-if="currentStep === 'login'"
      @auth-success="onLogin"
    />
    <CharacterSelection
      v-else-if="currentStep === 'select'"
      :token="session.token"
      :username="session.username"
      @char-selected="onCharacterActive"
      @logout="onLogout"
    />
    <MainDashboard
      v-else-if="currentStep === 'main'"
      :character="session.character"
      :token="session.token"
      @change-char="onBackToSelect"
      @logout="onLogout"
    />
  </div>
</template>

<style>
@import url('https://fonts.googleapis.com/css2?family=Cinzel:wght@400;700&family=Nunito:wght@400;600;700&display=swap');
:root {
  --gold: #d4a843;
  --gold-dim: #a07d2e;
  --bg-dark: #0d1117;
  --bg-card: #161b22;
  --border: #30363d;
  --text: #e6edf3;
  --text-dim: #8b949e;
  --purple: #bc8cff;
  --red: #f85149;
  --green: #3fb950;
  --blue: #58a6ff;
  --legendary: #ff8000;
  --epic: #a335ee;
  --rare: #0070dd;
  --common: #9d9d9d;
}
* { box-sizing: border-box; margin: 0; padding: 0; }
body { background: var(--bg-dark); color: var(--text); font-family: 'Nunito', sans-serif; }
h1, h2, h3 { font-family: 'Cinzel', serif; color: var(--gold); }
::-webkit-scrollbar { width: 6px; }
::-webkit-scrollbar-track { background: var(--bg-dark); }
::-webkit-scrollbar-thumb { background: var(--border); border-radius: 3px; }
</style>
