package com.volvo.congestiontax.repositories;

import com.volvo.congestiontax.entities.CityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Somesh Kumar
 */
@Repository
public interface CityRepository extends JpaRepository<CityEntity, Long> {

    Optional<CityEntity> findByNameAndCountry(String name, String country);
}
