export function showAlert(message, type = 'danger', element = null) {
    const alertPlaceholder = element || document.getElementById('alert-placeholder');
    if (!alertPlaceholder) return;

    const wrapper = document.createElement('div');
    wrapper.innerHTML = `
        <div class="alert alert-${type} alert-dismissible" role="alert">
            <div>${message}</div>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    `;
    alertPlaceholder.innerHTML = '';
    alertPlaceholder.append(wrapper);
}