package com.example.android.popularmoviesstage2.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmoviesstage2.R;
import com.example.android.popularmoviesstage2.TabsParent;

public class overviewFragment extends Fragment {

    TabsParent parentActivity;
    TextView txtOvw;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View myView = inflater.inflate(R.layout.fragment_overview, container, false);
        txtOvw = (TextView)myView.findViewById(R.id.tvOverview);
        parentActivity = ((TabsParent) getActivity());
        txtOvw.setText(parentActivity.getMovieOverview());

        // Inflate the layout for this fragment
        return myView;
    }
}
