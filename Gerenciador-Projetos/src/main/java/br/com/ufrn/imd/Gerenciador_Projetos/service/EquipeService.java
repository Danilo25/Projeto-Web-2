package br.com.ufrn.imd.Gerenciador_Projetos.service;

import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ufrn.imd.Gerenciador_Projetos.model.Equipe;
import br.com.ufrn.imd.Gerenciador_Projetos.model.Usuario;
import br.com.ufrn.imd.Gerenciador_Projetos.repository.EquipeRepository;
import br.com.ufrn.imd.Gerenciador_Projetos.repository.UsuarioRepository;

@Service
public class EquipeService {

    @Autowired
    private EquipeRepository equipeRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Equipe salvar(Equipe equipe, List<Long> idsUsuarios) {
        if (idsUsuarios != null && !idsUsuarios.isEmpty()) {
            List<Usuario> usuarios = usuarioRepository.findAllById(idsUsuarios);
            equipe.setUsuarios(new HashSet<>(usuarios));
        }
        return equipeRepository.save(equipe);
    }
    
}
