import { login, register } from './api-calls.js';

//simple function to check the email format
function isEmailValid(email) {
    const emailVerification = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    return emailVerification.test(email);
}

//simple function to check the number plate format
function isIrishPlateValid(plate) {
    const plateRegex = /^\d{2,3}-[A-Za-z]{1,2}-\d{1,6}$/;
    return plateRegex.test(plate);
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
                    const payloadBase64 = response.token.split('.')[1];
                    const decodedPayload = JSON.parse(atob(payloadBase64));
                    const roles = decodedPayload.roles || decodedPayload.authorities;
                    if (roles.includes('ROLE_ADMIN')) {
                        window.location.href = 'admin-dashboard.html';
                    } else {
                        window.location.href = 'user-dashboard.html';
                    }
                }
            } catch (error) {
                const msg = error.message || "Unknown error";
                if (msg.includes("Email not found")) {
                    alert("This email is not registered.");
                } else if (msg.includes("Incorrect password")) {
                    alert("Incorrect password.");
                } else {
                    alert("Login error: " + msg);
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
            const part1 = document.getElementById('part1').value.trim().toUpperCase();
            const part2 = document.getElementById('part2').value.trim().toUpperCase();
            const part3 = document.getElementById('part3').value.trim().toUpperCase();
            const numberPlate = `${part1}-${part2}-${part3}`;

            if (!firstName || !lastName || !email || !numberPlate || !password) {
                alert("Please complete all fields.");
                return;
            }

            // Validate number plate format
            if (!isIrishPlateValid(numberPlate)) {
                alert("Please enter a valid Irish number plate. (e.g., 24-DL-123456).");
                return;
            }
            // Validate email format
            if (!isEmailValid(email)) {
                alert("Please enter a valid email address.");
                return;
            }

            try {
                const user = {
                    firstName,
                    lastName,
                    email,
                    numberPlate,
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
                const msg = error.message || "Unknown error";
                alert("Registration error: " + msg);
            }
        });
    }
});  