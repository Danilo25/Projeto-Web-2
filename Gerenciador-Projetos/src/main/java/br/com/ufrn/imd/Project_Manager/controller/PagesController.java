package br.com.ufrn.imd.Project_Manager.controller;

import br.com.ufrn.imd.Project_Manager.dtos.UserResponse;
import br.com.ufrn.imd.Project_Manager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class PagesController {

    @Autowired
    private UserService userService;
    @GetMapping("/")
    public String showIndexPage(Model model) {
        List<UserResponse> users = userService.listAllUsers();
        model.addAttribute("users", users);
        return "index";
    }
    
}
