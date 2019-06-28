package com.example.jimmy.weatherforecast.Repository;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.util.MalformedJsonException;
import android.util.TimeUtils;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.example.jimmy.weatherforecast.R;
import com.example.jimmy.weatherforecast.model.City;
import com.example.jimmy.weatherforecast.model.FlatUtil;
import com.example.jimmy.weatherforecast.model.WeatherData;
import com.example.jimmy.weatherforecast.model.WeatherDataContract;
import com.example.jimmy.weatherforecast.net.NetworkUtil;
import com.example.jimmy.weatherforecast.net.WeatherDataSyncUtil;

import org.json.JSONException;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class WeatherRepository {

    private Application application;
    private MutableLiveData<List<City>> liveCityList;
    private MutableLiveData<List<WeatherData>> liveWeatherDataList;


    public WeatherRepository(Application application) {
        this.application = application;
        liveCityList = new MutableLiveData<>();
        liveWeatherDataList = new MutableLiveData<>();
    }

    public void fetchCitiesFromContentProvider() {

        List<City> list = new ArrayList<>();
        Cursor cursor = application.getContentResolver().query(WeatherDataContract.CITY_URI,
                WeatherDataContract.PROJECTION_CITY,
                null,
                null,
                null);
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                list.add(FlatUtil.cursorToCity(cursor));
            } while (cursor.moveToNext());
            liveCityList.postValue(list);
        }
    }

    public Cursor fetchWeatherDataCursorFromContentProvider(String cityID) {

        List<WeatherData> list = new ArrayList<>();
        Cursor cursor = application.getContentResolver().query(WeatherDataContract.WEATHER_DATA_URI,
                WeatherDataContract.PROJECTION_WEATHER,
                WeatherDataContract.WeatherDataEntry.COLUMN_CITY_ID + " = ?",
                new String[]{cityID},
                WeatherDataContract.WeatherDataEntry.COLUMN_DATE);
        return cursor;
    }

    public void showWeatherDataList(String cityID) {
        Cursor cursor = fetchWeatherDataCursorFromContentProvider(cityID);
        if (cursor != null && cursor.getCount() != 0 && !isDataOutOfDate(cursor)) {
            List<WeatherData> list = new ArrayList<>();
            cursor.moveToFirst();
            do {
                list.add(FlatUtil.cursorToWeatherData(application, cursor));
            } while (cursor.moveToNext());
            Log.d("debug", "WeatherRepository::cursor "+cursor.getCount());
            liveWeatherDataList.postValue(list);
        } else {

            deleteWeatherData(cityID);
            fetchWeatherDataFromHttp(cityID);
            Cursor cursor1 = fetchWeatherDataCursorFromContentProvider(cityID);
            List<WeatherData> list = new ArrayList<>();
            if (cursor1 != null && cursor1.getCount() != 0) {
                cursor1.moveToFirst();
                do {
                    list.add(FlatUtil.cursorToWeatherData(application, cursor1));
                } while (cursor1.moveToNext());

                liveWeatherDataList.postValue(list);
            }
            Log.d("debug", "WeatherRepository::cursor1 "+cursor1.getCount());
        }
    }

    public void deleteWeatherData(String cityID) {
        int numOfDeleted = application.getContentResolver().delete(WeatherDataContract.WEATHER_DATA_URI,
                null,
                new String[]{cityID});
        Log.d("debug", "deleteWeatherData "+numOfDeleted);
    }
    public boolean isDataOutOfDate(Cursor cursor) {
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToLast();
            long date = cursor.getLong(cursor.getColumnIndex(WeatherDataContract.WeatherDataEntry.COLUMN_DATE));
            long currentTime = System.currentTimeMillis();
            long DAY_IN_MILLIS = TimeUnit.DAYS.toMillis(1);
            if ((currentTime - date * 1000) > DAY_IN_MILLIS) {
                Log.d("debug", "currentTime= "+currentTime+" date="+date*1000+" DAY_IN_MILLIS= "+DAY_IN_MILLIS);
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    public void fetchWeatherDataFromHttp(String cityID) {
        int zipcode = getZipcodeFromCityID(cityID);
        URL url = NetworkUtil.buildUrlWithZipcode(String.valueOf(zipcode));
        String response = NetworkUtil.getReponseFromUrl(url);
        ContentValues[] values = null;
        try {
            if(NetworkUtil.receiveResponseSuccessfully(response))
                values = NetworkUtil.getWeatherDataFromJSON(response, cityID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (values != null && values.length != 0) {
            int num = application.getContentResolver().bulkInsert(WeatherDataContract.WEATHER_DATA_URI,
                    values);

            Log.d("debug", "WeatherRepository:: fetchWeatherDataFromHttp "+num);
        }

    }

    public int getZipcodeFromCityID(String cityID) {
        Cursor cursor = application.getContentResolver().query(WeatherDataContract.CITY_URI,
                new String[]{WeatherDataContract.CityEntry.COLUMN_ZIPCODE},
                WeatherDataContract.CityEntry.COLUMN_ID + " = ?",
                new String[]{cityID},
                null);
        int zipcode = 0;
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            zipcode = cursor.getInt(cursor.getColumnIndex(WeatherDataContract.CityEntry.COLUMN_ZIPCODE));
        }
        return zipcode;
    }
    public String initCityID() {
        Cursor cursor = application.getContentResolver().query(WeatherDataContract.CITY_URI,
                new String[]{WeatherDataContract.CityEntry.COLUMN_ID},
                null,
                null,
                WeatherDataContract.CityEntry.COLUMN_ID);
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            return String.valueOf(cursor.getInt(cursor.getColumnIndex(WeatherDataContract.CityEntry.COLUMN_ID)));
        }
        return null;
    }

    public void initWeatherDataList() {
        String cityID;
        if ((cityID = initCityID()) != null) {
            fetchWeatherDataCursorFromContentProvider(cityID);
        }
    }
    public void fetchCityFromHttp(String zipcode) {

        Flowable.fromCallable(() -> {
            URL url = NetworkUtil.buildUrlWithZipcode(zipcode);
            String response = NetworkUtil.getReponseFromUrl(url);
            ContentValues values = NetworkUtil.getCityDataFromJSON(response, zipcode);
            Uri uri = application.getContentResolver().insert(WeatherDataContract.CITY_URI, values);
            if (uri != null) {
                fetchCitiesFromContentProvider();
                return true;
            } else {
                return false;
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((isAdded) -> {
                    if (!isAdded)
                        Toast.makeText(application, R.string.city_activity_toast_city_exists, Toast.LENGTH_SHORT).show();
                }, Throwable::printStackTrace);
    }

    public MutableLiveData<List<City>> getLiveCityList() {
        return liveCityList;
    }

    public MutableLiveData<List<WeatherData>> getLiveWeatherDataList() {
        return liveWeatherDataList;
    }
    //following methods used only once

    public void syncAllWeatherData(Application application) {
        WeatherDataSyncUtil.syncAllWeatherData(application);
    }

    public void initializeSyncTask(Application application) {
        WeatherDataSyncUtil.initializeSyncTask(application);
    }

    public void immidiatelySync(Application application) {
        WeatherDataSyncUtil.immidiatelySync(application);
    }

}
