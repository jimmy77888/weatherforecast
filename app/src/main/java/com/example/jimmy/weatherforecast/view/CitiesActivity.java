package com.example.jimmy.weatherforecast.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.example.jimmy.weatherforecast.R;
import com.example.jimmy.weatherforecast.view.adapter.CityListAdapter;
import com.example.jimmy.weatherforecast.viewmodel.CityActivityViewModel;


//TODO note! livedata.setValue() must be called in main thread! And postValue() can be could in worker thread. Otherwise, the observer won't be triggered!!!

public class CitiesActivity extends AppCompatActivity implements ZipcodeDialogFragment.OnButtonClickListener, CityListAdapter.OnItemClickListener {


    private CityActivityViewModel viewModel;
    private static final int RESULT_CODE = 2;
    private static final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cities);
        viewModel = ViewModelProviders.of(this).get(CityActivityViewModel.class);
        ListView lvCityList = (ListView)findViewById(R.id.lv_city_list);
        CityListAdapter adapter = new CityListAdapter(null, this);
        lvCityList.setAdapter(adapter);
        viewModel.fetchCityList();
        viewModel.getLiveCityList().observe(this, adapter::setList);
        ImageButton ibAddCity = (ImageButton)findViewById(R.id.ib_add_city);
        ibAddCity.setOnClickListener((view)->{
            ZipcodeDialogFragment zipcodeDialogFragment = new ZipcodeDialogFragment();
            zipcodeDialogFragment.show(getSupportFragmentManager(), "zipcodeDialogFragment");
        });
    }

    @Override
    public void onItemClick(int cityID) {
        Intent intent = new Intent();
        intent.putExtra(Intent.EXTRA_TEXT, cityID);
        setResult(RESULT_CODE, intent);
        finish();
    }

    @Override
    public void onOkClick(String zipcode) {
        viewModel.fetchCityFromHttp(zipcode);
    }

}
