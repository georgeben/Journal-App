package com.kurobarabenjamingeorge.journal.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by George Benjamin on 6/29/2018.
 */

public class NoteProvider extends ContentProvider {

    private static final String LOG_TAG = NoteProvider.class.getSimpleName();

    private  static final  int ALL_NOTES = 1;
    private static final int NOTE_ID = 2;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static{
        sUriMatcher.addURI(NoteContract.CONTENT_AUTHORITY, NoteContract.NOTE_PATH, ALL_NOTES);
        sUriMatcher.addURI(NoteContract.CONTENT_AUTHORITY, NoteContract.NOTE_PATH+ "/#", NOTE_ID);
    }

    private NoteDBHelper mDBHelper;

    @Override
    public boolean onCreate() {
        mDBHelper = new NoteDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        SQLiteDatabase database = mDBHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);

        switch(match){
            case ALL_NOTES:
                cursor = database.query(NoteContract.NoteEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            case NOTE_ID:
                selection = NoteContract.NoteEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                cursor = database.query(NoteContract.NoteEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        int match = sUriMatcher.match(uri);
        switch(match){
            case ALL_NOTES:
                return  insertNote(uri, contentValues);
            default:
                throw  new IllegalArgumentException("Insertion is not supported  for "+uri);

        }
    }

    private Uri insertNote(Uri uri, ContentValues contentValues) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        long id =  db.insert(NoteContract.NoteEntry.TABLE_NAME, null, contentValues);

        if(id == -1){
            Log.e(NoteProvider.class.getSimpleName(), "Failed to insert a row");
            return null;
        }

        return  ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
