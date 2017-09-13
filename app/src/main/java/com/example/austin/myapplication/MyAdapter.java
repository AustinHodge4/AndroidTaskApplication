package com.example.austin.myapplication;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<CardInformationHolder> mDataset = new ArrayList<>();
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    private static class DateCardViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView mTextView;
        TextView mDateView;
        ImageView mCalendarView;
        Context context;

        DateCardViewHolder(View v) {
             super(v);
             mTextView = (TextView) v.findViewById(R.id.title);
             mDateView = (TextView) v.findViewById(R.id.date);
             mCalendarView = (ImageView) v.findViewById(R.id.calendar);
             context = v.getContext();
        }
    }
    private static class LocationCardViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView mTitleView;
        TextView mAddressView;
        ImageView mLocationView;
        Context context;

        LocationCardViewHolder(View v) {
            super(v);
            mTitleView = (TextView) v.findViewById(R.id.title);
            mAddressView = (TextView) v.findViewById(R.id.address);
            mLocationView = (ImageView) v.findViewById(R.id.location);
            context = v.getContext();

        }
    }
    private static class PhoneCardViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView mTitleView;
        TextView mSubtitleView;
        TextView mDateView;
        ImageView mPhoneView;
        Context context;

        PhoneCardViewHolder(View v) {
            super(v);
            mTitleView = (TextView) v.findViewById(R.id.title);
            mSubtitleView = (TextView) v.findViewById(R.id.subtitle);
            mDateView = (TextView) v.findViewById(R.id.date);
            mPhoneView = (ImageView) v.findViewById(R.id.phone);
            context = v.getContext();

        }
    }
    // Provide a suitable constructor (depends on the kind of dataset)
    MyAdapter(List<CardInformationHolder> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public int getItemViewType(int position){
        return mDataset.get(position).Type().Id();
    }
    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder viewHolder;
        switch(viewType){
            case 0:
                // create a new view
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.date_card, parent, false);
                // set the view's size, margins, paddings and layout parameters
                //...
                viewHolder = new DateCardViewHolder(view);
                return viewHolder;
            case 1:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.location_card, parent, false);
                // set the view's size, margins, paddings and layout parameters
                //...

                viewHolder = new LocationCardViewHolder(view);
                return viewHolder;
            case 2:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.phone_card, parent, false);
                // set the view's size, margins, paddings and layout parameters
                //...

                viewHolder = new PhoneCardViewHolder(view);
                return viewHolder;
        }
        return null;
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        switch(holder.getItemViewType()){
            case 0:
                // - get element from your dataset at this position
                // - replace the contents of the view with that element
                final DateCardViewHolder dateCard = (DateCardViewHolder)holder;
                dateCard.mTextView.setText(mDataset.get(position).Title());
                dateCard.mDateView.setText(mDataset.get(position).DateString());
                dateCard.mCalendarView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view){
                        DialogFragment newFragment = new TimePickerFragment();

                    }
                });
                return;
            case 1:
                final LocationCardViewHolder locationCard = (LocationCardViewHolder) holder;
                locationCard.mTitleView.setText(mDataset.get(position).Title());
                locationCard.mAddressView.setText(mDataset.get(position).Location());
                locationCard.mLocationView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent url = new Intent(Intent.ACTION_VIEW, Uri.parse(mDataset.get(position).EncodedLocation()));
                        locationCard.context.startActivity(url);
                    }
                });
                return;
            case 2:
                final PhoneCardViewHolder phoneCard = (PhoneCardViewHolder) holder;
                final CardInformationHolder card = mDataset.get(position);

                phoneCard.mTitleView.setText(card.Title());
                if(card.Subtitle().isEmpty()){
                    phoneCard.mSubtitleView.setVisibility(View.GONE);
                }
                else {
                    phoneCard.mSubtitleView.setText(card.Subtitle());
                }
                if(card.hasDate() && card.hasTime()) {
                    CardTextLabelAnimator textAnimator = new CardTextLabelAnimator(phoneCard.mDateView, new String[] {card.DateString(), "6:45 PM"});
                    textAnimator.startAnimation();
                }
                else if(card.hasDate()){
                    phoneCard.mDateView.setText(card.DateString());
                }
                else if(card.hasTime()){
                    phoneCard.mDateView.setText("4:20 PM");
                }

                phoneCard.mPhoneView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent url = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+card.Phone()));
                        try {
                            Log.println(Log.INFO, "Phone","tel:"+card.Phone());
                            phoneCard.context.startActivity(url);
                        }
                        catch(SecurityException e){
                            Log.println(Log.ERROR, "Error Permission", e.getMessage());
                        }
                    }
                });
        }

    }
    void addData(String value){
        mDataset.add(new CardInformationHolder(value, null, null, "Pizza Hut", null,CardInformationHolder.CARDTYPE.LOCATION));
        notifyItemInserted(getItemCount() + 1);
        Sort();
    }
    void remove(int position){
        mDataset.remove(position);
        notifyItemRemoved(position);
    }
    void Sort(){
        Collections.sort(mDataset);
        notifyDataSetChanged();
    }
    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
