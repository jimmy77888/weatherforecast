package com.example.jimmy.weatherforecast.model;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

//TODO lambdas format
//TODO RxJava
public class WeatherDataProvider extends ContentProvider {

    private static final int CODE_WEATHER = 100;
    private static final int CODE_CITY = 101;
    private static final UriMatcher matcher = buildUrimatch();
    private static final String PATH_WEATHER = "weather";
    private static final String PATH_CITY = "city";
    private WeatherDBHelper weatherDBHelper;
    private static final String[] weatherDataColumns = {
            WeatherDataContract.WeatherDataEntry.COLUMN_ID,
            WeatherDataContract.WeatherDataEntry.COLUMN_DATE,
            WeatherDataContract.WeatherDataEntry.COLUMN_WEATHER_CONDITION,
            WeatherDataContract.WeatherDataEntry.COLUMN_DESCRIPTION,
            WeatherDataContract.WeatherDataEntry.COLUMN_TEMP,
            WeatherDataContract.WeatherDataEntry.COLUMN_TEMP_MAX,
            WeatherDataContract.WeatherDataEntry.COLUMN_TEMP_MIN,
            WeatherDataContract.WeatherDataEntry.COLUMN_HUMIDITY,
            WeatherDataContract.WeatherDataEntry.COLUMN_PRESSURE
    };


    private static UriMatcher buildUrimatch() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(WeatherDataContract.AUTHORITY, PATH_WEATHER, CODE_WEATHER);
        matcher.addURI(WeatherDataContract.AUTHORITY, PATH_CITY, CODE_CITY);
        return matcher;
    }

    @Override
    public String getType(Uri uri) {
        int code = matcher.match(uri);
        switch (code) {
            case CODE_WEATHER:
                return WeatherDataContract.CONTENT_TYPE_WEATHER;

            case CODE_CITY:
                return WeatherDataContract.CONTENT_TYPE_CITY;
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public boolean onCreate() {
        weatherDBHelper = new WeatherDBHelper(getContext());
        return true;
    }

    //TODO  try to put cityID as param passed to query() method instead of a segment of Uri
    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {
        int matchType = matcher.match(uri);
        Cursor cursor;
        switch (matchType) {
            case CODE_WEATHER:
                cursor = weatherDBHelper.getReadableDatabase()
                        .query(WeatherDataContract.WEATHER_DATA_TABLE,
                                strings,
                                WeatherDataContract.WeatherDataEntry.COLUMN_CITY_ID + " = ?",
                                strings1,
                                null,
                                null,
                                WeatherDataContract.WeatherDataEntry.COLUMN_DATE);
                break;

            case CODE_CITY:
                cursor = weatherDBHelper.getReadableDatabase()
                        .query(WeatherDataContract.CITY_DATA_TABLE,
                                strings,
                                s,
                                strings1,
                                null,
                                null,
                                null
                        );
                break;
            default:
                throw new IllegalArgumentException();
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        int matchType = matcher.match(uri);
        long id = -1;
        switch (matchType) {
            case CODE_CITY:
                id = weatherDBHelper.getWritableDatabase()
                        .insert(WeatherDataContract.CITY_DATA_TABLE,
                                null,
                                contentValues);
                break;
            case CODE_WEATHER:
                id = weatherDBHelper.getWritableDatabase()
                        .insert(WeatherDataContract.WEATHER_DATA_TABLE,
                                null,
                                contentValues);
                break;
            default:
                throw new IllegalArgumentException();
        }
        Log.d("debug", "WeatherDataProvider::id="+id);
        if (id != -1) {
            getContext().getContentResolver().notifyChange(uri, null);
            return uri;
        }
        return null;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        int numOfInserted = 0;
        int matchType = matcher.match(uri);
        switch (matchType) {
            case CODE_WEATHER:
                for (ContentValues value : values) {
                    //long date = value.getAsLong(WeatherDataContract.WeatherDataEntry.COLUMN_DATE);
                    /*
                    if(!WeatherDataUtil.isDateNormalized(date))
                        throw new IllegalArgumentException("Date must be normalized.");
                    */

                    weatherDBHelper.getWritableDatabase()
                            .insert(WeatherDataContract.WEATHER_DATA_TABLE,
                                    null,
                                    value);
                    numOfInserted++;
                }
                break;
        }
        if (numOfInserted > 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return numOfInserted;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        int numOfDeleted = 0;
        int matchType = matcher.match(uri);
        switch (matchType) {
            case CODE_WEATHER:
                numOfDeleted = weatherDBHelper.getWritableDatabase()
                        .delete(WeatherDataContract.WEATHER_DATA_TABLE,
                                WeatherDataContract.WeatherDataEntry.COLUMN_CITY_ID + "=?",
                                strings);

                break;
            case CODE_CITY:
                numOfDeleted = weatherDBHelper.getWritableDatabase()
                        .delete(WeatherDataContract.CITY_DATA_TABLE,
                                WeatherDataContract.CityEntry.COLUMN_ID + "=?",
                                strings);

            default:
                throw new IllegalArgumentException();
        }
        if (numOfDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numOfDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        throw new UnsupportedOperationException("Do not implement this method.");
    }
}
