import { fetchUserDetails } from './api/userApi.js';
import { fetchUserTeams, createTeam } from './api/teamApi.js';
import { fetchAllProjects, createProject } from './api/projectApi.js';

import { renderWelcomeMessage, showUserError } from './ui/userUi.js';
import { renderTeamsCarousel } from './ui/teamUi.js';
import { renderProjects } from './ui/projectUi.js';

document.addEventListener('DOMContentLoaded', async () => {
    const userId = document.getElementById('user-context')?.dataset.userid;

    if (!userId || isNaN(userId)) {
        showUserError('Erro: ID de usuário inválido.');
        return;
    }

    try {
        const user = await fetchUserDetails(userId);
        renderWelcomeMessage(user);

        const teams = await fetchUserTeams(userId);
        renderTeamsCarousel(teams);

        const allProjects = await fetchAllProjects();
        const teamIds = new Set(teams.map(t => t.id));
        const userProjects = allProjects.filter(p => teamIds.has(p.teamId));

        renderProjects(userProjects, teams);

        fillSelectTimes(teams);

        document.getElementById('creator-name').textContent = user.name;
    } catch (e) {
        console.error(e);
        showUserError('Erro ao carregar dados.');
    }
});

function fillSelectTimes(teams) {
    const select = document.getElementById('teamSelect');
    select.innerHTML = '';

    if (!teams.length) {
        select.innerHTML = '<option value="">Nenhum time encontrado</option>';
        return;
    }

    teams.forEach(t => {
        const opt = document.createElement('option');
        opt.value = t.id;
        opt.textContent = t.name;
        select.appendChild(opt);
    });
}

document.getElementById('saveProjectBtn')?.addEventListener('click', async () => {
    const form = document.getElementById('projectForm');
    const formData = new FormData(form);

    const project = {
        name: formData.get('name'),
        description: formData.get('description'),
        initialDate: formData.get('initialDate'),
        finalDate: formData.get('finalDate'),
        status: formData.get('status'),
        teamId: formData.get('teamId')
    };

    try {
        await createProject(project);
        alert('Projeto criado com sucesso!');
        window.location.reload();
    } catch (e) {
        alert('Erro ao criar projeto: ' + e.message);
    }
});

document.getElementById('team-form')?.addEventListener('submit', async (event) => {
    event.preventDefault();

    const userId = document.getElementById('user-context')?.dataset.userid;
    const name = document.getElementById('name').value.trim();
    const description = document.getElementById('description').value.trim();

    if (!name) {
        alert('Por favor, insira um nome para a equipe.');
        return;
    }

    const team = {
        name,
        description,
        creatorId: userId,
        members: [{ id: userId }]
    };

    try {
        await createTeam(team);
        alert('Equipe criada com sucesso!');
        window.location.reload();
    } catch (e) {
        console.error(e);
        alert('Erro ao criar equipe: ' + e.message);
    }
});
