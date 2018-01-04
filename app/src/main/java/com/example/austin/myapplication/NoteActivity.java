package com.example.austin.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;


public class NoteActivity extends AppCompatActivity {
    public static final String KEY_NOTE_ID = "key_note_id";
    public static final String KEY_PREVIOUS_FRAGMENT = "key_previous_fragment";
    public static final String ACTION_TYPE = "action_type";
    public static final String ARCHIVE_ACTION = "archive_action";
    public static final String TRASH_ACTION = "trash_action";



    private DocumentReference mNoteRef;
    private String mNoteId = null;
    private String mPrevFrag = null;
    private Note mNote;
    private FirebaseFirestore mFirestore;
    private boolean isCreating = false;
    private boolean isBackPressed = false;

    @BindView(R.id.note_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.bottom_toolbar)
    Toolbar mBottomToolbar;

    @BindView(R.id.note_title)
    TextView mNoteTitle;
    @BindView(R.id.note_note)
    TextView mNoteNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFirestore = FirebaseFirestore.getInstance();

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            mNoteId = extras.getString(KEY_NOTE_ID);
            mPrevFrag = extras.getString(KEY_PREVIOUS_FRAGMENT);
            if(mNoteId != null) {

                mNoteRef = MainActivity.mUserRef.collection("Cards").document(mNoteId);
                mNoteRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot snapshot) {
                        mNote = snapshot.toObject(Note.class);
                        mNoteTitle.setText(mNote.getTitle());
                        mNoteNote.setText(mNote.getNote());
                    }
                });
                isCreating = false;
            }
            else {
                mNoteRef = MainActivity.mUserRef.collection("Cards").document();
                isCreating = true;
            }
        }

        getWindow().setEnterTransition(new Fade());
        getWindow().setExitTransition(new Fade());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_note_menu, menu);
        switch (mPrevFrag){
            case HomeFragment.TAG:
                menu.findItem(R.id.note_unarchive).setVisible(false);
                menu.findItem(R.id.note_restore).setVisible(false);
                menu.findItem(R.id.note_delete).setVisible(false);
                break;
            case ArchiveFragment.TAG:
                menu.findItem(R.id.note_archive).setVisible(false);
                menu.findItem(R.id.note_restore).setVisible(false);
                menu.findItem(R.id.note_delete).setVisible(false);
                break;
            case TrashFragment.TAG:
                menu.findItem(R.id.note_archive).setVisible(false);
                menu.findItem(R.id.note_trash).setVisible(false);
                menu.findItem(R.id.note_unarchive).setVisible(false);
                break;
        }
        if(isCreating){
            menu.findItem(R.id.note_archive).setVisible(false);
            menu.findItem(R.id.note_trash).setVisible(false);
            menu.findItem(R.id.note_unarchive).setVisible(false);
            menu.findItem(R.id.note_restore).setVisible(false);
            menu.findItem(R.id.note_delete).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()){
            case android.R.id.home:
                onClickHome();
                return true;
            case R.id.note_archive:
                onClickArchive();
                return true;
            case R.id.note_trash:
                onClickTrash();
                return true;
            case R.id.note_unarchive:
                onClickUnarchive();
                return true;
            case R.id.note_restore:
                onClickUntrash();
                return true;
            case R.id.note_delete:
                onClickDelete();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        onClickHome();
    }

    protected void onClickHome() {
        super.onBackPressed();
        hideKeyboard();
        if (!mPrevFrag.equals(TrashFragment.TAG)){
            if(!isBackPressed) {
                createOrUpdateCard();
            }
        }
    }
    protected void onClickArchive(){
        super.onBackPressed();
        hideKeyboard();
        NoteUtil.archiveDocument(mNoteRef);
        Intent returnIntent = new Intent();
        returnIntent.putExtra(NoteActivity.KEY_NOTE_ID, mNoteId);
        returnIntent.putExtra(NoteActivity.ACTION_TYPE, NoteActivity.ARCHIVE_ACTION);
        setResult(Activity.RESULT_OK,returnIntent);
    }
    protected void onClickTrash(){
        super.onBackPressed();
        hideKeyboard();
        NoteUtil.trashDocument(mNoteRef);
        Intent returnIntent = new Intent();
        returnIntent.putExtra(NoteActivity.KEY_NOTE_ID, mNoteId);
        returnIntent.putExtra(NoteActivity.ACTION_TYPE, NoteActivity.TRASH_ACTION);
        setResult(Activity.RESULT_OK,returnIntent);
    }
    protected void onClickUnarchive(){
        super.onBackPressed();
        hideKeyboard();
        NoteUtil.unarchiveDocument(mNoteRef);
    }
    protected void onClickUntrash(){
        super.onBackPressed();
        hideKeyboard();
        NoteUtil.untrashDocument(mNoteRef);
    }
    protected void onClickDelete(){
        super.onBackPressed();
        hideKeyboard();
        NoteUtil.deleteDocument(mNoteRef);
    }
    private void createOrUpdateCard(){
        isBackPressed = true;
        mNote = new Note(mNoteTitle.getText().toString(),
                mNoteNote.getText().toString(),
                new Date(),
                mPrevFrag.equals(ArchiveFragment.TAG),
                false);

        if(!isModified(mNote)) {
            isBackPressed = false;
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            mNoteRef
            .set(mNote)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("Success", "document successfully added!");
                    isBackPressed = false;
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w("Failure","Error adding document", e);
                    isBackPressed = false;
                }
            });
        }

    }
    public boolean isModified(Note note){
        return (!note.getTitle().equals(Note.FIELD_TITLE) || !note.getNote().equals(Note.FIELD_NOTE));
    }
    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
