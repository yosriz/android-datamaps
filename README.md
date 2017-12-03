## android-data-maps

Customizable map visualization for Android, a wrapper over JavaScript [DataMaps](https://github.com/markmarkoh/datamaps) library.

Supporting custom marker, custom color for each country.

![Screenshot_1512334958.png]({{site.baseurl}}/images/Screenshot_1512334958.png)
![Screenshot_1512334708.png]({{site.baseurl}}/images/Screenshot_1512334708.png)

**Dependency**

Add this in your root build.gradle file (not your module build.gradle file):

```
allprojects {
    repositories {
	        	maven { url "https://jitpack.io" }
	    }
}
````


Add this to your module's build.gradle file:


```dependencies {
    compile 'com.github.yosriz:android-datamaps:{latest-version}'	
}
````

**Usage**

Add `DataMapsView` to your layout xml, custom properties available:

```
 <com.yosriz.datamaps.android.DataMapsView
        android:id="@+id/map"
        android:layout_width="match_parent"
        android:layout_height="300dp"
        datamaps:default_fill_color="#bdbdbd"
        datamaps:marker_height="35dp"
        datamaps:marker_icon="pin.svg"
        datamaps:marker_width="35dp"
        datamaps:projection="mercator" />
```

Populate `CountryData`  with the desired countries data:

```
 List<CountryData> countryDataList = new ArrayList<>();
 countryDataList.add(new CountryData.Builder()
            .color(Color.parseColor("#bdbdbd"))
            .country("USA")
            .marker(true)
            .build());
```

load populated countries data to`DataMapsView`:
```
DataMapsData mapData = new DataMapsData(countryDataList);
dataMapsView.loadData(mapData);
```

see sample app for full example.


