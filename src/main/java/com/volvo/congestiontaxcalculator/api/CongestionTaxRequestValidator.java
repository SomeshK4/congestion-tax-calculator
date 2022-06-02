package com.volvo.congestiontaxcalculator.api;

import com.volvo.congestiontaxcalculator.model.CongestionTaxRequest;
import com.volvo.congestiontaxcalculator.model.VehicleTypeEnum;
import org.springframework.stereotype.Component;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import java.util.Arrays;
import java.util.Optional;

@Component
public class CongestionTaxRequestValidator {

    public boolean validateRequest(CongestionTaxRequest congestionTaxRequest) {
        Optional<VehicleTypeEnum> vehicleType = Arrays.stream(VehicleTypeEnum.values())
                .filter(vehicleTypeEnum -> congestionTaxRequest.getVehicleType().equals(vehicleTypeEnum.name()))
                .findAny();

        if (!vehicleType.isPresent()) {
            throw Problem.valueOf(Status.BAD_REQUEST, String.format("Vehicle type: %s not supported", congestionTaxRequest.getVehicleType()));
        }

        return true;
    }
}
