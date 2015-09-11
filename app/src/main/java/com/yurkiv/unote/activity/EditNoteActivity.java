package com.yurkiv.unote.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.yurkiv.unote.R;
import com.yurkiv.unote.colorpicker.ColorPickerDialog;
import com.yurkiv.unote.colorpicker.ColorPickerSwatch;
import com.yurkiv.unote.model.Hashtag;
import com.yurkiv.unote.model.Mention;
import com.yurkiv.unote.model.Note;
import com.yurkiv.unote.util.Constants;
import com.yurkiv.unote.util.Utility;
import com.yurkiv.unote.util.Utils;
import com.yurkiv.unote.view.RevealBackgroundView;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.realm.Realm;

public class EditNoteActivity extends AppCompatActivity implements RevealBackgroundView.OnStateChangeListener  {

    private static final String TAG = EditNoteActivity.class.getSimpleName();

    @InjectView(R.id.toolbarEditNote) protected Toolbar toolbar;
    @InjectView(R.id.llEditNote) protected LinearLayout llEditNote;
    @InjectView(R.id.editTitle) protected EditText editTitle;
    @InjectView(R.id.editContent) protected EditText editContent;
    @InjectView(R.id.inputTitle) protected TextInputLayout inputTitle;
    @InjectView(R.id.revealBackground) protected RevealBackgroundView vRevealBackground;

    private Note note;
    private static ColorGenerator generator = ColorGenerator.DEFAULT;
    private int selectedColor=generator.getRandomColor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        ButterKnife.inject(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        int actionbarSize = Utils.getActionBarSize(this);
        toolbar.setTranslationY(-actionbarSize);
        int height = Utils.getScreenHeight(this);
        llEditNote.setTranslationY(height);

        Realm realm = Realm.getInstance(this);
        if (!getIntent().getStringExtra(Constants.EXTRA_NOTE).isEmpty()){
            String noteId=getIntent().getStringExtra(Constants.EXTRA_NOTE);
            note=realm.where(Note.class).equalTo("id", noteId).findFirst();
            editTitle.setText(note.getTitle());
            editContent.setText(note.getContent());
            selectedColor=note.getColor();
            Log.i(TAG, "Note load from Realm: " + note.toString());
        }
        setupRevealBackground(savedInstanceState);
    }

    private void setupRevealBackground(Bundle savedInstanceState) {
        vRevealBackground.setOnStateChangeListener(this);
        if (savedInstanceState == null) {
            final int[] startingLocation = getIntent().getIntArrayExtra(Constants.EXTRA_REVEAL_START_LOCATION);
            vRevealBackground.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    vRevealBackground.getViewTreeObserver().removeOnPreDrawListener(this);
                    vRevealBackground.startFromLocation(startingLocation);
                    return true;
                }
            });
        } else {
            vRevealBackground.setToFinishedFrame();
        }
    }

    @Override
    public void onStateChange(int state) {
        if (RevealBackgroundView.STATE_FINISHED == state) {
            llEditNote.setVisibility(View.VISIBLE);
            startIntroAnimation();
        } else {
            llEditNote.setVisibility(View.INVISIBLE);
        }
    }

    private void startIntroAnimation(){
        toolbar.animate().translationY(0).setStartDelay(300).setDuration(300).start();
        llEditNote.animate().translationY(0).setStartDelay(300).setDuration(600).start();
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
                if (validateNoteForm()) {
                    setNoteResult();
                    finish();
                }
                return true;
            case R.id.color_note:
                setColor();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    private void setNoteResult() {
        String title=editTitle.getText().toString().trim();
        String content=editContent.getText().toString().trim();
        ArrayList<Hashtag> hashtags=Utility.getHashtagsFromContent(title + " " + content);
        ArrayList<Mention> mentions=Utility.getMentionsFromContent(title + " " + content);

        Realm realm = Realm.getInstance(getApplicationContext());

        if (getIntent().getStringExtra(Constants.EXTRA_NOTE).isEmpty()){
            realm.beginTransaction();
            note=realm.createObject(Note.class);
            note.setId(UUID.randomUUID().toString());
            realm.commitTransaction();
        }

        realm.beginTransaction();
        note.setTitle(title);
        note.setContent(content);
        note.setUpdatedAt(new Date());
        note.setColor(selectedColor);
        note.getHashtags().where().findAll().clear();
        for (Hashtag hashtag:hashtags){
            note.getHashtags().add(realm.copyToRealm(hashtag));
        }
        note.getMentions().where().findAll().clear();
        for (Mention mention:mentions){
            note.getMentions().add(realm.copyToRealm(mention));
        }
        realm.commitTransaction();

        Log.i(TAG, "Note has been saved to Realm: " + note.toString());
        Toast.makeText(EditNoteActivity.this, "The note has been saved.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        if (validateNoteForm()) {
            showOnBackDialog();
        } else{
            finish();
        }
    }

    private boolean validateNoteForm() {
        String title=editTitle.getText().toString();
        String content=editContent.getText().toString();
        if (((title==null || title.trim().length()==0) && (content!=null || content.trim().length()!=0))){
            inputTitle.setError(getString(R.string.title_required));
            editTitle.requestFocus();
            return false;
        } else return true;
    }

    private void setColor() {
        ColorPickerDialog colorcalendar = ColorPickerDialog.newInstance(
                R.string.color_picker_default_title,
                com.yurkiv.unote.util.Utils.ColorUtils.colorChoice(this),
                selectedColor,
                4,
                com.yurkiv.unote.util.Utils.isTablet(this) ? ColorPickerDialog.SIZE_LARGE : ColorPickerDialog.SIZE_SMALL);
        colorcalendar.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener(){
            @Override
            public void onColorSelected(int color) {
                selectedColor=color;
            }
        });
        colorcalendar.show(getFragmentManager(),"cal");
    }

    private void showOnBackDialog() {
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
}
