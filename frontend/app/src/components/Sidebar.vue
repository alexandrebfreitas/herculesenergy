<template>
  <div :class="['sidebar', { collapsed: isCollapsed }]">
    <button class="toggle-btn" @click="toggleSidebar">
      <span v-if="isCollapsed">➤</span>
      <span v-else>◀</span>
    </button>
    <nav>
      <ul>
        <li>
          <router-link to="/">
            <font-awesome-icon :icon="['fas', 'home']" />
            <span v-if="!isCollapsed">Home</span>
          </router-link>
        </li>
        <li>
          <router-link to="/about">
            <font-awesome-icon :icon="['fas', 'user']" />
            <span v-if="!isCollapsed">About</span>
          </router-link>
        </li>
        <li>
          <router-link to="/contact">
            <font-awesome-icon :icon="['fas', 'envelope']" />
            <span v-if="!isCollapsed">Contact</span>
          </router-link>
        </li>
        <li>
          <router-link to="/file-mapping">
            <font-awesome-icon :icon="['fas', 'folder-open']" />
            <span v-if="!isCollapsed">File Mapping</span>
          </router-link>
        </li>
      </ul>
    </nav>
  </div>
</template>

<script>
export default {
  name: "AppSidebar",
  data() {
    return {
      isCollapsed: false, // Estado inicial da sidebar
    };
  },
  methods: {
    toggleSidebar() {
      this.isCollapsed = !this.isCollapsed;
      localStorage.setItem("isCollapsed", this.isCollapsed); // Salva o estado no localStorage
    },
  },
  mounted() {
    // Recupera o estado salvo no localStorage
    const savedState = localStorage.getItem("isCollapsed");
    if (savedState !== null) {
      this.isCollapsed = JSON.parse(savedState); // Converte o valor de string para boolean
    }
  },
};
</script>

<style scoped>
/* Sidebar básica */
.sidebar {
  width: 250px;
  height: 100vh;
  background-color: #2c3e50;
  color: white;
  padding: 20px;
  box-shadow: 2px 0 5px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  transition: width 0.3s ease; /* Transição suave */
}

/* Sidebar comprimida */
.sidebar.collapsed {
  width: 80px;
}

/* Botão de alternância */
.toggle-btn {
  align-self: flex-end;
  background: none;
  border: none;
  color: white;
  font-size: 18px;
  cursor: pointer;
  margin-bottom: 20px;
  transition: transform 0.3s ease;
}

.toggle-btn:hover {
  transform: scale(1.1);
}

/* Navegação */
.sidebar nav ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.sidebar nav ul li {
  margin: 15px 0;
}

.sidebar nav ul li a {
  color: white;
  text-decoration: none;
  display: flex;
  align-items: center;
  gap: 10px;
  white-space: nowrap; /* Evita quebra de texto */
}

.sidebar nav ul li a:hover {
  color: #1abc9c;
}

/* Ajuste visual para a sidebar comprimida */
.sidebar.collapsed nav ul li a span {
  display: none; /* Esconde o texto */
}

.sidebar.collapsed nav ul li a {
  justify-content: center; /* Centraliza o ícone */
}
</style>
