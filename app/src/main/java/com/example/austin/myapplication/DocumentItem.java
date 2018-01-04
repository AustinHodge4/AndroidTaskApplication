package com.example.austin.myapplication;

import com.google.firebase.firestore.DocumentSnapshot;

/**
 * Created by austin on 12/30/17.
 */

public class DocumentItem {
    private DocumentSnapshot snapshot;
    private boolean selected;

    public DocumentItem(DocumentSnapshot snapshot, boolean selected){
        this.snapshot = snapshot;
        this.selected = selected;
    }

    public DocumentSnapshot getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(DocumentSnapshot snapshot) {
        this.snapshot = snapshot;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
