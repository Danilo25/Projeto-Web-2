package br.com.ufrn.imd.Project_Manager.repository;

import br.com.ufrn.imd.Project_Manager.model.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag,Long> {
    boolean existsByNameIgnoreCase(String name);

    @Query("""
    SELECT t FROM Tag t
        WHERE :name IS NULL OR LOWER(t.name) LIKE LOWER(CONCAT('%', :name, '%'))
    """)
    Page<Tag> searchTags(@Param("name") String name, Pageable pageable);
}
