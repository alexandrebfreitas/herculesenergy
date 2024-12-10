<!-- src/components/DiffViewer.vue -->
<template>
  <div class="diff-container" v-html="diffHtml"></div>
</template>
  
  <script>
import { ref, watch, onMounted } from "vue";
// Tente diferentes formas de importação conforme mencionado
import { Diff2Html } from "diff2html";
// import * as Diff2Html from "diff2html";
// import Diff2Html from "diff2html";
import "diff2html/bundles/css/diff2html.min.css";
import DiffMatchPatch from "diff-match-patch";

export default {
  name: "DiffViewer",
  props: {
    oldText: {
      type: String,
      required: true,
    },
    newText: {
      type: String,
      required: true,
    },
    language: {
      type: String,
      default: "javascript", // Ajuste conforme necessário
    },
    splitView: {
      type: Boolean,
      default: true, // true para side-by-side, false para inline
    },
  },
  setup(props) {
    const diffHtml = ref("");

    const generateDiff = () => {
      // Verifique se Diff2Html está definido
      if (!Diff2Html || !Diff2Html.getPrettyHtml) {
        console.error(
          "Diff2Html não está definido ou não possui getPrettyHtml"
        );
        return;
      }

      const dmp = new DiffMatchPatch();
      const diffs = dmp.diff_main(props.oldText, props.newText);
      dmp.diff_cleanupSemantic(diffs);
      const patch = dmp.patch_make(props.oldText, props.newText, diffs);
      const patchText = dmp.patch_toText(patch);

      // Gere o HTML do diff
      try {
        diffHtml.value = Diff2Html.getPrettyHtml(patchText, {
          inputFormat: "diff",
          showFiles: true,
          matching: "lines",
          outputFormat: props.splitView ? "side-by-side" : "line-by-line",
          // Adicione outras opções conforme necessário
        });
      } catch (error) {
        console.error("Erro ao gerar o HTML do diff:", error);
      }
    };

    watch(() => [props.oldText, props.newText, props.splitView], generateDiff, {
      immediate: true,
    });

    onMounted(() => {
      generateDiff();
    });

    return {
      diffHtml,
    };
  },
};
</script>
  
  <style scoped>
.diff-container {
  overflow-x: auto;
  width: 100%;
  height: 500px; /* Ajuste conforme necessário */
}
</style>
  