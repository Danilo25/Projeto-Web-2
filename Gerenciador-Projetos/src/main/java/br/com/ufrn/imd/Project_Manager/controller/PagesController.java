package br.com.ufrn.imd.Project_Manager.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

@Controller
public class PagesController {
    @GetMapping("/")
    public String index() {
        return "index";
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
}
