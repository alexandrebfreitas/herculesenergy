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
    const selectedStates = ref(new Set()); // Armazena estados selecionados

    const initializeMap = () => {
      const brazilBounds = [
        [-33.742, -73.982], // Sul-Oeste (latitude, longitude)
        [5.272, -32.392],   // Norte-Leste (latitude, longitude)
      ];

      const map = L.map("map", {
        center: [latitude.value, longitude.value],
        zoom: zoomLevel.value,
        maxZoom: 10,
        minZoom: 4,
        maxBounds: brazilBounds,
        maxBoundsViscosity: 1.0,
      });

      L.tileLayer(
          "https://{s}.basemaps.cartocdn.com/light_nolabels/{z}/{x}/{y}{r}.png",
          {
            attribution:
                '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors &copy; <a href="https://carto.com/">CARTO</a>',
            subdomains: "abcd",
            maxZoom: 19,
          }
      ).addTo(map);

      const defaultStyle = {
        color: "#3388ff",
        weight: 1,
        opacity: 0.8,
        fillOpacity: 0.4,
      };

      const hoverStyle = {
        color: "#ff7800",
        weight: 2,
        opacity: 1,
        fillOpacity: 0.7,
      };

      const selectedStyle = {
        color: "#00ff00",
        weight: 3,
        opacity: 1,
        fillOpacity: 0.7,
      };

      fetch("./assets/Brazil.geojson")
          .then((response) => {
            if (!response.ok) {
              throw new Error(`Erro ao carregar o arquivo GeoJSON: ${response.statusText}`);
            }
            return response.json();
          })
          .then((geojsonData) => {
            const geoJsonLayer = L.geoJSON(geojsonData, {
              style: defaultStyle,
              onEachFeature: (feature, layer) => {
                const stateName = feature.properties.NM_UF;

                layer.on({
                  mouseover: (e) => {
                    const layer = e.target;
                    if (!selectedStates.value.has(stateName)) {
                      layer.setStyle(hoverStyle);
                    }
                    layer.bindTooltip(stateName, {
                      permanent: false,
                      direction: "top",
                      className: "state-tooltip",
                    }).openTooltip();
                  },
                  mouseout: (e) => {
                    const layer = e.target;
                    if (!selectedStates.value.has(stateName)) {
                      layer.setStyle(defaultStyle);
                    }
                    layer.closeTooltip();
                  },
                  click: (e) => {
                    const layer = e.target;
                    if (selectedStates.value.has(stateName)) {
                      // Desmarcar estado
                      selectedStates.value.delete(stateName);
                      layer.setStyle(defaultStyle);
                    } else {
                      // Marcar estado
                      selectedStates.value.add(stateName);
                      layer.setStyle(selectedStyle);
                    }
                  },
                });
              },
            }).addTo(map);

            // Corrige o ajuste do mapa para enquadrar os estados do GeoJSON
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
      selectedStates,
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

.state-tooltip {
  background: #ff7800;
  color: white;
  font-weight: bold;
  border-radius: 4px;
  padding: 2px 5px;
  text-align: center;
}
</style>
