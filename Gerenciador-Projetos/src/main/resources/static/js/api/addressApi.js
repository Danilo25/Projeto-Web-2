export async function fetchUserAddress(userId) {
    const response = await fetch(`/api/addresses?userId=${userId}`);
    
    if (response.status === 404) {
        return null;
    }
    if (!response.ok) {
        throw new Error(await response.text() || 'Erro ao buscar endereço');
    }
    const pageData = await response.json();
    if (pageData && pageData.content && pageData.content.length > 0) {
        return pageData.content[0]; 
    } else {
        return null;
    }
}

export async function saveUserAddress(userId, addressData, isUpdating) {
    const method = isUpdating ? 'PUT' : 'POST';
    const requestBody = { ...addressData, userId: userId };
    let url = '/api/addresses';
    if (isUpdating) {
        if (!addressData.id) {
            throw new Error("O ID do endereço (addressData.id) é necessário para atualizar.");
        }
        url = `/api/addresses/${addressData.id}`;
    }
    const response = await fetch(url, {
        method: method,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(requestBody)
    });

    if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || 'Erro ao salvar endereço');
    }
    if (response.status === 204) {
        return null;
    }
    return await response.json();
}