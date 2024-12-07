<template>
  <div class="main-layout">
    <header class="main-header shadow-sm">
      <div class="header-container">
        <!-- Nome da Empresa -->
        <h1 class="company-name">Hercules</h1>

        <!-- Informações do Usuário e Botão do Menu -->
        <div class="header-right">
          <div class="user-info">
            <span class="user-name"><i class="fas fa-user"></i> Fulano</span>
            <span class="user-company"
              ><i class="fas fa-building"></i> Fantasia</span
            >
          </div>
          <div class="dropdown">
            <button
              class="btn btn-primary header-btn styled-menu-btn"
              type="button"
              id="menuButton"
              data-bs-toggle="dropdown"
              aria-expanded="false"
            >
              <font-awesome-icon icon="bars" />
            </button>
            <ul class="dropdown-menu" aria-labelledby="menuButton">
              <li><a class="dropdown-item" href="/about">About</a></li>
              <li><a class="dropdown-item" href="/login">Login</a></li>
              <li><a class="dropdown-item" href="/register">Register</a></li>
              <li><hr class="dropdown-divider" /></li>
              <li><a class="dropdown-item" href="/logout">Sair</a></li>
            </ul>
          </div>
        </div>
      </div>
    </header>

    <div class="content-wrapper">
      <Sidebar />
      <main class="content">
        <router-view />
      </main>
    </div>
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
/* Layout Principal */
.main-layout {
  display: flex;
  flex-direction: column;
  height: 100vh;
  min-height: 0; /* Permite que os filhos encolham dentro da altura da tela */
  z-index: 999; /* main content terá z-index menor que o sidebar */
}

/* Header */
.main-header {
  background-color: #ffffff;
  padding: 10px 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #e0e0e0;
}
.content {
  z-index: 1; /* main content terá z-index menor que o sidebar */
}

.content-wrapper > Sidebar {
  position: fixed;
  top: 60px; /* Abaixo do header */
  left: 0;
  width: 250px; /* Largura do sidebar */
  height: calc(100vh - 60px); /* Altura do sidebar */
  background-color: #2c3e50;
  z-index: 9999; /* Bem acima do main content */
  overflow: visible; /* Permite que o botão saia da área */
}

.content {
  flex: 1;
  padding: 20px;
  overflow-y: auto; /* Ativa a barra de rolagem vertical quando exceder o espaço disponível */
  background-color: #f5f5f5;
  transition: margin-left 0.3s ease;
  min-height: 0; /* Garante que a área do main possa encolher e rolar */
  position: relative;
  overflow-y: auto;
}
.header-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
}

/* Nome da Empresa */
.company-name {
  font-family: "Roboto", sans-serif;
  font-weight: bold;
  color: #333;
  font-size: 24px;
  margin: 0;
}

/* Informações do Usuário e Botão do Menu */
.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.user-info {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.user-info .user-name,
.user-info .user-company {
  font-size: 14px;
  color: #555;
}

.user-info .user-name i,
.user-info .user-company i {
  margin-right: 5px;
}

/* Botão do Menu */
.header-btn {
  font-size: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 5px;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  transition: background-color 0.3s, box-shadow 0.3s;
}

.header-btn i {
  font-size: 20px;
}

.header-btn:hover {
  background-color: #0056b3;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

/* Conteúdo */
.content-wrapper {
  display: flex;
  flex: 1;
}

.content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background-color: #f5f5f5;
  transition: margin-left 0.3s ease;
}
.styled-menu-btn {
  background-color: #0056b3; /* Azul escuro */
  color: #fff; /* Cor do texto */
  font-size: 20px; /* Tamanho do ícone */
  padding: 10px 15px; /* Espaçamento interno */
  border: none; /* Sem borda */
  border-radius: 50%; /* Botão arredondado */
  display: flex;
  justify-content: center;
  align-items: center;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1); /* Sombra suave */
  transition: all 0.3s ease; /* Transição suave para hover */
}

.styled-menu-btn:hover {
  background-color: #004494; /* Azul mais escuro no hover */
  box-shadow: 0 6px 8px rgba(0, 0, 0, 0.2); /* Sombra mais forte no hover */
  transform: scale(1.1); /* Leve ampliação */
}

.styled-menu-btn:active {
  background-color: #003366; /* Azul ainda mais escuro no clique */
  box-shadow: inset 0 3px 6px rgba(0, 0, 0, 0.3); /* Sombra interna */
}
</style>
