package br.com.ufrn.imd.Project_Manager.controller.api;

import br.com.ufrn.imd.Project_Manager.dtos.api.CommentRequest;
import br.com.ufrn.imd.Project_Manager.dtos.api.CommentResponse;
import br.com.ufrn.imd.Project_Manager.service.CommentService;
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
@RequestMapping("api/comments")
@Tag(name = "Comentários", description = "Endpoints para gerenciamento de comentários dos das tarefas")
public class CommentApiController {

    @Autowired
    private CommentService commentService;

    @GetMapping("/task/{id}")
    @Operation(summary = "Listar todos os comentários", description = "Retorna uma lista de todos os comentários das tarefas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de comentários retornada com sucesso."),
            @ApiResponse(responseCode = "204", description = "Nenhum comentário encontrado.")
    })
    public ResponseEntity<List<CommentResponse>> findAllByTask(@PathVariable Long id) {
        List<CommentResponse> comments = this.commentService.findCommentByTask(id);

        if(comments.isEmpty()){
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(comments);
    }

    @GetMapping("/search/{name}")
    @Operation(summary = "Buscar comentários por nome", description = "Retorna uma lista de comentários que correspondem ao nome fornecido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de comentários retornada com sucesso."),
            @ApiResponse(responseCode = "204", description = "Nenhum comentário encontrado com esse nome.")
    })
    public ResponseEntity<List<CommentResponse>> findByName(@PathVariable String name) {
        List<CommentResponse> comments = this.commentService.findByName(name);
        if(comments.isEmpty()){
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(comments);
    }

    @PostMapping("/create")
    @Operation(summary = "Criar um novo comentário", description = "Cria um novo comentário para uma tarefa.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Comentário criado com sucesso.")
    })
    public ResponseEntity<CommentResponse> createComment(@RequestBody CommentRequest commentRequest) {
        CommentResponse commentResponse = this.commentService.CreateComment(commentRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(commentResponse);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar um comentário", description = "Atualiza um comentário existente com base no ID fornecido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comentário atualizado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Comentário não encontrado.")
    })
    public ResponseEntity<CommentResponse> updateComment(@PathVariable Long id, @RequestBody CommentRequest commentRequest) {
        try {
            CommentResponse commentResponse = this.commentService.UpdateComment(id, commentRequest);
            return ResponseEntity.ok(commentResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar um comentário", description = "Deleta um comentário existente com base no ID fornecido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Comentário deletado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Comentário não encontrado.")
    })
    public ResponseEntity<CommentResponse> deleteComment(@PathVariable Long id) {
        try {
            this.commentService.DeleteComment(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
