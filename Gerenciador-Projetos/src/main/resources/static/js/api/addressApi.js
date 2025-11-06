export async function fetchUserAddress(userId) {
    const response = await fetch(`/api/addresses/user/${userId}`);
    
    if (response.status === 404) {
        return null;
    }
    if (!response.ok) {
        throw new Error(await response.text() || 'Erro ao buscar endereço');
    }
    return await response.json();
}

export async function saveUserAddress(userId, addressData, isUpdating) {
    const method = isUpdating ? 'PUT' : 'POST';
    const response = await fetch(`/api/addresses/user/${userId}`, {
        method: method,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(addressData)
    });

    if (!response.ok && response.status !== 201) {
        throw new Error(await response.text() || 'Erro ao salvar endereço');
    }
    if (response.status !== 204) {
        return await response.json();
    }
    return null;
}