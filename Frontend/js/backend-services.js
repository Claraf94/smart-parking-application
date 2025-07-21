//const API_BASE_URL = smartparking-backend-byfwgng0eehza3ch.francecentral-01.azurewebsites.net;
const API_BASE_URL = "http://localhost:8080";

//function to add headers to the request
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
    const responseText = await response.json();
    if (!response.ok) {
        const errorMessage = responseText.Error || responseText.message || 'Unknown error';
        throw new Error(errorMessage);
    }
    return responseText;
}

//POST requests
async function post(endpoint, data, headers = {}) {
    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
        method: 'POST',
        headers: getAuthHeaders(headers),
        body: JSON.stringify(data)
    });
    const responseText = await response.json();
    if (!response.ok) {
        const errorMessage = responseText.Error || responseText.message || 'Unknown error';
        throw new Error(errorMessage);
    }
    return responseText;
}

//PUT requests
async function put(endpoint, data, headers = {}) {
    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
        method: 'PUT',
        headers: getAuthHeaders(headers),
        body: JSON.stringify(data)
    });
    const responseText = await response.json();
    if (!response.ok) {
        const errorMessage = responseText.Error || responseText.message || 'Unknown error';
        throw new Error(errorMessage);
    }
    return responseText;
}

//DELETE requests
async function del(endpoint, headers = {}) {
    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
        method: 'DELETE',
        headers: getAuthHeaders(headers)
    });
    const responseText = await response.json();
    if (!response.ok) {
        const errorMessage = responseText.Error || responseText.message || 'Unknown error';
        throw new Error(errorMessage);
    }
    return responseText;
}
// Export functions for use in other modules
export {
    get,
    post,
    put,
    del
};

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