<template>
  <div class="min-h-screen bg-slate-950 text-slate-100">
    <div class="absolute inset-0 -z-10 overflow-hidden">
      <div class="h-full w-full bg-[radial-gradient(circle_at_top,_rgba(30,58,138,0.35),_transparent_55%)]"></div>
      <div class="absolute -right-24 top-24 h-72 w-72 rounded-full bg-amber-400/15 blur-3xl"></div>
      <div class="absolute -left-20 bottom-20 h-72 w-72 rounded-full bg-sky-400/10 blur-3xl"></div>
    </div>

    <router-view />
  </div>
</template>
