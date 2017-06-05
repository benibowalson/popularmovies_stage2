package com.example.android.popularmoviesstage2.fragments;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.example.android.popularmoviesstage2.adapters.Trailer;
import com.example.android.popularmoviesstage2.adapters.TrailerAdapter;
import com.example.android.popularmoviesstage2.utilities.MyConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class trailersFragment extends Fragment implements TrailerAdapter.ITrailerClickListener {

    private TabsParent parentActivity;
    private ArrayList<Trailer> mTrailers;
    private RecyclerView mRecycler;
    private TrailerAdapter mTrailerAdapter;
    private LinearLayoutManager linLayoutManager;
    private DividerItemDecoration decoration;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View myView = inflater.inflate(R.layout.fragment_trailers, container, false);
        linLayoutManager = new LinearLayoutManager(getContext());
        mRecycler = (RecyclerView)myView.findViewById(R.id.fragTrailerRecycler);
        decoration = new DividerItemDecoration(getContext(), linLayoutManager.getOrientation());
        mRecycler.addItemDecoration(decoration);
        mRecycler.setLayoutManager(linLayoutManager);

        parentActivity = ((TabsParent)getActivity());
        int movieID = parentActivity.idForTrailer();
        downloadTrailers.execute(Integer.toString(movieID));
        // Inflate the layout for this fragment
        return myView;
    }

    AsyncTask<String, Void, JSONObject> downloadTrailers = new AsyncTask<String, Void, JSONObject>() {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mRecycler.setAdapter(null);
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            String movieID = params[0];
            JSONObject jsonResponse = null;
            Uri uri = Uri.parse(MyConstants.BASE_URL + "/" + movieID + "/videos").buildUpon()
                    .appendQueryParameter(MyConstants.API_KEY, MyConstants.dAPIkey)
                    .build();
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(uri.toString()).build();
                Response response = client.newCall(request).execute();
                jsonResponse = new JSONObject(response.body().string());

            }  catch (JSONException jEx){
                Toast.makeText(getActivity(), "JSONexp: " + jEx.getMessage(), Toast.LENGTH_LONG).show();
            } catch (IOException Iex){
                Toast.makeText(getActivity(), "IOexp" + Iex.getMessage(), Toast.LENGTH_LONG).show();
            }

            return jsonResponse;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {

            mTrailers = new ArrayList<Trailer>();

            if(jsonObject != null){

                try {
                    JSONArray jArrTrailers = jsonObject.getJSONArray("results");

                    for(int i = 0; i < jArrTrailers.length(); i++){
                        JSONObject aTrailer = jArrTrailers.getJSONObject(i);
                        String id = aTrailer.getString("id");
                        String langCode = aTrailer.getString("iso_639_1");
                        String countryCode = aTrailer.getString("iso_3166_1");
                        String key = aTrailer.getString("key");
                        String name = aTrailer.getString("name");
                        String site = aTrailer.getString("site");
                        int size = aTrailer.getInt("size");
                        String type = aTrailer.getString("type");

                        Trailer trailer = new Trailer(id, langCode, countryCode, key, name, site, size, type);
                        mTrailers.add(trailer);
                    }

                    mTrailerAdapter = new TrailerAdapter(getActivity(), mTrailers, trailersFragment.this);
                    mRecycler.setAdapter(mTrailerAdapter);
                    mTrailerAdapter.notifyDataSetChanged();

                } catch (JSONException jEx){
                    Toast.makeText(getActivity(), "Post Exec JSON exp: " + jEx.getMessage(), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getActivity(), "Null Server Response!", Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    public void onTrailerClicked(int clickedPosition) {
        Trailer aTrailer = mTrailers.get(clickedPosition);
        //Intent to go to url
        String url = "https://www.youtube.com/watch?v=" + aTrailer.key;
        try {
            Intent webIntent = new Intent(Intent.ACTION_VIEW);
            webIntent.setData(Uri.parse(url));
            startActivity(webIntent);

        } catch (ActivityNotFoundException ex){
            Toast.makeText(getActivity(), "Youtube Exp: " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
