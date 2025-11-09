document.addEventListener("DOMContentLoaded", () => {
    const taskForm = document.getElementById("createTaskForm");

    if (!taskForm) return;

    taskForm.addEventListener("submit", async (event) => {
        event.preventDefault();

        const formData = new FormData(taskForm);
        const data = Object.fromEntries(formData.entries());

        try {
            const response = await fetch("/api/tasks", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    name: data.name,
                    description: data.description,
                    frameId: data.frameId,
                    finalDate: data.finalDate,
                    initialDate: new Date(),
                    status: data.status
                })
            });

            if (!response.ok) {
                const errorText = await response.text();
                alert("Erro ao criar tarefa: " + errorText);
                return;
            }

            const modalElement = bootstrap.Modal.getInstance(document.getElementById("createTaskModal"));
            modalElement.hide();
            taskForm.reset();

            location.reload();
        } catch (error) {
            console.error("Erro:", error);
            alert("Erro ao criar tarefa");
        }
    });
});
