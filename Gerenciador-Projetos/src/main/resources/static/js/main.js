import { fetchUserDetails } from './api/userApi.js';
import { fetchUserTeams, createTeam } from './api/teamApi.js';
import { fetchAllProjects, createProject } from './api/projectApi.js';
import { fetchClientsByProjects } from './ui/projectUi.js';
import { renderWelcomeMessage, showUserError } from './ui/userUi.js';
import { renderTeamsCarousel } from './ui/teamUi.js';
import { renderProjects } from './ui/projectUi.js';
import { showAlert } from './ui/showAlert.js';
import { initializeMemberSearch, addMemberToList, createMemberLi } from './ui/teamModalUi.js';

document.addEventListener('DOMContentLoaded', async () => {
    const userId = document.getElementById('user-context')?.dataset.userid;

    if (!userId) {
        showUserError('Erro: ID de usuário inválido.');
        return;
    }

    try {
        const user = await fetchUserDetails(userId);
        renderWelcomeMessage(user);

        const teamsPage = await fetchUserTeams(userId); 
        
        const teams = teamsPage.content;
        renderTeamsCarousel(teams, userId);

        const allProjects = await fetchAllProjects();
        const teamIds = new Set(teams.map(t => t.id));
        const userProjects = allProjects.content.filter(p => teamIds.has(p.teamId));

        const clients = await fetchClientsByProjects(userProjects);

        renderProjects(userProjects, teams, userId, clients);


        fillSelectTimes(teams);
        fillSelectClients();

        setupTeamModal(user);

    } catch (e) {
        console.error("ERRO DETALHADO NO CATCH (main.js):", e);
        console.error(e);
        showUserError('Erro ao carregar dados.');
    }
});

function fillSelectTimes(teams) {
    const select = document.getElementById('teamSelect');
    select.innerHTML = '';

    if (!teams.length) {
        select.innerHTML = '<option value="">Nenhum time encontrado</option>';
        return;
    }

    teams.forEach(t => {
        const opt = document.createElement('option');
        opt.value = t.id;
        opt.textContent = t.name;
        select.appendChild(opt);
    });
}

document.getElementById('saveProjectBtn')?.addEventListener('click', async () => {
    const form = document.getElementById('projectForm');
    const formData = new FormData(form);

    const project = {
        name: formData.get('name'),
        description: formData.get('description'),
        initialDate: formData.get('initialDate'),
        finalDate: formData.get('finalDate'),
        status: formData.get('status'),
        teamId: formData.get('teamId'),
        clientId: formData.get('clientId')
    };

    try {
        await createProject(project);
        window.location.reload();
    } catch (e) {
        alert('Erro ao criar projeto: ' + e.message);
    }
});

function setupTeamModal(user) {
    const teamModalEl = document.getElementById('teamFormModal');
    if (!teamModalEl) return;
    
    const teamModal = new bootstrap.Modal(teamModalEl);

    teamModalEl.addEventListener('show.bs.modal', (event) => {
        if (event.relatedTarget && event.relatedTarget.id === 'createTeamBtn') {
            
            document.getElementById('teamFormModalLabel').textContent = 'Criar Nova Equipe';
            document.getElementById('save-team-btn').textContent = 'Salvar Equipe';
            document.getElementById('team-form').reset();
            document.getElementById('teamId').value = '';
            document.getElementById('team-form-alert-placeholder').innerHTML = '';
            
            const membersList = document.getElementById('team-members-list');
            membersList.innerHTML = ''; 

            if (user) {
                const creatorLi = createMemberLi(user.id, user.name, user.position);
                creatorLi.querySelector('.btn-remove-member').remove(); 
                creatorLi.insertAdjacentHTML('beforeend', '<span class="badge bg-primary rounded-pill">Criador</span>');
                membersList.appendChild(creatorLi);
            }
            
            document.getElementById('team-form').onsubmit = (e) => handleCreateTeamSubmit(e, user.id, teamModal);
        }
    });

    initializeMemberSearch();
    initializeMemberRemoval(user.id); 
}

async function handleCreateTeamSubmit(event, creatorId, modalInstance) {
    event.preventDefault();
    const alertPlaceholder = document.getElementById('team-form-alert-placeholder');
    alertPlaceholder.innerHTML = '';

    const name = document.getElementById('teamName').value.trim();
    const description = document.getElementById('teamDescription').value.trim();

    if (!name) {
        showAlert('Por favor, insira um nome para a equipe.', 'warning', alertPlaceholder);
        return;
    }
    
    const memberItems = document.querySelectorAll('#team-members-list li[data-member-id]');
    const memberIds = Array.from(memberItems).map(li => 
        parseInt(li.dataset.memberId, 10)
    );
    const uniqueMemberIds = [...new Set(memberIds)];

    const teamRequest = {
        name,
        description,
        creatorId: creatorId,
        userIds: uniqueMemberIds
    };

    try {
        await createTeam(teamRequest);
        modalInstance.hide();
        window.location.reload(); 
    } catch (e) {
        console.error(e);
        showAlert(e.message || 'Erro ao criar equipe.', 'danger', alertPlaceholder);
    }
}

function initializeMemberRemoval(creatorIdToProtect) {
    const membersList = document.getElementById('team-members-list');
    const alertPlaceholder = document.getElementById('team-form-alert-placeholder');

    membersList.addEventListener('click', (e) => {
        const removeButton = e.target.closest('.btn-remove-member');
        if (removeButton) {
            const memberItem = removeButton.closest('li[data-member-id]');
            const memberId = parseInt(memberItem.dataset.memberId, 10);

            if (creatorIdToProtect && memberId === creatorIdToProtect) {
                showAlert('O criador não pode ser removido da equipe.', 'warning', alertPlaceholder);
                return;
            }
            memberItem.remove();
        }
    });
}

async function fillSelectClients() {
    const select = document.getElementById('clientSelect');
    select.innerHTML = '<option value="">Carregando clientes...</option>';

    try {
        const res = await fetch('/api/clients?page=0&size=100');
        if (!res.ok) throw new Error('Erro ao buscar clientes');

        const data = await res.json();
        const clients = data.content || [];

        if (!clients.length) {
            select.innerHTML = '<option value="">Nenhum cliente encontrado</option>';
            return;
        }

        select.innerHTML = '';
        clients.forEach(client => {
            const opt = document.createElement('option');
            opt.value = client.id;
            opt.textContent = `${client.name} (${client.company})`;
            select.appendChild(opt);
        });

    } catch (e) {
        console.error('Erro ao carregar clientes:', e);
        select.innerHTML = '<option value="">Erro ao carregar clientes</option>';
    }
}
