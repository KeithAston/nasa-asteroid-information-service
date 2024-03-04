package com.ka.nasainformationservice.utils;

import com.ka.nasainformationservice.helpers.MainHelper;
import com.ka.nasainformationservice.models.Asteroid;
import lombok.NoArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@NoArgsConstructor
@CommonsLog
public class CommonUtils {

    public Asteroid parseAsteroidData(JSONObject json) {
        var asteroid = new Asteroid();
        asteroid.setId(Integer.parseInt(json.getString(MainHelper.ID_KEY)));
        asteroid.setName(json.getString(MainHelper.NAME_KEY));
        asteroid.setIsPotentiallyHazardous(json.getBoolean(MainHelper.IS_POTENTIALLY_DANGEROUS_KEY));

        asteroid = extractAsteroidMetrics(asteroid, json);
        return asteroid;
    }

    private Asteroid extractAsteroidMetrics(Asteroid asteroid, JSONObject response){
        var estimatedDiameters = response.getJSONObject(MainHelper.ESTIMATED_DIAMETER_KEY);
        var kilometers = estimatedDiameters.getJSONObject(MainHelper.KILOMETERS_KEY);

        asteroid.setEstimatedDiameterInKMs_Min(kilometers.getDouble(MainHelper.ESTIMATED_DIAMETER_MIN_KEY));
        asteroid.setEstimatedDiameterInKMs_Max(kilometers.getDouble(MainHelper.ESTIMATED_DIAMETER_MAX_KEY));

        return populateCloseApproachData(asteroid, response);
    }

    private Asteroid populateCloseApproachData(Asteroid asteroid, JSONObject response){
        var closeApproachDataArray = response.getJSONArray(MainHelper.CLOSE_APPROACH_DATA_KEY);
        var formatter = new SimpleDateFormat(MainHelper.DATE_FORMAT);
        var todayDate = new Date();
        JSONObject approachInfo;
        Date approachDate;

        try {
            for (int i = 0; i < closeApproachDataArray.length(); i++) {
                approachInfo = closeApproachDataArray.getJSONObject(i);
                approachDate = formatter.parse(approachInfo.getString(MainHelper.CLOSE_APPROACH_DATE_KEY));

                if (approachDate.compareTo(todayDate) >= 0 || closeApproachDataArray.length() == 1) {
                    asteroid.setCloseApproachDate(formatter.format(approachDate));

                    var relativeVelocity = approachInfo.getJSONObject(MainHelper.RELATIVE_VELOCITY_KEY);
                    asteroid.setRelativeVelocityKmph(Double.parseDouble(relativeVelocity.getString(MainHelper.KMPH_KEY)));

                    var missDistance = approachInfo.getJSONObject(MainHelper.MISS_DIST_KEY);
                    asteroid.setMissDistanceKm(Double.parseDouble(missDistance.getString(MainHelper.KILOMETERS_KEY)));

                    return asteroid;
                }
            }
            return asteroid;
        } catch (ParseException parseException) {
            log.error(MainHelper.PARSING_CLOSE_APPROACH_DATA_EXCEPTION + parseException);
            return asteroid;
        } catch (Exception e) {
            log.error(MainHelper.PARSING_CLOSE_APPROACH_DATA_UNKNOWN_EXCEPTION + e);
            return asteroid;
        }
    }
}
