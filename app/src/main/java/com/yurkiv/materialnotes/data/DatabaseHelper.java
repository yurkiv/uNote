package com.yurkiv.materialnotes.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.yurkiv.materialnotes.model.Hashtag;
import com.yurkiv.materialnotes.model.Note;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by yurkiv on 03.02.2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG =DatabaseHelper.class.getSimpleName();
    private static final int DATABASE_VERSION=2;
    private static final String DATABASE_NAME="NotesDB";

    private static final String TABLE_NOTES="notes";
    private static final String TABLE_HASHTAGS="hashtags";
    private static final String TABLE_NOTES_HASHTAGS="notes_hashtags";

    private static final String KEY_ID="id";
    private static final String KEY_TITLE="title";
    private static final String KEY_CONTENT="content";
    private static final String KEY_UPDATED_AT="updated_at";
    private static final String KEY_NAME="name";
    private static final String KEY_NOTE_ID = "note_id";
    private static final String KEY_HASHTAG_ID = "tag_id";

    private static final String CREATE_TABLE_NOTE="CREATE TABLE "
            +TABLE_NOTES+"("+KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
            +KEY_TITLE+" TEXT, "
            +KEY_CONTENT+" TEXT, "
            +KEY_UPDATED_AT+" BIGINT"+")";

    private static final String CREATE_TABLE_HASHTAG="CREATE TABLE "
            +TABLE_HASHTAGS+"("+KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
            +KEY_NAME+" TEXT"+")";

    private static final String CREATE_TABLE_NOTES_HASHTAGS = "CREATE TABLE "
            + TABLE_NOTES_HASHTAGS + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_NOTE_ID + " INTEGER," + KEY_HASHTAG_ID + " INTEGER" + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_NOTE);
        Log.i(TAG, CREATE_TABLE_NOTE);
        db.execSQL(CREATE_TABLE_HASHTAG);
        Log.i(TAG, CREATE_TABLE_HASHTAG);
        db.execSQL(CREATE_TABLE_NOTES_HASHTAGS);
        Log.i(TAG, CREATE_TABLE_NOTES_HASHTAGS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXIST "+TABLE_NOTES);
        db.execSQL("DROP TABLE IF EXIST "+TABLE_HASHTAGS);
        db.execSQL("DROP TABLE IF EXIST "+CREATE_TABLE_NOTES_HASHTAGS);
        onCreate(db);
    }

    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }
    //========================================NOTES=============================================
    public long createNote(Note note){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(KEY_TITLE, note.getTitle());
        values.put(KEY_CONTENT, note.getContent());
        values.put(KEY_UPDATED_AT, note.getUpdatedAt().getTime());
        long note_id = db.insert(TABLE_NOTES, null, values);
        for (Hashtag hashtag : note.getHashtags()){
            long hashtag_id=createHashtag(hashtag);
            createNoteHashtags(note_id, hashtag_id);
        }
        Log.i(TAG, "Create note:"+note_id);
        return note_id;
    }

    public Note getNote(long noteId){
        SQLiteDatabase db=this.getReadableDatabase();
        String selectQuery="SELECT * FROM "+TABLE_NOTES+" WHERE "+KEY_ID+" = "+noteId;
        Log.e(TAG, selectQuery);
        Cursor cursor=db.rawQuery(selectQuery, null);
        if (cursor!=null){
            cursor.moveToFirst();
        }
        Note note=new Note();
        note.setId(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
        note.setTitle(cursor.getString(cursor.getColumnIndex(KEY_TITLE)));
        note.setContent(cursor.getString(cursor.getColumnIndex(KEY_CONTENT)));
        note.setUpdatedAt(new Date(cursor.getLong(cursor.getColumnIndex(KEY_UPDATED_AT))));
        note.setHashtags(getAllHashTagsByNote(note));
        return note;
    }

    public List<Note> getAllNotes(){
        List<Note> result=new ArrayList<>();
        SQLiteDatabase db=this.getReadableDatabase();
        String selectQuery="SELECT * FROM "+TABLE_NOTES;
        Log.e(TAG, selectQuery);
        Cursor cursor=db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()){
            do {
                Note note=new Note();
                note.setId(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
                note.setTitle(cursor.getString(cursor.getColumnIndex(KEY_TITLE)));
                note.setContent(cursor.getString(cursor.getColumnIndex(KEY_CONTENT)));
                note.setUpdatedAt(new Date(cursor.getLong(cursor.getColumnIndex(KEY_UPDATED_AT))));
                note.setHashtags(getAllHashTagsByNote(note));
                result.add(note);
                //Log.d(TAG, note.toString());
            } while (cursor.moveToNext());
        }
        Log.i(TAG, "getAllNotes:"+result);
        return result;
    }

    public List<Note> getAllNotesByHashTags(Hashtag hashtag){
        List<Note> result=new ArrayList<>();
        SQLiteDatabase db=this.getReadableDatabase();
        String selectQuery="SELECT * FROM "+TABLE_NOTES + " notes, "
                + TABLE_HASHTAGS + " tags, " + TABLE_NOTES_HASHTAGS + " notes_tags WHERE tags."
                + KEY_NAME + " = '" + hashtag.getName() + "'" + " AND tags." + KEY_ID
                + " = " + "notes_tags." + KEY_HASHTAG_ID + " AND notes." + KEY_ID + " = "
                + "notes_tags." + KEY_NOTE_ID;

        Log.e(TAG, selectQuery);
        Cursor cursor=db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()){
            do {
                Note note=new Note();
                note.setId(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
                note.setTitle(cursor.getString(cursor.getColumnIndex(KEY_TITLE)));
                note.setContent(cursor.getString(cursor.getColumnIndex(KEY_CONTENT)));
                note.setUpdatedAt(new Date(cursor.getLong(cursor.getColumnIndex(KEY_UPDATED_AT))));
                note.setHashtags(getAllHashTagsByNote(note));
                result.add(note);
                //Log.d(TAG, note.toString());
            } while (cursor.moveToNext());
        }
        Log.i(TAG, selectQuery);
        Log.i(TAG, "getAllNotesByHashTags:"+result);
        return result;
    }

    public void updateNote(Note note){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(KEY_TITLE, note.getTitle());
        values.put(KEY_CONTENT, note.getContent());
        values.put(KEY_UPDATED_AT, note.getUpdatedAt().getTime());
        for (Hashtag hashtag : note.getHashtags()){
            long hashtag_id=createHashtag(hashtag);
            createNoteHashtags(note.getId(), hashtag_id);
        }
        db.update(TABLE_NOTES, values, KEY_ID + " = ?", new String[]{String.valueOf(note.getId())});
        Log.i(TAG, "updateNote:"+note);

    }

    public void deleteNote(Note note){
        SQLiteDatabase db=this.getWritableDatabase();
        db.delete(TABLE_NOTES, KEY_ID+" = ?", new String[]{String.valueOf(note.getId())});
        Log.i(TAG, "deleteNote:"+note);
        deleteNoteHashtagByNote(note);
        deleteAllHashtagsIfNotUsed();
    }

    //========================================HASHTAGS=============================================
    public long createHashtag(Hashtag hashtag){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(KEY_NAME, hashtag.getName());
        Log.i(TAG, "createHashtag:"+hashtag);
        return db.insert(TABLE_HASHTAGS, null, values);
    }

    public Hashtag getHashtag(long hashtagId){
        SQLiteDatabase db=this.getReadableDatabase();
        String selectQuery="SELECT * FROM "+TABLE_HASHTAGS+" WHERE "+KEY_ID+" = "+hashtagId;
        Log.e(TAG, selectQuery);
        Cursor cursor=db.rawQuery(selectQuery, null);
        if (cursor!=null){
            cursor.moveToFirst();
        }
        Hashtag hashtag=new Hashtag();
        hashtag.setId(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
        hashtag.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
        return hashtag;
    }

    public List<Hashtag> getAllHashtags(){
        List<Hashtag> result=new ArrayList<>();
        SQLiteDatabase db=this.getReadableDatabase();
        String selectQuery="SELECT * FROM "+TABLE_HASHTAGS;
        Log.e(TAG, selectQuery);
        Cursor cursor=db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()){
            do {
                Hashtag hashtag=new Hashtag();
                hashtag.setId(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
                hashtag.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                result.add(hashtag);
                //Log.d(TAG, note.toString());
            } while (cursor.moveToNext());
        }
        Log.i(TAG, "getAllHashtags:"+result);
        return result;
    }

    public List<Hashtag> getAllHashTagsByNote(Note note){
        List<Hashtag> result=new ArrayList<>();
        SQLiteDatabase db=this.getReadableDatabase();
        String selectQuery="SELECT * FROM "+TABLE_HASHTAGS + " , " + TABLE_NOTES_HASHTAGS + " WHERE "+
                TABLE_NOTES_HASHTAGS + "." + KEY_NOTE_ID + " = '" + note.getId() + "'"
                + " AND " + TABLE_NOTES_HASHTAGS + "." + KEY_HASHTAG_ID + " = "
                + TABLE_HASHTAGS + "." + KEY_ID;

        Log.e(TAG, selectQuery);
        Cursor cursor=db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()){
            do {
                Hashtag hashtag=new Hashtag();
                hashtag.setId(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
                hashtag.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                result.add(hashtag);
                //Log.d(TAG, note.toString());
            } while (cursor.moveToNext());
        }
        Log.i(TAG, selectQuery);
        Log.i(TAG, "getAllHashTagsByNote:"+result);
        return result;
    }

    public void deleteAllHashtagsIfNotUsed(){
        SQLiteDatabase db=this.getWritableDatabase();
        String args="";
        StringBuilder stringBuilder=new StringBuilder();
        List<String> hashtags=getAllNoteHashtags();
        for (int i = 0; i < hashtags.size(); i++) {
            stringBuilder.append(hashtags.get(i));
            if (i!=hashtags.size()-1){
                stringBuilder.append(",");
            }
        }
        args=stringBuilder.toString();
        Log.i(TAG, args);
        db.delete(TABLE_HASHTAGS, KEY_ID + " NOT IN (" + args + ")", null);
        Log.i(TAG, "deleteAllHashtagsIfNotUsed: !="+args);
    }

    public void deleteHashtag(Hashtag hashtag){
        SQLiteDatabase db=this.getWritableDatabase();
        db.delete(TABLE_HASHTAGS, KEY_ID + " = ?", new String[]{String.valueOf(hashtag.getId())});
        Log.i(TAG, "deleteHashtag:"+hashtag);

    }

    //========================================NOTE_HASHTAGS========================================

    private void createNoteHashtags(long note_id, long hashtag_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NOTE_ID, note_id);
        values.put(KEY_HASHTAG_ID, hashtag_id);

        long id = db.insert(TABLE_NOTES_HASHTAGS, null, values);
        Log.i(TAG, "createNoteHashtags:"+id);
    }

    private void deleteNoteHashtagByNote(Note note){
        SQLiteDatabase db=this.getWritableDatabase();
        db.delete(TABLE_NOTES_HASHTAGS, KEY_NOTE_ID+" = ?", new String[]{String.valueOf(note.getId())});
        Log.i(TAG, "deleteNoteHashtagByNote:"+note.getId());

    }

    public List<String> getAllNoteHashtags(){
        List<String> result=new ArrayList<>();
        SQLiteDatabase db=this.getReadableDatabase();
        String selectQuery="SELECT * FROM "+TABLE_NOTES_HASHTAGS;
        Log.e(TAG, selectQuery);
        Cursor cursor=db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()){
            do {
                String id= String.valueOf(cursor.getLong(cursor.getColumnIndex(KEY_HASHTAG_ID)));
                result.add(id);
                //Log.d(TAG, note.toString());
            } while (cursor.moveToNext());
        }
        Log.i(TAG, "getAllNoteHashtags:"+result);
        return result;
    }

}
