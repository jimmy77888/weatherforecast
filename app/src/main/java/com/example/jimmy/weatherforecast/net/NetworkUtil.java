package com.example.jimmy.weatherforecast.net;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.jimmy.weatherforecast.BuildConfig;
import com.example.jimmy.weatherforecast.model.WeatherDataContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/*
* first time adding new city:
* 1. Url buildUrlWithZipcode(String zipcode){}
* 2. String getReponseFromUrl(URL url){}
* 3. ContentValue getCityDataFromResponse(String reponse){}
* 4. boolean cityHasExited(ContentValue value){}
* 5. ContentValue[] getWeatherDataFromResponse
* */
public class NetworkUtil {

    private static final String BASE_URI = "http://api.openweathermap.org/data/2.5/forecast";
    private static final String PARAM_ZIPCODE = "zip";
    private static final String PARAM_APPID = "appid";
    private static final String APPID = BuildConfig.OWM_APPID;
    private static final String TAG_CODE = "cod";
    private static final String TAG_LIST = "list";
    private static final String TAG_DATE = "dt";
    private static final String TAG_MAIN = "main";
    private static final String TAG_TEMP = "temp";
    private static final String TAG_TEMP_MIN = "temp_min";
    private static final String TAG_TEMP_MAX = "temp_max";
    private static final String TAG_WEATHER = "weather";
    private static final String TAG_HUMIDITY = "humidity";
    private static final String TAG_DESCRIPTION = "description";
    private static final String TAG_CONDITION = "main";
    private static final String TAG_PRESSURE = "pressure";
    private static final String TAG_WIND = "wind";
    private static final String TAG_WIND_SPEED = "speed";
    private static final String TAG_WIND_DEGREE = "deg";
    private static final String TAG_CITY = "city";
    private static final String TAG_CITY_NAME = "name";
    private static final String TAG_CITY_COORD = "coord";
    private static final String TAG_CITY_COORD_LAT = "lat";
    private static final String TAG_CITY_COORD_LON = "lon";
    private static final String TAG_CITY_COUNTRY = "country";
    private static final String TAG_CITY_TIMEZONE = "timezone";
    private static final int CNT = 40;
    private static final int DAILY_CNT = 8;

    public static URL buildUrlWithZipcode(String zipcode){
        Uri uri = Uri.parse(BASE_URI).buildUpon()
                .appendQueryParameter(PARAM_ZIPCODE, zipcode)
                .appendQueryParameter(PARAM_APPID, APPID)
                .build();
        try {
            URL url = new URL(uri.toString());
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int generateCityId(String cityName, String zipcode) {
        return (cityName.concat(zipcode.substring(0,2)).hashCode());
    }

    public static String getReponseFromUrl(URL url) {
        StringBuilder sb = new StringBuilder();
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream in = httpURLConnection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));    // TODO: replace \\a with \\z as delimiter and see what'd happen
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static boolean receiveResponseSuccessfully(String response) throws JSONException{
        JSONObject jsonWeatherData = new JSONObject(response);
        if (jsonWeatherData.getInt(TAG_CODE) == HttpURLConnection.HTTP_OK)
            return true;
        return false;
    }
    public static ContentValues getCityDataFromJSON(String response, String zipcode) throws JSONException{
        if (!receiveResponseSuccessfully(response))
            return null;
        JSONObject jsonWeatherData = new JSONObject(response);
        JSONObject jsonCity = jsonWeatherData.getJSONObject(TAG_CITY);
        String cityName = jsonCity.getString(TAG_CITY_NAME);
        JSONObject jsonCityCoord = jsonCity.getJSONObject(TAG_CITY_COORD);
        double cityLat = jsonCityCoord.getDouble(TAG_CITY_COORD_LAT);
        double cityLon = jsonCityCoord.getDouble(TAG_CITY_COORD_LON);
        String country = jsonCity.getString(TAG_CITY_COUNTRY);
        int timezone = jsonCity.getInt(TAG_CITY_TIMEZONE);
        int cityID = generateCityId(cityName, zipcode);
        ContentValues values = new ContentValues();
        values.put(WeatherDataContract.CityEntry.COLUMN_ID, cityID);
        values.put(WeatherDataContract.CityEntry.COLUMN_ZIPCODE, zipcode);
        values.put(WeatherDataContract.CityEntry.COLUMN_NAME, cityName);
        values.put(WeatherDataContract.CityEntry.COLUMN_COUNTRY, country);
        values.put(WeatherDataContract.CityEntry.COLUMN_LAT, cityLat);
        values.put(WeatherDataContract.CityEntry.COLUMN_LON, cityLon);
        values.put(WeatherDataContract.CityEntry.COLUMN_TIMEZONE, timezone);
        return values;
    }
    public static ContentValues[] getWeatherDataFromJSON(String response, String cityID) throws JSONException {
        if (!receiveResponseSuccessfully(response))
            return null;
        ContentValues[] values = new ContentValues[CNT/DAILY_CNT];
        JSONObject jsonWeatherData = new JSONObject(response);
        JSONArray jsonArrWeather = jsonWeatherData.getJSONArray(TAG_LIST);
        JSONObject jsonCity = jsonWeatherData.getJSONObject(TAG_CITY);
        for (int i = 0; i < CNT; i = i + DAILY_CNT) {
            JSONObject jsonDailyWeather = jsonArrWeather.getJSONObject(i);
            long date = jsonDailyWeather.getLong(TAG_DATE);
            JSONObject jsonMainWeather = jsonDailyWeather.getJSONObject(TAG_MAIN);
            double temp = jsonMainWeather.getDouble(TAG_TEMP);
            double tempMax = jsonMainWeather.getDouble(TAG_TEMP_MAX);
            double tempMin = jsonMainWeather.getDouble(TAG_TEMP_MIN);
            double pressure = jsonMainWeather.getDouble(TAG_PRESSURE);
            int humidity = jsonMainWeather.getInt(TAG_HUMIDITY);
            JSONObject jsonWeather = jsonDailyWeather.getJSONArray(TAG_WEATHER).getJSONObject(0);
            String description = jsonWeather.getString(TAG_DESCRIPTION);
            String weatherCondition = jsonWeather.getString(TAG_CONDITION);
            JSONObject jsonWind = jsonDailyWeather.getJSONObject(TAG_WIND);
            double windSpeed = jsonWind.getDouble(TAG_WIND_SPEED);
            double windDegree = jsonWind.getDouble(TAG_WIND_DEGREE);
            int index = i/DAILY_CNT;
            values[index] = new ContentValues();
            values[index].put(WeatherDataContract.WeatherDataEntry.COLUMN_CITY_ID, cityID);
            values[index].put(WeatherDataContract.WeatherDataEntry.COLUMN_DATE, date);
            values[index].put(WeatherDataContract.WeatherDataEntry.COLUMN_DESCRIPTION, description);
            values[index].put(WeatherDataContract.WeatherDataEntry.COLUMN_WEATHER_CONDITION, weatherCondition);
            values[index].put(WeatherDataContract.WeatherDataEntry.COLUMN_TEMP, temp);
            values[index].put(WeatherDataContract.WeatherDataEntry.COLUMN_TEMP_MAX, tempMax);
            values[index].put(WeatherDataContract.WeatherDataEntry.COLUMN_TEMP_MIN, tempMin);
            values[index].put(WeatherDataContract.WeatherDataEntry.COLUMN_PRESSURE, pressure);
            values[index].put(WeatherDataContract.WeatherDataEntry.COLUMN_HUMIDITY, humidity);
            values[index].put(WeatherDataContract.WeatherDataEntry.COLUMN_WIND_SPEED, windSpeed);
            values[index].put(WeatherDataContract.WeatherDataEntry.COLUMN_WIND_DEGREE, windDegree);
        }
        return values;
    }
}
