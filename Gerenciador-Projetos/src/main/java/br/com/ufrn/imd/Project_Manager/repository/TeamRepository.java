package br.com.ufrn.imd.Project_Manager.repository;

import br.com.ufrn.imd.Project_Manager.model.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Set;

import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    
    boolean existsByNameIgnoreCase(String name);
    Optional<Team> findByNameIgnoreCase(String name);

    @Query("""
        SELECT t FROM Team t
        WHERE (:name IS NULL OR LOWER(t.name) LIKE LOWER(CONCAT('%', :name, '%')))
            AND (:memberId IS NULL OR EXISTS (
                SELECT 1 FROM t.users u WHERE u.id = :memberId
            ))
    """)
    Page<Team> searchTeams(@Param("name") String name,
                           @Param("memberId") Long memberId,
                           Pageable pageable);
    
}
