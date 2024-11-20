<template>
  <div class="file-mapper">
    <aside class="AppSidebar">
      <h3>Opções</h3>
      <button @click="fetchFiles">Carregar Arquivos</button>
    </aside>
    <main class="main-panel">
      <h2>Arquivos do Repositório</h2>
      <div class="file-list">
        <ul>
          <li
            v-for="file in files"
            :key="file.id"
            :class="{ selected: file.id === selectedFile?.id }"
            @click="selectFile(file)"
          >
            <span v-if="file.type === 'directory'">📁</span>
            <span v-else>📄</span>
            {{ file.name }}
          </li>
        </ul>
      </div>
      <div v-if="selectedFile" class="file-details">
        <h3>Detalhes do Arquivo</h3>
        <p><strong>Nome:</strong> {{ selectedFile.name }}</p>
        <p><strong>Tipo:</strong> {{ selectedFile.type }}</p>
        <p><strong>Tamanho:</strong> {{ selectedFile.size }} KB</p>
        <button @click="mapFile(selectedFile)">Mapear Arquivo</button>
      </div>
    </main>
  </div>
</template>

<script>
export default {
  data() {
    return {
      files: [], // Lista de arquivos e diretórios
      selectedFile: null, // Arquivo selecionado
    };
  },
  methods: {
    fetchFiles() {
      // Simulação de chamada para buscar arquivos
      this.files = [
        { id: 1, name: "src", type: "directory", size: null },
        { id: 2, name: "package.json", type: "file", size: 14 },
        { id: 3, name: "README.md", type: "file", size: 5 },
      ];
    },
    selectFile(file) {
      this.selectedFile = file;
    },
    mapFile(file) {
      console.log("Arquivo mapeado:", file);
      alert(`Arquivo "${file.name}" foi mapeado com sucesso!`);
    },
  },
};
</script>

<style scoped>
.file-mapper {
  display: flex;
  height: 100vh;
}

.sidebar {
  width: 250px;
  background-color: #f4f4f4;
  padding: 20px;
  box-shadow: 2px 0 5px rgba(0, 0, 0, 0.1);
}

.main-panel {
  flex: 1;
  padding: 20px;
}

.file-list ul {
  list-style: none;
  padding: 0;
}

.file-list li {
  cursor: pointer;
  padding: 10px;
  margin: 5px 0;
  border: 1px solid #ddd;
  border-radius: 4px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.file-list li:hover {
  background-color: #f0f0f0;
}

.file-list li.selected {
  background-color: #e0f7fa;
  border-color: #00796b;
}

.file-details {
  margin-top: 20px;
}

.file-details p {
  margin: 5px 0;
}

button {
  background-color: #00796b;
  color: white;
  border: none;
  padding: 10px 15px;
  border-radius: 4px;
  cursor: pointer;
}

button:hover {
  background-color: #005f4b;
}
</style>
