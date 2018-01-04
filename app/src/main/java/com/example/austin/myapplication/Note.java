package com.example.austin.myapplication;

import java.util.Date;

/**
 * Created by austin on 12/15/17.
 */

public class Note {
    public static final String FIELD_TITLE = "title";
    public static final String FIELD_NOTE = "note";
    public static final String FIELD_MODIFIED = "modified";
    public static final String FIELD_ARCHIVED = "archived";
    public static final String FIELD_TRASHED = "trashed";

    private String title;
    private String note;
    private Date modified;

    private boolean archived;
    private boolean trashed;

    public Note() {}
    public Note(String title, String note, Date modified, boolean archived, boolean trashed) {
        this.title = title;
        this.note = note;
        this.modified = modified;
        this.archived = archived;
        this.trashed = trashed;
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

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public boolean isTrashed(){return trashed;}

    public void setTrashed(boolean trashed){ this.trashed = trashed;}

}
