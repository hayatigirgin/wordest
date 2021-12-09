package com.girgin.wordest.Fragments;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.girgin.wordest.Activities.ActivityMain;
import com.girgin.wordest.App;
import com.girgin.wordest.R;
import com.girgin.wordest.SQLite.SQLite;

import java.util.ArrayList;
import java.util.Random;

@SuppressLint("UseSwitchCompatOrMaterialCode")
public class FragmentExercise extends Fragment {

    //**********************************************************************************************
    //MODELS

    final Handler handler = new Handler(Looper.getMainLooper());

    //**********************************************************************************************
    //LISTS

    ArrayList<AsyncTask> asyncTaskArrayList = new ArrayList<>();
    ArrayList<ArrayList<String>> favoriteWordsList = new ArrayList<>();
    ArrayList<ArrayList<String>> firstWordsList = new ArrayList<>();
    ArrayList<ArrayList<String>> nextWordsList = new ArrayList<>();
    ArrayList<String> currentWordList = new ArrayList<>();
    ArrayList<String> nextWordList = new ArrayList<>();
    ArrayList<String> currentMeaningsList = new ArrayList<>();
    ArrayList<String> nextMeaningsList = new ArrayList<>();
    ArrayList<String> correctWordsList = new ArrayList<>();

    //**********************************************************************************************
    //VARIABLES

    String
            randomEnglishWord,
            randomTurkishWord,
            nextWord;
    int
            currentWordID,
            nextWordID,
            difficulty = 90,
            unknown_chance = 70,
            means_chance = 50,
            controlIconDuration = 1000;
    boolean
            isEnglish,
            wordIsFavorite,
            settingsIsVisible = false,
            helpIsVisible = false;

    //**********************************************************************************************
    //COMPONENTS

    View
            view;
    TextView
            textView_word,
            textView_currentAnswerLanguage,
            textView_difficultyCount,
            textView_unknownChanceCount,
            textView_meansChanceCount,
            textView_hint,
            textView_settings;
    EditText
            editText_inputWord;
    ImageButton
            imageButton_favorite,
            imageButton_hint,
            imageButton_settings,
            imageButton_settingsClose;
    ImageView
            imageView_control;
    ListView
            listView_help;
    LinearLayout
            linearLayout_settings,
            linearLayout_settingsButton,
            linearLayout_hintButton;
    RelativeLayout
            relativeLayout_progressBar;
    Switch
            switch_favorite,
            switch_sound;
    SeekBar
            seekBar_difficulty,
            seekBar_unknownChance,
            seekBar_meansChance;

    //**********************************************************************************************
    //MAIN

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        view = layoutInflater.inflate(R.layout.fragment_exercise, viewGroup, false);
        //***
        loadTextViews();
        //***
        loadEditTexts();
        //***
        loadImageButtons();
        //***
        loadImageViews();
        //***
        loadListViews();
        //***
        loadLinearLayouts();
        //***
        loadRelativeLayouts();
        //***
        loadSwitches();
        //***
        loadSeekBars();
        //***
        asyncTaskArrayList.add(new loadWords().execute());
        //***
        asyncTaskArrayList.add(new getNextWord().execute(controlSwitchFavorite(), String.valueOf(difficulty), String.valueOf(unknown_chance)));
        //***
        return view;
    }

    //**********************************************************************************************
    //BACKGROUND PROCESSES

    @SuppressLint("StaticFieldLeak")
    private class loadWords extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            relativeLayout_progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            SQLite sqLite = new SQLite(getContext());
            firstWordsList = sqLite.getWords();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            load();
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
    private class loadCorrectWords extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            SQLite sqLite = new SQLite(getContext());
            correctWordsList = sqLite.getCorrectWords(strings[0]);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
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
        textView_word = view.findViewById(R.id.fragmentPlay_textView_word);
        textView_currentAnswerLanguage = view.findViewById(R.id.fragmentPlay_textView_currentAnswerLanguage);
        textView_difficultyCount = view.findViewById(R.id.fragmentExercise_textView_difficultyCount);
        textView_difficultyCount.setText("%" + difficulty);
        textView_unknownChanceCount = view.findViewById(R.id.fragmentExercise_textView_unknownChanceCount);
        textView_unknownChanceCount.setText("%" + unknown_chance);
        textView_meansChanceCount = view.findViewById(R.id.fragmentExercise_textView_meansChanceCount);
        textView_meansChanceCount.setText("%" + means_chance);
        textView_hint = view.findViewById(R.id.fragmentExercise_textView_hint);
        textView_settings = view.findViewById(R.id.fragmentExercise_textView_settings);
    }

    void loadEditTexts() {
        editText_inputWord = view.findViewById(R.id.fragmentPlay_editText_input);
        editText_inputWord.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                control();
                listView_help.setVisibility(View.INVISIBLE);
                helpIsVisible = false;
                linearLayout_settings.setVisibility(View.INVISIBLE);
                settingsIsVisible = false;
                editText_inputWord.requestFocus();
//                imageButton_hint.setImageResource(R.drawable.icon_hint);
                return true;
            }
            return false;
        });
    }

    @SuppressLint("ResourceAsColor")
    void loadImageButtons() {
        imageButton_favorite = view.findViewById(R.id.fragmentPlay_imageButton_favorite);
        imageButton_favorite.setOnClickListener(v -> {
            SQLite sqLite = new SQLite(getContext());
            if (wordIsFavorite) {
                sqLite.changeFavoriteState(currentWordID);
                imageButton_favorite.setImageResource(R.drawable.icon_favorite_empty_light);
                wordIsFavorite = false;
                asyncTaskArrayList.add(new getNextWord().execute(controlSwitchFavorite(), String.valueOf(difficulty), String.valueOf(unknown_chance)));
            } else {
                sqLite.changeFavoriteState(currentWordID);
                imageButton_favorite.setImageResource(R.drawable.icon_favorite_filled);
                wordIsFavorite = true;
                asyncTaskArrayList.add(new getNextWord().execute(controlSwitchFavorite(), String.valueOf(difficulty), String.valueOf(unknown_chance)));
            }
        });
        imageButton_hint = view.findViewById(R.id.fragmentPlay_imageButton_hint);
        imageButton_hint.setOnClickListener(v -> {
            imageButton_hint.setImageResource(R.drawable.icon_hint);
            textView_hint.setTextColor(getResources().getColor(R.color.logo_letters));
            linearLayout_settings.setVisibility(View.INVISIBLE);
            if (settingsIsVisible) {
                controlSettings();
            }
            if (helpIsVisible) {
                listView_help.setVisibility(View.INVISIBLE);
                helpIsVisible = false;
            } else {
                SQLite sqLite = new SQLite(getContext());
                sqLite.wrongAnswer(currentWordID);
                App.closeKeyboard((ActivityMain) requireContext());
                linearLayout_settings.setVisibility(View.INVISIBLE);
                listView_help.setVisibility(View.VISIBLE);
                if (isEnglish) {
                    setArrayAdapter(currentMeaningsList);
                } else {
                    setArrayAdapter(correctWordsList);
                }
                helpIsVisible = true;
            }
        });
        imageButton_settings = view.findViewById(R.id.fragmentExercise_imageButton_settings);
        imageButton_settings.setOnClickListener(v -> {
            listView_help.setVisibility(View.INVISIBLE);
            helpIsVisible = false;
            controlSettings();
        });
        imageButton_settingsClose = view.findViewById(R.id.fragmentExercise_imageButton_settingsClose);
        imageButton_settingsClose.setOnClickListener(v -> controlSettings());
    }

    void loadImageViews() {
        imageView_control = view.findViewById(R.id.fragmentExercise_imageView_controlIcon);
    }

    void loadListViews() {
        listView_help = view.findViewById(R.id.fragmentExercise_listView);
    }

    void loadRelativeLayouts(){
        relativeLayout_progressBar = view.findViewById(R.id.fragmentExercise_relativeLayout_progressBar);
    }

    void loadLinearLayouts() {
        linearLayout_settings = view.findViewById(R.id.fragmentExercise_linearLayout_settings);
        linearLayout_settingsButton = view.findViewById(R.id.fragmentExercise_linearLayout_settingsButton);
        linearLayout_settingsButton.setOnClickListener(v -> controlSettings());
        linearLayout_hintButton = view.findViewById(R.id.fragmentExercise_linearLayout_hintButton);
        linearLayout_hintButton.setOnClickListener(v -> {
            imageButton_hint.setImageResource(R.drawable.icon_hint);
            textView_hint.setTextColor(getResources().getColor(R.color.logo_letters));
            linearLayout_settings.setVisibility(View.INVISIBLE);
            if (settingsIsVisible) {
                controlSettings();
            }
            if (helpIsVisible) {
                listView_help.setVisibility(View.INVISIBLE);
                helpIsVisible = false;
            } else {
                SQLite sqLite = new SQLite(getContext());
                sqLite.wrongAnswer(currentWordID);
                App.closeKeyboard((ActivityMain) requireContext());
                linearLayout_settings.setVisibility(View.INVISIBLE);
                listView_help.setVisibility(View.VISIBLE);
                if (isEnglish) {
                    setArrayAdapter(currentMeaningsList);
                } else {
                    setArrayAdapter(correctWordsList);
                }
                helpIsVisible = true;
            }
        });
    }

    void loadSwitches() {
        switch_favorite = view.findViewById(R.id.fragmentExercise_switch_favorite);
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
        switch_sound = view.findViewById(R.id.fragmentExercise_switch_sound);
    }

    void loadSeekBars() {
        seekBar_difficulty = view.findViewById(R.id.fragmentExercise_seekBar_difficulty);
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
        seekBar_unknownChance = view.findViewById(R.id.fragmentExercise_seekBar_unknownChance);
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
        seekBar_meansChance = view.findViewById(R.id.fragmentExercise_seekBar_meansChance);
        seekBar_meansChance.setProgress(means_chance);
        seekBar_meansChance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textView_meansChanceCount.setText("%" + progress);
                means_chance = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    //**********************************************************************************************
    // FUNCTIONS

    @SuppressLint("ResourceAsColor")
    void imageViewControl(boolean STATE) {
        imageView_control.setVisibility(View.VISIBLE);
        if (STATE) {
            imageButton_hint.setImageResource(R.drawable.icon_hint);
            textView_hint.setTextColor(getResources().getColor(R.color.logo_letters));
            imageView_control.setImageResource(R.drawable.icon_true);
            if (!isEnglish) {
                if (switch_sound.isChecked()) {
                    App.speechEnglish(getContext(), editText_inputWord.getText().toString());
                }
                handler.postDelayed(() -> {
                    imageView_control.setVisibility(View.INVISIBLE);
                    load();
                }, controlIconDuration);
            } else {
                handler.postDelayed(() -> imageView_control.setVisibility(View.INVISIBLE), controlIconDuration);
                load();
            }
        } else {
            imageView_control.setImageResource(R.drawable.icon_false);
            imageButton_hint.setImageResource(R.drawable.icon_hint_colored);
            textView_hint.setTextColor(getResources().getColor(R.color.imageButton_favorite_fill_color));
        }
    }

    ArrayList<String> randomWord(ArrayList<ArrayList<String>> LIST) {
        int randomNumber = new Random().nextInt(LIST.size());
        return LIST.get(randomNumber);
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

    String controlSwitchFavorite() {
        SQLite sqLite = new SQLite(getContext());
        if (switch_favorite.isChecked() && hasFavoriteWord(sqLite.getWords())) {
            return "true";
        } else {
            switch_favorite.setChecked(false);
            return "false";
        }
    }

    void controlSettings() {
        if (settingsIsVisible) {
            settingsIsVisible = false;
            editText_inputWord.setEnabled(true);
            imageButton_settings.setImageResource(R.drawable.icon_settings);
            textView_settings.setTextColor(getResources().getColor(R.color.logo_letters));
            linearLayout_settings.setVisibility(View.INVISIBLE);
            textView_currentAnswerLanguage.setVisibility(View.VISIBLE);
        } else {
            App.closeKeyboard((ActivityMain) requireContext());
            settingsIsVisible = true;
            editText_inputWord.setEnabled(false);
            imageButton_settings.setImageResource(R.drawable.icon_settings_active);
            textView_settings.setTextColor(getResources().getColor(R.color.logo_color));
            textView_currentAnswerLanguage.setVisibility(View.INVISIBLE);
            linearLayout_settings.setVisibility(View.VISIBLE);
        }
    }

    void setArrayAdapter(ArrayList<String> LIST) {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), R.layout.item_listview_help, R.id.item_listView_textView_help, LIST);
        listView_help.setAdapter(arrayAdapter);
        listView_help.refreshDrawableState();
    }

    String randomMean(ArrayList<String> list) {
        int RANDOM = new Random().nextInt(list.size());
        return list.get(RANDOM);
    }

    int findID(String WORD) {
        for (ArrayList<String> item : firstWordsList) {
            if (item.get(1).equals(WORD)) {
                return Integer.parseInt(item.get(0));
            }
        }
        return 0;
    }

    void load() {
        if (nextWordList.size() > 0) {
            currentWordList = nextWordList;
            currentWordID = Integer.parseInt(nextWordList.get(0));
            randomEnglishWord = nextWordList.get(1);
            currentMeaningsList = nextMeaningsList;
        } else {
            currentWordList = randomWord(firstWordsList);
            currentWordID = Integer.parseInt(currentWordList.get(0));
            randomEnglishWord = currentWordList.get(1);
            SQLite sqLite = new SQLite(getContext());
            currentMeaningsList = sqLite.getMeans(currentWordID);
        }
        isEnglish = !App.randomChance(means_chance);
        if (isEnglish) {
            imageButton_favorite.setVisibility(View.VISIBLE);
            textView_word.setText(randomEnglishWord);
            textView_currentAnswerLanguage.setText(getString(R.string.TURKISH));
            if (currentWordList.get(2).equals("1")) {
                imageButton_favorite.setImageResource(R.drawable.icon_favorite_filled);
                wordIsFavorite = true;
            } else {
                imageButton_favorite.setImageResource(R.drawable.icon_favorite_filled_light);
                wordIsFavorite = false;
            }
            if (switch_sound.isChecked()) {
                App.speechEnglish(getContext(), randomEnglishWord);
            }
        } else {
            imageButton_favorite.setVisibility(View.INVISIBLE);
            randomTurkishWord = randomMean(currentMeaningsList);
            textView_currentAnswerLanguage.setText(getString(R.string.ENGLISH));
            textView_word.setText(randomTurkishWord);
            asyncTaskArrayList.add(new loadCorrectWords().execute(randomTurkishWord));
        }
        editText_inputWord.setText("");
        asyncTaskArrayList.add(new getNextWord().execute(controlSwitchFavorite(), String.valueOf(difficulty), String.valueOf(unknown_chance)));
    }

    void control() {
        String inputWord = editText_inputWord.getText().toString().toLowerCase();
        if (isEnglish) {
            if (currentMeaningsList.contains(inputWord)) {
                asyncTaskArrayList.add(new controlAccuracy().execute("true", String.valueOf(currentWordID)));
                imageViewControl(true);
            } else {
                asyncTaskArrayList.add(new controlAccuracy().execute("false", String.valueOf(currentWordID)));
                imageViewControl(false);
            }
        } else {
            if (correctWordsList.contains(inputWord)) {
                asyncTaskArrayList.add(new controlAccuracy().execute("true", String.valueOf(currentWordID)));
                imageViewControl(true);
            } else {
                for (String item : correctWordsList) {
                    asyncTaskArrayList.add(new controlAccuracy().execute("false", String.valueOf(findID(item))));
                }
                imageViewControl(false);
            }
        }
    }
}
