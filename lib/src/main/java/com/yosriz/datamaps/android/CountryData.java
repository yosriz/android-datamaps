package com.yosriz.datamaps.android;


import android.support.annotation.ColorInt;

public class CountryData {

    private final String country;
    private final String colorString;
    private final boolean marker;

    /**
     * Constructs country data with custom color
     *
     * @param country 3 letters ISO string represents a country
     * @param color   country color int
     * @param marker  should add marker to this country
     */
    public CountryData(String country, @ColorInt int color, boolean marker) {
        this.country = country;
        this.colorString = Utils.colorToHexString(color);
        this.marker = marker;
    }

    /**
     * Constructs country data, without custom color
     *
     * @param country 3 letters ISO string represents a country
     * @param marker  should add marker to this country
     */
    public CountryData(String country, boolean marker) {
        this.country = country;
        this.colorString = null;
        this.marker = marker;
    }

    public String getCountry() {
        return country;
    }

    public String getColor() {
        return colorString;
    }

    public boolean doesHaveMarker() {
        return marker;
    }
}
