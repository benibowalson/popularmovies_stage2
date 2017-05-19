package com.example.android.popularmoviesstage2.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class MovieContentProvider extends ContentProvider {

    public static final int MOVIES_TABLE_CODE_INT = 100;
    public static final int SPECIFIC_MOVIE_CODE_INT = 101;

    public DbOpenHelper mOpenHelper;
    public static final UriMatcher mUriMatcher = builtUriMatcher();


    public MovieContentProvider() {}

    public static UriMatcher builtUriMatcher(){
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(MovieContract.AUTHORITY, MovieContract.MOVIES_PATH, MOVIES_TABLE_CODE_INT);
        matcher.addURI(MovieContract.AUTHORITY, MovieContract.MOVIES_PATH + "/#", SPECIFIC_MOVIE_CODE_INT);

        return matcher;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        int nRowsDeleted;
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int match = mUriMatcher.match(uri);

        switch (match){
            case SPECIFIC_MOVIE_CODE_INT:
                String id = uri.getPathSegments().get(1);
                nRowsDeleted = db.delete(MovieContract.MovieEntry.TABLE_NAME, "_id=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

        if(nRowsDeleted != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return nRowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
       Uri returnedURI;
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int match = mUriMatcher.match(uri);

        switch (match){
            case MOVIES_TABLE_CODE_INT:
                long id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if(id > 0){
                    returnedURI = ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnedURI;
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        mOpenHelper = new DbOpenHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO: Implement this to handle query requests from clients.
        Cursor returnedCursor;
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        int match = mUriMatcher.match(uri);

        switch (match){
            case MOVIES_TABLE_CODE_INT:
                returnedCursor = db.query(MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs, null, null, MovieContract.MovieEntry.COLUMN_TITLE + " ASC");
                break;
            case SPECIFIC_MOVIE_CODE_INT:
                String strID = uri.getPathSegments().get(1);
                String mSelection = "_id=?";
                String[] mSelectionArgs = new String[]{strID};
                returnedCursor = db.query(MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs, null, null, null);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
        returnedCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return returnedCursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
