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
 * Created by 48101040 on 5/23/2017.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewCard> {

    private ArrayList<Review> mReviews;
    private Context mContext;
    private IDoListen mClickHandler;

    public ReviewAdapter(Context ctx, ArrayList<Review> reviews, IDoListen listnr){

        this.mContext = ctx;
        this.mReviews = reviews;
        this.mClickHandler = listnr;
    }


    @Override
    public ReviewCard onCreateViewHolder(ViewGroup parent, int viewType) {
       View aCard = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_cardview, parent, false);
        return new ReviewCard(aCard);
    }

    @Override
    public void onBindViewHolder(ReviewCard holder, int position) {
        Review aReview = mReviews.get(position);
        String content = aReview.content + "\n\nAUTHOR: " + aReview.author;
        holder.txtContent.setText(content);
    }

    @Override
    public int getItemCount() {
        return mReviews.size();
    }

    public interface IDoListen {
        void onReviewClicked(int clickedPos);
    }


    public class ReviewCard extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView txtContent;

        public ReviewCard(View itemView){
            super(itemView);
            txtContent = (TextView)itemView.findViewById(R.id.tvReview);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickPos = getAdapterPosition();
            mClickHandler.onReviewClicked(clickPos);
        }
    }
}
