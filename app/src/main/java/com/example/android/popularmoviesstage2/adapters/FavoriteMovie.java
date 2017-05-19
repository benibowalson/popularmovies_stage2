package com.example.android.popularmoviesstage2.adapters;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 48101040 on 4/7/2017.
 */

public class FavoriteMovie implements Parcelable {

         String poster_path;
        String overview;
         String release_date;
         int id;
         String title;
         double vote_average;

    public FavoriteMovie(String poster_path, String overview, String release_date, int id, String title, double vote_average) {
        this.poster_path = poster_path;
        this.overview = overview;
        this.release_date = release_date;
        this.id = id;
        this.title = title;
        this.vote_average = vote_average;
    }

    private FavoriteMovie(Parcel in){
        poster_path = in.readString();
        overview = in.readString();
        release_date = in.readString();
        id = in.readInt();
        title = in.readString();
        vote_average = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(poster_path);
        dest.writeString(overview);
        dest.writeString(release_date);
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeDouble(vote_average);
    }

    public String toString(){
        return title + ", " + release_date;
    }

    public final static Creator<FavoriteMovie> CREATOR = new Creator<FavoriteMovie>(){
        @Override
        public FavoriteMovie createFromParcel(Parcel source) {
            return new FavoriteMovie(source);
        }

        @Override
        public FavoriteMovie[] newArray(int i){
            return new FavoriteMovie[i];
        }
    };
}
