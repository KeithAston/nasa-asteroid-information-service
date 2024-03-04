package com.ka.nasainformationservice.services;

import com.ka.nasainformationservice.Exceptions.APIKeyInvalidException;
import com.ka.nasainformationservice.Integrators.NasaIntegrator;
import com.ka.nasainformationservice.models.Asteroid;
import com.ka.nasainformationservice.models.SearchDates;
import com.ka.nasainformationservice.utils.CommonUtils;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.util.FileCopyUtils;

import java.io.InputStreamReader;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static java.nio.charset.StandardCharsets.UTF_8;

@ExtendWith(MockitoExtension.class)
public class AsteroidLookupServiceTest {

    @Mock
    private NasaIntegrator nasaIntegrator;
    @Mock
    private CommonUtils commonUtils;

    private AsteroidLookupService asteroidLookupService;
    private static final String AsteroidsResponse_FILENAME = "AsteroidsResponse.json";

    @BeforeEach
    public void setup(){
        asteroidLookupService = new AsteroidLookupService(nasaIntegrator,commonUtils);
    }

    @Test
    public void test_getAsteroidById() throws Exception {
        when(nasaIntegrator.getAsteroidById("123")).thenReturn(getSampleJson());
        when(commonUtils.parseAsteroidData(any(JSONObject.class))).thenReturn(getAsteroid());
        var asteroid = asteroidLookupService.getAsteroidByID("123");
        Assertions.assertEquals(asteroid.getId(),123);
        Assertions.assertEquals(asteroid.getIsPotentiallyHazardous(), true);
    }

    @Test
    public void test_getAsteroidById_InvalidAPIKey() throws Exception {
        when(nasaIntegrator.getAsteroidById("123")).thenThrow(new APIKeyInvalidException("API Key Invalid"));
        var asteroid = asteroidLookupService.getAsteroidByID("123");
        Assertions.assertNull(asteroid);
    }

    @Test
    public void test_getAsteroidByDates() throws Exception {
        var resourceLoader = new DefaultResourceLoader();

        var asteroidResource = resourceLoader.getResource("classpath:" + AsteroidsResponse_FILENAME);
        var asteroidReader = new InputStreamReader(asteroidResource.getInputStream(), UTF_8);
        var asteroidResponse = FileCopyUtils.copyToString(asteroidReader);
        when(nasaIntegrator.getAsteroidByDates(getSearchDates())).thenReturn(asteroidResponse);
        var asteroidLookupResponse = asteroidLookupService.getAsteroidByDate(getSearchDates());
        var asteroids = asteroidLookupResponse.getAsteroids();

        Assertions.assertEquals(asteroidLookupResponse.getNumOfAsteroidsFound(),5);
        Assertions.assertEquals(asteroidLookupResponse.getStart_date(), "2015-09-21");
        Assertions.assertEquals(asteroidLookupResponse.getEnd_date(), "2015-09-22");
        Assertions.assertEquals(asteroids.length, 5);
    }

    private Asteroid getAsteroid(){
        var asteroid = new Asteroid();
        asteroid.setName("test");
        asteroid.setMissDistanceKm(455);
        asteroid.setRelativeVelocityKmph(54);
        asteroid.setIsPotentiallyHazardous(true);
        asteroid.setId(12345);
        return asteroid;
    }

    private SearchDates getSearchDates(){
        var searchDates = new SearchDates();
        searchDates.setStart_date("2015-09-21");
        searchDates.setEnd_date("2015-09-22");
        return searchDates;
    }

    private String getSampleJson(){
        return "{\"key\": \"value\"}";
    }

}
