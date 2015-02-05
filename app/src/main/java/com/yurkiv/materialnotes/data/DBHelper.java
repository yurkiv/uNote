package com.yurkiv.materialnotes.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by yurkiv on 02.02.2015.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "NotesDB.db";
    private static final String NOTES_TABLE_NAME = "notes";
    private static final String NOTES_COLUMN_ID = "_id";
    private static final String NOTES_COLUMN_TITLE = "title";
    private static final String NOTES_COLUMN_CONTENT = "content";
    private static final String NOTES_COLUMN_UPDATED_AT = "updatedat";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String req="create table "+NOTES_TABLE_NAME+
                "("+NOTES_COLUMN_ID+" integer primary key, "+NOTES_COLUMN_TITLE+" text, "
                +NOTES_COLUMN_CONTENT+" text, "+NOTES_COLUMN_UPDATED_AT+" )";
        db.execSQL(req);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
