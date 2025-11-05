export function renderProjects(projects, teams) {
    const tbody = document.querySelector('#projectsTable tbody');
    const teamMap = new Map(teams.map(t => [t.id, t.name]));

    if (!projects.length) {
        tbody.innerHTML = `<tr><td colspan="5" class="text-center text-muted">
            Nenhum projeto encontrado.
        </td></tr>`;
        return;
    }

    tbody.innerHTML = projects.map(project => `
        <tr>
            <td>${project.name}</td>
            <td>${project.description || 'Sem descrição.'}</td>
            <td><span class="badge bg-info text-dark">${project.status || 'N/A'}</span></td>
            <td>${project.finalDate ? new Date(project.finalDate).toLocaleDateString('pt-BR') : '-'}</td>
            <td>${teamMap.get(project.teamId) || '—'}</td>
        </tr>
    `).join('');
}

export function renderProjectError(msg) {
    const tbody = document.querySelector('#projectsTable tbody');
    tbody.innerHTML = `<tr><td colspan="5" class="text-danger text-center">${msg}</td></tr>`;
}
