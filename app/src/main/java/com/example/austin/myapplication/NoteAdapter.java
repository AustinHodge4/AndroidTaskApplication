package com.example.austin.myapplication;

import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by austin on 12/15/17.
 */

public class NoteAdapter extends FirestoreAdapter<NoteAdapter.ViewHolder> {
    public interface OnNoteSelectedListener {

        void onNoteSelected(DocumentSnapshot note, View view, int index);
        void onNoteHold(DocumentSnapshot note, View view, int index);
    }

    private OnNoteSelectedListener mListener;

    NoteAdapter(Query query, OnNoteSelectedListener listener) {
        super(query);
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.card_view, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(holder, getSnapshot(position), mListener, position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.card_title)
        TextView titleView;

        @BindView(R.id.card_note)
        TextView noteView;

        @BindView(R.id.card_modified)
        TextView modifiedView;

        @BindView(R.id.card_view)
        CardView cardView;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(final ViewHolder holder,
                  final DocumentSnapshot snapshot,
                  final OnNoteSelectedListener listener,
                  final int index) {

            Note note = snapshot.toObject(Note.class);


            titleView.setText(note.getTitle());
            noteView.setText(note.getNote());
            SimpleDateFormat df = new SimpleDateFormat("E MM/dd hh:mm a", Locale.US);
            modifiedView.setText(df.format(note.getModified()));

            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onNoteSelected(snapshot, itemView, index);
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if(listener != null){
                        listener.onNoteHold(snapshot, itemView, index);
                    }
                    return true;
                }
            });
        }

    }
}
