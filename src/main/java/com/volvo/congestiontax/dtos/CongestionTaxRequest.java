package com.volvo.congestiontax.dtos;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Somesh Kumar
 */
@Data
@Builder
public class CongestionTaxRequest {
    private String vehicleType;
    private List<LocalDateTime> entryTimes;
    private String city;
    private String country;

}
