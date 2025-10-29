package br.com.ufrn.imd.Project_Manager.controller;

import br.com.ufrn.imd.Project_Manager.dtos.AddressRequest;
import br.com.ufrn.imd.Project_Manager.dtos.AddressResponse;
import br.com.ufrn.imd.Project_Manager.dtos.TeamResponse;
import br.com.ufrn.imd.Project_Manager.dtos.UserRequest;
import br.com.ufrn.imd.Project_Manager.dtos.UserResponse;
import br.com.ufrn.imd.Project_Manager.model.User;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.ufrn.imd.Project_Manager.service.UserService;
import br.com.ufrn.imd.Project_Manager.service.AddressService;
import br.com.ufrn.imd.Project_Manager.service.TeamService;

import org.springframework.web.bind.annotation.RequestBody;


@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private TeamService teamService;

    @GetMapping("/dashboard/{userId}")
    public String showDashboard(@PathVariable Long userId, Model model) {
        try {
            UserResponse user = userService.getUserById(userId);
            model.addAttribute("user", user);
            return "users/dashboard";
        } catch (Exception e) {
            return "redirect:/";
        }
    }

    @GetMapping("/dashboard/{userId}/teams")
    public String showUserTeams(@PathVariable Long userId, Model model, RedirectAttributes redirectAttributes) {
        try {
            Set<TeamResponse> teams = teamService.findTeamsByUserId(userId); 
            model.addAttribute("teams", teams);
            model.addAttribute("userId", userId); 
            return "users/my-teams"; 
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao buscar equipes: " + e.getMessage());
            return "redirect:/";
        }
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("userRequest", new UserRequest("", "", "", ""));
        return "users/register";
    }

    @PostMapping("/save")
    public String saveUserRegistration(
            @ModelAttribute("userRequest") UserRequest userRequest,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        model.addAttribute("userRequest", userRequest);
        try {
            userService.createUser(userRequest);
            redirectAttributes.addFlashAttribute("successMessage", "Usuário cadastrado com sucesso! Faça o login.");
            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Erro ao cadastrar usuário: " + e.getMessage());
            model.addAttribute("userRequest", userRequest);
            return "users/register";
        }
    }
    

    @GetMapping("/profile/{userId}")
    public String showUserProfile(@PathVariable Long userId, Model model, RedirectAttributes redirectAttributes) {
        try {
            UserResponse userResponse = userService.getUserById(userId);
            model.addAttribute("user", userResponse);
            return "users/profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Usuário não encontrado.");
            return "redirect:/";
        }
    }
    

}
