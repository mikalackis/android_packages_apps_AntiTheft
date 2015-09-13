
package com.android.antitheft;

import com.parse.ParseObject;
import com.parse.ParseGeoPoint;
import com.parse.ParseFile;

public class ParseHelper {

    public static ParseObject initializeActivityParseObject(final String status,
            final String imei) {
        ParseObject parseObject = new ParseObject("ActivityMonitor");
        parseObject.put("status", status != null ? status : "");
        parseObject.put("imei", imei != null ? imei : "");
        return parseObject;
    }

    public static ParseObject initializeLocationParseObject(final String imei,
            final double lat, final double lon) {
        String googleMapsURL = "http://maps.google.com/?q=%1$f,%2$f";
        ParseGeoPoint point = new ParseGeoPoint(lat, lon);
        ParseObject parseObject = new ParseObject("LocationMonitor");
        parseObject.put("imei", imei != null ? imei : "");
        parseObject.put("location", point);
        parseObject.put("gmaps_url", String.format(googleMapsURL, lat, lon));
        return parseObject;
    }

    public static ParseObject initializeFileParseObject(final String imei,
            final byte[] data, final String fileName) {
        ParseFile file = new ParseFile(fileName, data);
        ParseObject parseObject = new ParseObject("FileMonitor");
        parseObject.put("whos_there", file);
        parseObject.put("imei", imei != null ? imei : "");
        return parseObject;
    }

}
