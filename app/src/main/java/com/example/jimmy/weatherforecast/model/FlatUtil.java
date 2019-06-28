package com.example.jimmy.weatherforecast.model;

import android.app.Application;
import android.database.Cursor;
import android.util.Log;

import com.example.jimmy.weatherforecast.net.WeatherDataUtil;

public class FlatUtil {
    public static City cursorToCity(Cursor cursor) {
        if (cursor != null) {
            City city = new City();
            city.cityName = cursor.getString(cursor.getColumnIndex(WeatherDataContract.CityEntry.COLUMN_NAME));
            city.cityID = cursor.getInt(cursor.getColumnIndex(WeatherDataContract.CityEntry.COLUMN_ID));
            return city;
        }
        return null;
    }

    public static WeatherData cursorToWeatherData(Application application, Cursor cursor) {

        boolean isMetric = WeatherPreferenceUtil.isMetric(application);
        if (cursor != null) {
            WeatherData weatherData = new WeatherData();
            double maxTemp = cursor.getDouble(cursor.getColumnIndex(WeatherDataContract.WeatherDataEntry.COLUMN_TEMP_MAX));
            double minTemp = cursor.getDouble(cursor.getColumnIndex(WeatherDataContract.WeatherDataEntry.COLUMN_TEMP_MIN));
            double temp = cursor.getDouble(cursor.getColumnIndex(WeatherDataContract.WeatherDataEntry.COLUMN_TEMP));
            double convertMaxTemp, convertMinTemp, convertTemp;
            if (isMetric) {
                convertMaxTemp = WeatherPreferenceUtil.kToMetric(maxTemp);
                convertMinTemp = WeatherPreferenceUtil.kToMetric(minTemp);
                convertTemp = WeatherPreferenceUtil.kToMetric(temp);
            } else {
                convertMaxTemp = WeatherPreferenceUtil.kToImperial(maxTemp);
                convertMinTemp = WeatherPreferenceUtil.kToImperial(minTemp);
                convertTemp = WeatherPreferenceUtil.kToImperial(temp);
            }
            long milliDate = cursor.getLong(cursor.getColumnIndex(WeatherDataContract.WeatherDataEntry.COLUMN_DATE));
            String date = WeatherDataUtil.getFriendlyDate(application, milliDate);
            weatherData.condition = cursor.getString(cursor.getColumnIndex(WeatherDataContract.WeatherDataEntry.COLUMN_WEATHER_CONDITION));
            weatherData.date = date;
            weatherData.description = cursor.getString(cursor.getColumnIndex(WeatherDataContract.WeatherDataEntry.COLUMN_DESCRIPTION));
            weatherData.humidity = String.format("Humidity: %d%%", cursor.getInt(cursor.getColumnIndex(WeatherDataContract.WeatherDataEntry.COLUMN_HUMIDITY)));
            weatherData.pressure = String.format("Pressure: %.0f", cursor.getDouble(cursor.getColumnIndex(WeatherDataContract.WeatherDataEntry.COLUMN_PRESSURE)));
            weatherData.tempMin = String.format("%.0f", convertMinTemp);
            weatherData.tempMax = String.format("%.0f", convertMaxTemp);
            weatherData.temp = String.format("%.0f", convertTemp);

            return weatherData;
        }
        return null;
    }
}
