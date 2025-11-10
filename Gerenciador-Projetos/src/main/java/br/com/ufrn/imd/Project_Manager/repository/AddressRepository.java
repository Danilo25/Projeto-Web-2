package br.com.ufrn.imd.Project_Manager.repository;

import br.com.ufrn.imd.Project_Manager.model.Address;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    
    Optional<Address> findByUserId(Long userId); 

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN TRUE ELSE FALSE END " +
           "FROM Address a WHERE lower(a.publicPlace) = lower(:publicPlace) " +
           "AND lower(a.city) = lower(:city) " +
           "AND lower(a.state) = lower(:state) " +
           "AND lower(a.zipCode) = lower(:zipCode)")
    boolean existsAddress(
            @Param("publicPlace") String publicPlace,
            @Param("city") String city,
            @Param("state") String state,
            @Param("zipCode") String zipCode);

    @Query("""
        SELECT a FROM Address a
        WHERE (:city IS NULL OR LOWER(a.city) LIKE LOWER(CONCAT('%', :city, '%')))
          AND (:state IS NULL OR LOWER(a.state) LIKE LOWER(CONCAT('%', :state, '%')))
          AND (:zipCode IS NULL OR a.zipCode LIKE CONCAT(:zipCode, '%'))
          AND (:district IS NULL OR LOWER(a.district) LIKE LOWER(CONCAT('%', :district, '%')))
          AND (:publicPlace IS NULL OR LOWER(a.publicPlace) LIKE LOWER(CONCAT('%', :publicPlace, '%')))
          AND (:userId IS NULL OR a.user.id = :userId)
    """)
    Page<Address> searchAddresses(@Param("city") String city,
                                  @Param("state") String state,
                                  @Param("zipCode") String zipCode,
                                  @Param("district") String district,
                                  @Param("publicPlace") String publicPlace,
                                  @Param("userId") Long userId,
                                  Pageable pageable);
}
