package com.kurobarabenjamingeorge.journal.Data;

/**
 * Created by George Benjamin on 6/26/2018.
 */

public class Note {

    private String date;
    private String time;
    private String note_content;
    private int favourite;

    public Note(String date, String time, String note_content, int favourite) {
        this.date = date;
        this.time = time;
        this.note_content = note_content;
        this.favourite = favourite;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getNote_content() {
        return note_content;
    }

    public int getFavourite() {
        return favourite;
    }


}
