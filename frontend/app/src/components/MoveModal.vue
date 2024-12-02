<!-- src/components/MoveModal.vue -->
<template>
  <div class="move-modal">
    <div class="modal-header">
      <h3>Mover para...</h3>
      <button class="close-button" @click="$emit('close')">X</button>
    </div>
    <div class="modal-body">
      <!-- Barra de navegação para a hierarquia de pastas -->
      <nav>
        <span v-for="(part, index) in currentPathParts" :key="index">
          <span @click="navigateTo(index)" class="nav-part">
            {{ part || "Root" }}
          </span>
          <span v-if="index < currentPathParts.length - 1"> > </span>
        </span>
      </nav>

      <!-- Listagem de pastas -->
      <ul>
        <li v-for="folder in folders" :key="folder.relativePath" @click="navigateInto(folder)">
          📁 {{ folder.name || "Sem Nome" }}
        </li>
      </ul>
      <p v-if="folders.length === 0">Nenhuma pasta encontrada.</p>
    </div>
    <div class="modal-footer">
      <button @click="confirmMove">Mover Aqui</button>
    </div>
  </div>
</template>

<script>
import { listFiles, moveFile } from '@/services/FileRequestsMethods.js';

export default {
  name: 'MoveModal',
  props: {
    fileItem: {
      type: Object,
      required: true,
    },
  },
  data() {
    return {
      currentPath: "", // Caminho atual para navegação
      folders: [], // Lista de pastas no caminho atual
    };
  },
  computed: {
    currentPathParts() {
      return ["", ...(this.currentPath || "").split("/")].filter((part) => part !== "");
    },
  },
  mounted() {
    this.listFolders();
  },
  methods: {
    /**
     * Lista as pastas no caminho atual utilizando o serviço.
     */
    async listFolders() {
      try {
        const response = await listFiles(this.currentPath);
        if (response.data && Array.isArray(response.data)) {
          // Filtrar apenas as pastas
          this.folders = response.data.filter(item => item.isFolder);
        } else {
          console.error("Formato inválido de resposta do backend:", response.data);
          this.folders = [];
        }
      } catch (error) {
        console.error("Erro ao listar pastas:", error);
        this.folders = [];
      }
    },
    /**
     * Navega para a parte especificada do caminho.
     */
    navigateTo(index) {
      if (index === 0) {
        this.currentPath = "";
      } else {
        this.currentPath = this.currentPathParts.slice(1, index + 1).join("/");
      }
      this.listFolders(); // Atualiza a lista de pastas
    },
    /**
     * Navega para dentro de uma pasta.
     */
    navigateInto(folder) {
      if (!folder.name) {
        console.error("Pasta sem nome:", folder);
        return;
      }
      this.currentPath = this.currentPath
        ? `${this.currentPath}/${folder.name}`
        : folder.name;
      this.listFolders(); // Atualiza a lista de pastas no novo caminho
    },
    /**
     * Confirma o movimento para o caminho atual utilizando o serviço.
     */
    async confirmMove() {
      const destinationPath = this.currentPath || "/";

      // Extrair o caminho "from" a partir do fileItem
      const sourcePath = this.fileItem.path;
      const fileName = this.fileItem.name;

      try {
        await moveFile({
          fileName: fileName,
          from: sourcePath.substring(0, sourcePath.lastIndexOf('/')),
          to: destinationPath,
        });
        alert(`Item movido para ${destinationPath === "/" ? "Root" : destinationPath}`);
        this.$emit("refresh"); // Atualiza a lista de arquivos/pastas na interface principal
        this.$emit("close"); // Fecha o modal
      } catch (error) {
        console.error("Erro ao mover item:", error);
        alert("Erro ao mover item. Verifique o console para mais detalhes.");
      }
    },
  },
};
</script>

<style scoped>
.move-modal {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 400px;
  background: white;
  border: 1px solid #ccc;
  border-radius: 8px;
  z-index: 1000;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  padding: 10px;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 18px;
  font-weight: bold;
}

.close-button {
  background: none;
  border: none;
  font-size: 18px;
  cursor: pointer;
}

.modal-footer {
  margin-top: 10px;
  text-align: center;
}

.modal-body {
  margin-top: 10px;
  max-height: 300px;
  overflow-y: auto;
}

nav {
  margin-bottom: 10px;
}

.nav-part {
  cursor: pointer;
  text-decoration: underline;
  color: #007bff;
}

.nav-part:hover {
  color: #0056b3;
}

ul {
  list-style: none;
  padding: 0;
}

li {
  padding: 5px;
  cursor: pointer;
  display: flex;
  align-items: center;
}

li:hover {
  background: #e0e0e0;
}
</style>
