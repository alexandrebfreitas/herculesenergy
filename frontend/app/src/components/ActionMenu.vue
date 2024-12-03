<template>
  <div class="menu">
    <ul>
      <!-- Editar disponível apenas para arquivos -->
      <li @click="edit" v-if="!isFolder">Editar</li>

      <!-- Renomear disponível para ambos arquivos e pastas -->
      <li @click="rename">Renomear</li>

      <!-- Mover disponível para ambos arquivos e pastas -->
      <li @click="move">Mover</li>

      <!-- Download disponível para ambos arquivos e pastas -->
      <li @click="download">Download</li>

      <!-- Descompactar disponível apenas para arquivos ZIP -->
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
     * Determina se o item é um arquivo ZIP.
     */
    isZip() {
      return this.fileItem.extension === 'zip';
    },
    /**
     * Determina se o item é uma pasta.
     */
    isFolder() {
      return this.fileItem.isFolder;
    },
    /**
     * Obtém o caminho atual da pasta.
     */
    currentPath() {
      const path = this.fileItem.path;
      return path.substring(0, path.lastIndexOf('/')) || '';
    },
  },
  methods: {
    /**
     * Faz o download do arquivo ou pasta.
     */
    async download() {
      try {
        await downloadFile(this.fileItem.path);
        alert('Download iniciado.');
      } catch (error) {
        console.error('Erro ao fazer download:', error);
        alert('Erro ao fazer download.');
      }
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
          })
          .catch((error) => {
            console.error('Erro ao descompactar arquivo:', error);
            alert('Erro ao descompactar arquivo.');
          });
    },
    /**
     * Renomeia o item (arquivo ou pasta).
     */
    rename() {
      const newName = prompt('Digite o novo nome:', this.fileItem.name);
      if (!newName || newName.trim() === '' || newName === this.fileItem.name) {
        alert('Nome inválido ou não alterado.');
        return;
      }
      const oldPath = this.fileItem.path;
      const newPath = `${this.currentPath}/${newName}`.replace(/\/+/g, '/');
      renameFile(oldPath, newPath)
          .then(() => {
            this.$emit('refresh');
          })
          .catch((error) => {
            console.error('Erro ao renomear item:', error);
            alert('Erro ao renomear item.');
          });
    },
    /**
     * Exclui o item após confirmação do usuário.
     */
    deleteItem() {
      const confirmDelete = confirm(`Tem certeza que deseja excluir "${this.fileItem.name}"?`);
      if (!confirmDelete) return;

      deleteItemRequest(this.fileItem.path)
          .then(() => {
            alert('Item excluído com sucesso.');
            this.$emit('refresh');
          })
          .catch((error) => {
            console.error('Erro ao excluir item:', error);
            alert('Erro ao excluir item.');
          });
    },
    /**
     * Abre o editor para arquivos.
     */
    edit() {
      if (this.isFolder) {
        alert('Edição não disponível para pastas.');
        return;
      }
      this.showEditor = true;
    },
    /**
     * Fecha o AceEditor.
     */
    closeEditor() {
      this.showEditor = false;
    },
    /**
     * Abre o modal para mover o item.
     */
    move() {
      this.showMoveModal = true;
    },
    /**
     * Fecha o MoveModal.
     */
    closeMoveModal() {
      this.showMoveModal = false;
    },
    /**
     * Lida com o evento de refresh.
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
