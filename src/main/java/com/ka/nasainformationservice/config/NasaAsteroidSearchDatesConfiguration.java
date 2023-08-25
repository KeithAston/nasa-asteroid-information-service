package com.ka.nasainformationservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("com.ka.nasa.dates")
@Data
public class NasaAsteroidSearchDatesConfiguration {
    private String apiName;
    private String apiUrl;
    private String apiKey;
}
