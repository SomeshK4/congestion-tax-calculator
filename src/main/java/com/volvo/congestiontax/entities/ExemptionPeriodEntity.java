package com.volvo.congestiontax.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * @author Somesh Kumar
 */
@Getter
@Setter
@Entity
@Table(name = "exemption_periods")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExemptionPeriodEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", nullable = false)
    private CityEntity city;
}
