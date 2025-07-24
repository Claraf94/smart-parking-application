import { checkIn, checkOut } from "./api-services";

document.addEventListener("click", async (event) => {
    if(event.target.classList.contains("checkin-btn")) {
        const spotId = event.target.dataset.spotId;
        try {
            await checkIn(spotId);
            alert("Check-in done!");
        } catch (error) {
            alert(error.message || "Something went wrong during check-in.");
        }   
    }
    if(event.target.classList.contains("checkout-btn")) {
        const spotId = event.target.dataset.spotId;
        try {
            await checkOut(spotId);
            alert("Check-out done!");
        } catch (error) {
            alert(error.message || "Something went wrong during check-out.");
        }
    }
});