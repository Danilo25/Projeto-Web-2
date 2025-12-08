package br.com.ufrn.imd.Project_Manager.service;

import br.com.ufrn.imd.Project_Manager.dtos.api.TaskRequest;
import br.com.ufrn.imd.Project_Manager.dtos.api.TaskResponse;
import br.com.ufrn.imd.Project_Manager.model.Frame;
import br.com.ufrn.imd.Project_Manager.model.Project;
import br.com.ufrn.imd.Project_Manager.model.Task;
import br.com.ufrn.imd.Project_Manager.model.User;
import br.com.ufrn.imd.Project_Manager.repository.FrameRepository;
import br.com.ufrn.imd.Project_Manager.repository.TaskRepository;
import br.com.ufrn.imd.Project_Manager.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private FrameRepository frameRepository;

    @Autowired
    private UserRepository userRepository;

    public TaskResponse toTaskResponse(Task task) {
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

    public Page<TaskResponse> searchTasks(String name, Pageable pageable) {
        Page<Task> tasks = this.taskRepository.searchTasks(name, pageable);
        return tasks.map(this::toTaskResponse);
    }

    public TaskResponse getTaskById(Long taskId) {
        Task task = this.taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found!"));
        return toTaskResponse(task);
    }

    @Transactional
    public TaskResponse createTask(TaskRequest task) {

        Task newTask = new Task();

        if (task.frameId() != null) {
            Frame frame = this.frameRepository.findById(task.frameId())
                    .orElseThrow(() -> new RuntimeException("Frame not found!"));

            Project project = frame.getProject();
            if (project != null) {
                if ((task.initialDate() != null && task.initialDate().isBefore(project.getInitialDate()))
                        || (task.finalDate() != null && task.finalDate().isAfter(project.getFinalDate()))
                ) {
                    throw new RuntimeException("A tarefa não pode ter datas fora do intervalo do projeto!");
                }
            }

            newTask.setFrame(frame);
        }
        if (task.assigneeId() != null) {
            var user = this.userRepository.findById(task.assigneeId())
                    .orElseThrow(() -> new RuntimeException("User not found!"));
            newTask.setAssignee(user);
        }

        if (task.initialDate() != null && task.finalDate() != null) {
            if (task.finalDate().isBefore(task.initialDate())) {
                throw new RuntimeException("A data final não pode ser anterior à data inicial!");
            }
        }

        newTask.setName(task.name());
        newTask.setDescription(task.description());
        newTask.setInitialDate(task.initialDate());
        newTask.setFinalDate(task.finalDate());
        newTask.setStatus(task.status());

        newTask = this.taskRepository.save(newTask);

        return toTaskResponse(newTask);
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

        if (task.status() != null) {
            oldTask.setStatus(task.status());
        }
        if (task.frameId() != null) {
            Frame frame = this.frameRepository.findById(task.frameId())
                    .orElseThrow(() -> new RuntimeException("Frame not found!"));

            Project project = frame.getProject();
            if (project != null) {
                if ((task.initialDate() != null && task.initialDate().isBefore(project.getInitialDate()))
                        || (task.finalDate() != null && task.finalDate().isAfter(project.getFinalDate()))
                ) {
                    throw new RuntimeException("A tarefa não pode ter datas fora do intervalo do projeto!");
                }
            }

            oldTask.setFrame(frame);
        }

        if (task.initialDate() != null && task.finalDate() != null) {
            if (task.finalDate().isBefore(task.initialDate())) {
                throw new RuntimeException("A data final não pode ser anterior à data inicial!");
            }

            if (oldTask.getFrame() != null) {
                Project project = oldTask.getFrame().getProject();
                if (project != null) {
                    if (task.initialDate().isBefore(project.getInitialDate())
                            || task.finalDate().isAfter(project.getFinalDate())
                    ) {
                        throw new RuntimeException("A tarefa não pode ter datas fora do intervalo do projeto!");
                    }
                }
            }
        }

        if (task.initialDate() != null) {
            oldTask.setInitialDate(task.initialDate());
        }
        if (task.finalDate() != null) {
            oldTask.setFinalDate(task.finalDate());
        }

        if (task.assigneeId() != null) {
            User user = this.userRepository.findById(task.assigneeId())
                    .orElseThrow(() -> new RuntimeException("User not found!"));
            oldTask.setAssignee(user);
        }

        Task updatedTask = this.taskRepository.save(oldTask);

        return toTaskResponse(updatedTask);
    }

    @Transactional
    public void deleteTask(Long taskId) {
        Task task = this.taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found!"));
        this.taskRepository.delete(task);
    }
}
