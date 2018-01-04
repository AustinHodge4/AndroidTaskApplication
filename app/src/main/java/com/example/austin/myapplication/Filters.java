package com.example.austin.myapplication;

import android.text.TextUtils;

import com.google.firebase.firestore.Query;

/**
 * Created by austin on 12/17/17.
 */

class Filters {
    private boolean archive = false;
    private String sortBy = null;
    private Query.Direction sortDirection;

    Filters() {}

    static Filters getDefault(){
        Filters filter = new Filters();
        filter.setSortDirection(Query.Direction.DESCENDING);
        filter.setSortBy(Note.FIELD_MODIFIED);
        return filter;
    }

    boolean hasSortBy(){
        return !(TextUtils.isEmpty(sortBy));
    }

    public boolean isArchive() {
        return archive;
    }

    public void setArchive(boolean archive) {
        this.archive = archive;
    }

    String getSortBy() {
        return sortBy;
    }

    void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    Query.Direction getSortDirection() {
        return sortDirection;
    }

    void setSortDirection(Query.Direction sortDirection) {
        this.sortDirection = sortDirection;
    }
}
