package com.example.android.popularmoviesstage2.utilities;

/**
 * Created by 48101040 on 5/14/2017.
 */

public class SearchCriteria {

    public String mSearchCriteria;

    public SearchCriteria(String criteria){
        switch (criteria){
            case "popular":
                this.mSearchCriteria = "POPULAR";
                break;
            case "top rated":
                this.mSearchCriteria = "TOP RATED";
                break;
            case "favorites":
                this.mSearchCriteria = "FAVORITES";
                break;
            default:
                break;
        }
    }
}
