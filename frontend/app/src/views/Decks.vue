<!-- src/views/Decks.vue -->
<template>
  <div id="decks">
    <!-- Botão "Novo Estudo" -->
      <button class="btn btn-primary" @click="createFolder">Novo Estudo</button>

    <!-- Contêiner de rolagem horizontal -->
    <div class="scroll-container">
      <div class="columns-container">
        <FolderColumn
            v-for="(folderContext, index) in folders"
            :key="index"
            :folder-context="folderContext"
            @refresh="fetchFolders"
            @navigate="navigateTo"
        ></FolderColumn>
      </div>
    </div>
  </div>
</template>


<script>
import FolderColumn from '@/components/FolderColumn.vue';
import { listFiles, createFolder as createFolderRequest } from '@/services/FileRequestsMethods';
import createFolderContext from '../models/folderContext';

export default {
  name: 'AppDecks',
  components: { FolderColumn },
  data() {
    return {
      folders: [],
      currentPath: 'estudos', // Caminho base
    };
  },
  methods: {
    /**
     * Busca as pastas no caminho atual e atualiza o estado.
     */
    fetchFolders() {
      listFiles(this.currentPath)
          .then((response) => {
            // Atualizar 'folders' com objetos folderContext
            this.folders = response.data
                .filter((item) => item.isFolder) // Filtrar apenas pastas
                .map((item) => {
                  const folderPath = `${this.currentPath}/${item.relativePath || item.name}`.replace(/\/+/g, '/');

                  const folderContext = createFolderContext({
                    currentPath: this.currentPath,
                    folderPath: folderPath,
                    // Outros valores, se necessários
                  });

                  return folderContext;
                });
          })
          .catch((error) => {
            console.error('Erro ao buscar pastas:', error);
          });
    },
    /**
     * Cria uma nova pasta no caminho atual.
     */
    createFolder() {
      const folderName = prompt('Digite o nome da nova pasta:');
      if (!folderName || folderName.trim() === '') {
        alert('Nome da pasta inválido.');
        return;
      }

      createFolderRequest(folderName, this.currentPath)
          .then((response) => {
            alert(response.data);
            this.fetchFolders();
          })
          .catch((error) => {
            console.error('Erro ao criar pasta:', error);
            const errorMessage = error.response?.data?.message || error.response?.data || 'Erro desconhecido.';
            alert(`Erro ao criar pasta: ${errorMessage}`);
          });
    },
    /**
     * Navega para um novo caminho.
     * @param {String} newPath - Novo caminho para navegar.
     */
    navigateTo(newPath) {
      if (newPath.startsWith('estudos')) {
        this.currentPath = newPath;
        this.fetchFolders();
      } else {
        alert('Navegação inválida');
      }
    },
  },
  created() {
    this.fetchFolders();
  },
};
</script>

<style scoped>
.scroll-container {
  overflow-x: auto; /* Ativa rolagem horizontal */
  white-space: nowrap; /* Mantém os itens em linha */
  display: flex;
  gap: 10px; /* Espaçamento entre os itens */
}

.columns-container {
  display: flex;
  align-items: flex-start;
  gap: 10px; /* Espaçamento entre as colunas */
}

.create-folder-button-container {
  display: flex;
  align-items: center;
  margin-left: auto;
}

.create-folder-button-container button {
  padding: 10px 20px;
  font-size: 16px;
  cursor: pointer;
}
</style>
