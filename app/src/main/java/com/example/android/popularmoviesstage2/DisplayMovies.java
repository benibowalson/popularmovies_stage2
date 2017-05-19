package com.example.android.popularmoviesstage2;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmoviesstage2.adapters.CustomCursorAdapter;
import com.example.android.popularmoviesstage2.adapters.FavoriteMovie;
import com.example.android.popularmoviesstage2.adapters.Movie;
import com.example.android.popularmoviesstage2.adapters.MovieAdapter;
import com.example.android.popularmoviesstage2.data.MovieContract;
import com.example.android.popularmoviesstage2.utilities.CheckConnectivity;

import java.util.ArrayList;

public class DisplayMovies extends AppCompatActivity implements MovieAdapter.IListenToClicks, LoaderManager.LoaderCallbacks<Cursor> {

    private final int TASK_LOADER_ID = 1;
    private static final String TAG = DisplayMovies.class.getSimpleName();
    private CustomCursorAdapter mFavMoviesAdapter;

    private String mSearchCriteria;
    private RecyclerView myRecycler;
    private GridLayoutManager mGridLayout;
    private MovieAdapter mAdapter;
    private ArrayList<Movie> mMovies;
    private ArrayList<FavoriteMovie> mFavMovies;
    private TextView txtSearchCriteria;
    private Uri mAndroidURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_movies);

        txtSearchCriteria = (TextView)findViewById(R.id.tv_SortOrder);
        txtSearchCriteria.setText(mSearchCriteria);
        mGridLayout = new GridLayoutManager(this, 2);
        myRecycler = (RecyclerView)findViewById(R.id.rv_Recycler);
        myRecycler.setLayoutManager(mGridLayout);

        if(savedInstanceState == null){     //new page
            mSearchCriteria = "POPULAR";
            writePageHeading();
            CheckConnectivity checker = new CheckConnectivity(this, mSearchCriteria, myRecycler, this);
        } else {                            //device rotate, etc
            mSearchCriteria = savedInstanceState.getString("searchCriteria");
            writePageHeading();
                try {
                Toast.makeText(this, mSearchCriteria, Toast.LENGTH_LONG).show();
                if(mSearchCriteria.equals("FAVORITES")){
                    Toast.makeText(this, "Favs", Toast.LENGTH_LONG).show();
                    mFavMovies = new ArrayList<FavoriteMovie>();
                    mFavMovies = savedInstanceState.getParcelableArrayList("favMoviesList");
                    mFavMoviesAdapter = new CustomCursorAdapter(this);
                    if (mFavMovies.size() > 0){
                        myRecycler.setAdapter(mFavMoviesAdapter);
                    } else {
                        Toast.makeText(this, "No Favorites", Toast.LENGTH_LONG).show();
                    }

                } else {

                    mMovies = new ArrayList<Movie>();
                    mMovies = savedInstanceState.getParcelableArrayList("moviesList");
                    if(mMovies == null){
                        Toast.makeText(this, "Null Array", Toast.LENGTH_LONG).show();       //Test: Returns null
                    }

                    mAdapter = new MovieAdapter(this, mMovies, this);
                    myRecycler = (RecyclerView)findViewById(R.id.rv_Recycler);
                    myRecycler.setAdapter(mAdapter);                                    //App Crashes here

                }

            }catch (Exception ex){
                Toast.makeText(this, ex.getMessage(),Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);


        Toast.makeText(this, "Saving: " + mSearchCriteria, Toast.LENGTH_LONG).show();

        outState.putString("searchCriteria", mSearchCriteria);
        if(mSearchCriteria.equals("FAVORITES")){
            outState.putParcelableArrayList("favMoviesList", mFavMovies);
        } else {
            if(mMovies == null){
                Toast.makeText(this, "E don null 0", Toast.LENGTH_LONG).show();
            }
            //Toast.makeText(this, "Putting..." + Integer.toString(mMovies.size()),Toast.LENGTH_LONG).show();
            outState.putParcelableArrayList("moviesList", mMovies);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        txtSearchCriteria.setText(mSearchCriteria);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sort_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedItem = item.getItemId();
        switch (selectedItem){
            case R.id.mnu_popular:
                mSearchCriteria = "POPULAR";
                writePageHeading();
                CheckConnectivity checker = new CheckConnectivity(this, mSearchCriteria, myRecycler, this);
                break;
            case R.id.mnu_topRated:
                mSearchCriteria = "TOP RATED";
                writePageHeading();
                CheckConnectivity aChecker = new CheckConnectivity(this, mSearchCriteria, myRecycler, this);
                break;
            case R.id.mnu_favorites:
                mSearchCriteria = "FAVORITES";
                writePageHeading();
                getFavoriteMovies();
                break;
            default:
                break;
        }

        return true;
    }

    private void getFavoriteMovies() {
        getSupportLoaderManager().initLoader(TASK_LOADER_ID, null, this);
    }

    @Override
    public void onMovieThumbnailClick(int clickedPos) {
        Toast.makeText(this, "...WILL GO TO DETAILS ACTIVITY...", Toast.LENGTH_LONG).show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {

            // Initialize a Cursor, this will hold all the task data
            Cursor mTaskData = null;

            // onStartLoading() is called when a loader first starts loading data
            @Override
            protected void onStartLoading() {
                if (mTaskData != null) {
                    // Delivers any previously loaded data immediately
                    deliverResult(mTaskData);
                } else {
                    // Force a new load
                    forceLoad();
                }
            }

            // loadInBackground() performs asynchronous loading of data
            @Override
            public Cursor loadInBackground() {
                // Will implement to load data

                // Query and load all task data in the background; sort by priority
                // [Hint] use a try/catch block to catch any errors in loading data

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

            // deliverResult sends the result of the load, a Cursor, to the registered listener
            public void deliverResult(Cursor data) {
                mTaskData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        mFavMoviesAdapter = new CustomCursorAdapter(this);
        mFavMoviesAdapter.swapCursor(data);
        if(mFavMoviesAdapter.getItemCount() > 0){
            txtSearchCriteria.setText(mSearchCriteria);
            mGridLayout = new GridLayoutManager(this, 2);
            myRecycler.setAdapter(mFavMoviesAdapter);
        } else {
            Toast.makeText(this, "No Favorites!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mFavMoviesAdapter.swapCursor(null);
    }

    private void writePageHeading(){
        //myRecycler.setAdapter(null);
        txtSearchCriteria.setText(mSearchCriteria.equals("POPULAR")? "MOST POPULAR":(mSearchCriteria.equals("TOP RATED")?"TOP RATED": "FAVORITES"));
    }
}
