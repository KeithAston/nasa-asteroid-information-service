package com.ka.nasainformationservice.controllers;

import com.ka.nasainformationservice.Exceptions.APIKeyInvalidException;
import com.ka.nasainformationservice.models.Asteroid;
import com.ka.nasainformationservice.models.AsteroidLookupResponse;
import com.ka.nasainformationservice.models.SearchDates;
import com.ka.nasainformationservice.services.AsteroidLookupService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
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
            return new ResponseEntity<>("API Key provided is invalid", HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>("Something went wrong, please contact support", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

}
