package com.neerajsingh.popularmovies2.Network;


import com.neerajsingh.popularmovies2.Config;
import com.neerajsingh.popularmovies2.Data.GenresList;
import com.neerajsingh.popularmovies2.Data.MovieList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

/**
 * Created by neeraj.singh on 28/04/16.
 */
public interface BaseRequestInterface {
    @Headers("Accept: application/json")
    @GET("movie/popular?api_key="+ Config.API_KEY)
    Call<MovieList> getPopularMovies();

    @Headers("Accept: application/json")
    @GET("movie/popular?api_key="+ Config.API_KEY)
    Call<MovieList> getPopularMovies(@Query("page") int page);

    @Headers("Accept: application/json")
    @GET("movie/top_rated?api_key="+ Config.API_KEY)
    Call<MovieList> getTopRated();

    @Headers("Accept: application/json")
    @GET("movie/top_rated?api_key="+ Config.API_KEY)
    Call<MovieList> getTopRated(@Query("page") int page);

    @Headers("Accept: application/json")
    @GET("genre/movie/list?api_key="+ Config.API_KEY)
    Call<GenresList> getGenres();

}
