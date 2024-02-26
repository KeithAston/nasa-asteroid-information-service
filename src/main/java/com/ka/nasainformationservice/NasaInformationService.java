package com.ka.nasainformationservice;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class NasaInformationService
{
    public static void main( String[] args )
    {
        SpringApplication.run(NasaInformationService.class, args);
    }

    @Bean
    public RestTemplate getRestTemplate(){ return new RestTemplate();}
}
