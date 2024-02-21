package com.ka.nasainformationservice.controllers;

import com.ka.nasainformationservice.Exceptions.APIKeyInvalidException;
import com.ka.nasainformationservice.helpers.MainHelper;
import com.ka.nasainformationservice.models.Asteroid;
import com.ka.nasainformationservice.models.AsteroidLookupResponse;
import com.ka.nasainformationservice.models.SearchDates;
import com.ka.nasainformationservice.services.AsteroidLookupService;
import lombok.AllArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
@CommonsLog
public class AsteroidInformationController {

    private final AsteroidLookupService lookupService;

    @GetMapping("/asteroid/{asteroidId}")
    public ResponseEntity<Asteroid> getAsteroid(@PathVariable("asteroidId") String asteroidId){
        return new ResponseEntity<>(lookupService.getAsteroidByID(asteroidId),
                HttpStatus.OK);
    }

    @PostMapping("/asteroid/search")
    public HttpEntity<? extends Object> getAsteroidByDate(@Valid @RequestBody SearchDates searchDates){
        try {
            return new ResponseEntity<>(lookupService.getAsteroidByDate(searchDates),
                    HttpStatus.OK);
        } catch (APIKeyInvalidException apiKeyInvalidException) {
            log.error(MainHelper.API_KEY_INVALID_ASTEROID_DATES);
            return new ResponseEntity<>(MainHelper.NASA_API_INVALID_KEY, HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            log.error(MainHelper.ASTEROID_BY_DATE_GENERIC_ERROR + e);
            return new ResponseEntity<>(MainHelper.GENERIC_EXCEPTION_MESSAGE, HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

}
