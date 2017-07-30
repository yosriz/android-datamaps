package com.yosriz.datamaps.android;


import android.support.annotation.ColorInt;

public class CountryData {

    private final String country;
    private final transient int color;
    private final String colorString;

    /**
     * Constructs country data
     *
     * @param country 3 letters ISO string represents a country
     * @param color   country color int
     */
    public CountryData(String country, @ColorInt int color) {
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
