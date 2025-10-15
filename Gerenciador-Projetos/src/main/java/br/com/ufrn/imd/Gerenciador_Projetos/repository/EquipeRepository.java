package br.com.ufrn.imd.Gerenciador_Projetos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import br.com.ufrn.imd.Gerenciador_Projetos.model.Equipe;

@Repository
public interface EquipeRepository extends JpaRepository<Equipe, Long> {
    List<Equipe> findByNomeContainingIgnoreCase(String nome);
}
