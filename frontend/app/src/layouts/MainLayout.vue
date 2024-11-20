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
.main-layout {
  display: flex;
  height: 100vh;
}

/* Ajuste dinâmico do conteúdo */
.sidebar {
  flex-shrink: 0;
}

.content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background-color: #f5f5f5;
  transition: margin-left 0.3s ease; /* Transição suave */
}
</style>
