package br.com.ufrn.imd.Project_Manager.controller.api;

import br.com.ufrn.imd.Project_Manager.dtos.api.AddressRequest;
import br.com.ufrn.imd.Project_Manager.dtos.api.AddressResponse;
import br.com.ufrn.imd.Project_Manager.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

@RestController
@RequestMapping("/api/addresses")
@Tag(name = "Endereços", description = "Endpoints para gerenciamento de endereços dos usuários")
public class AddressApiController {

    @Autowired
    private AddressService addressService;

    @GetMapping
    @Operation(summary = "Lista ou busca endereços",
            description = "Retorna uma lista paginada de endereços, com filtros de logradouro, bairro, cidade, estado e CEP.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de endereços retornada com sucesso")
    })
    public ResponseEntity<Page<AddressResponse>> getAddresses(
            @Parameter(description = "Filtrar por logradouro (parcial)") @RequestParam(required = false) String publicPlace,
            @Parameter(description = "Filtrar por bairro (parcial)") @RequestParam(required = false) String district,
            @Parameter(description = "Filtrar por cidade (parcial)") @RequestParam(required = false) String city,
            @Parameter(description = "Filtrar por estado (parcial)") @RequestParam(required = false) String state,
            @Parameter(description = "Filtrar por CEP (prefixo ou exato)") @RequestParam(required = false) String zipCode,
            @Parameter(description = "Filtrar por ID do usuário associado") @RequestParam(required = false) Long userId,
            @PageableDefault(size = 20, sort = "city") Pageable pageable
    ) {
        Page<AddressResponse> addresses = addressService.searchAddresses(city, state, zipCode, district, publicPlace, userId, pageable);
        return ResponseEntity.ok(addresses);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um endereço por ID", description = "Retorna os detalhes de um endereço específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Endereço encontrado",
                    content = @Content(schema = @Schema(implementation = AddressResponse.class))),
            @ApiResponse(responseCode = "404", description = "Endereço não encontrado")
    })
    public ResponseEntity<?> getAddressById(@Parameter(description = "ID do endereço a ser buscado", required = true) @PathVariable Long id) {
        try {
            AddressResponse address = addressService.getAddressById(id);
            return ResponseEntity.ok().body(address);
        } catch (RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Operation(summary = "Cria um endereço e possibilita a associação a um usuário",
            description = "Cria um novo endereço e o associa a um usuário pelo ID fornecido opcionalmente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Endereço criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados fornecidos inválidos"),
            @ApiResponse(responseCode = "409", description = "Conflito (usuário já possui endereço OU endereço duplicado)"),
            @ApiResponse(responseCode = "500", description = "Erro inesperado ao criar endereço")
    })
    public ResponseEntity<?> createAddress(@RequestBody AddressRequest addressRequest) {
        try {
            AddressResponse newAddress = addressService.createAddress(addressRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(newAddress);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("não encontrado")) {
                return ResponseEntity.badRequest().body(e.getMessage());
            } else if (e.getMessage().contains("Conflito")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro inesperado.");
        }
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Atualiza um endereço", description = "Atualiza os dados do endereço existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Endereço atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados fornecidos inválidos"),
            @ApiResponse(responseCode = "404", description = "Endereço com ID fornecido não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro inesperado ao atualizar endereço")

    })
    public ResponseEntity<?> updateAddress(
            @Parameter(description = "ID do endereço a ser atualizado", required = true) @PathVariable Long id,
            @RequestBody AddressRequest addressRequest) {
        try {
            AddressResponse updatedAddress = addressService.updateAddress(id, addressRequest);
            return ResponseEntity.ok(updatedAddress);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Endereço com ID")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            if (e.getMessage().contains("Usuário com ID")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro inesperado ao atualizar endereço.");
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um endereço", description = "Remove o endereço associado ao usuário pelo ID fornecido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Endereço deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Endereço não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro inesperado ao deletar endereço")
    })
    public ResponseEntity<?> deleteAddress(
            @Parameter(description = "ID do endereço a ser deletado", required = true) @PathVariable Long id) {
        try {
            addressService.deleteAddress(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            if (e.getMessage().toLowerCase().contains("não encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro inesperado ao deletar endereço.");
        }
    }
}