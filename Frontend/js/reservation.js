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

        //create reservation
        document.getElementById('reservationForm').addEventListener('submit', async (event) => {
            event.preventDefault();
            const spotCode = document.getElementById('spotCode').value;
            const part1 = document.getElementById('part1').value.trim().toUpperCase();
            const part2 = document.getElementById('part2').value.trim().toUpperCase();
            const part3 = document.getElementById('part3').value.trim().toUpperCase();
            const numberPlate = `${part1}-${part2}-${part3}`;
            const reservationDate = document.getElementById('reservationDate').value; // yyyy-MM-dd
            const reservationTime = document.getElementById('reservationTime').value; // HH:mm
            if (!spotCode || !phoneNumberValue.value.trim() || !numberPlate || !reservationDate || !reservationTime) {
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

            const now = new Date();
            const selectedDateTime = new Date(`${reservationDate}T${reservationTime}`);
            if (selectedDateTime <= now) {
                alert("Reservation date and time must be in the future.");
                return;
            }

            try {
                const formattedPhone = iti.getNumber();
                const startTime = `${reservationDate}T${reservationTime}:00`;

                await createReservation({
                    spot: { spotCode },
                    phoneNumber: formattedPhone,
                    numberPlate,
                    startTime
                });

                alert("Reservation created successfully! Your reservation has a duration of 4 hours after the start time.");
                await loadUserReservationHistory();
                //cleaning the form
                document.getElementById('reservationForm').reset();
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

