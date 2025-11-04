package br.com.ufrn.imd.Project_Manager.controller;

import br.com.ufrn.imd.Project_Manager.dtos.DashboardPageProjectResponse;
import br.com.ufrn.imd.Project_Manager.dtos.DashboardPageTeamResponse;
import br.com.ufrn.imd.Project_Manager.dtos.api.TeamResponse;
import br.com.ufrn.imd.Project_Manager.model.Team;
import br.com.ufrn.imd.Project_Manager.service.ProjectService;
import br.com.ufrn.imd.Project_Manager.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    TeamService teamService;

    @Autowired
    ProjectService projectService;

    @GetMapping("/{id}")
    public String dashboard(@PathVariable Long id, Model model) {
        Set<DashboardPageTeamResponse> teams = teamService.findTeamsByUserIdForDashboardPage(id);
        model.addAttribute("teams", teams);

        List<DashboardPageProjectResponse> projects = projectService.getProjectsByTeamIdForDashboardPage(id);
        model.addAttribute("projects", projects);

        return "dashboard";
    }
}
