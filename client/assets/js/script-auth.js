async function login() {
    const username = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    try {
        const config = await fetchConfig();
        const response = await fetch(`${config.API_AUTH_URL}/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, password })
        });
        const data = await response.json();
        if (data) { 
            localStorage.setItem('userId', data.id);
            localStorage.setItem('sessionToken', data.token);
            Toastify({
                text: "Login realizado com sucesso",
                duration: 3000
            }).showToast();
            window.location.href = '/index.html';
        } else {
            Toastify({
                text: "Credenciais invalidas",
                duration: 3000
            }).showToast();
        }
    } catch (error) {
        Toastify({
            text: "Login inválido",
            duration: 3000
        }).showToast();
    }
}

async function fetchConfig() {
    const response = await fetch('../config.json');
    return response.json();
}