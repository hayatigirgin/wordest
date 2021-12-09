package com.girgin.wordest.Fragments;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.girgin.wordest.Activities.ActivityMain;
import com.girgin.wordest.App;
import com.girgin.wordest.R;

public class FragmentPlaySelector extends Fragment {

    View
            view;
    Button
            button_exercise,
            button_test;

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle){
        view = layoutInflater.inflate(R.layout.fragment_play_selector, viewGroup, false);
        //***
        loadButtons();
        //***
        return view;
    }

    void loadButtons(){
        button_exercise = view.findViewById(R.id.fragmentPlaySelector_exercise);
        button_exercise.setOnClickListener(v -> {
            ((ActivityMain) requireContext()).removeFragmentPlaySelector();
            ((ActivityMain) requireContext()).removeFragmentExercise();
            ((ActivityMain) requireContext()).loadFragmentExercise();
        });
        button_test = view.findViewById(R.id.fragmentPlaySelector_test);
        button_test.setOnClickListener(v -> {
            ((ActivityMain) requireContext()).removeFragmentPlaySelector();
            ((ActivityMain) requireContext()).removeFragmentTest();
            ((ActivityMain) requireContext()).loadFragmentTest();
        });
    }
}
