<!-- src/components/AceEditor.vue -->
<template>
  <Teleport to="body">
    <div class="ace-container">
      <div class="editor-header">
        <h2>Editar {{ item }}</h2>
        <button class="close-button" @click="closeEditor">✖️ Fechar</button>
      </div>
      <div ref="editor" class="ace-editor"></div>
      <div class="editor-footer">
        <button class="save-button" @click="save">💾 Salvar</button>
      </div>
    </div>
  </Teleport>
</template>

<script>
import ace from 'ace-builds';
import 'ace-builds/src-noconflict/theme-monokai';
import 'ace-builds/src-noconflict/mode-javascript';
import 'ace-builds/src-noconflict/mode-html';
import 'ace-builds/src-noconflict/mode-css';
import axios from 'axios';

export default {
  name: 'AceEditor',
  props: {
    item: {
      type: String,
      required: true,
    },
    currentPath: {
      type: String,
      required: true,
    },
  },
  emits: ['close', 'refresh'],
  data() {
    return {
      editorInstance: null,
      isLoading: true,
      language: 'javascript',
    };
  },
  methods: {
    /**
     * Função para determinar a linguagem com base na extensão do arquivo.
     */
    setLanguage() {
      const extension = this.item.split('.').pop().toLowerCase();
      switch (extension) {
        case 'js':
        case 'jsx':
        case 'ts':
        case 'tsx':
          this.language = 'javascript';
          this.editorInstance.getSession().setMode('ace/mode/javascript');
          break;
        case 'html':
        case 'htm':
          this.language = 'html';
          this.editorInstance.getSession().setMode('ace/mode/html');
          break;
        case 'css':
        case 'scss':
          this.language = 'css';
          this.editorInstance.getSession().setMode('ace/mode/css');
          break;
          // Adicione mais casos conforme necessário
        default:
          this.language = 'text';
          this.editorInstance.getSession().setMode('ace/mode/text');
      }
    },
    /**
     * Busca o conteúdo do arquivo para edição.
     */
    async fetchContent() {
      try {
        const response = await axios.get('/api/file/download', {
          params: { path: `${this.currentPath}/${this.item}` },
          responseType: 'text',
        });
        this.editorInstance.setValue(response.data, -1); // -1 move o cursor para o início
        this.isLoading = false;
      } catch (error) {
        console.error('Erro ao buscar o conteúdo do arquivo:', error);
        alert('Erro ao buscar o conteúdo do arquivo.');
        this.isLoading = false;
        this.closeEditor();
      }
    },
    /**
     * Salva o conteúdo editado do arquivo.
     */
    async save() {
      try {
        const updatedContent = this.editorInstance.getValue();
        await axios.post(
            '/api/file/save',
            {},
            {
              params: {
                path: `${this.currentPath}/${this.item}`,
                content: updatedContent,
              },
            }
        );
        alert('Arquivo salvo com sucesso.');
        this.$emit('refresh');
        this.closeEditor();
      } catch (error) {
        console.error('Erro ao salvar o arquivo:', error);
        alert('Erro ao salvar o arquivo.');
      }
    },
    /**
     * Fecha o editor.
     */
    closeEditor() {
      this.$emit('close');
    },
  },
  mounted() {
    this.editorInstance = ace.edit(this.$refs.editor, {
      theme: 'ace/theme/monokai',
      mode: 'ace/mode/javascript', // Padrão, será ajustado pela função setLanguage
      maxLines: Infinity,
      minLines: 10,
      fontSize: '14px',
      tabSize: 4,
      showPrintMargin: false,
    });

    this.setLanguage();
    this.fetchContent();
  },
  beforeUnmount() {
    if (this.editorInstance) {
      this.editorInstance.destroy();
      this.editorInstance = null;
    }
  },
  watch: {
    item() {
      if (this.editorInstance) {
        this.setLanguage();
        this.fetchContent();
      }
    },
    currentPath() {
      this.fetchContent();
    },
  },
};
</script>

<style scoped>
.ace-container {
  position: fixed;
  top: 10%;
  left: 10%;
  width: 80%;
  height: 80%;
  background-color: #2d2d2d;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.5);
  display: flex;
  flex-direction: column;
  z-index: 1001; /* Z-index mais alto */
}

.editor-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px;
  background-color: #1e1e1e;
  border-top-left-radius: 8px;
  border-top-right-radius: 8px;
}

.editor-header h2 {
  color: #fff;
  margin: 0;
  font-size: 18px;
}

.close-button {
  background: none;
  border: none;
  color: #fff;
  font-size: 20px;
  cursor: pointer;
}

.close-button:hover {
  color: #ff5f56;
}

.editor-footer {
  padding: 10px;
  background-color: #1e1e1e;
  border-bottom-left-radius: 8px;
  border-bottom-right-radius: 8px;
  text-align: right;
}

.save-button {
  background-color: #28a745;
  border: none;
  color: #fff;
  padding: 8px 16px;
  cursor: pointer;
  border-radius: 4px;
  font-size: 14px;
}

.save-button:hover {
  background-color: #218838;
}

.ace-editor {
  flex: 1;
  width: 100%;
  height: 100%;
}
</style>
