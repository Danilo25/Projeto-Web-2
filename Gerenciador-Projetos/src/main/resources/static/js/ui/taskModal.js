import { initializeAssigneeSelector } from './assigneeSelector.js';

document.addEventListener("DOMContentLoaded", () => {
    const taskForm = document.getElementById("createTaskForm");
    const modalElement = document.getElementById("createTaskModal");
    if (!taskForm || !modalElement) return;

    const teamId = taskForm.dataset.teamId;
    
    modalElement.addEventListener('show.bs.modal', () => {
        taskForm.reset(); 
        
        const initialAssignee = null;
        initializeAssigneeSelector('create-task', teamId, initialAssignee); 
    });

    taskForm.addEventListener("submit", async (event) => {
        event.preventDefault();
        const formData = new FormData(taskForm);
        const data = Object.fromEntries(formData.entries());

        try {
            const response = await fetch("/api/tasks", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    name: data.name,
                    description: data.description,
                    frameId: data.frameId,
                    finalDate: data.finalDate || null,
                    initialDate: new Date(),
                    status: data.status,
                    assigneeId: data.assigneeId || null 
                })
            });

            if (!response.ok) {
                const errorText = await response.text();
                alert("Erro ao criar tarefa: " + errorText);
                return;
            }

            const modalInstance = bootstrap.Modal.getInstance(modalElement);
            modalInstance.hide();
            taskForm.reset();
            location.reload();
        } catch (error) {
            console.error("Erro:", error);
            alert("Erro ao criar tarefa");
        }
    });
});