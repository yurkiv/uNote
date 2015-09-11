package com.yurkiv.unote.activity;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yurkiv.unote.R;
import com.yurkiv.unote.model.Hashtag;
import com.yurkiv.unote.model.Mention;
import com.yurkiv.unote.model.Note;
import com.yurkiv.unote.model.NoteBackup;
import com.yurkiv.unote.util.Utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.realm.Realm;

public class BackupRestoreActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener  {

    private static final String TAG = "BackupRestoreActivity";
    protected static final int REQUEST_CODE_RESOLUTION = 1;

    @InjectView(R.id.toolbar_restore) protected Toolbar toolbar;
    @InjectView(R.id.btnBackup) Button btnBackup;
    @InjectView(R.id.btnRestore) Button btnRestore;
    @InjectView(R.id.chbAllow) CheckBox chbAllow;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_restore);
        ButterKnife.inject(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.title_activity_backup_restore));


        btnBackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getGoogleApiClient() != null) {
                    Realm realm = Realm.getInstance(BackupRestoreActivity.this);
                    syncWithDrive(realm.where(Note.class).findAll());
                } else {
                    BackupRestoreActivity.this.onResume();
                }
            }
        });

        btnRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getGoogleApiClient()!=null) {
                    restoreFromDrive();
                } else {
                    BackupRestoreActivity.this.onResume();
                }
            }
        });

        chbAllow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                btnRestore.setEnabled(b);
            }
        });

    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "GoogleApiClient connected");
        btnBackup.setEnabled(true);
        chbAllow.setEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addScope(Drive.SCOPE_APPFOLDER) // required for App Folder sample
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        mGoogleApiClient.connect();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    private void restoreFromDrive(){
        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, "uNote"))
                .addFilter(Filters.eq(SearchableField.TRASHED, false))
                .build();

        Drive.DriveApi.getAppFolder(getGoogleApiClient()).queryChildren(getGoogleApiClient(), query).setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
            @Override
            public void onResult(DriveApi.MetadataBufferResult result) {
                if (!result.getStatus().isSuccess()) {
                    showMessage("Problem while retrieving files");
                    return;
                }
                if (result.getMetadataBuffer().getCount() != 0) {
                    Log.d(TAG, "file is on drive");
                    getFromUnoteFile(result.getMetadataBuffer().get(0).getDriveId());
                } else {
                    Log.d(TAG, "crate first update");
                }
            }
        });
    }

    private void getFromUnoteFile(DriveId driveId) {
        DriveFile driveFile=Drive.DriveApi.getFile(getGoogleApiClient(), driveId);
        driveFile.open(getGoogleApiClient(), DriveFile.MODE_READ_ONLY, null).setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
            @Override
            public void onResult(DriveApi.DriveContentsResult result) {
                if (!result.getStatus().isSuccess()) {
                    showMessage("Problem while retrieving files");
                    return;
                }
                DriveContents contents = result.getDriveContents();
                BufferedReader reader = new BufferedReader(new InputStreamReader(contents.getInputStream()));
                StringBuilder builder = new StringBuilder();
                String line;
                try {
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                } catch (IOException e) {

                }
                String contentsAsString = builder.toString();
                Log.d(TAG, "restore: " + contentsAsString);

                writeToRealm(contentsAsString);
            }
        });


    }

    private void writeToRealm(String contentsAsString) {
        Type listType = new TypeToken<List<NoteBackup>>() {}.getType();
        List<NoteBackup> noteBackups=new Gson().fromJson(contentsAsString, listType);
        Log.i(TAG, noteBackups.toString());

        Realm realm = Realm.getInstance(getApplicationContext());

        realm.beginTransaction();
        realm.clear(Note.class);
        realm.clear(Hashtag.class);
        realm.clear(Mention.class);
        realm.commitTransaction();

        for (NoteBackup noteBackup : noteBackups){

            String title=noteBackup.getTitle();
            String content=noteBackup.getContent();
            ArrayList<Hashtag> hashtags= Utility.getHashtagsFromContent(title + " " + content);
            ArrayList<Mention> mentions=Utility.getMentionsFromContent(title + " " + content);

            realm.beginTransaction();
            Note note=realm.createObject(Note.class);
            note.setId(UUID.randomUUID().toString());
            note.setTitle(title);
            note.setContent(content);
            note.setUpdatedAt(noteBackup.getUpdatedAt());
            note.setColor(noteBackup.getColor());
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
        }
        showMessage("Notes is restored");
    }


    protected void syncWithDrive(List<Note> notesData){
        List<NoteBackup> noteBackups=new ArrayList<>();
        for (Note note : notesData){
            noteBackups.add(new NoteBackup(note.getTitle(), note.getContent(), note.getUpdatedAt(), note.getColor()));
        }

        final String notes=new Gson().toJson(noteBackups);

        Log.d(TAG, notes);

        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, "uNote"))
                .addFilter(Filters.eq(SearchableField.TRASHED, false))
                .build();

        Drive.DriveApi.getAppFolder(getGoogleApiClient()).queryChildren(getGoogleApiClient(), query).setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
            @Override
            public void onResult(DriveApi.MetadataBufferResult result) {
                if (!result.getStatus().isSuccess()) {
                    showMessage("Problem while retrieving files");
                    return;
                }
                if (result.getMetadataBuffer().getCount() != 0) {
                    Log.d(TAG, "file is on drive");
                    updateUnoteFile(result.getMetadataBuffer().get(0).getDriveId(), notes);
                } else {
                    Log.d(TAG, "crate first update");
                    writeUnoteFile(notes);
                }
            }
        });
    }

    private void updateUnoteFile(DriveId driveId, final String notes) {
        DriveFile driveFile=Drive.DriveApi.getFile(getGoogleApiClient(), driveId);
        driveFile.delete(getGoogleApiClient()).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                if (!status.isSuccess()) {
                    showMessage("Problem while retrieving files");
                    return;
                }
                writeUnoteFile(notes);
            }
        });
    }

    private void writeUnoteFile(final String notes) {
        Drive.DriveApi.newDriveContents(getGoogleApiClient()).setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
            @Override
            public void onResult(DriveApi.DriveContentsResult result) {
                if (!result.getStatus().isSuccess()) {
                    showMessage("Error while trying to create new file contents");
                    return;
                }
                MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                        .setTitle("uNote")
                        .setMimeType("text/plain")
                        .setStarred(true).build();
                Drive.DriveApi.getAppFolder(getGoogleApiClient()).createFile(getGoogleApiClient(), changeSet, string2Cont(result.getDriveContents(), notes))
                        .setResultCallback(new ResultCallback<DriveFolder.DriveFileResult>() {
                            @Override
                            public void onResult(DriveFolder.DriveFileResult result) {
                                if (!result.getStatus().isSuccess()) {
                                    showMessage("Error while trying to create the file");
                                    return;
                                }
                                showMessage("Notes Backup created on file uNote.txt");
                            }
                        });
            }
        });
    }

    private DriveContents string2Cont(DriveContents driveContents, String s) {
        OutputStream oos = driveContents.getOutputStream();
        Writer writer = new OutputStreamWriter(oos);
        try {
            writer.write(s);
            writer.close();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }

        finally {
            try {
                oos.close();
            } catch (Exception ignore) { }
        }
        return driveContents;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_RESOLUTION && resultCode == RESULT_OK) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onPause() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onPause();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "GoogleApiClient connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "GoogleApiClient connection failed: " + result.toString());
        if (!result.hasResolution()) {
            // show the localized error dialog.
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, 0).show();
            return;
        }
        try {
            result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, "Exception while starting resolution activity", e);
        }
    }

    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

}
