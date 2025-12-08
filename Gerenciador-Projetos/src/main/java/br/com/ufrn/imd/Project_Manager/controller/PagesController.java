package br.com.ufrn.imd.Project_Manager.controller;


import br.com.ufrn.imd.Project_Manager.dtos.api.FrameResponse;
import br.com.ufrn.imd.Project_Manager.dtos.api.ProjectResponse;
import br.com.ufrn.imd.Project_Manager.model.User;
import br.com.ufrn.imd.Project_Manager.service.FrameService;
import br.com.ufrn.imd.Project_Manager.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import br.com.ufrn.imd.Project_Manager.repository.UserRepository;
import org.springframework.security.core.Authentication;

import java.util.List;

@Controller
public class PagesController {

    @Autowired
    private ProjectService projectService;
    @Autowired
    private FrameService frameService;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String rootRedirect() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            String email = auth.getName();
            User user = userRepository.findByEmailIgnoreCase(email).orElse(null);
            
            if (user != null) {
                return "redirect:/web/home/" + user.getId();
            }
        }
        return "redirect:/login";
    }

    @GetMapping("/web/home/{id}")
    public String showHomePage(@PathVariable Long id, Model model) {
        model.addAttribute("userId", id);
        return "users/home";
    }

    @GetMapping("/web/register")
    public String showRegisterPage() {
        return "users/register"; 
    }

    @GetMapping("/web/teams/register")
    public String showNewTeamPage(@RequestParam("creatorId") Long creatorId, Model model) {
        model.addAttribute("creatorId", creatorId);
        return "teams/team-register";
    }

    @GetMapping("/web/user/{userId}/team/{teamId}")
    public String showTeamDetailPage(@PathVariable Long userId, @PathVariable Long teamId, Model model) {
        model.addAttribute("userId", userId);
        model.addAttribute("teamId", teamId);
        return "teams/team-details";
    }

    @GetMapping("/web/profile/{id}")
    public String showProfilePage(@PathVariable Long id, Model model) {
        model.addAttribute("userId", id);
        return "users/profile"; 
    }

    @GetMapping("web/project/{userId}/{projectId}/board")
    public String showProjectBoard(@PathVariable Long projectId, Model model, @PathVariable String userId) {
        ProjectResponse project = projectService.getProjectById(projectId);

        if (project == null) {
            model.addAttribute("error", "Projeto não encontrado");
            return "error";
        }

        List<FrameResponse> frames = frameService.findByProjectOrderByOrderIndex(projectId);

        model.addAttribute("userId", userId);
        model.addAttribute("project", project);
        model.addAttribute("frames", frames);

        return "projects/project-board";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @GetMapping("/web/positions")
    public String showPositionsPage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmailIgnoreCase(email).orElse(null);
        
        if (user != null) {
            model.addAttribute("userId", user.getId());
        }
        
        return "positions/positions";
    }

    @GetMapping("/web/clients")
    public String showClientsPage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmailIgnoreCase(email).orElse(null);
        
        if (user != null) {
            model.addAttribute("userId", user.getId());
        }
        
        return "clients/clients";
    }
}
