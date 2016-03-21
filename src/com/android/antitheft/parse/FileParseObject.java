
package com.android.antitheft.parse;

import com.parse.ParseObject;
import com.parse.ParseClassName;
import com.parse.ParseFile;

@ParseClassName("FileParseObject")
public class FileParseObject extends ParseObject {

    public FileParseObject() {
        super();
    }

    public void setParseFile(byte[] data, String fileName) {
        ParseFile file = new ParseFile(fileName, data);
        put("file", file);
    }

    public String getImei() {
        return getString("imei");
    }

    public void setImei(String imei) {
        put("imei", imei);
    }

}
