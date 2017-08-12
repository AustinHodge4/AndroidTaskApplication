package com.example.austin.myapplication;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<CardInformationHolder> mDataset = new ArrayList<>();
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    private static class DateCardViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView mTextView;
         TextView mDateView;
         Context context;

         DateCardViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.title);
            mDateView = (TextView) v.findViewById(R.id.date);
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
            mAddressView = (TextView) v.findViewById(R.id.date);
            mLocationView = (ImageView) v.findViewById(R.id.location);
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
                DateCardViewHolder dateCard = (DateCardViewHolder)holder;
                dateCard.mTextView.setText(mDataset.get(position).Title());
                String formattedDate = new SimpleDateFormat("E MM/dd", Locale.US).format(mDataset.get(position).Date());
                dateCard.mDateView.setText(formattedDate);
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
                final LocationCardViewHolder phoneCard = (LocationCardViewHolder) holder;
                phoneCard.mTitleView.setText(mDataset.get(position).Title());
                phoneCard.mAddressView.setText(mDataset.get(position).Location());
                phoneCard.mLocationView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent url = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+mDataset.get(position).EncodedLocation()));
                        try {
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
        mDataset.add(new CardInformationHolder(value, Calendar.getInstance().getTime(), "Pizza Hut", CardInformationHolder.CARDTYPE.LOCATION));
        notifyItemInserted(getItemCount() + 1);
    }
    void remove(int position){
        mDataset.remove(position);
        notifyItemRemoved(position);
    }
    public void Sort(){
        Collections.sort(mDataset);
        notifyDataSetChanged();
    }
    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
