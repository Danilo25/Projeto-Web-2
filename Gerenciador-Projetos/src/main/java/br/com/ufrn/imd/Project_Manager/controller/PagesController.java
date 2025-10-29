package br.com.ufrn.imd.Project_Manager.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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
}
