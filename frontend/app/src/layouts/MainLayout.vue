<template>
  <div class="main-layout">
    <Sidebar />
    <main class="content">
      <router-view />
    </main>
  </div>
</template>

<script>
import Sidebar from "../components/Sidebar.vue";

export default {
  name: "MainLayout",
  components: {
    Sidebar,
  },
};
</script>

<style scoped>
/* Flexbox para layout principal */
.main-layout {
  display: flex; /* Ativa o flexbox */
  height: 100vh; /* Garante altura total da tela */
}

/* Sidebar fixa */
.main-layout .sidebar {
  flex: 0 0 250px; /* Largura fixa de 250px */
}

/* Conteúdo principal */
.main-layout .content {
  flex: 1; /* Ocupa o espaço restante */
  padding: 20px; /* Espaçamento interno */
  overflow-y: auto; /* Permite rolagem se necessário */
  background-color: #f5f5f5; /* Cor de fundo para diferenciar */
}
</style>
