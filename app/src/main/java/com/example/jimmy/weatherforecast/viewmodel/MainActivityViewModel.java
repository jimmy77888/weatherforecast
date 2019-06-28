package com.example.jimmy.weatherforecast.viewmodel;

import android.app.Application;
import android.database.Cursor;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.jimmy.weatherforecast.Repository.WeatherRepository;
import com.example.jimmy.weatherforecast.model.WeatherData;

import java.util.List;

public class MainActivityViewModel extends AndroidViewModel {

    private WeatherRepository weatherRepository;

    public MainActivityViewModel(Application application) {
        super(application);
        weatherRepository = new WeatherRepository(application);
    }

    public void fetchWeatherDataList(String cityID) {
        weatherRepository.showWeatherDataList(cityID);
    }


    public MutableLiveData<List<WeatherData>> getLiveWeatherDataList() {
        return weatherRepository.getLiveWeatherDataList();
    }

    public void initWeatherDataList() {
        String cityID = weatherRepository.initCityID();
        weatherRepository.showWeatherDataList(cityID);
    }
}
