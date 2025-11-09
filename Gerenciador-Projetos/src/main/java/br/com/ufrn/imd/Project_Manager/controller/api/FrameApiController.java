package br.com.ufrn.imd.Project_Manager.controller.api;

import br.com.ufrn.imd.Project_Manager.dtos.api.FrameRequest;
import br.com.ufrn.imd.Project_Manager.dtos.api.FrameResponse;
import br.com.ufrn.imd.Project_Manager.service.FrameService;
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

import java.util.List;

@RestController
@RequestMapping("/api/frames")
@Tag(name = "Quadros", description = "Endpoints para gerenciamento de quadros dos projetos")
public class FrameApiController {

    @Autowired
    private FrameService frameService;

    @GetMapping
    @Operation(summary = "Listar todos os quadros", description = "Retorna uma lista de todos os quadros disponíveis")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de quadros retornada com sucesso")
    })
    public ResponseEntity<List<FrameResponse>> getFrames(@Parameter(description = "Nome do quadro para busca (opcional)") @RequestParam(required = false) String name) {
        List<FrameResponse> frames = frameService.searchFrames(name);
        return ResponseEntity.ok().body(frames);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter quadro por ID", description = "Retorna os detalhes de um quadro específico pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quadro retornado com sucesso",
                    content = @Content(schema = @Schema(implementation = FrameResponse.class))),
            @ApiResponse(responseCode = "404", description = "Quadro não encontrado")
    })
    public ResponseEntity<?> getFrameById(@Parameter(description = "ID do quadro a ser buscado", required = true) @PathVariable Long id) {
        try {
            FrameResponse frame = frameService.getFrameById(id);
            return ResponseEntity.ok().body(frame);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Operation(summary = "Criar um novo quadro", description = "Cria um novo quadro com os detalhes fornecidos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Quadro criado com sucesso",
                    content = @Content(schema = @Schema(implementation = FrameResponse.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida")
    })
    public ResponseEntity<?> createFrame(@RequestBody FrameRequest frame) {
        try {
            FrameResponse newFrame = frameService.createFrame(frame);
            return ResponseEntity.status(HttpStatus.CREATED).body(newFrame);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Atualizar um quadro", description = "Atualiza os detalhes de um quadro existente pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quadro atualizado com sucesso",
                    content = @Content(schema = @Schema(implementation = FrameResponse.class))),
            @ApiResponse(responseCode = "404", description = "Quadro não encontrado"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida")
    })
    public ResponseEntity<?> updateFrame(@Parameter(description = "ID do quadro a ser atualizado", required = true) @PathVariable Long id,
                                         @RequestBody FrameRequest frame) {
        try {
            FrameResponse updatedFrame = frameService.updateFrame(id, frame);
            return ResponseEntity.ok().body(updatedFrame);
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Frame not found!")) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir um quadro", description = "Exclui um quadro existente pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Quadro excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Quadro não encontrado")
    })
    public ResponseEntity<Void> deleteFrame(@Parameter(description = "ID do quadro a ser excluído", required = true) @PathVariable Long id) {
        try {
            frameService.deleteFrame(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
