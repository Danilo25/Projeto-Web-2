export async function fetchUserDetails(userId) {
    const response = await fetch(`/api/users/${userId}`);
    if (!response.ok) throw new Error('Usuário não encontrado');
    return await response.json();
}
