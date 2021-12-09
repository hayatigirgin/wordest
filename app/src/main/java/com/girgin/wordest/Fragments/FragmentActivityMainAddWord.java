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
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.girgin.wordest.Activities.ActivityMain;
import com.girgin.wordest.App;
import com.girgin.wordest.R;
import com.girgin.wordest.SQLite.DatabaseAccess;
import com.girgin.wordest.SQLite.SQLite;

import java.util.ArrayList;
import java.util.Random;

public class FragmentActivityMainAddWord extends Fragment {

    ArrayList<String> words_list = new ArrayList<>();
    ArrayList<ArrayList<String>> database_words_list = new ArrayList<>();
    ArrayList<String> database_only_words_list = new ArrayList<>();
    ArrayList<String> database_means_list = new ArrayList<>();
    ArrayList<String> database_descriptions_list = new ArrayList<>();
    ArrayList<String> database_sentences_list = new ArrayList<>();
    ArrayList<String> database_words = new ArrayList<>();
    ArrayList<AsyncTask> asyncTasksArrayList = new ArrayList<>();

    int selected_id, random_id;
    String selected_word, random_word;

    View
            VIEW;
    AlertDialog.Builder
            builder;
    AutoCompleteTextView
            AUTOCOMPLETETEXTVIEW;
    ImageButton
            imageButtonClose;
    Button
            button_addThisWord,
            button_addRandomWord;

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        VIEW = layoutInflater.inflate(R.layout.fragment_activity_main_word_add, viewGroup, false);
        //***
        asyncTasksArrayList.add(new loadWordsFromDatabase().execute());
        //***
        loadButtons();
        //***
        loadImageButtons();
        //***
        loadAutoCompleteTextViews();
        //***
        builder = new AlertDialog.Builder(getContext());
        //***
        return VIEW;
    }

    //**********************************************************************************************
    //BACKGROUND PROCESSES

    @SuppressLint("StaticFieldLeak")
    private class loadWordsFromDatabase extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getContext());
            databaseAccess.openDatabase();
            database_words_list = databaseAccess.getWordsTable();
            for (ArrayList<String> word : database_words_list) {
                database_words.add(word.get(1));
            }
            databaseAccess.closeDatabase();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    //**********************************************************************************************
    //FUNCTIONS

    void showWordAddDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme);
        View view = LayoutInflater.from(getContext()).inflate(
                R.layout.dialog_word_add,
                VIEW.findViewById(R.id.dialog_wordAdd_constraintLayout));
        builder.setView(view);
        ((TextView)view.findViewById(R.id.dialog_wordAdd_textView_word)).setText(random_word.toUpperCase().replace("İ", "I"));
        AlertDialog alertDialog = builder.create();
        view.findViewById(R.id.dialog_wordAdd_button_cancel).setOnClickListener(v -> alertDialog.dismiss());
        view.findViewById(R.id.dialog_wordAdd_button_ok).setOnClickListener(v -> {
            getWordInfo(random_id + 1);
            SQLite sqLite = new SQLite(getContext());
            sqLite.addWord(random_word,
                    database_means_list,
                    database_descriptions_list,
                    database_sentences_list);
            ((ActivityMain) requireContext()).refreshRecyclerView();
            ((ActivityMain) requireContext()).removeFragmentAddWord();
            alertDialog.dismiss();
        });
        if (alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        alertDialog.show();
    }

    void getWordInfo(int WORD_ID) {
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getContext());
        databaseAccess.openDatabase();
        database_means_list = databaseAccess.getMeans(WORD_ID);
        databaseAccess.closeDatabase();
        databaseAccess.openDatabase();
        database_descriptions_list = databaseAccess.getDescriptions(WORD_ID);
        databaseAccess.closeDatabase();
        databaseAccess.openDatabase();
        database_sentences_list = databaseAccess.getSentences(WORD_ID);
        databaseAccess.closeDatabase();
    }

    void addSelectedWord() {
        selected_word = AUTOCOMPLETETEXTVIEW.getText().toString();
        if (!selected_word.equals("")) {
            database_only_words_list = App.getOnlyColumn(database_words_list);
            if (!inListControl(database_only_words_list, selected_word)) {
                SQLite sqLite = new SQLite(getContext());
                words_list = App.getOnlyColumn(sqLite.getWords());
                if (inListControl(words_list, selected_word)) {
                    for (ArrayList<String> item : database_words_list) {
                        if (item.get(1).equals(selected_word)) {
                            selected_id = Integer.parseInt(item.get(0));
                            break;
                        }
                    }
                    getWordInfo(selected_id);
                    sqLite.addWord(selected_word,
                            database_means_list,
                            database_descriptions_list,
                            database_sentences_list);
                    ((ActivityMain) requireContext()).refreshRecyclerView();
                    AUTOCOMPLETETEXTVIEW.setText("");
                } else {
                    Toast.makeText(getContext(), "This word is already on your list.", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getContext(), "This word was not found.", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getContext(), "Please select a word first.", Toast.LENGTH_LONG).show();
        }
    }

    void addRandomWord() {
        SQLite sqLite = new SQLite(getContext());
        words_list = App.getOnlyColumn(sqLite.getWords());
        random_id = randomNumber(database_words_list.size());
        while (!inListControl(words_list, database_words_list.get(random_id).get(1))) {
            random_id = randomNumber(database_words_list.size());
        }
        random_word = database_words_list.get(random_id).get(1);
        showWordAddDialog();
    }

    int randomNumber(int SIZE) {
        return new Random().nextInt(SIZE);
    }

    boolean inListControl(@NonNull ArrayList<String> list, String key_word) {
        for (String item : list) {
            if (item.equals(key_word)) {
                return false;
            }
        }
        return true;
    }

    //**********************************************************************************************
    //LOAD COMPONENTS

    void loadAutoCompleteTextViews() {
        AUTOCOMPLETETEXTVIEW = VIEW.findViewById(R.id.fragment_activityMain_addWord_autoCompleteTextView);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(VIEW.getContext(), R.layout.item_autocompletetextview, R.id.item_autocomplete_textview, database_words);
        AUTOCOMPLETETEXTVIEW.setAdapter(arrayAdapter);
        AUTOCOMPLETETEXTVIEW.setOnItemClickListener((parent, view, position, id) -> App.closeKeyboard((ActivityMain) requireContext()));
    }

    void loadImageButtons() {
        imageButtonClose = VIEW.findViewById(R.id.fragment_activityMain_addWord_imageButton_close);
        imageButtonClose.setOnClickListener(v -> {
            ((ActivityMain) requireContext()).removeFragmentAddWord();
            App.closeKeyboard((ActivityMain) requireContext());
        });
    }

    void loadButtons() {
        button_addThisWord = VIEW.findViewById(R.id.fragment_activityMain_addWord_button_addThisWord);
        button_addThisWord.setOnClickListener(v -> {
            App.closeKeyboard((ActivityMain) requireContext());
            addSelectedWord();
        });
        button_addRandomWord = VIEW.findViewById(R.id.fragment_activityMain_addWord_button_addRandomWord);
        button_addRandomWord.setOnClickListener(v -> {
            App.closeKeyboard((ActivityMain) requireContext());
            addRandomWord();
        });
    }
}
