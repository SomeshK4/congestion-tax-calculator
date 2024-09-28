package com.volvo.congestiontax.services;

import com.volvo.congestiontax.dtos.CongestionTaxRequest;
import com.volvo.congestiontax.dtos.CongestionTaxResponse;
import com.volvo.congestiontax.entities.CityEntity;
import com.volvo.congestiontax.entities.ExemptionPeriodEntity;
import com.volvo.congestiontax.entities.TaxRuleEntity;
import com.volvo.congestiontax.entities.VehicleEntity;
import com.volvo.congestiontax.exceptions.CityNotSupportedException;
import com.volvo.congestiontax.exceptions.VehicleTypeNotSupportedException;
import com.volvo.congestiontax.repositories.CityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

/**
 * @author Somesh Kumar
 */
@ExtendWith(MockitoExtension.class)
class CongestionTaxServiceTest {

    @Mock
    private CityRepository cityRepository;

    @InjectMocks
    private CongestionTaxService congestionTaxService;

    @Test
    void testCalculateCongestionTaxWithVehicleExempted() {
        //GIVEN
        VehicleEntity vehicleEntity = VehicleEntity.builder()
                .type("Emergency")
                .exempted(true)
                .build();
        CityEntity cityEntity = cityEntity();
        cityEntity.setVehicles(List.of(vehicleEntity));
        given(cityRepository.findByNameAndCountry("Gothenburg", "Sweden")).willReturn(Optional.of(cityEntity));
        CongestionTaxRequest congestionTaxRequest = CongestionTaxRequest.builder()
                .city("Gothenburg")
                .country("Sweden")
                .vehicleType("Emergency")
                .entryTimes(List.of(LocalDateTime.of(2021, 10, 10, 9, 0)))
                .build();
        //WHEN
        CongestionTaxResponse congestionTax = congestionTaxService.calculateCongestionTax(congestionTaxRequest);

        //THEN
        assertThat(congestionTax.getTotalTax()).isZero();
    }

    @Test
    void testCalculateCongestionTaxWithVehicleNotExempted() {
        //GIVEN
        VehicleEntity vehicleEntity = VehicleEntity.builder()
                .type("Car")
                .exempted(false)
                .build();
        CityEntity cityEntity = cityEntity();
        cityEntity.setVehicles(List.of(vehicleEntity));

        given(cityRepository.findByNameAndCountry("Gothenburg", "Sweden")).willReturn(Optional.of(cityEntity));
        List<LocalDateTime> entryTimes = new ArrayList<>();
        entryTimes.add(LocalDateTime.of(2024, 10, 10, 6, 0));
        CongestionTaxRequest congestionTaxRequest = CongestionTaxRequest.builder()
                .city("Gothenburg")
                .country("Sweden")
                .vehicleType("Car")
                .entryTimes(entryTimes)
                .build();
        //WHEN
        CongestionTaxResponse congestionTax = congestionTaxService.calculateCongestionTax(congestionTaxRequest);

        //THEN
        assertThat(congestionTax.getTotalTax()).isEqualTo(BigDecimal.valueOf(8));
    }

    @Test
    void testCalculateCongestionTaxWithVehicleNotExemptedAndMultipleEntryTimes() {
        //GIVEN
        VehicleEntity vehicleEntity = VehicleEntity.builder()
                .type("Car")
                .exempted(false)
                .build();
        CityEntity cityEntity = cityEntity();
        cityEntity.setVehicles(List.of(vehicleEntity));

        given(cityRepository.findByNameAndCountry("Gothenburg", "Sweden")).willReturn(Optional.of(cityEntity));
        List<LocalDateTime> entryTimes = new ArrayList<>();
        entryTimes.add(LocalDateTime.of(2024, 10, 10, 6, 0));
        entryTimes.add(LocalDateTime.of(2024, 10, 10, 6, 29));
        entryTimes.add(LocalDateTime.of(2024, 10, 10, 6, 31));
        entryTimes.add(LocalDateTime.of(2024, 10, 10, 7, 20));
        CongestionTaxRequest congestionTaxRequest = CongestionTaxRequest.builder()
                .city("Gothenburg")
                .country("Sweden")
                .vehicleType("Car")
                .entryTimes(entryTimes)
                .build();
        //WHEN
        CongestionTaxResponse congestionTax = congestionTaxService.calculateCongestionTax(congestionTaxRequest);

        //THEN
        assertThat(congestionTax.getTotalTax()).isEqualTo(BigDecimal.valueOf(31));
    }

    @Test
    void testCalculateCongestionTaxWithVehicleNotSupported() {
        //GIVEN
        VehicleEntity vehicleEntity = VehicleEntity.builder()
                .type("Car")
                .exempted(false)
                .build();
        CityEntity cityEntity = cityEntity();
        cityEntity.setVehicles(List.of(vehicleEntity));

        given(cityRepository.findByNameAndCountry("Gothenburg", "Sweden")).willReturn(Optional.of(cityEntity));

        //WHEN THEN
        assertThatThrownBy(() -> congestionTaxService.calculateCongestionTax(CongestionTaxRequest.builder()
                .city("Gothenburg")
                .country("Sweden")
                .vehicleType("X")
                .build()))
                .isInstanceOf(VehicleTypeNotSupportedException.class)
                .hasMessage("Vehicle type X not supported");
    }

    @Test
    void testCalculateCongestionTaxWithWeekendExemption() {
        //GIVEN
        VehicleEntity vehicleEntity = VehicleEntity.builder()
                .type("Car")
                .exempted(false)
                .build();
        CityEntity cityEntity = cityEntity();
        cityEntity.setWeekendExempted(true);
        cityEntity.setVehicles(List.of(vehicleEntity));

        given(cityRepository.findByNameAndCountry("Gothenburg", "Sweden")).willReturn(Optional.of(cityEntity));
        List<LocalDateTime> entryTimes = new ArrayList<>();
        entryTimes.add(LocalDateTime.of(2024, 9, 22, 6, 0));

        //WHEN
        CongestionTaxResponse congestionTax = congestionTaxService.calculateCongestionTax(CongestionTaxRequest.builder()
                .city("Gothenburg")
                .country("Sweden")
                .vehicleType("Car")
                .entryTimes(entryTimes)
                .build());

        //THEN
        assertThat(congestionTax.getTotalTax()).isZero();

    }

    @Test
    void testCalculateCongestionTaxWithPublicHolidayExemption() {
        //GIVEN
        VehicleEntity vehicleEntity = VehicleEntity.builder()
                .type("Car")
                .exempted(false)
                .build();
        CityEntity cityEntity = cityEntity();
        cityEntity.setVehicles(List.of(vehicleEntity));

        given(cityRepository.findByNameAndCountry("Gothenburg", "Sweden")).willReturn(Optional.of(cityEntity));
        List<LocalDateTime> entryTimes = new ArrayList<>();
        entryTimes.add(LocalDateTime.of(2021, 10, 10, 6, 0));

        //WHEN
        CongestionTaxResponse congestionTax = congestionTaxService.calculateCongestionTax(CongestionTaxRequest.builder()
                .city("Gothenburg")
                .country("Sweden")
                .vehicleType("Car")
                .entryTimes(entryTimes)
                .build());

        //THEN
        assertThat(congestionTax.getTotalTax()).isZero();


    }

    @Test
    void testCalculateCongestionTaxWithCityAndCountryNotSupported() {
        //GIVEN
        given(cityRepository.findByNameAndCountry("Gothenburg", "Sweden")).willReturn(Optional.empty());

        //WHEN THEN
        assertThatThrownBy(() -> congestionTaxService.calculateCongestionTax(CongestionTaxRequest.builder()
                .city("Gothenburg")
                .country("Sweden")
                .vehicleType("Car")
                .build()))
                .isInstanceOf(CityNotSupportedException.class)
                .hasMessage("City Gothenburg and country Sweden not configured");
    }

    private CityEntity cityEntity() {
        ExemptionPeriodEntity exemptionPeriodEntity = ExemptionPeriodEntity.builder()
                .startDate(LocalDate.of(2021, 10, 10))
                .endDate(LocalDate.of(2021, 10, 10))
                .build();
        TaxRuleEntity taxRuleEntity = TaxRuleEntity.builder()
                .fromTime(LocalTime.of(6, 0))
                .toTime(LocalTime.of(6, 29))
                .amount(BigDecimal.valueOf(8))
                .build();
        TaxRuleEntity taxRuleEntity1 = TaxRuleEntity.builder()
                .fromTime(LocalTime.of(6, 30))
                .toTime(LocalTime.of(6, 59))
                .amount(BigDecimal.valueOf(13))
                .build();
        TaxRuleEntity taxRuleEntity2 = TaxRuleEntity.builder()
                .fromTime(LocalTime.of(7, 0))
                .toTime(LocalTime.of(7, 59))
                .amount(BigDecimal.valueOf(18))
                .build();
        return CityEntity.builder()
                .name("Gothenburg")
                .country("Sweden")
                .weekendExempted(true)
                .dailyChargeCap(BigDecimal.valueOf(60))
                .singleChargeMinutes(60)
                .taxRules(List.of(taxRuleEntity, taxRuleEntity1, taxRuleEntity2))
                .exemptionPeriods(List.of(exemptionPeriodEntity))
                .build();

    }

}
