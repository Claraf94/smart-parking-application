import { submitContactForm } from "./api-calls";

//simple function to check the email format
function isEmailValid(email) {
    const emailVerification = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    return emailVerification.test(email);
}

export function handleContactFormSubmission({
    formSelector = '#contactForm',
    submitButtonSelector = '#submitButton',
    feedbackSelector = '#feedbackMessage',
} = {}) {
    const form = document.querySelector(formSelector);
    const submitButton = document.querySelector(submitButtonSelector);
    const feedbackMessage = document.querySelector(feedbackSelector);
    if (!form || !submitButton || !feedbackMessage) {
        console.error('Contact form elements not found.');
        return;
    }

    form.addEventListener('submit', async (event) => {
        event.preventDefault();
        submitButton.disabled = true;
        feedbackMessage.textContent = '';
        const originalMessage = submitButton.innerText;
        submitButton.innerText = 'Sending...';

        const name = form.querySelector('[name="name"]')?.value.trim();
        const email = form.querySelector('[name="_replyto"]')?.value.trim();
        const subject = form.querySelector('[name="subject"]')?.value.trim();
        const message = form.querySelector('[name="message"]')?.value.trim();

        if (!name || !email || !subject || !message) {
            feedbackMessage.textContent = 'Please fill in all fields.';
            submitButton.disabled = false;
            submitButton.innerText = originalMessage;
            return;
        }

        if (!isEmailValid(email)) {
            feedbackMessage.textContent = 'Please enter a valid email address.';
            submitButton.disabled = false;
            submitButton.innerText = originalMessage;
            return;
        }

        try {
            await submitContactForm({ name, email, subject, message });
            feedbackMessage.textContent = 'Your form has been sent successfully!';
            form.reset();
            setTimeout(() => {
                window.location.href = '/thank-you.html';
            }, 800);
        } catch (error) {
            console.error('Error submitting contact form:', error);
            feedbackMessage.textContent = 'There was an error sending your form. Please try again later.';
        } finally {
            if (!feedbackMessage.textContent.includes('sent successfully')) {
                submitButton.disabled = false;
                submitButton.innerText = originalMessage;
            }
        }
    });
}