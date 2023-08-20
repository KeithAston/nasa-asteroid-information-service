package com.ka.nasainformationservice.services;

import com.ka.nasainformationservice.Exceptions.APIKeyInvalidException;
import com.ka.nasainformationservice.Integrators.NasaIntegrator;
import com.ka.nasainformationservice.models.Asteroid;
import lombok.AllArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
@CommonsLog
public class AsteroidLookupService {

    private NasaIntegrator nasaIntegrator;

    public Asteroid getAsteroidByID(String asteroidID){
        String response;
        try {
            response = nasaIntegrator.getAsteroidById(asteroidID);
        } catch (APIKeyInvalidException e) {
            log.error(e.getMessage());
            return null;
        }

        Asteroid asteroid = populateAsteroidInfo(response);

        return asteroid;
    }

    private Asteroid populateAsteroidInfo(String response) {
        JSONObject jsonResponse = new JSONObject(response);
        Asteroid asteroid = new Asteroid();
        asteroid.setId(Integer.parseInt(jsonResponse.getString("id")));
        asteroid.setName(jsonResponse.getString("name"));
        asteroid.setIsPotentiallyHazardous(jsonResponse.getBoolean("is_potentially_hazardous_asteroid"));

        asteroid = extractAsteroidMetrics(asteroid, jsonResponse);
        return asteroid;
    }

    private Asteroid extractAsteroidMetrics(Asteroid asteroid, JSONObject response){

        JSONObject estimatedDiameters = (JSONObject) response.get("estimated_diameter");
        JSONObject kilometers = (JSONObject) estimatedDiameters.get("kilometers");

        asteroid.setEstimatedDiameterInKMs_Min(kilometers.getInt("estimated_diameter_min"));
        asteroid.setEstimatedDiameterInKMs_Max(kilometers.getInt("estimated_diameter_max"));

        return populateCloseApproachData(asteroid, response);
    }

    private Asteroid populateCloseApproachData(Asteroid asteroid, JSONObject response){
        JSONArray closeApproachDataArray = response.getJSONArray("close_approach_data");
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-DD");
        Date todayDate = new Date();
        JSONObject approachInfo;
        Date approachDate;
        log.info("Parsing Close Approach Data");

        try {
            for (int i = 0; i < closeApproachDataArray.length(); i++) {
                approachInfo = (JSONObject) closeApproachDataArray.get(i);
                approachDate = (Date) formatter.parse(approachInfo.getString("close_approach_date"));

                if (approachDate.compareTo(todayDate) >= 0) {
                    asteroid.setCloseApproachDate(formatter.format(approachDate));

                    JSONObject relativeVelocity = approachInfo.getJSONObject("relative_velocity");
                    asteroid.setRelativeVelocityKmph(Double.parseDouble(relativeVelocity.getString("kilometers_per_hour")));

                    JSONObject missDistance = approachInfo.getJSONObject("miss_distance");
                    asteroid.setMissDistanceKm(Double.parseDouble(missDistance.getString("kilometers")));

                    log.info("Successfully parsed Close Approach Data");
                    return asteroid;
                }
            }
            return asteroid;
        } catch (ParseException parseException) {
            log.error("Parsing Exception on Close Approach Data : " + parseException);
            return asteroid;
        } catch (Exception e) {
            log.error("Exception while parsing Close Approach Data" + e);
            return asteroid;
        }
    }
}
