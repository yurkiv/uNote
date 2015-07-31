package com.yurkiv.materialnotes.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.yurkiv.materialnotes.R;
import com.yurkiv.materialnotes.model.Hashtag;
import com.yurkiv.materialnotes.model.Note;
import com.yurkiv.materialnotes.util.Constants;

import java.text.DateFormat;

import io.realm.Realm;
import io.realm.RealmResults;

public class ViewNoteActivity extends ActionBarActivity {

    private static final String TAG = ViewNoteActivity.class.getSimpleName();

    private Toolbar toolbar;

    private TextView textTitle;
    private TextView textUpdated;
    private TextView textContent;

    private Note note;

    private static DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
    private static DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_note);

        toolbar = (Toolbar) findViewById(R.id.view_note_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textTitle = (TextView) findViewById(R.id.textTitle);
        textUpdated = (TextView) findViewById(R.id.textUpdated);
        textContent = (TextView) findViewById(R.id.textContent);

        if (!getIntent().getStringExtra(Constants.EXTRA_NOTE).isEmpty()){
            String noteId=getIntent().getStringExtra(Constants.EXTRA_NOTE);
            Realm realm = Realm.getInstance(this);
            note=realm.where(Note.class).equalTo("id",noteId).findFirst();
            textTitle.setText(note.getTitle());
            textContent.setText(note.getContent());
            textUpdated.setText(dateFormat.format(note.getUpdatedAt())+", "+timeFormat.format(note.getUpdatedAt()));
            Log.i(TAG, "onCreate: "+note.toString());
        }

        textTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editNote();
            }
        });

        textContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editNote();
            }
        });
    }

    @Override
    protected void onRestart() {
        if (!getIntent().getStringExtra(Constants.EXTRA_NOTE).isEmpty()){
            String noteId=getIntent().getStringExtra(Constants.EXTRA_NOTE);
            Realm realm = Realm.getInstance(this);
            note=realm.where(Note.class).equalTo("id",noteId).findFirst();
            textTitle.setText(note.getTitle());
            textContent.setText(note.getContent());
            textUpdated.setText(dateFormat.format(note.getUpdatedAt()) + ", " + timeFormat.format(note.getUpdatedAt()));
            Log.i(TAG, "onRestart: "+note.toString());
        }
        super.onRestart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.edit_note:
                editNote();
                return true;
            case R.id.delete_note:
                deleteNote();
                return true;
            case R.id.share_note:
                shareNote();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    private void editNote(){
        Intent intent=new Intent(ViewNoteActivity.this, EditNoteActivity.class);
        intent.putExtra(Constants.EXTRA_NOTE, note.getId());
        startActivity(intent);
    }

    private void deleteNote() {
        AlertDialog.Builder builder=new AlertDialog.Builder(ViewNoteActivity.this);
        builder.setMessage("Do you want to delete the note?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Realm realm = Realm.getInstance(getApplicationContext());
                        Note toEdit = realm.where(Note.class)
                                .equalTo("id", getIntent().getStringExtra(Constants.EXTRA_NOTE)).findFirst();
                        realm.beginTransaction();
                        note.getHashtags().where().findAll().clear();
                        toEdit.removeFromRealm();
                        realm.commitTransaction();
                        Toast.makeText(ViewNoteActivity.this, "The note has been deleted.", Toast.LENGTH_LONG).show();
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setCancelable(true);
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }

    private void shareNote(){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, note.getTitle()+"\n"+note.getContent());
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }
}
