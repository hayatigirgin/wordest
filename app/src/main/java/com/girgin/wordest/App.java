package com.girgin.wordest;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class App extends Application {

    static TextToSpeech textToSpeech;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static void loadFragment(
            FragmentManager fragmentManager,
            int frameLayout,
            Fragment fragment,
            String TAG,
            Bundle bundle){
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (bundle != null){
            fragment.setArguments(bundle);
        }
        fragmentTransaction.add(frameLayout, fragment, TAG).addToBackStack(TAG);
        fragmentTransaction.commit();
    }

    public static ArrayList<String> getOnlyColumn(ArrayList<ArrayList<String>> table_list) {
        ArrayList<String> list = new ArrayList<>();
        for (ArrayList<String> item : table_list) {
            list.add(item.get(1));
        }
        return list;
    }

    public static void closeKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void toastShow(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void toastShowLong(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void speechEnglish(Context context, String WORD) {
        textToSpeech = new TextToSpeech(context, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = textToSpeech.setLanguage(Locale.ENGLISH);
                if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                    textToSpeech.speak(WORD, TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });
    }

    public static boolean randomChance(int CHANCE){
        int RANDOM = new Random().nextInt(101);
        return RANDOM <= CHANCE;
    }
}
