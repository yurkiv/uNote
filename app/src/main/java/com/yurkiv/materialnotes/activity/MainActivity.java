package com.yurkiv.materialnotes.activity;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yurkiv.materialnotes.R;
import com.yurkiv.materialnotes.data.DatabaseHelper;
import com.yurkiv.materialnotes.data.Note;
import com.yurkiv.materialnotes.data.NotesAdapter;
import com.yurkiv.materialnotes.util.RequestResultCode;

import java.util.Date;
import java.util.List;


public class MainActivity extends ActionBarActivity implements SearchView.OnQueryTextListener {

    private static final String EXTRA_NOTE = "EXTRA_NOTE";
    private static final int VIEW_NOTE_RESULT_CODE = 5;


    private TextView textEmpty;
    private ListView listNotes;

    private List<Note> notesData;
    private NotesAdapter notesAdapter;
    private SearchView searchView;
    private MenuItem searchMenuItem;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper=new DatabaseHelper(getApplicationContext());

        textEmpty = (TextView) findViewById(R.id.textEmpty);
        listNotes = (ListView) findViewById(R.id.listNotes);

        //initSimpleNote();
        setupNotesAdapter();
        updateView();

        listNotes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Note note=notesData.get(position);
                Intent intent=new Intent(MainActivity.this, ViewNoteActivity.class);
                intent.putExtra(EXTRA_NOTE, note);
                startActivityForResult(intent, RequestResultCode.REQUEST_CODE_VIEW_NOTE);
            }
        });

    }

    private void initSimpleNote(){
        for (int i = 0; i < 10; i++) {
            Note note=new Note();
            note.setTitle("Note " + i);
            note.setContent("Content " + i);
            note.setUpdatedAt(new Date());
            databaseHelper.createNote(note);
        }
    }

    private void setupNotesAdapter(){
        notesData=databaseHelper.getAllNotes();
        notesAdapter=new NotesAdapter(notesData);
        listNotes.setAdapter(notesAdapter);
    }

    private void updateView(){
        if (notesData.isEmpty()){
            listNotes.setVisibility(View.GONE);
            textEmpty.setVisibility(View.VISIBLE);
        } else {
            listNotes.setVisibility(View.VISIBLE);
            textEmpty.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==RequestResultCode.REQUEST_CODE_VIEW_NOTE){
            if (resultCode==RESULT_OK){
                updateNote(data);
            } else if (resultCode==RequestResultCode.RESULT_CODE_DELETE_NOTE){
                deleteNote(data);
            }
        }
        if (requestCode==RequestResultCode.REQUEST_CODE_ADD_NOTE){
            if (resultCode==RESULT_OK){
                addNote(data);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void addNote(Intent data){
        Note note= (Note) data.getSerializableExtra(EXTRA_NOTE);
        long noteId=databaseHelper.createNote(note);
        note.setId(noteId);
        notesData.add(note);
        updateView();
        notesAdapter.notifyDataSetChanged();
    }

    private void updateNote(Intent data) {
        Note updatedNote= (Note) data.getSerializableExtra(EXTRA_NOTE);
        databaseHelper.updateNote(updatedNote);
        for (Note note: notesData){
            if (note.getId().equals(updatedNote.getId())){
                note.setTitle(updatedNote.getTitle());
                note.setContent(updatedNote.getContent());
                note.setUpdatedAt(updatedNote.getUpdatedAt());
            }
        }
        notesAdapter.notifyDataSetChanged();
    }

    private void deleteNote(Intent data) {
        Note deletedNote= (Note) data.getSerializableExtra(EXTRA_NOTE);
        databaseHelper.deleteNote(deletedNote);
        notesData.remove(deletedNote);
        updateView();
        notesAdapter.notifyDataSetChanged();
        Toast.makeText(MainActivity.this, "The note has been deleted.", Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        SearchManager searchManager= (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchMenuItem=menu.findItem(R.id.search_note);


        searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.add_note:
                Intent intent=new Intent(MainActivity.this, EditNoteActivity.class);
                startActivityForResult(intent, RequestResultCode.REQUEST_CODE_ADD_NOTE);
                return true;
            case R.id.sort_note:

                return true;
            case R.id.settings:

                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        notesAdapter.getFilter().filter(s);
        return true;
    }
}
