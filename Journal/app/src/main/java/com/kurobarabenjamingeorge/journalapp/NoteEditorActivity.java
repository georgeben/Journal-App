package com.kurobarabenjamingeorge.journalapp;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.kurobarabenjamingeorge.journalapp.data.JournalProvider;
import com.kurobarabenjamingeorge.journalapp.data.NoteDBHelper;

public class NoteEditorActivity extends AppCompatActivity {

    //Initializing member variables

    private String action;
    private EditText mEditor;
    private String noteFilter;
    private String oldText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);

        mEditor = (EditText) findViewById(R.id.editText);

        ActionBar actionBar = (ActionBar) getSupportActionBar();
        if(actionBar != null){
            //Displays the back navigation on the app bar
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();

        Uri uri = intent.getParcelableExtra(JournalProvider.CONTENT_ITEM_TYPE);

        if (uri == null) {
            action = Intent.ACTION_INSERT;
            setTitle(getString(R.string.new_note_app_bar_label));
        } else {
            action = Intent.ACTION_EDIT;
            noteFilter = NoteDBHelper.NOTE_ID + "=" + uri.getLastPathSegment();
            setTitle(getString(R.string.edit_note_app_bar_label));

            Cursor cursor = getContentResolver().query(uri,
                    NoteDBHelper.ALL_COLUMNS, noteFilter, null, null);
            cursor.moveToFirst();
            oldText = cursor.getString(cursor.getColumnIndex(NoteDBHelper.NOTE_TEXT));
            mEditor.setText(oldText);
            mEditor.requestFocus();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

            getMenuInflater().inflate(R.menu.menu_editor, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finishEditing();
                break;
            case R.id.action_delete:
                deleteNote();
                break;
            case R.id.action_save:
                if(TextUtils.isEmpty(mEditor.getText().toString().trim())){
                    Toast.makeText(this, R.string.save_error, Toast.LENGTH_SHORT).show();
                }else{
                    finishEditing();
                }
                break;
            case R.id.action_share_note:
                if(!(TextUtils.isEmpty(mEditor.getText().toString().trim()))){
                    shareNote();
                }else{
                    Toast.makeText(this, R.string.share_note_error, Toast.LENGTH_SHORT).show();
                }
                break;
        }

        return true;
    }

    private void shareNote() {
        String mimeType = "text/plain";
        String title = getString(R.string.share_note_chooser_title);
        String textToShare = mEditor.getText().toString().trim();

        ShareCompat.IntentBuilder
                /* The from method specifies the Context from which this share is coming from */
                .from(this)
                .setType(mimeType)
                .setChooserTitle(title)
                .setText(textToShare)
                .startChooser();

    }

    private void deleteNote() {
        if(TextUtils.isEmpty(mEditor.getText().toString().trim())){
            Toast.makeText(this, R.string.delete_note_error, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        DialogInterface.OnClickListener dialogClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        if (button == DialogInterface.BUTTON_POSITIVE) {
                            //Delete note
                            getContentResolver().delete(JournalProvider.CONTENT_URI,
                                    noteFilter, null);
                            Toast.makeText(getApplicationContext(), R.string.note_deleted,
                                    Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        }
                    }
                };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.confirm_dialog_text)
                .setTitle(R.string.delete_notes)
                .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                .show();

    }

    private void finishEditing() {
        String newText = mEditor.getText().toString().trim();

        switch (action) {
            case Intent.ACTION_INSERT:
                if (newText.length() == 0) {
                    setResult(RESULT_CANCELED);
                } else {
                    insertNote(newText);
                }
                break;
            case Intent.ACTION_EDIT:
                if (newText.length() == 0) {
                    deleteNote();
                } else if (oldText.equals(newText)) {
                    setResult(RESULT_CANCELED);
                } else {
                    updateNote(newText);
                }

        }
        finish();
    }

    private void updateNote(String noteText) {
        ContentValues values = new ContentValues();
        values.put(NoteDBHelper.NOTE_TEXT, noteText);
        getContentResolver().update(JournalProvider.CONTENT_URI, values, noteFilter, null);
        Toast.makeText(this, R.string.note_updated, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
    }

    private void insertNote(String noteText) {
        ContentValues values = new ContentValues();
        values.put(NoteDBHelper.NOTE_TEXT, noteText);
        getContentResolver().insert(JournalProvider.CONTENT_URI, values);
        setResult(RESULT_OK);
    }

    @Override
    public void onBackPressed() {
        if(!(TextUtils.isEmpty(mEditor.getText().toString().trim()))){
            DialogInterface.OnClickListener dialogClickListener =
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int button) {
                            if (button == DialogInterface.BUTTON_POSITIVE) {
                                //Update note
                                finishEditing();
                            }else if(button == DialogInterface.BUTTON_NEGATIVE){
                                finish();
                            }
                        }
                    };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.confirm_save_text)
                    .setTitle(R.string.save_note_dialog_title)
                    .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                    .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                    .show();
        }else{
            finish();
        }

    }
}
