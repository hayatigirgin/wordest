package com.girgin.wordest.SQLite;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;


public class SQLite extends SQLiteOpenHelper {
    private final String TAG = SQLite.class.getName();
    Queries queries = new Queries();

    private static final String DATABASE_NAME = "words.db";
    private static final int DATABASE_VERSION = 3;

    public static final String TABLE_WORDS = "WORDS";
    public static final String COLUMN_WORD_ID = "WORD_ID";
    public static final String COLUMN_WORD = "WORD";
    public static final String COLUMN_IS_FAVORITE = "IS_FAVORITE";
    public static final String COLUMN_TOTAL_VIEWS = "TOTAL_VIEWS";
    public static final String COLUMN_CORRECT_ANSWER = "CORRECT_ANSWER";

    public static final String TABLE_MEANS = "MEANS";
    public static final String COLUMN_MEANING = "MEANING";

    public static final String TABLE_DESCRIPTIONS = "DESCRIPTIONS";
    public static final String COLUMN_DESCRIPTION = "DESCRIPTION";

    public static final String TABLE_SENTENCES = "SENTENCES";
    public static final String COLUMN_SENTENCE = "SENTENCE";


    public SQLite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(queries.createTableWords(
                TABLE_WORDS,
                COLUMN_WORD_ID,
                COLUMN_WORD,
                COLUMN_IS_FAVORITE,
                COLUMN_TOTAL_VIEWS,
                COLUMN_CORRECT_ANSWER));
        sqLiteDatabase.execSQL(queries.createTable(TABLE_MEANS, COLUMN_WORD_ID, COLUMN_MEANING));
        sqLiteDatabase.execSQL(queries.createTable(TABLE_DESCRIPTIONS, COLUMN_WORD_ID, COLUMN_DESCRIPTION));
        sqLiteDatabase.execSQL(queries.createTable(TABLE_SENTENCES, COLUMN_WORD_ID, COLUMN_SENTENCE));
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL(queries.dropTable(TABLE_WORDS));
        sqLiteDatabase.execSQL(queries.dropTable(TABLE_MEANS));
        sqLiteDatabase.execSQL(queries.dropTable(TABLE_DESCRIPTIONS));
        sqLiteDatabase.execSQL(queries.dropTable(TABLE_SENTENCES));
        onCreate(sqLiteDatabase);
    }

    //**********************************************************************************************
    //SELECT

    public ArrayList<ArrayList<String>> getWords() {
        ArrayList<ArrayList<String>> words_list = new ArrayList<>();
        try {
            SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
            @SuppressLint("Recycle")
            Cursor cursor = sqLiteDatabase.rawQuery(queries.selectGetAllData(TABLE_WORDS), null);
            if (cursor.moveToFirst()) {
                do {
                    ArrayList<String> list = new ArrayList<>();
                    list.add(cursor.getString(0));  //WORD_ID
                    list.add(cursor.getString(1));  //WORD
                    list.add(cursor.getString(2));  //IS_FAVORITE
                    list.add(cursor.getString(3));  //TOTAL_VIEWS
                    list.add(cursor.getString(4));  //CORRECT_ANSWER
                    words_list.add(list);
                }
                while (cursor.moveToNext());
            }
            sqLiteDatabase.close();
        } catch (Exception e) {
            Log.e(TAG, "SQLite - getWords: query : " + queries.selectGetAllData(TABLE_WORDS));
        }
        return words_list;
    }

    public ArrayList<String> getMeans(int WORD_ID) {
        ArrayList<String> means_list = new ArrayList<>();
        try {
            SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
            @SuppressLint("Recycle") Cursor cursor = sqLiteDatabase.rawQuery(
                    "SELECT MEANING FROM MEANS WHERE WORD_ID = " + WORD_ID, null);
            while (cursor.moveToNext()) {
                means_list.add(cursor.getString(0));  //MEANING
            }
            sqLiteDatabase.close();
        } catch (Exception e) {
            Log.e(TAG, "SQLite - getWords:");
        }
        return means_list;
    }

    public ArrayList<String> getDescriptions(int WORD_ID) {
        ArrayList<String> descriptions_list = new ArrayList<>();
        try {
            SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
            @SuppressLint("Recycle") Cursor cursor = sqLiteDatabase.rawQuery(
                    "SELECT DESCRIPTION FROM DESCRIPTIONS WHERE WORD_ID = " + WORD_ID, null);
            while (cursor.moveToNext()) {
                descriptions_list.add(cursor.getString(0));  //DESCRIPTION
            }
            sqLiteDatabase.close();
        } catch (Exception e) {
            Log.e(TAG, "SQLite - getWords:");
        }
        return descriptions_list;
    }

    public ArrayList<String> getSentences(int WORD_ID) {
        ArrayList<String> sentences_list = new ArrayList<>();
        try {
            SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
            @SuppressLint("Recycle") Cursor cursor = sqLiteDatabase.rawQuery(
                    "SELECT SENTENCE FROM SENTENCES WHERE WORD_ID = " + WORD_ID, null);
            while (cursor.moveToNext()) {
                sentences_list.add(cursor.getString(0));  //SENTENCE
            }
            sqLiteDatabase.close();
        } catch (Exception e) {
            Log.e(TAG, "SQLite - getSentences:");
        }
        return sentences_list;
    }

    public ArrayList<String> getCorrectWords(String MEANING) {
        ArrayList<String> correct_words_list = new ArrayList<>();
        try {
            SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
            @SuppressLint("Recycle") Cursor cursor = sqLiteDatabase.rawQuery(
                    "SELECT WORDS.WORD FROM MEANS INNER JOIN WORDS ON WORDS.WORD_ID = MEANS.WORD_ID WHERE MEANING = '" + MEANING + "'", null);
            while (cursor.moveToNext()) {
                correct_words_list.add(cursor.getString(0));  //MEANING
            }
            sqLiteDatabase.close();
        } catch (Exception e) {
            Log.e(TAG, "SQLite - getCorrectWords:");
        }
        return correct_words_list;
    }

    @SuppressLint("Recycle")
    public ArrayList<ArrayList<String>> getRandomWords(boolean IS_KNOWN, int DIFFICULTY, boolean IS_FAVORITE) {
        ArrayList<ArrayList<String>> words_list = new ArrayList<>();
        try {
            SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
            Cursor cursor;
            if (IS_KNOWN) {
                if (IS_FAVORITE) {
                    cursor = sqLiteDatabase.rawQuery("SELECT *, (CASE WHEN (CORRECT_ANSWER * 100 / TOTAL_VIEWS) IS NULL THEN 0 WHEN TOTAL_VIEWS < 10 THEN 0 ELSE (CORRECT_ANSWER * 100 / TOTAL_VIEWS) END) AS 'PERCENT' FROM WORDS WHERE PERCENT < " + DIFFICULTY + " AND IS_FAVORITE = '1'", null);
                } else {
                    cursor = sqLiteDatabase.rawQuery("SELECT *, (CASE WHEN (CORRECT_ANSWER * 100 / TOTAL_VIEWS) IS NULL THEN 0 WHEN TOTAL_VIEWS < 10 THEN 0 ELSE (CORRECT_ANSWER * 100 / TOTAL_VIEWS) END) AS 'PERCENT' FROM WORDS WHERE PERCENT < " + DIFFICULTY, null);
                }
            } else {
                if (IS_FAVORITE) {
                    cursor = sqLiteDatabase.rawQuery("SELECT *, (CASE WHEN (CORRECT_ANSWER * 100 / TOTAL_VIEWS) IS NULL THEN 0 WHEN TOTAL_VIEWS < 10 THEN 0 ELSE (CORRECT_ANSWER * 100 / TOTAL_VIEWS) END) AS 'PERCENT' FROM WORDS WHERE PERCENT > " + DIFFICULTY + " OR PERCENT = " + DIFFICULTY + " AND IS_FAVORITE = '1'", null);
                } else {
                    cursor = sqLiteDatabase.rawQuery("SELECT *, (CASE WHEN (CORRECT_ANSWER * 100 / TOTAL_VIEWS) IS NULL THEN 0 WHEN TOTAL_VIEWS < 10 THEN 0 ELSE (CORRECT_ANSWER * 100 / TOTAL_VIEWS) END) AS 'PERCENT' FROM WORDS WHERE PERCENT > " + DIFFICULTY + " OR PERCENT = " + DIFFICULTY, null);
                }
            }
            if (cursor.moveToFirst()) {
                do {
                    ArrayList<String> list = new ArrayList<>();
                    list.add(cursor.getString(0));  //WORD_ID
                    list.add(cursor.getString(1));  //WORD
                    list.add(cursor.getString(2));  //IS_FAVORITE
                    list.add(cursor.getString(3));  //TOTAL_VIEWS
                    list.add(cursor.getString(4));  //CORRECT_ANSWER
                    words_list.add(list);
                }
                while (cursor.moveToNext());
            }
            sqLiteDatabase.close();
        } catch (Exception e) {
            Log.e(TAG, "SQLite - getRandomWords: query : ");
        }
        return words_list;
    }

    @SuppressLint("Recycle")
    public ArrayList<Integer> getPercentage(boolean IS_FAVORITE) {
        ArrayList<Integer> percentage_list = new ArrayList<>();
        try {
            SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
            Cursor cursor;
            if (IS_FAVORITE) {
                cursor = sqLiteDatabase.rawQuery("SELECT (CASE WHEN (CORRECT_ANSWER * 100 / TOTAL_VIEWS) IS NULL THEN 0 WHEN TOTAL_VIEWS < 10 THEN 0 ELSE (CORRECT_ANSWER * 100 / TOTAL_VIEWS) END) AS 'PERCENT' FROM WORDS WHERE IS_FAVORITE = '1'", null);
            } else {
                cursor = sqLiteDatabase.rawQuery("SELECT (CASE WHEN (CORRECT_ANSWER * 100 / TOTAL_VIEWS) IS NULL THEN 0 WHEN TOTAL_VIEWS < 10 THEN 0 ELSE (CORRECT_ANSWER * 100 / TOTAL_VIEWS) END) AS 'PERCENT' FROM WORDS", null);
            }
            if (cursor.moveToFirst()) {
                do {
                    percentage_list.add(cursor.getInt(0));  //PERCENT
                }
                while (cursor.moveToNext());
            }
            sqLiteDatabase.close();
        } catch (Exception e) {
            Log.e(TAG, "SQLite - getPercentage: query : ");
        }
        return percentage_list;
    }

    public ArrayList<Integer> getCounts() {
        ArrayList<Integer> counts_list = new ArrayList<>();
        try {
            SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
            @SuppressLint("Recycle")
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT SUM (TOTAL_VIEWS), SUM (CORRECT_ANSWER) FROM WORDS", null);
            if (cursor.moveToFirst()) {
                do {
                    counts_list.add(cursor.getInt(0));  //TOTAL_VIEWS
                    counts_list.add(cursor.getInt(1));  //CORRECT_ANSWERS
                }
                while (cursor.moveToNext());
            }
            sqLiteDatabase.close();
        } catch (Exception e) {
            Log.e(TAG, "SQLite - getCounts: query : " + queries.selectGetAllData(TABLE_WORDS));
        }
        return counts_list;
    }

    public ArrayList<Integer> getCountsThisWord(int WORD_ID) {
        ArrayList<Integer> counts_list = new ArrayList<>();
        try {
            SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
            @SuppressLint("Recycle")
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT TOTAL_VIEWS, CORRECT_ANSWER FROM WORDS WHERE WORD_ID = " + WORD_ID, null);
            if (cursor.moveToFirst()) {
                do {
                    counts_list.add(cursor.getInt(0));  //TOTAL_VIEWS
                    counts_list.add(cursor.getInt(1));  //CORRECT_ANSWERS
                }
                while (cursor.moveToNext());
            }
            sqLiteDatabase.close();
        } catch (Exception e) {
            Log.e(TAG, "SQLite - getCounts: query : " + queries.selectGetAllData(TABLE_WORDS));
        }
        return counts_list;
    }

    //**********************************************************************************************
    //INSERT

    public void addWord(String WORD,
                        ArrayList<String> MEANS_LIST,
                        ArrayList<String> DESCRIPTIONS_LIST,
                        ArrayList<String> SENTENCES_LIST) {
        int ID = 0;

        try {
            //ADD WORD
            SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("WORD", WORD);
            sqLiteDatabase.insert(TABLE_WORDS, null, contentValues);
            sqLiteDatabase.close();
        } catch (Exception e) {
            Log.e(TAG, "SQLite - addWord: ADD WORD");
        }

        try {
            //GET NEW WORD ID
            SQLiteDatabase sqLiteDatabaseFindID = this.getWritableDatabase();
            @SuppressLint("Recycle") Cursor cursor = sqLiteDatabaseFindID.rawQuery(
                    "SELECT WORD_ID FROM WORDS ORDER BY WORD_ID DESC LIMIT 1", null);
            if (cursor.moveToFirst()) {
                do {
                    ID = Integer.parseInt(cursor.getString(0));  //WORD_ID
                }
                while (cursor.moveToNext());
            }
            sqLiteDatabaseFindID.close();
        } catch (Exception e) {
            Log.e(TAG, "SQLite - addWord: GET NEW WORD ID");
        }

        try {
            //ADD MEANS
            for (String meaning : MEANS_LIST) {
                SQLiteDatabase sqLiteDatabaseMeans = this.getWritableDatabase();
                ContentValues contentValuesMeans = new ContentValues();
                contentValuesMeans.put("WORD_ID", ID);
                contentValuesMeans.put("MEANING", meaning);
                sqLiteDatabaseMeans.insert(TABLE_MEANS, null, contentValuesMeans);
                sqLiteDatabaseMeans.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "SQLite - addWord: ADD MEANS");
        }

        try {
            //ADD DESCRIPTIONS
            for (String description : DESCRIPTIONS_LIST) {
                SQLiteDatabase sqLiteDatabaseDescriptions = this.getWritableDatabase();
                ContentValues contentValuesDescriptions = new ContentValues();
                contentValuesDescriptions.put(COLUMN_WORD_ID, ID);
                contentValuesDescriptions.put(COLUMN_DESCRIPTION, description);
                sqLiteDatabaseDescriptions.insert(TABLE_DESCRIPTIONS, null, contentValuesDescriptions);
                sqLiteDatabaseDescriptions.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "SQLite - addWord: ADD DESCRIPTIONS");
        }

        try {
            //ADD SENTENCES
            for (String sentence : SENTENCES_LIST) {
                SQLiteDatabase sqLiteDatabaseSentences = this.getWritableDatabase();
                ContentValues contentValuesSentences = new ContentValues();
                contentValuesSentences.put(COLUMN_WORD_ID, ID);
                contentValuesSentences.put(COLUMN_SENTENCE, sentence);
                sqLiteDatabaseSentences.insert(TABLE_SENTENCES, null, contentValuesSentences);
                sqLiteDatabaseSentences.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "SQLite - addWord: ADD SENTENCES");
        }
    }

    //**********************************************************************************************
    //UPDATE

    @SuppressLint("Recycle")
    public void changeFavoriteState(int WORD_ID) {
        try {
            SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
            sqLiteDatabase.execSQL("UPDATE WORDS SET IS_FAVORITE = CASE WHEN IS_FAVORITE = 1 THEN 0 ELSE 1 END WHERE WORD_ID = " + WORD_ID);
            sqLiteDatabase.close();
        } catch (Exception e) {
            Log.e(TAG, "SQLite - changeFavoriteState:");
        }
    }

    public void correctAnswer(int WORD_ID) {
        try {
            SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
            sqLiteDatabase.execSQL("UPDATE WORDS SET TOTAL_VIEWS = TOTAL_VIEWS + 1, CORRECT_ANSWER = CORRECT_ANSWER + 1 WHERE WORD_ID = " + WORD_ID);
            sqLiteDatabase.close();
        } catch (Exception e) {
            Log.e(TAG, "SQLite - correctAnswer:");
        }
    }

    public void wrongAnswer(int WORD_ID) {
        try {
            SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
            sqLiteDatabase.execSQL("UPDATE WORDS SET TOTAL_VIEWS = TOTAL_VIEWS + 1 WHERE WORD_ID = " + WORD_ID);
            sqLiteDatabase.close();
        } catch (Exception e) {
            Log.e(TAG, "SQLite - correctAnswer:");
        }
    }

    //**********************************************************************************************
    //DELETE

    public void deleteWord(int WORD_ID) {
        try {
            SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
            sqLiteDatabase.execSQL("DELETE FROM WORDS WHERE WORD_ID = " + WORD_ID);
            sqLiteDatabase.execSQL("DELETE FROM MEANS WHERE WORD_ID = " + WORD_ID);
            sqLiteDatabase.execSQL("DELETE FROM DESCRIPTIONS WHERE WORD_ID = " + WORD_ID);
            sqLiteDatabase.close();
        } catch (Exception e) {
            Log.e(TAG, "SQLite - deleteWord:");
        }
    }
}
