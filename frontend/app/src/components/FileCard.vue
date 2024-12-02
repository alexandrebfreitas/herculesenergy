<!-- src/components/FileCard.vue -->
<template>
  <div class="card" :data-name="fileItem.name">
    <div class="card-content">
      <span @click="handleClick">
        <i :class="fileItem.isFolder ? 'folder-icon' : 'file-icon'"></i>
        <span class="file-name">{{ fileItem.name }}</span> <!-- Classe correta para seleção -->
        <span v-if="!fileItem.isFolder && fileItem.extension" class="file-extension">.{{ fileItem.extension }}</span>
      </span>
      <span class="file-size">{{ formatFileSize(fileItem.size) }}</span>
      <button @click.stop="toggleMenu">⋮</button> <!-- @click.stop para evitar propagação -->
    </div>
    <ActionMenu
        v-if="showMenu"
        :file-item="fileItem"
        @close="showMenu = false"
        @refresh="fetchItems"
        @navigate="navigate"
    ></ActionMenu>
  </div>
</template>

<script>
import ActionMenu from '@/components/ActionMenu.vue';

export default {
  name: 'FileCard',
  components: { ActionMenu },
  props: {
    fileItem: {
      type: Object,
      required: true,
    },
  },
  data() {
    return {
      showMenu: false,
    };
  },
  methods: {
    /**
     * Formata o tamanho do arquivo para uma representação legível.
     * @param {Number} size - Tamanho do arquivo em bytes.
     * @returns {String} - Tamanho formatado.
     */
    formatFileSize(size) {
      if (size < 1024) return `${size} bytes`;
      else if (size < 1048576) return `${(size / 1024).toFixed(1)} KB`;
      else if (size < 1073741824) return `${(size / 1048576).toFixed(1)} MB`;
      else return `${(size / 1073741824).toFixed(1)} GB`;
    },
    /**
     * Alterna a exibição do menu de ações.
     */
    toggleMenu() {
      this.showMenu = !this.showMenu;
    },
    /**
     * Lida com o clique no nome do arquivo/pasta.
     */
    handleClick() {
      if (this.fileItem.isFolder) {
        this.$emit('navigate', this.fileItem.path);
      } else {
        // Se for um arquivo, você pode implementar uma ação adicional, como visualizar o arquivo
      }
    },
    /**
     * Recarrega os itens após uma ação de refresh.
     */
    fetchItems() {
      this.$emit('refresh');
    },
    /**
     * Lida com a navegação para um novo caminho.
     * @param {String} newPath - Novo caminho para navegar.
     */
    navigate(newPath) {
      this.$emit('navigate', newPath);
    },
  },
};
</script>

<style scoped>
.card {
  padding: 10px;
  background-color: white;
  border: 1px solid #ddd;
  border-radius: 4px;
  position: relative;
}

.card-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.card-content span {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.file-name {
  margin-left: 5px;
}

.file-extension {
  margin-left: 5px;
  color: #888;
}

.file-size {
  font-size: 12px;
  color: #666;
  margin-right: 10px;
}

button {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 16px;
}
.file-name {
  margin-left: 5px;
  flex: 1; /* Permite que o elemento ocupe o espaço restante */
  overflow: hidden; /* Esconde o texto que ultrapassa o espaço */
  text-overflow: ellipsis; /* Adiciona reticências ao final do texto */
  white-space: nowrap; /* Impede que o texto quebre em múltiplas linhas */
}

.file-extension {
  margin-left: 5px;
  color: #888;
  flex-shrink: 0; /* Evita que a extensão seja comprimida */
}

.card-content span {
  display: flex;
  align-items: center;
  flex-wrap: nowrap; /* Impede que os itens se dividam em várias linhas */
  cursor: pointer;
  width: 100%; /* Garante que os itens internos respeitem o limite do card */
}
</style>
