let teamMembers = [];
let searchTimeout;
async function fetchTeamMembers(teamId) {
    if (teamMembers.length > 0) return;
    if (!teamId) return console.error("AssigneeSelector: ID da Equipe não foi fornecido.");

    try {
        const teamResponse = await fetch(`/api/teams/${teamId}`);
        if (!teamResponse.ok) throw new Error('Erro ao buscar dados da equipe.');
        const team = await teamResponse.json();

        if (!team.userIds || team.userIds.length === 0) {
            teamMembers = [];
            return;
        }
        const userPromises = team.userIds.map(id => 
            fetch(`/api/users/${id}`).then(res => res.ok ? res.json() : null)
        );
        teamMembers = (await Promise.all(userPromises)).filter(user => user != null);
    } catch (error) {
        console.error("Erro ao carregar membros da equipe:", error);
        teamMembers = [];
    }
}

function selectUser(id, name, position, hiddenInput, displayArea, searchInput, searchResults) {
    hiddenInput.value = id;
    displayArea.innerHTML = `
        <span>${name} <span class="text-muted small">(${position || 'Sem cargo'})</span></span>
        <button type="button" class="btn btn-danger btn-sm btn-remove-assignee" style="line-height: 1; padding: 0.25rem 0.5rem;">&times;</button>
    `;
    displayArea.classList.remove('d-none');
    searchInput.style.display = 'none';
    searchResults.innerHTML = '';
}

function resetAssigneeView(hiddenInput, displayArea, searchInput) {
    hiddenInput.value = ''; 
    displayArea.innerHTML = '';
    displayArea.classList.add('d-none');
    searchInput.style.display = 'block';
    searchInput.value = '';
}

export async function initializeAssigneeSelector(idPrefix, teamId, initialAssignee = null) {
    const searchInput = document.getElementById(`${idPrefix}-assignee-search`);
    const searchResults = document.getElementById(`${idPrefix}-assignee-results`);
    const hiddenInput = document.getElementById(`${idPrefix}-assignee-id`);
    const displayArea = document.getElementById(`${idPrefix}-assignee-display`);

    if (!searchInput || !searchResults || !hiddenInput || !displayArea) {
        console.error(`AssigneeSelector: Elementos não encontrados com o prefixo: ${idPrefix}`);
        return;
    }

    await fetchTeamMembers(teamId);
    if (initialAssignee && initialAssignee.id) {
        selectUser(initialAssignee.id, initialAssignee.name, initialAssignee.position, hiddenInput, displayArea, searchInput, searchResults);
    } else {
        resetAssigneeView(hiddenInput, displayArea, searchInput);
        searchInput.placeholder = "Digite um nome para buscar...";
        searchInput.disabled = false;
    }

    searchInput.addEventListener('input', () => {
        clearTimeout(searchTimeout);
        const query = searchInput.value.trim().toLowerCase();
        
        const membersToShow = (query.length === 0)
            ? teamMembers
            : teamMembers.filter(user => user.name.toLowerCase().includes(query));
        
        if (membersToShow.length === 0) {
            searchResults.innerHTML = '<li class="list-group-item text-muted">Nenhum membro encontrado.</li>';
            return;
        }

        searchResults.innerHTML = membersToShow.slice(0, 5).map(user => `
            <button type="button" class="list-group-item list-group-item-action btn-select-assignee" 
                    data-user-id="${user.id}" data-user-name="${user.name}" data-user-position="${user.position || 'Sem cargo'}">
                ${user.name} <span class="text-muted small">(${user.position || 'Sem cargo'})</span>
            </button>
        `).join('');
    });

    searchResults.addEventListener('click', (e) => {
        const button = e.target.closest('.btn-select-assignee');
        if (button) {
            const user = button.dataset;
            selectUser(user.userId, user.userName, user.userPosition, hiddenInput, displayArea, searchInput, searchResults);
        }
    });

    displayArea.addEventListener('click', (e) => {
        if (e.target.closest('.btn-remove-assignee')) {
            resetAssigneeView(hiddenInput, displayArea, searchInput);
            searchInput.focus();
        }
    });
}