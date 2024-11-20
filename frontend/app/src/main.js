import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import { library } from "@fortawesome/fontawesome-svg-core";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import { faHome, faUser, faEnvelope } from "@fortawesome/free-solid-svg-icons";
// Adicione os ícones à biblioteca
library.add(faHome, faUser, faEnvelope);

createApp(App).use(router).component("font-awesome-icon", FontAwesomeIcon).mount("#app");
