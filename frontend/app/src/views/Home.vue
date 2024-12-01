<template>
  <div class="dashboard-container">
    <div class="dashboard-header">
      <h1>Dashboard</h1>
      <p>Resumo das atividades e dados</p>
    </div>

    <div class="dashboard-grid">
      <!-- Resumo -->
      <div class="dashboard-summary">
        <h3>Resumo</h3>
        <div class="summary-cards">
          <div class="summary-card">
            <h4>Total de Registros</h4>
            <p>1,235</p>
          </div>
          <div class="summary-card">
            <h4>Estados Selecionados</h4>
            <p>{{ selectedStates.size }}</p>
          </div>
          <div class="summary-card">
            <h4>Última Atualização</h4>
            <p>{{ new Date().toLocaleDateString() }}</p>
          </div>
          <div class="summary-card">
            <h4>Taxa de Atividade</h4>
            <p>70%</p>
          </div>
        </div>
      </div>

      <!-- Gráfico e Mapa -->
      <div class="dashboard-row">
        <div class="dashboard-chart">
          <h3>Distribuição</h3>
          <canvas id="pieChart"></canvas>
        </div>
        <div class="dashboard-map">
          <h3>Mapa</h3>
          <AppMap />
        </div>
      </div>

      <!-- Tabela -->
      <div class="dashboard-table">
        <h3>Dados</h3>
        <table>
          <thead>
          <tr>
            <th>ID</th>
            <th>Nome</th>
            <th>Status</th>
            <th>Data</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="(item, index) in tableData" :key="index">
            <td>{{ item.id }}</td>
            <td>{{ item.name }}</td>
            <td>{{ item.status }}</td>
            <td>{{ item.date }}</td>
          </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script>
import AppMap from "../components/Map.vue";
import Chart from "chart.js/auto";

export default {
  name: "AppDashboard",
  components: {
    AppMap,
  },
  data() {
    return {
      tableData: [
        { id: 1, name: "Registro A", status: "Ativo", date: "2024-12-01" },
        { id: 2, name: "Registro B", status: "Inativo", date: "2024-12-02" },
        { id: 3, name: "Registro C", status: "Ativo", date: "2024-12-03" },
      ],
      selectedStates: new Set(),
    };
  },
  methods: {
    renderPieChart() {
      const ctx = document.getElementById("pieChart");
      new Chart(ctx, {
        type: "pie",
        data: {
          labels: ["Ativo", "Inativo"],
          datasets: [
            {
              data: [70, 30],
              backgroundColor: ["#4caf50", "#f44336"],
            },
          ],
        },
      });
    },
  },
  mounted() {
    this.renderPieChart();
  },
};
</script>

<style scoped>
.dashboard-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
  padding: 20px;
}

.dashboard-header {
  text-align: center;
  margin-bottom: 20px;
}

.dashboard-grid {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.dashboard-summary {
  grid-column: span 2;
}

.summary-cards {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.summary-card {
  flex: 1 1 200px;
  background: #f4f4f4;
  border-radius: 8px;
  padding: 15px;
  text-align: center;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.summary-card h4 {
  font-size: 16px;
  color: #555;
  margin-bottom: 10px;
}

.summary-card p {
  font-size: 20px;
  font-weight: bold;
  color: #333;
  margin: 0;
}

.dashboard-row {
  display: flex;
  justify-content: space-between;
  gap: 20px;
}

.dashboard-chart,
.dashboard-map {
  flex: 1;
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.dashboard-table {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.dashboard-table table {
  width: 100%;
  border-collapse: collapse;
}

.dashboard-table th,
.dashboard-table td {
  border: 1px solid #ddd;
  padding: 8px;
  text-align: left;
}

.dashboard-table th {
  background-color: #f4f4f4;
}

#pieChart {
  width: 100%;
  height: 300px;
}
</style>
