import { showAlert } from './ui/showAlert.js';

const form = document.getElementById('register-form');
const alertPlaceholder = document.getElementById('alert-placeholder');

form.addEventListener('submit', async (event) => {
    event.preventDefault();

    const name = document.getElementById('name').value;
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    const positionId = document.getElementById('positionSelect').value;

    const userData = {
        name,
        email,
        password,
        positionId: positionId ? Number(positionId) : null
    };

    try {
        const response = await fetch('/api/users', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(userData)
        });

        if (response.status === 201) {
            showAlert('Usuário cadastrado com sucesso! Redirecionando para login...', 'success', alertPlaceholder);
            form.reset();
            setTimeout(() => {
                window.location.href = '/login'; 
            }, 2000);
        } else {
            const errorMessage = await response.text();
            showAlert(errorMessage || 'Erro ao cadastrar usuário.', 'danger', alertPlaceholder);
        }

    } catch (error) {
        console.error('Erro na requisição:', error);
        showAlert('Erro de conexão. Tente novamente.', 'danger', alertPlaceholder);
    }
});
