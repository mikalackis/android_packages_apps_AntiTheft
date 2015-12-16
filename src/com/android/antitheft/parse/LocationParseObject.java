
package com.android.antitheft.parse;

import com.parse.ParseObject;
import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;

@ParseClassName("LocationParseObject")
public class LocationParseObject extends ParseObject {

    public LocationParseObject() {
        super();
    }

    public double getLatitude() {
        return getDouble("latitude");
    }

    public void setLatitudeLongitude(double latitude, double longitude) {
        ParseGeoPoint point = new ParseGeoPoint(latitude, longitude);
        put("location", point);
        put("gmaps_url", String.format(ParseHelper.GOOGLE_MAPS_URL, latitude, longitude));
    }

    public double getLongitude() {
        return getDouble("longitude");
    }

    public String getImei() {
        return getString("imei");
    }

    public void setImei(String imei) {
        put("imei", imei);
    }

    public String getGoogleMapsUrl() {
        return getString("gmaps_url");
    }

}
