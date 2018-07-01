package com.kurobarabenjamingeorge.journal.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kurobarabenjamingeorge.journal.Data.NoteContract.NoteEntry;

/**
 * Created by George Benjamin on 6/29/2018.
 */

public class NoteDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "journal.db";

    public NoteDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String SQL_CREATE_NOTES_TABLE = "CREATE TABLE "+ NoteEntry.TABLE_NAME
                +" ( " + NoteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NoteEntry.COLUMN_NOTE_DATE + " TEXT NOT NULL, "
                + NoteEntry.COLUMN_NOTE_TIME + " TEXT NOT NULL, "
                + NoteEntry.COLUMN_NOTE_CONTENT + " TEXT NOT NULL,"
                + NoteEntry.COLUMN_NOTE_ONLINE + " INTEGER NOT NULL DEFAULT  "+ NoteEntry.NOTE_OFFLINE+" , "
                + NoteEntry.COLUMN_NOTE_FAVOURITE+ " INTEGER NOT NULL );";

        sqLiteDatabase.execSQL(SQL_CREATE_NOTES_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
