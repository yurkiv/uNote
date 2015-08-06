package com.yurkiv.unote.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.yurkiv.unote.R;
import com.yurkiv.unote.model.Note;
import com.yurkiv.unote.util.Constants;

import java.text.DateFormat;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.realm.Realm;

public class ViewNoteActivity extends AppCompatActivity {

    private static final String TAG = ViewNoteActivity.class.getSimpleName();

    @InjectView(R.id.view_note_toolbar) protected Toolbar toolbar;
    @InjectView(R.id.textTitle) protected TextView textTitle;
    @InjectView(R.id.textUpdated) protected TextView textUpdated;
    @InjectView(R.id.textContent) protected TextView textContent;

    private Note note;

    private static DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
    private static DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_note);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        updateNoteView();

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
        updateNoteView();
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

    private void updateNoteView(){
        if (!getIntent().getStringExtra(Constants.EXTRA_NOTE).isEmpty()){
            String noteId=getIntent().getStringExtra(Constants.EXTRA_NOTE);
            Realm realm = Realm.getInstance(this);
            note=realm.where(Note.class).equalTo("id",noteId).findFirst();
            textTitle.setText(note.getTitle());
            textContent.setText(note.getContent());
            textUpdated.setText(dateFormat.format(note.getUpdatedAt())+", "+timeFormat.format(note.getUpdatedAt()));
            Log.i(TAG, "onCreate: "+note.toString());
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
                        toEdit.getHashtags().where().findAll().clear();
                        toEdit.getMentions().where().findAll().clear();
                        toEdit.removeFromRealm();
                        realm.commitTransaction();
                        Toast.makeText(ViewNoteActivity.this, "The note has been deleted.", Toast.LENGTH_LONG).show();
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
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
