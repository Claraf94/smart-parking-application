import { getReservableSpots, createReservation } from "./api-calls.js";

document.addEventListener('DOMContentLoaded', async () => {
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
            dateFormat: "Y-m-d\\TH:i",
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
            const numberPlate = document.getElementById('numberPlate').value.trim().toUpperCase();
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

            if (!iti.isValidNumber()) {
                alert("Please enter a valid phone number.");
                return;
            }

            try {
                const formattedPhone = iti.getNumber();
                await createReservation({ spot: { spotCode }, phoneNumber: formattedPhone, numberPlate, startTime });
                alert("Reservation created successfully!");
                //cleaning the form
                document.getElementById('reservationForm').reset();
                calendar.setDate(new Date());
                iti.setNumber("");
            } catch (error) {
                console.error('Error creating reservation:', error);
                alert("Reservation could not be created. Please try again.");
            }
        });
    } catch (error) {
        console.error('Error retrieving reservable spots:', error);
    }
});
