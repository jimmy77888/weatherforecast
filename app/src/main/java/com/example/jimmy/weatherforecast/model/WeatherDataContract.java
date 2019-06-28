package com.example.jimmy.weatherforecast.model;

import android.net.Uri;
import android.provider.BaseColumns;


public class WeatherDataContract {
    public static final String DATABASE_NAME = "weather_database";
    public static final String WEATHER_DATA_TABLE = "weather_data_table";
    public static final String CITY_DATA_TABLE = "city_table";
    public static final int VERSION_NUMBER = 2;
    public static final String AUTHORITY = "com.example.jimmy.weatherforecast";
    public static final String PATH_WEATHER = "weather";
    public static final String PATH_CITY = "city";
    public static final Uri WEATHER_DATA_URI = Uri.parse("content://" + AUTHORITY).buildUpon()
            .appendPath(PATH_WEATHER)
            .build();
    public static final Uri CITY_URI = Uri.parse("content://" + AUTHORITY).buildUpon()
            .appendPath(PATH_CITY)
            .build();
    public static final String CONTENT_TYPE_WEATHER = "weather";
    public static final String CONTENT_TYPE_CITY = "city";

    public class WeatherDataEntry implements BaseColumns {
        public static final String COLUMN_ID = WeatherDataEntry._ID;
        public static final String COLUMN_CITY_ID = "city_id";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_WEATHER_CONDITION = "weather_condition";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_TEMP = "temperature";
        public static final String COLUMN_TEMP_MAX = "temp_max";
        public static final String COLUMN_TEMP_MIN = "temp_min";
        public static final String COLUMN_HUMIDITY = "humidity";
        public static final String COLUMN_WIND_SPEED = "wind_speed";
        public static final String COLUMN_WIND_DEGREE = "wind_degree";
        public static final String COLUMN_PRESSURE = "pressure";


    }

    public class CityEntry implements BaseColumns {
        public static final String COLUMN_ID = CityEntry._ID;
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_ZIPCODE = "zip";
        public static final String COLUMN_LAT = "lat";
        public static final String COLUMN_LON = "lon";
        public static final String COLUMN_COUNTRY = "country";
        public static final String COLUMN_TIMEZONE = "timezone";
    }

    public static final String[] PROJECTION_WEATHER = {
            WeatherDataEntry.COLUMN_ID,
            WeatherDataEntry.COLUMN_DATE,
            WeatherDataEntry.COLUMN_WEATHER_CONDITION,
            WeatherDataEntry.COLUMN_DESCRIPTION,
            WeatherDataEntry.COLUMN_TEMP,
            WeatherDataEntry.COLUMN_TEMP_MAX,
            WeatherDataEntry.COLUMN_TEMP_MIN,
            WeatherDataEntry.COLUMN_HUMIDITY,
            WeatherDataEntry.COLUMN_WIND_SPEED,
            WeatherDataEntry.COLUMN_WIND_DEGREE,
            WeatherDataEntry.COLUMN_PRESSURE
    };

    public static final String[] PROJECTION_CITY = {
            CityEntry.COLUMN_ID,
            CityEntry.COLUMN_NAME
    };


}
