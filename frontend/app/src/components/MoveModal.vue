<!-- src/components/MoveModal.vue -->
<template>
  <Teleport to="body">
    <div class="move-modal">
      <div class="modal-header">
        <h2>Mover Arquivo/Pasta</h2>
        <button class="close-button" @click="closeModal">✖️ Fechar</button>
      </div>
      <div class="modal-body">
        <!-- Conteúdo do modal para mover arquivos/pastas -->
        <form @submit.prevent="handleMove">
          <label for="destination">Destino:</label>
          <input type="text" id="destination" v-model="destinationPath" required />
          <button type="submit" class="move-button">Mover</button>
        </form>
      </div>
    </div>
  </Teleport>
</template>

<script>
import axios from 'axios';

export default {
  name: 'MoveModal',
  props: {
    'file-item': {
      type: Object,
      required: true,
    },
  },
  emits: ['close', 'refresh'],
  data() {
    return {
      destinationPath: '',
    };
  },
  methods: {
    /**
     * Fecha o modal.
     */
    closeModal() {
      this.$emit('close');
    },
    /**
     * Lida com a movimentação do arquivo/pasta.
     */
    async handleMove() {
      try {
        await axios.post('/api/file/move', {
          source: this['file-item'].path,
          destination: this.destinationPath,
        });
        alert('Item movido com sucesso.');
        this.$emit('refresh');
        this.closeModal();
      } catch (error) {
        console.error('Erro ao mover item:', error);
        const errorMessage =
            error.response?.data?.message || error.response?.data || 'Erro desconhecido.';
        alert(`Erro ao mover item: ${errorMessage}`);
      }
    },
  },
};
</script>

<style scoped>
.move-modal {
  position: fixed;
  top: 20%;
  left: 20%;
  width: 60%;
  height: 60%;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.5);
  z-index: 1000; /* Z-index menor que o AceEditor */
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px;
  background-color: #f5f5f5;
  border-top-left-radius: 8px;
  border-top-right-radius: 8px;
}

.modal-header h2 {
  margin: 0;
  font-size: 18px;
}

.close-button {
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
}

.modal-body {
  padding: 20px;
}

.move-button {
  background-color: #007bff;
  border: none;
  color: #fff;
  padding: 8px 16px;
  cursor: pointer;
  border-radius: 4px;
}

.move-button:hover {
  background-color: #0056b3;
}
</style>
