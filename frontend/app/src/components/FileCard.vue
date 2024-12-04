<template>
  <div class="card" :data-name="fileItem.name">
    <div class="card-content">
      <span @click="handleClick" class="tooltip-container">
        <!-- Uso do componente FontAwesomeIcon -->
        <font-awesome-icon :icon="fileItem.isFolder ? 'folder-open' : 'file'" />

        <!-- Nome truncado com tooltip -->
        <span class="file-name" :title="tooltipContent">
          {{ fileItem.name.length > 15 ? fileItem.name.substring(0, 15) + '...' : fileItem.name }}
        </span>

        <span v-if="!fileItem.isFolder && fileItem.extension" class="file-extension">.{{ fileItem.extension }}</span>

        <!-- Tooltip personalizada -->
        <span class="tooltip">
          {{ tooltipContent }}
        </span>
      </span>
      <ActionMenu
          :fileItem="fileItem"
          @refresh="fetchItems"
          @navigate="navigate"
      ></ActionMenu>
      <!-- <span class="file-size">{{ formatFileSize(fileItem.size) }}</span> -->
    </div>

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
  computed: {
    /**
     * Conteúdo da tooltip com informações detalhadas do item.
     */
    tooltipContent() {
      const formattedSize = this.formatFileSize(this.fileItem.size);
      return `Nome: ${this.fileItem.name}\nTamanho: ${formattedSize}`;
    },
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
    toggleMenu() {
      this.showMenu = !this.showMenu;
    },
    handleClick() {
      if (this.fileItem.isFolder) {
        this.$emit('navigate', this.fileItem.path);
      }
    },
    fetchItems() {
      this.$emit('refresh');
    },
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
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
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

/* Tooltip */
.tooltip-container {
  position: relative;
  display: inline-block;
  cursor: pointer;
}

.tooltip-container:hover .tooltip {
  visibility: visible;
  opacity: 1;
}

.tooltip {
  visibility: hidden;
  width: 200px;
  background-color: #333;
  color: #fff;
  text-align: left;
  padding: 8px;
  border-radius: 4px;
  position: fixed; /* Permite renderizar fora do fluxo do layout */
  bottom: auto;
  top: auto; /* Ajustado dinamicamente no JavaScript */
  left: auto; /* Ajustado dinamicamente no JavaScript */
  opacity: 0;
  transition: opacity 0.3s;
  z-index: 1050; /* Valor alto para ficar acima da sidebar */
  white-space: pre-line; /* Exibe o texto em múltiplas linhas */
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}
</style>
