export async function fetchClientsByProjects(projects) {
    const uniqueClientIds = [...new Set(projects.map(p => p.clientId))];

    const clientMap = new Map();

    for (const clientId of uniqueClientIds) {
        try {
            const response = await fetch(`/api/clients/${clientId}`);
            if (!response.ok) throw new Error("Cliente não encontrado");

            const client = await response.json();
            clientMap.set(clientId, client.name);

        } catch (err) {
            console.error(`Erro ao buscar cliente ID ${clientId}:`, err);
            clientMap.set(clientId, "—");
        }
    }

    return clientMap;
}

export function renderProjects(projects, teams, userId, clients) {
    const tbody = document.querySelector('#projectsTable tbody');
    const teamMap = new Map(teams.map(t => [t.id, t.name]));

    if (!projects.length) {
        tbody.innerHTML = `
            <tr>
                <td colspan="7" class="text-center text-muted">
                    Nenhum projeto encontrado.
                </td>
            </tr>`;
        return;
    }

    tbody.innerHTML = projects.map(project => `
        <tr>
            <td>${project.name}</td>
            <td>${project.description || 'Sem descrição.'}</td>
            <td><span class="badge bg-info text-dark">${project.status || 'N/A'}</span></td>
            <td>${project.finalDate ? new Date(project.finalDate).toLocaleDateString('pt-BR') : '-'}</td>
            <td>${teamMap.get(project.teamId) || '—'}</td>
            <td>${clients.get(project.clientId) || '—'}</td>
            <td>
                <a href="/web/project/${userId}/${project.id}/board" class="btn btn-sm btn-outline-primary">
                    <i class="bi bi-kanban"></i> Ver Quadro
                </a>
            </td>
        </tr>
    `).join('');
}

export function renderProjectError(msg) {
    const tbody = document.querySelector('#projectsTable tbody');
    tbody.innerHTML = `
        <tr>
            <td colspan="7" class="text-danger text-center">${msg}</td>
        </tr>`;
}
