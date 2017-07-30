package com.yosriz.datamaps.android;

import java.util.List;

public class DataMapsData {
    private final List<CountryData> countries;

    public DataMapsData(List<CountryData> countries) {
        this.countries = countries;
    }

    public List<CountryData> getCountries() {
        return countries;
    }

}
