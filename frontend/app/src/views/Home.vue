<template>
  <div class="home-container">
    <div class="background-image"></div>
  </div>
</template>

<script>
export default {
  name: "AppHome",
};
</script>

<style scoped>
.home-container {
  width: 100%;
  height: 100vh; /* Tela cheia */
  display: flex;
  align-items: center;
  justify-content: center;
}

.background-image {
  background-image: url('@/assets/dashboard.jpg'); /* Caminho da imagem */
  background-size: cover; /* Preenche todo o espaço */
  background-position: center; /* Centraliza a imagem */
  width: 100%;
  height: 100%;
}
</style>