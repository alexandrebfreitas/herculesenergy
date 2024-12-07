<template>
  <div :class="['sidebar', { collapsed: isCollapsed }]">
    <button class="toggle-btn" @click="toggleSidebar">
      <font-awesome-icon
        :icon="isCollapsed ? ['fas', 'chevron-right'] : ['fas', 'chevron-left']"
      />
    </button>
    <nav>
      <ul>
        <li>
          <router-link to="/" class="tooltip-container">
            <font-awesome-icon :icon="['fas', 'home']" />
            <span v-if="!isCollapsed">Home</span>
            <span class="tooltip" v-if="isCollapsed">Home</span>
          </router-link>
        </li>
        <li>
          <div @click="toggleSubmenu" class="menu-item tooltip-container">
            <font-awesome-icon :icon="['fas', 'book']" />
            <span v-if="!isCollapsed">Rodadas de Preço</span>
            <span class="tooltip" v-if="isCollapsed">Rodadas de Preço</span>
            <font-awesome-icon
              class="submenu-indicator"
              :icon="
                isSubmenuOpen
                  ? ['fas', 'chevron-down']
                  : ['fas', 'chevron-right']
              "
              v-if="!isCollapsed"
            />
          </div>
          <ul :class="['submenu', { expanded: isSubmenuOpen }]">
            <li>
              <router-link
                to="/rodadas-preco/meteorologia"
                class="tooltip-container"
              >
                <font-awesome-icon :icon="['fas', 'cloud-sun']" />
                <span v-if="!isCollapsed">Meteorologia</span>
                <span class="tooltip" v-if="isCollapsed">Meteorologia</span>
              </router-link>
            </li>
            <li>
              <router-link
                to="/rodadas-preco/hidrologia"
                class="tooltip-container"
              >
                <font-awesome-icon :icon="['fas', 'water']" />
                <span v-if="!isCollapsed">Hidrologia (SMAP)</span>
                <span class="tooltip" v-if="isCollapsed"
                  >Hidrologia (SMAP)</span
                >
              </router-link>
            </li>
            <li>
              <router-link to="/rodadas-preco/decks" class="tooltip-container">
                <font-awesome-icon :icon="['fas', 'layer-group']" />
                <span v-if="!isCollapsed">Decks</span>
                <span class="tooltip" v-if="isCollapsed">Decks</span>
              </router-link>
            </li>
            <li>
              <router-link
                to="/rodadas-preco/resultados"
                class="tooltip-container"
              >
                <font-awesome-icon :icon="['fas', 'chart-line']" />
                <span v-if="!isCollapsed">Resultados</span>
                <span class="tooltip" v-if="isCollapsed">Resultados</span>
              </router-link>
            </li>
          </ul>
        </li>
        <li>
          <router-link to="/map" class="tooltip-container">
            <font-awesome-icon :icon="['fas', 'map']" />
            <span v-if="!isCollapsed">Map</span>
            <span class="tooltip" v-if="isCollapsed">Map</span>
          </router-link>
        </li>
        <li>
          <router-link to="/about" class="tooltip-container">
            <font-awesome-icon :icon="['fas', 'user']" />
            <span v-if="!isCollapsed">About</span>
            <span class="tooltip" v-if="isCollapsed">About</span>
          </router-link>
        </li>
        <li>
          <router-link to="/contact" class="tooltip-container">
            <font-awesome-icon :icon="['fas', 'envelope']" />
            <span v-if="!isCollapsed">Contact</span>
            <span class="tooltip" v-if="isCollapsed">Contact</span>
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
      isSubmenuOpen: false, // Controla a abertura do submenu
    };
  },
  methods: {
    toggleSidebar() {
      this.isCollapsed = !this.isCollapsed;
      localStorage.setItem("isCollapsed", this.isCollapsed); // Salva o estado no localStorage
    },
    toggleSubmenu() {
      this.isSubmenuOpen = !this.isSubmenuOpen;
    },
  },
  mounted() {
    const savedState = localStorage.getItem("isCollapsed");
    if (savedState !== null) {
      this.isCollapsed = JSON.parse(savedState);
    }
  },
};
</script>

<style scoped>
/* Sidebar básica */
/* Sidebar básica */
.sidebar {
  width: 250px;
  height: calc(100vh - 60px); /* Desconta a altura do header */
  background-color: #2c3e50;
  color: white;
  display: flex;
  flex-direction: column;
  align-items: center; /* Centraliza horizontalmente */
  transition: width 0.3s ease;
  position: relative;
  overflow: visible; /* Permite exibir tooltips */
}

/* Sidebar comprimida */
.sidebar.collapsed {
  width: 80px;
}

/* Botão de alternância */
.toggle-btn {
  position: absolute;
  top: 10px;
  right: -15px; /* Valor negativo para sair do sidebar */
  background: #1abc9c;
  border: none;
  border-radius: 50%;
  color: white;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
  transition: transform 0.3s ease, right 0.3s ease;
  z-index: 10000; /* Maior que o sidebar, garantindo que apareça acima de tudo */
}

.sidebar.collapsed .toggle-btn {
  right: -10px;
}

.toggle-btn:hover {
  transform: scale(1.1);
}

/* Navegação */
.sidebar nav ul {
  list-style: none;
  padding: 0;
  margin: 0;
  width: 100%;
}

.sidebar nav ul li {
  margin: 15px 0;
  text-align: left;
}

/* Links do menu */
.sidebar nav ul li a,
.sidebar nav ul li .menu-item {
  color: white;
  text-decoration: none;
  display: flex;
  align-items: center;
  padding: 10px 15px;
  gap: 10px;
  transition: all 0.3s ease;
  border-radius: 5px;
  position: relative;
  cursor: pointer;
}

/* Destaque ao passar o mouse (itens principais e subitens) */
.sidebar nav ul li a:hover,
.sidebar nav ul li .menu-item:hover,
.submenu li a:hover {
  background-color: rgba(255, 255, 255, 0.1);
  box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.2);
  color: white;
}

/* Tooltip */
.tooltip-container {
  position: relative;
}

.tooltip {
  position: absolute;
  left: 100%;
  top: 50%;
  transform: translateY(-50%);
  background-color: rgba(0, 0, 0, 0.8);
  color: white;
  padding: 5px 10px;
  border-radius: 5px;
  z-index: 1000; /* Garante que o tooltip fique acima */
  opacity: 0;
  visibility: hidden;
  white-space: nowrap;
  transition: opacity 0.3s ease, transform 0.3s ease;
}

.tooltip-container:hover .tooltip {
  opacity: 1;
  visibility: visible;
  transform: translateY(-50%) translateX(10px);
}

/* Submenu inicial - oculto */
.submenu {
  list-style: none;
  padding-left: 20px;
  margin: 0;
  max-height: 0; /* Altura inicial é 0 */
  overflow: hidden; /* Esconde o conteúdo */
  transition: max-height 0.5s ease; /* Transição suave para altura */
}

/* Submenu expandido */
.submenu.expanded {
  max-height: 500px; /* Altura suficiente para acomodar os itens */
}

/* Links dos subitens */
.submenu li {
  margin: 10px 0;
}

.submenu li a {
  color: #a0a0a0; /* Cor padrão dos subitens */
  text-decoration: none;
  display: flex;
  align-items: center;
  padding: 10px 15px;
  gap: 10px;
  transition: all 0.3s ease;
  border-radius: 5px;
}

/* Destaque ao passar o mouse nos subitens */
.submenu li a:hover {
  color: white; /* Texto fica branco ao passar o mouse */
  background-color: rgba(255, 255, 255, 0.1);
  box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.2);
}
</style>
