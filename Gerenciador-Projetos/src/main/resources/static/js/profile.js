import { fetchUserDetails, updateUser } from './api/userApi.js';
import { fetchUserAddress, saveUserAddress } from './api/addressApi.js';

import { showAlert } from './ui/showAlert.js';


let currentUserData = null;
let currentUserAddress = null;
const userId = document.getElementById('user-context')?.dataset.userid;

const alertPlaceholder = document.getElementById('alert-placeholder');
const userNameTitle = document.getElementById('user-name-title');
const infoName = document.getElementById('info-name');
const infoEmail = document.getElementById('info-email');
const infoPosition = document.getElementById('info-position');

const addressCardBody = document.getElementById('address-card-body');
const btnEditAddress = document.getElementById('btn-edit-address');

const editUserModalEl = document.getElementById('editUserModal');
const editUserModal = new bootstrap.Modal(editUserModalEl);
const editUserForm = document.getElementById('edit-user-form');
const userModalAlert = document.getElementById('user-modal-alert');
const editNameInput = document.getElementById('edit-name');
const editEmailInput = document.getElementById('edit-email');
const editPositionInput = document.getElementById('edit-position');
const editPasswordInput = document.getElementById('edit-password');

const editAddressModalEl = document.getElementById('editAddressModal');
const editAddressModal = new bootstrap.Modal(editAddressModalEl);
const editAddressModalLabel = document.getElementById('editAddressModalLabel');
const editAddressForm = document.getElementById('edit-address-form');
const addressModalAlert = document.getElementById('address-modal-alert');
const editPublicPlaceInput = document.getElementById('edit-publicPlace');
const editDistrictInput = document.getElementById('edit-district');
const editCityInput = document.getElementById('edit-city');
const editStateInput = document.getElementById('edit-state');
const editZipCodeInput = document.getElementById('edit-zipCode');


function clearAlert(element = alertPlaceholder) {
    element.innerHTML = '';
}

async function loadUserProfile() {
    if (!userId) {
        userNameTitle.textContent = "Erro: ID de usuário não encontrado.";
        return;
    }
    try {
        currentUserData = await fetchUserDetails(userId);

        userNameTitle.textContent = `Perfil de ${currentUserData.name}`;
        infoName.textContent = currentUserData.name;
        infoEmail.textContent = currentUserData.email;
        infoPosition.textContent = currentUserData.position || 'N/A';

        editNameInput.value = currentUserData.name;
        editEmailInput.value = currentUserData.email;
        editPositionInput.value = currentUserData.position || '';

    } catch (error) {
        console.error(error);
        showAlert(`Erro ao carregar perfil: ${error.message}`, 'danger', alertPlaceholder);
    }
}

async function loadUserAddress() {
    if (!userId) return;
    
    try {
        currentUserAddress = await fetchUserAddress(userId);
        console.log("Endereço carregado (processado):", currentUserAddress);
        
        if (currentUserAddress === null) {
            addressCardBody.innerHTML = `<p class="text-muted">Nenhum endereço cadastrado.</p>`;
            btnEditAddress.textContent = "Cadastrar Endereço";
            editAddressModalLabel.textContent = "Cadastrar Novo Endereço";
            clearAddressForm();
        } else {
            addressCardBody.innerHTML = `
                <ul class="list-group list-group-flush">
                    <li class="list-group-item"><strong>Logradouro:</strong> ${currentUserAddress.publicPlace || 'N/A'}</li>
                    <li class="list-group-item"><strong>Bairro:</strong> ${currentUserAddress.district || 'N/A'}</li>
                    <li class="list-group-item"><strong>Cidade:</strong> ${currentUserAddress.city || 'N/A'}</li>
                    <li class="list-group-item"><strong>Estado:</strong> ${currentUserAddress.state || 'N/A'}</li>
                    <li class="list-group-item"><strong>CEP:</strong> ${currentUserAddress.zipCode || 'N/A'}</li>
                </ul>
            `;
            fillAddressForm(currentUserAddress);
            btnEditAddress.textContent = "Editar";
            editAddressModalLabel.textContent = "Editar Endereço";
        }

    } catch (error) {
        console.error(error);
        addressCardBody.innerHTML = `<p class="text-danger">Erro ao carregar endereço.</p>`;
    }
}

function fillAddressForm(address) {
    editPublicPlaceInput.value = address.publicPlace || '';
    editDistrictInput.value = address.district || '';
    editCityInput.value = address.city || '';
    editStateInput.value = address.state || '';
    editZipCodeInput.value = address.zipCode || '';
}

function clearAddressForm() {
    editAddressForm.reset();
}

async function handleUserFormSubmit(e) {
    e.preventDefault();
    clearAlert(userModalAlert);
    
    const userRequest = {
        name: editNameInput.value,
        email: editEmailInput.value,
        position: editPositionInput.value,
        password: editPasswordInput.value || null
    };

    try {
        await updateUser(userId, userRequest);
        showAlert('Perfil atualizado com sucesso!', 'success', alertPlaceholder);
        editUserModal.hide();
        editPasswordInput.value = '';
        loadUserProfile();
    } catch (error) {
         showAlert(error.message || 'Erro ao atualizar perfil.', 'danger', userModalAlert);
    }
}

async function handleAddressFormSubmit(e) {
    e.preventDefault();
    clearAlert(addressModalAlert);

    const addressRequest = {
        publicPlace: editPublicPlaceInput.value,
        district: editDistrictInput.value,
        city: editCityInput.value,
        state: editStateInput.value,
        zipCode: editZipCodeInput.value
    };
    
    const isUpdating = currentUserAddress !== null;
    if (isUpdating) {
        addressRequest.id = currentUserAddress.id;
    }

    try {
        await saveUserAddress(userId, addressRequest, isUpdating);
        showAlert('Endereço salvo com sucesso!', 'success', alertPlaceholder);
        editAddressModal.hide();
        loadUserAddress();
    } catch (error) {
        showAlert(error.message || 'Erro ao salvar endereço.', 'danger', addressModalAlert);
    }
}

document.addEventListener('DOMContentLoaded', () => {
    if (!userId) {
        showAlert('Erro fatal: ID do usuário não encontrado. Faça login novamente.', 'danger');
        return;
    }
    
    loadUserProfile();
    loadUserAddress();

    editUserForm.addEventListener('submit', handleUserFormSubmit);
    editAddressForm.addEventListener('submit', handleAddressFormSubmit);
});