package com.yosriz.datamaps.android.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.yosriz.datamaps.android.DataMapsView;
import com.yosriz.datamaps.android.DataMapsData;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DataMapsView dataMapsView = (DataMapsView) findViewById(R.id.map);
        findViewById(R.id.refresh).setOnClickListener(v -> dataMapsView.reload());

        List<DataMapsData.CountryData> countryDataList = new ArrayList<>();
        countryDataList.add(new DataMapsData.CountryData("USA", 1));
        countryDataList.add(new DataMapsData.CountryData("ISR", 1));
        DataMapsData mapData = new DataMapsData(countryDataList);

        dataMapsView.setGeoChartLoadingListener(new DataMapsView.GeoChartLoadingListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "load success");
            }

            @Override
            public void onFailure() {
                Log.d(TAG, "load Failure");
            }
        });
        dataMapsView.loadData(mapData);
    }
}
