let carouselTrack;
let prevButton;
let nextButton;
let allTeams = [];
let cardWidth = 0;
let currentIndex = 0;
let itemsVisible = 3;

function createTeamCardHtml(team, userId) {
    return `
        <div class="carousel-card-wrapper">
            <div class="card">
                <div class="card-body d-flex flex-column">
                    <h5 class="card-title">${team.name}</h5>
                    <p class="card-text text-muted flex-grow-1">${team.description || 'Sem descrição.'}</p>
                    
                    <a href="/web/user/${userId}/team/${team.id}" class="btn btn-primary btn-sm mt-3 align-self-start">
                        Ver Equipe
                    </a>
                </div>
            </div>
        </div>
    `;
}

function updateCarouselPosition() {
    const newTransform = -currentIndex * cardWidth;
    if (carouselTrack) {
        carouselTrack.style.transform = `translateX(${newTransform}px)`;
    }
    updateButtonState();
}

function updateButtonState() {
    if (!prevButton || !nextButton) return;

    if (prevButton.style.display === 'none') {
        prevButton.disabled = true;
        nextButton.disabled = true;
        return;
    }

    prevButton.disabled = false;
    nextButton.disabled = false;
}

function setupCarouselDimensions() {
    if (window.innerWidth < 768) {
        itemsVisible = 1;
    } else {
        itemsVisible = 3;
    }

    const firstCardWrapper = carouselTrack.querySelector('.carousel-card-wrapper');
    if (firstCardWrapper) {
        cardWidth = firstCardWrapper.offsetWidth;
    }
    
    const lastValidIndex = Math.max(0, allTeams.length - itemsVisible);
    if (currentIndex > lastValidIndex) {
        currentIndex = lastValidIndex;
    }

    updateCarouselPosition();
}

export function renderTeamsCarousel(teams, userId) {
    carouselTrack = document.getElementById('teams-carousel-track');
    const teamSelect = document.getElementById('teamSelect');
    prevButton = document.getElementById('team-carousel-prev');
    nextButton = document.getElementById('team-carousel-next');
    
    allTeams = teams;

    if (!carouselTrack || !prevButton || !nextButton) {
        console.error("Elementos do carrossel não encontrados no DOM.");
        return;
    }

    if (!teams || teams.length === 0) {
        carouselTrack.innerHTML = `
            <div class="d-flex justify-content-center p-5">
                <div class="card" style="width: 22rem;">
                    <div class="card-body text-center">
                        <p>Você ainda não faz parte de nenhuma equipe.</p>
                    </div>
                </div>
            </div>`;
        if (teamSelect) teamSelect.innerHTML = `<option value="">Nenhum time disponível</option>`;
        prevButton.style.display = 'none';
        nextButton.style.display = 'none';
        return;
    }

    
    carouselTrack.innerHTML = teams.map(team => createTeamCardHtml(team, userId)).join('');

    setupCarouselDimensions();

    prevButton.replaceWith(prevButton.cloneNode(true));
    nextButton.replaceWith(nextButton.cloneNode(true));
    
    prevButton = document.getElementById('team-carousel-prev');
    nextButton = document.getElementById('team-carousel-next');

    prevButton.addEventListener('click', () => {
        const lastIndex = Math.max(0, allTeams.length - itemsVisible);

        if (currentIndex > 0) {
            currentIndex--;
        } else {
            currentIndex = lastIndex;
        }
        updateCarouselPosition();
    });

    nextButton.addEventListener('click', () => {
        const lastIndex = Math.max(0, allTeams.length - itemsVisible);

        if (currentIndex < lastIndex) {
            currentIndex++;
        } else {
            currentIndex = 0;
        }
        updateCarouselPosition();
    });

    let resizeTimer;
    window.addEventListener('resize', () => {
        clearTimeout(resizeTimer);
        resizeTimer = setTimeout(() => {
            setupCarouselDimensions();
        }, 250);
    });

    if (teams.length <= itemsVisible) {
        prevButton.style.display = 'none';
        nextButton.style.display = 'none';
    } else {
        prevButton.style.display = 'flex';
        nextButton.style.display = 'flex';
    }
    
    updateButtonState();

    if (teamSelect) {
        teamSelect.innerHTML = `
            <option value="" disabled selected>Selecione um time</option>
            ${teams.map(t => `<option value="${t.id}">${t.name}</option>`).join('')}
        `;
    }
}