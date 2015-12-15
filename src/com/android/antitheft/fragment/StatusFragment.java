
package com.android.antitheft.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.antitheft.DeviceInfo;
import com.android.antitheft.ParseHelper;
import com.android.antitheft.R;
import com.android.antitheft.eventbus.StatusUpdateEvent;
import com.android.antitheft.listeners.ParseSaveCallback;
import com.android.antitheft.util.PrefUtils;
import com.parse.SaveCallback;
import com.parse.ParseObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StatusFragment extends Fragment {
    
    private SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
    
    private TextView tvLastUpdate;
    private TextView tvLastCommand;
    
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
        
        String lastUpdate = PrefUtils.getInstance().getStringPreference(
                PrefUtils.PARSE_LAST_UPDATE_TIME, "-1:unknown");
        String[] updateSplit = lastUpdate.split(":");
        
        setupStatusText(updateSplit[0],updateSplit[1]);

        Button btnReportStatus = (Button) view.findViewById(R.id.btn_report_status);
        btnReportStatus.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ParseHelper.initializeActivityParseObject("AntiTheft online",
                        DeviceInfo.getInstance().getIMEI())
                        .saveInBackground(new ParseSaveCallback("AntiTheft online"));
            }
        });
        return view;
    }
    
    private void setupStatusText(String time,String action){
        if (!time.equals("-1")) {
            Date resultdate = new Date(Long.parseLong(time));
            tvLastUpdate.setText(sdf.format(resultdate));
            tvLastCommand.setText(action);
        }
        else {
            tvLastUpdate.setText("UNKNOWN");
            tvLastUpdate.setTextColor(getResources().getColor(android.R.color.holo_red_light));
            tvLastCommand.setText("UNKNOWN");
            tvLastCommand.setTextColor(getResources().getColor(android.R.color.holo_red_light));
        }
    }
    
    public void updateStatusData(final StatusUpdateEvent event){
        setupStatusText(event.getTime()+"", event.getAction());
    }

}
