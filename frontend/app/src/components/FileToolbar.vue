<!-- src/components/FileToolbar.vue -->
<template>
  <div class="file-toolbar">
    <!-- Outros botões existentes -->
    <button @click="openCompareModal">Comparar Arquivos</button>
  </div>
  <CompareModal v-if="isCompareModalOpen" @close="isCompareModalOpen = false" />
</template>
  
  <script>
import CompareModal from "./CompareModal.vue";

export default {
  name: "FileToolbar",
  components: { CompareModal },
  data() {
    return {
      isCompareModalOpen: false,
    };
  },
  methods: {
    openCompareModal() {
      this.isCompareModalOpen = true;
    },
  },
};
</script>
  
  <style scoped>
.file-toolbar {
  /* Estilos para a barra de ferramentas */
}
button {
  margin-left: 10px;
  padding: 8px 12px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
button:hover {
  background-color: #0056b3;
}
</style>
  