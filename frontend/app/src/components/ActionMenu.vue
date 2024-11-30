<!-- src/components/ActionMenu.vue -->
<template>
  <div class="menu">
    <ul>
      <!-- Editar apenas para arquivos, não para pastas -->
      <li @click="edit" v-if="!isFolder">Editar</li>

      <!-- Renomear disponível para ambos arquivos e pastas -->
      <li @click="rename">Renomear</li>

      <!-- Mover disponível para ambos arquivos e pastas -->
      <li @click="move">Mover</li>

      <!-- Download apenas para arquivos, não para pastas -->
      <li @click="download" v-if="!isFolder">Download</li>

      <!-- Descompactar apenas para arquivos ZIP e não para pastas -->
      <li v-if="isZip && !isFolder" @click="unzip">Descompactar</li>

      <!-- Excluir disponível para ambos arquivos e pastas -->
      <li @click="deleteItem">Excluir</li>
    </ul>

    <!-- Componentes MoveModal e AceEditor -->
    <MoveModal
        v-if="showMoveModal"
        @close="closeMoveModal"
        @refresh="handleRefresh"
        :file-item="fileItem"
    ></MoveModal>

    <AceEditor
        v-if="showEditor"
        @close="closeEditor"
        @refresh="handleRefresh"
        :item="fileItem.name"
        :current-path="currentPath"
    ></AceEditor>
  </div>
</template>

<script>
import MoveModal from '@/components/MoveModal.vue';
import AceEditor from '@/components/AceEditor.vue';
import {
  renameFile,
  downloadFile,
  unzipFile,
  deleteItem as deleteItemRequest,
} from '@/services/FileRequestsMethods';

export default {
  name: 'ActionMenu',
  components: {
    MoveModal,
    AceEditor,
  },
  props: {
    fileItem: {
      type: Object,
      required: true,
    },
  },
  data() {
    return {
      showMoveModal: false,
      showEditor: false,
    };
  },
  computed: {
    /**
     * Determina se o arquivo é um ZIP baseado na extensão.
     */
    isZip() {
      return this.fileItem.extension === 'zip';
    },
    /**
     * Determina se o item é uma pasta.
     * Prioriza a propriedade 'isFolder' se disponível, caso contrário, utiliza 'folder'.
     */
    isFolder() {
      return this.fileItem.isFolder !== undefined ? this.fileItem.isFolder : this.fileItem.folder;
    },
    /**
     * Obtém o caminho atual da pasta, removendo o nome do arquivo do caminho completo.
     */
    currentPath() {
      const path = this.fileItem.path;
      const lastSlashIndex = path.lastIndexOf('/');
      if (lastSlashIndex === -1) {
        return ''; // Arquivo na raiz
      }
      return path.substring(0, lastSlashIndex);
    },
  },
  methods: {
    /**
     * Abre o editor para editar o conteúdo do arquivo.
     */
    edit() {
      this.closeMoveModal();
      this.showEditor = true;
    },
    /**
     * Fecha o AceEditor.
     */
    closeEditor() {
      this.showEditor = false;
    },
    /**
     * Abre o modal para mover o arquivo ou pasta.
     */
    move() {
      this.closeEditor();
      this.showMoveModal = true;
    },
    /**
     * Fecha o MoveModal.
     */
    closeMoveModal() {
      this.showMoveModal = false;
    },
    /**
     * Renomeia o arquivo ou pasta.
     */
    rename() {
      const newName = prompt('Digite o novo nome:', this.fileItem.name);
      if (newName && newName.trim() !== '' && newName !== this.fileItem.name) {
        const oldPath = this.fileItem.path;
        const directory = oldPath.substring(0, oldPath.lastIndexOf('/'));
        const newPath = `${directory}/${newName}`.replace(/\/+/g, '/');

        // Faz a requisição para renomear o arquivo/pasta
        renameFile(oldPath, newPath)
            .then(() => {
              this.$emit('refresh');
              this.$emit('close');
            })
            .catch((error) => {
              console.error('Erro ao renomear item:', error);
              const errorMessage =
                  error.response?.data?.message || error.response?.data || 'Erro desconhecido.';
              alert(`Erro ao renomear item: ${errorMessage}`);
            });
      } else {
        alert('Nome inválido ou não alterado.');
      }
    },
    /**
     * Faz o download do arquivo.
     */
    download() {
      if (this.isFolder) {
        alert('Download indisponível para pastas.');
        return;
      }
      downloadFile(this.fileItem.path);
    },
    /**
     * Descompacta o arquivo ZIP.
     */
    unzip() {
      if (!this.isZip || this.isFolder) {
        alert('Descompactação disponível apenas para arquivos ZIP.');
        return;
      }
      unzipFile(this.fileItem.path)
          .then(() => {
            alert('Arquivo descompactado com sucesso.');
            this.$emit('refresh');
            this.$emit('close');
          })
          .catch((error) => {
            console.error('Erro ao descompactar arquivo:', error);
            const errorMessage = error.response?.data || 'Erro desconhecido.';
            alert(`Erro ao descompactar arquivo: ${errorMessage}`);
          });
    },
    /**
     * Exclui o arquivo ou pasta após confirmação do usuário.
     */
    deleteItem() {
      const confirmDelete = confirm(`Tem certeza que deseja excluir "${this.fileItem.name}"?`);
      if (!confirmDelete) {
        return;
      }
      deleteItemRequest(this.fileItem.path)
          .then(() => {
            alert('Item excluído com sucesso.');
            this.$emit('refresh');
            this.$emit('close');
          })
          .catch((error) => {
            console.error('Erro ao excluir item:', error);
            const errorMessage = error.response?.data || 'Erro desconhecido.';
            alert(`Erro ao excluir item: ${errorMessage}`);
          });
    },
    /**
     * Método para lidar com o evento de refresh.
     */
    handleRefresh() {
      this.$emit('refresh');
    },
  },
};
</script>

<style scoped>
.menu {
  position: absolute;
  top: 100%;
  right: 0;
  background: white;
  border: 1px solid #ccc;
  border-radius: 4px;
  z-index: 100;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.menu ul {
  list-style: none;
  margin: 0;
  padding: 0;
}

.menu li {
  padding: 8px 12px;
  cursor: pointer;
  white-space: nowrap;
}

.menu li:hover {
  background-color: #f0f0f0;
}
</style>
