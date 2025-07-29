import{checkAuthenticationToken}from './authentication-help.js';
document.addEventListener('DOMContentLoaded', () => {
    checkAuthenticationToken();
    //clean the token provided for the session and redirect to login page
    localStorage.removeItem('token');
    window.location.href = "login.html";
});