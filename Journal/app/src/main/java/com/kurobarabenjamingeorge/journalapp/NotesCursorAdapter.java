package com.kurobarabenjamingeorge.journalapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.kurobarabenjamingeorge.journalapp.data.NoteDBHelper;

public class NotesCursorAdapter extends CursorAdapter {
    public NotesCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(
                R.layout.item_note_layout, parent, false
        );
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        String noteText = cursor.getString(
                cursor.getColumnIndex(NoteDBHelper.NOTE_TEXT));

        String dateCreated = cursor.getString(cursor.getColumnIndex(NoteDBHelper.NOTE_CREATED));

        TextView tv = (TextView) view.findViewById(R.id.note_tv);
        tv.setText(noteText);

        TextView dateCreatedTextView = (TextView) view.findViewById(R.id.date_created);
        dateCreatedTextView.setText(dateCreated);

    }
}
