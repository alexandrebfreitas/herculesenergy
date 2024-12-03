<!-- src/components/FolderColumn.vue -->
<template>
  <div class="column">
    <h3 class="column-header">{{ folderName }}</h3>
    <Draggable
        v-model="items"
        :group="{ name: 'folders', pull: true, put: true }"
        @end="onDragEnd"
        tag="div"
        :data-folder="folderContext.folderPath"
        class="item-list"
    >
      <template #item="{ element }">
        <FileCard
            :key="element.path"
            :file-item="element"
            @refresh="fetchItems"
            @navigate="handleNavigate"
        ></FileCard>
      </template>
    </Draggable>
  </div>
</template>

<script>
import Draggable from 'vuedraggable';
import FileCard from '@/components/FileCard.vue';
import {
  listFiles,
  moveFile,
} from '@/services/FileRequestsMethods';
import createFileItem from '../models/fileItem';
import createFileOperation from '../models/fileOperation'; // Import necessário

export default {
  name: 'FolderColumn',
  components: { Draggable, FileCard },
  props: {
    folderContext: {
      type: Object,
      required: true,
    },
  },
  data() {
    return {
      items: [], // Fazer uma cópia local dos itens
    };
  },
  computed: {
    /**
     * Obtém o nome da pasta a partir do caminho.
     */
    folderName() {
      return this.folderContext.folderPath.split('/').pop() || '';
    },
  },
  methods: {
    /**
     * Busca os itens (arquivos e pastas) no caminho da pasta atual.
     */
    fetchItems() {
      listFiles(this.folderContext.folderPath)
          .then((response) => {
            this.items = response.data.map((item) => {
              const itemName = item.name;
              const isFolder = item.isFolder;
              const itemPath = `${this.folderContext.folderPath}/${item.relativePath || item.name}`.replace(/\/+/g, '/');
              const extension = isFolder ? '' : (item.extension || '').toLowerCase();
              const size = item.size;

              const fileItem = createFileItem({
                name: itemName,
                path: itemPath,
                isFolder: isFolder,
                extension: extension,
                size: size,
                // Outros valores padrão
              });
              return fileItem;
            });
          })
          .catch((error) => {
            console.error('Erro ao buscar itens:', error);
          });
    },
    /**
     * Lida com o fim da ação de arrastar e soltar.
     * @param {Object} event - Evento de drag end.
     */
    onDragEnd(event) {
      const movedItemName = event.item.dataset.name;
      const fromFolder = event.from.getAttribute('data-folder');
      const toFolder = event.to.getAttribute('data-folder');

      if (!fromFolder || !toFolder) {
        console.error('Caminhos inválidos.');
        return;
      }

      if (fromFolder === toFolder) {
        console.log('Nenhuma ação necessária, o item não foi movido.');
        return;
      }

      // Prepara o objeto de operação
      const fileOperation = createFileOperation({
        fileName: movedItemName,
        from: fromFolder,
        to: toFolder,
      });

      moveFile(fileOperation)
        .then(() => {
          this.fetchItems();
          this.$emit('refresh');
        })
        .catch((error) => {
          console.error('Erro ao mover item:', error);
          alert('Erro ao mover item.');
        });
    },
    edit() {
      this.showEditor = true;
    },
    /**
     * Emite um evento para navegar para um novo caminho.
     * @param {String} newPath - Novo caminho para navegar.
     */
    handleNavigate(newPath) {
      this.$emit('navigate', newPath);
    },
  },
  watch: {
    /**
     * Observa mudanças no caminho da pasta e refaz a busca de itens.
     */
    'folderContext.folderPath': {
      immediate: true,
      handler() {
        this.fetchItems();
      },
    },
  },
};
</script>

<style scoped>
.column {
  width: 250px;
  margin-right: 20px;
  background-color: #f5f5f5;
  border: 1px solid #ddd;
  border-radius: 5px;
}

.column-header {
  background-color: #e0e0e0;
  padding: 10px;
  margin: 0;
  text-align: center;
  font-size: 18px;
  font-weight: bold;
  border-bottom: 1px solid #ccc;
}

.item-list {
  padding: 10px;
  min-height: 200px;
}

.item-list > div {
  margin-bottom: 10px;
}
</style>
