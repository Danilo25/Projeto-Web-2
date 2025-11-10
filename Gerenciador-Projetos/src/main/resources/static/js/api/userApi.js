export async function fetchUserDetails(userId) {
    const response = await fetch(`/api/users/${userId}`);
    if (!response.ok) throw new Error('Usuário não encontrado');
    return await response.json();
}

export async function updateUser(userId, userData) {
    const response = await fetch(`/api/users/${userId}`, {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(userData)
    });

    if (!response.ok) {
        throw new Error(await response.text() || 'Erro ao atualizar usuário');
    }
    return await response.json();
}