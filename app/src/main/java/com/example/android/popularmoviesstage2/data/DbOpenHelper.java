package com.example.android.popularmoviesstage2.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 48101040 on 4/28/2017.
 */

public class DbOpenHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "popularMovies.db";
    private static final int DB_VERSION = 1;

    //Constructor
    DbOpenHelper(Context ctx){
        super(ctx, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TABLE_STR = "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME + " (" +
                MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY, " +
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " INTEGER, " +
                MovieContract.MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_SYNOPSIS + " TEXT, " +
                MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " TEXT, " +
                MovieContract.MovieEntry.COLUMN_RATING + " DOUBLE, " +
                MovieContract.MovieEntry.COLUMN_POSTER_PATH + " TEXT);";
        db.execSQL(CREATE_TABLE_STR);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXIST " + MovieContract.MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
