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
// Export functions for use in other modules
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
export async function resetPassword(token, newPassword) {
    try {
        return await post('/resetPassword/reset', { token, newPassword }, {});
    } catch (error) {
        console.error('Password reset error:', error);
        throw error;
    }
}  
