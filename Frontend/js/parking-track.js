import { checkIn, checkOut } from "./api-calls.js";
import { checkAuthenticationToken } from "./authentication-help.js";
document.addEventListener('click', async (event) => {
    checkAuthenticationToken();
    if (event.target.classList.contains("checkin-btn")) {
        const spotCode = event.target.getAttribute("data-code");
        try {
            await checkIn(spotCode);
            alert(`Check-in done at spot ${spotCode}.`);
            location.reload(); // Reload the page to reflect the current parking status
        } catch (error) {
            if (error.message) {
                alert(`Check-in failed: ${error.message}`);
            } else {
                alert('Check-in failed: Unknown error');
            }
        }
    }

    if (event.target.classList.contains("checkout-btn")) {
        const spotCode = event.target.getAttribute("data-code");
        try {
            await checkOut(spotCode);
            alert(`Check-out done at spot ${spotCode}.`);
            location.reload(); // Reload the page to reflect the current parking status
        } catch (error) {
            alert('Check-out failed: ' + (error.message || 'Unknown error'));
        }
    }
});