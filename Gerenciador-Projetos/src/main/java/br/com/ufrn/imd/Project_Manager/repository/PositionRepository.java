package br.com.ufrn.imd.Project_Manager.repository;

import br.com.ufrn.imd.Project_Manager.model.Position;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PositionRepository extends JpaRepository<Position, Long> {
    boolean existsByNameAndLevel(String name, String level);

    @Query("""
    SELECT p FROM Position p
        WHERE (:text IS NULL OR LOWER(CONCAT(p.name, ' ', p.level)) LIKE LOWER(CONCAT('%', :text, '%')))
    """)
    Page<Position> searchPositions(@Param("text") String text, Pageable pageable);
}
