// js/map.js

// 1) Parâmetros do “mundo”
const cols    = 5;              // número de colunas de vagas
const slotW   = 100;            // largura de cada vaga
const slotH   = 200;            // altura de cada vaga
const gap     = 20;             // espaçamento entre vagas
const streetH = 100;            // altura da rua
const worldW  = cols * (slotW + gap) - gap;
const worldH  = slotH + gap + streetH + gap + slotH;

// 2) Inicializa o mapa em CRS.Simple
const map = L.map('map', {
  crs:               L.CRS.Simple,
  minZoom:           -1,
  maxZoom:           1,
  zoom:              0,
  center:            [worldH / 2, worldW / 2],
  zoomControl:       false,
  attributionControl:false
});
const sw = map.unproject([0,       worldH]);
const ne = map.unproject([worldW,  0     ]);
map.setMaxBounds([sw, ne]).fitBounds([sw, ne]);

// 3) Estado global
let slotsData  = [];
let slotRects  = {};
let streetRect = null;
let routeLine  = null;

// ID da vaga reservada (defina antes de importar este script)
window.reservedSpotId = window.reservedSpotId || null;

// 4) Desenha a rua (faixa cinza)
function drawStreet() {
  if (streetRect) map.removeLayer(streetRect);
  const y0 = slotH + gap;
  const y1 = y0 + streetH;
  streetRect = L.rectangle([
    map.unproject([0,      y1]),
    map.unproject([worldW, y0])
  ], {
    color:       '#666',
    weight:      0,
    fillColor:   '#999',
    fillOpacity: 1
  }).addTo(map);
}

// 5) Desenha todas as vagas (retângulos coloridos)
function drawSlots() {
  // Remove retângulos antigos
  Object.values(slotRects).forEach(r => map.removeLayer(r));
  slotRects = {};

  slotsData.forEach(s => {
    const rect = L.rectangle([
      map.unproject([s.x,        s.y + slotH]),
      map.unproject([s.x + slotW, s.y      ])
    ], {
      color:       s.status === 'free'     ? 'green'
                   : s.status === 'occupied' ? 'red'
                   : 'yellow',
      weight:      1,
      fillOpacity: 1
    }).addTo(map);

    rect.bindPopup(`Vaga ${s.id}: ${s.status}`);
    slotRects[s.id] = rect;
  });
}

// 6) Carrega do backend (ou usa mockup) e redesenha
async function loadAndDraw() {
  try {
    const res = await fetch('/api/spots');
    if (!res.ok) throw new Error(`Status ${res.status}`);
    slotsData = await res.json();
    console.log('slotsData from API:', slotsData);
  } catch (err) {
    console.warn('Não foi possível obter /api/spots, usando dados de exemplo:', err);
    // Exemplo de 5 vagas na fileira de baixo
    slotsData = [
      { id: 1, x: 0,                    y: 0, status: 'free'     },
      { id: 2, x: slotW + gap,          y: 0, status: 'occupied' },
      { id: 3, x: 2 * (slotW + gap),    y: 0, status: 'reserved' },
      { id: 4, x: 3 * (slotW + gap),    y: 0, status: 'free'     },
      { id: 5, x: 4 * (slotW + gap),    y: 0, status: 'free'     }
    ];
  }

  drawStreet();
  drawSlots();
}
// chama ao carregar
loadAndDraw();

// 7) Atualiza a rota em L‐shape do usuário até a vaga reservada
function updateRoute(userXY) {
  if (!window.reservedSpotId) return;
  const dest = slotsData.find(s => s.id === window.reservedSpotId);
  if (!dest) return;

  // remove rota anterior
  if (routeLine) map.removeLayer(routeLine);

  // ponto de virada na rua
  const turn = {
    x: dest.x + slotW / 2,
    y: slotH + gap + streetH / 2
  };

  const pts = [
    map.unproject([ userXY.x,        userXY.y        ]),
    map.unproject([ userXY.x,        turn.y          ]),
    map.unproject([ turn.x,          turn.y          ]),
    map.unproject([ dest.x + slotW/2, dest.y + slotH/2 ])
  ];

  routeLine = L.polyline(pts, {
    color:     'blue',
    dashArray: '5,5',
    weight:    2
  }).addTo(map);
}

// 8) Geolocalização em tempo real
if (navigator.geolocation) {
  navigator.geolocation.watchPosition(pos => {
    // Converte lat/lon → x,y no CRS.Simple
    // Ajuste estes limites ao seu estacionamento real!
    const lonMin = -6.28895, lonMax = -6.28865;
    const latMin = 53.34885, latMax = 53.34899;

    const x = ((pos.coords.longitude - lonMin) / (lonMax - lonMin)) * worldW;
    const y = ((pos.coords.latitude  - latMin) / (latMax - latMin)) * worldH;

    updateRoute({ x, y });
  });
}