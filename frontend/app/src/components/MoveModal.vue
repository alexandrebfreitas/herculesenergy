<!-- src/components/MoveModal.vue -->
<template>
  <div class="modal-overlay">
    <div class="modal">
      <h3>Select Destination Folder</h3>
      <ul>
        <li
          v-for="(folder, index) in folders"
          :key="index"
          @click="selectFolder(folder)"
        >
          {{ folder }}
        </li>
      </ul>
      <button @click="$emit('close')">Cancel</button>
    </div>
  </div>
</template>

<script>
import { defineComponent } from 'vue';
import axios from 'axios';

export default defineComponent({
  name: 'MoveModal',
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
      folders: [],
    };
  },
  methods: {
    fetchFolders() {
      axios
        .get('/api/file/list')
        .then((response) => {
          this.folders = response.data.filter((folder) => folder.endsWith('/'));
        })
        .catch((error) => {
          console.error('Error fetching folders:', error);
        });
    },
    selectFolder(folder) {
      axios
        .post('/api/file/move', {
          fileName: this.item,
          from: this.currentPath,
          to: folder,
        })
        .then(() => {
          this.$emit('refresh');
          this.$emit('close');
        })
        .catch((error) => {
          console.error('Error moving item:', error);
        });
    },
  },
  created() {
    this.fetchFolders();
  },
});
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}

.modal {
  background: white;
  padding: 20px;
  width: 300px;
  max-height: 80vh;
  overflow-y: auto;
}

.modal h3 {
  margin-top: 0;
}

.modal ul {
  list-style: none;
  padding: 0;
}

.modal li {
  padding: 5px;
  cursor: pointer;
}

.modal li:hover {
  background: #f0f0f0;
}

.modal button {
  margin-top: 10px;
}
</style>
