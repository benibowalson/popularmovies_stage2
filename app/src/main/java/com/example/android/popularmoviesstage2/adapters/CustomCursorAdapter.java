package com.example.android.popularmoviesstage2.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.android.popularmoviesstage2.R;
import com.example.android.popularmoviesstage2.data.MovieContract;

/**
 * Created by 48101040 on 5/13/2017.
 */

public class CustomCursorAdapter extends RecyclerView.Adapter<CustomCursorAdapter.MovieViewHolder> {

    private Cursor mCursor;
    private Context mContext;
    private IFavClickListener mListener;

    public CustomCursorAdapter(Context ctx, IFavClickListener listener){
        this.mContext = ctx;
        this.mListener = listener;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View myCardView = LayoutInflater.from(mContext)
                .inflate(R.layout.movie_cardview, parent, false);
        return new MovieViewHolder(myCardView);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        //1. get indices for _id, movie_id, title, synopsis, release date, rating and poster path
        int idIndex = mCursor.getColumnIndex(MovieContract.MovieEntry._ID);
        int movieIDIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
        /*int titleIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE);
        int synopsisIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_SYNOPSIS);
        int releaseDateIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE);
        int ratingIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RATING);
        */
        int posterPathIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH);

        mCursor.moveToPosition(position);

        final int dbID = mCursor.getInt(idIndex);
        final int movieID = mCursor.getInt(movieIDIndex);
        /*String movieTitle = mCursor.getString(titleIndex);
        String movieSynopsis = mCursor.getString(synopsisIndex);
        String movieReleaseDate = mCursor.getString(releaseDateIndex);
        double movieRating = mCursor.getDouble(ratingIndex);
        */
        String moviePosterPath = mCursor.getString(posterPathIndex);

        //Associate the id to the current recycled view
        holder.itemView.setTag(dbID);
        Glide.with(mContext)
                .load(moviePosterPath)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(holder.imgPoster);

    }

    @Override
    public int getItemCount() {
        return (mCursor == null)? 0: mCursor.getCount();
    }

    public Cursor swapCursor(Cursor newCursor){
        if(mCursor == newCursor) return null;

        Cursor temp = mCursor;
        this.mCursor = newCursor;

        if(newCursor != null) this.notifyDataSetChanged();

        return temp;
    }

    public interface IFavClickListener {
        void onFavMovieClicked(int movieID);
    }

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imgPoster;

        public MovieViewHolder(View itemView) {
            super(itemView);
            imgPoster = (ImageView)itemView.findViewById(R.id.iv_movie);
        }

        @Override
        public void onClick(View v) {
            int movieID = (int)v.getTag();
            //int movieID = (int) itemView.getTag();
            mListener.onFavMovieClicked(movieID);
        }
    }
}
