package com.example.jimmy.weatherforecast.view;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jimmy.weatherforecast.R;
import com.example.jimmy.weatherforecast.model.WeatherDataContract;
import com.example.jimmy.weatherforecast.net.WeatherDataSyncUtil;
import com.example.jimmy.weatherforecast.view.adapter.WeatherRecyclerViewAdapter;
import com.example.jimmy.weatherforecast.viewmodel.MainActivityViewModel;


public class MainActivity extends AppCompatActivity {

    private static final int LOADER_ID = 100;
    private RecyclerView rvWeatherList;
    private ImageButton ibCities;
    private static final int REQUEST_CODE = 1;
    private static final int RESULT_CODE = 2;
    private WeatherRecyclerViewAdapter weatherAdapter;
    private MainActivityViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        ibCities = (ImageButton)findViewById(R.id.ib_cities);
        rvWeatherList = (RecyclerView)findViewById(R.id.rv_weather_list);
        ibCities.setOnClickListener((view)->{
            Intent intent = new Intent(MainActivity.this, CitiesActivity.class);
            startActivityForResult(intent, REQUEST_CODE);
        });
        weatherAdapter = new WeatherRecyclerViewAdapter();
        rvWeatherList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false)); //TODO set it available
        rvWeatherList.setAdapter(weatherAdapter);
        viewModel.getLiveWeatherDataList().observe(this, (list)->{
            weatherAdapter.setList(list);
        });
        viewModel.initWeatherDataList();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_CODE) {
            int numCityID = data.getIntExtra(Intent.EXTRA_TEXT, 0);
            if (numCityID != 0) {
                String cityID = String.valueOf(numCityID);
                new Thread(){
                    @Override
                    public void run() {
                        Log.d("debug", "MainActivity::onActiivtyResult::numCityID="+cityID);
                        viewModel.fetchWeatherDataList(cityID);
                    }
                }.start();

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.main, menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_item_setting:

                Intent intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_item_share:

                Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                        .setType("text/plain")
                        .setText(WeatherDataContract.WEATHER_DATA_URI.toString())
                        .getIntent();
                if (shareIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(shareIntent);
                }
                break;
        }
        return true;
    }
}

