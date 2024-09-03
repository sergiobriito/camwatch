document.addEventListener("DOMContentLoaded", () => {
    checkAuthentication();
    initializePage();
    document.getElementById('userID').textContent = localStorage.getItem('userId') || 'N/A';
    document.getElementById('totalDevices').textContent = localStorage.getItem('totalDevices') || '0';
    setInterval(() => {
        location.reload();
    }, 60 * 1000); 
});

async function initializePage() {
    try {
        const config = await fetchConfig();
        const userId = localStorage.getItem('userId');
        const devices = await fetchDevices(config.API_URL, userId);
        const totalDevices = devices.length;
        populateDeviceTable(devices, config.API_URL);
        localStorage.setItem('totalDevices', totalDevices);
        document.getElementById('totalDevices').textContent = localStorage.getItem('totalDevices') || '0';
    } catch (error) {
        console.error("Error initializing page:", error);
    }
}

async function fetchConfig() {
    const response = await fetch('../config.json');
    if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
    }
    return response.json();
}

async function fetchDevices(apiUrl, userId) {
    checkToken();
    const token = localStorage.getItem('sessionToken');
    const response = await fetch(`${apiUrl}/devices/all/${userId}`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        }
    });
    if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
    }
    return response.json();
}

function populateDeviceTable(devices, apiUrl) {
    const tableBody = document.querySelector("#device-table tbody");
    devices.forEach(device => {
        const row = document.createElement("tr");

        device.forEach((item, index) => {
            const cell = document.createElement("td");
            if (index === 4) {
                const formattedDate = formatDate(new Date(item));
                cell.textContent = formattedDate;
            } else if (index === 6) {
                const deleteButton = document.createElement("button");
                deleteButton.className = "btn btn-danger";
                deleteButton.textContent = "Remover";
                deleteButton.onclick = () => deleteDevice(item, row.id, apiUrl);
                cell.appendChild(deleteButton);
            } else {
                cell.textContent = item;
            }
            styleCell(cell);
            row.appendChild(cell);
        });

        row.id = `row-${device[6]}`;
        tableBody.appendChild(row);
    });
}

function formatDate(date) {
    date.setHours(date.getHours() + 3); 
    const formattedDate = date.toLocaleDateString("en-GB");
    const formattedTime = date.toLocaleTimeString("en-GB", {
        hour: "2-digit",
        minute: "2-digit",
    });
    return `${formattedDate} ${formattedTime}`;
}

function styleCell(cell) {
    const text = cell.textContent.trim();
    if (text === "online") {
        cell.style.color = "green";
    } else if (text === "offline") {
        cell.style.color = "red";
    }
}

function checkAuthentication() {
    const userId = localStorage.getItem('userId');
    if (!userId) {
        window.location.href = 'pages/sign-in.html';
    }
}

async function logout() {
    try {
        const config = await fetchConfig();
        const response = await fetch(`${config.API_AUTH_URL}/logout`, {
            method: 'POST'
        });
        if (response.ok) {  
            localStorage.removeItem('userId');
            localStorage.removeItem('sessionToken');
            localStorage.removeItem('totalDevices');
            window.location.href = 'pages/sign-in.html';
        }
    } catch (error) {
        console.error('Logout failed:', error);
    }
}

function parseJwt(token) {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join(''));

    return JSON.parse(jsonPayload);
}

function isTokenExpired(token) {
    const decodedToken = parseJwt(token);
    const currentTime = Math.floor(Date.now() / 1000);
    return decodedToken.exp < currentTime;
}

function checkToken() {
    const token = localStorage.getItem('sessionToken');
    if (!token) {
        logout();
        return;
    }
    if (isTokenExpired(token)) {
        logout();
    } 
}

function deleteDevice(deviceId, rowId, apiUrl) {
    checkToken();
    fetch(`${apiUrl}/devices/delete/${deviceId}`, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json'
        }
    })
    .then(response => {
        if (response.ok) {
            const row = document.getElementById(rowId);
            if (row) {
                row.remove();
            }
        } else {
            console.error('Failed to delete the device.');
        }
    })
    .catch(error => {
        console.error('Error:', error);
    });
}
