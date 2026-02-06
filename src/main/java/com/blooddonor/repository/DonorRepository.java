package com.blooddonor.repository;

import com.blooddonor.model.Donor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DonorRepository extends JpaRepository<Donor, Long> {

    Optional<Donor> findByEmail(String email);

    List<Donor> findByBloodGroupAndCity(String bloodGroup, String city);

    List<Donor> findByBloodGroupAndCityAndAvailabilityStatus(
            String bloodGroup, 
            String city, 
            Donor.AvailabilityStatus status
    );

    List<Donor> findByCity(String city);

    List<Donor> findByBloodGroup(String bloodGroup);
    
    boolean existsByEmail(String email);

    List<Donor> findByIsVerifiedTrue();
}
