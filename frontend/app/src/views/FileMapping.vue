<template>
  <div @click="hideContextMenu($event)">
    <h1>File Upload, Download, and Edit</h1>

    <!-- Navigation Bar and File List -->
    <div>
      <h2>Navigation</h2>

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
      <select @change="handleContextMenuSelection" v-model="selectedAction">
        <option value="">Select action</option>
        <option value="download">Download</option>
        <option value="delete">Delete</option>
        <option v-if="isEditableFile(fileToDownload)" value="edit">Edit</option>
      </select>
    </div>

    <div v-if="uploadResponse" class="upload-response">
      <p>{{ uploadResponse }}</p>
    </div>

    <!-- Editor de Arquivos (Ace Editor) -->
    <div v-if="isEditing" class="editor-container">
      <div id="editor" ref="aceEditor" style="height: 400px;"></div>
      <button @click="saveEdit">Save</button>
      <button @click="cancelEdit">Cancel</button>
    </div>
  </div>
</template>

<script>
import ace from 'ace-builds/src-noconflict/ace';
import 'ace-builds/src-noconflict/mode-javascript';
import 'ace-builds/src-noconflict/theme-monokai';

// Atualizar a configuração do basePath do Ace Editor
ace.config.set('basePath', '/js/ace'); // Caminho que aponta para a pasta pública onde os workers estão localizados
ace.config.setModuleUrl('ace/mode/javascript_worker', '/js/ace/worker-javascript.js');

// Importar os arquivos de worker necessários
import 'ace-builds/src-noconflict/worker-javascript';

ace.config.set('basePath', '/node_modules/ace-builds/src-noconflict');

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
      fileToDelete: "", // Armazena o arquivo a ser deletado
      undoStack: [], // Pilha de caminhos para voltar
      redoStack: [], // Pilha de caminhos para avançar
      selectedAction: "", // Ação selecionada no dropdown
      isEditing: false, // Estado de edição de arquivos
      editorInstance: null, // Instância do Ace Editor
      fileContent: "", // Conteúdo do arquivo sendo editado
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
      this.fileToDelete = file; // Armazenar o arquivo que o usuário deseja deletar
    },
    hideContextMenu(event) {
      // Verificar se o clique foi fora do menu contextual
      if (!event || (this.contextMenuVisible && event.target.closest('.context-menu') === null)) {
        this.contextMenuVisible = false;
        this.selectedAction = ""; // Resetar a ação selecionada
      }
    },
    handleContextMenuSelection() {
      if (this.selectedAction === "download") {
        this.downloadFile(this.fileToDownload);
      } else if (this.selectedAction === "delete") {
        this.deleteFile(this.fileToDelete);
      } else if (this.selectedAction === "edit") {
        this.editFile(this.fileToDownload);
      }
      this.hideContextMenu();
    },
    isEditableFile(fileName) {
      return fileName.endsWith(".csv") || fileName.endsWith(".txt");
    },
    editFile(fileName) {
      // Construir o caminho completo do arquivo para edição
      const completePath = this.currentPath ? `${this.currentPath}/${fileName}` : fileName;
      const editUrl = `/api/file/download?path=${encodeURIComponent(completePath)}`;

      fetch(editUrl)
          .then((response) => response.text())
          .then((content) => {
            this.fileContent = content;
            this.isEditing = true;

            // Esperar até que o editor esteja disponível no DOM
            this.$nextTick(() => {
              if (!this.editorInstance) {
                this.editorInstance = ace.edit(this.$refs.aceEditor);
                this.editorInstance.session.setMode('ace/mode/javascript');
                this.editorInstance.setTheme('ace/theme/monokai');
                this.editorInstance.setValue(this.fileContent);
              } else {
                this.editorInstance.setValue(this.fileContent);
              }
            });
          })
          .catch((error) => {
            alert("Error: " + error.message);
          });
    },
    saveEdit() {
      const updatedContent = this.editorInstance.getValue();
      const formData = new FormData();
      formData.append("content", updatedContent);

      const saveUrl = `/api/file/save?path=${encodeURIComponent(this.currentPath + "/" + this.fileToDownload)}`;

      fetch(saveUrl, {
        method: "POST",
        body: formData,
      })
          .then((response) => {
            if (!response.ok) {
              throw new Error("Error saving file");
            }
            return response.text();
          })
          .then((message) => {
            alert(message);
            this.isEditing = false;
            this.listFiles(); // Atualizar lista de arquivos
          })
          .catch((error) => {
            alert("Error: " + error.message);
          });
    },
    cancelEdit() {
      // Cancelar a edição e esconder o editor
      if (this.editorInstance) {
        this.editorInstance.destroy();
        this.editorInstance = null;
      }
      this.isEditing = false; // Atualizar o estado para não estar mais editando
    },
    deleteFile(fileName) {
      // Construir o caminho completo do arquivo para deletar
      const completePath = this.currentPath ? `${this.currentPath}/${fileName}` : fileName;

      // Atualizar a URL para utilizar o prefixo /api
      const deleteUrl = `/api/file/delete?path=${encodeURIComponent(completePath)}`;

      fetch(deleteUrl, {
        method: "DELETE",
      })
          .then((response) => {
            if (!response.ok) {
              throw new Error(`Error deleting file: ${response.statusText}`);
            }
            return response.text();
          })
          .then((data) => {
            alert(data);
            this.listFiles(); // Atualizar a lista de arquivos após a exclusão bem-sucedida
          })
          .catch((error) => {
            alert("Error: " + error.message);
          });
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

/* Estilo do Editor de Arquivos */
.editor-container {
  margin-top: 20px;
}
</style>
