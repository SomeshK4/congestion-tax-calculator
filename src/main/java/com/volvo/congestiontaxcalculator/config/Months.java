package com.volvo.congestiontaxcalculator.config;

import lombok.Data;

import java.util.List;

@Data
public class Months {

    private Integer month;
    private List<Integer> dates;
}
