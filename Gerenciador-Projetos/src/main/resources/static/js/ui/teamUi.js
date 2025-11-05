export function renderTeamsCarousel(teams) {
    const carouselInner = document.getElementById('teams-carousel-inner');
    const teamSelect = document.getElementById('teamSelect');

    if (!teams || teams.length === 0) {
        carouselInner.innerHTML = `
            <div class="carousel-item active">
                <div class="d-flex justify-content-center p-5">
                    <div class="card">
                        <div class="card-body text-center">
                            <p>Você ainda não faz parte de nenhuma equipe.</p>
                        </div>
                    </div>
                </div>
            </div>`;
        if (teamSelect) teamSelect.innerHTML = `<option value="">Nenhum time disponível</option>`;
        return;
    }

    carouselInner.innerHTML = teams.map((team, i) => `
        <div class="carousel-item ${i === 0 ? 'active' : ''}">
            <div class="d-flex justify-content-center p-5">
                <div class="card">
                    <div class="card-body">
                        <h5 class="card-title">${team.name}</h5>
                        <p class="card-text text-muted">${team.description || 'Sem descrição.'}</p>
                        <a href="/web/teams/edit/${team.id}" class="btn btn-primary btn-sm mt-3">
                            Ver Equipe
                        </a>
                    </div>
                </div>
            </div>
        </div>`).join('');

    if (teamSelect) {
        teamSelect.innerHTML = `
            <option value="" disabled selected>Selecione um time</option>
            ${teams.map(t => `<option value="${t.id}">${t.name}</option>`).join('')}
        `;
    }
}
