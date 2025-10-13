package br.com.ufrn.imd.Gerenciador_Projetos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.ufrn.imd.Gerenciador_Projetos.model.Usuario;

import java.util.List;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    List<Usuario> findByNomeContainingIgnoreCase(String nome);
}
