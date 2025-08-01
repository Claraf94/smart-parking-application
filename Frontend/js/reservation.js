import { getReservableSpots, createReservation, getUserReservationHistory, cancelReservationById } from "./api-calls.js";
import { checkAuthenticationToken } from "./authentication-help.js";

document.addEventListener('DOMContentLoaded', async () => {
    checkAuthenticationToken();
    try {
        const phoneNumberValue = document.querySelector("#phoneNumber");
        phoneNumberValue.addEventListener('keydown', (event) => {
            const enableKeys = ['Backspace', 'Delete', 'ArrowLeft', 'ArrowRight', 'Tab', 'Enter', 'Home', 'End', ' '];
            if (enableKeys.includes(event.key)) {
                return;
            }
            //allow only numbers and spaces
            if (!/^[0-9]$/.test(event.key)) {
                event.preventDefault();
                return;
            }
        });
        const iti = window.intlTelInput(phoneNumberValue, {
            initialCountry: "ie",
            nationalMode: false,
            separateDialCode: true,
            autoPlaceholder: "aggressive",
            utilsScript: "https://cdn.jsdelivr.net/npm/intl-tel-input@17.0.19/build/js/utils.js"
        });

        //get the reservable spots for selection 
        const spots = await getReservableSpots();
        const spotSelect = document.getElementById('spotCode');

        spots.forEach(spot => {
            const select = document.createElement('option');
            select.value = spot.spotCode;
            select.textContent = `${spot.spotCode} - ${spot.locationDescription || 'No description available'}`;
            spotSelect.appendChild(select);
        });

        //calendar
        const calendar = flatpickr("#startTime", {
            enableTime: true,
            dateFormat: "Y-m-d H:i",
            time_24hr: true,
            defaultDate: new Date(),
            position: "above"
        });
        document.getElementById('calendarIcon').addEventListener('click', () => {
            calendar.open();
        });

        //create reservation
        document.getElementById('reservationForm').addEventListener('submit', async (event) => {
            event.preventDefault();
            const spotCode = document.getElementById('spotCode').value;
            const part1 = document.getElementById('part1').value.trim().toUpperCase();
            const part2 = document.getElementById('part2').value.trim().toUpperCase();
            const part3 = document.getElementById('part3').value.trim().toUpperCase();
            const numberPlate = `${part1}-${part2}-${part3}`;
            const insertedDate = document.getElementById('startTime')._flatpickr.selectedDates[0];
            if (!insertedDate) {
                alert("Please select a valid date and time.");
                return;
            }
            const startTime = insertedDate.getFullYear() + '-' +
                String(insertedDate.getMonth() + 1).padStart(2, '0') + '-' +
                String(insertedDate.getDate()).padStart(2, '0') + 'T' +
                String(insertedDate.getHours()).padStart(2, '0') + ':' +
                String(insertedDate.getMinutes()).padStart(2, '0');
            if (!spotCode || !phoneNumberValue.value.trim() || !numberPlate || !startTime) {
                alert("Please fill in all fields.");
                return;
            }
            
            const plateRegex = /^\d{2,3}-[A-Z]{1,2}-\d{1,6}$/;
            if (!plateRegex.test(numberPlate)) {
                alert("Please enter a valid Irish number plate. (e.g., 24-DL-123456).");
                return;
            }
            if (!iti.isValidNumber()) {
                alert("Please enter a valid phone number.");
                return;
            }

            try {
                const formattedPhone = iti.getNumber();
                await createReservation({ spot: { spotCode }, phoneNumber: formattedPhone, numberPlate, startTime });
                alert("Reservation created successfully!");
                await loadUserReservationHistory();
                //cleaning the form
                document.getElementById('reservationForm').reset();
                calendar.setDate(new Date());
                iti.setNumber("");
            } catch (error) {
                alert(error.message || "Reservation could not be created. Please try again.");
            }
        });
        await loadUserReservationHistory();
    } catch (error) {
        alert('Error retrieving reservable spots: ' + (error.message || "Unknown error"));
    }
});

async function loadUserReservationHistory() {
    const content = document.getElementById('historyList');
    content.innerHTML = '';
    try {
        const backup = await getUserReservationHistory();
        if (!backup || backup.length === 0) {
            content.innerHTML = '<p>No reservations history.</p>';
            return;
        }
        backup.reverse().forEach(res => {
            const div = document.createElement('div');
            div.className = 'reservation-item';
            div.innerHTML = `
            <div class="reservation-card">
                <div class="reservation-details">
                    <strong>Reservation ID:</strong> ${res.reservationID}<br>
                    <strong>Phone:</strong> ${res.phoneNumber}<br>
                    <strong>Number Plate:</strong> ${res.numberPlate}<br>
                    <strong>Date:</strong> ${new Date(res.startTime).toLocaleDateString()}<br>
                    <strong>Time:</strong> ${new Date(res.startTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}<br>
                    <strong>Spot:</strong> ${res.spot.spotCode}<br>
                    <strong>Status:</strong> ${res.reservationStatus}<br>
                </div>
            </div>
            `;

            //in case the user needs to cancel a reservation that is active
            if (res.reservationStatus === 'ACTIVE') {
                const cancelButton = document.createElement('button');
                cancelButton.className = 'btn btn-sm btn-danger mt-2';
                cancelButton.textContent = 'Cancel';
                cancelButton.addEventListener('click', async () => {
                    if (confirm("Are you sure you want to cancel this reservation?")) {
                        try {
                            await cancelReservationById(res.reservationID);
                            alert("Reservation cancelled");
                            await loadUserReservationHistory();
                        } catch (error) {
                            alert(error.message || "Reservation could not be cancelled. Please try again.");
                        }
                    }
                });
                div.querySelector('.reservation-card').appendChild(cancelButton);
            }
            content.appendChild(div);
        });
    } catch (error) {
        console.error("Error loading reservation history:", error);
    }
}

