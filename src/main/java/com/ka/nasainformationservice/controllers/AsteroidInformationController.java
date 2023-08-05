package com.ka.nasainformationservice.controllers;

import com.ka.nasainformationservice.services.AsteroidLookupService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/AsteroidService")
@AllArgsConstructor
public class AsteroidInformationController {

    private AsteroidLookupService lookupService;

    @RequestMapping("/asteroid/{asteroidId}")
    public ResponseEntity<String> getAsteroid(@PathVariable("asteroidId")
                                                          String asteroidId){

        return new ResponseEntity<>(lookupService.getAsteroidByID(asteroidId),
                HttpStatus.OK);
    }

}
