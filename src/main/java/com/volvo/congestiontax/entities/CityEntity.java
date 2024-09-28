package com.volvo.congestiontax.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Somesh Kumar
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cities")
public class CityEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String country;

    @Column(name = "single_charge_minutes")
    private Integer singleChargeMinutes;
    @Column(name = "daily_charge_cap")
    private BigDecimal dailyChargeCap;

    @Column(name = "weekend_exempted")
    private boolean weekendExempted;

    @OneToMany(mappedBy = "city", fetch = FetchType.LAZY)
    private List<VehicleEntity> vehicles;

    @OneToMany(mappedBy = "city", fetch = FetchType.LAZY)
    private List<TaxRuleEntity> taxRules;

    @OneToMany(mappedBy = "city", fetch = FetchType.LAZY)
    private List<ExemptionPeriodEntity> exemptionPeriods;


}
