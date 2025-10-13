package br.com.ufrn.imd.Gerenciador_Projetos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import br.com.ufrn.imd.Gerenciador_Projetos.model.Usuario;
import br.com.ufrn.imd.Gerenciador_Projetos.repository.UsuarioRepository;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Usuario salvar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public List<Usuario> pesquisarPorNome(String nome) {
        if (StringUtils.hasText(nome)) {
            return usuarioRepository.findByNomeContainingIgnoreCase(nome);
        }
        return listarTodos();
    }
}
