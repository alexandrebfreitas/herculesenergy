import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import { library } from "@fortawesome/fontawesome-svg-core";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import { faHome, faUser, faEnvelope,faFolderOpen,faMap} from "@fortawesome/free-solid-svg-icons";
import $ from "jquery";
import './assets/main.css';
window.$ = $; // Adiciona jQuery como global (necessário para DataTables)
// Adicione os ícones à biblioteca
library.add(faHome, faUser, faEnvelope,faFolderOpen,faMap);

createApp(App).use(router).component("font-awesome-icon", FontAwesomeIcon).mount("#app");
