package com.ka.nasainformationservice.services;

import com.ka.nasainformationservice.Exceptions.APIKeyInvalidException;
import com.ka.nasainformationservice.Integrators.NasaIntegrator;
import com.ka.nasainformationservice.helpers.MainHelper;
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
        int asteroidsFound = jsonResponse.getInt(MainHelper.ELEMENT_COUNT_KEY);

        if(asteroidsFound > 5) {
            log.info(MainHelper.FIVE_ASTEROIDS_ONLY_MESSAGE);
            asteroidsFound = 5;
        }
        log.info("Processing " + asteroidsFound + " asteroids");
        lookupResponse.setNumOfAsteroidsFound(asteroidsFound);
        lookupResponse.setAsteroids(populateAsteroids(jsonResponse, asteroidsFound, searchDates));

        return lookupResponse;
    }

    private Asteroid[] populateAsteroids(JSONObject jsonResponse, int asteroidCount, SearchDates searchDates){
        JSONObject nearEarthObjects = (JSONObject) jsonResponse.get(MainHelper.NEAR_EARTH_OBJECTS_KEY);

        JSONArray objectsArray = nearEarthObjects.getJSONArray(searchDates.getStart_date());
        Asteroid[] asteroids = new Asteroid[asteroidCount];

        for (int i = 0; i < asteroidCount && i < 5; i++) {
            JSONObject asteroidInfo = (JSONObject) objectsArray.get(i);
            Asteroid asteroid = populateAsteroidLookupInfo(asteroidInfo);
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
        return populateAsteroidLookupInfo(new JSONObject(response));
    }

    private Asteroid populateAsteroidLookupInfo(JSONObject json) {
        Asteroid asteroid = new Asteroid();
        asteroid.setId(Integer.parseInt(json.getString(MainHelper.ID_KEY)));
        asteroid.setName(json.getString(MainHelper.NAME_KEY));
        asteroid.setIsPotentiallyHazardous(json.getBoolean(MainHelper.IS_POTENTIALLY_DANGEROUS_KEY));

        asteroid = extractAsteroidMetrics(asteroid, json);
        return asteroid;
    }

    private Asteroid extractAsteroidMetrics(Asteroid asteroid, JSONObject response){

        JSONObject estimatedDiameters = response.getJSONObject(MainHelper.ESTIMATED_DIAMETER_KEY);
        JSONObject kilometers = estimatedDiameters.getJSONObject(MainHelper.KILOMETERS_KEY);

        asteroid.setEstimatedDiameterInKMs_Min(kilometers.getDouble(MainHelper.ESTIMATED_DIAMETER_MIN_KEY));
        asteroid.setEstimatedDiameterInKMs_Max(kilometers.getDouble(MainHelper.ESTIMATED_DIAMETER_MAX_KEY));

        return populateCloseApproachData(asteroid, response);
    }

    private Asteroid populateCloseApproachData(Asteroid asteroid, JSONObject response){
        JSONArray closeApproachDataArray = response.getJSONArray(MainHelper.CLOSE_APPROACH_DATA_KEY);
        DateFormat formatter = new SimpleDateFormat(MainHelper.DATE_FORMAT);
        Date todayDate = new Date();
        JSONObject approachInfo;
        Date approachDate;
        log.info(MainHelper.PARSING_CLOSE_APPROACH_DATA);

        try {
            for (int i = 0; i < closeApproachDataArray.length(); i++) {
                approachInfo = closeApproachDataArray.getJSONObject(i);
                approachDate = (Date) formatter.parse(approachInfo.getString(MainHelper.CLOSE_APPROACH_DATE_KEY));

                if (approachDate.compareTo(todayDate) >= 0 || closeApproachDataArray.length() == 1) {
                    asteroid.setCloseApproachDate(formatter.format(approachDate));

                    JSONObject relativeVelocity = approachInfo.getJSONObject(MainHelper.RELATIVE_VELOCITY_KEY);
                    asteroid.setRelativeVelocityKmph(Double.parseDouble(relativeVelocity.getString(MainHelper.KMPH_KEY)));

                    JSONObject missDistance = approachInfo.getJSONObject(MainHelper.MISS_DIST_KEY);
                    asteroid.setMissDistanceKm(Double.parseDouble(missDistance.getString(MainHelper.KILOMETERS_KEY)));

                    log.info(MainHelper.PARSING_CLOSE_APPROACH_DATA_SUCCESS);
                    return asteroid;
                }
            }
            return asteroid;
        } catch (ParseException parseException) {
            log.error(MainHelper.PARSING_CLOSE_APPROACH_DATA_EXCEPTION + parseException);
            return asteroid;
        } catch (Exception e) {
            log.error(MainHelper.PARSING_CLOSE_APPROACH_DATA_UNKNOWN_EXCEPTION + e);
            return asteroid;
        }
    }
}
