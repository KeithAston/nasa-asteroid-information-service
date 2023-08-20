package com.ka.nasainformationservice.models;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Asteroid {
    private int id;
    private String name;
    private Boolean isPotentiallyHazardous;
    private int estimatedDiameterInKMs_Min;
    private int estimatedDiameterInKMs_Max;
    private String closeApproachDate;
    private double relativeVelocityKmph;
    private double missDistanceKm;
}
