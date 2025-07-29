//make sure the user is authenticated before accessing certain pages
export function checkAuthenticationToken(redirectTo = 'login.html') {
    const token = localStorage.getItem('token');
    if (!token) {
        window.location.href = redirectTo;
    }
}