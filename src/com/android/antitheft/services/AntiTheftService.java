
package com.android.antitheft.services;

import android.app.Service;
import android.content.Context;

public abstract class AntiTheftService extends Service{

    public abstract void startAntiTheftService(final Context context, final int state);

}
