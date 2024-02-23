package com.ka.nasainformationservice.services;

import com.ka.nasainformationservice.Exceptions.APIKeyInvalidException;
import com.ka.nasainformationservice.Integrators.NasaIntegrator;
import com.ka.nasainformationservice.helpers.MainHelper;
import com.ka.nasainformationservice.models.Asteroid;
import com.ka.nasainformationservice.models.AsteroidLookupResponse;
import com.ka.nasainformationservice.models.SearchDates;
import com.ka.nasainformationservice.utils.CommonUtils;
import lombok.AllArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
@CommonsLog
public class AsteroidLookupService {

    private final NasaIntegrator nasaIntegrator;
    private final CommonUtils commonUtils;

    public Asteroid getAsteroidByID(String asteroidID){
        String response;
        try {
            response = nasaIntegrator.getAsteroidById(asteroidID);
        } catch (APIKeyInvalidException e) {
            log.error(MainHelper.API_KEY_INVALID_ASTEROID_ID);
            return null;
        } catch (Exception e) {
            log.error(MainHelper.ASTEROID_BY_ID_GENERIC_ERROR + e.getMessage());
            return null;
        }

        log.info(MainHelper.PARSING_ASTEROID_DATA);
        return commonUtils.parseAsteroidData(new JSONObject(response));
    }

    public AsteroidLookupResponse getAsteroidByDate(SearchDates searchDates) {

        String response;
        try {
            validateSearchDates(searchDates);
            response = nasaIntegrator.getAsteroidByDates(searchDates);
        } catch (APIKeyInvalidException e) {
            log.error(MainHelper.API_KEY_INVALID_ASTEROID_DATES);
            return null;
        } catch (Exception e) {
            log.error(MainHelper.ASTEROID_BY_DATE_GENERIC_ERROR + e.getMessage());
            return null;
        }

        return parseAsteroidData(response, searchDates);
    }

    private void validateSearchDates(SearchDates searchDates) throws Exception {
        DateFormat formatter = new SimpleDateFormat(MainHelper.DATE_FORMAT);
        Date startDate = formatter.parse(searchDates.getStart_date());
        Date endDate = formatter.parse(searchDates.getEnd_date());
        long diffInMillis = Math.abs(endDate.getTime() - startDate.getTime());

        if(TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS) > 7) {
            throw new Exception("Please choose dates within 7 days of each other.");
        }
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
        log.info(MainHelper.PARSING_ASTEROID_DATA);

        for (int i = 0; i < asteroidCount && i < 5; i++) {
            JSONObject asteroidInfo = (JSONObject) objectsArray.get(i);
            Asteroid asteroid = commonUtils.parseAsteroidData(asteroidInfo);
            asteroids[i] = asteroid;
        }
        return asteroids;
    }



}
