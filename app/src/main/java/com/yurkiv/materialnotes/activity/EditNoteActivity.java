package com.yurkiv.materialnotes.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.yurkiv.materialnotes.R;
import com.yurkiv.materialnotes.model.Note;

import java.util.Date;

public class EditNoteActivity extends ActionBarActivity {

    private static final String EXTRA_NOTE = "EXTRA_NOTE";

    private Toolbar mToolbar;

    private EditText editTitle;
    private EditText editContent;
    private Note note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        mToolbar = (Toolbar) findViewById(R.id.edit_note_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editTitle = (EditText) findViewById(R.id.editTitle);
        editContent = (EditText) findViewById(R.id.editContent);

        note = (Note) getIntent().getSerializableExtra(EXTRA_NOTE);
        if (note!=null){
            editTitle.setText(note.getTitle());
            editContent.setText(note.getContent());
        } else {
            note=new Note();
            note.setUpdatedAt(new Date());
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
            default: return super.onOptionsItemSelected(item);
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

    private void setNoteResult() {
        note.setTitle(editTitle.getText().toString().trim());
        note.setContent(editContent.getText().toString().trim());
        note.setUpdatedAt(new Date());
        Intent intent=new Intent();
        intent.putExtra(EXTRA_NOTE, note);
        setResult(RESULT_OK, intent);
        Toast.makeText(EditNoteActivity.this, "The note has been saved.", Toast.LENGTH_LONG).show();
    }

    private boolean isNoteFormOk() {
        String title=editTitle.getText().toString();
        return !(title==null || title.trim().length()==0);
    }

    private void onBack(){
        if (isNoteFormOk()) {
            if ((editTitle.getText().toString().equals(note.getTitle()))
                    && (editContent.getText().toString().equals(note.getContent()))){
                setResult(RESULT_CANCELED, new Intent());
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
                                setResult(RESULT_CANCELED, new Intent());
                                finish();
                            }
                        })
                        .setCancelable(true);
                AlertDialog alertDialog=builder.create();
                alertDialog.show();
            }
        } else {
            setResult(RESULT_CANCELED, new Intent());
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        onBack();
    }

    public static boolean isNullOrBlank(String str) {
        return str == null || str.trim().length() == 0;
    }
}
