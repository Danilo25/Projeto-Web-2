package br.com.ufrn.imd.Project_Manager.repository;

import br.com.ufrn.imd.Project_Manager.model.Frame;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FrameRepository extends JpaRepository<Frame, Long> {
    List<Frame> findByProjectIdOrderByOrderIndexAsc(Long projectId);

    @Query("""
    SELECT f FROM Frame f
        WHERE :name IS NULL OR LOWER(f.name) LIKE LOWER(CONCAT('%', :name, '%'))
    """)
    Page<Frame> searchFrames(@Param("name") String name, Pageable pageable);
}
