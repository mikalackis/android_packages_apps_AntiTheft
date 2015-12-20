
package com.android.antitheft.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.support.v7.widget.RecyclerView;
import com.android.antitheft.adapters.ControlAdapter;
import com.android.antitheft.commands.AntiTheftCommand;
import com.android.antitheft.commands.AntiTheftCommandUtil;
import android.support.v7.widget.LinearLayoutManager;

import com.android.antitheft.R;

public class ControlFragment extends Fragment {

    private static final String TAG = "RecyclerViewFragment";
    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    private static final int SPAN_COUNT = 2;
    private static final int DATASET_COUNT = 60;

    protected RecyclerView mRecyclerView;
    protected ControlAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected AntiTheftCommand[] mDataset;

    public static ControlFragment createFragment() {
        // final Bundle args = new Bundle();
        // args.putSerializable(HOTELS, hotels);
        // f.setArguments(args);
        return new ControlFragment();
    }

    public ControlFragment() {
        mDataset = AntiTheftCommandUtil.getAllCommands().values().toArray(new AntiTheftCommand[AntiTheftCommandUtil.getAllCommands().size()]);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.control_fragment,
                container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        // LinearLayoutManager is used here, this will layout the elements in a similar fashion
        // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
        // elements are laid out.
        mLayoutManager = new LinearLayoutManager(getActivity());
        
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new ControlAdapter(mDataset);

        // Set CustomAdapter as the adapter for RecyclerView.
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

}
