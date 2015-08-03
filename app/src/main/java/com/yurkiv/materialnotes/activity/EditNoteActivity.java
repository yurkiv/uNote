package com.yurkiv.materialnotes.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.yurkiv.materialnotes.R;
import com.yurkiv.materialnotes.colorpicker.ColorPickerDialog;
import com.yurkiv.materialnotes.colorpicker.ColorPickerSwatch;
import com.yurkiv.materialnotes.model.Hashtag;
import com.yurkiv.materialnotes.model.Note;
import com.yurkiv.materialnotes.util.Constants;
import com.yurkiv.materialnotes.util.Utility;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import io.realm.Realm;

public class EditNoteActivity extends ActionBarActivity {

    private static final String TAG = EditNoteActivity.class.getSimpleName();
    private Toolbar mToolbar;

    private EditText editTitle;
    private EditText editContent;

    private Note note;
    private int selectedColor =Color.GRAY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        mToolbar = (Toolbar) findViewById(R.id.edit_note_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editTitle = (EditText) findViewById(R.id.editTitle);
        editContent = (EditText) findViewById(R.id.editContent);

        if (!getIntent().getStringExtra(Constants.EXTRA_NOTE).isEmpty()){
            String noteId=getIntent().getStringExtra(Constants.EXTRA_NOTE);
            Realm realm = Realm.getInstance(this);
            note=realm.where(Note.class).equalTo("id",noteId).findFirst();
            editTitle.setText(note.getTitle());
            editContent.setText(note.getContent());
            selectedColor=note.getColor();
            Log.i(TAG, "onCreate: " + note.toString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.save_note:
                if (isNoteFormOk()) {
                    setNoteResult();
                    finish();
                } else validateNoteForm();
                return true;
            case R.id.color_note:
                setColor();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    private void setColor() {
        ColorPickerDialog colorcalendar = ColorPickerDialog.newInstance(
                R.string.color_picker_default_title,
                com.yurkiv.materialnotes.util.Utils.ColorUtils.colorChoice(this),
                selectedColor,
                5,
                com.yurkiv.materialnotes.util.Utils.isTablet(this)? ColorPickerDialog.SIZE_LARGE : ColorPickerDialog.SIZE_SMALL);

        //Implement listener to get selected color value
        colorcalendar.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener(){

            @Override
            public void onColorSelected(int color) {
                selectedColor =color;
            }

        });

        colorcalendar.show(getFragmentManager(),"cal");
    }

    private void setNoteResult() {
        String title=editTitle.getText().toString().trim();
        String content=editContent.getText().toString().trim();
        ArrayList<Hashtag> hashtags= Utility.getHashtagsFromContent(title + " " + content);

        Realm realm = Realm.getInstance(getApplicationContext());
        realm.beginTransaction();
        if (!getIntent().getStringExtra(Constants.EXTRA_NOTE).isEmpty()){
            String noteId=getIntent().getStringExtra(Constants.EXTRA_NOTE);
            note=realm.where(Note.class).equalTo("id",noteId).findFirst();
        } else {
            note=realm.createObject(Note.class);
            note.setId(UUID.randomUUID().toString());
        }
        note.setTitle(title);
        note.setContent(content);
        note.setUpdatedAt(new Date());
        note.setColor(selectedColor);

        note.getHashtags().where().findAll().clear();

        for (Hashtag hashtag:hashtags){
            note.getHashtags().add(realm.copyToRealm(hashtag));
        }
        realm.commitTransaction();

        Log.i(TAG, "setNoteResult: " + note.toString());
        Toast.makeText(EditNoteActivity.this, "The note has been saved.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        if (isNoteFormOk()) {
            if ((editTitle.getText().toString().equals(note.getTitle()))
                    && (editContent.getText().toString().equals(note.getContent()))){
                finish();
            } else {
                AlertDialog.Builder builder=new AlertDialog.Builder(EditNoteActivity.this);
                builder.setMessage("Do you want to save the note?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                setNoteResult();
                                finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setCancelable(true);
                AlertDialog alertDialog=builder.create();
                alertDialog.show();
            }
        } else {
            finish();
        }
    }

    private void validateNoteForm() {
        String msg=null;
        if (isNullOrBlank(editTitle.getText().toString())){
            msg=getString(R.string.title_required);
        }
        if (msg!=null){
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
        }
    }

    public static boolean isNullOrBlank(String str) {
        return str == null || str.trim().length() == 0;
    }

    private boolean isNoteFormOk() {
        String title=editTitle.getText().toString();
        return !(title==null || title.trim().length()==0);
    }
}
