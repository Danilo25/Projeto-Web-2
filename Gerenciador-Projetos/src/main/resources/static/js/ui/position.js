class PositionAPI {
    constructor(baseUrl = "/api/positions") {
        this.baseUrl = baseUrl;
    }

    async list(text = "", page = 0, size = 50) {
        const url = `${this.baseUrl}?text=${text}&page=${page}&size=${size}`;
        const response = await fetch(url);

        if (!response.ok) throw new Error("Erro ao buscar cargos");
        return await response.json();
    }

    async create(position) {
        const response = await fetch(this.baseUrl, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(position)
        });

        if (!response.ok) {
            const msg = await response.text();
            throw new Error(msg || "Erro ao criar cargo");
        }

        return await response.json();
    }
}

class PositionManager {
    constructor(api, dropdownSelector, modalSelectors) {
        this.api = api;
        this.dropdown = document.querySelector(dropdownSelector);

        this.inputName = document.querySelector(modalSelectors.name);
        this.inputLevel = document.querySelector(modalSelectors.level);
        this.inputDesc = document.querySelector(modalSelectors.desc);

        this.saveButton = document.querySelector(modalSelectors.saveButton);

        this.modal = new bootstrap.Modal(document.getElementById(modalSelectors.modal));

        this.registerEvents();
        this.loadDropdown();
    }

    async loadDropdown() {
        try {
            const data = await this.api.list();
            const positions = data.content || [];

            this.dropdown.innerHTML = `<option value="">Selecione um cargo</option>`;

            positions.forEach(pos => {
                const option = document.createElement("option");
                option.value = pos.id;
                option.textContent = `${pos.name} - Nível ${pos.level}`;
                this.dropdown.appendChild(option);
            });

        } catch (err) {
            console.error(err);
            alert("Erro ao carregar cargos.");
        }
    }

    registerEvents() {
        this.saveButton.addEventListener("click", () => this.savePosition());
    }

    async savePosition() {
        const newPosition = {
            name: this.inputName.value.trim(),
            level: this.inputLevel.value.trim(),
            description: this.inputDesc.value.trim()
        };

        if (!newPosition.name || !newPosition.level) {
            alert("Nome e nível são obrigatórios!");
            return;
        }

        try {
            await this.api.create(newPosition);

            await this.loadDropdown();

            document.activeElement.blur();

            this.modal.hide();

            this.inputName.value = "";
            this.inputLevel.value = "";
            this.inputDesc.value = "";

            alert("Cargo criado com sucesso!");

        } catch (err) {
            alert("Erro: " + err.message);
        }
    }

}

document.addEventListener("DOMContentLoaded", () => {
    const api = new PositionAPI();

    new PositionManager(api,
        "#positionSelect",
        {
            modal: "addCargoModal",
            name: "#cargoName",
            level: "#cargoLevel",
            desc: "#cargoDescription",
            saveButton: "#saveCargoBtn"
        }
    );
});
