package com.ka.nasainformationservice.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/AsteroidService")
@AllArgsConstructor
public class AsteroidInformationController {

    @RequestMapping("/getAsteroid")
    public ResponseEntity<String> getAsteroidInfoWithID(){
        return new ResponseEntity<>("Information will be here", HttpStatus.OK);
    }

}
