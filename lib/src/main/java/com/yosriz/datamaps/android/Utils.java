package com.yosriz.datamaps.android;


import android.support.annotation.ColorInt;

public class Utils {

    public static String colorToHexString(@ColorInt int color) {
        return String.format("#%06X", (0xFFFFFF & color));
    }

}
