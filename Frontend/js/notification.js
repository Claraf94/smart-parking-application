import { getNotifications } from "./api-calls.js";
import { checkAuthenticationToken } from "./authentication-help.js";

document.addEventListener('DOMContentLoaded', async () => {
    checkAuthenticationToken();
    await loadNotifications();
});

async function loadNotifications() {
    const content = document.getElementById('notificationList');
    content.innerHTML = '';
    try {
        const userNotifications = await getNotifications();
        if (!userNotifications || userNotifications.length === 0) {
            content.innerHTML = '<p>You do not have any notifications.</p>';
            return;
        }

        // Mostrar da mais recente para mais antiga
        userNotifications.reverse().forEach(notification => {
            const div = document.createElement('div');
            div.className = 'notification-item';
            div.innerHTML = `
                <div class="notification-card">
                    <div class="notification-details">
                        <strong>Type:</strong> ${notification.notificationType}<br>
                        <strong>Message:</strong> ${notification.textMessage}<br>
                        <strong>Date:</strong> ${new Date(notification.created).toLocaleDateString()}<br>
                        <strong>Time:</strong> ${new Date(notification.created).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}<br>
                        ${notification.fine > 0 ? `<strong>Fine:</strong> €${notification.fine.toFixed(2)} (${notification.isPaid ? 'Paid' : 'Unpaid'})<br>` : ''}
                    </div>
                </div>
            `;

            content.appendChild(div);
        });
    } catch (error) {
        console.error("Failed to load notifications:", error);
        content.innerHTML = "<p>Error loading notifications.</p>";
    }
} 