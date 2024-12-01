import { createRouter, createWebHistory } from "vue-router";

// Importação de componentes
import MainLayout from "../layouts/MainLayout.vue"; // Layout principal
import AppHome from "../views/Home.vue"; // Página inicial
import AppAbout from "../views/About.vue"; // Sobre
import AppContact from "../views/Contact.vue"; // Contato
import AppLogin from "../views/Login.vue"; // Login
import AppRegister from "../views/Register.vue"; // Registro
import FileMapping from "../views/FileMapping.vue"; // Mapeamento de Arquivo
import AppMap from "../views/Map.vue"; // Mapa
import AppDecks from "../views/Decks.vue"; // Decks

// Configuração de rotas
const routes = [
  // Rotas sem layout (Login e Registro)
  {
    path: "/login",
    component: AppLogin,
  },
  {
    path: "/register",
    component: AppRegister,
  },

  // Rotas com layout principal
  {
    path: "/",
    component: MainLayout,
    children: [
      { path: "", component: AppHome }, // Página inicial
      { path: "about", component: AppAbout }, // Sobre
      { path: "contact", component: AppContact }, // Contato
      { path: "file-mapping", component: FileMapping }, // Mapeamento de Arquivo
      { path: "map", component: AppMap }, // Mapa
      { path: "rodadas-preco/decks", component: AppDecks }, // Decks
    ],
  },
];

// Configuração do roteador
const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
