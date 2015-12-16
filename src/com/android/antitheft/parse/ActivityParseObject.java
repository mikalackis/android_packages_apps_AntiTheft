package com.android.antitheft.parse;

import com.parse.ParseObject;
import com.parse.ParseClassName;

@ParseClassName("ActivityParseObject")
public class ActivityParseObject extends ParseObject {
    
    private String action;
    private String imei;
    
    public ActivityParseObject(){
        super();
    }

    public String getAction() {
        return getString("action");
    }

    public void setAction(String action) {
        put("action",action);
    }

    public String getImei() {
        return getString("imei");
    }

    public void setImei(String imei) {
        put("imei",imei);
    }
    
}
