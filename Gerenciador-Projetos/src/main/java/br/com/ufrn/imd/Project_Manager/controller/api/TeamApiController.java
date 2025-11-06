package br.com.ufrn.imd.Project_Manager.controller.api;

import br.com.ufrn.imd.Project_Manager.dtos.api.TeamRequest;
import br.com.ufrn.imd.Project_Manager.dtos.api.TeamResponse;
import br.com.ufrn.imd.Project_Manager.service.TeamService;
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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import java.util.Set;

@RestController
@RequestMapping("/api/teams")
@Tag(name = "Equipes", description = "Endpoints para gerenciamento de equipes e seus membros")
public class TeamApiController {

    @Autowired
    private TeamService teamService;

    @GetMapping
    @Operation(summary = "Lista ou pesquisa equipes por nome ou ID de membro",
            description = "Retorna uma lista paginada de todas as equipes ou filtra por nome ou ID de membro.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de equipes retornada com sucesso")
    })
    public ResponseEntity<Page<TeamResponse>> getTeams(@Parameter(description = "Nome parcial para filtrar equipes (opcional)") @RequestParam(required = false) String name,
                                                       @Parameter(description = "Id do membro para filtrar equipes (opcional)") @RequestParam(required = false) Long memberId,
                                                       @PageableDefault(size = 20, sort = "name") Pageable pageable)
    {
        Page<TeamResponse> teamsPage = teamService.searchTeams(name, memberId, pageable);
        return ResponseEntity.ok(teamsPage);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma equipe por ID", description = "Retorna os detalhes de uma equipe específica, incluindo os IDs dos membros.")
    @ApiResponses(value = { 
            @ApiResponse(responseCode = "200", description = "Equipe encontrada"),
            @ApiResponse(responseCode = "404", description = "Equipe não encontrada")
     })
    public ResponseEntity<?> getTeamById(@Parameter(description = "ID da equipe a ser buscada", required = true) @PathVariable Long id) {
        try {
            TeamResponse team = teamService.getTeamById(id);
            return ResponseEntity.ok(team);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping
    @Operation(summary = "Cria uma nova equipe", description = "Adiciona os dados da equipe criada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Equipe criada com sucesso",
                    content = @Content (schema = @Schema(implementation = TeamResponse.class))),
            @ApiResponse(responseCode = "400", description = "Nome vazio ou usuário(s) não encontrado(s)"),
            @ApiResponse(responseCode = "409", description = "Conflito de nome da equipe"),
            @ApiResponse(responseCode = "500", description = "Erro inesperado ao criar equipe")
    })
    public ResponseEntity<?> createTeam(@RequestBody TeamRequest teamRequest) {
         try {
            if (teamRequest.creatorId() == null) {
                return ResponseEntity.badRequest().body("O ID do criador é obrigatório no corpo da requisição.");
            }
            TeamResponse newTeam = teamService.createTeam(teamRequest, teamRequest.creatorId());
            return new ResponseEntity<>(newTeam, HttpStatus.CREATED);
        } catch (RuntimeException e) {
             if (e.getMessage().equals("O nome da equipe não pode ser vazio.") || e.getMessage().contains("Nenhum usuário")) {
                 return ResponseEntity.badRequest().body(e.getMessage());
             } else if (e.getMessage().contains("Conflito")) {
                 return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
             }
             return ResponseEntity.internalServerError().body("Erro inesperado ao criar equipe: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza uma equipe existente", description = "Atualiza os dados de uma equipe.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Equipe atualizada com sucesso",
                    content =  @Content(schema = @Schema(implementation = TeamResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Equipe ou membro(s) não encontrado(s)"),
            @ApiResponse(responseCode = "409", description = "Conflito (novo nome já existe em outra equipe)")
    })
    public ResponseEntity<?> updateTeam(
            @Parameter(description = "ID da equipe a ser atualizada", required = true) @PathVariable Long id,
            @RequestBody TeamRequest teamRequest) {
         try {
            TeamResponse updatedTeam = teamService.updateTeam(id, teamRequest);
            return ResponseEntity.ok(updatedTeam);
        } catch (RuntimeException e) {
             if (e.getMessage().equals("Equipe não encontrada!")) {
                 return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
             } else if (e.getMessage().contains("Conflito")) {
                 return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
             } else if (e.getMessage().contains("Nenhum usuário")) {
                 return ResponseEntity.badRequest().body(e.getMessage());
             }
             return ResponseEntity.internalServerError().body("Erro inesperado ao atualizar equipe: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deleta uma equipe", description = "Deleta a equipe")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Equipe deletada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Equipe não encontrada")
    })
    public ResponseEntity<?> deleteTeam(
            @Parameter(description = "ID da equipe a ser deletada", required = true) @PathVariable Long id) {
        try {
            teamService.deleteTeam(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}