package com.ka.nasainformationservice.services;

import com.ka.nasainformationservice.Integrators.NasaIntegrator;
import lombok.AllArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@CommonsLog
public class AsteroidLookupService {

    private NasaIntegrator nasaIntegrator;

    public String getAsteroidByID(String asteroidID){
        return nasaIntegrator.getAsteroidById(asteroidID);
    }

}
