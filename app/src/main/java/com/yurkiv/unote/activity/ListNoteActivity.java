package com.yurkiv.unote.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;
import com.yurkiv.unote.R;
import com.yurkiv.unote.adapter.NotesAdapter;
import com.yurkiv.unote.adapter.NotesAdapterOld;
import com.yurkiv.unote.callbacks.NavigationDrawerCallbacks;
import com.yurkiv.unote.fragment.NavigationDrawerFragment;
import com.yurkiv.unote.model.Hashtag;
import com.yurkiv.unote.model.Mention;
import com.yurkiv.unote.model.Note;
import com.yurkiv.unote.util.Constants;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.realm.Realm;


public class ListNoteActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, NavigationDrawerCallbacks {

    private static final String TAG = ListNoteActivity.class.getSimpleName();

    @InjectView(R.id.note_list_toolbar) protected Toolbar toolbar;
    @InjectView(R.id.textEmpty) protected TextView textEmpty;
    @InjectView(R.id.listNotes) protected RecyclerView rvNotes;
    @InjectView(R.id.fab) protected FloatingActionButton fab;

    private NavigationDrawerFragment navigationDrawerFragment;
    private List<Note> notesData;
    private List<Hashtag> hashtags;
    private List<Mention> mentions;
    private NotesAdapter notesAdapter;
    private SearchView searchView;
    private MenuItem searchMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notelist);
        ButterKnife.inject(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        navigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.fragment_drawer);
        navigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), toolbar);

        rvNotes.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvNotes.setLayoutManager(mLayoutManager);
        fab.attachToRecyclerView(rvNotes);

        //initSimpleNote();
        setupNotesAdapter();
        updateView();

        notesAdapter.setOnItemClickListener(new NotesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Note note=notesData.get(position);
                Intent intent=new Intent(ListNoteActivity.this, ViewNoteActivity.class);
                intent.putExtra(Constants.EXTRA_NOTE, note.getId());
                startActivity(intent);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ListNoteActivity.this, EditNoteActivity.class);
                intent.putExtra(Constants.EXTRA_NOTE, "");
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onRestart() {
        updateData();
        super.onRestart();
    }

    private void initSimpleNote(){
        for (int i = 0; i < 10; i++) {
            Realm realm = Realm.getInstance(this);
            realm.beginTransaction();
            Note note=realm.createObject(Note.class);
            note.setTitle("Note " + i);
            note.setContent("Content " + i);
            note.setUpdatedAt(new Date());
            realm.commitTransaction();
        }
    }

    private void setupNotesAdapter(){
        Log.i(TAG, "setupNotesAdapter");
        Realm realm = Realm.getInstance(this);
        notesData = realm.where(Note.class).findAll();
        notesAdapter=new NotesAdapter(this);
        notesAdapter.setData(notesData);
        rvNotes.setAdapter(notesAdapter);
        Log.i(TAG, notesData.toString());
        hashtags=realm.where(Hashtag.class).findAll();
        mentions=realm.where(Mention.class).findAll();
        navigationDrawerFragment.updateNavigationDrawer(hashtags, mentions);
        Log.i(TAG, hashtags.toString());
        Log.i(TAG, mentions.toString());
    }

    private void updateView(){
        if (notesData.size()==0){
            Log.i(TAG, "isEmpty");
            rvNotes.setVisibility(View.GONE);
            textEmpty.setVisibility(View.VISIBLE);
        } else {
            rvNotes.setVisibility(View.VISIBLE);
            textEmpty.setVisibility(View.GONE);
        }
    }

    private void updateData() {
        Log.i(TAG, "updateData");
        Realm realm = Realm.getInstance(this);
        notesData = realm.where(Note.class).findAll();
        notesAdapter.setData(notesData);
        Log.i(TAG, notesData.toString());
        notesAdapter.notifyDataSetChanged();
        //TODO: Only unique
        hashtags=realm.where(Hashtag.class).findAll();
        mentions=realm.where(Mention.class).findAll();
        Log.i(TAG, hashtags.toString());
        Log.i(TAG, mentions.toString());
        navigationDrawerFragment.updateNavigationDrawer(hashtags, mentions);
        updateView();
    }

    private void sortList(MenuItem item, Comparator<Note> noteComparator) {
        Collections.sort(notesData, noteComparator);
        notesAdapter.notifyDataSetChanged();
        if (item.isChecked()) item.setChecked(false);
        else item.setChecked(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_note_list, menu);
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
            case R.id.sort_by_title:
                sortList(item, NotesAdapterOld.titleComparator);
                return true;
            case R.id.newest_first:
                sortList(item, NotesAdapterOld.newestFirstComparator);
                return true;
            case R.id.oldest_first:
                sortList(item, NotesAdapterOld.oldestFirstComparator);
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (navigationDrawerFragment.isDrawerOpen())
            navigationDrawerFragment.closeDrawer();
        else
            super.onBackPressed();
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
//        notesAdapter.getFilter().filter(s);
        return true;
    }

    @Override
    public void onHashtagOrMentionItemSelected(String query) {
        Log.i(TAG, query);
        Realm realm = Realm.getInstance(this);
        notesData = realm.where(Note.class)
                .contains("title", query)
                .or()
                .contains("content", query)
                .findAll();
        Log.i(TAG, notesData.toString());
        notesAdapter.setData(notesData);
        notesAdapter.notifyDataSetChanged();
        updateView();
    }

    @Override
    public void onAllNotesSelected() {
        Realm realm = Realm.getInstance(this);
        notesData = realm.where(Note.class).findAll();
        Log.i(TAG, notesData.toString());
        notesAdapter.setData(notesData);
        notesAdapter.notifyDataSetChanged();
        updateView();
    }
}
