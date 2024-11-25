import { createRouter, createWebHistory } from "vue-router";
import AppHome from "../views/Home.vue";
import AppAbout from "../views/About.vue";
import AppContact from "../views/Contact.vue";
import FileMapping from "../views/FileMapping.vue"; // Importa a nova tela
import AppMap from "../views/Map.vue"; // Importa a nova tela
import AppDecks from "../views/Decks.vue"; // Importa a nova tela

const routes = [
  { path: "/", component: AppHome },
  { path: "/about", component: AppAbout },
  { path: "/contact", component: AppContact },
  { path: "/contact", component: AppContact },
  { path: "/file-mapping", component: FileMapping },
  { path: "/map", component: AppMap },
  { path: "/rodadas-preco/decks", component: AppDecks },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
