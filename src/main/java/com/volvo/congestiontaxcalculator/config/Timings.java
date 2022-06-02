package com.volvo.congestiontaxcalculator.config;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalTime;

@Data
public class Timings {

    private BigDecimal amount;
    private String startTime;
    private String endTime;

    public boolean checkIfTimeIsBetweenConfiguredTime(LocalTime localTime) {
        LocalTime localStartTime = LocalTime.parse(this.startTime);
        LocalTime localEndTime = LocalTime.parse(this.endTime);
        return localTime.isAfter(localStartTime) && localTime.isBefore(localEndTime);
    }

}
