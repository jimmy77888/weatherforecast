package com.example.jimmy.weatherforecast.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.jimmy.weatherforecast.Repository.WeatherRepository;
import com.example.jimmy.weatherforecast.model.City;

import java.util.List;

public class CityActivityViewModel extends AndroidViewModel {

    private WeatherRepository weatherRepository;

    public CityActivityViewModel( Application application) {
        super(application);
        weatherRepository = new WeatherRepository(application);
    }

    public void fetchCityList() {
        weatherRepository.fetchCitiesFromContentProvider();
    }

    public void fetchCityFromHttp(String zipcode) {
        weatherRepository.fetchCityFromHttp(zipcode);

    }

    public MutableLiveData<List<City>> getLiveCityList() {
        return weatherRepository.getLiveCityList();
    }


}
