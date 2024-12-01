import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import { library } from "@fortawesome/fontawesome-svg-core";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
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
    faCloudSun,   // Importa o ícone para Meteorologia
    faWater,      // Importa o ícone para Hidrologia
    faLayerGroup, // Importa o ícone para Decks
    faChartLine,   // Importa o ícone para Resultados
    faBars,    // Importa o ícone para Resultados
} from "@fortawesome/free-solid-svg-icons";

import $ from "jquery";
import './assets/main.css';
window.$ = $; // Adiciona jQuery como global (necessário para DataTables)
// Importe o CSS do Bootstrap
import 'bootstrap/dist/css/bootstrap.min.css'

// Importe o JS do Bootstrap
import 'bootstrap'

// Adicione os ícones à biblioteca
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
    faCloudSun,   // Adiciona o ícone à biblioteca
    faWater,      // Adiciona o ícone à biblioteca
    faLayerGroup, // Adiciona o ícone à biblioteca
    faChartLine,   // Adiciona o ícone à biblioteca
    faBars,   // Adiciona o ícone à biblioteca
);

// Cria o app Vue e registra o FontAwesomeIcon como componente global
createApp(App)
    .use(router)
    .component("font-awesome-icon", FontAwesomeIcon)
    .mount("#app");
