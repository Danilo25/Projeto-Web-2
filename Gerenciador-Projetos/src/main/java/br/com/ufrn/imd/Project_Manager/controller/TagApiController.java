package br.com.ufrn.imd.Project_Manager.controller;

import br.com.ufrn.imd.Project_Manager.dtos.TagRequest;
import br.com.ufrn.imd.Project_Manager.dtos.TagResponse;
import br.com.ufrn.imd.Project_Manager.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
@Tag(name = "Etiquetas", description = "Endpoints para gerenciamento de etiquetas das tarefas")
public class TagApiController {

    private final TagService tagService;

    @PostMapping("/create")
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

    @GetMapping
    @Operation(summary = "Listar todas as etiquetas", description = "Retorna uma lista de todas as etiquetas disponíveis.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de etiquetas retornada com sucesso"),
            @ApiResponse(responseCode = "204", description = "Nenhuma etiqueta encontrada")
    })
    public ResponseEntity<List<TagResponse>> listTags() {
        List<TagResponse> tags = this.tagService.listAllTags();

        if(tags.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(tags);
    }

    @GetMapping("/{name}")
    @Operation(summary = "Buscar etiqueta por nome", description = "Retorna uma lista de etiquetas que correspondem ao nome fornecido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Etiqueta(s) encontrada(s) com sucesso"),
            @ApiResponse(responseCode = "204", description = "Nenhuma etiqueta encontrada com o nome fornecido")
    })
    public ResponseEntity<List<TagResponse>> getTagById(@PathVariable String name) {
        List<TagResponse> tags = this.tagService.findByName(name);

        if(tags.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(tags);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar etiqueta", description = "Atualiza os detalhes de uma etiqueta existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Etiqueta atualizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Etiqueta não encontrada para o ID fornecido")
    })
    public ResponseEntity<String> updateTag(@PathVariable Long id, @RequestBody TagRequest tagRequest) {
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
    public ResponseEntity<String> deleteTag(@PathVariable Long id) {
        try {
            this.tagService.deleteTag(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
