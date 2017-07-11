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

    public static class CountryData {

        final String country;
        final transient int color;
        final String colorString;

        public CountryData(String country, int color) {
            this.country = country;
            this.color = color;
            colorString = String.format("#%06X", (0xFFFFFF & color));
        }

        public String getCountry() {
            return country;
        }

        public int getColor() {
            return color;
        }
    }
}
