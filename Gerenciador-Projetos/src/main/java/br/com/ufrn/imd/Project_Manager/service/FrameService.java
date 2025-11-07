package br.com.ufrn.imd.Project_Manager.service;

import br.com.ufrn.imd.Project_Manager.dtos.api.FrameRequest;
import br.com.ufrn.imd.Project_Manager.dtos.api.FrameResponse;
import br.com.ufrn.imd.Project_Manager.dtos.api.TaskResponse;
import br.com.ufrn.imd.Project_Manager.model.Frame;
import br.com.ufrn.imd.Project_Manager.model.Project;
import br.com.ufrn.imd.Project_Manager.repository.FrameRepository;
import br.com.ufrn.imd.Project_Manager.repository.ProjectRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FrameService {

    @Autowired
    private FrameRepository frameRepository;

    @Autowired
    private ProjectRepository projectRepository;

    public List<FrameResponse> findByName(String frameName) {
        List<Frame> frames = this.frameRepository.findByNameIgnoreCase(frameName);

        return frames.stream().map(e -> new FrameResponse(
                e.getId(),
                e.getName(),
                e.getOrderIndex(),
                e.getProject().getId(),
                e.getTasks() != null ? e.getTasks().stream().map(task -> new TaskResponse(
                        task.getId(),
                        task.getName(),
                        task.getDescription(),
                        task.getInitialDate(),
                        task.getFinalDate(),
                        task.getStatus(),
                        task.getFrame() != null ? task.getFrame().getId() : null,
                        task.getAssignee() != null ? task.getAssignee().getId() : null,
                        task.getAssignee() != null ? task.getAssignee().getName() : null
                )).toList(): null
        )).toList();
    }

    public FrameResponse getFrameById(Long frameId) {
        Frame frame = this.frameRepository.findById(frameId)
                .orElseThrow(() -> new RuntimeException("Frame not found!"));

        return new FrameResponse(
                frame.getId(),
                frame.getName(),
                frame.getOrderIndex(),
                frame.getProject().getId(),
                frame.getTasks() != null ? frame.getTasks().stream().map(task -> new TaskResponse(
                        task.getId(),
                        task.getName(),
                        task.getDescription(),
                        task.getInitialDate(),
                        task.getFinalDate(),
                        task.getStatus(),
                        task.getFrame() != null ? task.getFrame().getId() : null,
                        task.getAssignee() != null ? task.getAssignee().getId() : null,
                        task.getAssignee() != null ? task.getAssignee().getName() : null
                )).toList() : null
        );
    }

    public List<FrameResponse> listAllFrames() {
        return this.frameRepository.findAll().stream().map(e -> new FrameResponse(
                e.getId(),
                e.getName(),
                e.getOrderIndex(),
                e.getProject().getId(),
                e.getTasks() != null ? e.getTasks().stream().map(task -> new TaskResponse(
                        task.getId(),
                        task.getName(),
                        task.getDescription(),
                        task.getInitialDate(),
                        task.getFinalDate(),
                        task.getStatus(),
                        task.getFrame() != null ? task.getFrame().getId() : null,
                        task.getAssignee() != null ? task.getAssignee().getId() : null,
                        task.getAssignee() != null ? task.getAssignee().getName() : null
                )).toList() : null
        )).toList();
    }

    @Transactional
    public FrameResponse createFrame(FrameRequest frame) {
        Frame newFrame;

        if (frame.projectId() != null) {
            Project project = this.projectRepository.findById(frame.projectId())
                    .orElseThrow(() -> new RuntimeException("Project not found!"));

            newFrame = new Frame(frame.name(), frame.orderIndex(), project);
        } else {
            newFrame = new Frame(frame.name(), frame.orderIndex());
        }

        newFrame = this.frameRepository.save(newFrame);

        return new FrameResponse(
                newFrame.getId(),
                newFrame.getName(),
                newFrame.getOrderIndex(),
                newFrame.getProject().getId(),
                newFrame.getTasks() != null ? newFrame.getTasks().stream().map(task -> new TaskResponse(
                        task.getId(),
                        task.getName(),
                        task.getDescription(),
                        task.getInitialDate(),
                        task.getFinalDate(),
                        task.getStatus(),
                        task.getFrame() != null ? task.getFrame().getId() : null,
                        task.getAssignee() != null ? task.getAssignee().getId() : null,
                        task.getAssignee() != null ? task.getAssignee().getName() : null
                )).toList() : null

        );
    }

    @Transactional
    public FrameResponse updateFrame(Long id, FrameRequest frame) {
        Frame oldFrame = this.frameRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Frame not found!"));

        if (frame.name() != null){
            oldFrame.setName(frame.name());
        }
        if (frame.orderIndex() != null){
            oldFrame.setOrderIndex(frame.orderIndex());
        }
        if (frame.projectId() != null){
            Project project = this.projectRepository.findById(frame.projectId())
                    .orElseThrow(() -> new RuntimeException("Project not found!"));

            oldFrame.setProject(project);
        }

        Frame updatedFrame = this.frameRepository.save(oldFrame);

        return new FrameResponse(
                updatedFrame.getId(),
                updatedFrame.getName(),
                updatedFrame.getOrderIndex(),
                updatedFrame.getProject().getId(),
                updatedFrame.getTasks() != null ? updatedFrame.getTasks().stream().map(task -> new TaskResponse(
                        task.getId(),
                        task.getName(),
                        task.getDescription(),
                        task.getInitialDate(),
                        task.getFinalDate(),
                        task.getStatus(),
                        task.getFrame() != null ? task.getFrame().getId() : null,
                        task.getAssignee() != null ? task.getAssignee().getId() : null,
                        task.getAssignee() != null ? task.getAssignee().getName() : null
                )).toList() : null
        );
    }

    @Transactional
    public void deleteFrame(Long frameId) {
        Frame frame = this.frameRepository.findById(frameId)
                .orElseThrow(() -> new RuntimeException("Frame not found!"));
        this.frameRepository.delete(frame);
    }

    @Transactional
    public List<FrameResponse> findByProjectOrderByOrderIndex(Long projectId) {
        List<Frame> frames = frameRepository.findByProjectIdOrderByOrderIndexAsc(projectId);

        return frames.stream().map(e -> new FrameResponse(
                e.getId(),
                e.getName(),
                e.getOrderIndex(),
                e.getProject().getId(),
                e.getTasks() != null ? e.getTasks().stream().map(task -> new TaskResponse(
                        task.getId(),
                        task.getName(),
                        task.getDescription(),
                        task.getInitialDate(),
                        task.getFinalDate(),
                        task.getStatus(),
                        task.getFrame() != null ? task.getFrame().getId() : null,
                        task.getAssignee() != null ? task.getAssignee().getId() : null,
                        task.getAssignee() != null ? task.getAssignee().getName() : null
                )).toList() : null
        )).toList();
    }
}
