package com.example.android.popularmoviesstage2.tasks;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.android.popularmoviesstage2.adapters.Movie;
import com.example.android.popularmoviesstage2.adapters.MovieAdapter;
import com.example.android.popularmoviesstage2.utilities.MyConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by 48101040 on 5/16/2017.
 */

public class MovieDownload {
    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;
    private String mSearchCriteria;
    private Uri mDownloadURI;
    //private GridLayoutManager mGridLayout;
    private ArrayList<Movie> mMovieArrayList;
    private Context mContext;
    private MovieAdapter.IListenToClicks mClickListener;

    public MovieDownload(Context ctx, String searchCriteria, RecyclerView recyclerView, MovieAdapter.IListenToClicks listnr){
        this.mContext = ctx;
        this.mSearchCriteria = searchCriteria;
        this.mRecyclerView = recyclerView;
        this.mClickListener = listnr;
        this.mDownloadURI = buildAndroidURI(searchCriteria);
        doDownload();
    }

    private  void doDownload(){

        Toast.makeText(mContext, "...Downloading...Please wait...", Toast.LENGTH_LONG).show();

        AsyncTask<String, Void, JSONObject> myNetworkTask = new AsyncTask<String, Void, JSONObject>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mRecyclerView.setAdapter(null);
            }

            @Override
            protected JSONObject doInBackground(String... params) {

                JSONObject dJSONObj = null;

                try{
                    OkHttpClient myClient = new OkHttpClient();
                    Request myRequest = new Request.Builder().url(mDownloadURI.toString()).build();
                    Response myResponse = myClient.newCall(myRequest).execute();
                    dJSONObj = new JSONObject(myResponse.body().string());
                } catch (IOException ex){
                    ex.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return dJSONObj;
            }

            @Override
            protected void onPostExecute(JSONObject jsonObject) {

                if(jsonObject == null){
                    Toast.makeText(mContext, "Null JSON", Toast.LENGTH_LONG).show();
                }

                mMovieArrayList = new ArrayList<Movie>();

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
                        mMovieArrayList.add(currentMovie);
                    }

                    //mGridLayout = new GridLayoutManager(mContext, 2);
                    //mRecyclerView.setLayoutManager(mGridLayout);
                    //mRecyclerView.setHasFixedSize(true);
                    mMovieAdapter = new MovieAdapter(mContext, mMovieArrayList, mClickListener);
                    mRecyclerView.setAdapter(mMovieAdapter);
                    mMovieAdapter.notifyDataSetChanged();                    //important

                } catch (JSONException e) {
                    Toast.makeText(mContext, "JSON: " + e.getMessage(), Toast.LENGTH_LONG).show();
                } catch (Exception ex){
                    Toast.makeText(mContext, "Exp: " + ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

    private Uri buildAndroidURI(String searCriteria){
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
                builtUri = null;
                break;
            default:
                builtUri = null;
                break;
        }

        return builtUri;
    }


}
