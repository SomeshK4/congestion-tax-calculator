package com.volvo.congestiontax.controllers;

import com.volvo.congestiontax.dtos.CongestionTaxRequest;
import com.volvo.congestiontax.dtos.CongestionTaxResponse;
import com.volvo.congestiontax.services.CongestionTaxService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Somesh Kumar
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CongestionTaxController {

    private final CongestionTaxService congestionTaxService;

    @PostMapping("/congestion-taxes")
    public ResponseEntity<CongestionTaxResponse> getCongestionTaxDetails(@RequestBody CongestionTaxRequest congestionTaxRequest) {
        CongestionTaxResponse congestionTaxResponse = congestionTaxService.calculateCongestionTax(congestionTaxRequest);
        return ResponseEntity.ok(congestionTaxResponse);
    }
}
