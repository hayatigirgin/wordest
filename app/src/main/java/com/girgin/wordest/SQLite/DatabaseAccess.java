package com.girgin.wordest.SQLite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DatabaseAccess {
    private final String TAG = DatabaseAccess.class.getName();
    private final SQLiteOpenHelper sqLiteOpenHelper;
    private SQLiteDatabase sqLiteDatabase;
    private static DatabaseAccess databaseAccess;
    Cursor cursor = null;


    private DatabaseAccess(Context context){
        this.sqLiteOpenHelper = new DatabaseOpenHelper(context);
    }

    public static DatabaseAccess getInstance(Context context){
        if (databaseAccess == null){
            databaseAccess = new DatabaseAccess(context);
        }
        return databaseAccess;
    }

    public void openDatabase(){
        this.sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();
    }

    public void closeDatabase(){
        if (sqLiteDatabase != null){
            this.sqLiteDatabase.close();
        }
    }

    public ArrayList<ArrayList<String>> getWordsTable(){
        ArrayList<ArrayList<String>> words_list = new ArrayList<>();
        try {
            cursor = sqLiteDatabase.rawQuery("SELECT * FROM WORDS", null);
            while (cursor.moveToNext()){
                ArrayList<String> list = new ArrayList<>();
                list.add(cursor.getString(0));  //WORD_ID
                list.add(cursor.getString(1));  //WORD
                list.add(cursor.getString(2));  //IS_FAVORITE
                words_list.add(list);
            }
        }
        catch (Exception e){
            Log.e(TAG, "DatabaseAccess - getWords:");
        }
        return words_list;
    }

    public ArrayList<String> getMeans(int WORD_ID){
        ArrayList<String> means_list = new ArrayList<>();
        try {
            cursor = sqLiteDatabase.rawQuery("SELECT MEANING FROM MEANS WHERE WORD_ID = " + WORD_ID, null);
            while (cursor.moveToNext()){
                means_list.add(cursor.getString(0));  //MEANING
            }
        }
        catch (Exception e){
            Log.e(TAG, "DatabaseAccess - getMeans:");
        }
        return means_list;
    }

    public ArrayList<String> getDescriptions(int WORD_ID){
        ArrayList<String> descriptions_list = new ArrayList<>();
        try {
            cursor = sqLiteDatabase.rawQuery("SELECT DESCRIPTION FROM DESCRIPTIONS WHERE WORD_ID = " + WORD_ID, null);
            while (cursor.moveToNext()){
                descriptions_list.add(cursor.getString(0));  //DESCRIPTION
            }
        }
        catch (Exception e){
            Log.e(TAG, "DatabaseAccess - getDescriptions:");
        }
        return descriptions_list;
    }

    public ArrayList<String> getSentences(int WORD_ID){
        ArrayList<String> sentences_list = new ArrayList<>();
        try {
            cursor = sqLiteDatabase.rawQuery("SELECT SENTENCE FROM SENTENCES WHERE WORD_ID = " + WORD_ID, null);
            while (cursor.moveToNext()){
                sentences_list.add(cursor.getString(0));  //SENTENCE
            }
        }
        catch (Exception e){
            Log.e(TAG, "DatabaseAccess - getSentences:");
        }
        return sentences_list;
    }

    public ArrayList<String> getAllMeans(){
        ArrayList<String> means_list = new ArrayList<>();
        try {
            cursor = sqLiteDatabase.rawQuery("SELECT MEANING FROM MEANS", null);
            while (cursor.moveToNext()){
                means_list.add(cursor.getString(0));  //MEANING
            }
        }
        catch (Exception e){
            Log.e(TAG, "DatabaseAccess - getAllMeans:");
        }
        return means_list;
    }
}
