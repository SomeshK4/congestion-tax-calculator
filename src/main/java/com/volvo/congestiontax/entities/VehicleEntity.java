package com.volvo.congestiontax.entities;

import jakarta.persistence.*;
import lombok.*;

/**
 * @author Somesh Kumar
 */
@Getter
@Setter
@Entity
@Table(name = "vehicles")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "type")
    private String type;

    @Column(name = "exempted")
    private boolean exempted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", nullable = false)
    private CityEntity city;

}
