package com.girgin.wordest.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.girgin.wordest.Activities.ActivityMain;
import com.girgin.wordest.BuildConfig;
import com.girgin.wordest.R;

public class FragmentStart extends Fragment {

    View
            VIEW;
    Button
            BUTTON_START;
    TextView
            TEXTVIEW_VERSION;

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle){
        VIEW = layoutInflater.inflate(R.layout.fragment_start, viewGroup, false);
        //***
        loadButtons();
        //***
        loadTextViews();
        //***
        return VIEW;
    }

    void loadButtons(){
        BUTTON_START = VIEW.findViewById(R.id.fragment_start_button_start);
        BUTTON_START.setOnClickListener(view -> ((ActivityMain) requireContext()).removeFragmentStart());
    }

    @SuppressLint("SetTextI18n")
    void loadTextViews(){
        TEXTVIEW_VERSION = VIEW.findViewById(R.id.fragment_start_textview_version);
        TEXTVIEW_VERSION.setText(getString(R.string.Version) + " " + BuildConfig.VERSION_NAME);
    }
}
