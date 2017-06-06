package com.example.android.popularmoviesstage2;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
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
import com.example.android.popularmoviesstage2.adapters.Movie;
import com.example.android.popularmoviesstage2.adapters.MovieAdapter;
import com.example.android.popularmoviesstage2.data.MovieContract;
import com.example.android.popularmoviesstage2.utilities.MyConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DisplayMovies extends AppCompatActivity
        implements MovieAdapter.IListenToClicks, CustomCursorAdapter.IFavClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private final int TASK_LOADER_ID = 0;
    private static final String TAG = DisplayMovies.class.getSimpleName();
    private CustomCursorAdapter mFavMoviesAdapter;

    private String mSearchCriteria;
    private GridLayoutManager mGridLayout;
    private RecyclerView myRecycler;
    private MovieAdapter mMovieAdapter;
    private ArrayList<Movie> mMovies;
    private Cursor mCursorFavMovies;
    private TextView txtSearchCriteria;
    private Uri mDownloadURI;
    private boolean DeviceIsOnline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_movies);

        txtSearchCriteria = (TextView)findViewById(R.id.tv_SortOrder);
        txtSearchCriteria.setText(mSearchCriteria);
        mGridLayout = new GridLayoutManager(this, 2);
        myRecycler = (RecyclerView)findViewById(R.id.rv_Recycler);
        myRecycler.setLayoutManager(mGridLayout);

        mMovies = new ArrayList<Movie>();

        if(savedInstanceState == null){     //new page
            mSearchCriteria = "POPULAR";
            writePageHeading();
            new interNetCheck().execute();
        } else {                            //device rotate, etc
            mSearchCriteria = savedInstanceState.getString("searchCriteria");
            writePageHeading();
            try {
                if(mSearchCriteria.equals("FAVORITES")){
                    getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, this);
                } else {

                    mMovies = new ArrayList<Movie>();
                    mMovies = savedInstanceState.getParcelableArrayList("moviesList");
                    if(mMovies == null){
                        Toast.makeText(this, "Null Array", Toast.LENGTH_LONG).show();
                    } else {
                        mMovieAdapter = new MovieAdapter(this, mMovies, this);
                        myRecycler.setAdapter(mMovieAdapter);
                    }
                }

            } catch (Exception ex){
                Toast.makeText(this, ex.getMessage(),Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("searchCriteria", mSearchCriteria);
        outState.putParcelableArrayList("moviesList", mMovies);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
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
                new interNetCheck().execute();
                break;
            case R.id.mnu_topRated:
                mSearchCriteria = "TOP RATED";
                writePageHeading();
                new interNetCheck().execute();
                break;
            case R.id.mnu_favorites:
                mSearchCriteria = "FAVORITES";
                writePageHeading();
                getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, this);
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

        try {
            Movie clickedMovie = mMovies.get(clickedPos);
            Intent myIntent = new Intent(this, TabsParent.class);
            Bundle bundleOfExtras = new Bundle();
            bundleOfExtras.putParcelable("Movie_Object", clickedMovie);
            myIntent.putExtras(bundleOfExtras);                     //Extras: PLURAL
            startActivity(myIntent);
        }catch(Exception ex){
            Log.e(this.getClass().getSimpleName(), ex.getMessage());
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onFavMovieClicked(int anID) {
        Toast.makeText(this, "Fav Mov clicked", Toast.LENGTH_LONG).show();
        /*Intent intent = new Intent(this, TabsParent.class);
        intent.putExtra("movieID", anID);
        startActivity(intent);
        */
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

        mCursorFavMovies = data;

        if(mSearchCriteria.equals("FAVORITES")){

            mFavMoviesAdapter = new CustomCursorAdapter(this, this);
            mFavMoviesAdapter.swapCursor(mCursorFavMovies);

            myRecycler.setAdapter(mFavMoviesAdapter);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mFavMoviesAdapter.swapCursor(null);
    }

    private void writePageHeading(){
        myRecycler.setAdapter(null);
        txtSearchCriteria.setText(mSearchCriteria.equals("POPULAR")? "MOST POPULAR":(mSearchCriteria.equals("TOP RATED")? "TOP RATED":"FAVORITES"));
    }


    private class interNetCheck extends AsyncTask<Void, Void, Void>{
        boolean isOnline = false;
        @Override
        protected Void doInBackground(Void... params) {
            try{
                int timeOut_ms = 6000;
                Socket aSock = new Socket();
                SocketAddress aSockAdd = new InetSocketAddress("8.8.8.8", 53);
                aSock.connect(aSockAdd, timeOut_ms);
                aSock.close();
                isOnline = true;
            } catch (IOException ex){
                isOnline = false;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(isOnline){
                new doDownload().execute();
            } else {
                startActivity(new Intent(DisplayMovies.this, NoInternet.class));
            }
        }
    }

    private class doDownload extends AsyncTask<String, Void, JSONObject>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            myRecycler.setAdapter(null);
            buildAndroidURI();
            Toast.makeText(DisplayMovies.this,"...Downloading, please wait...", Toast.LENGTH_LONG).show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject dJSONObj = null;

            try{
                OkHttpClient myClient = new OkHttpClient();
                Request myRequest = new Request.Builder().url(mDownloadURI.toString()).build();
                Response myResponse = myClient.newCall(myRequest).execute();
                dJSONObj = new JSONObject(myResponse.body().string());
            } catch (JSONException ex){
                ex.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return dJSONObj;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {

            mMovies = new ArrayList<Movie>();

            try {

                JSONArray arrMovies = jsonObject.getJSONArray("results");

                for (int i = 0; i < arrMovies.length(); i++){
                    JSONObject aMovie = arrMovies.getJSONObject(i);
                    String poster_path = MyConstants.IMAGE_BASE_URL + MyConstants.IMAGE_PREFERED_SIZE + aMovie.getString("poster_path");
                    String overview = aMovie.getString("overview");
                    boolean adult = aMovie.getBoolean("adult");
                    String release_date = aMovie.getString("release_date");
                    JSONArray genreIDs = aMovie.getJSONArray("genre_ids");
                    List<Integer> genre_ids = new ArrayList<>();
                    for(int j = 0; j < genreIDs.length(); j++){
                        genre_ids.add(genreIDs.getInt(j));
                    }

                    int id = aMovie.getInt("id");
                    String original_title = aMovie.getString("original_title");
                    String original_language = aMovie.getString("original_language");
                    String title = aMovie.getString("title");
                    String backdrop_path = MyConstants.IMAGE_BASE_URL + MyConstants.IMAGE_PREFERED_SIZE + aMovie.getString("backdrop_path");
                    double popularity = aMovie.getDouble("popularity");
                    int vote_count = aMovie.getInt("vote_count");
                    boolean video = aMovie.getBoolean("video");
                    double vote_average = aMovie.getDouble("vote_average");

                    Movie currentMovie = new Movie(poster_path, overview, adult, release_date,genre_ids, id, original_title, original_language, title, backdrop_path, popularity, vote_count, video, vote_average);
                    mMovies.add(currentMovie);
                }

                mMovieAdapter = new MovieAdapter(DisplayMovies.this, mMovies, DisplayMovies.this);
                myRecycler.setAdapter(mMovieAdapter);
                mMovieAdapter.notifyDataSetChanged();                    //important

            } catch (JSONException e) {
                Toast.makeText(DisplayMovies.this, "JSON: " + e.getMessage(), Toast.LENGTH_LONG).show();
            } catch (Exception ex){
                Toast.makeText(DisplayMovies.this, "Exp: " + ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void buildAndroidURI(){
        Uri builtUri;

        switch (mSearchCriteria){
            case "POPULAR":
               /* builtUri = Uri.parse(MyConstants.BASE_URL).buildUpon()          //.buildUpon() serves as "?" - the question mark
                        .appendQueryParameter(MyConstants.SORT_PARAM_KEY, MyConstants.POPULARITY_VALUE)
                        .appendQueryParameter(MyConstants.API_KEY, MyConstants.dAPIkey)
                        .build();
                */
                builtUri = Uri.parse(MyConstants.BASE_URL_POPULAR).buildUpon()
                        .appendQueryParameter(MyConstants.API_KEY, MyConstants.dAPIkey)
                        .build();
                break;
            case "TOP RATED":
                /*builtUri = Uri.parse(MyConstants.BASE_URL).buildUpon()
                        .appendQueryParameter(MyConstants.CERT_COUNTRY_KEY, MyConstants.dCertCountry)
                        .appendQueryParameter(MyConstants.CERT_TYPE_KEY, MyConstants.dCertType)
                        .appendQueryParameter(MyConstants.SORT_PARAM_KEY, MyConstants.TOP_RATE_VALUE)
                        .appendQueryParameter(MyConstants.API_KEY, MyConstants.dAPIkey)
                        .build();
                */
                builtUri = Uri.parse(MyConstants.BASE_URL_TOP_RATED).buildUpon()
                        .appendQueryParameter(MyConstants.API_KEY, MyConstants.dAPIkey)
                        .build();

                break;
            case "FAVORITES":
                builtUri = Uri.parse(null);
                break;
            default:
                builtUri = Uri.parse(null);
                break;
        }

        mDownloadURI = builtUri;
    }
}
