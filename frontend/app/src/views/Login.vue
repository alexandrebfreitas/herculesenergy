<template>
  <div class="auth-page">
    <div class="auth-container">
      <h2>Login</h2>
      <form @submit.prevent="handleLogin">
        <div class="mb-3">
          <label for="email" class="form-label">Email</label>
          <input
              type="email"
              id="email"
              v-model="email"
              class="form-control"
              required
          />
        </div>
        <div class="mb-3">
          <label for="password" class="form-label">Senha</label>
          <input
              type="password"
              id="password"
              v-model="password"
              class="form-control"
              required
          />
        </div>
        <button type="submit" class="btn btn-primary w-100">Login</button>
      </form>
      <div class="mt-3 text-center">
        <p>Não tem uma conta?</p>
        <button @click="goToRegister" class="btn btn-link">Registre-se aqui</button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "AppLogin",
  data() {
    return {
      email: "",
      password: "",
    };
  },
  methods: {
    handleLogin() {
      console.log("Login com", this.email, this.password);
    },
    goToRegister() {
      this.$router.push("/register");
    },
  },
};
</script>

<style>
.auth-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(to right, #6a11cb, #2575fc);
}

.auth-container {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
  text-align: center;
}
</style>
