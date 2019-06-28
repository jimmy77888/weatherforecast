package com.example.jimmy.weatherforecast.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.jimmy.weatherforecast.R;


public class WeatherPreferenceUtil {

    public static String getPreferredZipcode(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String prefZipcode = sharedPreferences.getString(context.getString(R.string.key_zipcode), context.getString(R.string.def_zipcode));
        return prefZipcode;
    }
    public static boolean isMetric(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean unit = sharedPreferences.getBoolean(context.getResources().getString(R.string.key_units), context.getResources().getBoolean(R.bool.def_unit));
        if (unit)
            return true;
        return false;
    }
    //formula to convert k to f
    public static double kToImperial(double kTemp) {
        return (kTemp * 1.8 - 459.67);
    }
    //formula to convert k to c
    public static double kToMetric(double kTemp) {
        return (kTemp - 273.15);
    }
}
   