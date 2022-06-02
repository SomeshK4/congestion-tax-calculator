package com.volvo.congestiontaxcalculator.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "congestion-tax")
@Data
public class CongestionTaxConfigProperties {
    private List<Timings> timings;
    private List<String> exemptVehicles;
    private Holidays holidays;
    private Integer singleCharge;
}
