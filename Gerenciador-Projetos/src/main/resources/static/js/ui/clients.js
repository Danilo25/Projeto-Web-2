const clientsTableBody = document.querySelector('#clientsTable tbody');
const clientForm = document.getElementById('create-client-form');
const clientModalAlert = document.getElementById('client-modal-alert');
const clientNameInput = document.getElementById('client-name');
const clientCompanyInput = document.getElementById('client-company');
const clientEmailInput = document.getElementById('client-email');
const clientPhoneInput = document.getElementById('client-phone');
const createClientModal = new bootstrap.Modal(document.getElementById('createClientModal'));

function clearClientAlert() {
    clientModalAlert.innerHTML = '';
}

function showClientAlert(message, type = 'success') {
    clientModalAlert.innerHTML = `<div class="alert alert-${type}" role="alert">${message}</div>`;
}

async function loadClients() {
    try {
        const res = await fetch('/api/clients');
        const data = await res.json();
        const clients = data.content || [];

        if (clients.length === 0) {
            clientsTableBody.innerHTML = `<tr><td colspan="4" class="text-center text-muted">Nenhum cliente cadastrado</td></tr>`;
            return;
        }

        clientsTableBody.innerHTML = clients.map(client => `
            <tr>
                <td>${client.name}</td>
                <td>${client.company}</td>
                <td>${client.email}</td>
                <td>${client.phoneNumber || '-'}</td>
            </tr>
        `).join('');

    } catch (err) {
        clientsTableBody.innerHTML = `<tr><td colspan="4" class="text-center text-danger">Erro ao carregar clientes</td></tr>`;
    }
}

clientForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    clearClientAlert();

    const clientRequest = {
        name: clientNameInput.value.trim(),
        company: clientCompanyInput.value.trim(),
        email: clientEmailInput.value.trim(),
        phoneNumber: clientPhoneInput.value.trim()
    };

    try {
        const res = await fetch('/api/clients', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(clientRequest)
        });

        if (!res.ok) {
            const text = await res.text();
            throw new Error(text || 'Erro ao cadastrar cliente');
        }

        showClientAlert('Cliente cadastrado com sucesso!');
        clientForm.reset();
        createClientModal.hide();
        loadClients();

    } catch (err) {
        showClientAlert(err.message, 'danger');
    }
});

document.addEventListener('DOMContentLoaded', () => {
    loadClients();
});
