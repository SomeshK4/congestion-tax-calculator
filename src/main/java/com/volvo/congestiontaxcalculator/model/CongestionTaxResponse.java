package com.volvo.congestiontaxcalculator.model;

import java.math.BigDecimal;


public record CongestionTaxResponse(String vehicleType, BigDecimal tax) {

}
