package br.com.ufrn.imd.Project_Manager.controller;

import br.com.ufrn.imd.Project_Manager.dtos.ProjectRequest;
import br.com.ufrn.imd.Project_Manager.dtos.ProjectResponse;
import br.com.ufrn.imd.Project_Manager.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects")
public class ProjectApiController {

    @Autowired
    private ProjectService projectService;

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getAllProjects() {
        List<ProjectResponse> projects = projectService.listAllProjects();
        return ResponseEntity.ok().body(projects);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable Long id) {
        ProjectResponse project = projectService.getProjectById(id);
        return ResponseEntity.ok().body(project);
    }

    @GetMapping("/search/{name}")
    public ResponseEntity<List<ProjectResponse>> getProjectsByName(@PathVariable String name) {
        List<ProjectResponse> projects = projectService.findByName(name);
        return ResponseEntity.ok().body(projects);
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@RequestBody ProjectRequest project) {
        ProjectResponse newProject = projectService.createProject(project);
        return ResponseEntity.ok().body(newProject);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProjectResponse> updateProject(@PathVariable Long id, @RequestBody ProjectRequest project) {
        ProjectResponse updatedProject = projectService.updateProject(id, project);
        return ResponseEntity.ok().body(updatedProject);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{projectId}/add-frame/{frameId}")
    public ResponseEntity<ProjectResponse> addFrameToProject(@PathVariable Long projectId, @PathVariable Long frameId) {
        ProjectResponse updatedProject = projectService.addFrameToProject(projectId, frameId);
        return ResponseEntity.ok().body(updatedProject);
    }

    @PutMapping("/{projectId}/remove-frame/{frameId}")
    public ResponseEntity<ProjectResponse> removeFrameFromProject(@PathVariable Long projectId, @PathVariable Long frameId) {
        ProjectResponse updatedProject = projectService.removeFrameFromProject(projectId, frameId);
        return ResponseEntity.ok().body(updatedProject);
    }
}
