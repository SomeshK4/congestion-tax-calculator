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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Somesh Kumar
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CongestionTaxService {

    private final CityRepository cityRepository;

    /**
     * Calculate the congestion tax for the given vehicle type and entry times
     *
     * @param congestionTaxRequest
     * @return tax amount
     */
    public CongestionTaxResponse calculateCongestionTax(CongestionTaxRequest congestionTaxRequest) {
        log.info("[CongestionTaxService] [calculateCongestionTax] Request: {}", congestionTaxRequest);
        BigDecimal totalTax = BigDecimal.ZERO;
        CongestionTaxResponse.CongestionTaxResponseBuilder responseBuilder = CongestionTaxResponse.builder();

        CityEntity cityEntity = cityRepository.findByNameAndCountry(congestionTaxRequest.getCity(), congestionTaxRequest.getCountry())
                .orElseThrow(() -> new CityNotSupportedException(String.format("City %s and country %s not configured", congestionTaxRequest.getCity(), congestionTaxRequest.getCountry())));

        boolean isVehicleExempted = isVehicleExempted(cityEntity.getVehicles(), congestionTaxRequest.getVehicleType());
        if (!isVehicleExempted) {
            //Collections.sort(congestionTaxRequest.getEntryTimes());  //Sort the entry times in ascending order
            Collections.sort(congestionTaxRequest.getEntryTimes());  //Sort the entry times in ascending order
            List<ExemptionPeriodEntity> exemptionPeriods = cityEntity.getExemptionPeriods();
            congestionTaxRequest.getEntryTimes()
                    .removeIf(entryTime ->
                            isNonTaxableDate(cityEntity, exemptionPeriods, entryTime.toLocalDate())); //Remove non-taxable dates

            //First method to calculate congestion tax by day
            Map<String, BigDecimal> congestionTaxesByDay = calculateCongestionTaxByDay(congestionTaxRequest.getEntryTimes(), cityEntity, new HashMap<>());

            //Second method to calculate congestion tax by day
            //Map<String, BigDecimal> congestionTaxesByDay = calculateCongestionTaxByDay(congestionTaxRequest.getEntryTimes(), cityEntity);

            totalTax = congestionTaxesByDay.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add); //Calculate total tax for all days, can be removed if not needed in the response

            log.info("[CongestionTaxService] [calculateCongestionTax] Total Tax: {}", totalTax);
            List<CongestionTaxResponse.Tax> list = congestionTaxesByDay.entrySet().stream().map(entry ->
                    CongestionTaxResponse.Tax.builder()
                            .date(entry.getKey())
                            .amount(entry.getValue())
                            .build()).toList();

            responseBuilder.taxesByDate(list);
        }

        return responseBuilder
                .totalTax(totalTax)
                .city(congestionTaxRequest.getCity())
                .country(congestionTaxRequest.getCountry())
                .vehicleType(congestionTaxRequest.getVehicleType())
                .build();
    }

    public Map<String, BigDecimal> calculateCongestionTaxByDay(List<LocalDateTime> entryTimes, CityEntity cityEntity) {
        // Group entry times by date for daily calculation
        Map<LocalDate, List<LocalDateTime>> entriesByDate = entryTimes.stream()
                .collect(Collectors.groupingBy(LocalDateTime::toLocalDate));

        Map<String, BigDecimal> taxPerDay = new HashMap<>();

        // Process each day separately
        entriesByDate.forEach((date, times) -> {
            BigDecimal totalTaxForDay = calculateTaxForDay(times, cityEntity);

            // Apply daily charge cap
            BigDecimal dailyChargeCap = cityEntity.getDailyChargeCap();
            if (dailyChargeCap != null && totalTaxForDay.compareTo(dailyChargeCap) > 0) {
                totalTaxForDay = dailyChargeCap;
            }

            // Store the result using date as the key
            taxPerDay.put(date.toString(), totalTaxForDay);
        });

        return taxPerDay;
    }

    private BigDecimal calculateTaxForDay(List<LocalDateTime> entryTimes, CityEntity cityEntity) {
        // Sort the times for a single day in chronological order
        entryTimes.sort(Comparator.naturalOrder());

        BigDecimal totalTaxForDay = BigDecimal.ZERO;
        LocalDateTime lastProcessedTime = null;
        BigDecimal maxTaxInPeriod = BigDecimal.ZERO;

        for (LocalDateTime currentTime : entryTimes) {
            if (lastProcessedTime == null || Duration.between(lastProcessedTime, currentTime).toMinutes() > cityEntity.getSingleChargeMinutes()) {
                // If outside the 60-minute window or first entry, finalize the tax for the last window
                totalTaxForDay = totalTaxForDay.add(maxTaxInPeriod);
                maxTaxInPeriod = BigDecimal.ZERO;
                lastProcessedTime = currentTime;
            }

            // Calculate the tax for the current entry time and compare with the max tax in this period
            BigDecimal currentTax = getTaxAmountAsPerTaxRules(currentTime.toLocalTime(), cityEntity.getTaxRules());
            if (currentTax.compareTo(maxTaxInPeriod) > 0) {
                maxTaxInPeriod = currentTax;
            }
        }

        // Add the final period's max tax to the total for the day
        totalTaxForDay = totalTaxForDay.add(maxTaxInPeriod);

        return totalTaxForDay;
    }


    private Map<String, BigDecimal> calculateCongestionTaxByDay(List<LocalDateTime> entryTimes, CityEntity cityEntity, Map<String, BigDecimal> taxPerDay) {
        Set<LocalDateTime> processedTimes = new HashSet<>();
        Map<String, BigDecimal> dailyTax = new HashMap<>();
        for (int i = 0; i < entryTimes.size(); i++) {
            LocalDateTime currentEntryTime = entryTimes.get(i);
            if (!processedTimes.add(currentEntryTime)) {
                continue;
            }
            BigDecimal tax = getTaxAmountAsPerTaxRules(entryTimes.get(i).toLocalTime(), cityEntity.getTaxRules());
            for (int j = i + 1; j < entryTimes.size(); j++) {
                LocalDateTime nextEntryTime = entryTimes.get(j);
                long diffInMinutes = Duration.between(currentEntryTime, nextEntryTime).toMinutes();

                if (diffInMinutes > cityEntity.getSingleChargeMinutes()) {
                    break;
                }
                processedTimes.add(nextEntryTime);
                BigDecimal nextTax = getTaxAmountAsPerTaxRules(nextEntryTime.toLocalTime(), cityEntity.getTaxRules());
                if (nextTax.compareTo(tax) > 0) {
                    tax = nextTax;
                }
            }
            String dateString = currentEntryTime.toLocalDate().toString();
            dailyTax.merge(dateString, tax, BigDecimal::add);
            BigDecimal dailyTotal = dailyTax.get(dateString);
            if (Objects.nonNull(cityEntity.getDailyChargeCap()) && dailyTotal.compareTo(cityEntity.getDailyChargeCap()) > 0) {
                dailyTotal = cityEntity.getDailyChargeCap();
                dailyTax.put(dateString, dailyTotal);
            }
            taxPerDay.put(dateString, dailyTotal);
        }
        return taxPerDay;
    }


    private BigDecimal getTaxAmountAsPerTaxRules(LocalTime time, List<TaxRuleEntity> taxRuleEntities) {
        return taxRuleEntities.stream()
                .filter(taxRuleEntity -> {
                    LocalTime fromTime = taxRuleEntity.getFromTime();
                    LocalTime toTime = taxRuleEntity.getToTime();
                    return (time.equals(fromTime) || time.equals(toTime)) ||
                            (time.isAfter(fromTime) && time.isBefore(toTime));
                })
                .map(TaxRuleEntity::getAmount)
                .findFirst()
                .orElse(BigDecimal.ZERO);
    }


    /**
     * Check for non-taxable dates (weekend, public holidays etc.)
     *
     * @param cityEntity
     * @param date
     * @return true/false non-taxable
     */
    private boolean isNonTaxableDate(CityEntity cityEntity, List<ExemptionPeriodEntity> exemptionPeriods, LocalDate date) {
        if (cityEntity.isWeekendExempted() &&
                (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY)) {
            return true;
        }
        return exemptionPeriods.stream()
                .anyMatch(exemptionPeriodEntity -> {
                    if (exemptionPeriodEntity.getStartDate().isEqual(date) || exemptionPeriodEntity.getEndDate().isEqual(date)) {
                        return true;
                    }
                    return exemptionPeriodEntity.getStartDate().isBefore(date) &&
                            exemptionPeriodEntity.getEndDate().isAfter(date);
                });

    }

    /**
     * Check for exempt vehicles
     *
     * @param vehicleEntities
     * @param vehicleType
     * @return true/false vehicle is exempted
     */
    private boolean isVehicleExempted(List<VehicleEntity> vehicleEntities, String vehicleType) {
        VehicleEntity vehicle = vehicleEntities.stream()
                .filter(vehicleEntity -> vehicleEntity.getType().equalsIgnoreCase(vehicleType))
                .findFirst()
                .orElseThrow(() ->
                        new VehicleTypeNotSupportedException(String.format("Vehicle type %s not supported", vehicleType)));
        return vehicle.isExempted();
    }

}
