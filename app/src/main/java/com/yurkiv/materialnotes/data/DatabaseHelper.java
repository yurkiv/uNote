package com.yurkiv.materialnotes.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.yurkiv.materialnotes.model.Note;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by yurkiv on 03.02.2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String LOG=DatabaseHelper.class.getSimpleName();
    private static final int DATABASE_VERSION=1;
    private static final String DATABASE_NAME="NotesDB";

    private static final String TABLE_NOTES="notes";
    private static final String KEY_ID="id";
    private static final String KEY_TITLE="title";
    private static final String KEY_CONTENT="content";
    private static final String KEY_UPDATED_AT="updated_at";

    private static final String CREATE_TABLE_NOTE="CREATE TABLE "
            +TABLE_NOTES+"("+KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
            +KEY_TITLE+" TEXT, "
            +KEY_CONTENT+" TEXT, "
            +KEY_UPDATED_AT+" BIGINT"+")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_NOTE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXIST "+TABLE_NOTES);
        onCreate(db);
    }

    public long createNote(Note note){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(KEY_TITLE, note.getTitle());
        values.put(KEY_CONTENT, note.getContent());
        values.put(KEY_UPDATED_AT, note.getUpdatedAt().getTime());
        return db.insert(TABLE_NOTES, null, values);
    }

    public Note getNote(long noteId){
        SQLiteDatabase db=this.getReadableDatabase();
        String selectQuery="SELECT * FROM "+TABLE_NOTES+" WHERE "+KEY_ID+" = "+noteId;
        Log.e(LOG, selectQuery);
        Cursor cursor=db.rawQuery(selectQuery, null);
        if (cursor!=null){
            cursor.moveToFirst();
        }
        Note note=new Note();
        note.setId(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
        note.setTitle(cursor.getString(cursor.getColumnIndex(KEY_TITLE)));
        note.setContent(cursor.getString(cursor.getColumnIndex(KEY_CONTENT)));
        note.setUpdatedAt(new Date(cursor.getLong(cursor.getColumnIndex(KEY_UPDATED_AT))));
        return note;
    }

    public List<Note> getAllNotes(){
        List<Note> result=new ArrayList<>();
        SQLiteDatabase db=this.getReadableDatabase();
        String selectQuery="SELECT * FROM "+TABLE_NOTES;
        Log.e(LOG, selectQuery);
        Cursor cursor=db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()){
            do {
                Note note=new Note();
                note.setId(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
                note.setTitle(cursor.getString(cursor.getColumnIndex(KEY_TITLE)));
                note.setContent(cursor.getString(cursor.getColumnIndex(KEY_CONTENT)));
                note.setUpdatedAt(new Date(cursor.getLong(cursor.getColumnIndex(KEY_UPDATED_AT))));
                result.add(note);
                //Log.d(LOG, note.toString());
            } while (cursor.moveToNext());
        }
        return result;
    }

    public void updateNote(Note note){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(KEY_TITLE, note.getTitle());
        values.put(KEY_CONTENT, note.getContent());
        values.put(KEY_UPDATED_AT, note.getUpdatedAt().getTime());
        db.update(TABLE_NOTES, values, KEY_ID+" = ?", new String[]{String.valueOf(note.getId())});
    }

    public void deleteNote(Note note){
        SQLiteDatabase db=this.getWritableDatabase();
        db.delete(TABLE_NOTES, KEY_ID+" = ?", new String[]{String.valueOf(note.getId())});
    }

    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }
}
