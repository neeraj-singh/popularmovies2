package com.neerajsingh.popularmovies2.Data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by neeraj.singh on 28/04/16.
 */
public class MovieList {

    @SerializedName("page")
    int page;

    @SerializedName("results")
    List<Movie> popularMovies ;

    @SerializedName("total_results")
    int total_results;

    @SerializedName("total_pages")
    int total_pages;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public List<Movie> getPopularMovies() {
        return popularMovies;
    }

    public void setPopularMovies(List<Movie> popularMovies) {
        this.popularMovies = popularMovies;
    }

    public int getTotal_results() {
        return total_results;
    }

    public void setTotal_results(int total_results) {
        this.total_results = total_results;
    }

    public int getTotal_pages() {
        return total_pages;
    }

    public void setTotal_pages(int total_pages) {
        this.total_pages = total_pages;
    }
}
