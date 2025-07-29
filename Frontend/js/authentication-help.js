//make sure the user is authenticated before accessing certain pages
export function checkAuthenticationToken(redirectTo = 'login.html') {
    const token = localStorage.getItem('token');
    if (!token) {
        window.location.href = redirectTo;
    }
}

// -------- GET USER TYPE FUNCTION ----------
export function getUserRoleFromToken() {
    const token = localStorage.getItem('token');
    if (!token) return null;

    try {
        const payloadBase64 = token.split('.')[1];
        const decodedPayload = JSON.parse(atob(payloadBase64));
        const roles = decodedPayload.roles;
        return Array.isArray(roles) ? roles[0] : roles;
    } catch (e) {
        console.error("Failed to parse token payload:", e);
        return null;
    }
}