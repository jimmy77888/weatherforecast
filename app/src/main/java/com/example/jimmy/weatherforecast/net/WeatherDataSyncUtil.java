package com.example.jimmy.weatherforecast.net;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

import com.example.jimmy.weatherforecast.net.FetchDataJobService;
import com.example.jimmy.weatherforecast.net.FetchWeatherDataIntentService;
import com.example.jimmy.weatherforecast.model.WeatherDataContract;
import com.example.jimmy.weatherforecast.net.NetworkUtil;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import org.json.JSONException;

import java.net.URL;
import java.util.concurrent.TimeUnit;

public class WeatherDataSyncUtil {
    public static final int INTERVAL_HOURS = 3;
    public static final int INTERVAL_START = (int)TimeUnit.HOURS.toSeconds(INTERVAL_HOURS);
    public static final int INTERVAL_END = INTERVAL_START + INTERVAL_START/3;
    public static final String JOB_TAG = "fetch_weather_data_job";

    public static void initializeSyncTask(Application application) {
        GooglePlayDriver googleDriver = new GooglePlayDriver(application);
        FirebaseJobDispatcher jobDispatcher = new FirebaseJobDispatcher(googleDriver);
        Job syncJob = jobDispatcher.newJobBuilder()
                .setService(FetchDataJobService.class)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(INTERVAL_START,INTERVAL_END))
                .setLifetime(Lifetime.FOREVER)
                .setReplaceCurrent(true)
                .setTag(JOB_TAG)
                .build();
        jobDispatcher.mustSchedule(syncJob);
        immidiatelySync(application);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Cursor cursor = application.getContentResolver().query(WeatherDataContract.WEATHER_DATA_URI,
                        new String[]{WeatherDataContract.WeatherDataEntry.COLUMN_ID},
                        null,
                        null,
                        null);
                if (cursor == null || cursor.getCount() == 0) {

                    immidiatelySync(application);
                }
            }
        });
        thread.start();
    }

    public static void immidiatelySync(Application application) {
        Intent intent = new Intent(application, FetchWeatherDataIntentService.class);
        application.startService(intent);
    }

    //fetch data from openWeatherData
    public static synchronized void syncAllWeatherData(Application application) {

        Cursor cursor = application.getContentResolver().query(WeatherDataContract.CITY_URI,
                WeatherDataContract.PROJECTION_CITY,
                null,
                null,
                null);
        int numOfCities = cursor.getCount();
        if (numOfCities == 0){
            return;
        }
        cursor.moveToFirst();
        for (int i = 0; i < numOfCities; i++) {
            String zipcode = cursor.getString(cursor.getColumnIndex(WeatherDataContract.CityEntry.COLUMN_ZIPCODE));
            int cityID = cursor.getInt(cursor.getColumnIndex(WeatherDataContract.CityEntry.COLUMN_ID));
            cursor.moveToNext();
            URL url = NetworkUtil.buildUrlWithZipcode(zipcode);
            String response = NetworkUtil.getReponseFromUrl(url);
            ContentValues[] values = null;
            try {
                if(NetworkUtil.receiveResponseSuccessfully(response))
                    values = NetworkUtil.getWeatherDataFromJSON(response, String.valueOf(cityID));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (values != null && values.length != 0) {
                application.getContentResolver().delete(WeatherDataContract.WEATHER_DATA_URI,
                        null,
                        new String[]{String.valueOf(cityID)});
                application.getContentResolver().bulkInsert(WeatherDataContract.WEATHER_DATA_URI,
                        values);
            }

        }
    }
}
