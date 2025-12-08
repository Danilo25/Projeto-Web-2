package br.com.ufrn.imd.Project_Manager.controller.api;

import br.com.ufrn.imd.Project_Manager.dtos.api.ClientRequest;
import br.com.ufrn.imd.Project_Manager.dtos.api.ClientResponse;
import br.com.ufrn.imd.Project_Manager.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clients")
public class ClientApiController {

    @Autowired
    private ClientService clientService;

    @GetMapping
    @Operation(summary = "Lista ou pesquisa clientes por nome, empresa ou email",
            description = "Retorna uma lista paginada de todos os clientes ou filtra por nome, empresa ou email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de clientes retornada com sucesso")
    })
    public ResponseEntity<Page<ClientResponse>> getClients(
            @Parameter(description = "Nome parcial para filtrar clientes (opcional)") @RequestParam(required = false) String name,
            @Parameter(description = "Empresa parcial para filtrar clientes (opcional)") @RequestParam(required = false) String company,
            @Parameter(description = "Email parcial para filtrar clientes (opcional)") @RequestParam(required = false) String email,
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        Page<ClientResponse> clientsPage = clientService.searchClients(name, company, email, pageable);
        return ResponseEntity.ok(clientsPage);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um cliente por ID", description = "Retorna os detalhes de um cliente específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente encontrado",
                    content = @Content(schema = @Schema(implementation = ClientResponse.class))),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
     })
    public ResponseEntity<?> getClientById(
            @Parameter(description = "ID do cliente a ser buscado", required = true) @PathVariable Long id) {
        try {
            ClientResponse client = clientService.getClientById(id);
            return ResponseEntity.ok(client);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping
    @Operation(summary = "Cria um novo cliente", description = "Adiciona um novo cliente ao sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cliente criado com sucesso",
                    content = @Content(schema = @Schema(implementation = ClientResponse.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "409", description = "Combinação de nome, empresa e email já existe")
    })
    public ResponseEntity<?> createClient(@RequestBody ClientRequest clientRequest) {
        try {
            ClientResponse createdClient = clientService.createClient(clientRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdClient);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Conflito")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Atualiza um cliente existente", description = "Atualiza os dados de um cliente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso",
                    content = @Content(schema = @Schema(implementation = ClientResponse.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
            @ApiResponse(responseCode = "409", description = "Combinação de nome, empresa e email já existe")
    })
    public ResponseEntity<?> updateClient(
            @Parameter(description = "ID do cliente a ser atualizado", required = true) @PathVariable Long id,
            @RequestBody ClientRequest clientRequest) {
        try {
            ClientResponse updatedClient = clientService.updateClient(id, clientRequest);
            return ResponseEntity.ok(updatedClient);
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Client not found!")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else if (e.getMessage().contains("Conflito")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deleta um cliente", description = "Remove um cliente do sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cliente deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    public ResponseEntity<?> deleteClient(
            @Parameter(description = "ID do cliente a ser deletado", required = true) @PathVariable Long id) {
        try {
            clientService.deleteClient(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
