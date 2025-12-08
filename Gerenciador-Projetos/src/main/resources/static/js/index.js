const tableBody = document.getElementById('user-table-body');
const paginationControls = document.getElementById('pagination-controls');
const defaultPageSize = 10;

async function fetchUsers(page = 0, size = defaultPageSize, sort = 'name') {
    tableBody.innerHTML = '<tr><td colspan="3" class="text-center">Carregando usuários...</td></tr>';
    paginationControls.innerHTML = '';

    try {
        const apiUrl = `/api/users?page=${page}&size=${size}&sort=${sort},asc`;
        const response = await fetch(apiUrl);

        if (!response.ok) {
            throw new Error(`Erro na API: ${response.statusText}`);
        }

        const userPage = await response.json();
        tableBody.innerHTML = '';

        if (userPage.content && userPage.content.length > 0) {
            userPage.content.forEach(user => {
                const row = tableBody.insertRow();

                const nameCell = row.insertCell();
                nameCell.textContent = user.name || 'N/A';

                const positionCell = row.insertCell();
                positionCell.textContent =
                    user.position
                        ? `${user.position.name} (Nível ${user.position.level})`
                        : 'N/A';

                const actionCell = row.insertCell();
                actionCell.className = 'text-end';
                const loginLink = document.createElement('a');
                loginLink.href = `/web/home/${user.id}`;
                loginLink.className = 'btn btn-success btn-sm';
                loginLink.textContent = 'Login';
                actionCell.appendChild(loginLink);
            });
        } else {
            tableBody.innerHTML = '<tr><td colspan="3" class="text-center">Nenhum usuário encontrado.</td></tr>';
        }

        renderPagination(userPage, size, sort);

    } catch (error) {
        console.error('Erro ao buscar usuários:', error);
        tableBody.innerHTML = `<tr><td colspan="3" class="text-center text-danger">Erro ao carregar usuários.</td></tr>`;
    }
}

function renderPagination(pageData, size, sort) {
     if (pageData.totalPages <= 1) return;
     const prevLi = document.createElement('li');
     prevLi.className = `page-item ${pageData.first ? 'disabled' : ''}`;
     const prevLink = document.createElement('a');
     prevLink.className = 'page-link';
     prevLink.href = '#';
     prevLink.textContent = 'Anterior';
     if (!pageData.first) {
         prevLink.onclick = (e) => { e.preventDefault(); fetchUsers(pageData.number - 1, size, sort); };
     }
     prevLi.appendChild(prevLink);
     paginationControls.appendChild(prevLi);

     for (let i = 0; i < pageData.totalPages; i++) {
         const pageLi = document.createElement('li');
         pageLi.className = `page-item ${i === pageData.number ? 'active' : ''}`;
         const pageLink = document.createElement('a');
         pageLink.className = 'page-link';
         pageLink.href = '#';
         pageLink.textContent = i + 1;
         pageLink.onclick = (e) => { e.preventDefault(); fetchUsers(i, size, sort); };
         pageLi.appendChild(pageLink);
         paginationControls.appendChild(pageLi);
     }
     const nextLi = document.createElement('li');
     nextLi.className = `page-item ${pageData.last ? 'disabled' : ''}`;
     const nextLink = document.createElement('a');
     nextLink.className = 'page-link';
     nextLink.href = '#';
     nextLink.textContent = 'Próximo';
      if (!pageData.last) {
         nextLink.onclick = (e) => { e.preventDefault(); fetchUsers(pageData.number + 1, size, sort); };
     }
     nextLi.appendChild(nextLink);
     paginationControls.appendChild(nextLi);
}

document.addEventListener('DOMContentLoaded', () => {
    fetchUsers();
});