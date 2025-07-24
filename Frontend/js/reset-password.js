import { requestPasswordReset, resetPassword } from './api-calls.js';

//simple function to check the email format
function isEmailValid(email) {
    const emailVerification = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    return emailVerification.test(email);
}

document.addEventListener('DOMContentLoaded', () => {
    const forgotPasswordForm = document.getElementById('forgotPasswordForm');
    if (forgotPasswordForm) {
        forgotPasswordForm.addEventListener('submit', async (event) => {
            event.preventDefault();
            console.log("Request reset password link submitted.");

            const email = document.getElementById('email').value.trim();
            if (!email) {
                alert("Please enter your email address.");
                return;
            }

            if (!isEmailValid(email)) {
                alert("Please enter a valid email address.");
                return;
            }

            try {
                const response = await requestPasswordReset(email);
                alert("A password reset link has been sent to your email address if this email is registered.");
                //redirect to login page
                window.location.href = 'login.html';
            } catch (error) {
                console.error(error);
                alert("Error requesting reset link: " + error.message);
            }
        });
    }

    //reset password
    const resetPasswordForm = document.getElementById('resetPasswordForm');
    if (resetPasswordForm) {
        resetPasswordForm.addEventListener('submit', async (event) => {
            event.preventDefault();
            console.log("Reset password form submitted.");

            const urlParams = new URLSearchParams(window.location.search);
            const token = urlParams.get('token');
            const newPassword = document.getElementById('newPassword').value.trim();
            const confirmNewPassword = document.getElementById('confirmPassword').value.trim();
            console.log("token:", token);
            console.log("newPassword:", newPassword);
            console.log("confirmNewPassword:", confirmNewPassword);

            if (!token || !newPassword || !confirmNewPassword) {
                alert("Please fill in all fields.");
                return;
            }

            if (newPassword !== confirmNewPassword) {
                alert("Passwords inserted do not match. Please try again.");
                return;
            }

            try {
                const response = await resetPassword(token, {
                    newPassword,
                    confirmNewPassword
                }, { Authorization: undefined });
                if (response && response.success) {
                    alert("Password successfully reset. You can now log in.");
                    window.location.href = 'login.html';
                } else {
                    alert("Reset password failed: " + response.message);
                }
            } catch (error) {
                console.error(error);
                alert("Error resetting password: " + error.message);
            }
        });
    }
});

