package com.example.austin.myapplication;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by austin on 12/15/17.
 */

class NoteUtil {

    private static final String TAG = "NoteUtil";

    static void deleteDocument(DocumentSnapshot snapshot){
        snapshot.getReference()
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Document successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
    }
    static void deleteDocument(DocumentReference documentReference){
        documentReference
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Document successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
    }
    static void deleteDocument(ArrayList<DocumentSnapshot> snapshots){
        WriteBatch batch = FirebaseFirestore.getInstance().batch();
        for(DocumentSnapshot snapshot : snapshots){
            batch.delete(snapshot.getReference());
        }
        batch.commit()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Documents successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting documents", e);
                    }
                });
    }
    static void trashDocument(DocumentSnapshot snapshot){
        Map<String, Object> map = new HashMap<>();
        map.put(Note.FIELD_ARCHIVED, false);
        map.put(Note.FIELD_TRASHED, true);
        snapshot.getReference()
                .update(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Document successfully trashed!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error trashing document", e);
                    }
                });
    }
    static void trashDocument(DocumentReference documentReference){
        Map<String, Object> map = new HashMap<>();
        map.put(Note.FIELD_ARCHIVED, false);
        map.put(Note.FIELD_TRASHED, true);
        documentReference
                .update(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Document successfully trashed!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error trashing document", e);
                    }
                });
    }
    static void trashDocument(ArrayList<DocumentSnapshot> snapshots){
        WriteBatch batch = FirebaseFirestore.getInstance().batch();
        for(DocumentSnapshot snapshot : snapshots){
            batch.update(snapshot.getReference(), "trashed", true);
            batch.update(snapshot.getReference(), "archived", false);
        }
        batch.commit()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Documents successfully trahsed!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error trashing documents", e);
                    }
                });
    }
    static void untrashDocument(DocumentSnapshot snapshot){
        snapshot.getReference()
                .update(Note.FIELD_TRASHED, false)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Document successfully untrashed!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error untrashing document", e);
                    }
                });
    }
    static void untrashDocument(DocumentReference documentReference){
        documentReference
                .update(Note.FIELD_TRASHED, false)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Document successfully untrashed!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error untrashing document", e);
                    }
                });
    }
    static void untrashDocument(ArrayList<DocumentSnapshot> snapshots){
        WriteBatch batch = FirebaseFirestore.getInstance().batch();
        for(DocumentSnapshot snapshot : snapshots){
            batch.update(snapshot.getReference(), "trashed", false);
        }
        batch.commit()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Documents successfully untrahsed!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error untrashing documents", e);
                    }
                });
    }
    static void archiveDocument(DocumentSnapshot snapshot){
        snapshot.getReference()
                .update(Note.FIELD_ARCHIVED, true)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Document successfully archived!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error archiving document", e);
                    }
                });
    }
    static void archiveDocument(DocumentReference documentReference){
        documentReference
                .update(Note.FIELD_ARCHIVED, true)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Document successfully archived!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG,"Error archiving document", e);
                    }
                });
    }
    static void archiveDocument(ArrayList<DocumentSnapshot> snapshots){
        WriteBatch batch = FirebaseFirestore.getInstance().batch();
        for(DocumentSnapshot snapshot : snapshots){
            batch.update(snapshot.getReference(), "archived", true);
        }
        batch.commit()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Documents successfully archived!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error archiving documents", e);
                    }
                });
    }
    static void unarchiveDocument(DocumentSnapshot snapshot){
        snapshot.getReference()
                .update(Note.FIELD_ARCHIVED, false)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "document successfully unarchived!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error unarchiving document", e);
                    }
                });
    }
    static void unarchiveDocument(DocumentReference documentReference){
        documentReference
                .update(Note.FIELD_ARCHIVED, false)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Document successfully unarchived!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG,"Error unarchiving document", e);
                    }
                });
    }
    static void unarchiveDocument(ArrayList<DocumentSnapshot> snapshots){
        WriteBatch batch = FirebaseFirestore.getInstance().batch();
        for(DocumentSnapshot snapshot : snapshots){
            batch.update(snapshot.getReference(), "archived", false);
        }
        batch.commit()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Documents successfully unarchived!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error unarchiving documents", e);
                    }
                });
    }
}
