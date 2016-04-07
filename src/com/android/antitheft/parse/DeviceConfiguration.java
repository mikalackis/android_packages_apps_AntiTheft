
package com.android.antitheft.parse;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.util.Date;

@ParseClassName("DeviceConfiguration")
public class DeviceConfiguration extends ParseObject {

    public DeviceConfiguration() {
        super();
    }

    public Date getLeaseStartDate() {
        return getDate("leaseStartDate");
    }

    public Date getLeaseEndDate() {
        return getDate("leaseEndDate");
    }

    public boolean isMasterDevice() {
        return getBoolean("masterDevice");
    }

    public int getArielSystemStatus() {
        return getInt("arielSystemStatus");
    }

    public String getClientId() {
        return getString("clientId");
    }

    public boolean isActive() {
        return getBoolean("isActive");
    }

    public String getPhoneNumber() { return getString("phoneNumber"); }
}
