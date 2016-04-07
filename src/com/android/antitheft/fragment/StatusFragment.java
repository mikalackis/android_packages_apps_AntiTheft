
package com.android.antitheft.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentResolver;
import android.os.Bundle;
import android.provider.ArielSettings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.antitheft.AntiTheftApplication;
import com.android.antitheft.DeviceInfo;
import com.android.antitheft.R;
import com.android.antitheft.eventbus.StatusUpdateEvent;
import com.android.antitheft.listeners.ParseSaveCallback;
import com.android.antitheft.parse.ActivityParseObject;
import com.android.antitheft.parse.ParseHelper;
import com.android.antitheft.util.PrefUtils;
import com.parse.SaveCallback;
import com.parse.ParseObject;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StatusFragment extends Fragment {

    private SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");

    private TextView tvLastUpdate;
    private TextView tvLastCommand;
    private TextView tvDeviceId;
    private TextView tvPhoneNumber;

    public static StatusFragment createFragment() {
        // final Bundle args = new Bundle();
        // args.putSerializable(HOTELS, hotels);
        // f.setArguments(args);
        return new StatusFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.status_fragment,
                container, false);
        tvLastUpdate = (TextView) view.findViewById(R.id.txt_last_update);
        tvLastCommand = (TextView) view.findViewById(R.id.txt_last_command);
        tvDeviceId = (TextView) view.findViewById(R.id.txt_device_id);
        tvPhoneNumber = (TextView) view.findViewById(R.id.txt_phone_number);

        tvDeviceId.setText(DeviceInfo.getInstance().getUniquePsuedoID());

        ContentResolver resolver = AntiTheftApplication.getInstance().getContentResolver();
        tvPhoneNumber.setText(ArielSettings.Secure.getString(resolver, ArielSettings.Secure.ARIEL_PHONE_NUMBER));

        Gson gson = new Gson();
        String eventJson = PrefUtils.getInstance().getStringPreference(
                PrefUtils.PARSE_LAST_UPDATE_EVENT, null);
        if (eventJson != null) {
            StatusUpdateEvent event = gson.fromJson(eventJson, StatusUpdateEvent.class);
            setupStatusText(event);
        }
        
        Button btnReportStatus = (Button) view.findViewById(R.id.btn_report_status);
        btnReportStatus.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ActivityParseObject activityObject = new ActivityParseObject();
                activityObject.setAction("AntiTheft online");
                activityObject.setImei(DeviceInfo.getInstance().getIMEI());
                activityObject.saveEventually(new ParseSaveCallback("AntiTheft online"));
            }
        });
        return view;
    }

    private void setupStatusText(StatusUpdateEvent event) {
        if (event.getTime() != -1) {
            Date resultdate = new Date(event.getTime());
            tvLastUpdate.setText(sdf.format(resultdate));
            tvLastCommand.setText(event.getAction());
        }
        else {
            tvLastUpdate.setText("UNKNOWN");
            tvLastUpdate.setTextColor(getResources().getColor(android.R.color.holo_red_light));
            tvLastCommand.setText("UNKNOWN");
            tvLastCommand.setTextColor(getResources().getColor(android.R.color.holo_red_light));
        }
    }

    public void updateStatusData(final StatusUpdateEvent event) {
        setupStatusText(event);
    }

}
