import { createRouter, createWebHistory } from "vue-router";
import AppHome from "../views/Home.vue";
import AppAbout from "../views/About.vue";
import AppContact from "../views/Contact.vue";

const routes = [
  { path: "/", component: AppHome },
  { path: "/about", component: AppAbout },
  { path: "/contact", component: AppContact },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
