package com.example.android.popularmoviesstage2.adapters;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 48101040 on 5/23/2017.
 */

public class Review implements Parcelable {
    public String id;
    public String author;
    public String content;
    public String url;

    public Review(String id, String author, String content, String url){
        this.id = id;
        this.author = author;
        this.content = content;
        this.url = url;
    }

    private Review(Parcel in){

        id = in.readString();
        author = in.readString();
        content = in.readString();
        url = in.readString();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(author);
        dest.writeString(content);
        dest.writeString(url);
    }

    public String toString(){
        return id + ", " + author;
    }

    public final static Creator<Review> CREATOR = new Creator<Review>(){
        @Override
        public Review createFromParcel(Parcel source) {
            return new Review(source);
        }

        @Override
        public Review[] newArray(int i){
            return new Review[i];
        }
    };
}
