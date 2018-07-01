package com.kurobarabenjamingeorge.journalapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.kurobarabenjamingeorge.journalapp.data.JournalProvider;
import com.kurobarabenjamingeorge.journalapp.data.NoteDBHelper;

public class NoteListActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>{

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private static final int EDITOR_REQUEST_CODE = 100;
    private CursorAdapter cursorAdapter;
    View mEmptyView;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    private void signOut(){
        mAuth.signOut();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                //Checks if the user is signed in, if not redirects user to the signed in page
                if(mAuth.getCurrentUser() == null){
                    Intent signOutIntent = new Intent(NoteListActivity.this, MainActivity.class);
                    signOutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(signOutIntent);
                }
            }
        };

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditorForNewNote();
            }
        });

        cursorAdapter = new NotesCursorAdapter(this, null, 0);

        ListView listOfNotes = (ListView) findViewById(android.R.id.list);
        listOfNotes.setAdapter(cursorAdapter);

        listOfNotes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(NoteListActivity.this, NoteEditorActivity.class);
                Uri uri = Uri.parse(JournalProvider.CONTENT_URI + "/" + id);
                intent.putExtra(JournalProvider.CONTENT_ITEM_TYPE, uri);
                startActivityForResult(intent, EDITOR_REQUEST_CODE);
            }
        });

        mEmptyView = findViewById(R.id.empty_view);
        listOfNotes.setEmptyView(mEmptyView);

        getLoaderManager().initLoader(0, null, this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Creates an overflow menu
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Handles menu item click
        int id = item.getItemId();

        switch (id) {
            case R.id.action_new_note:
                openEditorForNewNote();
                break;
            case R.id.action_delete_all:
                deleteAllNotes();
                break;
            case R.id.action_about:
                startActivity(new Intent(NoteListActivity.this, AboutActivity.class));
                break;
            case R.id.action_logout:
                signOut();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteAllNotes() {

        DialogInterface.OnClickListener dialogClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        if (button == DialogInterface.BUTTON_POSITIVE) {
                            getContentResolver().delete(
                                    JournalProvider.CONTENT_URI, null, null
                            );
                            restartLoader();

                            Toast.makeText(NoteListActivity.this, R.string.deleted_all_notes, Toast.LENGTH_SHORT).show();
                        }
                    }
                };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_all_notes_alert_message)
                .setTitle(R.string.delete_all_notes_alert_title)
                .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                .show();
    }


    private void restartLoader() {
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, JournalProvider.CONTENT_URI,
                null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }

    public void openEditorForNewNote() {
        Intent intent = new Intent(this, NoteEditorActivity.class);
        startActivityForResult(intent, EDITOR_REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDITOR_REQUEST_CODE && resultCode == RESULT_OK) {
            restartLoader();
        }
    }

    @Override
    public void onBackPressed() {
        Intent quitIntent = new Intent(getApplicationContext(), MainActivity.class);
        quitIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        quitIntent.putExtra("EXIT", true);
        startActivity(quitIntent);
        //super.onBackPressed();
        //finish();
    }

    public void hideWelcomeCard(View view) {
        mEmptyView.setVisibility(View.INVISIBLE);
        mEmptyView.setEnabled(false);

    }
}
