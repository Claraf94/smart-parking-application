import { getSpotsOverview, getAllUsers } from './api-calls.js';

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
    let selectedData = {...data};
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

    new Chart(chartContent, {
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
}
document.addEventListener('DOMContentLoaded', async () => {
    await loadAdminDashboard();
});


