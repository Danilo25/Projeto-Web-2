package br.com.ufrn.imd.Project_Manager.repository;

import br.com.ufrn.imd.Project_Manager.model.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    Optional<Client> findByNameAndCompanyAndEmailAllIgnoreCase(String name, String company, String email);

    @Query("""
        SELECT c FROM Client c
        WHERE (:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')))
            AND (:company IS NULL OR LOWER(c.company) LIKE LOWER(CONCAT('%', :company, '%')))
            AND (:email IS NULL OR LOWER(c.email) LIKE LOWER(CONCAT('%', :email, '%')))
    """)
    Page<Client> searchClients(@Param("name") String name,
                               @Param("company") String company,
                               @Param("email") String email,
                               Pageable pageable);
}
