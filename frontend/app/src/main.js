// src/main.js
import { createApp } from 'vue';
import App from './App.vue';
import router from './router';

// Importa Font Awesome Core e Vue Component
import { library } from "@fortawesome/fontawesome-svg-core";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";

// Importa apenas os ícones que serão usados
import {
    faHome,
    faUser,
    faEnvelope,
    faFolderOpen,
    faMap,
    faBook,
    faChevronDown,
    faChevronRight,
    faChevronLeft,
    faCloudSun,    // Ícone para Meteorologia
    faWater,       // Ícone para Hidrologia
    faLayerGroup,  // Ícone para Decks
    faChartLine,   // Ícone para Resultados
    faBars,        // Ícone para Menu
    faEdit,        // Ícone para Editar
    faPen,         // Ícone para Renomear
    faArrowsAltH,  // Ícone para Mover
    faDownload,    // Ícone para Download
    faFileArchive, // Ícone para Descompactar
    faTrash,       // Ícone para Excluir
    faTimes,       // Ícone para Cancelar
    faCheck,       // Ícone para Confirmar
    faSave,        // Ícone para Salvar
} from "@fortawesome/free-solid-svg-icons";

// Importa jQuery (necessário para DataTables)
import $ from "jquery";
import './assets/main.css';
window.$ = $; // Adiciona jQuery como global

// Importa o CSS e JS do Bootstrap (apenas uma vez)
import 'bootstrap/dist/css/bootstrap.min.css'; // Importa o CSS do Bootstrap
import 'bootstrap/dist/js/bootstrap.bundle.min.js'; // Importa o JS do Bootstrap (inclui Popper)
import 'bootstrap-icons/font/bootstrap-icons.css';
// Adiciona os ícones à biblioteca do Font Awesome
library.add(
    faHome,
    faUser,
    faEnvelope,
    faFolderOpen,
    faMap,
    faBook,
    faChevronDown,
    faChevronRight,
    faChevronLeft,
    faCloudSun,
    faWater,
    faLayerGroup,
    faChartLine,
    faBars,
    faEdit,
    faPen,
    faArrowsAltH,
    faDownload,
    faFileArchive,
    faTrash,
    faTimes,
    faCheck,
    faSave
);
import "monaco-editor/min/vs/editor/editor.main.css";
// Cria o app Vue, registra o FontAwesomeIcon como componente global e monta a aplicação
createApp(App)
    .use(router)
    .component("font-awesome-icon", FontAwesomeIcon)
    .mount("#app");
