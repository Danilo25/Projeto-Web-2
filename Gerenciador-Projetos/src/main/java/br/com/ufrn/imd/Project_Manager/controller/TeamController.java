package br.com.ufrn.imd.Project_Manager.controller;

import br.com.ufrn.imd.Project_Manager.dtos.TeamRequest;
import br.com.ufrn.imd.Project_Manager.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.ArrayList;

@Controller
@RequestMapping("/teams")
public class TeamController {

    @Autowired
    private TeamService teamService;
    @GetMapping("/register")
    public String showRegisterTeamForm(@RequestParam Long creatorId, Model model) {
        TeamRequest teamRequest = new TeamRequest(
                "",
                "",
                new ArrayList<>(),
                creatorId          
        );
        model.addAttribute("team", teamRequest);
        return "register-team";
    }

    @PostMapping("/save")
    public String saveTeam(
            @ModelAttribute("team") TeamRequest teamRequest,
            RedirectAttributes redirectAttributes) {
        if (teamRequest.creatorId() == null) {
             redirectAttributes.addFlashAttribute("errorMessage", "Erro: ID do criador não encontrado no formulário.");
             return "redirect:/";
        }
        try {
            teamService.createTeam(teamRequest, teamRequest.creatorId()); 
            redirectAttributes.addFlashAttribute("successMessage", "Equipe criada com sucesso!");
            return "redirect:/users/dashboard/" + teamRequest.creatorId() + "/teams"; 
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao criar equipe: " + e.getMessage());
            return "redirect:/teams/register?creatorId=" + teamRequest.creatorId(); 
        }
    }
}