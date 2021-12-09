package com.girgin.wordest.SQLite;

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class DatabaseOpenHelper extends SQLiteAssetHelper {
    private static final String DATABASE_NAME = "database.db";
    private static final int DATABASE_VERSION = 3;

    public DatabaseOpenHelper (Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
}
