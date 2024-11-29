<!-- src/components/AceEditor.vue -->
<template>
  <div class="editor-container">
    <button @click="$emit('close')">Close</button>
    <Vue3AceEditor
      v-model="content"
      :lang="language"
      :theme="theme"
      style="height: 500px; width: 100%;"
    ></Vue3AceEditor>
    <button @click="save">Save</button>
  </div>
</template>

<script>
import { defineComponent } from 'vue';
import Vue3AceEditor from 'vue3-ace-editor';
import axios from 'axios';

// Import Ace Editor modes and themes
import 'ace-builds/src-noconflict/ace';
import 'ace-builds/src-noconflict/ext-language_tools';
import 'ace-builds/src-noconflict/mode-text';
import 'ace-builds/src-noconflict/theme-chrome';

export default defineComponent({
  name: 'AceEditor',
  components: { Vue3AceEditor },
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
  data() {
    return {
      content: '',
      language: 'text',
      theme: 'chrome',
    };
  },
  methods: {
    fetchContent() {
      axios
        .get('/api/file/download', {
          params: { path: `${this.currentPath}/${this.item}` },
          responseType: 'text',
        })
        .then((response) => {
          this.content = response.data;
          this.setEditorMode();
        })
        .catch((error) => {
          console.error('Error fetching file content:', error);
        });
    },
    save() {
      axios
        .post(
          '/api/file/save',
          {},
          {
            params: {
              path: `${this.currentPath}/${this.item}`,
              content: this.content,
            },
          }
        )
        .then(() => {
          alert('File saved successfully');
          this.$emit('refresh');
          this.$emit('close');
        })
        .catch((error) => {
          console.error('Error saving file:', error);
        });
    },
    setEditorMode() {
      const extension = this.item.split('.').pop();
      switch (extension) {
        case 'js':
          this.language = 'javascript';
          import('ace-builds/src-noconflict/mode-javascript');
          break;
        case 'html':
          this.language = 'html';
          import('ace-builds/src-noconflict/mode-html');
          break;
        case 'css':
          this.language = 'css';
          import('ace-builds/src-noconflict/mode-css');
          break;
        // Add more cases as needed
        default:
          this.language = 'text';
      }
    },
  },
  created() {
    this.fetchContent();
  },
});
</script>

<style scoped>
.editor-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.7);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.editor-container button {
  margin: 10px;
}
</style>
