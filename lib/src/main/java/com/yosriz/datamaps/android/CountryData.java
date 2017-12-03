package com.yosriz.datamaps.android;


import android.support.annotation.ColorInt;

public class CountryData {

    private final String country;
    private final String colorString;
    private final boolean doesHaveMarker;
    private final float value;
    private final String label;

    private CountryData(boolean putMarker, int color, String country, float value, String label) {
        this.colorString = Utils.colorToHexString(color);
        this.country = country;
        this.value = value;
        this.label = label;
        this.doesHaveMarker = putMarker;
    }

    public String getCountry() {
        return country;
    }

    public String getColor() {
        return colorString;
    }

    public boolean doesHaveMarker() {
        return doesHaveMarker;
    }

    public float getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }

    public static class Builder {
        private boolean putMarker;
        private @ColorInt int color;
        private String country;
        private float value;
        private String label;

        /**
         * Whether to add marker for this country on map
         */
        public Builder marker(boolean show) {
            putMarker = show;
            return this;
        }

        public Builder color(@ColorInt int color) {
            this.color = color;
            return this;
        }

        /**
         * Sets country
         *
         * @param country 3 letters ISO string represents a country
         */
        public Builder country(String country) {
            this.country = country;
            return this;
        }

        public Builder value(float value) {
            this.value = value;
            return this;
        }

        public Builder label(String label) {
            this.label = label;
            return this;
        }

        public CountryData build() {
            return new CountryData(putMarker, color, country, value, label);
        }
    }
}
