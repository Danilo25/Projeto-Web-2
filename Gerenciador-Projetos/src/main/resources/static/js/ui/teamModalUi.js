import { showAlert } from './showAlert.js';

export function createMemberLi(id, name, position) {
    const newMemberLi = document.createElement('li');
    newMemberLi.className = 'list-group-item d-flex justify-content-between align-items-center';
    newMemberLi.dataset.memberId = id;
    newMemberLi.innerHTML = `
        <span>
            ${name} 
            <span class="text-muted small">(${position || 'Sem cargo'})</span>
        </span>
        <button type="button" class="btn btn-danger btn-sm btn-remove-member">
            &times;
        </button>
    `;
    return newMemberLi;
}

export function addMemberToList(id, name, position) {
    const membersList = document.getElementById('team-members-list');
    const alertPlaceholder = document.getElementById('team-form-alert-placeholder');
    const userId = parseInt(id, 10);

    const alreadyExists = membersList.querySelector(`li[data-member-id="${userId}"]`);

    if (alreadyExists) {
        showAlert(`Usuário ${name} já está na equipe.`, 'warning', alertPlaceholder);
        return;
    }

    const newMemberLi = createMemberLi(id, name, position);
    
    const placeholder = membersList.querySelector('li:not([data-member-id])');
    if (placeholder) {
        placeholder.remove();
    }
    
    membersList.appendChild(newMemberLi);
}

export function initializeMemberSearch() {
    const searchInput = document.getElementById('team-member-search-input');
    const searchResults = document.getElementById('team-member-search-results');
    let searchTimeout;

    if (!searchInput) return;

    searchInput.addEventListener('input', () => {
        clearTimeout(searchTimeout);
        const query = searchInput.value.trim();
        
        if (query.length < 2) {
            searchResults.innerHTML = '';
            return;
        }
        searchResults.innerHTML = '<li class="list-group-item">Buscando...</li>';

        searchTimeout = setTimeout(async () => {
            try {
                const pageSize = 20; 
                const response = await fetch(`/api/users?name=${query}&size=${pageSize}&sort=id,asc`);
                if (!response.ok) throw new Error('Erro ao buscar usuários.');
                
                const userPage = await response.json();
                
                const memberItems = document.querySelectorAll('#team-members-list li[data-member-id]');
                const existingMemberIds = Array.from(memberItems).map(li => 
                    parseInt(li.dataset.memberId, 10)
                );
                const existingMemberIdsSet = new Set(existingMemberIds);

                const filteredUsers = userPage.content.filter(user => 
                    !existingMemberIdsSet.has(user.id)
                );
                
                const usersToShow = filteredUsers.slice(0, 5);


                if (!usersToShow || usersToShow.length === 0) {
                    searchResults.innerHTML = '<li class="list-group-item text-muted">Nenhum usuário encontrado.</li>';
                    return;
                }

                searchResults.innerHTML = usersToShow.map(user => `
                    <button type="button" class="list-group-item list-group-item-action btn-add-member" 
                            data-user-id="${user.id}" 
                            data-user-name="${user.name}" 
                            data-user-position="${user.position || 'Sem cargo'}">
                        ${user.name} <span class="text-muted small">(${user.position || 'Sem cargo'})</span>
                    </button>
                `).join('');

            } catch (error) {
                console.error(error);
                searchResults.innerHTML = '<li class="list-group-item text-danger">Erro ao buscar.</li>';
            }
        }, 300);
    });

    document.addEventListener('click', (e) => {
        if (!searchResults.contains(e.target) && e.target !== searchInput) {
            searchResults.innerHTML = '';
        }
    });

    searchResults.addEventListener('click', (e) => {
        const button = e.target.closest('.btn-add-member');
        if (button) {
            const user = button.dataset;
            addMemberToList(user.userId, user.userName, user.userPosition);
            searchInput.value = '';
            searchResults.innerHTML = '';
        }
    });
}