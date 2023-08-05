package com.ka.nasainformationservice.services;

import com.ka.nasainformationservice.Integrators.NasaIntegrator;
import com.ka.nasainformationservice.models.Asteroid;
import lombok.AllArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@CommonsLog
public class AsteroidLookupService {

    private NasaIntegrator nasaIntegrator;
    private Asteroid asteroid;

    public String getAsteroidByID(String asteroidID){
        String response = nasaIntegrator.getAsteroidById(asteroidID);
        asteroid = new Asteroid();
        populateAsteroidInfo(response);

        return asteroid.toString();
    }

    private void populateAsteroidInfo(String response){
        JSONObject jsonResponse = new JSONObject(response);
        asteroid.setId(Integer.parseInt(jsonResponse.getString("id")));
    }

}
