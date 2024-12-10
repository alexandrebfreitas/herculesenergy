<!-- src/components/MonacoDiffEditor.vue -->
<template>
  <div>
    <div
      v-if="viewMode === 'side-by-side'"
      ref="diffEditorContainer"
      class="monaco-diff-editor"
    ></div>
    <div
      v-else
      ref="unifiedEditorContainer"
      class="monaco-unified-editor"
    ></div>
  </div>
</template>
  
<script>
import * as monaco from "monaco-editor";
import DiffMatchPatch from "diff-match-patch";

export default {
  name: "MonacoDiffEditor",
  props: {
    original: {
      type: String,
      required: true,
    },
    modified: {
      type: String,
      required: true,
    },
    language: {
      type: String,
      default: "plaintext",
    },
    theme: {
      type: String,
      default: "vs-dark",
    },
    viewMode: {
      type: String,
      default: "side-by-side",
    },
  },
  watch: {
    original(newOriginal) {
      if (this.viewMode === "side-by-side" && this.originalModel) {
        this.originalModel.setValue(newOriginal);
      }
      if (this.viewMode === "unified" && this.unifiedEditor) {
        this.unifiedEditor.setValue(newOriginal);
        this.addDiffMarkers();
      }
    },
    modified(newModified) {
      if (this.viewMode === "side-by-side" && this.modifiedModel) {
        this.modifiedModel.setValue(newModified);
      }
      if (this.viewMode === "unified" && this.unifiedEditor) {
        this.unifiedEditor.setValue(newModified);
        this.addDiffMarkers();
      }
    },
    language(newLanguage) {
      this.updateLanguage(newLanguage);
    },
    theme(newTheme) {
      monaco.editor.setTheme(newTheme);
    },
    viewMode(newViewMode) {
      console.log(
        `MonacoDiffEditor: Modo de visualização alterado para: ${newViewMode}`
      );
      this.updateViewMode(newViewMode);
    },
  },
  mounted() {
    if (this.viewMode === "side-by-side") {
      this.initDiffEditor();
    } else {
      this.initUnifiedEditor();
    }
  },
  beforeUnmount() {
    if (this.diffEditor) {
      this.diffEditor.dispose();
      this.diffEditor = null;
    }
    if (this.originalModel) {
      this.originalModel.dispose();
      this.originalModel = null;
    }
    if (this.modifiedModel) {
      this.modifiedModel.dispose();
      this.modifiedModel = null;
    }
    if (this.unifiedEditor) {
      this.unifiedEditor.dispose();
      this.unifiedEditor = null;
    }
  },
  methods: {
    initDiffEditor() {
      try {
        this.originalModel = monaco.editor.createModel(
          this.original,
          this.language
        );
        this.modifiedModel = monaco.editor.createModel(
          this.modified,
          this.language
        );

        console.log("Original Model Content:", this.originalModel.getValue());
        console.log("Modified Model Content:", this.modifiedModel.getValue());

        this.diffEditor = monaco.editor.createDiffEditor(
          this.$refs.diffEditorContainer,
          {
            theme: this.theme,
            automaticLayout: true,
            readOnly: false,
          }
        );

        this.diffEditor.setModel({
          original: this.originalModel,
          modified: this.modifiedModel,
        });

        console.log("MonacoDiffEditor Inicializado em modo side-by-side");
      } catch (error) {
        console.error("Erro ao inicializar Diff Editor:", error);
      }
    },
    initUnifiedEditor() {
      // Utilizamos $nextTick para garantir que o DOM esteja atualizado
      this.$nextTick(() => {
        try {
          this.unifiedEditor = monaco.editor.create(
            this.$refs.unifiedEditorContainer,
            {
              value: this.modified,
              language: this.language,
              theme: this.theme,
              automaticLayout: true,
              readOnly: false,
            }
          );

          console.log("Unified Editor Content:", this.unifiedEditor.getValue());

          this.addDiffMarkers();

          console.log("MonacoDiffEditor Inicializado em modo unified");

          // Forçar layout após um pequeno delay para garantir que o container tenha tamanho
          setTimeout(() => {
            this.unifiedEditor.layout();
          }, 100);
        } catch (error) {
          console.error("Erro ao inicializar Unified Editor:", error);
        }
      });
    },
    updateLanguage(newLanguage) {
      if (this.viewMode === "side-by-side") {
        if (this.originalModel && this.modifiedModel) {
          monaco.editor.setModelLanguage(this.originalModel, newLanguage);
          monaco.editor.setModelLanguage(this.modifiedModel, newLanguage);
        }
      }
      if (this.viewMode === "unified") {
        if (this.unifiedEditor && this.unifiedEditor.getModel()) {
          monaco.editor.setModelLanguage(
            this.unifiedEditor.getModel(),
            newLanguage
          );
        }
      }
    },
    updateViewMode(newViewMode) {
      // Destrói os editores atuais
      if (this.diffEditor) {
        this.diffEditor.dispose();
        this.diffEditor = null;
      }
      if (this.originalModel) {
        this.originalModel.dispose();
        this.originalModel = null;
      }
      if (this.modifiedModel) {
        this.modifiedModel.dispose();
        this.modifiedModel = null;
      }
      if (this.unifiedEditor) {
        this.unifiedEditor.dispose();
        this.unifiedEditor = null;
      }

      // Utilizamos $nextTick para garantir que o DOM esteja atualizado antes de inicializar o novo editor
      this.$nextTick(() => {
        if (newViewMode === "side-by-side") {
          this.initDiffEditor();
        } else {
          this.initUnifiedEditor();
        }
      });
    },
    addDiffMarkers() {
      if (!this.unifiedEditor) return;

      const dmp = new DiffMatchPatch();
      const diffs = dmp.diff_main(this.original, this.modified);
      dmp.diff_cleanupSemantic(diffs);

      const markers = [];
      let lineNumber = 1;

      diffs.forEach(([operation, text]) => {
        const lines = text.split("\n").length - 1;
        if (operation === 1) {
          // Inserção
          for (let i = 0; i < lines; i++) {
            markers.push({
              startLineNumber: lineNumber + i,
              startColumn: 1,
              endLineNumber: lineNumber + i,
              endColumn: 1,
              message: "Inserção",
              severity: monaco.MarkerSeverity.Info,
            });
          }
        } else if (operation === -1) {
          // Deleção
          for (let i = 0; i < lines; i++) {
            markers.push({
              startLineNumber: lineNumber + i,
              startColumn: 1,
              endLineNumber: lineNumber + i,
              endColumn: 1,
              message: "Deleção",
              severity: monaco.MarkerSeverity.Warning,
            });
          }
        }
        lineNumber += lines;
      });

      monaco.editor.setModelMarkers(
        this.unifiedEditor.getModel(),
        "diff",
        markers
      );
    },
  },
};
</script>

<style scoped>
.monaco-diff-editor,
.monaco-unified-editor {
  z-index:99999;
  flex: 1;
  width: 100%;
  height: 100%;
  border: 1px solid #ddd;
  border-radius: 4px;
  overflow: hidden;
}

/* Container para Botões de Salvar e Alternância de Visão */
.action-buttons {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
}

/* Estilos para os Botões de Salvar */
.save-buttons {
  display: flex;
  gap: 10px;
}

.save-buttons button {
  padding: 8px 16px;
  border: none;
  background-color: #007bff;
  color: white;
  border-radius: 4px;
  cursor: pointer;
}

.save-buttons button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.save-buttons button:hover:not(:disabled) {
  background-color: #0056b3;
}

/* Estilos para o Botão de Alternância de Visão */
.toggle-view-container {
  /* Mantém o botão alinhado à direita */
}

.toggle-view-button {
  padding: 8px 16px;
  border: none;
  background-color: #28a745;
  color: white;
  border-radius: 4px;
  cursor: pointer;
}

.toggle-view-button:hover {
  background-color: #1e7e34;
}

/* Responsividade */
@media (max-width: 768px) {
  .monaco-diff-editor,
  .monaco-unified-editor {
    height: 300px; /* Ajuste conforme necessário */
  }

  .action-buttons {
    flex-direction: column;
    align-items: stretch;
  }

  .save-buttons {
    flex-direction: column;
    gap: 5px;
  }

  .toggle-view-button {
    width: 100%;
    margin-top: 10px;
  }
}
</style>
