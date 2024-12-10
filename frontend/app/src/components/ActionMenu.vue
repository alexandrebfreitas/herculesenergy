<!-- src/components/ActionMenu.vue -->
<template>
  <div class="dropdown">
    <!-- Botão de Ações -->
    <button
        class="btn btn-secondary btn-sm "
        type="button"
        id="actionMenuButton"
        data-bs-toggle="dropdown"
        aria-expanded="false"
    >
      ⋮
    </button>

    <!-- Menu Dropdown do Bootstrap -->
    <ul class="dropdown-menu" aria-labelledby="actionMenuButton">
      <!-- Editar disponível apenas para arquivos -->
      <li v-if="!isFolder">
        <button class="dropdown-item" @click="edit">Editar</button>
      </li>

      <!-- Renomear disponível para ambos arquivos e pastas -->
      <li>
        <button class="dropdown-item" @click="rename">Renomear</button>
      </li>

      <!-- Mover disponível para ambos arquivos e pastas -->
      <li>
        <button class="dropdown-item" @click="move">Mover</button>
      </li>

      <!-- Download disponível para ambos arquivos e pastas -->
      <li>
        <button class="dropdown-item" @click="download">Download</button>
      </li>

      <!-- Descompactar disponível apenas para arquivos ZIP -->
      <li v-if="isZip && !isFolder">
        <button class="dropdown-item" @click="unzip">Descompactar</button>
      </li>

      <li><hr class="dropdown-divider"></li>

      <!-- Excluir disponível para ambos arquivos e pastas -->
      <li>
        <button class="dropdown-item text-danger" @click="deleteItem">Excluir</button>
      </li>
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
        this.$emit('refresh');

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
            this.closeMenu();
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
    /**
     * Fecha o menu dropdown
     */
  },
};
</script>

<style scoped>
.menu.dropdown .dropdown-menu {
  display: block; /* Sempre visível */
  position: static; /* Remove o posicionamento absoluto */
  float: none; /* Remove o float */
  margin-top: 0; /* Remove a margem superior */
  /* Opcional: Personalize a largura conforme necessário */
  min-width: 200px;
}

/* Mantém o efeito hover nos itens */
.dropdown-item:hover {
  background-color: #f8f9fa; /* Cor de fundo no hover */
  cursor: pointer;
}

/* Estiliza o separador */
.dropdown-divider {
  border-top: 1px solid #e9ecef;
  margin: 0.5rem 0;
}

/* Opcional: Ajustes adicionais para o menu */
.menu {
  
  /* Adicione estilos conforme necessário */
}
</style>
