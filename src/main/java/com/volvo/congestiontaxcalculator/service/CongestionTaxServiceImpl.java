package com.volvo.congestiontaxcalculator.service;

import com.volvo.congestiontaxcalculator.config.CongestionTaxConfigProperties;
import com.volvo.congestiontaxcalculator.config.Months;
import com.volvo.congestiontaxcalculator.config.Timings;
import com.volvo.congestiontaxcalculator.config.Years;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CongestionTaxServiceImpl implements CongestionTaxService{

    private final CongestionTaxConfigProperties congestionTaxConfigProperties;

    @Override
    public BigDecimal congestionTax(String vehicleType, List<LocalDateTime> dateTimeList) {

        BigDecimal totalFee = new BigDecimal(0);

        LocalDateTime[] dates = dateTimeList.toArray(LocalDateTime[]::new);

        for (int i = 0; i < dates.length; i++) {
            BigDecimal tollFee;
            if (i == 0 || timeDiff(dates[i - 1], dates[i])) {
                tollFee = getTollFee(dates[i], vehicleType);
                totalFee = totalFee.add(tollFee);
            }
        }

        if (totalFee.intValue() > congestionTaxConfigProperties.getSingleCharge()) {
            totalFee = new BigDecimal(congestionTaxConfigProperties.getSingleCharge());
        }
        return totalFee;
    }

    private boolean timeDiff(LocalDateTime d1, LocalDateTime d2) {
        long diff = ChronoUnit.MINUTES.between(d1, d2);
        return diff > congestionTaxConfigProperties.getSingleCharge();
    }

    private BigDecimal getTollFee(LocalDateTime localDateTime, String vehicle) {
        if (isTollFreeDate(localDateTime) || isTollFreeVehicle(vehicle))
            return BigDecimal.valueOf(0);

        LocalTime localTime = localDateTime.toLocalTime();
        Optional<Timings> timings = congestionTaxConfigProperties.getTimings().stream()
                .filter(timing -> timing.checkIfTimeIsBetweenConfiguredTime(localTime))
                .findFirst();
        if (timings.isPresent()) {
           return timings.get().getAmount();
        }
        return BigDecimal.valueOf(0);
    }


    private boolean isTollFreeVehicle(String vehicleType) {
        if(Objects.isNull(vehicleType)) {
            return false;
        }
        return congestionTaxConfigProperties.getExemptVehicles()
                .contains(vehicleType);
    }

    private boolean isTollFreeDate(LocalDateTime localDateTime) {

        int year = localDateTime.getYear();
        int month = localDateTime.getMonthValue();
        DayOfWeek dayOfWeek = localDateTime.getDayOfWeek();
        int dayOfMonth = localDateTime.getDayOfMonth();

        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY)
            return true;

        for (Years years : congestionTaxConfigProperties.getHolidays().getYears()) {
            if (years.getYear() == year) {
                for (Months months : years.getMonths()) {
                    if (months.getMonth() == month && (months.getDates().isEmpty() || months.getDates().contains(dayOfMonth))) {
                            return true;
                    }
                }
            }
        }

        return false;
    }
}
