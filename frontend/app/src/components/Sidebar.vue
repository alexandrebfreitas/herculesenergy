<template>
  <aside class="sidebar">
    <nav>
      <ul>
        <li>
          <router-link to="/">
            <font-awesome-icon :icon="['fas', 'home']" />
            Home
          </router-link>
        </li>
        <li>
          <router-link to="/about">
            <font-awesome-icon :icon="['fas', 'user']" />
            About
          </router-link>
        </li>
        <li>
          <router-link to="/contact">
            <font-awesome-icon :icon="['fas', 'envelope']" />
            Contact
          </router-link>
        </li>
        <li>
          <router-link to="/file-mapping">
            <font-awesome-icon :icon="['fas', 'folder-open']" />
            Files
          </router-link>
        </li>
      </ul>
    </nav>
  </aside>
</template>

<script>
export default {
  name: "AppSidebar",
};
</script>

<style scoped>
.sidebar {
  width: 250px;
  background-color: #2c3e50;
  padding: 20px;
  color: white;
}
.sidebar ul {
  list-style: none;
  padding: 0;
}
.sidebar li {
  margin: 10px 0;
}
.sidebar li a {
  color: white;
  text-decoration: none;
  display: flex;
  align-items: center;
  gap: 10px;
}
.sidebar li a:hover {
  text-decoration: underline;
}
</style>
