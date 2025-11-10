import { initializeAssigneeSelector } from './assigneeSelector.js';

document.addEventListener("DOMContentLoaded", () => {
    const modal = document.getElementById("taskDetailsModal");
    const form = document.getElementById("taskDetailsForm");

    if (!modal || !form) return;
    
    window.openTaskDetails = async (cardElement, userId) => {
        const taskId = cardElement.dataset.taskId;
        const teamId = cardElement.dataset.teamId;
        
        try {
            const response = await fetch(`/api/tasks/${taskId}`);
            if (!response.ok) return alert("Erro ao carregar detalhes da tarefa");

            const task = await response.json();

            form.querySelector("#task-id").value = task.id;
            form.querySelector("#task-name").value = task.name || "";
            form.querySelector("#task-description").value = task.description || "";
            form.querySelector("#task-initialDate").value = task.initialDate || "";
            form.querySelector("#task-finalDate").value = task.finalDate || "";
            form.querySelector("#task-status").value = task.status || "PENDENTE";
            form.querySelector("#task-assignee-id").value = task.assigneeId || "";

            initializeTaskTagManagement(taskId);
            initializeTaskCommentManagement(taskId, userId);
            initializeAssigneeSelector('task', teamId, {
                id: task.assigneeId,
                name: task.assigneeName
            });

            const modalInstance = new bootstrap.Modal(modal);
            modalInstance.show();
        } catch (error) {
            console.error("Erro ao abrir detalhes:", error);
        }
    };

    form.addEventListener("submit", async (e) => {
        e.preventDefault();
        const formData = new FormData(form);
        const data = Object.fromEntries(formData.entries());

        const response = await fetch(`/api/tasks/${data.id}`, {
            method: "PATCH",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(data)
        });

        if (response.ok) {
            alert("Tarefa atualizada!");
            location.reload();
        } else {
            alert("Erro ao salvar alterações");
        }
    });
});

window.initializeTaskTagManagement = function (taskId) {
    const tagListContainer = document.getElementById('task-tag-list');
    const tagInput = document.getElementById('task-tag-input');
    const addTagBtn = document.getElementById('task-add-tag-btn');
    const createTagBtn = document.getElementById('task-create-tag-btn');

    if (!tagListContainer || !tagInput || !addTagBtn || !createTagBtn) {
        console.warn("Elementos de tag não encontrados.");
        return;
    }

    async function loadTags() {
        try {
            const response = await fetch(`/api/tags/task/${taskId}`);
            if (!response.ok) {
                tagListContainer.innerHTML = `<span class="text-muted small">Nenhuma tag associada</span>`;
                return;
            }

            const tags = await response.json();

            tagListContainer.innerHTML = tags.length > 0
                ? tags.map(tag => `
                    <span class="badge bg-primary text-white me-1 mb-1 d-inline-flex align-items-center">
                        ${tag.name}
                        <button type="button" class="btn btn-sm btn-light ms-2 py-0 px-1 remove-tag-btn" data-tag-id="${tag.id}">
                            ✕
                        </button>
                    </span>
                `).join('')
                : `<span class="text-muted small">Nenhuma tag associada</span>`;
        } catch (error) {
            console.error("Erro ao carregar tags:", error);
            tagListContainer.innerHTML = `<span class="text-danger small">Erro ao carregar tags</span>`;
        }
    }

    createTagBtn.onclick = async () => {
        const tagName = tagInput.value.trim();
        if (!tagName) return alert("Informe um nome para a tag.");

        try {
            const response = await fetch(`/api/tags`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ name: tagName })
            });

            if (!response.ok) {
                const err = await response.text();
                alert("Erro ao criar tag: " + err);
                return;
            }

            const tagText = await response.text();
            const match = tagText.match(/name=([^\]]+)/);
            const name = match ? match[1].trim() : "desconhecida";

            alert(`Tag "${name}" criada com sucesso!`);
        } catch (error) {
            console.error("Erro ao criar tag:", error);
            alert("Erro inesperado ao criar tag.");
        }
    };

    addTagBtn.onclick = async () => {
        const tagName = tagInput.value.trim();
        if (!tagName) return alert("Informe o nome da tag a adicionar.");

        try {
            const searchResponse = await fetch(`/api/tags?name=${encodeURIComponent(tagName)}`);
            if (!searchResponse.ok) throw new Error('Tag não encontrada.');

            const tagPage = await searchResponse.json();
            if (!tagPage.content || tagPage.content.length === 0) {
                alert(`A tag "${tagName}" não foi encontrada. Crie-a primeiro usando o botão 'Criar Etiqueta'.`);
                return;
            }

            const tagToAdd = tagPage.content[0];
            const response = await fetch(`/api/tags/add-to-task?tagId=${tagToAdd.id}&taskId=${taskId}`, {
                method: "POST",
                headers: { "Content-Type": "application/json" }
            });

            if (!response.ok) {
                const err = await response.text();
                alert("Erro ao adicionar tag: " + err);
                return;
            }

            tagInput.value = "";
            await loadTags();
        } catch (error) {
            console.error("Erro ao adicionar tag:", error);
            alert(error.message);
        }
    };

    tagListContainer.onclick = async (e) => {
        const removeBtn = e.target.closest(".remove-tag-btn");
        if (!removeBtn) return;

        const tagId = removeBtn.dataset.tagId;
        if (!confirm("Tem certeza que deseja remover esta tag?")) return;

        try {
            const response = await fetch(`/api/tags/remove-from-task?tagId=${tagId}&taskId=${taskId}`, { method: "DELETE" });

            if (response.ok) {
                await loadTags();
            } else {
                const err = await response.text();
                alert("Erro ao remover tag: " + err);
            }
        } catch (error) {
            console.error("Erro ao remover tag:", error);
        }
    };

    loadTags();
};


window.initializeTaskCommentManagement = function (taskId, globalUserID) {
    const commentListContainer = document.getElementById("task-comments");
    const commentInput = document.getElementById("new-comment-input");
    const addCommentBtn = document.getElementById("btn-add-comment");

    if (!commentListContainer || !commentInput || !addCommentBtn) {
        console.warn("Elementos de comentário não encontrados.");
        return;
    }

    async function loadComments() {
        try {
            const response = await fetch(`/api/comments?taskId=${taskId}`);
            if (!response.ok || response.status === 204) {
                commentListContainer.innerHTML = `<p class="text-muted small">Sem comentários ainda.</p>`;
                return;
            }

            const commentsPage = await response.json();
            const comments = commentsPage.content;
            if (!comments || comments.length === 0) {
                commentListContainer.innerHTML = `<p class="text-muted small">Sem comentários ainda.</p>`;
                return;
            }

            const commentsHTML = await Promise.all(
                comments.map(async (c) => {
                    try {
                        const userResponse = await fetch(`/api/users/${c.userId}`);
                        const user = userResponse.ok ? await userResponse.json() : { name: "Usuário desconhecido" };

                        return `
                        <div class="border rounded p-2 mb-2 position-relative">
                            <button type="button"
                                    class="btn-close position-absolute top-0 end-0 small delete-comment-btn"
                                    data-comment-id="${c.commentId}"
                                    aria-label="Excluir"></button>
                            <small class="fw-semibold">${user.name}</small><br>
                            <span>${c.text}</span><br>
                            <small class="text-muted">${new Date(c.createdAt).toLocaleString()}</small>
                        </div>
                    `;
                    } catch (err) {
                        console.error("Erro ao buscar usuário:", err);
                        return `
                        <div class="border rounded p-2 mb-2 position-relative">
                            <small class="fw-semibold text-muted">Usuário desconhecido</small><br>
                            <span>${c.text}</span><br>
                            <small class="text-muted">${new Date(c.createdAt).toLocaleString()}</small>
                        </div>
                    `;
                    }
                })
            );

            commentListContainer.innerHTML = commentsHTML.join("");
        } catch (error) {
            console.error("Erro ao carregar comentários:", error);
            commentListContainer.innerHTML = `<p class="text-danger small">Erro ao carregar comentários.</p>`;
        }
    }


    addCommentBtn.onclick = async () => {
        const text = commentInput.value.trim();
        if (!text) return alert("Digite um comentário antes de enviar.");
        const createdAt = new Date().toISOString();

        try {
            const response = await fetch(`/api/comments`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    text: text,
                    createdAt: createdAt,
                    userId: userId,
                    taskId: taskId
                })
            });

            if (!response.ok) {
                const err = await response.text();
                alert("Erro ao criar comentário: " + err);
                return;
            }

            commentInput.value = "";
            await loadComments();
        } catch (error) {
            console.error("Erro ao criar comentário:", error);
        }
    };

    commentListContainer.onclick = async (e) => {
        const deleteBtn = e.target.closest(".delete-comment-btn");
        if (!deleteBtn) return;

        const commentId = deleteBtn.dataset.commentId;
        if (!confirm("Tem certeza que deseja excluir este comentário?")) return;

        try {
            const response = await fetch(`/api/comments/${commentId}`, {
                method: "DELETE"
            });

            if (response.ok) {
                await loadComments();
            } else {
                alert("Erro ao excluir comentário.");
            }
        } catch (error) {
            console.error("Erro ao excluir comentário:", error);
        }
    };

    loadComments();
};