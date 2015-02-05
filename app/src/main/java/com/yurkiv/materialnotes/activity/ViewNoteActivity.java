package com.yurkiv.materialnotes.activity;

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
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
        DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
        textUpdated.setText(dateFormat.format(note.getUpdatedAt())+" , "+timeFormat.format(note.getUpdatedAt()));

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
                textUpdated.setText(note.getUpdatedAt().toString());
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
        Log.d("ViewNoteActivity", note.toString());
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
        Intent intentHome = new Intent(this, MainActivity.class);
        intentHome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intentHome.putExtra(EXTRA_NOTE, note);
        //startActivity(intentHome);
        setResult(RequestResultCode.RESULT_CODE_DELETE_NOTE, intentHome);
        //setResult(RESULT_CANCELED, new Intent());
        finish();
    }
}
