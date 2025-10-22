package br.com.ufrn.imd.Project_Manager.service;

import br.com.ufrn.imd.Project_Manager.dtos.ProjectRequest;
import br.com.ufrn.imd.Project_Manager.dtos.ProjectResponse;
import br.com.ufrn.imd.Project_Manager.model.Project;
import br.com.ufrn.imd.Project_Manager.model.Team;
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

    public List<ProjectResponse> findByName(String projectName) {
        List<Project> projects = this.projectRepository.findByNameIgnoreCase(projectName);

        return projects.stream().map(e -> new ProjectResponse(
                e.getId(),
                e.getName(),
                e.getDescription(),
                e.getInitialDate(),
                e.getFinalDate(),
                e.getStatus(),
                e.getTeam().getId()
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
                project.getTeam().getId()
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
                e.getTeam().getId()
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
                savedProject.getTeam().getId()
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
                updatedProject.getTeam().getId()
        );
    }

    @Transactional
    public void deleteProject(Long id) {
        Project foundProject = this.projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found!"));
        this.projectRepository.delete(foundProject);
    }

    @Transactional
    public void addFrameToProject(){}

    @Transactional
    public void removeFrameFromProject(){}

}
