<template>
  <div class="compare-modal-overlay">
    <div class="compare-modal">
      <div class="modal-header">
        <h3>Comparar Arquivos</h3>
        <button class="close-button" @click="closeModal">X</button>
      </div>
      <div class="modal-body">
        <div class="file-selector">
          <h4>Arquivo 1</h4>
          <FileNavigator
            @fileSelected="setFile1"
            initialPath="estudos"
            :allowFolders="false"
          />
          <div v-if="file1Path" class="selected-file">
            Selecionado: {{ file1Path }}
          </div>
        </div>
        <div class="file-selector">
          <h4>Arquivo 2</h4>
          <FileNavigator
            @fileSelected="setFile2"
            initialPath="estudos"
            :allowFolders="false"
          />
          <div v-if="file2Path" class="selected-file">
            Selecionado: {{ file2Path }}
          </div>
        </div>
      </div>
      <div class="modal-footer">
        <button @click="compareFiles" :disabled="!file1Path || !file2Path">
          Comparar
        </button>
      </div>
      <div v-if="loading" class="loading-spinner">
        <p>Carregando...</p>
      </div>
      <div
        v-if="comparisonReady"
        class="comparison-container"
        ref="diffContainer"
      >
        <div>
          <button @click="saveFileContent(file1Path, file1Content)">
            Salvar Arquivo 1
          </button>
          <button @click="saveFileContent(file2Path, file2Content)">
            Salvar Arquivo 2
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, watch, onBeforeUnmount } from "vue";
import FileNavigator from "./FileNavigator.vue";
import { getFileContent, saveFile } from "@/services/FileRequestsMethods.js";
import * as monaco from "monaco-editor";

export default {
  name: "CompareModal",
  components: {
    FileNavigator,
  },
  emits: ["close"],
  setup(props, { emit }) {
    const file1Path = ref(null);
    const file2Path = ref(null);
    const file1Content = ref("");
    const file2Content = ref("");
    const comparisonReady = ref(false);
    const loading = ref(false);
    const diffContainer = ref(null);
    let diffEditor = null;

    const closeModal = () => {
      emit("close");
    };

    const setFile1 = async (filePath) => {
      file1Path.value = filePath;
      await loadFileContent(filePath, "file1Content");
      checkComparisonReady();
    };

    const setFile2 = async (filePath) => {
      file2Path.value = filePath;
      await loadFileContent(filePath, "file2Content");
      checkComparisonReady();
    };

    const loadFileContent = async (filePath, target) => {
      try {
        loading.value = true;
        const response = await getFileContent(filePath);
        if (response.status === 200 && response.data) {
          if (target === "file1Content") {
            file1Content.value = response.data;
          } else {
            file2Content.value = response.data;
          }
        }
      } catch (error) {
        console.error(`Erro ao carregar o arquivo "${filePath}":`, error);
      } finally {
        loading.value = false;
      }
    };

    const checkComparisonReady = () => {
      if (file1Content.value && file2Content.value) {
        comparisonReady.value = true;
        initializeDiffEditor();
      }
    };

    const initializeDiffEditor = () => {
      if (diffEditor) {
        diffEditor.dispose();
      }
      if (diffContainer.value) {
        diffEditor = monaco.editor.createDiffEditor(diffContainer.value, {
          automaticLayout: true,
          readOnly: true,
        });

        const originalModel = monaco.editor.createModel(
          file1Content.value,
          "text/plain"
        );
        const modifiedModel = monaco.editor.createModel(
          file2Content.value,
          "text/plain"
        );

        diffEditor.setModel({
          original: originalModel,
          modified: modifiedModel,
        });
      }
    };

    watch(
      () => [file1Content.value, file2Content.value],
      () => {
        if (diffEditor) {
          const originalModel = monaco.editor.createModel(
            file1Content.value,
            "javascript"
          );
          const modifiedModel = monaco.editor.createModel(
            file2Content.value,
            "javascript"
          );
          diffEditor.setModel({
            original: originalModel,
            modified: modifiedModel,
          });
        }
      }
    );

    onBeforeUnmount(() => {
      if (diffEditor) {
        diffEditor.dispose();
      }
    });

    const compareFiles = () => {
      if (file1Content.value && file2Content.value) {
        comparisonReady.value = true;
        initializeDiffEditor();
      }
    };

    const saveFileContent = async (filePath, fileContent) => {
      try {
        if (!filePath || !fileContent) {
          alert("Caminho ou conteúdo do arquivo não definidos.");
          return;
        }
        const message = await saveFile(filePath, fileContent);
        alert(message);
      } catch (error) {
        alert(
          "Erro ao salvar o arquivo. Verifique o console para mais detalhes."
        );
      }
    };

    return {
      file1Path,
      file2Path,
      file1Content,
      file2Content,
      comparisonReady,
      loading,
      compareFiles,
      setFile1,
      setFile2,
      closeModal,
      diffContainer,
      saveFileContent,
    };
  },
};
</script>

<style scoped>
/* Estilos iguais aos fornecidos anteriormente */
.compare-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.compare-modal {
  background: white;
  width: 90%;
  max-width: 1200px;
  border-radius: 8px;
  padding: 20px;
  position: relative;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.close-button {
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
}

.modal-body {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
  gap: 20px;
}

.file-selector {
  width: 45%;
}

.selected-file {
  margin-top: 10px;
  font-style: italic;
  color: #555;
}

.modal-footer {
  text-align: center;
  margin-top: 20px;
}

.modal-footer button {
  padding: 10px 20px;
  background-color: #28a745;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.modal-footer button:disabled {
  background-color: #6c757d;
  cursor: not-allowed;
}

.comparison-container {
  margin-top: 30px;
  height: 500px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.loading-spinner {
  text-align: center;
  padding: 20px;
  font-size: 18px;
  color: #555;
}
</style>
