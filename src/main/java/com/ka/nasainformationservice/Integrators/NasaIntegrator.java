package com.ka.nasainformationservice.Integrators;

import com.ka.nasainformationservice.Exceptions.APIKeyInvalidException;
import com.ka.nasainformationservice.config.NasaAsteroidLookupConfiguration;
import com.ka.nasainformationservice.config.NasaAsteroidSearchDatesConfiguration;
import com.ka.nasainformationservice.models.SearchDates;
import lombok.AllArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@AllArgsConstructor
@CommonsLog
public class NasaIntegrator {

    @Autowired
    private RestTemplate restTemplate;

    private final NasaAsteroidLookupConfiguration asteroidLookupConfiguration;
    private final NasaAsteroidSearchDatesConfiguration asteroidSearchDatesConfiguration;

    private static final String URL_PARAM_OPENER = "?";
    private static final String URL_ADD_PARAM = "&";
    private static final String API_KEY_PARAM = "api_key=";
    private static final String API_KEY_START_DATE = "start_date=";
    private static final String API_KEY_END_DATE = "end_date=";

    public String getAsteroidByDates(SearchDates searchDates) throws APIKeyInvalidException {
        log.info("Attempting to call Nasa API : Specific Dates Lookup");

        try {
            String response = restTemplate.getForObject(
                    getDatesearchAPIUrl(searchDates), String.class);
            log.info("Nasa Specific Date Search API Successfully called");
            return response;
        } catch (Exception e) {
            log.error("Call to Nasa Specific Date Search API failed");
            if(invalidAPIKey(e)){
                throw new APIKeyInvalidException("API Key provided is invalid");
            }
            return null;
        }
    }

    public String getAsteroidById(String asteroidId) throws APIKeyInvalidException {
        log.info("Attempting to call Nasa API : Individual Lookup");

        try {
            String response = restTemplate.getForObject(
                    getLookupSearchAPIUrl(asteroidId),
                    String.class);
            log.info("Nasa Lookup API Successfully called");
            return response;
        } catch (Exception e) {
            log.error("Call to Nasa Lookup API failed");
            if(invalidAPIKey(e)){
                throw new APIKeyInvalidException("API Key provided is invalid");
            }
            return null;

        }
    }

    private String getLookupSearchAPIUrl(String asteroidId){
        StringBuilder sb = new StringBuilder();
        sb.append(asteroidLookupConfiguration.getApiUrl());
        sb.append(asteroidId);
        sb.append(URL_PARAM_OPENER);
        sb.append(API_KEY_PARAM);
        sb.append(asteroidLookupConfiguration.getApiKey());

        return sb.toString();
    }

    private String getDatesearchAPIUrl(SearchDates searchDates){
        StringBuilder sb = new StringBuilder();
        sb.append(asteroidSearchDatesConfiguration.getApiUrl());
        sb.append(URL_PARAM_OPENER);
        sb.append(API_KEY_START_DATE);
        sb.append(searchDates.getStart_date());
        sb.append(URL_ADD_PARAM);
        sb.append(API_KEY_END_DATE);
        sb.append(searchDates.getEnd_date());
        sb.append(URL_ADD_PARAM);
        sb.append(API_KEY_PARAM);
        sb.append(asteroidSearchDatesConfiguration.getApiKey());

        return sb.toString();
    }

    private boolean invalidAPIKey(Exception e){
        return e.getMessage().contains("invalid api_key");
    }
}
