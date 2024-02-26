package com.ka.nasainformationservice.controllers;

import com.ka.nasainformationservice.helpers.MainHelper;
import com.ka.nasainformationservice.models.Asteroid;
import com.ka.nasainformationservice.models.AsteroidLookupResponse;
import com.ka.nasainformationservice.models.SearchDates;
import com.ka.nasainformationservice.services.AsteroidLookupService;
import lombok.AllArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
@CommonsLog
public class AsteroidInformationController {

    private final AsteroidLookupService lookupService;

    @GetMapping("/asteroid/{asteroidId}")
    public ResponseEntity<?> getAsteroid(@PathVariable("asteroidId") String asteroidId){
        Asteroid asteroid = lookupService.getAsteroidByID(asteroidId);

        if(asteroid != null){
            log.info(MainHelper.PARSING_ASTEROID_DATA_SUCCESS);
            return new ResponseEntity<>(asteroid, HttpStatus.OK);
        }
        return new ResponseEntity<>(MainHelper.GENERIC_EXCEPTION_MESSAGE, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @PostMapping("/asteroid/search")
    public ResponseEntity<?> getAsteroidByDate(@RequestBody SearchDates searchDates){
        AsteroidLookupResponse asteroidLookupResponse = lookupService.getAsteroidByDate(searchDates);

        if(asteroidLookupResponse != null) {
            log.info(MainHelper.PARSING_ASTEROID_DATA_SUCCESS);
            return new ResponseEntity<>(asteroidLookupResponse, HttpStatus.OK);
        }
        return new ResponseEntity<>(MainHelper.GENERIC_EXCEPTION_MESSAGE, HttpStatus.SERVICE_UNAVAILABLE);
    }

}
