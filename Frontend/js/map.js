// Inicializa o mapa centrado no National Museum of Ireland
const map = L.map('map').setView([53.34875, -6.2874], 18);

// Adiciona mapa moderno do MapTiler
L.tileLayer('https://api.maptiler.com/maps/streets-v2/256/{z}/{x}/{y}.png?key=gUjTF0KX3lv94LMWum09', {
  attribution: '&copy; <a href="https://www.maptiler.com/">MapTiler</a> © OpenStreetMap contributors',
  maxZoom: 20
}).addTo(map);

// Adiciona um marcador para a localização do usuário, se permitido
if (navigator.geolocation) {
  navigator.geolocation.getCurrentPosition(position => {
    const userLat = position.coords.latitude;
    const userLng = position.coords.longitude;

    const userMarker = L.marker([userLat, userLng], { title: "Você está aqui" })
      .addTo(map)
      .bindPopup("Você está aqui");

    userMarker.openPopup();
  });
} else {
  console.warn("Geolocalização não suportada pelo navegador.");
}

// Guarda os marcadores por spotCode
const markersMap = new Map();

function carregarSpots() {
  axios.get('http://localhost:8080/spots')
    .then(response => {
      const spots = response.data;

      spots.forEach(spot => {
        const corPreenchimento = spot.status === 'OCCUPIED' ? 'red' :
                                 spot.status === 'RESERVED' ? 'orange' :
                                 spot.status === 'MAINTENANCE' ? 'gray' : 'green';

        // Calcula o retângulo da vaga com base na latitude (x) e longitude (y)
        const largura = 0.00003; // ~2.5 metros
        const altura = 0.00005;  // ~5.5 metros

        const bounds = [
          [spot.x, spot.y],
          [spot.x + altura, spot.y + largura]
        ];

        const rectangle = L.rectangle(bounds, {
          color: spot.isReservable ? '#b28cd9' : '#444',
          fillColor: corPreenchimento,
          fillOpacity: 0.7,
          weight: spot.isReservable ? 2 : 1
        }).addTo(map);

        rectangle.bindPopup(`
          <strong>${spot.spotCode}</strong><br/>
          Status: ${spot.status}<br/>
          Reservável: ${spot.isReservable ? 'Sim' : 'Não'}<br/>
          ${spot.locationDescription}
        `);
      });
    })
    .catch(error => {
      console.error("Erro ao carregar spots: ", error);
    });
}
// Carrega vagas ao iniciar e atualiza a cada 10s
document.addEventListener("DOMContentLoaded", function () {
  carregarSpots();
  setInterval(carregarSpots, 10000);
});