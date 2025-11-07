package br.com.ufrn.imd.Project_Manager.service;

import br.com.ufrn.imd.Project_Manager.dtos.api.TaskRequest;
import br.com.ufrn.imd.Project_Manager.dtos.api.TaskResponse;
import br.com.ufrn.imd.Project_Manager.model.Frame;
import br.com.ufrn.imd.Project_Manager.model.Task;
import br.com.ufrn.imd.Project_Manager.model.User;
import br.com.ufrn.imd.Project_Manager.repository.FrameRepository;
import br.com.ufrn.imd.Project_Manager.repository.TaskRepository;
import br.com.ufrn.imd.Project_Manager.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private FrameRepository frameRepository;

    @Autowired
    private UserRepository userRepository;

    public List<TaskResponse> findByName(String taskName) {
        List<Task> tasks = this.taskRepository.findByNameIgnoreCase(taskName);

        return tasks.stream().map(e -> new TaskResponse(
                e.getId(),
                e.getName(),
                e.getDescription(),
                e.getInitialDate(),
                e.getFinalDate(),
                e.getStatus(),
                e.getFrame() != null ? e.getFrame().getId() : null,
                e.getAssignee() != null ? e.getAssignee().getId() : null,
                e.getAssignee() != null ? e.getAssignee().getName() : null
        )).toList();
    }

    public TaskResponse getTaskById(Long taskId) {
        Task task = this.taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found!"));

        return new TaskResponse(
                task.getId(),
                task.getName(),
                task.getDescription(),
                task.getInitialDate(),
                task.getFinalDate(),
                task.getStatus(),
                task.getFrame() != null ? task.getFrame().getId() : null,
                task.getAssignee() != null ? task.getAssignee().getId() : null,
                task.getAssignee() != null ? task.getAssignee().getName() : null
        );
    }

    public List<TaskResponse> listAllTasks() {
        return this.taskRepository.findAll().stream().map(e -> new TaskResponse(
                e.getId(),
                e.getName(),
                e.getDescription(),
                e.getInitialDate(),
                e.getFinalDate(),
                e.getStatus(),
                e.getFrame() != null ? e.getFrame().getId() : null,
                e.getAssignee() != null ? e.getAssignee().getId() : null,
                e.getAssignee() != null ? e.getAssignee().getName() : null
        )).toList();
    }

    @Transactional
    public TaskResponse createTask(TaskRequest task) {

        Task newTask = new Task();

        if (task.frameId() != null) {
            Frame frame = this.frameRepository.findById(task.frameId())
                    .orElseThrow(() -> new RuntimeException("Frame not found!"));
            newTask.setFrame(frame);
        }
        if (task.assigneeId() != null) {
            var user = this.userRepository.findById(task.assigneeId())
                    .orElseThrow(() -> new RuntimeException("User not found!"));
            newTask.setAssignee(user);
        }

        newTask.setName(task.name());
        newTask.setDescription(task.description());
        newTask.setInitialDate(task.initialDate());
        newTask.setFinalDate(task.finalDate());
        newTask.setStatus(task.status());

        newTask = this.taskRepository.save(newTask);

        return new TaskResponse(
                newTask.getId(),
                newTask.getName(),
                newTask.getDescription(),
                newTask.getInitialDate(),
                newTask.getFinalDate(),
                newTask.getStatus(),
                newTask.getFrame() != null ? newTask.getFrame().getId() : null,
                newTask.getAssignee() != null ? newTask.getAssignee().getId() : null,
                newTask.getAssignee() != null ? newTask.getAssignee().getName() : null
        );
    }

    @Transactional
    public TaskResponse updateTask(Long id, TaskRequest task) {
        Task oldTask = this.taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found!"));

        if (task.name() != null) {
            oldTask.setName(task.name());
        }
        if (task.description() != null) {
            oldTask.setDescription(task.description());
        }
        if (task.initialDate() != null) {
            oldTask.setInitialDate(task.initialDate());
        }
        if (task.finalDate() != null) {
            oldTask.setFinalDate(task.finalDate());
        }
        if (task.status() != null) {
            oldTask.setStatus(task.status());
        }
        if (task.frameId() != null) {
            Frame frame = this.frameRepository.findById(task.frameId())
                    .orElseThrow(() -> new RuntimeException("Frame not found!"));

            oldTask.setFrame(frame);
        }
        if (task.assigneeId() != null) {
            User user = this.userRepository.findById(task.assigneeId())
                    .orElseThrow(() -> new RuntimeException("User not found!"));
            oldTask.setAssignee(user);
        }

        Task updatedTask = this.taskRepository.save(oldTask);

        return new TaskResponse(
                updatedTask.getId(),
                updatedTask.getName(),
                updatedTask.getDescription(),
                updatedTask.getInitialDate(),
                updatedTask.getFinalDate(),
                updatedTask.getStatus(),
                updatedTask.getFrame() != null ? updatedTask.getFrame().getId() : null,
                updatedTask.getAssignee() != null ? updatedTask.getAssignee().getId() : null,
                updatedTask.getAssignee() != null ? updatedTask.getAssignee().getName() : null
        );
    }

    @Transactional
    public void deleteTask(Long taskId) {
        Task task = this.taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found!"));
        this.taskRepository.delete(task);
    }
}
