package com.example.android.popularmoviesstage2.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.android.popularmoviesstage2.R;
import com.example.android.popularmoviesstage2.TabsParent;
import com.example.android.popularmoviesstage2.adapters.Review;
import com.example.android.popularmoviesstage2.adapters.ReviewAdapter;
import com.example.android.popularmoviesstage2.utilities.MyConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class reviewsFragment extends Fragment implements ReviewAdapter.IDoListen {

    TabsParent parentActivity;
    ArrayList<Review> mReviews;
    RecyclerView mReviewsRecycler;
    ReviewAdapter mReviewAdapter;
    LinearLayoutManager linLayoutManager;
    DividerItemDecoration decoration;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mReviewAdapter = new ReviewAdapter(getActivity(), mReviews, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        parentActivity = ((TabsParent)getActivity());
        View myView = inflater.inflate(R.layout.fragment_reviews, container, false);

        linLayoutManager = new LinearLayoutManager(getContext());
        mReviewsRecycler = (RecyclerView) myView.findViewById(R.id.fragRecycler);
        decoration = new DividerItemDecoration(getContext(), linLayoutManager.getOrientation());
        mReviewsRecycler.addItemDecoration(decoration);
        mReviewsRecycler.setLayoutManager(linLayoutManager);

        int movieID = parentActivity.sendMovieIDforReview();
        downloadReviews.execute(Integer.toString(movieID));

        // Inflate the layout for this fragment
        return myView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList("reviews", mReviews);
    }

    AsyncTask<String, Void, JSONObject> downloadReviews = new AsyncTask<String, Void, JSONObject>() {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mReviewsRecycler.setAdapter(null);
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            String movieID = params[0];
            JSONObject jsonResponse = null;
            Uri uri = Uri.parse(MyConstants.BASE_URL + "/" + movieID + "/reviews").buildUpon()
                    .appendQueryParameter(MyConstants.API_KEY, MyConstants.dAPIkey)
                    .build();
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(uri.toString()).build();
                Response response = client.newCall(request).execute();
                jsonResponse = new JSONObject(response.body().string());
            } catch (JSONException jEx){
                Toast.makeText(getActivity(), "JSONexp: " + jEx.getMessage(), Toast.LENGTH_LONG).show();
            } catch (IOException Iex){
                Toast.makeText(getActivity(), "IOexp" + Iex.getMessage(), Toast.LENGTH_LONG).show();
            }

            return jsonResponse;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {

            mReviews = new ArrayList<Review>();

            if(jsonObject != null){
                try {
                    JSONArray jArrReviews = jsonObject.getJSONArray("results");
                    for(int i = 0; i < jArrReviews.length(); i++){
                        JSONObject aReview = jArrReviews.getJSONObject(i);
                        String id = aReview.getString("id");
                        String author = aReview.getString("author");
                        String content = aReview.getString("content");
                        String url = aReview.getString("url");

                        Review rev = new Review(id, author, content, url);
                        mReviews.add(rev);
                    }

                    mReviewAdapter = new ReviewAdapter(getActivity(), mReviews, reviewsFragment.this);
                    mReviewsRecycler.setAdapter(mReviewAdapter);
                    mReviewAdapter.notifyDataSetChanged();

                } catch (JSONException jEx){
                    Toast.makeText(getActivity(), "Post Exec JSON exp: " + jEx.getMessage(), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getActivity(), "Null Server Response!", Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    public void onReviewClicked(int clickedPos) {
        Review aReview = mReviews.get(clickedPos);
        //Intent to go to url
        String url = aReview.url;
        Intent webIntent = new Intent(Intent.ACTION_VIEW);
        webIntent.setData(Uri.parse(url));
        startActivity(webIntent);
    }


}
