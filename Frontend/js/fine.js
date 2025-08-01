import { getUserFines } from "./api-calls.js";

async function displayFines() {
    const finesContent = document.getElementById('userFinesList');
    if (!finesContent) return;

    let fines = [];
    try {
        fines = await getUserFines();
    } catch (error) {
        let msg = 'Erro ao carregar suas multas.';
        if (error.message) msg += ' ' + error.message;
        finesContent.innerHTML = `<p class="text-danger">${msg}</p>`;
        return;
    }

    if (!Array.isArray(fines) || fines.length === 0) {
        finesContent.innerHTML = '<p class="text-muted">You do not have any fines to be shown.</p>';
        return;
    }

    // ordenar da mais nova para a mais velha por created
    fines.sort((a, b) => {
        const dateA = a.created ? new Date(a.created) : new Date(0);
        const dateB = b.created ? new Date(b.created) : new Date(0);
        return dateB - dateA;
    });

    finesContent.innerHTML = fines.map(fine => {
        const amount = fine.fine != null ? parseFloat(fine.fine).toFixed(2) : '0.00';
        const isPaid = fine.isPaid === true || fine.isPaid === 1 || fine.isPaid === '1';
        const createdAt = fine.created ? new Date(fine.created).toLocaleString() : 'N/A';
        const message = fine.textMessage || '';
        return `
            <div class="message-box">
                <div><strong>Amount:</strong> €${amount}</div>
                <div><strong>Status:</strong> ${isPaid ? 'Paid' : 'Unpaid'}</div>
                <div><strong>Applied at:</strong> ${createdAt}</div>
                <div><em>${message}</em></div>
            </div>
        `;
    }).join('');
}

document.addEventListener('DOMContentLoaded', () => {
    displayFines();
});
