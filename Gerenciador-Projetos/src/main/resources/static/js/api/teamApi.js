export async function fetchUserTeams(userId) {
    const response = await fetch(`/api/teams?memberId=${userId}`);
    if (!response.ok) throw new Error('Erro ao buscar equipes');
    return await response.json();
}

export async function createTeam(teamData) {
    const response = await fetch('/api/teams', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(teamData)
    });
    if (!response.ok) throw new Error(await response.text());
    return await response.json();
}