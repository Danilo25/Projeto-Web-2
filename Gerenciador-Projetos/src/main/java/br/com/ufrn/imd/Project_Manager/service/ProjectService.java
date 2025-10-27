package br.com.ufrn.imd.Project_Manager.service;

import br.com.ufrn.imd.Project_Manager.dtos.ProjectRequest;
import br.com.ufrn.imd.Project_Manager.dtos.ProjectResponse;
import br.com.ufrn.imd.Project_Manager.model.Frame;
import br.com.ufrn.imd.Project_Manager.model.Project;
import br.com.ufrn.imd.Project_Manager.model.Team;
import br.com.ufrn.imd.Project_Manager.repository.FrameRepository;
import br.com.ufrn.imd.Project_Manager.repository.ProjectRepository;
import br.com.ufrn.imd.Project_Manager.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private FrameRepository frameRepository;

    public List<ProjectResponse> findByName(String projectName) {
        List<Project> projects = this.projectRepository.findByNameIgnoreCase(projectName);

        return projects.stream().map(e -> new ProjectResponse(
                e.getId(),
                e.getName(),
                e.getDescription(),
                e.getInitialDate(),
                e.getFinalDate(),
                e.getStatus(),
                e.getTeam().getId(),
                e.getFrames().stream().map(Frame::getId).toList()
        )).toList();
    }

    public ProjectResponse getProjectById(Long projectId) {
        Project project = this.projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found!"));

        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getInitialDate(),
                project.getFinalDate(),
                project.getStatus(),
                project.getTeam().getId(),
                project.getFrames().stream().map(Frame::getId).toList()
        );
    }

    public List<ProjectResponse> listAllProjects() {
        return this.projectRepository.findAll().stream().map(e -> new ProjectResponse(
                e.getId(),
                e.getName(),
                e.getDescription(),
                e.getInitialDate(),
                e.getFinalDate(),
                e.getStatus(),
                e.getTeam().getId(),
                e.getFrames().stream().map(Frame::getId).toList()
        )).toList();
    }

    @Transactional
    public ProjectResponse createProject(ProjectRequest project) {
        Team team = teamRepository.findById(project.teamId())
                .orElseThrow(() -> new RuntimeException("Team not found!"));

        Project newProject = new Project(
                project.name(),
                project.description(),
                project.initialDate(),
                project.finalDate(),
                project.status(),
                team
        );

        Project savedProject = this.projectRepository.save(newProject);

        return new ProjectResponse(
                savedProject.getId(),
                savedProject.getName(),
                savedProject.getDescription(),
                savedProject.getInitialDate(),
                savedProject.getFinalDate(),
                savedProject.getStatus(),
                savedProject.getTeam().getId(),
                savedProject.getFrames().stream().map(Frame::getId).toList()
        );
    }

    @Transactional
    public ProjectResponse updateProject(Long projectId, ProjectRequest project){
        Project oldProject = this.projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found!"));

        if (project.name() != null) {
            oldProject.setName(project.name());
        }
        if (project.description() != null) {
            oldProject.setDescription(project.description());
        }
        if (project.initialDate() != null) {
            oldProject.setInitialDate(project.initialDate());
        }
        if (project.finalDate() != null) {
            oldProject.setFinalDate(project.finalDate());
        }
        if (project.status() != null){
            oldProject.setStatus(project.status());
        }
        if (project.teamId() != null) {
            Team oldTeam = teamRepository.findById(project.teamId())
                    .orElseThrow(() -> new RuntimeException("Team not found!"));

            oldProject.setTeam(oldTeam);
        }

        Project updatedProject = this.projectRepository.save(oldProject);

        return new ProjectResponse(
                updatedProject.getId(),
                updatedProject.getName(),
                updatedProject.getDescription(),
                updatedProject.getInitialDate(),
                updatedProject.getFinalDate(),
                updatedProject.getStatus(),
                updatedProject.getTeam().getId(),
                updatedProject.getFrames().stream().map(Frame::getId).toList()
        );
    }

    @Transactional
    public void deleteProject(Long id) {
        Project foundProject = this.projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found!"));
        this.projectRepository.delete(foundProject);
    }

    @Transactional
    public ProjectResponse addFrameToProject(Long projectId, Long frameId) {
        Project project = this.projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found!"));

        Frame frame = this.frameRepository.findById(frameId)
                .orElseThrow(() -> new RuntimeException("Frame not found!"));

        if (frame.getProject() == null) {
            frame.setProject(project);
            this.frameRepository.save(frame);

            Project updatedProject = this.projectRepository.findById(projectId).get();
            return new ProjectResponse(
                    updatedProject.getId(),
                    updatedProject.getName(),
                    updatedProject.getDescription(),
                    updatedProject.getInitialDate(),
                    updatedProject.getFinalDate(),
                    updatedProject.getStatus(),
                    updatedProject.getTeam().getId(),
                    updatedProject.getFrames().stream().map(Frame::getId).toList()
            );
        } else {
            throw new RuntimeException("Frame is already associated with a project!");
        }
    }

    @Transactional
    public ProjectResponse removeFrameFromProject(Long projectId, Long frameId){
        Project project = this.projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found!"));

        Frame frame = this.frameRepository.findById(frameId)
                .orElseThrow(() -> new RuntimeException("Frame not found!"));

        if (frame.getProject() != null && frame.getProject().getId().equals(project.getId())) {
            frame.setProject(null);
            this.frameRepository.save(frame);

            Project updatedProject = this.projectRepository.findById(projectId).get();
            return new ProjectResponse(
                    updatedProject.getId(),
                    updatedProject.getName(),
                    updatedProject.getDescription(),
                    updatedProject.getInitialDate(),
                    updatedProject.getFinalDate(),
                    updatedProject.getStatus(),
                    updatedProject.getTeam().getId(),
                    updatedProject.getFrames().stream().map(Frame::getId).toList()
            );
        } else {
            throw new RuntimeException("Frame is not associated with the specified project!");
        }
    }

}
