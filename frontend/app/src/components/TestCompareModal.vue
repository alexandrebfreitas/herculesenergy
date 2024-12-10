<!-- src/components/TestCompareModal.vue -->
<template>
  <div class="compare-modal-overlay">
    <div class="compare-modal">
      <div class="modal-header">
        <h3>Teste Comparação de Arquivos</h3>
        <button class="close-button" @click="closeModal">X</button>
      </div>
      <div class="comparison-container" ref="diffContainer">
        <!-- O Monaco Diff Editor será inserido aqui -->
      </div>
    </div>
  </div>
</template>
  
  <script>
import { ref, onBeforeUnmount, onMounted } from "vue";
import * as monaco from "monaco-editor";

export default {
  name: "TestCompareModal",
  setup() {
    const diffContainer = ref(null);
    let diffEditor = null;

    const closeModal = () => {
      // Implemente a lógica de fechamento do modal conforme necessário
      console.log("Fechando modal de teste.");
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
          "console.log('Hello, World!');\nconst a = 10;",
          "javascript"
        );
        const modifiedModel = monaco.editor.createModel(
          "console.log('Hello, Vue!');\nconst a = 20;",
          "javascript"
        );

        diffEditor.setModel({
          original: originalModel,
          modified: modifiedModel,
        });
      }
    };

    onMounted(() => {
      initializeDiffEditor();
    });

    onBeforeUnmount(() => {
      if (diffEditor) {
        diffEditor.dispose();
      }
    });

    return {
      diffContainer,
      closeModal,
    };
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

.comparison-container {
  margin-top: 30px;
  height: 500px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.diff-container {
  width: 100%;
  height: 100%;
}
</style>
  