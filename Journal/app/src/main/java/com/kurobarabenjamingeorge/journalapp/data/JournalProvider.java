package com.kurobarabenjamingeorge.journalapp.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class JournalProvider extends ContentProvider {

    private static final String AUTHORITY = "com.kurobarabenjamingeorge.journalapp.data.journalprovider";
    private static final String BASE_PATH = "notes";
    public static final Uri CONTENT_URI =
            Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH );

    // Integer constants to identify the operation i.e operation on the whole table or on a single item
    private static final int NOTES = 100;
    private static final int NOTES_ID = 200;

    //Creating a URI matcher
    private static final UriMatcher uriMatcher =
            new UriMatcher(UriMatcher.NO_MATCH);

    public static final String CONTENT_ITEM_TYPE = "Note";

    static {
        uriMatcher.addURI(AUTHORITY, BASE_PATH, NOTES);
        uriMatcher.addURI(AUTHORITY, BASE_PATH +  "/#", NOTES_ID);
    }

    private SQLiteDatabase database;

    @Override
    public boolean onCreate() {

        NoteDBHelper helper = new NoteDBHelper(getContext());
        database = helper.getWritableDatabase();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        if (uriMatcher.match(uri) == NOTES_ID) {
            //If operation is to be performed on a single item
            selection = NoteDBHelper.NOTE_ID + "=" + uri.getLastPathSegment();
        }

        return database.query(NoteDBHelper.TABLE_NOTES, NoteDBHelper.ALL_COLUMNS,
                selection, null, null, null,
                NoteDBHelper.NOTE_CREATED + " DESC");
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id = database.insert(NoteDBHelper.TABLE_NOTES,
                null, values);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return database.delete(NoteDBHelper.TABLE_NOTES, selection, selectionArgs);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return database.update(NoteDBHelper.TABLE_NOTES,
                values, selection, selectionArgs);
    }
}
