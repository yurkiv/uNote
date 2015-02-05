package com.yurkiv.materialnotes.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.yurkiv.materialnotes.R;
import com.yurkiv.materialnotes.data.Note;
import com.yurkiv.materialnotes.util.RequestResultCode;

import java.text.DateFormat;

public class ViewNoteActivity extends ActionBarActivity {

    private static final String EXTRA_NOTE = "EXTRA_NOTE";

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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textTitle = (TextView) findViewById(R.id.textTitle);
        textUpdated = (TextView) findViewById(R.id.textUpdated);
        textContent = (TextView) findViewById(R.id.textContent);

        note = (Note) getIntent().getSerializableExtra(EXTRA_NOTE);

        textTitle.setText(note.getTitle());
        textContent.setText(note.getContent());
        textUpdated.setText(dateFormat.format(note.getUpdatedAt())+", "+timeFormat.format(note.getUpdatedAt()));

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("ViewNoteActivity", "onActivityResult");
        if (requestCode== RequestResultCode.REQUEST_CODE_EDIT_NOTE){
            if (resultCode==RESULT_OK){
                Log.d("ViewNoteActivity", "RESULT_OK");
                note = (Note) data.getSerializableExtra(EXTRA_NOTE);
                textTitle.setText(note.getTitle());
                textContent.setText(note.getContent());
                
            }
        }

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

    @Override
    public void onBackPressed() {
        Intent intentHome = new Intent(this, MainActivity.class);
        intentHome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intentHome.putExtra(EXTRA_NOTE, note);
        //startActivity(intentHome);
        setResult(RESULT_OK, intentHome);
        //setResult(RESULT_CANCELED, new Intent());
        finish();
    }

    private void editNote(){
        Intent intent=new Intent(ViewNoteActivity.this, EditNoteActivity.class);
        intent.putExtra(EXTRA_NOTE, note);
        startActivityForResult(intent, RequestResultCode.REQUEST_CODE_EDIT_NOTE);
    }

    private void deleteNote() {
        AlertDialog.Builder builder=new AlertDialog.Builder(ViewNoteActivity.this);
        builder.setMessage("Do you want to delete the note?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intentHome = new Intent(ViewNoteActivity.this, MainActivity.class);
                        intentHome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intentHome.putExtra(EXTRA_NOTE, note);
                        setResult(RequestResultCode.RESULT_CODE_DELETE_NOTE, intentHome);
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
