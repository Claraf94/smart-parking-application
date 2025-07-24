import { checkIn, checkOut } from "./api-calls.js";

document.addEventListener('click', async (event) => {
    if(event.target.classList.contains("checkin-btn")) {
        const spotCode = event.target.getAttribute("data-code");
        try {
            await checkIn(spotCode);
            alert(`Check-in done at spot ${spotCode}.`);
            location.reload(); // Reload the page to reflect the current parking status
        } catch (error) {
            alert(`Something went wrong during check-in: ${error.response?.data || error.message}`);
        }
    }
    if(event.target.classList.contains("checkout-btn")) {
        const spotCode = event.target.getAttribute("data-code");
        try {
            await checkOut(spotCode);
            alert(`Check-out done at spot ${spotCode}.`);
            location.reload(); // Reload the page to reflect the current parking status
        } catch (error) {
            alert(`Something went wrong during check-out: ${error.response?.data || error.message}`);
        }
    }
});

