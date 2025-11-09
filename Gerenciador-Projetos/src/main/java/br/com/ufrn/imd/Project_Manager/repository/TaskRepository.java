package br.com.ufrn.imd.Project_Manager.repository;

import br.com.ufrn.imd.Project_Manager.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    @Query("""
    SELECT t FROM Task t
        WHERE :name IS NULL OR LOWER(t.name) LIKE LOWER(CONCAT('%', :name, '%'))
    """)
    List<Task> searchTasks(@RequestParam("name") String name);
}
