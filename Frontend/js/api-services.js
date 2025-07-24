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

// Generic helper functions for backend services
//GET requests
async function get(endpoint, headers = {}) {
    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
        method: 'GET',
        headers: getAuthHeaders(headers)
    });
    const data = await readResponseAsJson(response);
    if (!response.ok) {
        const errorMessage = data.Error || data.message || 'Unknown error';
        throw new Error(errorMessage);
    }
    return data;
}

//POST requests
async function post(endpoint, data, headers = {}) {
    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
        method: 'POST',
        headers: getAuthHeaders(headers),
        body: JSON.stringify(data)
    });
    const responseData = await readResponseAsJson(response);
    if (!response.ok) {
        const errorMessage = responseData.Error || responseData.message || 'Unknown error';
        throw new Error(errorMessage);
    }
    return responseData;
}

//PUT requests
async function put(endpoint, data, headers = {}) {
    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
        method: 'PUT',
        headers: getAuthHeaders(headers),
        body: JSON.stringify(data)
    });
    const responseData = await readResponseAsJson(response);
    if (!response.ok) {
        const errorMessage = responseData.Error || responseData.message || 'Unknown error';
        throw new Error(errorMessage);
    }
    return responseData;
}

//DELETE requests
async function del(endpoint, headers = {}) {
    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
        method: 'DELETE',
        headers: getAuthHeaders(headers)
    });
    const responseData = await readResponseAsJson(response);
    if (!response.ok) {
        const errorMessage = responseData.Error || responseData.message || 'Unknown error';
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

//function to read the response as a text and convert it to JSON
async function readResponseAsJson(response) {
    const responseText = await response.text();
    try {
        return JSON.parse(responseText);
    } catch (error) {
        return responseText;
    }
}

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
export async function loadSpots(){
    return await get('/spots');
}

//update the coordinates of a spot function
export async function updateSpotCoordinates(spotId, x, y) {
    try {
        return await put(`/spots/${spotId}`, {x,y});
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

export async function checkOut(spotCode) {
    return await put(`/parkingTrack/checkout/${spotCode}`);
}

