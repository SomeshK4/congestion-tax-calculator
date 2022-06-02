package com.volvo.congestiontaxcalculator.config;

import lombok.Data;

import java.util.List;

@Data
public class Holidays {

    private List<Years> years;
}
