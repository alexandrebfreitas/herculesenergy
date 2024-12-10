<template>
  <div id="decks">
    <!-- Linha com os botões -->
    <div class="row">
      <div class="col-auto">
        <button class="btn btn-primary mb-3" @click="createFolder">
          Novo Estudo
        </button>
      </div>
      <div class="col-auto">
        <!-- Botão para alternar entre visualização em lista e colunas -->
        <button class="btn btn-secondary mb-3" @click="toggleViewMode">
          Mudar para {{ viewMode === "columns" ? "Lista" : "Colunas" }}
        </button>
      </div>
      <!-- Botão para abrir o CompareModal -->
      <div class="col-auto">
        <button class="btn btn-info mb-3" @click="openCompareModal">
          Comparar Arquivos
        </button>
      </div>

      <!-- Se estiver em modo lista, mostra o seletor de tamanho da página -->
      <div class="col-auto" v-if="viewMode === 'list'">
        <label for="pageSizeSelect" class="me-2">Itens por página:</label>
        <select
          id="pageSizeSelect"
          v-model="selectedPageSize"
          @change="changePageSize"
        >
          <option value="12">12</option>
          <option value="24">24</option>
          <option value="36">36</option>
          <option value="all">Todos</option>
        </select>
      </div>
    </div>
    <!-- Inclusão do CompareModal -->
    <CompareModal
      v-if="isCompareModalOpen"
      @close="isCompareModalOpen = false"
    />

    <!-- Se estiver em viewMode 'columns', mostra como colunas com nome truncado -->
    <div v-if="viewMode === 'columns'" class="scroll-container base-layer">
      <div class="columns-container">
        <div
          class="column"
          v-for="(folderContext, index) in folders"
          :key="index"
        >
          <h3 class="column-header">
            <span class="title-wrapper">
              {{ truncateName(getFolderName(folderContext.folderPath)) }}
            </span>
          </h3>

          <Draggable
            v-model="folderContext.items"
            :group="{ name: 'folders', pull: true, put: true }"
            @end="onDragEnd"
            tag="div"
            :data-folder="folderContext.folderPath"
            class="item-list"
          >
            <template #item="{ element }">
              <div class="card" :data-name="element.name">
                <div class="card-content">
                  <span
                    @click="handleClickBaseLayer($event, element)"
                    class="tooltip-container"
                  >
                    <font-awesome-icon
                      :icon="element.isFolder ? 'folder-open' : 'file'"
                    />
                    <span class="file-name" :title="getTooltipContent(element)">
                      {{
                        element.name.length > 15
                          ? element.name.substring(0, 15) + "..."
                          : element.name
                      }}
                    </span>
                    <span
                      v-if="!element.isFolder && element.extension"
                      class="file-extension"
                    >
                      .{{ element.extension }}
                    </span>
                    <span class="tooltip">
                      {{ getTooltipContent(element) }}
                    </span>
                  </span>

                  <ActionMenu
                    :fileItem="element"
                    @refresh="refresh"
                    @navigate="navigate"
                  />
                </div>
              </div>
            </template>
          </Draggable>
        </div>
      </div>
    </div>

    <!-- Se estiver em viewMode 'list', mostra como lista SEM truncar o nome -->
    <div v-else class="list-view">
      <!-- Controles de Paginação: apenas se houver mais de uma página e pageSize != 'all' -->
      <div
        v-if="viewMode === 'list' && pageSize !== 'all' && totalPages > 1"
        class="pagination-controls"
      >
        <button @click="prevPage" :disabled="currentPage === 1">
          Anterior
        </button>
        <span>Página {{ currentPage }} de {{ totalPages }}</span>
        <button @click="nextPage" :disabled="currentPage === totalPages">
          Próxima
        </button>
      </div>

      <ul class="studies-list">
        <li
          v-for="(folderContext, index) in paginatedFolders"
          :key="index"
          @click="handleListClick($event, folderContext)"
          class="study-item"
        >
          {{ getFolderName(folderContext.folderPath) }}
        </li>
      </ul>
    </div>

    <!-- Camadas sobrepostas -->
    <div class="overlays-container">
      <div
        v-for="(overlay, oIndex) in overlays"
        :key="oIndex"
        class="overlay-layer"
        :style="{
          top: overlay.top + 50 + 'px',
          left: overlay.left + 50 + 'px',
        }"
      >
        <div class="overlay-column column">
          <h3 class="column-header">
            <span class="title-wrapper">
              {{
                truncateName(getFolderName(overlay.folderContext.folderPath))
              }}
            </span>
            <button v-if="oIndex >= 0" class="back-button" @click="goBack">
              Voltar
            </button>
          </h3>

          <Draggable
            v-model="overlay.items"
            :group="{ name: 'folders', pull: true, put: true }"
            @end="onDragEnd"
            tag="div"
            :data-folder="overlay.folderContext.folderPath"
            class="item-list"
          >
            <template #item="{ element }">
              <div class="card" :data-name="element.name">
                <div class="card-content">
                  <span
                    @click="handleClickOverlay($event, element)"
                    class="tooltip-container"
                  >
                    <font-awesome-icon
                      :icon="element.isFolder ? 'folder-open' : 'file'"
                    />
                    <span class="file-name" :title="getTooltipContent(element)">
                      {{
                        element.name.length > 15
                          ? element.name.substring(0, 15) + "..."
                          : element.name
                      }}
                    </span>
                    <span
                      v-if="!element.isFolder && element.extension"
                      class="file-extension"
                    >
                      .{{ element.extension }}
                    </span>
                    <span class="tooltip">
                      {{ getTooltipContent(element) }}
                    </span>
                  </span>
                  <ActionMenu
                    :fileItem="element"
                    @refresh="refreshOverlay(overlay)"
                    @navigate="navigateOverlay(overlay)"
                  />
                </div>
              </div>
            </template>
          </Draggable>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Draggable from "vuedraggable";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import ActionMenu from "@/components/ActionMenu.vue";
import {
  listFiles,
  createFolder as createFolderRequest,
  moveFile,
} from "@/services/FileRequestsMethods";
import createFolderContext from "@/models/folderContext";
import createFileItem from "@/models/fileItem";
import createFileOperation from "@/models/fileOperation";
import CompareModal from "@/components/CompareModal.vue"; // Importação do CompareModal
export default {
  name: "AppDecks",
  components: { Draggable, FontAwesomeIcon, ActionMenu, CompareModal },
  data() {
    return {
      folders: [],
      currentPath: "estudos", // Caminho base
      overlays: [],
      viewMode: "columns", // Alterna entre 'columns' e 'list'
      currentPage: 1,
      pageSize: 12, // Valor inicial, pode ser 12
      selectedPageSize: "12", // String para o select
      isCompareModalOpen: false, // Estado para controlar a exibição do CompareModal
    };
  },
  computed: {
    totalPages() {
      if (this.pageSize === "all") return 1;
      return Math.ceil(this.folders.length / this.pageSize);
    },
    paginatedFolders() {
      // Retorna os folders da página atual apenas se estivermos no modo lista
      if (this.viewMode !== "list") return this.folders;
      if (this.pageSize === "all") return this.folders; // Se 'all', retorna tudo
      const start = (this.currentPage - 1) * this.pageSize;
      const end = start + this.pageSize;
      return this.folders.slice(start, end);
    },
  },
  methods: {
    truncateName(name) {
      return name.length > 10 ? name.substring(0, 10) + "..." : name;
    },
    openCompareModal() {
      this.isCompareModalOpen = true;
    },
    closeCompareModal() {
      this.isCompareModalOpen = false;
    },
    fetchFolders() {
      listFiles(this.currentPath)
        .then((response) => {
          this.folders = response.data
            .filter((item) => item.isFolder)
            .map((item) => {
              const folderPath = `${this.currentPath}/${
                item.relativePath || item.name
              }`.replace(/\/+/g, "/");
              const folderContext = createFolderContext({
                currentPath: this.currentPath,
                folderPath: folderPath,
              });
              return folderContext;
            });
          this.folders.forEach((folderContext) => {
            this.fetchItems(folderContext);
          });
        })
        .catch((error) => {
          console.error("Erro ao buscar pastas:", error);
        });
    },
    createFolder() {
      const folderName = prompt("Digite o nome da nova pasta:");
      if (!folderName || folderName.trim() === "") {
        alert("Nome da pasta inválido.");
        return;
      }

      createFolderRequest(folderName, this.currentPath)
        .then((response) => {
          alert(response.data);
          this.fetchFolders();
        })
        .catch((error) => {
          console.error("Erro ao criar pasta:", error);
          const errorMessage =
            error.response?.data?.message ||
            error.response?.data ||
            "Erro desconhecido.";
          alert(`Erro ao criar pasta: ${errorMessage}`);
        });
    },
    navigate(newPath) {
      if (newPath.startsWith("estudos")) {
        this.currentPath = newPath;
        this.fetchFolders();
      } else {
        alert("Navegação inválida");
      }
    },
    fetchItems(folderContext) {
      listFiles(folderContext.folderPath)
        .then((response) => {
          folderContext.items = response.data.map((item) => {
            const itemPath = `${folderContext.folderPath}/${
              item.relativePath || item.name
            }`.replace(/\/+/g, "/");
            return createFileItem({
              name: item.name,
              path: itemPath,
              isFolder: item.isFolder,
              extension: item.isFolder
                ? ""
                : (item.extension || "").toLowerCase(),
              size: item.size,
            });
          });
        })
        .catch((error) => {
          console.error("Erro ao buscar itens:", error);
        });
    },
    onDragEnd(event) {
      const movedItemName = event.item.dataset.name;
      const fromFolder = event.from.getAttribute("data-folder");
      const toFolder = event.to.getAttribute("data-folder");

      if (!fromFolder || !toFolder) {
        console.error("Caminhos inválidos.");
        return;
      }

      if (fromFolder === toFolder) {
        console.log("Nenhuma ação necessária, o item não foi movido.");
        return;
      }

      const fileOperation = createFileOperation({
        fileName: movedItemName,
        from: fromFolder,
        to: toFolder,
      });

      moveFile(fileOperation)
        .then(() => {
          // Atualiza camadas base e overlays
          this.folders.forEach((f) => this.fetchItems(f));
          this.overlays.forEach((overlay) => {
            this.fetchOverlayItems(overlay);
          });
          this.fetchFolders();
        })
        .catch((error) => {
          console.error("Erro ao mover item:", error);
          alert("Erro ao mover item.");
        });
    },
    formatFileSize(size) {
      if (size < 1024) return `${size} bytes`;
      else if (size < 1048576) return `${(size / 1024).toFixed(1)} KB`;
      else if (size < 1073741824) return `${(size / 1048576).toFixed(1)} MB`;
      else return `${(size / 1073741824).toFixed(1)} GB`;
    },
    getFolderName(folderPath) {
      return folderPath.split("/").pop() || "";
    },
    getTooltipContent(element) {
      const formattedSize = this.formatFileSize(element.size);
      return `Nome: ${element.name}\nTamanho: ${formattedSize}`;
    },
    handleClickBaseLayer(event, element) {
      if (element.isFolder) {
        const rect = this.getColumnPosition(event);
        this.createOverlayForPath(element.path, rect);
      }
    },
    handleClickOverlay(event, element) {
      if (element.isFolder) {
        const rect = this.getColumnPosition(event);
        this.createOverlayForPath(element.path, rect);
      }
    },
    getColumnPosition(event) {
      let columnEl = event.currentTarget.closest(".column");
      if (!columnEl) columnEl = event.currentTarget;
      const rect = columnEl.getBoundingClientRect();
      const containerRect = this.$el.getBoundingClientRect();
      return {
        top: rect.top - containerRect.top,
        left: rect.left - containerRect.left,
      };
    },
    createOverlayForPath(folderPath, position) {
      const folderContext = createFolderContext({
        currentPath: folderPath.split("/").slice(0, -1).join("/"),
        folderPath: folderPath,
      });

      const newOverlay = {
        folderContext: folderContext,
        items: [],
        top: position.top || 0,
        left: position.left || 0,
      };

      this.fetchOverlayItems(newOverlay).then(() => {
        this.overlays.push(newOverlay);
      });
    },
    fetchOverlayItems(overlay) {
      return listFiles(overlay.folderContext.folderPath)
        .then((response) => {
          overlay.items = response.data.map((item) => {
            const itemPath = `${overlay.folderContext.folderPath}/${
              item.relativePath || item.name
            }`.replace(/\/+/g, "/");
            return createFileItem({
              name: item.name,
              path: itemPath,
              isFolder: item.isFolder,
              extension: item.isFolder
                ? ""
                : (item.extension || "").toLowerCase(),
              size: item.size,
            });
          });
        })
        .catch((error) => {
          console.error("Erro ao buscar itens da overlay:", error);
        });
    },
    refresh() {
      this.fetchFolders();
    },
    refreshOverlay(overlay) {
      return () => {
        this.fetchOverlayItems(overlay);
      };
    },
    navigateOverlay(overlay) {
      return (newPath) => {
        if (newPath.startsWith("estudos")) {
          overlay.folderContext.folderPath = newPath;
          this.fetchOverlayItems(overlay);
        } else {
          alert("Navegação inválida");
        }
      };
    },
    goBack() {
      if (this.overlays.length > 0) {
        this.overlays.pop();
      }
    },
    toggleViewMode() {
      this.viewMode = this.viewMode === "columns" ? "list" : "columns";
    },
    handleListClick(event, folderContext) {
      const rect = this.getListItemPosition(event);
      this.createOverlayForPath(folderContext.folderPath, rect);
    },
    getListItemPosition(event) {
      const liEl = event.currentTarget;
      const rect = liEl.getBoundingClientRect();
      const containerRect = this.$el.getBoundingClientRect();
      return {
        top: rect.top - containerRect.top,
        left: rect.left - containerRect.left,
      };
    },

    // Métodos de paginação
    prevPage() {
      if (this.currentPage > 1) {
        this.currentPage--;
      }
    },
    nextPage() {
      if (this.currentPage < this.totalPages) {
        this.currentPage++;
      }
    },
    changePageSize() {
      // Se o usuário selecionar "all", pageSize = 'all'
      // Caso contrário, converter para número
      if (this.selectedPageSize === "all") {
        this.pageSize = "all";
      } else {
        this.pageSize = parseInt(this.selectedPageSize, 10);
      }
      this.currentPage = 1; // Reinicia na primeira página
    },
  },
  created() {
    this.fetchFolders();
  },
};
</script>

<style scoped>
#decks {
  position: relative;
}

/* Estilos da visualização em colunas */
.scroll-container {
  overflow-x: auto;
  white-space: nowrap;
  display: flex;
  gap: 10px;
}

.base-layer {
  position: relative;
  z-index: 0;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.columns-container {
  display: flex;
  align-items: flex-start;
  gap: 10px;
}

.column {
  width: 250px;
  margin-right: 20px;
  background-color: #f5f5f5;
  border: 1px solid #ddd;
  border-radius: 5px;
  position: relative;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.column-header {
  position: relative;
  background-color: #e0e0e0;
  padding: 10px;
  margin: 0;
  text-align: center;
  font-size: 18px;
  font-weight: bold;
  border-bottom: 1px solid #ccc;
}

.column-header .title-wrapper {
  display: inline-block;
  max-width: calc(100% - 60px);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.item-list {
  padding: 10px;
  max-height: 400px;
  overflow-y: auto;
  box-sizing: border-box;
}

.item-list > div {
  margin-bottom: 10px;
}

.card {
  padding: 10px;
  background-color: white;
  border: 1px solid #ddd;
  border-radius: 4px;
  position: relative;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.card-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
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
  position: fixed;
  opacity: 0;
  transition: opacity 0.3s;
  z-index: 1050;
  white-space: pre-line;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

/* Container das overlays */
.overlays-container {
  position: absolute;
  top: 0;
  left: 0;
  pointer-events: none;
}

/* Overlay layer */
.overlay-layer {
  position: absolute;
  width: 250px;
  background: #fff;
  pointer-events: auto;
  border: 1px solid #ddd;
  border-radius: 5px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.overlay-column {
  width: 100%;
  margin: 0;
  box-sizing: border-box;
}

.back-button {
  position: absolute;
  top: 5px;
  right: 5px;
  font-size: 12px;
  background: #ccc;
  border: none;
  padding: 4px 6px;
  cursor: pointer;
  border-radius: 3px;
}

/* Estilos da visualização em lista */
.list-view {
  padding: 10px;
}

.studies-list {
  list-style-type: none;
  padding: 0;
  margin: 0;
}

.study-item {
  padding: 10px;
  background: #f5f5f5;
  margin-bottom: 5px;
  border: 1px solid #ddd;
  border-radius: 5px;
  cursor: pointer;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.study-item:hover {
  background: #e0e0e0;
}

/* Controles de paginação */
.pagination-controls {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}
</style>
