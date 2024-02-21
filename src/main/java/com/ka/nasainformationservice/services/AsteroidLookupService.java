package com.ka.nasainformationservice.services;

import com.ka.nasainformationservice.Exceptions.APIKeyInvalidException;
import com.ka.nasainformationservice.Integrators.NasaIntegrator;
import com.ka.nasainformationservice.models.Asteroid;
import com.ka.nasainformationservice.models.AsteroidLookupResponse;
import com.ka.nasainformationservice.models.SearchDates;
import lombok.AllArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@AllArgsConstructor
@CommonsLog
public class AsteroidLookupService {

    private final NasaIntegrator nasaIntegrator;

    public AsteroidLookupResponse getAsteroidByDate(SearchDates searchDates) throws Exception {
        String response;
        response = nasaIntegrator.getAsteroidByDates(searchDates);
        return parseAsteroidData(response, searchDates);
    }

    private AsteroidLookupResponse parseAsteroidData(String response, SearchDates searchDates){
        AsteroidLookupResponse lookupResponse = new AsteroidLookupResponse();
        lookupResponse.setStart_date(searchDates.getStart_date());
        lookupResponse.setEnd_date(searchDates.getEnd_date());
        JSONObject jsonResponse = new JSONObject(response);
        int asteroidsFound = jsonResponse.getInt("element_count");

        if(asteroidsFound > 5) {
            log.info("More than 5 asteroids were found between given dates");
            asteroidsFound = 5;
        }
        log.info("Processing " + asteroidsFound + " asteroids");
        lookupResponse.setNumOfAsteroidsFound(asteroidsFound);
        lookupResponse.setAsteroids(populateAsteroids(jsonResponse, asteroidsFound, searchDates));

        return lookupResponse;
    }

    private Asteroid[] populateAsteroids(JSONObject jsonResponse, int asteroidCount, SearchDates searchDates){
        JSONObject nearEarthObjects = (JSONObject) jsonResponse.get("near_earth_objects");

        JSONArray objectsArray = nearEarthObjects.getJSONArray(searchDates.getStart_date());
        Asteroid[] asteroids = new Asteroid[objectsArray.length()];

        for (int i = 0; i < objectsArray.length() && i < 5; i++) {
            Asteroid asteroid = new Asteroid();

            JSONObject asteroidInfo = (JSONObject) objectsArray.get(i);

            asteroid.setId(Integer.parseInt(asteroidInfo.getString("id")));
            asteroid.setName(asteroidInfo.getString("name"));
            asteroid.setIsPotentiallyHazardous(asteroidInfo.getBoolean("is_potentially_hazardous_asteroid"));

            asteroid = extractAsteroidMetrics(asteroid, asteroidInfo);
            asteroids[i] = asteroid;
        }

        return asteroids;

    }

    public Asteroid getAsteroidByID(String asteroidID){
        String response;
        try {
            response = nasaIntegrator.getAsteroidById(asteroidID);
        } catch (APIKeyInvalidException e) {
            log.error(e.getMessage());
            return null;
        }
        return populateAsteroidLookupInfo(response);
    }

    private Asteroid populateAsteroidLookupInfo(String response) {
        JSONObject jsonResponse = new JSONObject(response);
        Asteroid asteroid = new Asteroid();
        asteroid.setId(Integer.parseInt(jsonResponse.getString("id")));
        asteroid.setName(jsonResponse.getString("name"));
        asteroid.setIsPotentiallyHazardous(jsonResponse.getBoolean("is_potentially_hazardous_asteroid"));

        asteroid = extractAsteroidMetrics(asteroid, jsonResponse);
        return asteroid;
    }

    private Asteroid extractAsteroidMetrics(Asteroid asteroid, JSONObject response){

        JSONObject estimatedDiameters = response.getJSONObject("estimated_diameter");
        JSONObject kilometers = estimatedDiameters.getJSONObject("kilometers");

        asteroid.setEstimatedDiameterInKMs_Min(kilometers.getDouble("estimated_diameter_min"));
        asteroid.setEstimatedDiameterInKMs_Max(kilometers.getDouble("estimated_diameter_max"));

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
                approachInfo = closeApproachDataArray.getJSONObject(i);
                approachDate = (Date) formatter.parse(approachInfo.getString("close_approach_date"));

                if (approachDate.compareTo(todayDate) >= 0 || closeApproachDataArray.length() == 1) {
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
            log.error("Unknown exception while parsing Close Approach Data : " + e);
            return asteroid;
        }
    }
}
