<template>
  <div class="AppAbout">
    <h1>Sobre o Sistema</h1>
    <p>
      Bem-vindo ao nosso sistema de gerenciamento de rodadas de preço (PLD) no
      Ambiente Livre de Contratação (ACL) de energia. Este sistema foi
      projetado para oferecer uma solução completa e eficiente para a
      integração e análise de dados relevantes no setor de energia elétrica.
    </p>
    <p>
      O sistema permite o acesso a dados de medição e arquivos regulatórios
      disponibilizados pelo Operador Nacional do Sistema (ONS). Além disso, ele
      recebe dados de previsão de precipitação (Prevista ECMWS e Realizada), que
      são convertidos em VNA (Vazão Natural Afluente) por meio do modelo/software
      <strong>SMAP</strong>, também fornecido pelo ONS. Esses dados são utilizados como
      input para os modelos de previsão de preço de energia <strong>NEWAVE</strong> e
      <strong>DECOMP</strong>.
    </p>
    <p>
      O sistema possibilita a gestão das rodadas de preço de maneira intuitiva
      e simplificada, permitindo ajustes nos parâmetros de entrada com
      facilidade. Ele foi desenvolvido para otimizar processos e fornecer uma
      interface amigável para profissionais do setor.
    </p>
    <p>
      Nosso objetivo é oferecer uma ferramenta que integre tecnologia e
      eficiência, contribuindo para a tomada de decisões estratégicas no mercado
      de energia.
    </p>
  </div>
</template>

<script>
export default {
  name: "AppAbout",
};
</script>

<style scoped>
.about {
  padding: 20px;
  font-family: Arial, sans-serif;
  line-height: 1.6;
}

.about h1 {
  font-size: 2rem;
  margin-bottom: 1rem;
  color: #2c3e50;
}

.about p {
  font-size: 1rem;
  margin-bottom: 1rem;
  color: #34495e;
}
</style>
