import { getReservableSpots, createReservation } from "./api-calls.js";

document.addEventListener('DOMContentLoaded', async () => {
    try {
        //making sure the phone number starts with +353
        const phoneNumber = document.getElementById('phoneNumber');
        phoneNumber.value = '+353 '; // default value
        phoneNumber.addEventListener('input', function () {
            if (!this.value.startsWith('+353 ')) {
                this.value = '+353 ';
                this.setSelectionRange(this.selectionStart < 6 ? 6 : this.selectionStart);
            }
        });
        const spots = await getReservableSpots();
        const spotSelect = document.getElementById('spotCode');

        spots.forEach(spot => {
            const select = document.createElement('option');
            select.value = spot.spotCode;
            select.textContent = `${spot.spotCode} - ${spot.locationDescription || 'No description available'}`;
            spotSelect.appendChild(select);
        });

        // Initialize the date and time pickers
        new tempusDominus.TempusDominus(document.getElementById('reservationDateTime'), {
            display: {
                components: {
                    calendar: true,
                    date: true,
                    month: true,
                    year: true,
                    hours: true,
                    minutes: true,
                    seconds: false
                },
                placement: 'right'  // ou 'left', 'top', 'bottom' conforme quiser
            },
            localization: {
                locale: "en-IE"
            }
        });
    } catch (error) {
    console.error('Error retrieving reservable spots:', error);
}
});
