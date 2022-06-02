package com.volvo.congestiontaxcalculator.config;

import lombok.Data;

import java.util.List;

@Data
public class Years {

    private Integer year;
    private List<Months> months;
}
