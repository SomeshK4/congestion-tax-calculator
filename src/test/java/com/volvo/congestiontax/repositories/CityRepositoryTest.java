package com.volvo.congestiontax.repositories;

import com.volvo.congestiontax.entities.CityEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * @author Somesh Kumar
 */
@DataJpaTest
@ActiveProfiles("test")
class CityRepositoryTest {

    @Autowired
    private CityRepository cityRepository;

    @Test
    void testFindByNameAndCountry() {
        //GIVEN
        CityEntity cityEntity = CityEntity.builder()
                .id(1L)
                .singleChargeMinutes(60)
                .dailyChargeCap(BigDecimal.valueOf(60))
                .weekendExempted(true)
                .name("Gothenburg")
                .country("Sweden")
                .build();
        //WHEN
        cityRepository.save(cityEntity);

        //THEN
        CityEntity city = cityRepository.findByNameAndCountry("Gothenburg", "Sweden").get();
        assertThat(city.getName()).isEqualTo("Gothenburg");
        assertThat(city.getCountry()).isEqualTo("Sweden");

    }

    @Test
    void testFindByNameAndCountryWithInvalidCity() {
        //GIVEN
        CityEntity cityEntity = CityEntity.builder()
                .id(1L)
                .singleChargeMinutes(60)
                .dailyChargeCap(BigDecimal.valueOf(60))
                .weekendExempted(true)
                .name("Gothenburg")
                .country("Sweden")
                .build();
        //WHEN
        cityRepository.save(cityEntity);

        //THEN
        assertThat(cityRepository.findByNameAndCountry("Gothenburg", "Norway")).isEmpty();
    }


}
