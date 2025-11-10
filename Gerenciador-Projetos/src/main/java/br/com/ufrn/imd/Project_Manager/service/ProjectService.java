package br.com.ufrn.imd.Project_Manager.service;

import br.com.ufrn.imd.Project_Manager.dtos.api.ProjectRequest;
import br.com.ufrn.imd.Project_Manager.dtos.api.ProjectResponse;
import br.com.ufrn.imd.Project_Manager.model.Frame;
import br.com.ufrn.imd.Project_Manager.model.Project;
import br.com.ufrn.imd.Project_Manager.model.Team;
import br.com.ufrn.imd.Project_Manager.repository.FrameRepository;
import br.com.ufrn.imd.Project_Manager.repository.ProjectRepository;
import br.com.ufrn.imd.Project_Manager.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private FrameRepository frameRepository;

    private ProjectResponse toProjectResponse(Project project) {
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

    public Page<ProjectResponse> searchProjects(String name, Long teamId, Pageable pageable) {
        Page<Project> projects = this.projectRepository.searchProjects(name, teamId, pageable);
        return projects.map(this::toProjectResponse);
    }

    public ProjectResponse getProjectById(Long projectId) {
        Project project = this.projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found!"));
        return toProjectResponse(project);
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

        return this.toProjectResponse(savedProject);
    }

    @Transactional
    public ProjectResponse updateProject(Long projectId, ProjectRequest project) {
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
        if (project.status() != null) {
            oldProject.setStatus(project.status());
        }
        if (project.teamId() != null) {
            Team oldTeam = teamRepository.findById(project.teamId())
                    .orElseThrow(() -> new RuntimeException("Team not found!"));

            oldProject.setTeam(oldTeam);
        }

        Project updatedProject = this.projectRepository.save(oldProject);

        return this.toProjectResponse(updatedProject);
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
            return this.toProjectResponse(updatedProject);
        } else {
            throw new RuntimeException("Frame is already associated with a project!");
        }
    }

    @Transactional
    public ProjectResponse removeFrameFromProject(Long projectId, Long frameId) {
        Project project = this.projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found!"));

        Frame frame = this.frameRepository.findById(frameId)
                .orElseThrow(() -> new RuntimeException("Frame not found!"));

        if (frame.getProject() != null && frame.getProject().getId().equals(project.getId())) {
            frame.setProject(null);
            this.frameRepository.save(frame);

            Project updatedProject = this.projectRepository.findById(projectId).get();
            return this.toProjectResponse(updatedProject);
        } else {
            throw new RuntimeException("Frame is not associated with the specified project!");
        }
    }
}
