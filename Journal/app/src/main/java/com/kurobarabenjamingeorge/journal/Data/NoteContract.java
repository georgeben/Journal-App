package com.kurobarabenjamingeorge.journal.Data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by George Benjamin on 6/29/2018.
 */

public final class NoteContract {

    public static final String CONTENT_AUTHORITY = "com.kurobarabenjamingeorge.myjournal";
    public static final String  NOTE_PATH = "notes";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);

    public static final class NoteEntry implements BaseColumns{

        public static  final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, NOTE_PATH);

        public static final String TABLE_NAME = "notes";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NOTE_DATE = "date";
        public static final String COLUMN_NOTE_TIME = "time";
        public static final String COLUMN_NOTE_CONTENT = "content";
        public static final String COLUMN_NOTE_FAVOURITE = "favourite";
        public  static final String COLUMN_NOTE_ONLINE = "online";

        public static final int NOT_FAVOURITE_NOTE = 0;
        public static final int FAVOURITE_NOTE = 1;

        public static final int NOTE_ONLINE = 0;
        public static final int NOTE_OFFLINE = 1;


    }
}
