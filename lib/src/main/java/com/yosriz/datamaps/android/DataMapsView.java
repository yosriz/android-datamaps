package com.yosriz.datamaps.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RawRes;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONArray;

import java.io.InputStream;
import java.util.List;
import java.util.Scanner;


public class DataMapsView extends WebView {

    public static final double MAP_ASPECT_RATIO = 0.66875;
    private GeoChartLoadingListener geoChartLoadingListener;
    private Handler handler = new Handler();
    private Thread thread;
    private int markerWidth;
    private int markerHeight;

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

    @SuppressLint("SetJavaScriptEnabled")
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

        //TODO init from xml
        markerWidth = 50;
        markerHeight = 50;
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
        });
    }

    public void setGeoChartLoadingListener(GeoChartLoadingListener listener) {
        this.geoChartLoadingListener = listener;
    }

    @SuppressLint("DefaultLocale")
    public void loadData(DataMapsData data) {

        cancelCurrentLoad();
        setMapData(data);
        thread = new Thread() {
            @Override
            public void run() {
                String html = readStringFromRawResources(R.raw.datamaps);
                handler.post(() ->
                        loadDataWithBaseURL("file:///android_asset/", html, "text/html", "utf-8", null)
                );
            }
        };
        thread.start();
    }

    private void cancelCurrentLoad() {
        handler.removeCallbacksAndMessages(null);
        if (thread != null && !thread.isInterrupted()) {
            thread.interrupt();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        cancelCurrentLoad();
    }

    private void setMapData(DataMapsData data) {

        Point webViewSize = calculateWebViewSize();
        setWebViewHeight(webViewSize);


        String dataJson = toJson(data.getCountries());
        MapData mapData = new MapData(markerWidth,
                markerHeight,
                webViewSize.x, webViewSize.y,
                dataJson
        );
        addJavascriptInterface(mapData, "mapData");
    }

    private String toJson(List<DataMapsData.CountryData> data) {
        JSONArray jsonArray = new JSONArray();

        for (DataMapsData.CountryData countryData : data) {
            jsonArray.put(countryData.country);
        }

        return jsonArray.toString();
    }

    @NonNull
    private Point calculateWebViewSize() {
        Point displaySize = getDisplaySize();
        int margins = 0;
        if (getLayoutParams() instanceof MarginLayoutParams) {
            MarginLayoutParams layoutParams = (MarginLayoutParams) getLayoutParams();
            margins = layoutParams.getMarginStart() + layoutParams.getMarginEnd();
        }
        Point webViewSize = new Point();
        webViewSize.x = displaySize.x - margins;
        webViewSize.y = (int) ((double) webViewSize.x * MAP_ASPECT_RATIO);
        return webViewSize;
    }

    private void setWebViewHeight(Point webViewSize) {
        ViewGroup.LayoutParams lp = getLayoutParams();
        lp.height = webViewSize.y;
        setLayoutParams(lp);
    }

    @NonNull
    private Point getDisplaySize() {
        Point displaySize;
        displaySize = new Point();
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getSize(displaySize);
        return displaySize;
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

    private class MapData {
        private final int markerPinWidth, markerPinHeight, width, height;
        private final String countriesIso3Json;

        private MapData(int markerPinWidth, int markerPinHeight,
                        int width, int height, String countriesIso3Json) {
            this.markerPinWidth = markerPinWidth;
            this.markerPinHeight = markerPinHeight;
            this.countriesIso3Json = countriesIso3Json;
            this.width = width;
            this.height = height;
        }

        @JavascriptInterface
        public int getMarkerPinWidth() {
            return markerPinWidth;
        }

        @JavascriptInterface
        public int getMarkerPinHeight() {
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
            return countriesIso3Json;
        }
    }
}
