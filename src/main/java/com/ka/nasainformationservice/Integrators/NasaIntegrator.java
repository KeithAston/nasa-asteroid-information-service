package com.ka.nasainformationservice.Integrators;

import com.ka.nasainformationservice.Exceptions.APIKeyInvalidException;
import com.ka.nasainformationservice.config.NasaAsteroidLookupConfiguration;
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

    private static final String URL_PARAM_OPENER = "?";
    private static final String API_KEY_PARAM = "api_key=";
    public String getAsteroidById(String asteroidId) throws APIKeyInvalidException {
        log.info("Attempting to call Nasa API");

        try {
            String response = restTemplate.getForObject(
                    asteroidLookupConfiguration.getApiUrl() + asteroidId
                    + URL_PARAM_OPENER + API_KEY_PARAM +
                    asteroidLookupConfiguration.getApiKey(),
                    String.class);
            log.info("Nasa Lookup API Successfully called");
            return response;
        } catch (Exception e) {
            log.error("Call to Nasa API failed");
            if(invalidAPIKey(e)){
                throw new APIKeyInvalidException("API Key provided is invalid");
            }
            return null;

        }
    }
    private boolean invalidAPIKey(Exception e){
        return e.getMessage().contains("invalid api_key");
    }
}
