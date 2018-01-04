package com.example.austin.myapplication;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * RecyclerView adapter for displaying the results of a Firestore {@link Query}.
 *
 * Note that this class forgoes some efficiency to gain simplicity. For example, the result of
 * {@link DocumentSnapshot#toObject(Class)} is not cached so the same object may be deserialized
 * many times as the user scrolls.
 */
public abstract class FirestoreAdapter<VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH>
        implements EventListener<QuerySnapshot> {

    private static final String TAG = "FirestoreAdapter";

    private Query mQuery;
    private ListenerRegistration mRegistration;

    private ArrayList<DocumentItem> mSnapshots = new ArrayList<>();

    FirestoreAdapter(Query query) {
        mQuery = query;
    }

    @Override
    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
        if (e != null) {
            Log.w(TAG, "onEvent:error", e);
            onError(e);
            return;
        }

        // Dispatch the event
        Log.d(TAG, "onEvent:numChanges:" + documentSnapshots.getDocumentChanges().size());
        for (DocumentChange change : documentSnapshots.getDocumentChanges()) {
            switch (change.getType()) {
                case ADDED:
                    onDocumentAdded(change);
                    break;
                case MODIFIED:
                    onDocumentModified(change);
                    break;
                case REMOVED:
                    onDocumentRemoved(change);
                    break;
            }
        }

        onDataChanged();
    }

    void startListening() {
        if (mQuery != null && mRegistration == null) {
            mRegistration = mQuery.addSnapshotListener(this);
        }
    }

    void stopListening() {
        if (mRegistration != null) {
            mRegistration.remove();
            mRegistration = null;
        }

        mSnapshots.clear();
        notifyDataSetChanged();
    }

    void setQuery(Query query) {
        // Stop listening
        stopListening();

        // Clear existing data
        mSnapshots.clear();
        notifyDataSetChanged();

        // Listen to new query
        mQuery = query;
        startListening();
    }

    @Override
    public int getItemCount() {
        return mSnapshots.size();
    }

    public int getSelectedItemCount(){
        int count = 0;
        for(DocumentItem item : mSnapshots){
            if(item.isSelected()) count++;
        }
        return count;
    }

    public boolean isSelected(int index){
        return mSnapshots.get(index).isSelected();
    }

    public void toggleSelection(int index){
        mSnapshots.get(index).setSelected(!mSnapshots.get(index).isSelected());
    }

    public void clearSelection(){
        for (DocumentItem item : mSnapshots) {
            item.setSelected(false);
        }
    }
    public ArrayList<DocumentSnapshot> getSelected(){
        ArrayList<DocumentSnapshot> snapshots = new ArrayList<>();
        for(DocumentItem snapshot : mSnapshots){
            if(snapshot.isSelected()) snapshots.add(snapshot.getSnapshot());
        }
        return snapshots;
    }

    DocumentSnapshot getSnapshot(int index) {
        return mSnapshots.get(index).getSnapshot();
    }

    private void onDocumentAdded(DocumentChange change) {
        DocumentItem item = new DocumentItem(change.getDocument(), false);
        mSnapshots.add(change.getNewIndex(), item);
        notifyItemInserted(change.getNewIndex());
    }

    private void onDocumentModified(DocumentChange change) {
        DocumentItem item = new DocumentItem(change.getDocument(), false);
        if (change.getOldIndex() == change.getNewIndex()) {
            // Item changed but remained in same position
            mSnapshots.set(change.getOldIndex(), item);
            notifyItemChanged(change.getOldIndex());
        } else {
            // Item changed and changed position
            mSnapshots.remove(change.getOldIndex());
            mSnapshots.add(change.getNewIndex(), item);
            notifyItemMoved(change.getOldIndex(), change.getNewIndex());
        }
    }

    private void onDocumentRemoved(DocumentChange change) {
        mSnapshots.remove(change.getOldIndex());
        notifyItemRemoved(change.getOldIndex());
    }
    protected void onError(FirebaseFirestoreException e) {}

    protected void onDataChanged() {}
}