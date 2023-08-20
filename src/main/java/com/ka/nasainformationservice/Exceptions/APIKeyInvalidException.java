package com.ka.nasainformationservice.Exceptions;

public class APIKeyInvalidException extends Exception{
    public APIKeyInvalidException(String errorMessage){
        super(errorMessage);
    }
}
