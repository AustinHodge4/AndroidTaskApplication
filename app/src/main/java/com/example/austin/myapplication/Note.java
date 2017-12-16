package com.example.austin.myapplication;

import java.util.Date;

/**
 * Created by austin on 12/15/17.
 */

public class Note {
    public static final String FIELD_TITLE = "title";
    public static final String FIELD_NOTE = "note";
    public static final String FIELD_MODIFIED = "modified";

    private String title;
    private String note;
    private Date modified;

    public Note() {}

    public Note(String title, String note, Date modified) {
        this.title = title;
        this.note = note;
        this.modified = modified;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

}
