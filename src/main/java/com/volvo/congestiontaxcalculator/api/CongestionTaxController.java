package com.volvo.congestiontaxcalculator.api;

import com.volvo.congestiontaxcalculator.model.CongestionTaxRequest;
import com.volvo.congestiontaxcalculator.model.CongestionTaxResponse;
import com.volvo.congestiontaxcalculator.service.CongestionTaxService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/road-tolls")
@RequiredArgsConstructor
public class CongestionTaxController {

    private final CongestionTaxService congestionTaxService;
    private final CongestionTaxRequestValidator congestionTaxRequestValidator;

    @PostMapping(path = "/congestiontax", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CongestionTaxResponse> congestionTax(@RequestBody CongestionTaxRequest congestionTaxRequest) {
        BigDecimal tax = null;
        if(congestionTaxRequestValidator.validateRequest(congestionTaxRequest)) {
            tax = congestionTaxService.congestionTax(congestionTaxRequest.getVehicleType(), congestionTaxRequest.getDates());
        }
        return ResponseEntity.ok(new CongestionTaxResponse(congestionTaxRequest.getVehicleType(), tax));

    }

}
