package com.girgin.wordest.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.girgin.wordest.Activities.ActivityMain;
import com.girgin.wordest.R;
import com.girgin.wordest.SQLite.SQLite;

import java.util.ArrayList;

public class FragmentActivityMainWordInfo extends Fragment {

    int WORD_ID;
    String WORD;

    //*** LISTS
    ArrayList<AsyncTask> asyncTaskArrayList = new ArrayList<>();
    ArrayList<String> means_list = new ArrayList<>();
    ArrayList<String> descriptions_list = new ArrayList<>();
    ArrayList<String> sentences_list = new ArrayList<>();
    ArrayList<Integer> countsList = new ArrayList<>();

    View
            VIEW;
    ImageButton
            IMAGE_BUTTON_CLOSE,
            imageButton_delete;
    Button
            BUTTON_MEANS,
            BUTTON_DESCRIPTIONS,
            BUTTON_INFO;
    ListView
            LISTVIEW;
    TextView
            TEXTVIEW_WORD,
            textView_totalViewsCount,
            textView_correctAnswersCount,
            textView_percent;
    ConstraintLayout
            constraintLayout_info;
    ProgressBar
            progressBar_percent;

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        VIEW = layoutInflater.inflate(R.layout.fragment_activity_main_word_info, viewGroup, false);
        //***
        asyncTaskArrayList.add(new loadInformation().execute());
        asyncTaskArrayList.add(new loadCounts().execute());
        //***
        loadImageButtons();
        //***
        loadTextViews();
        //***
        loadButtons();
        //***
        loadListViews();
        //***
        loadProgressBar();
        //***
        loadConstraintLayouts();
        //***
        return VIEW;
    }

    //**********************************************************************************************
    //BACKGROUND PROCESSES

    @SuppressLint("StaticFieldLeak")
    private class loadInformation extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            getWordID();
        }

        @Override
        protected String doInBackground(String... strings) {
            SQLite sqLite = new SQLite(getContext());
            means_list = sqLite.getMeans(WORD_ID);
            descriptions_list = sqLite.getDescriptions(WORD_ID);
            sentences_list = sqLite.getSentences(WORD_ID);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            setArrayAdapter(means_list);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class loadCounts extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            SQLite sqLite = new SQLite(getContext());
            countsList = sqLite.getCountsThisWord(WORD_ID);
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
            } else {
                progressBar_percent.setVisibility(View.INVISIBLE);
            }
        }
    }

    //**********************************************************************************************
    //FUNCTIONS

    void getWordID() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            WORD_ID = bundle.getInt("WORD_ID", 0);
            WORD = bundle.getString("WORD", "");
        }
    }

    void setArrayAdapter(ArrayList<String> LIST){
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), R.layout.item_listview, R.id.item_listView_textView, LIST);
        LISTVIEW.setAdapter(arrayAdapter);
        LISTVIEW.refreshDrawableState();
    }

    void showWordInfoDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme);
        View view = LayoutInflater.from(getContext()).inflate(
                R.layout.dialog_word_add,
                VIEW.findViewById(R.id.dialog_wordAdd_constraintLayout));
        builder.setView(view);
        ((TextView)view.findViewById(R.id.dialog_wordAdd_textView_title)).setText(getString(R.string.DELETE_THIS_WORD));
        ((TextView)view.findViewById(R.id.dialog_wordAdd_textView_word)).setText(WORD.toUpperCase().replace("İ", "I"));
        AlertDialog alertDialog = builder.create();
        view.findViewById(R.id.dialog_wordAdd_button_cancel).setOnClickListener(v -> alertDialog.dismiss());
        view.findViewById(R.id.dialog_wordAdd_button_ok).setOnClickListener(v -> {
            SQLite sqLite = new SQLite(getContext());
            sqLite.deleteWord(WORD_ID);
            ((ActivityMain) requireContext()).refreshRecyclerView();
            ((ActivityMain) requireContext()).removeFragmentWordInfo();
            alertDialog.dismiss();
        });
        if (alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        alertDialog.show();
    }

    //**********************************************************************************************
    //LOAD COMPONENTS

    void loadImageButtons() {
        IMAGE_BUTTON_CLOSE = VIEW.findViewById(R.id.fragment_activityMain_word_info_close);
        IMAGE_BUTTON_CLOSE.setOnClickListener(v -> ((ActivityMain) requireContext()).removeFragmentWordInfo());
        imageButton_delete = VIEW.findViewById(R.id.fragment_activityMain_word_info_delete);
        imageButton_delete.setOnClickListener(v -> showWordInfoDialog());
    }

    void loadTextViews(){
        TEXTVIEW_WORD = VIEW.findViewById(R.id.fragment_activityMain_wordInfo_textView_word);
        TEXTVIEW_WORD.setText(WORD.toUpperCase().replace("İ", "I"));
        textView_totalViewsCount = VIEW.findViewById(R.id.fragmentWordInfo_textView_totalViewsCount);
        textView_correctAnswersCount = VIEW.findViewById(R.id.fragmentWordInfo_textView_correctAnswersCount);
        textView_percent = VIEW.findViewById(R.id.fragmentWordInfo_textView_percent);
    }

    void loadConstraintLayouts(){
        constraintLayout_info = VIEW.findViewById(R.id.fragment_activityMain_wordInfo_constraintLayout);
    }

    void loadProgressBar() {
        progressBar_percent = VIEW.findViewById(R.id.fragmentWordInfo_progressBar_percent);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    void loadButtons(){
        BUTTON_MEANS = VIEW.findViewById(R.id.fragment_activityMain_wordInfo_button_means);
        BUTTON_MEANS.setOnClickListener(v -> {
            constraintLayout_info.setVisibility(View.INVISIBLE);
            LISTVIEW.setVisibility(View.VISIBLE);
            BUTTON_MEANS.setBackground(getResources().getDrawable(R.drawable.background_button_word_info));
            BUTTON_DESCRIPTIONS.setBackgroundColor(Color.TRANSPARENT);
            BUTTON_INFO.setBackgroundColor(Color.TRANSPARENT);
            setArrayAdapter(means_list);
        });
        BUTTON_DESCRIPTIONS = VIEW.findViewById(R.id.fragment_activityMain_wordInfo_button_descriptions);
        BUTTON_DESCRIPTIONS.setOnClickListener(v -> {
            constraintLayout_info.setVisibility(View.INVISIBLE);
            LISTVIEW.setVisibility(View.VISIBLE);
            BUTTON_DESCRIPTIONS.setBackground(getResources().getDrawable(R.drawable.background_button_word_info));
            BUTTON_MEANS.setBackgroundColor(Color.TRANSPARENT);
            BUTTON_INFO.setBackgroundColor(Color.TRANSPARENT);
            setArrayAdapter(descriptions_list);
        });
        BUTTON_INFO = VIEW.findViewById(R.id.fragment_activityMain_wordInfo_button_info);
        BUTTON_INFO.setOnClickListener(v -> {
            constraintLayout_info.setVisibility(View.VISIBLE);
            LISTVIEW.setVisibility(View.INVISIBLE);
            BUTTON_INFO.setBackground(getResources().getDrawable(R.drawable.background_button_word_info));
            BUTTON_DESCRIPTIONS.setBackgroundColor(Color.TRANSPARENT);
            BUTTON_MEANS.setBackgroundColor(Color.TRANSPARENT);
            setArrayAdapter(sentences_list);
        });
    }

    void loadListViews(){
        LISTVIEW = VIEW.findViewById(R.id.fragment_activityMain_wordInfo_listView);
    }
}
