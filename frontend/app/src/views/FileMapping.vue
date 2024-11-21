<template>
  <div @click="hideContextMenu">
    <h1>File Upload and Download</h1>

    <!-- Navigation Bar and File List -->
    <div>
      <!-- Botões Undo, Redo e Upload ao lado da barra de navegação -->
      <div class="navigation-bar">
        <div class="navigation-buttons">
          <button @click="undo" :disabled="undoStack.length === 0">↩️</button>
          <button @click="redo" :disabled="redoStack.length === 0">↪️</button>
          <button @click="triggerFileInput">📤</button>
          <input type="file" ref="fileInput" style="display: none;" @change="uploadFile" />
        </div>

        <!-- Barra de navegação para mostrar o caminho atual -->
        <nav v-if="currentPathParts.length">
          <span v-for="(part, index) in currentPathParts" :key="index">
            <span @click="navigateDirectly(index)" style="cursor: pointer; text-decoration: underline;">
              {{ part || "Root" }}
            </span>
            <span v-if="index < currentPathParts.length - 1"> > </span>
          </span>
        </nav>
      </div>

      <!-- Lista de Arquivos e Pastas -->
      <div v-if="files.length > 0">
        <ul>
          <transition-group name="file" tag="ul">
            <li v-for="file in files" :key="file" class="file-item">
              <span
                  @click="navigateTo(file)"
                  @contextmenu.prevent="showContextMenu($event, file)"
                  style="cursor: pointer; text-decoration: underline;"
              >
                <!-- Exibir ícone para pasta ou arquivo -->
                <span v-if="file.endsWith('/')">📁</span>
                <span v-else>📄</span>
                {{ file }}
              </span>
            </li>
          </transition-group>
        </ul>
      </div>
      <div v-else>
        <p>No files or folders found in the current directory.</p>
      </div>
    </div>

    <!-- Menu Contextual Customizado -->
    <div v-if="contextMenuVisible" :style="contextMenuStyle" class="context-menu">
      <button @click="confirmDownload">Download</button>
    </div>

    <div v-if="uploadResponse" class="upload-response">
      <p>{{ uploadResponse }}</p>
    </div>
  </div>
</template>

<script>
export default {
  name: "FileMapping",
  data() {
    return {
      fileName: "",
      uploadResponse: "",
      files: [],
      currentPath: "", // Caminho atual para navegação
      contextMenuVisible: false,
      contextMenuStyle: {
        top: "0px",
        left: "0px",
      },
      fileToDownload: "", // Armazena o arquivo a ser baixado
      undoStack: [], // Pilha de caminhos para voltar
      redoStack: [], // Pilha de caminhos para avançar
    };
  },
  computed: {
    currentPathParts() {
      // Adicionar "Root" à navegação para indicar a pasta raiz
      return ["", ...this.currentPath.split("/")].filter((part) => part !== "");
    },
  },
  mounted() {
    // Listar arquivos automaticamente ao montar o componente
    this.listFiles();
  },
  methods: {
    triggerFileInput() {
      // Disparar a seleção de arquivo
      this.$refs.fileInput.click();
    },
    uploadFile(event) {
      const file = event.target.files[0];
      if (!file) return;

      const formData = new FormData();
      formData.append("file", file);

      // Adicionar o caminho atual ao endpoint de upload
      let uploadUrl = `/api/file/upload`;
      const uploadPath = this.currentPath || ""; // Se currentPath for vazio, significa a raiz
      formData.append("path", uploadPath);

      fetch(uploadUrl, {
        method: "POST",
        body: formData,
      })
          .then((response) => response.text())
          .then((data) => {
            this.uploadResponse = data;
            this.listFiles(); // Atualizar a lista de arquivos após o upload bem-sucedido
          })
          .catch((error) => {
            this.uploadResponse = "Error: " + error.message;
          });
    },
    downloadFile(fileName) {
      // Construir o caminho completo do arquivo para download
      const completePath = this.currentPath ? `${this.currentPath}/${fileName}` : fileName;

      // Atualizar a URL para utilizar o prefixo /api
      const downloadUrl = `/api/file/download?path=${encodeURIComponent(completePath)}`;

      fetch(downloadUrl)
          .then((response) => {
            if (response.ok) {
              return response.blob();
            } else {
              throw new Error("File not found");
            }
          })
          .then((blob) => {
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement("a");
            a.href = url;
            a.download = fileName.endsWith('/') ? `${fileName.slice(0, -1)}.zip` : fileName;
            document.body.appendChild(a);
            a.click();
            a.remove();
          })
          .catch((error) => {
            alert("Error: " + error.message);
          });
    },
    listFiles() {
      // Atualizar a URL para utilizar o prefixo /api e o caminho atual
      const listUrl = `/api/file/list?path=${this.currentPath}`;

      fetch(listUrl)
          .then((response) => {
            if (!response.ok) {
              throw new Error(`Error fetching file list: ${response.statusText}`);
            }
            return response.json();
          })
          .then((data) => {
            this.files = data;
          })
          .catch((error) => {
            this.files = []; // Resetar os arquivos se houver erro
            alert("Error fetching file list: " + error.message);
          });
    },
    navigateTo(file) {
      // Se o item for uma pasta (termina com '/'), navegue para dentro dela
      if (file.endsWith("/")) {
        this.undoStack.push(this.currentPath);
        this.currentPath = this.currentPath ? `${this.currentPath}/${file.slice(0, -1)}` : file.slice(0, -1);
        this.redoStack = []; // Limpar a pilha de redo ao navegar para uma nova pasta
        this.listFiles(); // Lista os arquivos na nova pasta
      } else {
        // Se for um arquivo, define o nome do arquivo para possível download
        this.fileName = file;
      }
    },
    navigateDirectly(index) {
      // Navegar diretamente para uma parte específica da barra de navegação
      this.undoStack.push(this.currentPath);
      if (index === 0) {
        // Se o índice for 0, significa que queremos ir para a raiz
        this.currentPath = "";
      } else {
        this.currentPath = this.currentPathParts.slice(1, index + 1).join("/");
      }
      this.redoStack = []; // Limpar a pilha de redo ao navegar diretamente
      this.listFiles(); // Lista os arquivos na pasta selecionada
    },
    undo() {
      if (this.undoStack.length > 0) {
        this.redoStack.push(this.currentPath);
        this.currentPath = this.undoStack.pop();
        this.listFiles(); // Atualizar a lista de arquivos
      }
    },
    redo() {
      if (this.redoStack.length > 0) {
        this.undoStack.push(this.currentPath);
        this.currentPath = this.redoStack.pop();
        this.listFiles(); // Atualizar a lista de arquivos
      }
    },
    showContextMenu(event, file) {
      // Mostrar o menu de contexto no local do clique
      this.contextMenuVisible = true;
      this.contextMenuStyle = {
        top: `${event.clientY}px`,
        left: `${event.clientX}px`,
      };
      this.fileToDownload = file; // Armazenar o arquivo que o usuário deseja baixar
    },
    hideContextMenu() {
      // Esconder o menu de contexto
      this.contextMenuVisible = false;
    },
    confirmDownload() {
      // Confirmar o download do arquivo ou pasta selecionado
      this.downloadFile(this.fileToDownload);
      this.hideContextMenu();
    },
  },
};
</script>

<style scoped>
/* Estilo da animação para novos arquivos */
.file-enter-active {
  animation: fadeIn 0.6s ease-out;
}
.file-leave-active {
  animation: fadeOut 0.6s ease-in;
}
@keyframes fadeIn {
  0% {
    opacity: 0;
    transform: translateY(-10px);
  }
  100% {
    opacity: 1;
    transform: translateY(0);
  }
}
@keyframes fadeOut {
  0% {
    opacity: 1;
    transform: translateY(0);
  }
  100% {
    opacity: 0;
    transform: translateY(-10px);
  }
}

/* Estilo do menu contextual */
.context-menu {
  position: fixed;
  background-color: #fff;
  border: 1px solid #ccc;
  padding: 10px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
  z-index: 1000;
}

/* Estilo dos botões de navegação e da barra de navegação */
.navigation-bar {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}
.navigation-buttons button {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 1em; /* Tamanho do texto */
  padding: 5px;
}
.navigation-buttons button:disabled {
  color: #ccc;
  cursor: not-allowed;
}

/* Estilo da resposta de upload */
.upload-response {
  margin-top: 10px;
  color: #008000; /* Verde para indicar uma mensagem positiva */
}
</style>
