import { login, register } from './api-calls.js';

//simple function to check the email format
function isEmailValid(email) {
    const emailVerification = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    return emailVerification.test(email);
}


document.addEventListener('DOMContentLoaded', () => {
    //login user
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', async (event) => {
            event.preventDefault();
            console.log("Login form submitted.");

            const email = document.getElementById('email').value.trim();
            const password = document.getElementById('password').value.trim();

            if (!email || !password) {
                alert("Please fill in both email and password fields.");
                return;
            }

            // Validate email format
            if (!isEmailValid(email)) {
                alert("Please enter a valid email address.");
                return;
            }

            try {
                const response = await login(email, password);
                if (response.token) {
                    localStorage.setItem('token', response.token);
                    window.location.href = 'user-dashboard.html';
                } else {
                    alert("Login failed. Please check your credentials.");
                }
            } catch (error) {
                if (error.message.includes("Email not found")) {
                    alert("This email is not registered.");
                } else if (error.message.includes("Incorrect password")) {
                    alert("Incorrect password.");
                } else {
                    alert("Login error: " + error.message);
                }
            }
        });
    }


    //register user
    const registerForm = document.getElementById('registerForm');
    if (registerForm) {
        registerForm.addEventListener('submit', async (event) => {
            event.preventDefault();
            console.log("Register form submitted.");

            const firstName = document.getElementById('firstName').value.trim();
            const lastName = document.getElementById('lastName').value.trim();
            const email = document.getElementById('email').value.trim();
            const password = document.getElementById('password').value.trim();

            if (!firstName || !lastName || !email || !password) {
                alert("Please complete all fields.");
                return;
            }

            if (!isEmailValid(email)) {
                alert("Please enter a valid email address.");
                return;
            }

            try {
                const user = {
                    firstName,
                    lastName,
                    email,
                    password
                };
                const response = await register(user);
                if (response.success) {
                    alert("Registration successful! You can now log in.");
                    //redirect to login page
                    window.location.href = 'login.html';
                } else {
                    alert("Registration failed: " + response.message);
                }
            } catch (error) {
                console.error(error);
                alert("Registration error: " + error.message);
            }
        });
    }
});  