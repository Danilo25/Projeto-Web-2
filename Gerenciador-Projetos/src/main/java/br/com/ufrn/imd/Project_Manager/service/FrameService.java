package br.com.ufrn.imd.Project_Manager.service;

import br.com.ufrn.imd.Project_Manager.dtos.api.FrameRequest;
import br.com.ufrn.imd.Project_Manager.dtos.api.FrameResponse;
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

    @Autowired
    private TaskService taskService;

    public FrameResponse toFrameResponse(Frame frame) {
        return new FrameResponse(
                frame.getId(),
                frame.getName(),
                frame.getOrderIndex(),
                frame.getProject() != null ? frame.getProject().getId() : null,
                frame.getTasks() != null ?
                        frame.getTasks().stream().map(taskService::toTaskResponse).toList()
                        : null
                );
    }

    public List<FrameResponse> searchFrames(String name) {
        List<Frame> frames = this.frameRepository.searchFrames(name);
        return frames.stream().map(this::toFrameResponse).toList();
    }

    public FrameResponse getFrameById(Long frameId) {
        Frame frame = this.frameRepository.findById(frameId)
                .orElseThrow(() -> new RuntimeException("Frame not found!"));

        return toFrameResponse(frame);
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

        return toFrameResponse(newFrame);
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

        return toFrameResponse(updatedFrame);
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

        return frames.stream().map(this::toFrameResponse).toList();
    }
}
