package br.com.ufrn.imd.Project_Manager.controller;

import br.com.ufrn.imd.Project_Manager.dtos.ProjectRequest;
import br.com.ufrn.imd.Project_Manager.dtos.ProjectResponse;
import br.com.ufrn.imd.Project_Manager.service.ProjectService;
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
@RequestMapping("/projects")
@Tag(name = "Projetos", description = "Endpoints para gerenciamento de projetos")
public class ProjectApiController {

    @Autowired
    private ProjectService projectService;

    @GetMapping
    @Operation(summary = "Lista todos os projetos", description = "Retorna uma lista de todos os projetos cadastrados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de projetos retornada com sucesso")
    })
    public ResponseEntity<List<ProjectResponse>> getAllProjects() {
        List<ProjectResponse> projects = projectService.listAllProjects();
        return ResponseEntity.ok().body(projects);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um projeto por ID", description = "Retorna os detalhes de um projeto específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Projeto encontrado"),
            @ApiResponse(responseCode = "404", description = "Projeto não encontrado")
    })
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable Long id) {
        try {
            ProjectResponse project = projectService.getProjectById(id);
            return ResponseEntity.ok().body(project);
        } catch (RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Busca projetos por nome", description = "Retorna uma lista de projetos que correspondem ao nome fornecido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de projetos retornada com sucesso")
    })
    @GetMapping("/search/{name}")
    public ResponseEntity<List<ProjectResponse>> getProjectsByName(@PathVariable String name) {
        List<ProjectResponse> projects = projectService.findByName(name);
        return ResponseEntity.ok().body(projects);
    }

    @PostMapping
    @Operation(summary = "Cria um novo projeto", description = "Cadastra um novo projeto no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Projeto criado com sucesso"),
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
            @ApiResponse(responseCode = "200", description = "Projeto atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Projeto não encontrado"),
            @ApiResponse(responseCode = "400", description = "Dados fornecidos inválidos")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateProject(@PathVariable Long id, @RequestBody ProjectRequest project) {
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
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        try {
            projectService.deleteProject(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return  ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Adiciona um frame a um projeto)", description = "Associa um frame existente a um projeto.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Frame adicionado ao projeto com sucesso"),
            @ApiResponse(responseCode = "404", description = "Projeto não encontrado"),
            @ApiResponse(responseCode = "400", description = "Dados fornecidos inválidos")
    })
    @PutMapping("/{projectId}/add-frame/{frameId}")
    public ResponseEntity<?> addFrameToProject(@PathVariable Long projectId, @PathVariable Long frameId) {
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
            @ApiResponse(responseCode = "200", description = "Frame removido do projeto com sucesso"),
            @ApiResponse(responseCode = "404", description = "Projeto não encontrado"),
            @ApiResponse(responseCode = "400", description = "Dados fornecidos inválidos")
    })
    @PutMapping("/{projectId}/remove-frame/{frameId}")
    public ResponseEntity<?> removeFrameFromProject(@PathVariable Long projectId, @PathVariable Long frameId) {
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
