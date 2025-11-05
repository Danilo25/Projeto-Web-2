export function renderWelcomeMessage(userData) {
    const welcomeMessageElement = document.getElementById('welcome-message');
    welcomeMessageElement.textContent = `Bem-vindo, ${userData.name || 'usuário'}!`;
}

export function showUserError(message) {
    const welcomeMessageElement = document.getElementById('welcome-message');
    welcomeMessageElement.textContent = message;
}