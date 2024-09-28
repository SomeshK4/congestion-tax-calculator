package com.volvo.congestiontax.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Somesh Kumar
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CongestionTaxResponse {

    private BigDecimal totalTax;
    private List<Tax> taxesByDate;
    private String city;
    private String country;
    private String vehicleType;


    @Data
    @Builder
    public static class Tax {
        private String date;
        private BigDecimal amount;
    }
}
