package com.girgin.wordest.Fragments;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.girgin.wordest.Activities.ActivityMain;
import com.girgin.wordest.BuildConfig;
import com.girgin.wordest.R;
import com.girgin.wordest.SQLite.SQLite;

import java.util.ArrayList;

public class FragmentProfile extends Fragment {

    ArrayList<AsyncTask> asyncTaskArrayList = new ArrayList<>();
    ArrayList<Integer> countsList = new ArrayList<>();

    //***

    View
            view;
    TextView
            textView_totalViewsCount,
            textView_correctAnswersCount,
            textView_percent;
    ProgressBar
            progressBar_percent;

    //***

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle){
        view = layoutInflater.inflate(R.layout.fragment_profile, viewGroup, false);
        //***
        loadTextViews();
        //***
        loadProgressBar();
        //***
        asyncTaskArrayList.add(new loadCounts().execute());
        //***
        return view;
    }

    //**********************************************************************************************
    //BACKGROUND PROCESSES

    @SuppressLint("StaticFieldLeak")
    private class loadCounts extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            SQLite sqLite = new SQLite(getContext());
            countsList = sqLite.getCounts();
            return null;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            int totalViews = countsList.get(0);
            int correctAnswers = countsList.get(1);
            int percent = (int) Math.round(((double) correctAnswers / (double) totalViews) * 100);
            textView_totalViewsCount.setText(String.valueOf(totalViews));
            textView_correctAnswersCount.setText(String.valueOf(correctAnswers));
            textView_percent.setText("%" + percent);
            if (percent > 0){
                progressBar_percent.setProgress(percent);
            }
            else {
                progressBar_percent.setVisibility(View.INVISIBLE);
            }
        }
    }

    //**********************************************************************************************
    //LOAD COMPONENTS

    void loadProgressBar() {
        progressBar_percent = view.findViewById(R.id.fragmentProfile_progressBar_percent);
    }

    void loadTextViews() {
        textView_totalViewsCount = view.findViewById(R.id.fragmentProfile_textView_totalViewsCount);
        textView_correctAnswersCount = view.findViewById(R.id.fragmentProfile_textView_correctAnswersCount);
        textView_percent = view.findViewById(R.id.fragmentProfile_textView_percent);
    }
}
