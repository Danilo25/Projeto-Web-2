package br.com.ufrn.imd.Project_Manager.repository;

import br.com.ufrn.imd.Project_Manager.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<Tag,Long> {
    List<Tag> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);

    @Query("""
    SELECT t FROM Tag t
        WHERE :name IS NULL OR LOWER(t.name) LIKE LOWER(CONCAT('%', :name, '%'))
    """)
    List<Tag> searchTags(@Param("name") String name);
}
