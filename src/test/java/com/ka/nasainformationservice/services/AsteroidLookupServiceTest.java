package com.ka.nasainformationservice.services;

import com.ka.nasainformationservice.Exceptions.APIKeyInvalidException;
import com.ka.nasainformationservice.Integrators.NasaIntegrator;
import com.ka.nasainformationservice.models.Asteroid;
import com.ka.nasainformationservice.models.AsteroidLookupResponse;
import com.ka.nasainformationservice.models.SearchDates;
import com.ka.nasainformationservice.utils.CommonUtils;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static java.nio.charset.StandardCharsets.UTF_8;

public class AsteroidLookupServiceTest {

    @Mock
    private NasaIntegrator nasaIntegrator;
    @Mock
    private CommonUtils commonUtils;

    private AsteroidLookupService asteroidLookupService;
    private static final String AsteroidsResponse_FILENAME = "AsteroidsResponse.json";
    private String asteroidResponse;

    @Before
    public void setup() throws IOException {
        MockitoAnnotations.initMocks(this);
        asteroidLookupService = new AsteroidLookupService(nasaIntegrator,commonUtils);
        ResourceLoader resourceLoader = new DefaultResourceLoader();

        Resource asteroidResource = resourceLoader.getResource("classpath:" + AsteroidsResponse_FILENAME);
        Reader asteroidReader = new InputStreamReader(asteroidResource.getInputStream(), UTF_8);
        asteroidResponse = FileCopyUtils.copyToString(asteroidReader);
    }

    @Test
    public void test_getAsteroidById() throws Exception {
        when(nasaIntegrator.getAsteroidById("123")).thenReturn(getSampleJson());
        when(commonUtils.parseAsteroidData(any(JSONObject.class))).thenReturn(getAsteroid());
        Asteroid asteroid = asteroidLookupService.getAsteroidByID("123");
        assertEquals(asteroid.getId(),123);
        assertEquals(asteroid.getIsPotentiallyHazardous(), true);
    }

    @Test
    public void test_getAsteroidById_InvalidAPIKey() throws Exception {
        when(nasaIntegrator.getAsteroidById("123")).thenThrow(new APIKeyInvalidException("API Key Invalid"));
        Asteroid asteroid = asteroidLookupService.getAsteroidByID("123");
        assertNull(asteroid);
    }

    @Test
    public void test_getAsteroidByDates() throws Exception {
        when(nasaIntegrator.getAsteroidByDates(getSearchDates())).thenReturn(asteroidResponse);
        AsteroidLookupResponse asteroidLookupResponse = asteroidLookupService.getAsteroidByDate(getSearchDates());
        Asteroid[] asteroids = asteroidLookupResponse.getAsteroids();

        assertEquals(asteroidLookupResponse.getNumOfAsteroidsFound(),5);
        assertEquals(asteroidLookupResponse.getStart_date(), "2015-09-21");
        assertEquals(asteroidLookupResponse.getEnd_date(), "2015-09-22");
        assertEquals(asteroids.length, 5);
    }

    private Asteroid getAsteroid(){
        Asteroid asteroid = new Asteroid();
        asteroid.setName("test");
        asteroid.setMissDistanceKm(455);
        asteroid.setRelativeVelocityKmph(54);
        asteroid.setIsPotentiallyHazardous(true);
        asteroid.setId(123);
        return asteroid;
    }

    private SearchDates getSearchDates(){
        SearchDates searchDates = new SearchDates();
        searchDates.setStart_date("2015-09-21");
        searchDates.setEnd_date("2015-09-22");
        return searchDates;
    }

    private String getSampleJson(){
        return "{\"key\": \"value\"}";
    }

}
