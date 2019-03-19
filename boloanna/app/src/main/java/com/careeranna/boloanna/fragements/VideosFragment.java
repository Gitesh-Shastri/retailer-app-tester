package com.careeranna.boloanna.fragements;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.careeranna.boloanna.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class VideosFragement extends Fragment {


    public VideosFragement() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_videos_fragement, container, false);
    }

}
