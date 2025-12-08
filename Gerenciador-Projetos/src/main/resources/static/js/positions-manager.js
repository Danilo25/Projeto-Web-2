import { showAlert } from './ui/showAlert.js';

const tableBody = document.getElementById('positionsTableBody');
const paginationContainer = document.getElementById('pagination');
const searchInput = document.getElementById('searchInput');
const btnSearch = document.getElementById('btnSearch');

const cargoModalEl = document.getElementById('addCargoModal');
const cargoModal = new bootstrap.Modal(cargoModalEl);
const btnSave = document.getElementById('saveCargoBtn');
const btnOpenCreate = document.getElementById('btnOpenCreateModal');

const inputId = document.getElementById('cargoId');
const inputName = document.getElementById('cargoName');
const inputLevel = document.getElementById('cargoLevel');
const inputDesc = document.getElementById('cargoDescription');
const modalTitle = document.getElementById('cargoModalTitle');

let currentPage = 0;

async function fetchPositions(page = 0, text = '') {
    try {
        const response = await fetch(`/api/positions?page=${page}&text=${text}&sort=name,asc`);
        if (!response.ok) throw new Error("Erro ao buscar cargos");
        const data = await response.json();
        renderTable(data.content);
        renderPagination(data);
    } catch (error) {
        console.error(error);
        tableBody.innerHTML = `<tr><td colspan="4" class="text-center text-danger">Erro ao carregar dados.</td></tr>`;
    }
}

function renderTable(positions) {
    if (!positions || positions.length === 0) {
        tableBody.innerHTML = `<tr><td colspan="4" class="text-center text-muted">Nenhum cargo encontrado.</td></tr>`;
        return;
    }

    tableBody.innerHTML = positions.map(pos => `
        <tr>
            <td class="ps-4 fw-semibold">${pos.name}</td>
            <td><span class="badge bg-secondary">${pos.level}</span></td>
            <td class="text-muted small">${pos.description || '-'}</td>
            <td class="text-end pe-4">
                <button class="btn btn-sm btn-outline-primary me-2 btn-edit" 
                        data-id="${pos.id}" 
                        data-name="${pos.name}" 
                        data-level="${pos.level}" 
                        data-desc="${pos.description || ''}">
                    <i class="bi bi-pencil"></i>
                </button>
                <button class="btn btn-sm btn-outline-danger btn-delete" data-id="${pos.id}">
                    <i class="bi bi-trash"></i>
                </button>
            </td>
        </tr>
    `).join('');
}

function renderPagination(data) {
    let html = '';

    html += `<li class="page-item ${data.first ? 'disabled' : ''}">
                <a class="page-link" href="#" onclick="window.changePage(${data.number - 1})">Anterior</a>
             </li>`;

    for(let i=0; i < data.totalPages; i++) {
        html += `<li class="page-item ${data.number === i ? 'active' : ''}">
                    <a class="page-link" href="#" onclick="window.changePage(${i})">${i + 1}</a>
                 </li>`;
    }

    html += `<li class="page-item ${data.last ? 'disabled' : ''}">
                <a class="page-link" href="#" onclick="window.changePage(${data.number + 1})">Próximo</a>
             </li>`;

    paginationContainer.innerHTML = html;
}


function openModal(isEdit = false, data = null) {
    if (isEdit && data) {
        modalTitle.textContent = "Editar Cargo";
        inputId.value = data.id;
        inputName.value = data.name;
        inputLevel.value = data.level;
        inputDesc.value = data.desc;
    } else {
        modalTitle.textContent = "Novo Cargo";
        inputId.value = "";
        inputName.value = "";
        inputLevel.value = "";
        inputDesc.value = "";
    }
    cargoModal.show();
}

async function savePosition() {
    const id = inputId.value;
    const body = {
        name: inputName.value,
        level: inputLevel.value,
        description: inputDesc.value
    };

    if(!body.name || !body.level) {
        alert("Nome e Nível são obrigatórios");
        return;
    }

    const method = id ? 'PATCH' : 'POST';
    const url = id ? `/api/positions/${id}` : '/api/positions';

    try {
        const response = await fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body)
        });

        if (!response.ok) {
            const err = await response.text();
            throw new Error(err);
        }
        cargoModal.hide();
        fetchPositions(currentPage, searchInput.value);
        showToast("Sucesso", "Cargo salvo com sucesso!", "success");

    } catch (error) {
        alert("Erro ao salvar: " + error.message);
    }
}


async function deletePosition(id) {
    if(!confirm("Tem certeza que deseja excluir este cargo? Usuários vinculados podem ficar sem cargo.")) return;

    try {
        const response = await fetch(`/api/positions/${id}`, { method: 'DELETE' });
        if(!response.ok) throw new Error("Erro ao excluir");
        showToast("Sucesso", "Cargo excluído.", "success");
        fetchPositions(currentPage, searchInput.value);
    } catch (error) {
        alert("Erro ao excluir cargo: " + error.message);
    }
}

function showToast(title, message, type) {
    const toastEl = document.getElementById('liveToast');
    document.getElementById('toastTitle').innerText = title;
    document.getElementById('toastBody').innerText = message;

    const header = toastEl.querySelector('.toast-header');
    if(type === 'success') header.classList.add('bg-success', 'text-white');
    else header.classList.remove('bg-success', 'text-white');
    const toast = new bootstrap.Toast(toastEl);
    toast.show();
}

window.changePage = (page) => {
    if (page >= 0) {
        currentPage = page;
        fetchPositions(page, searchInput.value);
    }
};

document.addEventListener('DOMContentLoaded', () => {
    fetchPositions();
    btnSearch.addEventListener('click', () => {
        currentPage = 0;
        fetchPositions(0, searchInput.value);
    });

    btnOpenCreate.addEventListener('click', () => openModal(false));
    btnSave.addEventListener('click', savePosition);
    tableBody.addEventListener('click', (e) => {
        const btnEdit = e.target.closest('.btn-edit');
        const btnDelete = e.target.closest('.btn-delete');

        if (btnEdit) {
            openModal(true, btnEdit.dataset);
        } else if (btnDelete) {
            deletePosition(btnDelete.dataset.id);
        }
    });
});