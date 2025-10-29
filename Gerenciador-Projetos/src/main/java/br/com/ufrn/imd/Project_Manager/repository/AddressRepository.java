package br.com.ufrn.imd.Project_Manager.repository;

import br.com.ufrn.imd.Project_Manager.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long>, JpaSpecificationExecutor<Address> {
    
    Optional<Address> findByUserId(Long userId); 

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN TRUE ELSE FALSE END " +
           "FROM Address a WHERE lower(a.publicPlace) = lower(:publicPlace) " +
           "AND lower(a.city) = lower(:city) " +
           "AND lower(a.state) = lower(:state) " +
           "AND lower(a.zipCode) = lower(:zipCode)")
    boolean existsAdress(
            @Param("publicPlace") String publicPlace,
            @Param("city") String city,
            @Param("state") String state,
            @Param("zipCode") String zipCode);
}