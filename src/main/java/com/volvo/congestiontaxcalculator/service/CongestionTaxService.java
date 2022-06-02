package com.volvo.congestiontaxcalculator.service;

import com.volvo.congestiontaxcalculator.model.CongestionTaxRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface CongestionTaxService {

    BigDecimal congestionTax(String vehicleType, List<LocalDateTime> dateTimeList);
}
