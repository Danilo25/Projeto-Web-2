export async function fetchAllProjects() {
    const response = await fetch('/projects');
    if (!response.ok) throw new Error('Erro ao buscar projetos');
    return await response.json();
}

export async function createProject(project) {
    const response = await fetch('/projects', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(project)
    });
    if (!response.ok) throw new Error(await response.text());
    return await response.json();
}
