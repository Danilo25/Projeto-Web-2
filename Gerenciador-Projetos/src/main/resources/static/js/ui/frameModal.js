document.addEventListener("DOMContentLoaded", () => {
    const frameForm = document.getElementById("createFrameForm");

    if (!frameForm) return;

    frameForm.addEventListener("submit", async (event) => {
        event.preventDefault();

        const formData = new FormData(frameForm);
        const data = Object.fromEntries(formData.entries());

        try {
            const response = await fetch("/api/frames", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    name: data.name,
                    projectId: data.projectId
                })
            });

            if (!response.ok) {
                const errorText = await response.text();
                alert("Erro ao criar quadro: " + errorText);
                return;
            }

            const modalElement = bootstrap.Modal.getInstance(document.getElementById("createFrameModal"));
            modalElement.hide();
            frameForm.reset();

            location.reload();
        } catch (error) {
            console.error("Erro:", error);
            alert("Erro ao criar quadro");
        }
    });
});
