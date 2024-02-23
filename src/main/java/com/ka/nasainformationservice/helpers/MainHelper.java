package com.ka.nasainformationservice.helpers;

public class MainHelper {
    public static final String URL_PARAM_OPENER = "?";
    public static final String URL_ADD_PARAM = "&";
    public static final String API_KEY_PARAM = "api_key=";
    public static final String API_KEY_START_DATE = "start_date=";
    public static final String API_KEY_END_DATE = "end_date=";
    public static final String DATE_FORMAT = "yyyy-MM-DD";

    public static final String NASA_API_REQUEST_STARTING = "Attempting to call Nasa API";
    public static final String NASA_API_SUCCESS_MESSAGE = "Nasa Lookup API Successfully called in : ";
    public static final String NASA_API_FAILED_MESSAGE = "Call to Nasa Specific Date Search API failed in : ";
    public static final String NASA_API_INVALID_KEY = "API Key provided is invalid";
    public static final String GENERIC_EXCEPTION_MESSAGE = "Something went wrong, please check API key or contact support";
    public static final String API_KEY_INVALID_ASTEROID_DATES = "API key invalid when retrieving asteroids by date";
    public static final String API_KEY_INVALID_ASTEROID_ID = "API key invalid when retrieving asteroids by Id";
    public static final String ASTEROID_BY_DATE_GENERIC_ERROR = "Error retrieving asteroids by date : ";
    public static final String ASTEROID_BY_ID_GENERIC_ERROR = "Error retrieving asteroids by Id : ";
    public static final String PARSING_ASTEROID_DATA = "Parsing Asteroid Data";
    public static final String PARSING_CLOSE_APPROACH_DATA_EXCEPTION = "Parsing Exception on Asteroid Data : ";
    public static final String PARSING_CLOSE_APPROACH_DATA_UNKNOWN_EXCEPTION = "Unknown exception while parsing Asteroid Data : ";
    public static final String PARSING_ASTEROID_DATA_SUCCESS = "Successfully parsed Asteroid Data";
    public static final String INVALID_API_KEY_SNIPPET = "invalid api_key";

    public static final String ELEMENT_COUNT_KEY = "element_count";
    public static final String NEAR_EARTH_OBJECTS_KEY = "near_earth_objects";
    public static final String IS_POTENTIALLY_DANGEROUS_KEY = "is_potentially_hazardous_asteroid";
    public static final String ID_KEY = "id";
    public static final String NAME_KEY = "name";
    public static final String CLOSE_APPROACH_DATA_KEY = "close_approach_data";
    public static final String ESTIMATED_DIAMETER_KEY = "estimated_diameter";
    public static final String KILOMETERS_KEY = "kilometers";
    public static final String ESTIMATED_DIAMETER_MIN_KEY = "estimated_diameter_min";
    public static final String ESTIMATED_DIAMETER_MAX_KEY = "estimated_diameter_max";
    public static final String CLOSE_APPROACH_DATE_KEY = "close_approach_date";
    public static final String RELATIVE_VELOCITY_KEY = "relative_velocity";
    public static final String KMPH_KEY = "kilometers_per_hour";
    public static final String MISS_DIST_KEY = "miss_distance";

    public static final String FIVE_ASTEROIDS_ONLY_MESSAGE = "More than 5 asteroids were found between given dates";

}
