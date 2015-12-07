package com.android.antitheft.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.antitheft.R;

public class ControlFragment extends Fragment{
    
    public static ControlFragment createFragment() {
        // final Bundle args = new Bundle();
        // args.putSerializable(HOTELS, hotels);
        // f.setArguments(args);
        return new ControlFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.status_fragment,
                container, false);
        TextView tv = (TextView)view.findViewById(R.id.textStatus);
        tv.setText("ALL SYSTEMS HIIIIIII");
        return view;
    }


}
