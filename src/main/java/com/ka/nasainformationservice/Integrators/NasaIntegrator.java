package com.ka.nasainformationservice.Integrators;

import com.ka.nasainformationservice.Exceptions.APIKeyInvalidException;
import com.ka.nasainformationservice.config.NasaAsteroidLookupConfiguration;
import com.ka.nasainformationservice.config.NasaAsteroidSearchDatesConfiguration;
import com.ka.nasainformationservice.helpers.MainHelper;
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
    private final RestTemplate restTemplate;

    private final NasaAsteroidLookupConfiguration asteroidLookupConfiguration;
    private final NasaAsteroidSearchDatesConfiguration asteroidSearchDatesConfiguration;

    public String getAsteroidByDates(SearchDates searchDates) throws Exception {
        log.info(MainHelper.NASA_API_REQUEST_STARTING);

        try {
            String response = restTemplate.getForObject(
                    getDatesearchAPIUrl(searchDates), String.class);
            log.info(MainHelper.NASA_API_SUCCESS_MESSAGE);
            return response;
        } catch (Exception e) {
            log.error(MainHelper.NASA_API_FAILED_MESSAGE);
            if(invalidAPIKey(e)){
                log.error(MainHelper.NASA_API_INVALID_KEY);
                throw new APIKeyInvalidException(MainHelper.NASA_API_INVALID_KEY);
            }
            throw new Exception(MainHelper.NASA_API_FAILED_MESSAGE);
        }
    }

    public String getAsteroidById(String asteroidId) throws APIKeyInvalidException {
        log.info(MainHelper.NASA_API_REQUEST_STARTING);

        try {
            String response = restTemplate.getForObject(
                    getLookupSearchAPIUrl(asteroidId),
                    String.class);
            log.info(MainHelper.NASA_API_SUCCESS_MESSAGE);
            return response;
        } catch (Exception e) {
            log.error(MainHelper.NASA_API_FAILED_MESSAGE);
            if(invalidAPIKey(e)){
                throw new APIKeyInvalidException(MainHelper.NASA_API_INVALID_KEY);
            }
            return null;
        }
    }

    private String getLookupSearchAPIUrl(String asteroidId){
        StringBuilder sb = new StringBuilder();
        sb.append(asteroidLookupConfiguration.getApiUrl());
        sb.append(asteroidId);
        sb.append(MainHelper.URL_PARAM_OPENER);
        sb.append(MainHelper.API_KEY_PARAM);
        sb.append(asteroidLookupConfiguration.getApiKey());

        return sb.toString();
    }

    private String getDatesearchAPIUrl(SearchDates searchDates){
        StringBuilder sb = new StringBuilder();
        sb.append(asteroidSearchDatesConfiguration.getApiUrl());
        sb.append(MainHelper.URL_PARAM_OPENER);
        sb.append(MainHelper.API_KEY_START_DATE);
        sb.append(searchDates.getStart_date());
        sb.append(MainHelper.URL_ADD_PARAM);
        sb.append(MainHelper.API_KEY_END_DATE);
        sb.append(searchDates.getEnd_date());
        sb.append(MainHelper.URL_ADD_PARAM);
        sb.append(MainHelper.API_KEY_PARAM);
        sb.append(asteroidSearchDatesConfiguration.getApiKey());

        return sb.toString();
    }

    private boolean invalidAPIKey(Exception e){
        return e.getMessage().contains("invalid api_key");
    }
}
