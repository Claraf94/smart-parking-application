import { getSpotsOverview, getAllUsers, getCurrentParkedUsers, getAllReservations, getAllNotifications, getParkingTrackHistory, get, loadSpots, getReservableSpots, getUnpaidFines, payFine, createNotification, createFineNotification } from './api-calls.js';

let reservableChartInstance = null;
let regularChartInstance = null;

async function loadAdminDashboard() {
    try {
        const { overall, reservableSpotsOverview, regularSpotsOverview } = await getSpotsOverview();
        const users = await getAllUsers();
        //total of registered users
        document.getElementById('totalUsers').textContent = users.length;
        //total of parking spots
        document.getElementById('overallSpots').textContent = overall;
        //status overview
        getChart('reservableSpotsChart', reservableSpotsOverview, 'reservable');
        getChart('regularSpotsChart', regularSpotsOverview, 'regular');
    } catch (error) {
        console.error('Erro ao carregar dashboard:', error);
    }
}

function getChart(canvasId, data, type) {
    const chartContent = document.getElementById(canvasId).getContext('2d');
    const colorCharts = {
        reservable: {
            EMPTY: 'purple',
            RESERVED: 'orange',
            OCCUPIED: 'red',
            MAINTENANCE: 'yellow'
        },
        regular: {
            EMPTY: 'green',
            OCCUPIED: 'red',
            MAINTENANCE: 'yellow'
        }
    };

    const typeChart = type === 'reservable' ? colorCharts.reservable : colorCharts.regular;
    let selectedData = { ...data };
    if (type === 'regular') {
        // For regular spots, only shows EMPTY, OCCUPIED, and MAINTENANCE
        selectedData = {
            EMPTY: data.EMPTY || 0,
            OCCUPIED: data.OCCUPIED || 0,
            MAINTENANCE: data.MAINTENANCE || 0
        };
    }
    const labels = Object.keys(selectedData);
    const dataset = Object.values(selectedData);
    const backgroundColors = labels.map(label => typeChart[label]);

    if (type === 'reservable' && reservableChartInstance) {
        reservableChartInstance.destroy();
    } else if (type === 'regular' && regularChartInstance) {
        regularChartInstance.destroy();
    }

    const newChart = new Chart(chartContent, {
        type: 'doughnut',
        data: {
            labels: labels,
            datasets: [{
                data: dataset,
                backgroundColor: backgroundColors
            }]
        },
        options: {
            responsive: true,
            plugins: {
                legend: {
                    position: 'bottom'
                },
                title: {
                    display: false
                }
            }
        }
    });
    if (type === 'reservable') {
        reservableChartInstance = newChart;
    } else if (type === 'regular') {
        regularChartInstance = newChart;
    }
}

async function loadTabsContent() {
    await loadCurrentParked();
    await loadReservationsHistory();
    await loadNotificationsHistory();
    await loadParkingHistory();
}

async function loadCurrentParked() {
    let list = await getCurrentParkedUsers();
    const listContainer = document.getElementById('currentParkedList');
    if (!listContainer) return;
    if (list.length === 0) {
        listContainer.innerHTML = `<p class="text-muted">No users parked at this moment.</p>`;
        return;
    }
    //sort the list by check-in date in descending order
    list.sort((a, b) => new Date(b.checkIn) - new Date(a.checkIn));

    listContainer.innerHTML = list.map(item => {
        const userID = item.user?.userID || 'N/A';
        const spotCode = item.spot?.spotCode || 'N/A';
        const checkIn = item.checkIn ? new Date(item.checkIn).toLocaleString() : 'N/A';
        return `<div class="message-box">
                    <div><strong>User ID:</strong> ${userID}</div>
                    <div><strong>Spot:</strong> ${spotCode}</div>
                    <div><strong>Check-in:</strong> ${checkIn}</div>
                </div>`;
    }).join('');
}

async function loadReservationsHistory(userId = '', spotCode = '', status = '') {
    let list = await getAllReservations();
    if (userId) {
        list = list.filter(item => item.user?.userID == userId);
    }
    if (spotCode) {
        list = list.filter(item => (item.spot?.spotCode || '').toLowerCase() === spotCode.toLowerCase());
    }
    if (status) {
        list = list.filter(item => item.reservationStatus === status);
    }
    const listContainer = document.getElementById('reservationsList');
    if (!listContainer) return;
    if (list.length === 0) {
        listContainer.innerHTML = `<p class="text-muted">No reservations found.</p>`;
        return;
    }

    //sort the list by reserved date in descending order
    list.sort((a, b) => new Date(b.reservedAt) - new Date(a.reservedAt));

    listContainer.innerHTML = list.map(item => {
        const userID = item.user?.userID || 'N/A';
        const firstName = item.user?.firstName || '';
        const lastName = item.user?.lastName || '';
        const fullName = `${firstName} ${lastName}`.trim() || 'Unknown User';
        const spotCode = item.spot?.spotCode || 'N/A';
        const statusReservation = item.reservationStatus || 'N/A';
        const reservedAt = item.reservedAt ? new Date(item.reservedAt).toLocaleString() : 'N/A';
        const endedAt = item.endTime ? new Date(item.endTime).toLocaleString() : 'N/A';

        let finalDate = 'N/A';
        if (['CANCELLED', 'FINISHED', 'EXPIRED'].includes(status)) finalDate = endedAt;

        return `<div class="message-box">
                    <div><strong>User:</strong> ${fullName}</div>
                    <div><strong>ID:</strong> ${userID}</div>
                    <div><strong>Spot:</strong> ${spotCode}</div>
                    <div><strong>Status:</strong> ${statusReservation}</div>
                    <div><strong>Reserved at:</strong> ${reservedAt}</div>
                    <div><strong>Ended at:</strong> ${finalDate}</div>
                </div>`;
    }).join('');
}

async function loadNotificationsHistory() {
    const list = await getAllNotifications();
    const listContainer = document.getElementById('notificationList');
    if (!listContainer) return;
    if (list.length === 0) {
        listContainer.innerHTML = `<p class="text-muted">No notifications found.</p>`;
        return;
    }
    //sort the list by sent date in descending order
    list.sort((a, b) => new Date(b.created) - new Date(a.created));

    listContainer.innerHTML = list.map(item => {
        const userID = item.user?.userID || 'N/A';
        const type = item.notificationType || 'N/A';
        const message = item.textMessage || '';
        const createdAt = item.created ? new Date(item.created).toLocaleString() : 'Unknown time';

        return `<div class="message-box">
                    <div><strong>User ID:</strong> ${userID}</div>
                    <div><strong>Type:</strong> ${type}</div>
                    <div>${message}</div>
                    <div><em><small>Sent at: ${createdAt}</small></em></div>
                </div>`;
    }).join('');
}


async function loadParkingHistory(userId = '', spotCode = '') {
    let list = [];
    if (userId && spotCode) {
        list = await get(`/parkingTrack/user/${userId}`);
        list = list.filter(item => (item.spot?.spotCode || item.spot?.spotCode || '').toLowerCase() === spotCode.toLowerCase());
    } else if (userId) {
        list = await get(`/parkingTrack/user/${userId}`);
    } else if (spotCode) {
        list = await get(`/parkingTrack/spots/${spotCode}`);
    } else {
        list = await getParkingTrackHistory();
    }
    const listContainer = document.getElementById('parkingHistoryList');
    if (!listContainer) return;
    if (list.length === 0) {
        listContainer.innerHTML = `<p class="text-muted">No parking history found.</p>`;
        return;
    }

    //sort the list by check-in date in descending order
    list.sort((a, b) => new Date(b.checkIn) - new Date(a.checkIn));

    listContainer.innerHTML = list.map(item => {
        const userID = item.user?.userID || 'N/A';
        const firstName = item.user?.firstName || '';
        const lastName = item.user?.lastName || '';
        const fullName = `${firstName} ${lastName}`.trim() || 'Unknown User';
        const spotCode = item.spots?.spotCode || item.spot?.spotCode || 'N/A';
        const checkIn = item.checkIn ? new Date(item.checkIn).toLocaleString() : 'N/A';
        const checkOut = item.checkOut ? new Date(item.checkOut).toLocaleString() : 'N/A';

        return `<div class="message-box">
                    <div><strong>User:</strong> ${fullName}</div>
                    <div><strong>ID:</strong> ${userID}</div>
                    <div><strong>Spot:</strong> ${spotCode}</div>
                    <div><strong>Check-in:</strong> ${checkIn}</div>
                    <div><strong>Check-out:</strong> ${checkOut}</div>
                </div>`;
    }).join('');
}

async function loadFines(filterStatus = '', userId = '') {
    const finesContainer = document.getElementById('finesList');
    if (!finesContainer) return;

    let fines = [];
    if (filterStatus === 'unpaid') {
        fines = await getUnpaidFines();
    } else {
        const notifications = await getAllNotifications();
        fines = notifications.filter(n => n.notificationType === 'FINE_APPLIED');
        if (filterStatus === 'paid') {
            fines = fines.filter(f => f.isPaid === true);
        }
    }

    if (userId) {
        fines = fines.filter(f => f.user?.userID == userId);
    }
    
    if (Array.isArray(fines)) {
        fines.sort((a, b) => {
            const dateA = a.created ? new Date(a.created) : new Date(0);
            const dateB = b.created ? new Date(b.created) : new Date(0);
            return dateB - dateA;
        });
    }

    if (!Array.isArray(fines) || fines.length === 0) {
        finesContainer.innerHTML = '<p class="text-muted">No fines found.</p>';
        return;
    }

    finesContainer.innerHTML = fines.map(fine => {
        const userIdDisplay = fine.user?.userID || 'N/A';
        const amount = fine.fine || 0;
        const isPaid = !!fine.isPaid;
        // prioridade para o campo camelCase que o backend retorna
        const notificationIdRaw = fine.notificationId ?? fine.notificationID ?? fine.id;
        const notificationId = notificationIdRaw != null ? notificationIdRaw : '';
        if (!notificationId) {
            console.warn('Fine without a recognizable ID (button disabled):', fine);
        }
        const statusText = isPaid ? 'Paid' : 'Unpaid';
        return `<div class="message-box">
                    <div><strong>User ID:</strong> ${userIdDisplay}</div>
                    <div><strong>Amount:</strong> $${amount}</div>
                    <div><strong>Status:</strong> ${statusText}</div>
                    <button class="btn btn-sm btn-success pay-fine-btn" data-id="${notificationId}" ${isPaid || !notificationId ? 'disabled' : ''}>
                        ${isPaid ? 'Paid' : 'Mark as Paid'}
                    </button>
                </div>`;
    }).join('');

    if (finesContainer._fineClickHandler) {
        finesContainer.removeEventListener('click', finesContainer._fineClickHandler);
    }

    function handlePayFineClick(event) {
        const button = event.target.closest('.pay-fine-btn');
        if (!button) return;
        if (button.disabled) return;
        const rawId = button.getAttribute('data-id');
        if (!rawId || rawId === 'undefined') {
            alert('Notification ID not found for this fine.');
            console.error('Invalid ID on button:', rawId, button);
            return;
        }
        const id = !isNaN(rawId) ? parseInt(rawId, 10) : rawId;
        button.disabled = true;
        const previousText = button.textContent;
        button.textContent = 'Processing...';
        payFine(id).then(async () => {
            button.textContent = 'Paid';
            const filterStatusLocal = document.getElementById('fineFilter')?.value;
            const selectedUserId = document.getElementById('fineUserSelect')?.value || '';
            await loadFines(filterStatusLocal, selectedUserId);
        }).catch(async (error) => {
            button.disabled = false;
            button.textContent = previousText || 'Mark as Paid';
            if (error.response) {
                const text = await error.response.text();
                alert('Failed to mark fine as paid: ' + text);
            } else {
                alert('Failed to mark fine as paid: ' + (error.message || 'Unknown error'));
            }
        });
    }

    finesContainer.addEventListener('click', handlePayFineClick);
    finesContainer._fineClickHandler = handlePayFineClick;
}


async function populateFiltersSearch() {
    const users = await getAllUsers();

    //parking filters
    const spots = await loadSpots();
    const userFilterParking = document.getElementById('userFilterParking');
    const spotFilterParking = document.getElementById('spotFilterParking');
    if (userFilterParking && spotFilterParking) {
        userFilterParking.innerHTML = '<option value="">All Users</option>';
        spotFilterParking.innerHTML = '<option value="">All Spots</option>';

        users.forEach(user => {
            userFilterParking.innerHTML += `<option value="${user.userID}">${user.firstName} ${user.lastName} - ID: ${user.userID}</option>`;
        });

        spots.forEach(spot => {
            spotFilterParking.innerHTML += `<option value="${spot.spotCode}">${spot.spotCode} - ${spot.locationDescription || ''}</option>`;
        });
    }

    //reservation filters
    const reservableSpots = await getReservableSpots();
    const userFilterReservation = document.getElementById('userFilterReservation');
    const spotFilterReservation = document.getElementById('spotFilterReservation');
    const statusFilterReservation = document.getElementById('statusFilterReservation');
    if (userFilterReservation && spotFilterReservation && statusFilterReservation) {
        userFilterReservation.innerHTML = '<option value="">All Users</option>';
        spotFilterReservation.innerHTML = '<option value="">All Spots</option>';
        statusFilterReservation.innerHTML = `
          <option value="">All Statuses</option>
          <option value="ACTIVE">Active</option>
          <option value="CANCELLED">Cancelled</option>
          <option value="FINISHED">Finished</option>
          <option value="EXPIRED">Expired</option>
        `;

        users.forEach(user => {
            userFilterReservation.innerHTML += `<option value="${user.userID}">${user.firstName} ${user.lastName} - ID: ${user.userID}</option>`;
        });

        reservableSpots.forEach(spot => {
            spotFilterReservation.innerHTML += `<option value="${spot.spotCode}">${spot.spotCode} - ${spot.locationDescription || ''}</option>`;
        });
    }

    //fine filters
    const fineUserSelect = document.getElementById('fineUserSelect');
    if (fineUserSelect) {
        fineUserSelect.innerHTML = '<option value="">All Users</option>';
        users.forEach(user => {
            fineUserSelect.innerHTML += `<option value="${user.userID}">${user.firstName} ${user.lastName} - ID: ${user.userID}</option>`;
        });
    }
}


async function getUsersForNotification() {
    const users = await getAllUsers();
    const notificationUserSelect = document.getElementById('notificationUserSelect');
    if (!notificationUserSelect) return;
    notificationUserSelect.innerHTML = '<option value="">All Users</option>';
    users.forEach(user => {
        notificationUserSelect.innerHTML += `<option value="${user.userID}">${user.firstName} ${user.lastName} - ID: ${user.userID}</option>`;
    });
}

document.addEventListener('DOMContentLoaded', async () => {
    await loadAdminDashboard();
    await loadCurrentParked();
    await populateFiltersSearch();
    await loadParkingHistory();
    await loadReservationsHistory();
    await loadNotificationsHistory();
    await loadFines();
    await getUsersForNotification();

    const applyParkingFilterBtn = document.getElementById('applyParkingFilterBtn');
    if (applyParkingFilterBtn) {
        applyParkingFilterBtn.addEventListener('click', async () => {
            const userId = document.getElementById('userFilterParking').value;
            const spotCode = document.getElementById('spotFilterParking').value;
            await loadParkingHistory(userId, spotCode);
        });
    }

    const applyReservationFilterBtn = document.getElementById('applyReservationFilterBtn');
    if (applyReservationFilterBtn) {
        applyReservationFilterBtn.addEventListener('click', async () => {
            const userId = document.getElementById('userFilterReservation').value;
            const spotCode = document.getElementById('spotFilterReservation').value;
            const status = document.getElementById('statusFilterReservation').value;
            await loadReservationsHistory(userId, spotCode, status);
        });
    }

    const applyFineFilterBtn = document.getElementById('applyFineFilterBtn');
    if (applyFineFilterBtn) {
        applyFineFilterBtn.addEventListener('click', async () => {
            const filterStatus = document.getElementById('fineFilter').value;
            const userId = document.getElementById('fineUserSelect')?.value || '';
            await loadFines(filterStatus, userId);
        });
    }

    const createNotificationForm = document.getElementById('createNotificationForm');
    if (createNotificationForm) {
        createNotificationForm.addEventListener('submit', async (event) => {
            event.preventDefault();
            const userId = document.getElementById('notificationUserSelect').value;
            const type = document.getElementById('notificationType').value;
            const message = document.getElementById('notificationMessage').value;

            if (!type || !message) {
                alert('Please fill in all fields.');
                return;
            }
            try {
                let response;
                if (type === 'FINE_APPLIED') {
                    if (!userId) {
                        alert('To send a notification related to fines, you must select a user.');
                        return;
                    }
                    response = await createFineNotification({ userID: parseInt(userId) });
                } else {
                    const dataToSend = {
                        notificationType: type,
                        textMessage: message,
                        user: userId ? { userID: parseInt(userId) } : null
                    };
                    response = await createNotification(dataToSend);
                }

                alert('Notification sent successfully!');
                const modalElement = document.getElementById('createNotificationModal');
                const modal = bootstrap.Modal.getInstance(modalElement);
                if (modal) modal.hide();
                createNotificationForm.reset();
                await loadNotificationsHistory();
            } catch (error) {
                alert(`Error creating notification: ${error.message || error}`);
            }
        });
    }

    //update all dashboard data every 10 seconds
    setInterval(async () => {
        await loadAdminDashboard();
        await loadCurrentParked();
    }, 10000);
});


