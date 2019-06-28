package com.example.jimmy.weatherforecast.net;

import android.app.IntentService;
import android.content.Intent;

public class FetchWeatherDataIntentService extends IntentService {

    public FetchWeatherDataIntentService() {
        super("FetchWeatherDataIntentService");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        WeatherDataSyncUtil.syncAllWeatherData(getApplication());
    }
}
   