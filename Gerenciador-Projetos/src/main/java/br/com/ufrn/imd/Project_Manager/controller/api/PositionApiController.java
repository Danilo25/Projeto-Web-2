package br.com.ufrn.imd.Project_Manager.controller.api;

import br.com.ufrn.imd.Project_Manager.dtos.api.PositionRequest;
import br.com.ufrn.imd.Project_Manager.dtos.api.PositionResponse;
import br.com.ufrn.imd.Project_Manager.service.PositionService;
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
@RequestMapping("/api/positions")
public class PositionApiController {

    @Autowired
    private PositionService positionService;

    @GetMapping
    @Operation(summary = "Lista ou pesquisa cargos por nome ou nível",
            description = "Retorna uma lista de todos os cargos disponíveis.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de cargos retornada com sucesso")
    })
    public ResponseEntity<Page<PositionResponse>> getPositions(@Parameter(description = "Texto parcial para filtrar cargos por nome ou nível (opcional)") String text,
                                                               @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        Page<PositionResponse> positions = positionService.getPositions(text, pageable);
        return ResponseEntity.ok(positions);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um cargo por ID", description = "Retorna os detalhes de um cargo específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cargo encontrado",
                    content = @Content(schema = @Schema(implementation = PositionResponse.class))),
            @ApiResponse(responseCode = "404", description = "Cargo não encontrado")
    })
    public ResponseEntity<PositionResponse> getPositionById(
            @Parameter(description = "ID do cargo a ser buscado", required = true) @PathVariable Long id) {
        try {
            PositionResponse position = positionService.getPositionById(id);
            return ResponseEntity.ok(position);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Operation(summary = "Cria um novo cargo", description = "Adiciona um novo cargo ao sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cargo criado com sucesso",
                    content = @Content(schema = @Schema(implementation = PositionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "409", description = "Combinação de nome e nível já existe")
    })
    public ResponseEntity<?> createPosition(@RequestBody PositionRequest positionRequest) {
        try {
            PositionResponse newPosition = positionService.createPosition(positionRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(newPosition);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Conflito")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Atualiza um cargo existente", description = "Atualiza os detalhes de um cargo pelo seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cargo atualizado com sucesso",
                    content = @Content(schema = @Schema(implementation = PositionResponse.class))),
            @ApiResponse(responseCode = "404", description = "Cargo não encontrado"),
            @ApiResponse(responseCode = "409", description = "Combinação de nome e nível já existe")
    })
    public ResponseEntity<?> updatePosition(@Parameter(description = "ID do cargo a ser atualizado", required = true) @PathVariable Long id,
                                            @RequestBody PositionRequest positionRequest) {
        try {
            PositionResponse updatedPosition = positionService.updatePosition(id, positionRequest);
            return ResponseEntity.ok(updatedPosition);
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Position not found!")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else if (e.getMessage().contains("Conflito")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deleta um cargo", description = "Remove um cargo do sistema pelo seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cargo deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cargo não encontrado")
    })
    public ResponseEntity<Void> deletePosition(
            @Parameter(description = "ID do cargo a ser deletado", required = true) @PathVariable Long id) {
        try {
            positionService.deletePosition(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
