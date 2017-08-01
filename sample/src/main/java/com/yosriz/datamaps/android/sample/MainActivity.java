package com.yosriz.datamaps.android.sample;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.yosriz.datamaps.android.CountryData;
import com.yosriz.datamaps.android.DataMapsView;
import com.yosriz.datamaps.android.DataMapsData;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public static String TAG = MainActivity.class.getSimpleName();
    public boolean toggle = false;

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
        countryDataList.add(new CountryData("USA", Color.BLUE, true));
        countryDataList.add(new CountryData("ISR", toggle));
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
        this.toggle = !this.toggle;
    }
}
