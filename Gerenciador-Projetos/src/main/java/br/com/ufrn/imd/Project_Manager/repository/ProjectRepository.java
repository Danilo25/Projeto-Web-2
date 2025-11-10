package br.com.ufrn.imd.Project_Manager.repository;

import br.com.ufrn.imd.Project_Manager.model.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Query("""
        SELECT p FROM Project p
        WHERE (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))
            AND (:teamId IS NULL OR p.team.id = :teamId)
    """)
    Page<Project> searchProjects(@Param("name") String name,
                                 @Param("teamId") Long teamId,
                                 Pageable pageable);
}
