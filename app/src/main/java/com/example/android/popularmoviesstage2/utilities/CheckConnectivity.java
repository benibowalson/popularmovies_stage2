package com.example.android.popularmoviesstage2.utilities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;

import com.example.android.popularmoviesstage2.NoInternet;
import com.example.android.popularmoviesstage2.adapters.MovieAdapter;
import com.example.android.popularmoviesstage2.tasks.MovieDownload;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by 48101040 on 5/13/2017.
 */

public class CheckConnectivity {

    private Context mContext;
    private MovieAdapter.IListenToClicks mClickListener;
    private RecyclerView mRecyclerView;
    private String mSearchCriteria;

    public CheckConnectivity(Context ctx, String searchCriteria, RecyclerView recycler, MovieAdapter.IListenToClicks listener){
        this.mContext = ctx;
        this.mClickListener = listener;
        this.mSearchCriteria = searchCriteria;
        this.mRecyclerView = recycler;
        doCheckConnectivity();
    }

    void doCheckConnectivity(){
        AsyncTask<Void, Void, Void> interNetCheck = new AsyncTask<Void, Void, Void>() {
            boolean isOnline = false;
            @Override
            protected Void doInBackground(Void... params) {
                try{
                    int timeOut_ms = 5000;
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
                    MovieDownload movieDownload = new MovieDownload(mContext, mSearchCriteria, mRecyclerView, mClickListener);
                } else {
                    mContext.startActivity(new Intent(mContext, NoInternet.class));
                }
            }
        }.execute();

    }

}
