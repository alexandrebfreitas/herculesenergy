<!-- src/components/CompareModal.vue -->
<template>
  <div class="compare-modal-overlay">
    <div class="compare-modal">
      <div class="modal-header">
        <h3>Comparar Arquivos</h3>
        <button class="close-button" @click="$emit('close')">X</button>
      </div>
      <div class="modal-body">
        <div class="file-selector">
          <h4>Arquivo 1</h4>
          <FileNavigator @fileSelected="setFile1" />
        </div>
        <div class="file-selector">
          <h4>Arquivo 2</h4>
          <FileNavigator @fileSelected="setFile2" />
        </div>
      </div>
      <div class="modal-footer">
        <button @click="compareFiles" :disabled="!file1Path || !file2Path">
          Comparar
        </button>
      </div>
      <div v-if="comparisonReady" class="comparison-container">
        <MonacoDiffEditor
          :original="file1Content"
          :modified="file2Content"
          :language="language"
          theme="vs-light"
          :options="{ readOnly: true, automaticLayout: true }"
        />
      </div>
    </div>
  </div>
</template>

<script>
import FileNavigator from "./FileNavigator.vue";
import { MonacoDiffEditor } from "monaco-editor-vue3";
import { getFileContent } from "@/services/FileRequestsMethods.js";

export default {
  name: "CompareModal",
  components: { MonacoDiffEditor, FileNavigator },
  data() {
    return {
      file1Path: null,
      file2Path: null,
      file1Content: "",
      file2Content: "",
      comparisonReady: false,
      language: "plaintext",
    };
  },
  methods: {
    /**
     * Mapeia a extensão do arquivo para a linguagem do Monaco Editor.
     */
    getLanguage(filePath) {
      const extension = filePath.split(".").pop().toLowerCase();
      const languages = {
        js: "javascript",
        ts: "typescript",
        py: "python",
        java: "java",
        c: "c",
        cpp: "cpp",
        css: "css",
        html: "html",
        json: "json",
        // Adicione mais mapeamentos conforme necessário
      };
      return languages[extension] || "plaintext";
    },
    /**
     * Define o primeiro arquivo para comparação.
     */
    async setFile1(filePath) {
      this.file1Path = filePath;
      await this.loadFileContent(filePath, "file1Content");
      this.updateLanguage();
    },
    /**
     * Define o segundo arquivo para comparação.
     */
    async setFile2(filePath) {
      this.file2Path = filePath;
      await this.loadFileContent(filePath, "file2Content");
      this.updateLanguage();
    },
    /**
     * Carrega o conteúdo do arquivo selecionado.
     */
    async loadFileContent(filePath, target) {
      try {
        const response = await getFileContent(filePath);
        this[target] = response.data.content;
        if (this.file1Content && this.file2Content) {
          this.comparisonReady = true;
        }
      } catch (error) {
        console.error(`Erro ao carregar o arquivo ${filePath}:`, error);
        alert(
          `Erro ao carregar o arquivo ${filePath}. Verifique o console para mais detalhes.`
        );
      }
    },
    /**
     * Aciona a comparação dos arquivos.
     */
    compareFiles() {
      if (this.file1Content && this.file2Content) {
        this.comparisonReady = true;
      }
    },
    /**
     * Atualiza a linguagem com base na extensão do arquivo.
     */
    updateLanguage() {
      if (this.file1Path) {
        this.language = this.getLanguage(this.file1Path);
      } else if (this.file2Path) {
        this.language = this.getLanguage(this.file2Path);
      } else {
        this.language = "plaintext";
      }
    },
  },
};
</script>

<style scoped>
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
  max-width: 1000px;
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
}
</style>
