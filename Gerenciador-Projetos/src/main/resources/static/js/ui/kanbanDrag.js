let draggedTaskId = null;

function dragTask(event) {
    draggedTaskId = event.target.dataset.taskId;
}

function allowDrop(event) {
    event.preventDefault();
}

async function dropTask(event, frameElement) {
    event.preventDefault();

    const frameId = frameElement.dataset.frameId;

    try {
        const response = await fetch(`/api/tasks/${draggedTaskId}`, {
            method: "PATCH",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ frameId })
        });

        if (response.ok) {
            location.reload();
        } else {
            const error = await response.text();
            alert("Erro ao mover tarefa: " + error);
        }
    } catch (e) {
        console.error(e);
        alert("Falha ao mover a tarefa");
    }
}