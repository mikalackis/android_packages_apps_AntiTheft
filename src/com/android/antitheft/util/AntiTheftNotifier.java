/*
 * Copyright (C) 2014 The CyanogenMod Project
 *
 * * Licensed under the GNU GPLv2 license
 *
 * The text of the license can be found in the LICENSE file
 * or at https://www.gnu.org/licenses/gpl-2.0.txt
 */

package com.android.antitheft.util;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.android.antitheft.R;
import com.android.antitheft.activities.SettingsActivity;

import java.io.File;

public class AntiTheftNotifier {

    private AntiTheftNotifier() {
        // Don't instantiate me bro
    }

    public static void notifyAntiTheftState(Context context) {

        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle()
                .setBigContentTitle(context.getString(R.string.notification_big_title))
                .bigText(context.getString(R.string.notification_big_content));

        NotificationCompat.Builder builder = createBaseContentBuilder(context)
                .setSmallIcon(android.R.drawable.arrow_up_float)
                .setContentTitle(context.getString(R.string.notification_title))
                .setContentText(context.getString(R.string.notification_content))
                .setTicker("TICKER")
                .setStyle(style)
                .addAction(android.R.drawable.btn_star,
                        "UPDATE_INSTALL",
                        createInstallPendingIntent(context));

        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE))
                .notify(R.string.app_label, builder.build());
    }

    // public static void notifyDownloadError(Context context,
    // Intent updateIntent, int failureMessageResId) {
    // NotificationCompat.Builder builder = createBaseContentBuilder(context, updateIntent)
    // .setSmallIcon(android.R.drawable.stat_notify_error)
    // .setContentTitle(context.getString(R.string.not_download_failure))
    // .setContentText(context.getString(failureMessageResId))
    // .setTicker(context.getString(R.string.not_download_failure));
    //
    // ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE))
    // .notify(R.string.not_download_success, builder.build());
    // }

    private static NotificationCompat.Builder createBaseContentBuilder(Context context) {
        Intent installIntent = new Intent(context, SettingsActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 1,
                installIntent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_UPDATE_CURRENT);

        return new NotificationCompat.Builder(context)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(contentIntent)
                .setLocalOnly(true)
                .setAutoCancel(true);
    }

    private static PendingIntent createInstallPendingIntent(Context context) {
        Intent installIntent = new Intent(context, SettingsActivity.class);

        return PendingIntent.getActivity(
                context,
                0,
                installIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
                );
    }
}
