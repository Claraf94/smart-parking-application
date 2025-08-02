//const API_BASE_URL = "https://smartparking-backend-byfwgng0eehza3ch.francecentral-01.azurewebsites.net";
const API_BASE_URL = "http://localhost:8080";

// ------- API HELPER METHODS: TOKEN, GET, POST, PUT, DELETE ------
//function to add authentication headers to the request
function getAuthHeaders(extraHeaders = {}) {
    const token = localStorage.getItem('token');
    return {
        'Content-Type': 'application/json',
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
        ...extraHeaders
    };
}

//function to read the response as a text and convert it to JSON
async function readResponseAsJson(response) {
    const responseText = await response.text();
    try {
        return JSON.parse(responseText);
    } catch (error) {
        return responseText;
    }
}

//function to extract error messages from the response
function extractErrorMessage(responseData, response) {
    if (!responseData) {
        return response.statusText || `Error ${response.status}`;
    }

    if (typeof responseData === 'object') {
        if (responseData.message) return responseData.message;
        if (responseData.error) return responseData.error;
        try {
            return JSON.stringify(responseData);
        } catch {
            return String(responseData);
        }
    }
    return String(responseData);
}

// Generic helper functions for backend services
//GET requests
async function get(endpoint, headers = {}) {
    let response;
    try {
        response = await fetch(`${API_BASE_URL}${endpoint}`, {
            method: 'GET',
            headers: getAuthHeaders(headers)
        });
    } catch (networkErr) {
        throw new Error(networkErr.message || 'Network error');
    }
    const responseData = await readResponseAsJson(response);
    if (!response.ok) {
        const errorMessage = extractErrorMessage(responseData, response);
        throw new Error(errorMessage);
    }
    return responseData;
}

//POST requests
async function post(endpoint, data, headers = {}) {
    let response;
    try {
        response = await fetch(`${API_BASE_URL}${endpoint}`, {
            method: 'POST',
            headers: getAuthHeaders(headers),
            body: JSON.stringify(data)
        });
    } catch (networkErr) {
        throw new Error(networkErr.message || 'Network error');
    }
    const responseData = await readResponseAsJson(response);
    if (!response.ok) {
        const errorMessage = extractErrorMessage(responseData, response);
        throw new Error(errorMessage);
    }
    return responseData;
}

//PUT requests
async function put(endpoint, data = null, headers = {}) {
    const options = {
        method: 'PUT',
        headers: getAuthHeaders(headers)
    };
    if (data !== null) {
        options.body = JSON.stringify(data);
    }
    let response;
    try {
        response = await fetch(`${API_BASE_URL}${endpoint}`, options);
    } catch (networkErr) {
        throw new Error(networkErr.message || 'Network error');
    }
    const responseData = await readResponseAsJson(response);
    if (!response.ok) {
        const errorMessage = extractErrorMessage(responseData, response);
        throw new Error(errorMessage);
    }
    return responseData;
}

//DELETE requests
async function del(endpoint, headers = {}) {
    let response;
    try {
        response = await fetch(`${API_BASE_URL}${endpoint}`, {
            method: 'DELETE',
            headers: getAuthHeaders(headers)
        });
    } catch (networkErr) {
        throw new Error(networkErr.message || 'Network error');
    }
    const responseData = await readResponseAsJson(response);
    if (!response.ok) {
        const errorMessage = extractErrorMessage(responseData, response);
        throw new Error(errorMessage);
    }
    return responseData;
}

// Export HTTP methods for use in other modules
export {
    get,
    post,
    put,
    del
};

// --------- AUTHENTICATION AND USER FUNCTIONS ----------
//authentication function
export async function login(email, password) {
    try {
        return await post('/users/login', { email, password }, {});
    } catch (error) {
        console.error('Login error:', error);
        throw error;
    }
}

//register user function
export async function register(user) {
    try {
        return await post('/users/register', user, {});
    } catch (error) {
        console.error('Registration error:', error);
        throw error;
    }
}

//request password reset function
export async function requestPasswordReset(email) {
    try {
        return await post('/resetPassword/request', { email }, {});
    } catch (error) {
        console.error('Password reset request error:', error);
        throw error;
    }
}

//reset password function
export async function resetPassword(token, data, customHeaders = {}) {
    try {
        const response = await fetch(`${API_BASE_URL}/resetPassword/reset?token=${encodeURIComponent(token)}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                ...customHeaders
            },
            body: JSON.stringify(data)
        });

        const responseData = await readResponseAsJson(response);
        return {
            success: response.ok,
            status: response.status,
            ...responseData
        };
    } catch (error) {
        console.error('Password reset error:', error);
        throw error;
    }
}

// --------- PARKING SPOTS FUNCTIONS ----------
//function to load all the parking spots and display them on the map
export async function loadSpots() {
    return await get('/spots');
}

//update the coordinates of a spot function
export async function updateSpotCoordinates(spotId, x, y) {
    try {
        return await put(`/spots/${spotId}`, { x, y });
    } catch (error) {
        console.error('Error updating spot coordinates:', error);
        throw error;
    }
}

//get the closest empty spot function
export async function getClosestSpot(userLatitude, userLongitude) {
    return await get(`/spots/closestSpot?x=${userLatitude}&y=${userLongitude}`);
}

// ------ VOICE ASSISTANCE FUNCTION ------
//function to assist drivers with navigation instructions
export function speakInstruction(text) {
    const speech = new SpeechSynthesisUtterance(text);
    speech.lang = 'en-US';
    speech.rate = 1;
    speechSynthesis.speak(speech);
}

// --------- PARKING TRACK FUNCTIONS ----------
//check in function
export async function checkIn(spotCode) {
    return await put(`/parkingTrack/checkin/${spotCode}`);
}

//check out function
export async function checkOut(spotCode) {
    return await put(`/parkingTrack/checkout/${spotCode}`);
}

// --------- RESERVATION FUNCTIONS ----------
//get all reservable spots function
export async function getReservableSpots() {
    return await get(`/spots?isReservable=true`);
}

//create a reservation function
export async function createReservation(reservationData) {
    return await post('/reservations/create', reservationData);
}

//get history of reservations function
export async function getUserReservationHistory() {
    return await get('/reservations/user');
}

//cancel reservation function
export async function cancelReservationById(reservationId) {
    return await put(`/reservations/cancel/${reservationId}`);
}

// --------- NOTIFICATION FUNCTIONS ----------

//get all notifications received function
export async function getNotifications() {
    return await get('/notifications/user');
}

// -------- FINE FUNCTIONS ----------
//get all personal fines function
export async function getUserFines() {
    return await get('/notifications/personal-fines');
}

// --------- ADMIN FUNCTIONS ----------
//creat a new parking spot function
export async function createParkingSpot(spotData) {
    try {
        return await post('/spots/register', spotData);
    } catch (error) {
        console.error('Error creating parking spot:', error);
        throw error;
    }
}

//update spot information function
export async function updateSpotInfo(spotId, spotData) {
    try {
        return await put(`/spots/update/${spotId}`, spotData);
    } catch (error) {
        console.error('Error updating spot information:', error);
        throw error;
    }
}

//delete a spot function
export async function deleteParkingSpot(spotId) {
    try {
        return await del(`/spots/delete/${spotId}`);
    } catch (error) {
        console.error('Error deleting spot:', error);
        throw error;
    }
}

//get a overview of all spots function
export async function getSpotsOverview() {
    const spots = await loadSpots();
    const overall = spots.length;
    const statusOverview = { EMPTY: 0, OCCUPIED: 0, RESERVED: 0, MAINTENANCE: 0 };
    const reservableSpotsOverview = { EMPTY: 0, OCCUPIED: 0, RESERVED: 0, MAINTENANCE: 0 };
    const regularSpotsOverview = { EMPTY: 0, OCCUPIED: 0, RESERVED: 0, MAINTENANCE: 0 };
    for (const spot of spots) {
        const status = spot.status;
        const isReservable = spot.isReservable;
        statusOverview[status] = (statusOverview[status] || 0) + 1;
        if (isReservable) {
            reservableSpotsOverview[status] = (reservableSpotsOverview[status] || 0) + 1;
        } else {
            regularSpotsOverview[status] = (regularSpotsOverview[status] || 0) + 1;
        }
    }
    return { overall, statusOverview, reservableSpotsOverview, regularSpotsOverview };
}

//get all users function
export async function getAllUsers() {
    return await get('/users/findAll');
}

//get user by email function
export async function getUserByEmail(email) {
    const response = await get(`/users/findByEmail/${email}`);
    return response ? Array.isArray(response) ? response : [response] : [];
}

//get users by type function
export async function getUsersByType(userType) {
    const response = await get(`/users/findByUserType/${userType}`);
    return response ? Array.isArray(response) ? response : [response] : [];
}

//get current parked users function
export async function getCurrentParkedUsers() {
    return await get('/parkingTrack/checked-in');
}

//get parking track history function
export async function getParkingTrackHistory() {
    return await get('/parkingTrack/all');
}

//get all reservations function
export async function getAllReservations() {
    return await get('/reservations/all');
}

//get all notifications function
export async function getAllNotifications() {
    return await get('/notifications/all');
}

//get unpaid fines function
export async function getUnpaidFines() {
    return await get('/notifications/unpaid-fines');
}

//mark fine as paid function
export async function payFine(notificationId) {
    return await put(`/notifications/${notificationId}/pay`);
}

//create a new notification function
export async function createNotification(notificationData) {
    return await post('/notifications/create', notificationData);
}

//create a fine notification function
export async function createFineNotification(user) {
    return await post('/notifications/fine', user);
}


// --------- CONTACT FORM FUNCTION
export async function submitContactForm({ name, email, subject, message } = {}, {
    endpoint = "https://formspree.io/f/movlnazp",

} = {}) {
    if (!name || !email || !subject || !message) {
        throw new Error("All fields are required.");
    }

    const formData = new FormData();
    formData.append('name', name);
    formData.append('_replyto', email);
    formData.append('subject', subject);
    formData.append('message', message);
    formData.append('_gotcha', '');//to prevent spam bots

    const response = await fetch(endpoint, {
        method: 'POST',
        body: formData,
        headers: {
            Accept: 'application/json'
        }
    });
    const payload = await response.json().catch(() => ({}));
    if (!response.ok) {
        const errMsg = payload.error || (payload.errors ? payload.errors.map(e => e.message).join(', ') : null) || 'Contact form submission failed.';
        throw new Error(errMsg);
    }
    return payload;
}
