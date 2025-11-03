package br.com.ufrn.imd.Project_Manager.controller.api;

import br.com.ufrn.imd.Project_Manager.dtos.api.FrameRequest;
import br.com.ufrn.imd.Project_Manager.dtos.api.FrameResponse;
import br.com.ufrn.imd.Project_Manager.service.FrameService;
import io.swagger.v3.oas.annotations.Operation;
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
    public ResponseEntity<List<FrameResponse>> getAllFrames() {
        List<FrameResponse> frames = frameService.listAllFrames();
        return ResponseEntity.ok().body(frames);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter quadro por ID", description = "Retorna os detalhes de um quadro específico pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quadro retornado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Quadro não encontrado")
    })
    public ResponseEntity<?> getFrameById(@PathVariable Long id) {
        try {
            FrameResponse frame = frameService.getFrameById(id);
            return ResponseEntity.ok().body(frame);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search/{name}")
    @Operation(summary = "Buscar quadros por nome", description = "Retorna uma lista de quadros que correspondem ao nome fornecido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de quadros retornada com sucesso")
    })
    public ResponseEntity<List<FrameResponse>> getFramesByName(@PathVariable String name) {
        List<FrameResponse> frames = frameService.findByName(name);
        return ResponseEntity.ok().body(frames);
    }

    @PostMapping
    @Operation(summary = "Criar um novo quadro", description = "Cria um novo quadro com os detalhes fornecidos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Quadro criado com sucesso"),
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
            @ApiResponse(responseCode = "200", description = "Quadro atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "404", description = "Quadro não encontrado")
    })
    public ResponseEntity<?> updateFrame(@PathVariable Long id, @RequestBody FrameRequest frame) {
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
    public ResponseEntity<Void> deleteFrame(@PathVariable Long id) {
        try {
            frameService.deleteFrame(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
