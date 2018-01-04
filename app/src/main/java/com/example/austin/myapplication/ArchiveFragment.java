package com.example.austin.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ArchiveFragment extends Fragment implements NoteAdapter.OnNoteSelectedListener, SwipeRefreshLayout.OnRefreshListener{

    public static final String TAG = "ArchiveFragment";

    @BindView(R.id.card_recycler)
    RecyclerView mNoteRecycler;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout mSwipeRefresh;

    @BindView(R.id.archive_empty)
    View mEmptyView;

    FirebaseFirestore mFirestore;
    NoteAdapter mAdapter;
    ActionMode mActionMode;
    Query mQuery;

    MainActivityViewModel mViewModel;

    private ArrayList<View> selectedViews = new ArrayList<>();
    private HomeFragment.OnNoteEvent mListener;

    public ArchiveFragment() {}

    public static ArchiveFragment newInstance() {
        return new ArchiveFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_archive, container, false);
        ButterKnife.bind(this, view);
        mFirestore = FirebaseFirestore.getInstance();
        mViewModel = MainActivity.mViewModel;
        mQuery = mFirestore.collection("Users");
        mSwipeRefresh.setOnRefreshListener(this);

        mAdapter = new NoteAdapter(mQuery, this) {
            @Override
            protected void onDataChanged() {
                // Show/hide content if the query returns empty.
                if (getItemCount() == 0) {
                    mNoteRecycler.setVisibility(View.GONE);
                    mEmptyView.setVisibility(View.VISIBLE);
                } else {
                    mNoteRecycler.setVisibility(View.VISIBLE);
                    mEmptyView.setVisibility(View.GONE);
                }
            }
            @Override
            protected void onError(FirebaseFirestoreException e) {
                // Show a snackbar on errors
                Snackbar.make(getActivity().findViewById(android.R.id.content),
                        "Error: check logs for info.", Snackbar.LENGTH_LONG).show();
            }
        };
        mNoteRecycler.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mNoteRecycler.setAdapter(mAdapter);
        setHasOptionsMenu(true);
        return view;
    }
    @Override
    public void onStart(){
        super.onStart();

        onFilter(mViewModel.getFilters());

        if(mAdapter != null) mAdapter.startListening();

    }
    @Override
    public void onStop(){
        super.onStop();
        if(mAdapter != null) mAdapter.stopListening();
    }
    @Override
    public void onPause() {
        super.onPause();
        mNoteRecycler.setLayoutFrozen(true);
    }
    @Override
    public void onResume() {
        super.onResume();
        mNoteRecycler.setLayoutFrozen(false);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof HomeFragment.OnNoteEvent) {
            mListener = (HomeFragment.OnNoteEvent) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnNoteEvent");
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_archive_menu, menu);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            if(extras != null) {
                String noteId = extras.getString(NoteActivity.KEY_NOTE_ID);
                final String action = extras.getString(NoteActivity.ACTION_TYPE);
                if(noteId != null && action != null) {
                    DocumentReference noteRef = MainActivity.mUserRef.collection("Cards").document(noteId);
                    noteRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot snapshot) {
                            ArrayList<DocumentSnapshot> snapshots = new ArrayList<>();
                            snapshots.add(snapshot);
                            if(action.equals(NoteActivity.ARCHIVE_ACTION)) {
                                if (mListener != null) mListener.onArchiveEvent(snapshots);
                            }
                            else if(action.equals(NoteActivity.TRASH_ACTION)){
                                if (mListener != null) mListener.onTrashEvent(snapshots);
                            }
                        }
                    });
                }
                else
                {
                    throw new RuntimeException(getClass().getName() + " could not find noteId and action onActivityResult");
                }
            }
        }
    }
    @Override
    public void onNoteSelected(DocumentSnapshot note, View view, int index){
        if(mActionMode != null){
            if(selectedViews.contains(view)){
                view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.cardview_light_background));
            }else {
                selectedViews.add(view);
                view.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.selector));
            }
            toggleSelection(index);
        }
        else {
            Toast.makeText(getContext(), "Selected Note!!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), NoteActivity.class);
            intent.putExtra(NoteActivity.KEY_NOTE_ID, note.getId());
            intent.putExtra(NoteActivity.KEY_PREVIOUS_FRAGMENT, TAG);
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    getActivity(),
                    view,
                    ViewCompat.getTransitionName(view));

            startActivityForResult(intent,1, options.toBundle());
        }
    }
    @Override
    public void onNoteHold(DocumentSnapshot note, final View view, int index){
        if(mActionMode == null){
            mActionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(new ActionMode.Callback() {
                @Override
                public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                    actionMode.getMenuInflater().inflate(R.menu.selected_archive_menu, menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                    switch(menuItem.getItemId()){
                        case R.id.note_unarchive:
                            NoteUtil.unarchiveDocument(mAdapter.getSelected());
                            actionMode.finish();
                            return true;
                        case R.id.note_trash:
                            NoteUtil.trashDocument(mAdapter.getSelected());
                            if(mListener != null) mListener.onTrashEvent(mAdapter.getSelected());
                            actionMode.finish();
                            return true;
                    }
                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode actionMode) {
                    mAdapter.clearSelection();
                    for(View v : selectedViews){
                        v.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.cardview_light_background));
                    }                    mActionMode = null;
                    selectedViews.clear();
                }
            });
            toggleSelection(index);
            selectedViews.add(view);
            view.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.selector));
        }
        else {
            mActionMode.finish();
            mActionMode = null;
            selectedViews.clear();
        }
        Toast.makeText(getContext(), "Holding Note!!", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onRefresh() {
        onFilter(mViewModel.getFilters());
        mSwipeRefresh.setRefreshing(false);
    }
    public void onFilter(Filters filters) {
        if(!MainActivity.isUserSet()) MainActivity.setUser();
        // Construct query basic query
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            Query query = MainActivity.mUserRef.collection("Cards");
            query = query.whereEqualTo(Note.FIELD_ARCHIVED, true);
            query = query.whereEqualTo(Note.FIELD_TRASHED, false);
            if(filters.hasSortBy()){
                query = query.orderBy(filters.getSortBy(), filters.getSortDirection());
            }
            // Update the query
            mAdapter.setQuery(query);
        }
        // Save filters
        mViewModel.setFilters(filters);
    }
    private void toggleSelection(int index){
        mAdapter.toggleSelection(index);
        int count = mAdapter.getSelectedItemCount();
        if(count == 0){
            mActionMode.finish();
        }
        else{
            mActionMode.setTitle(String.valueOf(count));
            mActionMode.invalidate();
        }
    }

}
