package br.com.ufrn.imd.Project_Manager.controller.api;

import br.com.ufrn.imd.Project_Manager.dtos.api.CommentRequest;
import br.com.ufrn.imd.Project_Manager.dtos.api.CommentResponse;
import br.com.ufrn.imd.Project_Manager.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
@Tag(name = "Comentários", description = "Endpoints para gerenciamento de comentários dos das tarefas")
public class CommentApiController {

    @Autowired
    private CommentService commentService;

    @GetMapping
    @Operation(summary = "Lista ou busca os comentários", description = "Retorna uma lista de todos os comentários ou busca por comentários de uma tarefa específica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de comentários retornada com sucesso.")
    })
    public ResponseEntity<Page<CommentResponse>> getTasks(@Parameter(description = "ID da tarefa para buscar comentários específicos") @RequestParam(required = false) Long taskId,
                                                          @Parameter(description = "Texto do comentário para busca") @RequestParam(required = false) String text,
                                                          @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        Page<CommentResponse> comments = this.commentService.searchComments(taskId, text, pageable);
        return ResponseEntity.ok().body(comments);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter comentário por ID", description = "Retorna os detalhes de um comentário específico pelo seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comentário retornado com sucesso.",
                    content = @Content(schema = @Schema(implementation = CommentResponse.class))),
            @ApiResponse(responseCode = "404", description = "Comentário não encontrado.")
    })
    public ResponseEntity<?> getCommentById(@Parameter(description = "ID do comentário a ser buscado", required = true) @PathVariable Long id) {
        try {
            CommentResponse comment = commentService.getCommentById(id);
            return ResponseEntity.ok().body(comment);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("")
    @Operation(summary = "Criar um novo comentário", description = "Cria um novo comentário para uma tarefa.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Comentário criado com sucesso.",
                    content = @Content(schema = @Schema(implementation = CommentResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados fornecidos inválidos.")
    })
    public ResponseEntity<?> createComment(@RequestBody CommentRequest commentRequest) {
        try {
            CommentResponse commentResponse = this.commentService.createComment(commentRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(commentResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Atualizar um comentário", description = "Atualiza um comentário existente com base no ID fornecido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comentário atualizado com sucesso.",
                    content = @Content(schema = @Schema(implementation = CommentResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados fornecidos inválidos."),
            @ApiResponse(responseCode = "404", description = "Comentário não encontrado.")
    })
    public ResponseEntity<?> updateComment(@Parameter(description = "ID do comentário a ser atualizado", required = true) @PathVariable Long id,
                                                         @RequestBody CommentRequest commentRequest) {
        try {
            CommentResponse commentResponse = this.commentService.updateComment(id, commentRequest);
            return ResponseEntity.ok(commentResponse);
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Comment not found!")) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar um comentário", description = "Deleta um comentário existente com base no ID fornecido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Comentário deletado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Comentário não encontrado.")
    })
    public ResponseEntity<CommentResponse> deleteComment(@Parameter(description = "ID do comentário a ser deletado", required = true) @PathVariable Long id) {
        try {
            this.commentService.deleteComment(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
