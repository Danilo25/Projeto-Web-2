package br.com.ufrn.imd.Project_Manager.controller.api;

import br.com.ufrn.imd.Project_Manager.dtos.api.ProjectRequest;
import br.com.ufrn.imd.Project_Manager.dtos.api.ProjectResponse;
import br.com.ufrn.imd.Project_Manager.service.ProjectService;
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
@RequestMapping("/api/projects")
@Tag(name = "Projetos", description = "Endpoints para gerenciamento de projetos")
public class ProjectApiController {

    @Autowired
    private ProjectService projectService;

    @GetMapping
    @Operation(summary = "Lista ou pesquisa os projetos por nome ou ID da equipe",
            description = "Retorna uma lista de todos os projetos cadastrados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de projetos retornada com sucesso")
    })
    public ResponseEntity<Page<ProjectResponse>> getProjects(@Parameter(description = "Nome parcial para filtrar projetos (opcional)") @RequestParam(required = false) String name,
                                                             @Parameter(description = "ID da equipe para filtrar projetos (opcional)") @RequestParam(required = false) Long teamId,
                                                             @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        Page<ProjectResponse> projects = projectService.searchProjects(name, teamId, pageable);
        return ResponseEntity.ok().body(projects);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um projeto por ID", description = "Retorna os detalhes de um projeto específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Projeto encontrado",
                    content = @Content(schema = @Schema(implementation = ProjectResponse.class))),
            @ApiResponse(responseCode = "404", description = "Projeto não encontrado")
    })
    public ResponseEntity<?> getProjectById(@Parameter(description = "ID do projeto a ser buscado", required = true) @PathVariable Long id) {
        try {
            ProjectResponse project = projectService.getProjectById(id);
            return ResponseEntity.ok().body(project);
        } catch (RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Operation(summary = "Cria um novo projeto", description = "Cadastra um novo projeto no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Projeto criado com sucesso",
                    content = @Content(schema = @Schema(implementation = ProjectResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados fornecidos inválidos")
    })
    public ResponseEntity<?> createProject(@RequestBody ProjectRequest project) {
        try {
            ProjectResponse newProject = projectService.createProject(project);
            return ResponseEntity.status(HttpStatus.CREATED).body(newProject);
        } catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Atualiza um projeto existente", description = "Atualiza os dados de um projeto pelo seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Projeto atualizado com sucesso",
                    content = @Content(schema = @Schema(implementation = ProjectResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados fornecidos inválidos"),
            @ApiResponse(responseCode = "404", description = "Projeto não encontrado")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateProject(@Parameter(description = "ID do projeto a ser atualizado", required = true) @PathVariable Long id,
                                           @RequestBody ProjectRequest project) {
        try {
            ProjectResponse updatedProject = projectService.updateProject(id, project);
            return ResponseEntity.ok().body(updatedProject);
        } catch (RuntimeException e){
            if (e.getMessage().equals("Project not found!")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Deleta um projeto", description = "Remove um projeto do sistema pelo seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Projeto deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Projeto não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@Parameter(description = "ID do projeto a ser deletado", required = true) @PathVariable Long id) {
        try {
            projectService.deleteProject(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return  ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Adiciona um frame a um projeto)", description = "Associa um frame existente a um projeto.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Frame adicionado ao projeto com sucesso",
                    content = @Content(schema = @Schema(implementation = ProjectResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados fornecidos inválidos"),
            @ApiResponse(responseCode = "404", description = "Projeto não encontrado")
    })
    @PutMapping("/add-frame")
    public ResponseEntity<?> addFrameToProject(@Parameter(description = "ID do projeto que terá o quadro adicionado", required = true) @RequestParam Long projectId,
                                               @Parameter(description = "ID do quadro a ser adicionado ao projeto", required = true) @RequestParam Long frameId) {
        try {
            ProjectResponse updatedProject = projectService.addFrameToProject(projectId, frameId);
            return ResponseEntity.ok().body(updatedProject);
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Project not found!")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Remove um frame de um projeto)", description = "Desassocia um frame existente de um projeto.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Frame removido do projeto com sucesso",
                    content = @Content(schema = @Schema(implementation = ProjectResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados fornecidos inválidos"),
            @ApiResponse(responseCode = "404", description = "Projeto não encontrado")
    })
    @PutMapping("/remove-frame")
    public ResponseEntity<?> removeFrameFromProject(@Parameter(description = "ID do projeto que terá o quadro removido", required = true) @RequestParam Long projectId,
                                                    @Parameter(description = "ID do quadro a ser removido do projeto", required = true) @RequestParam Long frameId) {
        try {
            ProjectResponse updatedProject = projectService.removeFrameFromProject(projectId, frameId);
            return ResponseEntity.ok().body(updatedProject);
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Project not found!")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
