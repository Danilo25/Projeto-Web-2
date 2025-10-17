package br.com.ufrn.imd.Project_Manager.controller;

import java.util.List;

import br.com.ufrn.imd.Project_Manager.model.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.ufrn.imd.Project_Manager.service.TeamService;

@Controller
@RequestMapping("/teams")
public class TeamController {
    
    @Autowired
    private TeamService teamService;

    @GetMapping("/create")
    public String showRegisterForm(Model model) {
        model.addAttribute("team", new Team());
        return "register-team";
    }

    @PostMapping("/save")
    public String saveTeam(@ModelAttribute Team team, @RequestParam(required = false) List<Long> users) {
        teamService.saveTeam(team, users);
        return "redirect:/teams";
    }
    
}
