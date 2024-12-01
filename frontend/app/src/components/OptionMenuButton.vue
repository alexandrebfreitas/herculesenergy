<template>
  <header class="main-header">
    <div class="container">
      <div class="logo">Hercules</div>
      <div class="menu">
        <button class="btn-menu" @click="toggleMenu">
          <font-awesome-icon icon="bars" />
        </button>
        <nav v-show="menuOpen" class="menu-dropdown">
          <ul>
            <li><router-link to="/about">About</router-link></li>
            <li><router-link to="/login">Login</router-link></li>
            <li><router-link to="/register">Register</router-link></li>
            <li><button @click="logout">Logout</button></li>
          </ul>
        </nav>
      </div>
    </div>
  </header>
</template>

<script>
export default {
  data() {
    return {
      menuOpen: false,
    };
  },
  methods: {
    toggleMenu() {
      this.menuOpen = !this.menuOpen;
    },
    logout() {
      localStorage.removeItem("authToken"); // Exemplo de logout
      this.$router.push("/login");
    },
  },
};
</script>

<style scoped>
.main-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 20px;
  background: #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.btn-menu {
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
}

.menu-dropdown {
  position: absolute;
  top: 50px;
  right: 20px;
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
  border-radius: 8px;
}

.menu-dropdown ul {
  list-style: none;
  margin: 0;
  padding: 10px;
}

.menu-dropdown li {
  padding: 10px;
  text-align: center;
}
</style>
