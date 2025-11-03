package br.com.ufrn.imd.Project_Manager.controller.api;

import br.com.ufrn.imd.Project_Manager.dtos.api.TaskRequest;
import br.com.ufrn.imd.Project_Manager.dtos.api.TaskResponse;
import br.com.ufrn.imd.Project_Manager.service.TaskService;
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
@RequestMapping("/api/tasks")
@Tag(name = "Tarefas", description = "Endpoints para gerenciamento de tarefas")
public class TaskApiController {

    @Autowired
    private TaskService taskService;

    @GetMapping
    @Operation(summary = "Listar todas as tarefas", description = "Retorna uma lista de todas as tarefas cadastradas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de tarefas retornada com sucesso")
    })
    public ResponseEntity<List<TaskResponse>> getAllTasks() {
        List<TaskResponse> tasks = taskService.listAllTasks();
        return ResponseEntity.ok().body(tasks);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter tarefa por ID", description = "Retorna os detalhes de uma tarefa específica pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarefa retornada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
    })
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long id) {
        try {
            TaskResponse task = taskService.getTaskById(id);
            return ResponseEntity.ok().body(task);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search/{name}")
    @Operation(summary = "Buscar tarefas por nome", description = "Retorna uma lista de tarefas que correspondem ao nome fornecido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de tarefas retornada com sucesso")
    })
    public ResponseEntity<List<TaskResponse>> getTasksByName(@PathVariable String name) {
        List<TaskResponse> tasks = taskService.findByName(name);
        return ResponseEntity.ok().body(tasks);
    }

    @PostMapping
    @Operation(summary = "Criar nova tarefa", description = "Cria uma nova tarefa com os dados fornecidos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tarefa criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos para criação da tarefa")
    })
    public ResponseEntity<?> createTask(@RequestBody TaskRequest task) {
        try {
            TaskResponse newTask = taskService.createTask(task);
            return ResponseEntity.status(HttpStatus.CREATED).body(newTask);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Atualizar tarefa", description = "Atualiza os dados de uma tarefa existente pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarefa atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos para atualização da tarefa"),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
    })
    public ResponseEntity<?> updateTask(@PathVariable Long id, @RequestBody TaskRequest task) {
        try {
            TaskResponse updatedTask = taskService.updateTask(id, task);
            return ResponseEntity.ok().body(updatedTask);
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Task not found!")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir tarefa", description = "Exclui uma tarefa existente pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tarefa excluída com sucesso"),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
    })
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        try {
            taskService.deleteTask(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
