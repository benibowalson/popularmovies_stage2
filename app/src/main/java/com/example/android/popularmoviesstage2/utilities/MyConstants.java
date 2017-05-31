package com.example.android.popularmoviesstage2.utilities;

/**
 * Created by 48101040 on 4/8/2017.
 */

public class MyConstants {
    public static final String API_KEY = "api_key";
    public static final String dAPIkey = "[API_KEY]";
    public static final String BASE_URL = "https://api.themoviedb.org/3/movie";
    public static final String BASE_URL_X = "https://api.themoviedb.org/3/discover/movie";
    public static final String BASE_URL_POPULAR = BASE_URL + "/popular";
    public static final String BASE_URL_TOP_RATED = BASE_URL + "/top_rated";
    public static final String SORT_PARAM_KEY = "sort_by";
    public static final String POPULARITY_VALUE = "popular";
    public static final String POPULARITY_VALUE_2 = "popular.desc";
    public static final String TOP_RATE_VALUE = "top_rated";
    public static final String TOP_RATE_VALUE_2 = "vote_average.desc";
    public static final String CERT_COUNTRY_KEY = "certification_country";
    public static final String dCertCountry = "US";
    public static final String CERT_TYPE_KEY = "certification";
    public static final String dCertType = "R";
    public static final String LANGUAGE_KEY = "language";
    public static final String dLanguageValue = "en-US";
    public static final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p";
    public static final String IMAGE_PREFERED_SIZE = "/w185";
    public static final String USER_AUTH_URL = "https://api.themoviedb.org/3/authentication/token/new";
}
