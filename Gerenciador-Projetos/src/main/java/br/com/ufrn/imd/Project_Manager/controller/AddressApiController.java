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
            @ApiResponse(responseCode = "404", description = "Endereço não encontrado para o usuário")
    })
    public ResponseEntity<AddressResponse> getAddressByUserId(
            @Parameter(description = "ID do usuário", required = true) @PathVariable Long userId) {
        try {
            AddressResponse address = addressService.getAddressByUserId(userId);
            return ResponseEntity.ok(address);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Operation(summary = "Cria um endereço para um usuário", description = "Associa um novo endereço a um usuário existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Endereço criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "ID do usuário não fornecido"),
            @ApiResponse(responseCode = "409", description = "Usuário não encontrado ou já possui endereço")
    })
    public ResponseEntity<?> createAddress(@RequestBody AddressRequest addressRequest) {
        try {
            AddressResponse newAddress = addressService.createAddressForUser(addressRequest);
            return new ResponseEntity<>(newAddress, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PutMapping("/{addressId}")
    @Operation(summary = "Atualiza um endereço existente", description = "Atualiza os dados de um endereço pelo seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Endereço atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Endereço ou novo usuário não encontrado"),
            @ApiResponse(responseCode = "409", description = "Conflito ao tentar associar a um usuário que já tem endereço")
    })
    public ResponseEntity<?> updateAddress(
            @Parameter(description = "ID do endereço a ser atualizado", required = true) @PathVariable Long addressId,
            @RequestBody AddressRequest addressRequest) {
        try {
            AddressResponse updatedAddress = addressService.updateAddress(addressId, addressRequest);
            return ResponseEntity.ok(updatedAddress);
        } catch (RuntimeException e) {
             if (e.getMessage().contains("not found")) {
                 return ResponseEntity.notFound().build();
             }
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @DeleteMapping("/{addressId}")
    @Operation(summary = "Deleta um endereço", description = "Remove um endereço do sistema pelo seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Endereço deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Endereço não encontrado")
    })
    public ResponseEntity<Void> deleteAddress(
            @Parameter(description = "ID do endereço a ser deletado", required = true) @PathVariable Long addressId) {
        try {
            addressService.deleteAddress(addressId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}