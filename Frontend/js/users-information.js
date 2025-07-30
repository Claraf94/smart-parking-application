import { getAllUsers, getUserByEmail, getUsersByType } from "./api-calls.js";

const emailInput = document.getElementById('emailInput');
const userTypeOption = document.getElementById('userTypeOption');
const applyFilterButton = document.getElementById('applyFilterButton');
const tableBody = document.getElementById('tableBody');

async function loadUsers({ email = '', userType = 'ALL' } = {}) {
    try {
        let allUsers = [];
        if (email.trim()) {
            allUsers = await getUserByEmail(email.trim());
        } else if (userType !== 'ALL') {
            allUsers = await getUsersByType(userType);
        } else {
            allUsers = await getAllUsers();
        }
        populateUsersTable(allUsers);
    } catch (error) {
        console.error('Error loading users:', error);
        tableBody.innerHTML = '<tr><td colspan="4" class="text-center">Error loading users</td></tr>';
    }
}

function populateUsersTable(allUsers) {
    tableBody.innerHTML = '';
    if (!Array.isArray(allUsers) || allUsers.length === 0) {
        tableBody.innerHTML = '<tr><td colspan="4" class="text-center">No user was found</td></tr>';
        return;
    }

    allUsers.forEach(user => {
        console.log()
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${user.firstName} ${user.lastName}</td>
            <td>${user.email}</td>
            <td>${user.userType}</td>
            <td>${user.created ? new Date(user.created).toLocaleDateString() : 'No records'}</td>
            `;
        tableBody.appendChild(row);
    });
}

applyFilterButton.addEventListener('click', () => {
    const email = emailInput.value.trim();
    const userType = userTypeOption.value;
    loadUsers({ email, userType });
});

document.addEventListener('DOMContentLoaded', () => {
    loadUsers();
});