package com.example.jimmy.weatherforecast.net;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import android.os.AsyncTask;

public class FetchDataJobService extends JobService {
    private AsyncTask asyncTaskFetchData;
    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        asyncTaskFetchData = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                WeatherDataSyncUtil.syncAllWeatherData(getApplication());
                jobFinished(jobParameters,false );
                return null;
            }

            @Override
            protected void onPostExecute(Void param) {
                jobFinished(jobParameters,false);
            }
        };
        asyncTaskFetchData.execute();
        //return true so the thread wouldn't be finished
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        if (asyncTaskFetchData != null) {
            asyncTaskFetchData.cancel(true);
        }
        return true;
    }
}
