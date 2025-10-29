package br.com.ufrn.imd.Project_Manager.controller;

import br.com.ufrn.imd.Project_Manager.dtos.AddressRequest;
import br.com.ufrn.imd.Project_Manager.dtos.AddressResponse;
import br.com.ufrn.imd.Project_Manager.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

    @GetMapping("/user/{userId}")
    @Operation(summary = "Busca endereço pelo ID do usuário", description = "Retorna o endereço associado a um usuário específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Endereço encontrado"),
            @ApiResponse(responseCode = "404", description = "Endereço ou usuário não encontrado")
    })
    public ResponseEntity<?> getAddressByUserId(
            @Parameter(description = "ID do usuário para buscar o endereço", required = true)
            @PathVariable Long userId) {
        try {
            AddressResponse address = addressService.getMyAddress(userId);
            return ResponseEntity.ok(address);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/user/{userId}")
    @Operation(summary = "Cria um endereço para um usuário específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Endereço criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "409", description = "Conflito (usuário já possui endereço OU endereço duplicado)")
    })
    public ResponseEntity<?> createAddressForUserId(
            @PathVariable Long userId,
            @RequestBody AddressRequest addressRequest) {
        try {
            AddressResponse newAddress = addressService.createAddressForUser(addressRequest, userId);
            return new ResponseEntity<>(newAddress, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            if (e.getMessage().toLowerCase().contains("não encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else if (e.getMessage().toLowerCase().contains("conflito")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro inesperado.");
        }
    }

    @GetMapping
    @Operation(summary = "Lista ou busca endereços", description = "Retorna uma lista paginada de endereços, com filtros opcionais.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de endereços retornada com sucesso")
    })
    public ResponseEntity<Page<AddressResponse>> listOrSearchAddresses(
            @Parameter(description = "Filtrar por logradouro (parcial)") @RequestParam(required = false) String publicPlace,
            @Parameter(description = "Filtrar por bairro (parcial)") @RequestParam(required = false) String district,
            @Parameter(description = "Filtrar por cidade (parcial)") @RequestParam(required = false) String city,
            @Parameter(description = "Filtrar por estado (parcial)") @RequestParam(required = false) String state,
            @Parameter(description = "Filtrar por CEP (prefixo ou exato)") @RequestParam(required = false) String zipCode,
            @PageableDefault(size = 20, sort = "city") Pageable pageable 
    ) {
        Page<AddressResponse> addresses = addressService.searchAddresses(city, state, zipCode, district, publicPlace, pageable);
        return ResponseEntity.ok(addresses);
    }

    @PutMapping("/user/{userId}")
    @Operation(summary = "Atualiza o endereço de um usuário", description = "Atualiza os dados do endereço existente associado ao ID do usuário fornecido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Endereço atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Endereço não encontrado para o usuário fornecido"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida (dados do endereço)")
    })
    public ResponseEntity<?> updateAddressByUserId(
            @Parameter(description = "ID do usuário cujo endereço será atualizado", required = true)
            @PathVariable Long userId,
            @RequestBody AddressRequest addressRequest) {
        try {
            AddressResponse updatedAddress = addressService.updateMyAddress(userId, addressRequest);
            return ResponseEntity.ok(updatedAddress);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            if (e.getMessage().toLowerCase().contains("não encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro inesperado ao atualizar endereço.");
        }
    }

    @DeleteMapping("/user/{userId}")
    @Operation(summary = "Deleta o endereço de um usuário", description = "Remove o endereço associado ao ID do usuário fornecido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Endereço deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Endereço não encontrado para o usuário fornecido")
    })
    public ResponseEntity<?> deleteAddressByUserId(
            @Parameter(description = "ID do usuário cujo endereço será deletado", required = true)
            @PathVariable Long userId) {
        try {
            addressService.deleteMyAddress(userId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            if (e.getMessage().toLowerCase().contains("não encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro inesperado ao deletar endereço.");
        }
    }
}