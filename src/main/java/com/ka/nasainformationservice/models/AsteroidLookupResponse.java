package com.ka.nasainformationservice.models;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AsteroidLookupResponse {
    private String start_date;
    private String end_date;
    private int numOfAsteroidsFound;
    private Asteroid[] asteroids;
}
