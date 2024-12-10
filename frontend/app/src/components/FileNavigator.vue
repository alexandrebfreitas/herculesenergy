<!-- src/components/FileNavigator.vue -->
<template>
  <div class="file-navigator">
    <div class="navigator-header">
      <button v-if="!isRoot" @click="navigateUp">⬆️ Voltar</button>
      <span class="current-path">{{ displayPath }}</span>
    </div>
    <ul>
      <li
        v-for="file in files"
        :key="file.relativePath"
        @click="handleItemClick(file)"
        :class="{ folder: file.isFolder }"
      >
        <span v-if="file.isFolder">📁 {{ file.name }}</span>
        <span v-else>📄 {{ file.name }}</span>
      </li>
    </ul>
  </div>
</template>
  
  <script>
import { ref, watch, onMounted } from "vue";
import { listFiles } from "@/services/FileRequestsMethods.js";

export default {
  name: "FileNavigator",
  props: {
    initialPath: {
      type: String,
      default: "estudos",
    },
  },
  emits: ["fileSelected"], // Define o evento emitido
  setup(props, { emit }) {
    const rootPath = ref("estudos");
    const currentPath = ref(props.initialPath);
    const files = ref([]);
    const isRoot = ref(currentPath.value === rootPath.value);

    /**
     * Formata o caminho para exibição (opcional).
     */
    const displayPath = ref(currentPath.value);

    /**
     * Função para buscar arquivos e pastas no diretório atual.
     */
    const fetchFiles = async () => {
      try {
        console.log(`Buscando arquivos no caminho: "${currentPath.value}"`);
        const response = await listFiles(currentPath.value);
        if (Array.isArray(response.data)) {
          files.value = response.data;
          console.log(
            `Arquivos listados em "${currentPath.value}":`,
            files.value
          );
        } else {
          throw new Error("Formato de resposta inesperado da API.");
        }
      } catch (error) {
        console.error(
          `Erro ao listar arquivos em "${currentPath.value}":`,
          error
        );
        alert(
          `Erro ao listar arquivos em "${currentPath.value}". Verifique o console para mais detalhes.`
        );
      }
    };

    /**
     * Manipula o clique em um item (arquivo ou pasta).
     */
    const handleItemClick = (file) => {
      console.log("Item clicado:", file);
      if (file.isFolder) {
        navigateTo(file.name); // Navega para a subpasta
      } else {
        // Construir o caminho completo do arquivo
        const fullPath = `${currentPath.value}/${file.name}`;
        console.log(`Emitindo evento 'fileSelected' com path: "${fullPath}"`);
        emit("fileSelected", fullPath); // Emite o evento com o caminho completo do arquivo
      }
    };

    /**
     * Navega para um subdiretório, concatenando o nome da pasta ao caminho atual.
     */
    const navigateTo = (folderName) => {
      if (folderName && typeof folderName === "string") {
        const newPath = `${currentPath.value}/${folderName}`;
        console.log(`Navegando para a pasta: "${newPath}"`);
        currentPath.value = newPath;
      } else {
        console.warn(
          "Nome de pasta inválido ao tentar navegar para uma subpasta:",
          folderName
        );
      }
    };

    /**
     * Navega para o diretório pai.
     */
    const navigateUp = () => {
      console.log("Navegando para o diretório pai.");
      const pathSegments = currentPath.value.split("/").filter((seg) => seg);
      pathSegments.pop();
      const newPath = pathSegments.length
        ? pathSegments.join("/")
        : rootPath.value;
      console.log(`Novo caminho após navegar para cima: "${newPath}"`);
      currentPath.value = newPath;
    };

    // Observa mudanças no currentPath para buscar novos arquivos
    watch(currentPath, () => {
      isRoot.value = currentPath.value === rootPath.value;
      displayPath.value = currentPath.value; // Atualiza a exibição do caminho
      fetchFiles();
    });

    // Busca arquivos ao montar o componente
    onMounted(() => {
      fetchFiles();
    });

    return {
      currentPath,
      files,
      handleItemClick,
      navigateTo,
      navigateUp,
      isRoot,
      displayPath,
    };
  },
};
</script>
  
  <style scoped>
.file-navigator {
  border: 1px solid #ddd;
  padding: 10px;
  border-radius: 4px;
  max-height: 400px;
  overflow-y: auto;
}

.navigator-header {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.navigator-header button {
  margin-right: 10px;
  padding: 5px 10px;
  cursor: pointer;
}

.current-path {
  font-weight: bold;
}

ul {
  list-style-type: none;
  padding: 0;
}

li {
  padding: 8px;
  cursor: pointer;
  border-radius: 4px;
}

li:hover {
  background-color: #f0f0f0;
}

li.folder {
  font-weight: bold;
}
</style>
  