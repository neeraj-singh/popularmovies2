package com.neerajsingh.popularmovies2.Network;

import android.app.Application;

import com.neerajsingh.popularmovies.Utils;

/**
 * Created by neeraj.singh on 28/04/16.
 */
public class PopularMoviesApplication extends Application {

    public static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";
    public static final String POSTER_SIZE = "w185";
    public static BaseRequestInterface baseRequestInterface;

    @Override
    public void onCreate() {
        super.onCreate();
        initializeRetrofit();
        Utils.getGenres();
    }

    public static void initializeRetrofit() {
        baseRequestInterface = ServiceGenerator.createService(BaseRequestInterface.class);
    }

    public static BaseRequestInterface getBaseRequestInterface() {
        return baseRequestInterface;
    }
}
