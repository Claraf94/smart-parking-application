import { loadSpots, getClosestSpot, speakInstruction } from './api-calls.js';
import { checkAuthenticationToken } from './authentication-help.js'

//For this project, OpenStreetMap is used as the base layer.
//Also, a part of the area of the National Museum of Ireland was chosen as the parking area for simulation.
//The map is centered on the coordinates of the museum, with a zoom level of 19.

/* General configuration */
//central coordinates of the map
const museumLat = 53.34895;
const museumLng = -6.2872;
const mapZoom = 19;
const refreshInterval = 10000; //each 10 seconds the map will be refreshed

//approximately size of a parking spot in meters converted to degrees(latitude and longitude).
const deltaLatitude = 0.00005;
const deltaLongitude = 0.000040;

//initialize leaflet map
const map = L.map('map').setView([museumLat, museumLng], mapZoom);
//OpenStreetMap base map layer
L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
  maxZoom: 21,
  attribution: 'Map data © OpenStreetMap contributors'
}).addTo(map);

const statusLabel = [{ name: 'Reserved', color: 'orange', key: 'RESERVED' },
{ name: 'Maintenance', color: 'yellow', key: 'MAINTENANCE' },
{ name: 'Occupied', color: 'red', key: 'OCCUPIED' },
{ name: 'Reservable (empty)', color: 'purple', key: 'RESERVABLE_EMPTY' },
{ name: 'Empty (non-reservable)', color: 'green', key: 'EMPTY' },
];

const legend = L.control({ position: 'bottomright' });

//store the spots and labels in the map in a list of layers
let parkingSquares = [];
let spotLabels = [];
let dragSpots = [];
let userLatitude = 0;
let userLongitude = 0;
let currentRoute = null;
let showRoute = false;
let directions = [];
let currentDirectionIndex = 0;
let watchId = null;

legend.onAdd = function () {
  const div = L.DomUtil.create('div', 'informational');
  div.innerHTML = `<strong>Parking Informational Labels</strong><br/>` +
    statusLabel.map(status => `
      <label>
        <span class="color-box" style="background:${status.color}"></span>
        <span style="flex:1;">${status.name}</span><br>
      </label>
    `).join('');
  return div;
};
legend.addTo(map);

async function renderSpots() {
  try {
    const spots = await loadSpots();
    if (!spots || spots.length === 0) {
      console.warn("No parking spots found.");
      return;
    }
    //clear the previous layers from the map
    [...parkingSquares, ...spotLabels, ...dragSpots].forEach(p => map.removeLayer(p));
    parkingSquares = [];
    spotLabels = [];
    dragSpots = [];

    //define some colors based on the status to help the user identify the parking spot status
    //red for occupied, orange for reserved, yellow for maintenance, purple for spots that are empty but can be reserved,
    //and green for empty spots that cannot be reserved.
    spots.forEach(spot => {
      const { x: latitude, y: longitude, spotCode, spotsID, boundaries, status, spotColor, spotLabel, isReservable, locationDescription } = spot;
      console.log(`Spot ${spotCode} has status: ${status}`);
      const [centerLatitude, centerLongitude] = spotLabel;

      //this is the rectangle properties that represents visually the parking spot on the map
      const spotRectangle = L.rectangle(boundaries, {
        color: spotColor,
        fillColor: spotColor,
        weight: 2,
        fillOpacity: 0.7,
        interactive: true
      }).addTo(map);
      parkingSquares.push(spotRectangle);

      const invisibleArea = L.circle([latitude, longitude], {
        radius: 2,
        fillOpacity: 0,
        opacity: 0,
        interactive: true
      }).addTo(map);
      dragSpots.push(invisibleArea);

      //label properties with the spot code to help to identify which spot it is
      const label = L.marker([centerLatitude, centerLongitude], {
        icon: L.divIcon({
          className: 'spot-label',
          html: `<div style="font-size:8px; font-weight:bold; color:white;">${spotCode}</div>`,
          iconSize: [15, 10]
        })
      }).addTo(map);
      spotLabels.push(label);

      //popup with the spot information
      const popup = `
        <div class="spot-popup">
          <strong>${spotCode}</strong><br>
          Status: ${status}<br>
          Reservable: ${isReservable ? 'Yes' : 'No'}<br>
          <button class="checkin-btn" data-code="${spotCode}">Check-In</button>
          <button class="checkout-btn" data-code="${spotCode}">Check-Out</button>
        </div>`;

      spotRectangle.bindPopup(popup);
      invisibleArea.bindPopup(spotRectangle.getPopup());
      invisibleArea.on('click', (event) => {
        spotRectangle.openPopup(event.latlng);
      });
    });
  } catch (error) {
    console.error("Some error occurred while loading parking spots:", error);
  }
}

//this function is called when the page is loaded to display the parking spots on the map
document.addEventListener("DOMContentLoaded", () => {
  checkAuthenticationToken();
  console.log("Rendering parking spots...");
  //if allowed by the user, the current location is retrieved by using the geolocation
  if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(position => {
      userLatitude = position.coords.latitude;
      userLongitude = position.coords.longitude;
      L.circleMarker([userLatitude, userLongitude], {
        radius: 6,
        color: 'purple',
        fillColor: 'purple',
        fillOpacity: 1.0
      }).addTo(map).bindPopup("You are here");
      renderSpots();
    }, error => {
      console.error("Geolocation error:", error);
      alert("Unable to retrieve your location. Please enable location services.");
      renderSpots();
    });
  } else {
    console.error("Geolocation is not supported by this browser.");
    renderSpots();
  }
});

//this will refresh the parking spots every 10 seconds
setInterval(renderSpots, refreshInterval);

//function to speak the instruction text
let audioUnlock = false;
function unlockInstruction() {
  if (audioUnlock) return;
  try {
    window.speechSynthesis.speak(new SpeechSynthesisUtterance(''));
  } catch (error) {
    console.warn('Something went wrong:', error);
  }
  audioUnlock = true;
}

//this will unlock the instruction when the user clicks on the map
document.addEventListener('click', () => {
  unlockInstruction();
}, { once: true });

window.speechSynthesis.onvoiceschanged = () => {
  window.speechSynthesis.getVoices();
};

function speakInstructionSafe(text) {
  if (!text) return;
  if (!audioUnlock) unlockInstruction();
  window.speechSynthesis.cancel();
  speakInstruction(text);
}


//function to get the closest empty spot
document.getElementById('routeBtn').addEventListener('click', async () => {
  if (showRoute && currentRoute) {
    map.removeControl(currentRoute);
    showRoute = false;
    currentRoute = null;
    if (watchId !== null) {
      navigator.geolocation.clearWatch(watchId);
      watchId = null;
    }
    return;
  }
  if (userLatitude && userLongitude) {
    try {
      const closestSpot = await getClosestSpot(userLatitude, userLongitude);
      //remove the previous route if it exists to create a new one
      if (currentRoute) {
        map.removeControl(currentRoute);
      }
      currentRoute = L.Routing.control({
        show: false,
        addWaypoints: [false],
        draggableWaypoints: false,
        routeWhileDragging: false,
        waypoints: [
          L.latLng(userLatitude, userLongitude),
          L.latLng(closestSpot.x, closestSpot.y)
        ],
        lineOptions: {
          styles: [{ color: 'purple', opacity: 0.5, weight: 3 }]
        },
        createPointer: () => null
      }).addTo(map);
      L.circleMarker([closestSpot.x, closestSpot.y], {
        radius: 6,
        color: 'purple',
        fillColor: 'purple',
        fillOpacity: 0.7
      }).addTo(map).bindPopup("Closest empty spot");
      showRoute = true;
      currentRoute.on('routesfound', (event) => {
        directions = event.routes[0].instructions;
        currentDirectionIndex = 0;
        if (directions.length > 0) {
          const instr = directions[0];
          const instructionSteps = document.getElementById('routeInstructions');
          if (instructionSteps) {
            instructionSteps.innerText = instr.text;
            speakInstructionSafe(instr.text);
          }
        }
      });

      if (watchId !== null) {
        navigator.geolocation.clearWatch(watchId);
      }
      watchId = navigator.geolocation.watchPosition(position => {
        const currentLatitude = position.coords.latitude;
        const currentLongitude = position.coords.longitude;

        if (directions.length === 0 || currentDirectionIndex >= directions.length) return;

        const currentInstruction = directions[currentDirectionIndex];
        if (!currentInstruction || !currentInstruction.latLng) return;

        const instrLatitude = currentInstruction.latLng.lat;
        const instrLongitude = currentInstruction.latLng.lng;

        const distance = map.distance(
          L.latLng(currentLatitude, currentLongitude),
          L.latLng(instrLatitude, instrLongitude)
        );

        if (distance < 10) {
          speakInstructionSafe(currentInstruction.text);
          currentDirectionIndex++;
          const instructionSteps = document.getElementById('routeInstructions');
          if (instructionSteps) instructionSteps.innerText = currentInstruction.text;
        }
      });
    } catch (error) {
      console.error("Some error occurred while trying to trace the route:", error);
      alert("Error finding the closest spot.");
    }
  }
});