package br.com.ufrn.imd.Gerenciador_Projetos.controller;

import br.com.ufrn.imd.Gerenciador_Projetos.model.Usuario;
import br.com.ufrn.imd.Gerenciador_Projetos.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioApiController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/search")
    public List<Usuario> searchUsuarios(@RequestParam String nome) {
        return usuarioService.pesquisarPorNome(nome);
    }
}
