package br.com.ufrn.imd.Project_Manager.repository;

import br.com.ufrn.imd.Project_Manager.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("""
    SELECT c FROM Comment c
        WHERE :taskId IS NULL OR c.task.id = :taskId
            AND (:text IS NULL OR LOWER(c.text) LIKE LOWER(CONCAT('%', :text, '%')))
    """)
    Page<Comment> searchComments(@Param("taskId") Long taskId,
                                 @Param("text") String text,
                                 Pageable pageable);

}
