package com.yosriz.datamaps.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RawRes;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.List;
import java.util.Scanner;


public class DataMapsView extends WebView {

    private GeoChartLoadingListener geoChartLoadingListener;
    private Handler handler = new Handler();
    private String markerIcon;
    private int markerWidth;
    private int markerHeight;
    private String projection;
    private boolean dataLoaded = false;
    private boolean globalLayoutOccurred = false;
    private DataMapsData mapData;
    private boolean htmlLoaded;
    private MapDataInterface mapDataInterface = new MapDataInterface();
    private int defaultfillColor;

    public interface GeoChartLoadingListener {
        void onSuccess();

        void onFailure();
    }

    public DataMapsView(Context context) {
        this(context, null);
    }

    public DataMapsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressLint({"SetJavaScriptEnabled", "DefaultLocale"})
    public DataMapsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (!isInEditMode()) {
            getSettings().setJavaScriptEnabled(true);
            getSettings().setBuiltInZoomControls(false);
            getSettings().setUseWideViewPort(true);
            getSettings().setLoadWithOverviewMode(true);
        }
        preventTouchEvents();
        listenToPageLoadingEvents();

        enableDebuggingIfNecessary(context);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.DataMapsView, 0, 0);
        try {
            markerWidth = ta.getDimensionPixelSize(R.styleable.DataMapsView_marker_width, 50);
            markerHeight = ta.getDimensionPixelSize(R.styleable.DataMapsView_marker_height, 50);
            markerIcon = ta.getString(R.styleable.DataMapsView_marker_icon);
            projection = ta.getString(R.styleable.DataMapsView_projection);
            defaultfillColor = ta.getColor(R.styleable.DataMapsView_default_fill_color, Color.LTGRAY);
        } finally {
            ta.recycle();
        }

        Thread thread = new Thread() {
            @Override
            public void run() {
                String html = readStringFromRawResources(R.raw.datamaps);
                handler.post(() -> {
                            addJavascriptInterface(mapDataInterface, "mapData");
                            loadDataWithBaseURL("file:///android_asset/", html, "text/html", "utf-8", null);
                        }
                );
            }
        };
        thread.start();

        getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            globalLayoutOccurred = true;
            setAndLoadMapData();
        });
    }

    private void enableDebuggingIfNecessary(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (0 != (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE)) {
                WebView.setWebContentsDebuggingEnabled(true);
            }
        }
    }

    private void preventTouchEvents() {
        setOnTouchListener((v, event) -> true);
    }

    private void listenToPageLoadingEvents() {
        addJavascriptInterface(new LoadingListenerInterface(), "loadingListener");
        setWebViewClient(new WebViewClient() {

            @Override
            public void onReceivedError(WebView view, int errorCode, String description,
                                        String failingUrl) {
                if (geoChartLoadingListener != null) {
                    geoChartLoadingListener.onFailure();
                }
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request,
                                            WebResourceResponse errorResponse) {
                if (geoChartLoadingListener != null) {
                    geoChartLoadingListener.onFailure();
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                htmlLoaded = true;
                setAndLoadMapData();
            }

        });
    }

    public void setGeoChartLoadingListener(GeoChartLoadingListener listener) {
        this.geoChartLoadingListener = listener;
    }

    @SuppressLint("DefaultLocale")
    public void loadData(DataMapsData data) {
        mapData = data;
        dataLoaded = true;
        setAndLoadMapData();
    }

    /**
     * Sets marker icon, icon file must be placed at assets folder
     *
     * @param markerIcon icon assets filename
     */
    public void setMarkerIcon(String markerIcon) {
        this.markerIcon = markerIcon;
    }

    /**
     * Sets marker icon width
     *
     * @param markerWidth marker icon width
     */
    public void setMarkerWidth(int markerWidth) {
        this.markerWidth = markerWidth;
    }

    /**
     * Sets marker icon height
     *
     * @param markerHeight marker icon height
     */
    public void setMarkerHeight(int markerHeight) {
        this.markerHeight = markerHeight;
    }

    /**
     * Set map projection
     *
     * @param projection map projection
     */
    public void setProjection(String projection) {
        this.projection = projection;
    }

    public String getMarkerIcon() {
        return markerIcon;
    }

    public int getMarkerWidth() {
        return markerWidth;
    }

    public int getMarkerHeight() {
        return markerHeight;
    }

    public String getProjection() {
        return projection;
    }

    private void setAndLoadMapData() {
        if (dataLoaded && htmlLoaded && globalLayoutOccurred) {
            //Log.d("DataMapsView", "getHeight() = " + getHeight() + "getWidth() = " + getWidth());
            String dataJson = toJson(mapData.getCountries());
            mapDataInterface.setCountriesJson(dataJson);
            mapDataInterface.setHeight(getHeight());
            mapDataInterface.setWidth(getWidth());
            mapDataInterface.setMarkerPinHeight(markerHeight);
            mapDataInterface.setMarkerPinWidth(markerWidth);
            mapDataInterface.setMarkerPinIcon(markerIcon);
            mapDataInterface.setProjection(projection);
            mapDataInterface.setDefaultFillColor(Utils.colorToHexString(defaultfillColor));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                evaluateJavascript("loadMap()", null);
            } else {
                loadUrl("javascript:loadMap()");
            }
            dataLoaded = htmlLoaded = false;
        }
    }

    private String toJson(List<CountryData> data) {
        JSONArray jsonArray = new JSONArray();
        try {
            for (CountryData countryData : data) {
                JSONObject countryJson = new JSONObject();

                countryJson.put("country", countryData.getCountry());
                countryJson.put("color", countryData.getColor());
                countryJson.put("marker", countryData.doesHaveMarker());

                jsonArray.put(countryJson);
            }

        } catch (JSONException e) {
            Log.e(DataMapsView.class.getSimpleName(), "Fatal Error, Cannot serialize country data to json", e);
            e.printStackTrace();
        }

        return jsonArray.toString();
    }

    private String readStringFromRawResources(@RawRes int res) {
        InputStream inputStream = getContext().getResources().openRawResource(res);
        Scanner s = new Scanner(inputStream, "UTF-8").useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    private class LoadingListenerInterface {

        @JavascriptInterface
        public void loadError() {
            getHandler().post(() -> {
                if (geoChartLoadingListener != null)
                    geoChartLoadingListener.onFailure();
            });
        }

        @JavascriptInterface
        public void loadSuccess() {
            getHandler().post(() -> {
                if (geoChartLoadingListener != null) geoChartLoadingListener.onSuccess();
            });
        }
    }

    private class MapDataInterface {
        private int markerPinWidth;
        private int markerPinHeight;
        private int width;
        private int height;
        private String markerPinIcon;
        private String countriesJson;
        private String projection;
        private String defaultFillColor;

        public void setMarkerPinWidth(int markerPinWidth) {
            this.markerPinWidth = markerPinWidth;
        }

        public void setMarkerPinHeight(int markerPinHeight) {
            this.markerPinHeight = markerPinHeight;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public void setMarkerPinIcon(String markerPinIcon) {
            this.markerPinIcon = markerPinIcon;
        }

        public void setCountriesJson(String countriesJson) {
            this.countriesJson = countriesJson;
        }

        public void setProjection(String projection) {
            this.projection = projection;
        }

        @JavascriptInterface
        public int getMarkerIconWidth() {
            return markerPinWidth;
        }

        @JavascriptInterface
        public int getMarkerIconHeight() {
            return markerPinHeight;
        }

        @JavascriptInterface
        public int getWidth() {
            return width;
        }

        @JavascriptInterface
        public int getHeight() {
            return height;
        }

        @JavascriptInterface
        public String getCountriesJson() {
            return countriesJson;
        }

        @JavascriptInterface
        public String getMarkerIcon() {
            return markerPinIcon;
        }

        @JavascriptInterface
        public String getProjection() {
            return projection;
        }

        @JavascriptInterface
        public String getDefaultFillColor() {
            return defaultFillColor;
        }

        public void setDefaultFillColor(String defaultFillColor) {
            this.defaultFillColor = defaultFillColor;
        }
    }
}