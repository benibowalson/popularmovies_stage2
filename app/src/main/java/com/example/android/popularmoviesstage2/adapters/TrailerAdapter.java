package com.example.android.popularmoviesstage2.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmoviesstage2.R;

import java.util.ArrayList;

/**
 * Created by 48101040 on 5/24/2017.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerCard> {

    private ArrayList<Trailer> mTrailers;
    private Context mContext;
    private ITrailerClickListener mClickListener;

    public TrailerAdapter(Context ctx, ArrayList<Trailer> trailers, ITrailerClickListener listener ){
        this.mContext = ctx;
        this.mTrailers = trailers;
        this.mClickListener = listener;
    }

    @Override
    public TrailerCard onCreateViewHolder(ViewGroup parent, int viewType) {
        View aCard = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_cardview, parent, false);
        return new TrailerCard(aCard);
    }

    @Override
    public void onBindViewHolder(TrailerCard holder, int position) {
        Trailer aTrailer = mTrailers.get(position);
        String name = aTrailer.name;
        holder.txtTrailer.setText(name);
    }

    @Override
    public int getItemCount() {
        return mTrailers.size();
    }

    public interface ITrailerClickListener {
        void onTrailerClicked(int clickedPosition);
    }

    public class TrailerCard extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView txtTrailer;

        public TrailerCard(View itemView){
            super(itemView);
            txtTrailer = (TextView)itemView.findViewById(R.id.tvTrailer);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mClickListener.onTrailerClicked(clickedPosition);
        }
    }
}
