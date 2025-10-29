package br.com.ufrn.imd.Project_Manager.controller;

import br.com.ufrn.imd.Project_Manager.dtos.TeamRequest;
import br.com.ufrn.imd.Project_Manager.dtos.TeamResponse;
import br.com.ufrn.imd.Project_Manager.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
@Tag(name = "Equipes", description = "Endpoints para gerenciamento de equipes e seus membros")
public class TeamApiController {

    @Autowired
    private TeamService teamService;

    @GetMapping
    @Operation(summary = "Lista ou pesquisa equipes", description = "Retorna uma lista de todas as equipes ou filtra por nome.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de equipes retornada com sucesso")
    })
    public ResponseEntity<List<TeamResponse>> listOrSearchTeams(
            @Parameter(description = "Nome parcial para filtrar equipes (opcional)") @RequestParam(required = false) String name) {
        List<TeamResponse> teams = teamService.searchTeamByName(name);
        return ResponseEntity.ok(teams);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma equipe por ID", description = "Retorna os detalhes de uma equipe específica, incluindo os IDs dos membros.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Equipe encontrada"),
            @ApiResponse(responseCode = "404", description = "Equipe não encontrada")
    })
    public ResponseEntity<TeamResponse> getTeamById(
            @Parameter(description = "ID da equipe a ser buscada", required = true) @PathVariable Long id) {
        try {
            TeamResponse team = teamService.getTeamById(id);
            return ResponseEntity.ok(team);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Operation(summary = "Cria uma nova equipe", description = "Cadastra uma nova equipe, associando usuários existentes e o criador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Equipe criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos, criador não especificado, ou usuário(s) não encontrado(s)")
    })
    public ResponseEntity<?> createTeam(@RequestBody TeamRequest teamRequest) {
         try {
            if (teamRequest.creatorId() == null) {
                return ResponseEntity.badRequest().body("Creator ID (creatorId) is required in the request body.");
            }
            TeamResponse newTeam = teamService.createTeam(teamRequest, teamRequest.creatorId()); 
            return new ResponseEntity<>(newTeam, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); 
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza uma equipe existente", description = "Atualiza nome, descrição e membros de uma equipe.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Equipe atualizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Equipe não encontrada"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou usuário não encontrado")
    })
    public ResponseEntity<?> updateTeam(
            @Parameter(description = "ID da equipe a ser atualizada", required = true) @PathVariable Long id,
            @RequestBody TeamRequest teamRequest) {
         try {
            TeamResponse updatedTeam = teamService.updateTeam(id, teamRequest);
            return ResponseEntity.ok(updatedTeam);
        } catch (RuntimeException e) {
             if (e.getMessage().contains("not found")) {
                 return ResponseEntity.notFound().build();
             }
             return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deleta uma equipe", description = "Remove uma equipe do sistema pelo seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Equipe deletada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Equipe não encontrada")
    })
    public ResponseEntity<Void> deleteTeam(
            @Parameter(description = "ID da equipe a ser deletada", required = true) @PathVariable Long id) {
        try {
            teamService.deleteTeam(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}