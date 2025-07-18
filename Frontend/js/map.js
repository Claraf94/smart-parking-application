//initialize the map with leaflet. For this project, OpenStreetMap is used as the base layer.
//Also, a part of the area of the National Museuum of Ireland was chosen as the parking area.
//The map is centered on the coordinates of the museum, with a zoom level of 19.

/* General configuration */
//central coordinates of the map
const museumLat = 53.34895;
const museumLng = -6.2872;
const mapZoom = 19;

//approximately size of a parking spot in meters converted to degrees(latitude and longitude).
const deltaLatitude = 0.00005;
const deltaLongitude = 0.000040;

//automatic refresh of the map that happens every 10 seconds
const refreshInterval = 10000;

//initialize the map with leaflet
const map = L.map('map').setView([museumLat, museumLng], mapZoom);

//OpenStreetMap base map layer
L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
  maxZoom: 21,
  attribution: 'Map data © OpenStreetMap contributors'
}).addTo(map);

//store the spots and lables in the map in a list
let parkingSquares = [];
let spotLabels = [];
let dragSpots = [];
let parkingSpots = [];
let userLatitude = 0;
let userLongitude = 0;
let currentRoute = null;

//if allowed by the user, the current location is retrieved by using the geolocation.
if (navigator.geolocation) {
  navigator.geolocation.getCurrentPosition(position => {
    userLatitude = position.coords.latitude;
    userLongitude = position.coords.longitude;
    L.circleMarker([userLatitude, userLongitude], {
      radius: 6,
      color: 'red',
      fillColor: 'red',
      fillOpacity: 0.7
    }).addTo(map).bindPopup("You are here");
  });
}

//if the user provides permission to use their geolocation, then it is possible to display
//the route from the user to the closer empty parking spot
function getClosestParkingSpot(userLatitude, userLongitude, spots) {
  let closestSpot = null;
  let closestDistance = Infinity;

  spots.forEach(spot => {
    if (spot.status === 'EMPTY') {
      const distanceLat = spot.x - userLatitude;
      const distanceLng = spot.y - userLongitude;
      const currentDistance = Math.sqrt(distanceLat * distanceLat + distanceLng * distanceLng);
      if (currentDistance < closestDistance) {
        closestDistance = currentDistance;
        closestSpot = spot;
      }
    }
  });
  return closestSpot;
}

//function to load all the parking spots and display them on the map
function loadSpots() {
  axios.get('http://localhost:8080/spots')
    .then(response => {
      //clear the previous spots from the map
      parkingSquares.forEach(p => map.removeLayer(p));
      spotLabels.forEach(s => map.removeLayer(s));
      dragSpots.forEach(d => map.removeLayer(d));
      parkingSquares = [];
      spotLabels = [];
      dragSpots = [];
      parkingSpots = response.data;

      parkingSpots.forEach(spot => {
        const latitude = spot.x;
        const longitude = spot.y;

        const boundaries = [
          [latitude, longitude],
          [latitude + deltaLatitude, longitude + deltaLongitude]
        ];

        //define some colors based on the status to help the user identify the parking spot status
        //red for occupied, orange for reserved, gray for maintenance, purple for spots that are empty but can be reserved,
        //and green for empty spots that cannot be reserved.
        const spotsColor = spot.status === 'OCCUPIED' ? 'red'
          : spot.status === 'RESERVED' ? 'orange'
            : spot.status === 'MAINTENANCE' ? 'gray'
              : spot.isReservable ? 'purple'
                : 'green';

        //this is the rectangle that represents visually the parking spot on the map
        const parkingSpot = L.rectangle(boundaries, {
          color: spotsColor,
          weight: 1,
          fillOpacity: 0.7
        }).addTo(map);
        parkingSquares.push(parkingSpot);

        //label with the spot code to help to identify with spot it is
        // Label flutuante estilo balãozinho
        const centerLatitude = (boundaries[0][0] + boundaries[1][0]) / 2;
        const centerLongitude = (boundaries[0][1] + boundaries[1][1]) / 2;

        const spotLabel = L.marker([centerLatitude, centerLongitude], {
          icon: L.divIcon({
            className: 'spot-label',
            html: `<div style="font-size:8px; font-weight:bold; color:white;">${spot.spotCode}</div>`,
            iconSize: [20, 10]
          })
        }).addTo(map);
        spotLabels.push(spotLabel);

        //to help organize the spot on the map
        const dragSpot = L.marker([latitude, longitude], {
          draggable: true,
          opacity: 0 // invisível
        }).addTo(map);
        dragSpots.push(dragSpot);

        //update the rectangle boundaries when the marker is dragged
        dragSpot.on('drag', function (drag) {
          const newCoordinates = drag.target.getLatLng();
          const newBoundaries = [
            [newCoordinates.lat, newCoordinates.lng],
            [newCoordinates.lat + deltaLatitude, newCoordinates.lng + deltaLongitude]
          ];
          parkingSpot.setBounds(newBoundaries);
        });

        //save on the database the new coordinates
        dragSpot.on('dragend', function (saveDrag) {
          const newCoordinates = saveDrag.target.getLatLng();

          axios.put(`http://localhost:8080/spots/${spot.spotsID}`, {
            x: newCoordinates.lat,
            y: newCoordinates.lng
          }, {
            headers: {
              Authorization: 'Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjbGFyYS5mcmVpdGFzQGhvdG1haWwuY29tIiwicm9sZXMiOlsiUk9MRV9BRE1JTiJdLCJpYXQiOjE3NTI4Mzc0NzAsImV4cCI6MTc1Mjg0ODI3MH0.-mg1QQkKq0JMYIK7SsFh4YM71hledj6sHQ41WMFPrAo'
            }
          }).then(() => {
            console.log(`Spot ${spot.spotCode} updated.`);
          }).catch(err => {
            console.error("Some error occurred while updating the spot:", err);
          });
        });

        //popup with the spot information
        parkingSpot.bindPopup(`
          <strong>${spot.spotCode}</strong><br>
          Status: ${spot.status}<br>
          Reservável: ${spot.isReservable ? 'Yes' : 'No'}<br>
          ${spot.locationDescription}
        `);
      });

      //trace the route from the user to the closest empty parking spot 
      if (userLatitude && userLongitude) {
        const closestSpot = getClosestParkingSpot(userLatitude, userLongitude, parkingSpots);
        if (closestSpot) {
          if (currentRoute) {
            map.removeControl(currentRoute);
          }
          currentRoute = L.Routing.control({
            show: false,
            addWaypoints: false,
            
            waypoints: [
              L.latLng(userLatitude, userLongitude),
              L.latLng(closestSpot.x, closestSpot.y)
            ],
            routeWhileDragging: false,
            draggableWaypoints: false,
            createMarker: function(i, wp, nWps){
              const icon = i === 0 ? 'start-icon.png' : 'end-icon.png';
              return L.marker(wp.latLng, {
                icon: L.icon({
                  iconUrl: icon,
                  iconSize: [25, 41],
                  iconAnchor: [12, 41],
                  popupAnchor: [1, -34],
                  shadowSize: [41, 41]
                })
              }).bindPopup(i === 0 ? "Your location" : `Closest spot: ${closestSpot.spotCode}`);
            }
          }).addTo(map);
        }
      }
    })
    .catch(error => {
      console.error("Some error occurred while loading the spots:", error);
    });
}
//this function is called when the page is loaded to display the parking spots on the map
document.addEventListener("DOMContentLoaded", loadSpots);
//this will refresh the parking spots every 10 seconds
setInterval(loadSpots, refreshInterval);