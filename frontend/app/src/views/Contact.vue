<template>
  <div class="contact">
    <h1>Contato</h1>
    <p>Entre em contato comigo por meio dos canais abaixo:</p>
    <ul>
      <li>
        <strong>LinkedIn:</strong>
        <a
          href="https://www.linkedin.com/in/alexandre-burghesi-424152135/"
          target="_blank"
          rel="noopener noreferrer"
        >
          Alexandre Burghesi
        </a>
      </li>
      <li>
        <strong>GitHub:</strong>
        <a
          href="https://github.com/alexandrebfreitas"
          target="_blank"
          rel="noopener noreferrer"
        >
          github.com/alexandrebfreitas
        </a>
      </li>
      <li>
        <strong>Telefone:</strong> (11) 94842-1727
      </li>
    </ul>
  </div>
</template>

<script>
export default {
  name: "AppContact",
};
</script>

<style scoped>
.contact {
  padding: 20px;
  font-family: Arial, sans-serif;
  line-height: 1.6;
}

.contact h1 {
  font-size: 2rem;
  margin-bottom: 1rem;
  color: #2c3e50;
}

.contact p {
  font-size: 1rem;
  margin-bottom: 1rem;
  color: #34495e;
}

.contact ul {
  list-style: none;
  padding: 0;
}

.contact ul li {
  margin: 10px 0;
  font-size: 1rem;
}

.contact a {
  color: #3498db;
  text-decoration: none;
}

.contact a:hover {
  text-decoration: underline;
}
</style>
