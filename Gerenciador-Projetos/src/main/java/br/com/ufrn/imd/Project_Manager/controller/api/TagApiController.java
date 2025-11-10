package br.com.ufrn.imd.Project_Manager.controller.api;

import br.com.ufrn.imd.Project_Manager.dtos.api.TagRequest;
import br.com.ufrn.imd.Project_Manager.dtos.api.TagResponse;
import br.com.ufrn.imd.Project_Manager.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
@Tag(name = "Etiquetas", description = "Endpoints para gerenciamento de etiquetas das tarefas")
public class TagApiController {

    private final TagService tagService;

    @GetMapping
    @Operation(summary = "Listar todas as etiquetas", description = "Retorna uma lista de todas as etiquetas disponíveis.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de etiquetas retornada com sucesso")
    })
    public ResponseEntity<Page<TagResponse>> getTags(@Parameter(description = "Nome da etiqueta para busca (opcional)") @RequestParam(required = false) String name,
                                                     @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        Page<TagResponse> tags = this.tagService.getTags(name, pageable);
        return ResponseEntity.ok(tags);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter etiqueta por ID", description = "Retorna os detalhes de uma etiqueta específica pelo seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Etiqueta retornada com sucesso",
                    content = @Content(schema = @Schema(implementation = TagResponse.class))),
            @ApiResponse(responseCode = "404", description = "Etiqueta não encontrada")
    })
    public ResponseEntity<?> getTagById(@Parameter(description = "ID da etiqueta a ser buscada", required = true) @PathVariable Long id) {
        try {
            TagResponse tag = this.tagService.getTagById(id);
            return ResponseEntity.ok().body(tag);
        } catch (RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Operation(summary = "Criar uma nova etiqueta", description = "Cria uma nova etiqueta para ser associada às tarefas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Etiqueta criada com sucesso"),
            @ApiResponse(responseCode = "409", description = "Conflito ao criar a etiqueta, possivelmente já existe uma com o mesmo nome")
    })
    public ResponseEntity<String> createTag(@RequestBody TagRequest tagRequest) {
        try {
            TagResponse tagResponse =  this.tagService.createTag(tagRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(tagResponse.toString());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Atualizar etiqueta", description = "Atualiza os detalhes de uma etiqueta existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Etiqueta atualizada com sucesso",
                    content = @Content(schema = @Schema(implementation = TagResponse.class))),
            @ApiResponse(responseCode = "404", description = "Etiqueta não encontrada para o ID fornecido")
    })
    public ResponseEntity<String> updateTag(@Parameter(description = "ID da etiqueta a ser atualizada", required = true) @PathVariable Long id,
                                            @RequestBody TagRequest tagRequest) {
        try {
            TagResponse tagResponse = this.tagService.updateTag(id, tagRequest);
            return ResponseEntity.status(HttpStatus.OK).body(tagResponse.toString());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar etiqueta", description = "Remove uma etiqueta existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Etiqueta deletada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Etiqueta não encontrada para o ID fornecido")
    })
    public ResponseEntity<String> deleteTag(@Parameter(description = "ID da etiqueta a ser deletada", required = true) @PathVariable Long id) {
        try {
            this.tagService.deleteTag(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/add-to-task")
    @Operation(summary = "Adicionar etiqueta à tarefa", description = "Adiciona uma etiqueta a uma tarefa existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Etiqueta adicionada à tarefa com sucesso"),
            @ApiResponse(responseCode = "400", description = "Etiqueta já associada ou a etiqueta não não encontrada"),
            @ApiResponse(responseCode = "404", description = "Etiqueta não encontrada")
    })
    public ResponseEntity<?> addTagToTask(@Parameter(description = "ID da etiqueta a ser adicionada", required = true) @RequestParam Long tagId,
                                          @Parameter(description = "ID da tarefa à qual a etiqueta será adicionada", required = true) @RequestParam Long taskId) {
        try {
            this.tagService.addTagToTask(tagId, taskId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Tag not found!")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            }
        }
    }

    @DeleteMapping("/remove-from-task")
    @Operation(summary = "Remover etiqueta da tarefa", description = "Remove uma etiqueta de uma tarefa existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Etiqueta removida da tarefa com sucesso"),
            @ApiResponse(responseCode = "400", description = "Etiqueta não estava associada à tarefa ou a tarefa não foi encontrada"),
            @ApiResponse(responseCode = "404", description = "Etiqueta não encontrada")
    })
    public ResponseEntity<?> removeTagFromTask(@Parameter(description = "ID da etiqueta a ser removida", required = true) @RequestParam Long tagId,
                                                  @Parameter(description = "ID da tarefa da qual a etiqueta será removida", required = true) @RequestParam Long taskId) {
        try{
            this.tagService.removeTagFromTask(tagId, taskId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Tag not found!")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            }
        }
    }

    @GetMapping("/task/{taskId}")
    @Operation(summary = "Listar etiquetas da tarefa", description = "Lista todas as etiquetas associadas à tarefa.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de etiquetas associadas a tarefa retornada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Tarefa não encontrada.")
    })
    public ResponseEntity<?> getTasksByTask(@Parameter(description = "ID da tarefa cujas etiquetas serão listadas", required = true) @PathVariable Long taskId) {
        try {
            List<TagResponse> tags = this.tagService.findTagsByTask(taskId);
            return ResponseEntity.ok(tags);
        } catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
