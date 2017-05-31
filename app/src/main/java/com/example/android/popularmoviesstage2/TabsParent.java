package com.example.android.popularmoviesstage2;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.android.popularmoviesstage2.adapters.FavoriteMovie;
import com.example.android.popularmoviesstage2.adapters.Movie;
import com.example.android.popularmoviesstage2.adapters.PagerAdapter;
import com.example.android.popularmoviesstage2.data.MovieContract;

public class TabsParent extends AppCompatActivity implements TabLayout.OnTabSelectedListener
,LoaderManager.LoaderCallbacks<Cursor>{

    private TabLayout tabLayout;
    private ViewPager viewPager;
    TextView errorTextView;
    TextView txtOverview;
    CheckBox chkFavorite;
    ImageView ivPoster;
    Movie currentMovie;
    FavoriteMovie currentFavMovie;
    Cursor mMoviesData;
    PagerAdapter myPagerAdapter;

    private static final int LOADER_TASK_ID = 1;
    private static final String TAG = TabsParent.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabsparent);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        tabLayout.addTab(tabLayout.newTab().setText("SYNOPSIS"));
        tabLayout.addTab(tabLayout.newTab().setText("REVIEWS"));
        tabLayout.addTab(tabLayout.newTab().setText("TRAILERS"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = (ViewPager)findViewById(R.id.pager);
        ivPoster = (ImageView)findViewById(R.id.imgPoster);
        //txtOverview = (TextView)findViewById(R.id.tvOverview);
        chkFavorite = (CheckBox)findViewById(R.id.chkFavorite);

        //Create PagerAdapter
        myPagerAdapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(myPagerAdapter);

        getSupportLoaderManager().initLoader(LOADER_TASK_ID, null, this);    //load initial data

        //Click Listener
        tabLayout.addOnTabSelectedListener(this);

        if(savedInstanceState == null){                 //New Activity
            Intent sourceIntent = getIntent();
            Bundle extras = sourceIntent.getExtras();
            if(extras != null){

                currentMovie = (Movie) extras.getParcelable("Movie_Object");

                try{
                    writeFields();
                } catch (Exception ex){
                    Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            currentMovie = savedInstanceState.getParcelable("aMovie");
            writeFields();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("aMovie", currentMovie);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        writeFields();
    }

    @Override
    protected void onResume() {
        getSupportLoaderManager().restartLoader(LOADER_TASK_ID, null, this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        saveOrRemoveFavorite();
        super.onPause();
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    void writeFields(){
        Glide.with(this)
                .load(currentMovie.poster_path)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                //.placeholder()
                //.error()
                .into(ivPoster);

    }

    public String getMovieOverview(){
        return currentMovie.overview;
    }

    public int sendMovieIDforReview(){
        return currentMovie.id;
    }

    public int idForTrailer(){
        return currentMovie.id;
    }

    public void saveOrRemoveFavorite(){

        int id = currentMovie.id;
        if(chkFavorite.isChecked()){
            //Add to favorites; if not added already
            getSupportLoaderManager().initLoader(LOADER_TASK_ID, null, this);       //load data
            if(!alreadyFavorite(id)){
                ContentValues contentValues = new ContentValues();
                contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, id);
                contentValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, currentMovie.poster_path);
                contentValues.put(MovieContract.MovieEntry.COLUMN_RATING, currentMovie.vote_average);
                contentValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, currentMovie.poster_path);
                contentValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, currentMovie.release_date);
                contentValues.put(MovieContract.MovieEntry.COLUMN_SYNOPSIS, currentMovie.overview);
                contentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, currentMovie.title);

                Uri uri = getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, contentValues);

                if(uri != null){
                    Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            //Remove from favorites
            if(alreadyFavorite(id)){
                int dbID = 0;
                Cursor nCursor = getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, null, null, null, null);
                if(nCursor != null){
                    nCursor.moveToFirst();
                    while(!nCursor.isAfterLast()){
                        int movieID = nCursor.getInt(nCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID));
                        if(movieID == id){
                            dbID = nCursor.getInt(nCursor.getColumnIndex(MovieContract.MovieEntry._ID));
                            break;
                        }
                        nCursor.moveToNext();
                    }


                    String stringID = Integer.toString(dbID);
                    Uri uri = MovieContract.MovieEntry.CONTENT_URI;
                    uri = uri.buildUpon().appendPath(stringID).build();

                    int nRows = getContentResolver().delete(uri, null, null);
                    getSupportLoaderManager().restartLoader(LOADER_TASK_ID, null, this);

                    nCursor.close();

                    Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {

            @Override
            protected void onStartLoading() {
                if (mMoviesData != null) {
                    // Delivers any previously loaded data immediately
                    deliverResult(mMoviesData);
                } else {
                    // Force a new load
                    forceLoad();
                }

            }

            @Override
            public Cursor loadInBackground() {
                try {
                    return getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            null);

                } catch (Exception e) {
                    Log.e(TAG, "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            }

            public void deliverResult(Cursor data) {
                mMoviesData = data;
                super.deliverResult(data);
            }

        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMoviesData = data;
        chkFavorite.setChecked((alreadyFavorite(currentMovie.id)));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    boolean alreadyFavorite(int aMovieID){

        boolean isFavorite = false;
        mMoviesData.moveToFirst();
        while(!mMoviesData.isAfterLast() && !isFavorite){
            int movieID = mMoviesData.getInt(mMoviesData.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID));
            if(movieID == aMovieID){
                isFavorite = true;
            }

            mMoviesData.moveToNext();
        }

        return isFavorite;
    }
}
