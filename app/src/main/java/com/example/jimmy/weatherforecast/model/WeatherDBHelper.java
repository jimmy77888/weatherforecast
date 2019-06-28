package com.example.jimmy.weatherforecast.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class WeatherDBHelper extends SQLiteOpenHelper {



    public WeatherDBHelper(Context context) {
        super(context, WeatherDataContract.DATABASE_NAME, null, WeatherDataContract.VERSION_NUMBER);

    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String SQL_CREATE_WEATHER_DATA_TABLE = "CREATE TABLE "+WeatherDataContract.WEATHER_DATA_TABLE
                +"("+WeatherDataContract.WeatherDataEntry.COLUMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
                +WeatherDataContract.WeatherDataEntry.COLUMN_CITY_ID+" INTEGER NOT NULL, "
                +WeatherDataContract.WeatherDataEntry.COLUMN_DATE+" REAL NOT NULL, "
                +WeatherDataContract.WeatherDataEntry.COLUMN_WEATHER_CONDITION+" TEXT NOT NULL, "
                +WeatherDataContract.WeatherDataEntry.COLUMN_DESCRIPTION+" TEXT NOT NULL, "
                +WeatherDataContract.WeatherDataEntry.COLUMN_TEMP+" REAL NOT NULL, "
                +WeatherDataContract.WeatherDataEntry.COLUMN_TEMP_MAX+" REAL NOT NULL, "
                +WeatherDataContract.WeatherDataEntry.COLUMN_TEMP_MIN+" REAL NOT NULL, "
                +WeatherDataContract.WeatherDataEntry.COLUMN_HUMIDITY+" INTEGER NOT NULL, "
                +WeatherDataContract.WeatherDataEntry.COLUMN_WIND_SPEED+" REAL NOT NULL, "
                +WeatherDataContract.WeatherDataEntry.COLUMN_WIND_DEGREE+" REAL NOT NULL, "
                +WeatherDataContract.WeatherDataEntry.COLUMN_PRESSURE+" REAL NOT NULL)";

        sqLiteDatabase.execSQL(SQL_CREATE_WEATHER_DATA_TABLE);

        String SQL_CITY_TABLE = "CREATE TABLE " + WeatherDataContract.CITY_DATA_TABLE
                + " (" + WeatherDataContract.CityEntry.COLUMN_ID + " INTEGER PRIMARY KEY, "
                + WeatherDataContract.CityEntry.COLUMN_NAME + " TEXT NOT NULL, "
                + WeatherDataContract.CityEntry.COLUMN_ZIPCODE + " INTEGER NOT NULL, "
                + WeatherDataContract.CityEntry.COLUMN_LAT + " REAL NOT NULL, "
                + WeatherDataContract.CityEntry.COLUMN_LON + " REAL NOT NULL, "
                + WeatherDataContract.CityEntry.COLUMN_TIMEZONE + " INTEGER NOT NULL, "
                + WeatherDataContract.CityEntry.COLUMN_COUNTRY + " TEXT NOT NULL)";
        sqLiteDatabase.execSQL(SQL_CITY_TABLE);
        Log.d("debug", "WeatherDBHelper:: onCreate");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+WeatherDataContract.WEATHER_DATA_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + WeatherDataContract.CITY_DATA_TABLE);
        onCreate(sqLiteDatabase);
    }
}
