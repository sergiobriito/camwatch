async function register() {
    const username = document.getElementById('email').value;
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    try {
        const config = await fetchConfig();
        const response = await fetch(`${config.API_AUTH_URL}/register`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, email, password })
        }); 
        const data = await response.text();
        const code = await response.status;
        if (code == 201) { 
            Toastify({
                text: "Cadastro realizado com sucesso",
                duration: 3000
            }).showToast();
            setTimeout(function() {
                window.location.href = '/pages/sign-in.html';
            }, 3000);
        }else if (code == 409){
            Toastify({
                text: "Email já cadastrado",
                duration: 3000
            }).showToast();
        }else{
            Toastify({
                text: data,
                duration: 3000
            }).showToast();
        };
    } catch (error) {
        Toastify({
            text: "Falha ao realizar cadastro",
            duration: 3000
        }).showToast();
    }
}

async function fetchConfig() {
    const response = await fetch('../config.json');
    return response.json();
}