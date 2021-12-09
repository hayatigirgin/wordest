package com.girgin.wordest.SQLite;

public class Queries {

    public String createTableWords (
            String TABLE_WORDS,
            String COLUMN_WORD_ID,
            String COLUMN_WORD,
            String COLUMN_IS_FAVORITE,
            String COLUMN_TOTAL_VIEWS,
            String COLUMN_CORRECT_ANSWER){
        return "CREATE TABLE IF NOT EXISTS '"+ TABLE_WORDS +"' (" +
                "'"+ COLUMN_WORD_ID +"'   INTEGER NOT NULL UNIQUE, " +
                "'"+ COLUMN_WORD +"'  TEXT NOT NULL, " +
                "'"+ COLUMN_IS_FAVORITE +"'   INTEGER NOT NULL DEFAULT 0, " +
                "'"+ COLUMN_TOTAL_VIEWS +"'   INTEGER NOT NULL DEFAULT 0, " +
                "'"+ COLUMN_CORRECT_ANSWER +"'    INTEGER NOT NULL DEFAULT 0, " +
                "PRIMARY KEY('"+ COLUMN_WORD_ID +"' AUTOINCREMENT))";
    }

    public String createTable (String TABLE_NAME,String COLUMN_WORD_ID, String COLUMN_NAME){
        return "CREATE TABLE IF NOT EXISTS '"+ TABLE_NAME +"' (" +
                "'"+ COLUMN_WORD_ID +"'   INTEGER NOT NULL, " +
                "'"+ COLUMN_NAME +"'   TEXT NOT NULL)";
    }

    public String dropTable (String TABLE_NAME){
        return "DROP TABLE IF NOT EXISTS '"+ TABLE_NAME;
    }

    //**********************************************************************************************
    //SELECT

    public String selectGetAllData(String TABLE_NAME){
        return "SELECT * FROM " + TABLE_NAME;
    }

    //**********************************************************************************************
    //UPDATE

    /*
    public String updateChangeFavoriteState(int WORD_ID){
        return "UPDATE WORDS SET IS_FAVORITE = CASE WHEN IS_FAVORITE = 1 THEN 0 ELSE 1 END WHERE WORD_ID=" + WORD_ID;
    }*/

    /*
    UPDATE WORDS SET TOTAL_VIEWS = TOTAL_VIEWS + 1, CORRECT_ANSWER = CORRECT_ANSWER + 1 WHERE WORD_ID = {word_id}
     */
}
