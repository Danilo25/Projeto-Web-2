import { showAlert } from './ui/showAlert.js';
import { initializeMemberSearch, addMemberToList, createMemberLi } from './ui/teamModalUi.js';

document.addEventListener('DOMContentLoaded', async () => {
    const userId = document.getElementById('user-context')?.dataset.userid;
    const teamId = document.getElementById('team-context')?.dataset.teamid;
    const alertPlaceholder = document.getElementById('alert-placeholder');

    if (!userId || !teamId) {
        document.getElementById('team-name').textContent = "Erro: IDs de usuário ou equipe não encontrados.";
        return;
    }

    let teamData = null;
    try {
        teamData = await fetchTeamDetails(teamId);
        renderTeamInfo(teamData);
        
        setupTeamModal(teamData);

    } catch (error) {
        console.error('Erro ao carregar equipe:', error);
        document.getElementById('team-name').textContent = "Erro ao carregar equipe";
        showAlert(error.message, 'danger', alertPlaceholder);
    }

    try {
        const projectsData = await fetchTeamProjects(teamId);
        renderProjectsTable(projectsData);
    } catch (error) {
        console.error('Erro ao carregar projetos:', error);
        showAlert('Erro ao buscar projetos da equipe. (Backend)', 'danger', alertPlaceholder);
        const tbody = document.querySelector('#projectsTable tbody');
        if (tbody) {
            tbody.innerHTML = `<tr><td colspan="4" class="text-center text-danger">Erro ao buscar projetos da equipe.</td></tr>`;
        }
    }
});

async function fetchTeamDetails(teamId) {
    const response = await fetch(`/api/teams/${teamId}`);
    if (!response.ok) {
        throw new Error('Erro ao buscar detalhes da equipe. Verifique se a API está no ar.');
    }
    return await response.json();
}

async function fetchTeamProjects(teamId) {
    const response = await fetch(`/projects/team/${teamId}`); 
    if (!response.ok) {
        throw new Error('Erro ao buscar projetos da equipe.');
    }
    return await response.json();
}

function renderTeamInfo(team) {
    document.getElementById('team-name').textContent = team.name;
    document.getElementById('team-description').textContent = team.description || 'Equipe sem descrição.';
}

function renderProjectsTable(projects) {
    const tbody = document.querySelector('#projectsTable tbody');
    
    if (!projects || projects.length === 0) {
        tbody.innerHTML = `<tr><td colspan="4" class="text-center text-muted">
            Esta equipe ainda não possui projetos.
        </td></tr>`;
        return;
    }

    tbody.innerHTML = projects.map(project => `
        <tr>
            <td>
                <a href="#">${project.name}</a> 
            </td>
            <td>${project.description || 'Sem descrição.'}</td>
            <td><span class="badge bg-info text-dark">${project.status || 'N/A'}</span></td>
            <td>${project.finalDate ? new Date(project.finalDate).toLocaleDateString('pt-BR') : '—'}</td>
        </tr>
    `).join('');
}


function setupTeamModal(team) {
    const teamModalEl = document.getElementById('teamFormModal');
    if (!teamModalEl) return;
    
    const teamModal = new bootstrap.Modal(teamModalEl);

    teamModalEl.addEventListener('show.bs.modal', (event) => {
        if (event.relatedTarget && event.relatedTarget.id === 'editTeamBtn') {
            
            document.getElementById('teamFormModalLabel').textContent = 'Gerenciar Equipe';
            document.getElementById('save-team-btn').textContent = 'Salvar Alterações';
            document.getElementById('team-form-alert-placeholder').innerHTML = '';
            
            document.getElementById('teamId').value = team.id;
            document.getElementById('teamName').value = team.name;
            document.getElementById('teamDescription').value = team.description || '';
            
            loadAndRenderMembers(team.userIds);
            
            document.getElementById('team-form').onsubmit = (e) => handleEditTeamSubmit(e, team.id, teamModal);
        }
    });

    initializeMemberSearch();
    initializeMemberRemoval(); 
}

async function loadAndRenderMembers(userIds) {
    const membersList = document.getElementById('team-members-list');
    if (!userIds || userIds.length === 0) {
        membersList.innerHTML = '<li class="list-group-item">Nenhum membro nesta equipe.</li>';
        return;
    }

    membersList.innerHTML = '<li class="list-group-item">Carregando membros...</li>';
    
    try {
        const userPromises = userIds.map(id => 
            fetch(`/api/users/${id}`).then(res => res.json())
        );
        const members = await Promise.all(userPromises);

        membersList.innerHTML = ''; 
        members.forEach(member => {
            const memberLi = createMemberLi(member.id, member.name, member.position);
            membersList.appendChild(memberLi);
        });

    } catch (error) {
        console.error('Erro ao buscar detalhes dos membros:', error);
        membersList.innerHTML = '<li class="list-group-item text-danger">Erro ao carregar membros.</li>';
    }
}

async function handleEditTeamSubmit(event, teamId, modalInstance) {
    event.preventDefault();
    const alertPlaceholder = document.getElementById('team-form-alert-placeholder');
    alertPlaceholder.innerHTML = '';

    const memberItems = document.querySelectorAll('#team-members-list li[data-member-id]');
    const currentMemberIds = Array.from(memberItems).map(li => 
        parseInt(li.dataset.memberId, 10)
    );

    const teamRequest = {
        name: document.getElementById('teamName').value,
        description: document.getElementById('teamDescription').value,
        userIds: currentMemberIds
    };

    try {
        const response = await fetch(`/api/teams/${teamId}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(teamRequest)
        });

        if (!response.ok) {
            throw new Error(await response.text() || 'Erro ao salvar equipe.');
        }

        renderTeamInfo(teamRequest); 
        modalInstance.hide();
        showAlert('Equipe atualizada com sucesso!', 'success', document.getElementById('alert-placeholder'));

    } catch (error) {
        console.error('Erro ao atualizar equipe:', error);
        showAlert(error.message, 'danger', alertPlaceholder);
    }
}

function initializeMemberRemoval() {
    const membersList = document.getElementById('team-members-list');
    
    membersList.addEventListener('click', (e) => {
        const removeButton = e.target.closest('.btn-remove-member');
        if (removeButton) {
            const memberItem = removeButton.closest('li[data-member-id]');
            memberItem.remove();
            
            if (membersList.children.length === 0) {
                membersList.innerHTML = '<li class="list-group-item">Nenhum membro nesta equipe.</li>';
            }
        }
    });
}