<template>
  <div class="decks-container">
    <h1>Estudos</h1>
    <div class="boards">
      <!-- Cada coluna representa um estudo -->
      <div
          v-for="(folder, folderIndex) in folders"
          :key="folder.name"
          class="board"
          :data-folder-index="folderIndex"
      >
        <h2>{{ folder.name }}</h2>
        <draggable
            v-model="folder.items"
            group="folders"
            class="card-list"
            :item-key="'name'"
            @end="onDragEnd"
        >
          <template #item="{ element }">
            <div class="card-container">
              <span v-if="element.isFolder">📁</span>
              <span v-else>📄</span>
              {{ element.name }}
            </div>
          </template>
        </draggable>
      </div>
    </div>
    <!-- Botão flutuante para adicionar nova pasta -->
    <button class="add-folder-btn" @click="addFolder">+</button>
  </div>
</template>

<script>
import draggable from "vuedraggable";

export default {
  name: "AppDecks",
  components: { draggable },
  data() {
    return {
      basePath: "estudos", // Caminho base para a API
      folders: [], // Armazena os estudos e seus itens
    };
  },
  mounted() {
    this.loadFolders(); // Carrega os estudos ao montar o componente
  },
  methods: {
    // Carrega os estudos e organiza os itens em colunas
    loadFolders() {
      const apiUrl = `/api/file/list?path=${encodeURIComponent(this.basePath)}`;
      fetch(apiUrl)
          .then((response) => response.json())
          .then((data) => {
            this.folders = data
                .filter((folder) => folder.endsWith("/")) // Apenas pastas
                .map((folderName) => ({
                  name: folderName.replace(/\/$/, ""), // Remove o '/' do final
                  items: [], // Inicializa os itens vazios
                }));

            // Carrega os itens de cada coluna
            this.folders.forEach((folder) => {
              this.loadFolderItems(folder);
            });
          })
          .catch((error) => console.error("Error loading folders:", error));
    },
    // Carrega os itens de uma pasta (coluna)
    loadFolderItems(folder) {
      const folderPath = `${this.basePath}/${folder.name}`;
      const apiUrl = `/api/file/list?path=${encodeURIComponent(folderPath)}`;
      fetch(apiUrl)
          .then((response) => response.json())
          .then((items) => {
            folder.items = items.map((item) => ({
              name: item.replace(/\/$/, ""), // Remove o '/' se for pasta
              isFolder: item.endsWith("/"), // Define se é uma pasta
            }));
          })
          .catch((error) =>
              console.error(`Error loading items for ${folder.name}:`, error)
          );
    },
    onDragEnd(event) {
      if (!event.item || !event.from || !event.to) {
        console.error("Invalid drag-and-drop event data.");
        return;
      }

      const movedItem = event.item._underlying_vm_ || event.item; // Verifica o objeto correto
      const fileName = movedItem.name;

      if (!fileName) {
        console.error("File name is missing in moved item:", movedItem);
        return;
      }

      const fromFolderIndex = event.from.closest(".board").dataset.folderIndex;
      const toFolderIndex = event.to.closest(".board").dataset.folderIndex;

      const fromFolder = this.folders[fromFolderIndex];
      const toFolder = this.folders[toFolderIndex];

      if (!fromFolder || !toFolder) {
        console.error("Could not determine source or destination folder.");
        return;
      }

      const fromFolderPath = `${this.basePath}/${fromFolder.name}`;
      const toFolderPath = `${this.basePath}/${toFolder.name}`;

      // Define o payload com os 3 parâmetros
      const payload = {
        fileName: fileName,
        from: fromFolderPath,
        to: toFolderPath,
      };

      // Corrige a URL para incluir o prefixo `/api`
      const moveFileUrl = `/api/file/move`;

      // Envia a requisição ao back-end
      fetch(moveFileUrl, {
        method: "POST",
        headers: {
          "Content-Type": "application/json", // Indica que o payload é JSON
        },
        body: JSON.stringify(payload), // Converte o objeto para JSON
      })
          .then((response) => {
            if (!response.ok) {
              return response.text().then((error) => {
                console.error("Error moving file:", error);
              });
            }
          })
          .catch((error) => console.error("Error moving file:", error));
    },
    // Adiciona uma nova pasta ao estudo selecionado
    addFolder() {
      const folderName = prompt("Enter the name of the new folder:");
      if (!folderName) return;

      fetch(
          `/api/file/create-folder?folderName=${encodeURIComponent(
              folderName
          )}&path=${encodeURIComponent(this.basePath)}`,
          {
            method: "POST",
          }
      )
          .then((response) => {
            if (!response.ok) {
              return response.text().then((error) => {
                console.error(`Error creating folder: ${error}`);
              });
            }
            this.loadFolders();
          })
          .catch((error) => console.error("Error creating folder:", error));
    },
  },
};
</script>

<style scoped>
/* Container principal */
.decks-container {
  display: flex;
  flex-direction: column;
  padding: 20px;
  background-color: #f8f9fa; /* Fundo suave */
  position: relative; /* Para posicionar o botão flutuante */
}

/* Título principal */
.decks-container h1 {
  font-size: 2rem;
  font-weight: bold;
  margin-bottom: 20px;
  color: #333;
  text-align: center;
}

/* Container das colunas */
.boards {
  display: flex;
  gap: 20px;
  overflow-x: auto; /* Adiciona barra de rolagem horizontal */
  padding-bottom: 10px; /* Espaço para evitar sobreposição da barra */
}

/* Cada coluna */
.board {
  background-color: #ffffff;
  border: 2px dashed #dcdcdc; /* Destaque para área de drop */
  border-radius: 8px;
  box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1); /* Sombra leve */
  padding: 15px;
  width: 300px;
  flex-shrink: 0; /* Impede que as colunas sejam redimensionadas */
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  align-items: center;
  transition: border-color 0.2s ease;
}

/* Efeito visual quando o item é arrastado */
.board.dragging-over {
  border-color: #007bff; /* Cor de destaque */
  background-color: #e7f3ff; /* Fundo claro para destaque */
}

/* Cabeçalho da coluna */
.board h2 {
  background-color: #007bff; /* Cor de destaque */
  color: white; /* Texto branco */
  text-align: center;
  padding: 10px;
  margin: -15px -15px 10px -15px; /* Remove o espaçamento e preenche todo o topo */
  border-radius: 8px 8px 0 0; /* Arredondamento no topo */
  font-size: 1.2rem;
  font-weight: bold;
  width: 100%;
}

/* Lista de itens na coluna */
.card-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
  width: 100%;
}

/* Cada item */
.card-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px;
  background-color: #f9f9f9;
  border: 1px solid #ddd;
  border-radius: 4px;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

/* Hover para os itens */
.card-container:hover {
  transform: scale(1.02);
  box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);
}

/* Botão para adicionar novas pastas (flutuante) */
.add-folder-btn {
  position: absolute;
  top: 20px;
  right: 20px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 50%;
  width: 50px;
  height: 50px;
  font-size: 1.5rem;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.2);
  transition: background-color 0.2s ease;
}

/* Hover no botão flutuante */
.add-folder-btn:hover {
  background-color: #0056b3;
}
</style>
