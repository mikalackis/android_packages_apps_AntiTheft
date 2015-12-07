
package com.android.antitheft.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.antitheft.R;

public class StatusFragment extends Fragment {
    
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
        TextView tv = (TextView)view.findViewById(R.id.textStatus);
        tv.setText("ALL SYSTEMS OPERATIONAL");
        return view;
    }

}
