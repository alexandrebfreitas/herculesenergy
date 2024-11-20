<template>
  <div>
    <div id="map"></div>
  </div>
</template>

<script>
import { onMounted, ref } from "vue";
import "leaflet/dist/leaflet.css";
import L from "leaflet";

export default {
  name: "AppMap",
  setup() {
    const latitude = ref(-14.235004); // Coordenadas iniciais do Brasil
    const longitude = ref(-51.92528);
    const zoomLevel = ref(4);

    const initializeMap = () => {
      // Define as bordas para enquadrar o Brasil
      const brazilBounds = [
        [-33.742, -73.982], // Sul-Oeste (latitude, longitude)
        [5.272, -32.392],   // Norte-Leste (latitude, longitude)
      ];

      // Inicializa o mapa com as opções
      const map = L.map("map", {
        center: [latitude.value, longitude.value],
        zoom: zoomLevel.value,
        maxZoom: 10, // Define o zoom máximo
        minZoom: 4,  // Define o zoom mínimo
        maxBounds: brazilBounds, // Define os limites do mapa
        maxBoundsViscosity: 1.0, // Mantém o mapa dentro dos limites
      });

      // Adiciona o provedor de tiles sem labels
      L.tileLayer(
        "https://{s}.basemaps.cartocdn.com/light_nolabels/{z}/{x}/{y}{r}.png",
        {
          attribution:
            '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors &copy; <a href="https://carto.com/">CARTO</a>',
          subdomains: "abcd",
          maxZoom: 19,
        }
      ).addTo(map);

      // Estilo padrão para os estados
      const defaultStyle = {
        color: "#3388ff",
        weight: 1,
        opacity: 0.8,
        fillOpacity: 0.4,
      };

      // Estilo ao passar o mouse
      const hoverStyle = {
        color: "#ff7800",
        weight: 3,
        opacity: 1,
        fillOpacity: 0.7,
      };

      // Carrega o arquivo GeoJSON
      fetch("./assets/Brazil.geojson")
        .then((response) => {
          if (!response.ok) {
            throw new Error(`Erro ao carregar o arquivo GeoJSON: ${response.statusText}`);
          }
          return response.json();
        })
        .then((geojsonData) => {
          // Adiciona a camada GeoJSON ao mapa
          const geoJsonLayer = L.geoJSON(geojsonData, {
            style: defaultStyle,
            onEachFeature: (feature, layer) => {
              // Adiciona eventos para interatividade
              layer.on({
                mouseover: (e) => {
                  const layer = e.target;
                  layer.setStyle(hoverStyle);
                  const info = `
                    <strong>Estado: ${feature.properties.NM_UF}</strong><br>
                    Sigla: ${feature.properties.SIGLA_UF}<br>
                    Região: ${feature.properties.NM_REGIAO}<br>
                    Área: ${feature.properties.AREA_KM2.toLocaleString()} km²
                  `;
                  layer.bindPopup(info).openPopup();
                },
                mouseout: (e) => {
                  const layer = e.target;
                  layer.setStyle(defaultStyle);
                  layer.closePopup();
                },
              });
            },
          }).addTo(map);

          // Ajusta o mapa para enquadrar os estados do GeoJSON
          map.fitBounds(geoJsonLayer.getBounds());
        })
        .catch((error) => {
          console.error("Erro ao carregar o GeoJSON:", error);
        });
    };

    onMounted(() => {
      initializeMap();
    });

    return {
      latitude,
      longitude,
      zoomLevel,
    };
  },
};
</script>

<style>
#map {
  width: 100%;
  height: 500px;
  border: 1px solid #ccc;
  border-radius: 8px;
}
</style>
