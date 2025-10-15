package br.com.ufrn.imd.Gerenciador_Projetos.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.ufrn.imd.Gerenciador_Projetos.model.Equipe;
import br.com.ufrn.imd.Gerenciador_Projetos.service.EquipeService;

@Controller
@RequestMapping("/equipes")
public class EquipeController {
    
    @Autowired
    private EquipeService equipeService;

    @GetMapping("/nova")
    public String mostrarFormularioCadastro(Model model) {
        model.addAttribute("equipe", new Equipe());
        return "cadastro-equipe";
    }

    @PostMapping("/salvar")
    public String salvarEquipe(@ModelAttribute Equipe equipe, @RequestParam(required = false) List<Long> usuarios) {
        equipeService.salvar(equipe, usuarios);
        return "redirect:/equipes";
    }
    
}
