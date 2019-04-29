package br.edu.uepb.nutes.ocariot.data.model.fitbit;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import br.edu.uepb.nutes.ocariot.data.model.ocariot.ActivityLevel;

/**
 * Represents Physical Activity object from FitBit platform.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class ActivitySummayFitBit {
    @SerializedName("goals  ")
    private GoalsFitBit goals;

    @SerializedName("calories")
    private int calories;

    @SerializedName("steps")
    private int steps;

    @SerializedName("activityLevel")
    private List<ActivityLevel> levels;
/**
 * {
 *   summary VERSION ENDPOINT 1.2
 *   "summary": {
 *     "activityLevels": [
 *       {
 *         "distance": 0,
 *         "minutes": 851,
 *         "name": "sedentary"
 *       },
 *       {
 *         "distance": 3.1,
 *         "minutes": 170,
 *         "name": "lightly"
 *       },
 *       {
 *         "distance": 1.24,
 *         "minutes": 28,
 *         "name": "moderately"
 *       },
 *       {
 *         "distance": 1.33,
 *         "minutes": 18,
 *         "name": "very"
 *       }
 *     ],
 *     "calories": {
 *       "bmr": 1566,
 *       "total": 2206
 *     },
 *     "customHeartRateZones": [],
 *     "distance": 5.67,
 *     "elevation": 0,
 *     "floors": 0,
 *     "heartRateZones": [
 *       {
 *         "caloriesOut": 8.7,
 *         "max": 91,
 *         "min": 30,
 *         "minutes": 6,
 *         "name": "Out of Range"
 *       },
 *       {
 *         "caloriesOut": 0,
 *         "max": 128,
 *         "min": 91,
 *         "minutes": 0,
 *         "name": "Fat Burn"
 *       },
 *       {
 *         "caloriesOut": 0,
 *         "max": 155,
 *         "min": 128,
 *         "minutes": 0,
 *         "name": "Cardio"
 *       },
 *       {
 *         "caloriesOut": 0,
 *         "max": 220,
 *         "min": 155,
 *         "minutes": 0,
 *         "name": "Peak"
 *       }
 *     ],
 *     "steps": 7830
 *   }
 * }
 */
}
