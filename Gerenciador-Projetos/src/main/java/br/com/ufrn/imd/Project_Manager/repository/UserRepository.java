package br.com.ufrn.imd.Project_Manager.repository;

import br.com.ufrn.imd.Project_Manager.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByNameIgnoreCase(String name);
    Optional<User> findByEmail(String email);

    Optional<User> findByEmailIgnoreCase(String email);
    Optional<User> findByNameIgnoreCase(String name);

    @Query("""
    SELECT u FROM User u
        WHERE (:name IS NULL OR LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%')))
        AND (:position IS NULL OR LOWER(CONCAT(u.position.name, ' ', u.position.level)) LIKE LOWER(CONCAT('%', :position, '%')))
    """)
    Page<User> searchUsers(@Param("name") String name,
                           @Param("position") String position,
                           Pageable pageable);
}
