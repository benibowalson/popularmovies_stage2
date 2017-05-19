package com.example.android.popularmoviesstage2.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by 48101040 on 4/28/2017.
 */

public class MovieContract {

    public static final String AUTHORITY = "com.example.android.popularmoviesstage2";
    public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);
    public static final String MOVIES_PATH = "tblMovies";

    public static final class MovieEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(MOVIES_PATH).build();
        public static final String TABLE_NAME = "tblMovies";
        //BaseColumns class already produces the "_ID" column           //id in our table
        public static final String COLUMN_MOVIE_ID = "movie_id";        //id in remote server
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_SYNOPSIS = "synopsis";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_POSTER_PATH = "poster_path";
    }
}
