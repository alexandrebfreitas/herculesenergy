<!-- src/components/FileDiff.vue -->
<template>
  <div class="diff-container" ref="diffContainer"></div>
</template>

<script>
import * as monaco from 'monaco-editor';

export default {
  name: 'FileDiff',
  props: {
    original: {
      type: String,
      required: true,
    },
    modified: {
      type: String,
      required: true,
    },
  },
  mounted() {
    this.createDiffEditor();
  },
  watch: {
    original(newVal) {
      if (this.diffEditor) {
        this.diffEditor.setModel({
          original: monaco.editor.createModel(newVal, 'javascript'),
          modified: this.diffEditor.getModel().modified,
        });
      }
    },
    modified(newVal) {
      if (this.diffEditor) {
        this.diffEditor.setModel({
          original: this.diffEditor.getModel().original,
          modified: monaco.editor.createModel(newVal, 'javascript'),
        });
      }
    },
  },
  beforeDestroy() {
    if (this.diffEditor) {
      this.diffEditor.dispose();
    }
  },
  methods: {
    createDiffEditor() {
      this.diffEditor = monaco.editor.createDiffEditor(this.$refs.diffContainer, {
        // Configurações opcionais
        readOnly: true,
        automaticLayout: true,
        renderSideBySide: true,
      });

      const originalModel = monaco.editor.createModel(this.original, 'javascript');
      const modifiedModel = monaco.editor.createModel(this.modified, 'javascript');

      this.diffEditor.setModel({
        original: originalModel,
        modified: modifiedModel,
      });
    },
  },
};
</script>

<style scoped>
.diff-container {
  width: 100%;
  height: 600px; /* Ajuste conforme necessário */
  border: 1px solid #ccc;
  border-radius: 4px;
}
</style>
