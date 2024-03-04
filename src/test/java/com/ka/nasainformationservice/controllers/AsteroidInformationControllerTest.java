package com.ka.nasainformationservice.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.ka.nasainformationservice.models.Asteroid;
import com.ka.nasainformationservice.models.AsteroidLookupResponse;
import com.ka.nasainformationservice.models.SearchDates;
import com.ka.nasainformationservice.services.AsteroidLookupService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(AsteroidInformationController.class)
public class AsteroidInformationControllerTest {
    //The @WebMvcTest annotation is used to unit test the
    // Spring MVC components (@Controller,  @ControllerAdvice)

    @Autowired
    private MockMvc mvc;

    @MockBean
    private AsteroidLookupService lookupService;

    @Captor
    private ArgumentCaptor<String> getAsteroidByIdArgumentCaptor;
    @Captor
    private ArgumentCaptor<SearchDates> getAsteroidByDateArgumentCaptor;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void TEST_getAsteroidById() throws Exception {
        when(lookupService.getAsteroidByID(any(String.class))).thenReturn(getAsteroid());
        var asteroidId = "12345";
        mvc.perform(MockMvcRequestBuilders
                .get("/api/v1/asteroid/" + asteroidId)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpectAll(
                    MockMvcResultMatchers.status().isOk(),
                    MockMvcResultMatchers.jsonPath("$.id").exists(),
                    MockMvcResultMatchers.jsonPath("$.name").exists(),
                    MockMvcResultMatchers.jsonPath("$.isPotentiallyHazardous").exists(),
                    MockMvcResultMatchers.jsonPath("$.estimatedDiameterInKMs_Min").exists(),
                    MockMvcResultMatchers.jsonPath("$.estimatedDiameterInKMs_Max").exists(),
                    MockMvcResultMatchers.jsonPath("$.closeApproachDate").exists(),
                    MockMvcResultMatchers.jsonPath("$.relativeVelocityKmph").exists(),
                    MockMvcResultMatchers.jsonPath("$.missDistanceKm").exists()
            );

        verify(lookupService).getAsteroidByID(getAsteroidByIdArgumentCaptor.capture());
        String response = getAsteroidByIdArgumentCaptor.getValue();
        then(response).isEqualTo(asteroidId);
    }

    @Test
    public void TEST_getAsteroidByDate() throws Exception {
        var searchDates = new SearchDates();
        searchDates.setStart_date("2015-09-21");
        searchDates.setEnd_date("2015-09-22");
        when(lookupService.getAsteroidByDate(any(SearchDates.class))).thenReturn(getMultipleAsteroids(searchDates));

        mvc.perform(MockMvcRequestBuilders
                .post("/api/v1/asteroid/search")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(searchDates)))
            .andDo(print())
            .andExpectAll(
                    MockMvcResultMatchers.status().isOk(),
                    MockMvcResultMatchers.jsonPath("$.start_date").exists(),
                    MockMvcResultMatchers.jsonPath("$.end_date").exists(),
                    MockMvcResultMatchers.jsonPath("$.numOfAsteroidsFound").isNumber(),
                    MockMvcResultMatchers.jsonPath("$.asteroids[*].id").isNotEmpty()
            );

        verify(lookupService).getAsteroidByDate(getAsteroidByDateArgumentCaptor.capture());
        var response = getAsteroidByDateArgumentCaptor.getValue();
        then(response).isEqualTo(searchDates);
    }

    private AsteroidLookupResponse getMultipleAsteroids(SearchDates searchDates) {
        var asteroids = new AsteroidLookupResponse();
        asteroids.setStart_date(searchDates.getStart_date());
        asteroids.setEnd_date(searchDates.getEnd_date());
        asteroids.setNumOfAsteroidsFound(2);
        var asteroidArray = new Asteroid[2];
        asteroidArray[0] = getAsteroid();
        asteroidArray[1] = getAsteroid();
        asteroids.setAsteroids(asteroidArray);
        return asteroids;
    }

    private Asteroid getAsteroid(){
        var asteroid = new Asteroid();
        asteroid.setName("test");
        asteroid.setMissDistanceKm(455);
        asteroid.setCloseApproachDate("12/12/2028");
        asteroid.setEstimatedDiameterInKMs_Min(10);
        asteroid.setEstimatedDiameterInKMs_Max(20);
        asteroid.setRelativeVelocityKmph(54);
        asteroid.setIsPotentiallyHazardous(true);
        asteroid.setId(12345);
        return asteroid;
    }

}
