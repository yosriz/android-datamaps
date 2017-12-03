package com.yosriz.datamaps.android.sample;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.yosriz.datamaps.android.CountryData;
import com.yosriz.datamaps.android.DataMapsData;
import com.yosriz.datamaps.android.DataMapsView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DataMapsView dataMapsView = (DataMapsView) findViewById(R.id.map);
        findViewById(R.id.refresh).setOnClickListener(v -> {
            dataMapsView.reload();
            populateMap(dataMapsView);
        });

        populateMap(dataMapsView);
    }

    private void populateMap(DataMapsView dataMapsView) {
        List<CountryData> countryDataList = new ArrayList<>();
        countryDataList.add(new CountryData.Builder()
                .color(ContextCompat.getColor(this, R.color.colorA))
                .country("USA")
                .marker(true)
                .build());
        countryDataList.add(
                new CountryData.Builder()
                        .color(ContextCompat.getColor(this, R.color.colorB))
                        .country("DEU")
                        .marker(true)
                        .build());
        countryDataList.add(
                new CountryData.Builder()
                        .color(ContextCompat.getColor(this, R.color.colorC))
                        .country("MEX")
                        .marker(false)
                        .build());
        countryDataList.add(
                new CountryData.Builder()
                        .color(ContextCompat.getColor(this, R.color.colorD))
                        .country("CHN")
                        .marker(true)
                        .build());
        countryDataList.add(
                new CountryData.Builder()
                        .color(ContextCompat.getColor(this, R.color.colorE))
                        .country("BRA")
                        .marker(false)
                        .build());
        countryDataList.add(
                new CountryData.Builder()
                        .color(ContextCompat.getColor(this, R.color.colorF))
                        .country("DZA")
                        .marker(true)
                        .build());
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
