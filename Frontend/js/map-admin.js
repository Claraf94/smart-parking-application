import { loadSpots, updateSpotCoordinates, createParkingSpot, updateSpotInfo, deleteParkingSpot } from './api-calls.js';
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

//initialize variables when creating a new parking spot
let currentLatitude = null;
let currentLongitude = null;
let creatingNewSpot = false;

//initialize leaflet map
const map = L.map('map').setView([museumLat, museumLng], mapZoom);
//OpenStreetMap base map layer
L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    maxZoom: 20,
    attribution: 'Map data © OpenStreetMap contributors'
}).addTo(map);

//store the spots and labels in the map in a list of layers
let parkingSquares = [];
let spotLabels = [];
let dragSpots = [];

async function renderSpots() {
    try {
        const spots = await loadSpots();
        if (!spots || spots.length === 0) {
            console.warn('No parking spots found.');
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

            //label properties with the spot code to help to identify which spot it is
            const label = L.marker([centerLatitude, centerLongitude], {
                icon: L.divIcon({
                    className: 'spot-label',
                    html: `<div style="font-size:8px; font-weight:bold; color:white;">${spotCode}</div>`,
                    iconSize: [15, 10]
                })
            }).addTo(map);
            spotLabels.push(label);

            //to help organize the spot on the map
            const dragSpot = L.marker([latitude, longitude], {
                draggable: true,
                opacity: 0,
                interactive: false,
            }).addTo(map);
            dragSpots.push(dragSpot);

            //update the rectangle boundaries when the marker is dragged
            dragSpot.on('drag', event => {
                const newCoordinates = event.target.getLatLng();
                const newBoundaries = [
                    [newCoordinates.lat, newCoordinates.lng],
                    [newCoordinates.lat + deltaLatitude, newCoordinates.lng + deltaLongitude]
                ];
                spotRectangle.setBounds(newBoundaries);
            });

            //save on the database the new coordinates
            dragSpot.on('dragend', async event => {
                const newCoordinates = event.target.getLatLng();
                try {
                    await updateSpotCoordinates(spotsID, newCoordinates.lat, newCoordinates.lng);
                    console.log(`Spot ${spotCode} updated.`);
                } catch (error) {
                    console.error('Some error occurred while updating the spot:', error);
                }
            });


            //popup with the spot information
            const popup = `
            <div class="spot-popup">
                <strong>${spotCode}</strong><br>
                Status: ${status}<br>
                Reservable: ${isReservable ? 'Yes' : 'No'}<br>
                <button class="edit-btn" data-id="${spotsID}" data-spotcode="${spotCode}" data-status="${status}" data-reservable="${isReservable}" data-description="${locationDescription || ''}">Edit</button>
            </div>`;
            spotRectangle.bindPopup(popup);
            spotRectangle.on('click', function (event) {
                spotRectangle.openPopup(event.latlng);
            });
        });
    } catch (error) {
        console.error('Some error occurred while loading parking spots:', error);
    }
}

//this function is called when the page is loaded to display the parking spots on the map
async function sidebarSubmit(event) {
    event.preventDefault();
    const spotId = document.getElementById('editSpotId').value;
    const status = document.getElementById('spotStatus').value;
    const isReservable = document.getElementById('spotReservable').value === 'true';
    const locationDescription = document.getElementById('spotDescription').value;

    const updatedInfo = { status, isReservable, locationDescription };

    try {
        await updateSpotInfo(spotId, updatedInfo);
        await renderSpots();
        const sidebarContent = document.getElementById('spotSidebar');
        const sidebar = bootstrap.Offcanvas.getInstance(sidebarContent);
        if (sidebar) {
            sidebar.hide();
        }
    } catch (error) {
        alert('Error updating spot information.');
    }
}

function saveChangesButton(event) {
    if (event.target.classList.contains('edit-btn')) {
        const button = event.target;
        const spotId = button.getAttribute('data-id');
        const spotCode = button.getAttribute('data-spotcode');

        document.getElementById('editSpotId').value = spotId;
        document.getElementById('spotStatus').value = button.getAttribute('data-status');
        document.getElementById('spotReservable').value = button.getAttribute('data-reservable');
        document.getElementById('spotDescription').value = button.getAttribute('data-description');
        document.getElementById('editSidebarTitle').textContent = `Edit Spot ${spotCode}`;

        const sidebarContent = document.getElementById('spotSidebar');
        const sidebar = new bootstrap.Offcanvas(sidebarContent);
        sidebar.show();
    }

}

async function createNewParkingSpot(event) {
    event.preventDefault();
    const spotCode = document.getElementById('newSpotCode').value.trim();
    const status = document.getElementById('newSpotStatus').value;
    const isReservable = document.getElementById('newSpotReservable').value === 'false';
    const locationDescription = document.getElementById('newSpotDescription').value;

    const spotData = {
        spotCode,
        status,
        isReservable,
        locationDescription,
        x: currentLatitude,
        y: currentLongitude,
    };

    try {
        await createParkingSpot(spotData);
        alert('New parking spot created.');
        await renderSpots();
        const sidebarContent = document.getElementById('createSpotSidebar');
        const sidebar = bootstrap.Offcanvas.getInstance(sidebarContent);
        if (sidebar) {
            sidebar.hide();
        }
    } catch (error) {
        alert('Error creating new parking spot: ' + error.message);
    }
}

function createSpotSidebar() {
    creatingNewSpot = true;
    alert('Please click on the map to set the location to add the new parking spot.');
}

map.on('click', function (event) {
    if (!creatingNewSpot) {
        return;
    }
    currentLatitude = event.latlng.lat;
    currentLongitude = event.latlng.lng;

    document.getElementById('newSpotLatitude').value = currentLatitude.toFixed(8);
    document.getElementById('newSpotLongitude').value = currentLongitude.toFixed(8);

    const createSidebarContent = document.getElementById('createSpotSidebar');
    const createSidebar = new bootstrap.Offcanvas(createSidebarContent);
    createSidebar.show();
    creatingNewSpot = false;
});

async function deleteSpot() {
    const spotId = document.getElementById('editSpotId').value;
    if (!spotId) {
        alert('It was not possible to delete this parking spot. Spot ID is not recognizable.');
        return;
    }

    const confirmAction = confirm('Delete this parking spot?');
    if (!confirmAction) {
        return;
    }
    try {
        await deleteParkingSpot(spotId);
        alert('Parking spot deleted.');
        await renderSpots();
        const sidebarContent = document.getElementById('spotSidebar');
        const sidebar = bootstrap.Offcanvas.getInstance(sidebarContent);
        if (sidebar) {
            sidebar.hide();
        }
    } catch (error) {
        alert('Something went wrong while deleting the selected parking spot: ' + error.message);
    }
}

document.addEventListener("DOMContentLoaded", () => {
    checkAuthenticationToken();
    console.log("Rendering parking spots...");
    renderSpots();

    document.getElementById('editSpotForm').addEventListener('submit', sidebarSubmit);
    document.addEventListener('click', saveChangesButton);
    document.getElementById('createNewSpotButton').addEventListener('click', createSpotSidebar);
    document.getElementById('createSpotForm').addEventListener('submit', createNewParkingSpot);
    document.getElementById('deleteSpotButton').addEventListener('click', deleteSpot);
});

//this will refresh the parking spots every 10 seconds
setInterval(renderSpots, refreshInterval);