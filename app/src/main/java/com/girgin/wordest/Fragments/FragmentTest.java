package com.girgin.wordest.Fragments;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.girgin.wordest.App;
import com.girgin.wordest.R;
import com.girgin.wordest.SQLite.DatabaseAccess;
import com.girgin.wordest.SQLite.SQLite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

@SuppressLint("UseSwitchCompatOrMaterialCode")
public class FragmentTest extends Fragment {

    //**********************************************************************************************
    //MODELS

    Handler handler = new Handler();

    //**********************************************************************************************
    //LISTS

    ArrayList<AsyncTask> asyncTaskArrayList = new ArrayList<>();
    ArrayList<ArrayList<String>> firstWordsList = new ArrayList<>();
    ArrayList<ArrayList<String>> nextWordsList = new ArrayList<>();
    ArrayList<String> currentWordList = new ArrayList<>();
    ArrayList<String> nextWordList = new ArrayList<>();
    ArrayList<String> currentMeaningsList = new ArrayList<>();
    ArrayList<String> nextMeaningsList = new ArrayList<>();
    ArrayList<String> allMeaningsList = new ArrayList<>();
    ArrayList<ArrayList<String>> favoriteWordsList = new ArrayList<>();

    //**********************************************************************************************
    //VARIABLES

    String
            currentWord,
            nextWord,
            correctMeaning;
    int
            currentWordID,
            nextWordID,
            duration = 1000,
            difficulty = 90,
            unknown_chance = 70;
    boolean
            settingsIsVisible = false,
            wordIsFavorite;

    //**********************************************************************************************
    //COMPONENTS

    View
            view;
    TextView
            textView_word,
            textView_A,
            textView_B,
            textView_C,
            textView_D,
            textView_difficultyCount,
            textView_unknownChanceCount,
            textView_settings;
    LinearLayout
            linearLayout_A,
            linearLayout_B,
            linearLayout_C,
            linearLayout_D,
            linearLayout_options,
            linearLayout_settings,
            linearLayout_settingsButton;
    RelativeLayout
            relativeLayout_progressBar;
    ImageButton
            imageButton_settings,
            imageButton_settingsClose,
            imageButton_favorite;
    Switch
            switch_favorite,
            switch_sound;
    SeekBar
            seekBar_difficulty,
            seekBar_unknownChance;

    //**********************************************************************************************
    //MAIN

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        view = layoutInflater.inflate(R.layout.fragment_test, viewGroup, false);
        //***
        loadRelativeLayouts();
        //***
        asyncTaskArrayList.add(new getAllMeanings().execute());
        //***
        loadTextViews();
        //***
        loadLinearLayouts();
        //***
        loadImageButtons();
        //***
        loadSwitches();
        //***
        loadSeekBars();
        //***
        return view;
    }

    //**********************************************************************************************
    //BACKGROUND PROCESSES

    @SuppressLint("StaticFieldLeak")
    private class getAllMeanings extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            relativeLayout_progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getContext());
            databaseAccess.openDatabase();
            allMeaningsList = databaseAccess.getAllMeans();
            databaseAccess.closeDatabase();
            SQLite sqLite = new SQLite(getContext());
            firstWordsList = sqLite.getWords();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            load();
            linearLayout_options.setVisibility(View.VISIBLE);
            relativeLayout_progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class getNextWord extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected String doInBackground(String... strings) {
            ArrayList<Integer> percentageList;
            boolean IS_FAVORITE = Boolean.parseBoolean(strings[0]);
            int DIFFICULTY = Integer.parseInt(strings[1]);
            int UNKNOWN_CHANCE = Integer.parseInt(strings[2]);
            SQLite sqLite = new SQLite(getContext());
            percentageList = sqLite.getPercentage(IS_FAVORITE);
            if (percentageList.stream().allMatch(integer -> integer >= DIFFICULTY)) {
                nextWordsList = sqLite.getRandomWords(false, DIFFICULTY, IS_FAVORITE);
            } else if (percentageList.stream().allMatch(integer -> integer < DIFFICULTY)) {
                nextWordsList = sqLite.getRandomWords(true, DIFFICULTY, IS_FAVORITE);
            } else {
                nextWordsList = sqLite.getRandomWords(App.randomChance(UNKNOWN_CHANCE), DIFFICULTY, IS_FAVORITE);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            SQLite sqLite = new SQLite(getContext());
            nextWordList = randomWord(nextWordsList);
            nextWordID = Integer.parseInt(nextWordList.get(0));
            nextWord = nextWordList.get(1);
            nextMeaningsList = sqLite.getMeans(nextWordID);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class controlAccuracy extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            SQLite sqLite = new SQLite(getContext());
            if (strings[0].equals("true")) {
                sqLite.correctAnswer(Integer.parseInt(strings[1]));
            } else {
                sqLite.wrongAnswer(Integer.parseInt(strings[1]));
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    //**********************************************************************************************
    //LOAD COMPONENTS

    @SuppressLint("SetTextI18n")
    void loadTextViews() {
        textView_word = view.findViewById(R.id.fragmentTest_textView_word);
        textView_A = view.findViewById(R.id.fragmentTest_textView_A);
        textView_B = view.findViewById(R.id.fragmentTest_textView_B);
        textView_C = view.findViewById(R.id.fragmentTest_textView_C);
        textView_D = view.findViewById(R.id.fragmentTest_textView_D);
        textView_difficultyCount = view.findViewById(R.id.fragmentTest_textView_difficultyCount);
        textView_difficultyCount.setText("%" + difficulty);
        textView_unknownChanceCount = view.findViewById(R.id.fragmentTest_textView_unknownChanceCount);
        textView_unknownChanceCount.setText("%" + unknown_chance);
        textView_settings = view.findViewById(R.id.fragmentTest_textView_settings);
    }

    void loadLinearLayouts() {
        linearLayout_A = view.findViewById(R.id.fragmentTest_linearLayout_A);
        linearLayout_A.setOnClickListener(v -> {
            controlLinearLayouts(false);
            control(linearLayout_A, textView_A.getText().toString());
            handler.postDelayed(this::load, duration);
        });
        linearLayout_B = view.findViewById(R.id.fragmentTest_linearLayout_B);
        linearLayout_B.setOnClickListener(v -> {
            controlLinearLayouts(false);
            control(linearLayout_B, textView_B.getText().toString());
            handler.postDelayed(this::load, duration);
        });
        linearLayout_C = view.findViewById(R.id.fragmentTest_linearLayout_C);
        linearLayout_C.setOnClickListener(v -> {
            controlLinearLayouts(false);
            control(linearLayout_C, textView_C.getText().toString());
            handler.postDelayed(this::load, duration);
        });
        linearLayout_D = view.findViewById(R.id.fragmentTest_linearLayout_D);
        linearLayout_D.setOnClickListener(v -> {
            controlLinearLayouts(false);
            control(linearLayout_D, textView_D.getText().toString());
            handler.postDelayed(this::load, duration);
        });
        linearLayout_options = view.findViewById(R.id.fragmentTest_linearLayout_options);
        linearLayout_settings = view.findViewById(R.id.fragmentTest_linearLayout_settings);
        linearLayout_settingsButton = view.findViewById(R.id.fragmentTest_linearLayout_settingsButton);
        linearLayout_settingsButton.setOnClickListener(v -> controlSettings());
    }

    void loadImageButtons() {
        imageButton_settings = view.findViewById(R.id.fragmentTest_imageButton_settings);
        imageButton_settings.setOnClickListener(v -> controlSettings());
        imageButton_settingsClose = view.findViewById(R.id.fragmentTest_imageButton_settingsClose);
        imageButton_settingsClose.setOnClickListener(v -> controlSettings());
        imageButton_favorite = view.findViewById(R.id.fragmentTest_imageButton_favorite);
        imageButton_favorite.setOnClickListener(v -> {
            SQLite sqLite = new SQLite(getContext());
            if (wordIsFavorite) {
                sqLite.changeFavoriteState(currentWordID);
                imageButton_favorite.setImageResource(R.drawable.icon_favorite_filled_light);
                wordIsFavorite = false;
                asyncTaskArrayList.add(new getNextWord().execute(controlSwitchFavorite(), String.valueOf(difficulty), String.valueOf(unknown_chance)));
            } else {
                sqLite.changeFavoriteState(currentWordID);
                imageButton_favorite.setImageResource(R.drawable.icon_favorite_filled_borderless);
                wordIsFavorite = true;
                asyncTaskArrayList.add(new getNextWord().execute(controlSwitchFavorite(), String.valueOf(difficulty), String.valueOf(unknown_chance)));
            }
        });
    }

    void loadSwitches() {
        switch_favorite = view.findViewById(R.id.fragmentTest_switch_favorite);
        switch_favorite.setOnClickListener(v -> {
            if (switch_favorite.isChecked()) {
                SQLite sqLite = new SQLite(getContext());
                if (!hasFavoriteWord(sqLite.getWords())) {
                    switch_favorite.setChecked(false);
                    App.toastShowLong(getContext(), getString(R.string.Your_favorite_list_is_empty));
                }
            }
            asyncTaskArrayList.add(new getNextWord().execute(controlSwitchFavorite(), String.valueOf(difficulty), String.valueOf(unknown_chance)));
        });
        switch_sound = view.findViewById(R.id.fragmentTest_switch_sound);
    }

    void loadSeekBars() {
        seekBar_difficulty = view.findViewById(R.id.fragmentTest_seekBar_difficulty);
        seekBar_difficulty.setProgress(difficulty);
        seekBar_difficulty.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textView_difficultyCount.setText("%" + progress);
                difficulty = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBar_unknownChance = view.findViewById(R.id.fragmentTest_seekBar_unknownChance);
        seekBar_unknownChance.setProgress(unknown_chance);
        seekBar_unknownChance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textView_unknownChanceCount.setText("%" + progress);
                unknown_chance = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    void loadRelativeLayouts() {
        relativeLayout_progressBar = view.findViewById(R.id.fragmentTest_relativeLayout_progressBar);
    }

    //**********************************************************************************************
    //FUNCTIONS

    void controlSettings() {
        if (settingsIsVisible) {
            settingsIsVisible = false;
            imageButton_settings.setImageResource(R.drawable.icon_settings);
            textView_settings.setTextColor(getResources().getColor(R.color.logo_letters));
            linearLayout_settings.setVisibility(View.INVISIBLE);
            linearLayout_options.setVisibility(View.VISIBLE);
        } else {
            settingsIsVisible = true;
            imageButton_settings.setImageResource(R.drawable.icon_settings_active);
            textView_settings.setTextColor(getResources().getColor(R.color.logo_color));

            linearLayout_options.setVisibility(View.INVISIBLE);
            linearLayout_settings.setVisibility(View.VISIBLE);
        }
    }

    void controlLinearLayouts(boolean IS_ENABLED) {
        if (IS_ENABLED) {
            linearLayout_A.setEnabled(true);
            linearLayout_B.setEnabled(true);
            linearLayout_C.setEnabled(true);
            linearLayout_D.setEnabled(true);
        } else {
            linearLayout_A.setEnabled(false);
            linearLayout_B.setEnabled(false);
            linearLayout_C.setEnabled(false);
            linearLayout_D.setEnabled(false);
        }
    }

    String controlSwitchFavorite() {
        SQLite sqLite = new SQLite(getContext());
        if (switch_favorite.isChecked() && hasFavoriteWord(sqLite.getWords())) {
            return "true";
        } else {
            switch_favorite.setChecked(false);
            return "false";
        }
    }

    boolean hasFavoriteWord(ArrayList<ArrayList<String>> WORDS_LIST) {
        favoriteWordsList.clear();
        for (ArrayList<String> item : WORDS_LIST) {
            if (item.get(2).equals("1")) {
                favoriteWordsList.add(item);
            }
        }
        return favoriteWordsList.size() > 0;
    }

    ArrayList<String> randomWord(ArrayList<ArrayList<String>> LIST) {
        int randomNumber = new Random().nextInt(LIST.size());
        return LIST.get(randomNumber);
    }

    String randomMeaning(ArrayList<String> LIST) {
        int randomNumber = new Random().nextInt(LIST.size());
        return LIST.get(randomNumber);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    void load() {
        controlLinearLayouts(true);
        if (nextWordList.size() > 0) {
            currentWordList = nextWordList;
            currentWordID = Integer.parseInt(nextWordList.get(0));
            currentWord = nextWordList.get(1);
            currentMeaningsList = nextMeaningsList;
        } else {
            currentWordList = randomWord(firstWordsList);
            currentWordID = Integer.parseInt(currentWordList.get(0));
            currentWord = currentWordList.get(1);
            SQLite sqLite = new SQLite(getContext());
            currentMeaningsList = sqLite.getMeans(currentWordID);
        }
        if (switch_sound.isChecked()) {
            App.speechEnglish(getContext(), currentWord);
        }
        if (currentWordList.get(2).equals("1")) {
            imageButton_favorite.setImageResource(R.drawable.icon_favorite_filled_borderless);
            wordIsFavorite = true;
        } else {
            imageButton_favorite.setImageResource(R.drawable.icon_favorite_filled_light);
            wordIsFavorite = false;
        }
        correctMeaning = randomMeaning(currentMeaningsList);
        textView_word.setText(currentWordList.get(1));
        ArrayList<String> meaningOptions = new ArrayList<>();
        meaningOptions.add(correctMeaning);
        while (meaningOptions.size() != 4) {
            String randomMeaning = randomMeaning(allMeaningsList);
            if (!currentMeaningsList.contains(randomMeaning) && !meaningOptions.contains(randomMeaning)) {
                meaningOptions.add(randomMeaning);
            }
        }
        linearLayout_A.setBackground(getResources().getDrawable(R.drawable.background_word_info));
        linearLayout_B.setBackground(getResources().getDrawable(R.drawable.background_word_info));
        linearLayout_C.setBackground(getResources().getDrawable(R.drawable.background_word_info));
        linearLayout_D.setBackground(getResources().getDrawable(R.drawable.background_word_info));
        Collections.shuffle(meaningOptions, new Random());
        textView_A.setText(meaningOptions.get(0));
        textView_B.setText(meaningOptions.get(1));
        textView_C.setText(meaningOptions.get(2));
        textView_D.setText(meaningOptions.get(3));
        asyncTaskArrayList.add(new getNextWord().execute(controlSwitchFavorite(), String.valueOf(difficulty), String.valueOf(unknown_chance)));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    void control(LinearLayout LINEARLAYOUT, String SELECTED_MEANING) {
        if (SELECTED_MEANING.equals(correctMeaning)) {
            asyncTaskArrayList.add(new controlAccuracy().execute("true", String.valueOf(currentWordID)));
            LINEARLAYOUT.setBackground(getResources().getDrawable(R.drawable.background_linearlayout_true));
        } else {
            asyncTaskArrayList.add(new controlAccuracy().execute("false", String.valueOf(currentWordID)));
            LINEARLAYOUT.setBackground(getResources().getDrawable(R.drawable.background_linearlayout_false));
            if (textView_A.getText().toString().equals(correctMeaning)) {
                linearLayout_A.setBackground(getResources().getDrawable(R.drawable.background_linearlayout_true));
            } else if (textView_B.getText().toString().equals(correctMeaning)) {
                linearLayout_B.setBackground(getResources().getDrawable(R.drawable.background_linearlayout_true));
            } else if (textView_C.getText().toString().equals(correctMeaning)) {
                linearLayout_C.setBackground(getResources().getDrawable(R.drawable.background_linearlayout_true));
            } else if (textView_D.getText().toString().equals(correctMeaning)) {
                linearLayout_D.setBackground(getResources().getDrawable(R.drawable.background_linearlayout_true));
            }
        }
    }
}
