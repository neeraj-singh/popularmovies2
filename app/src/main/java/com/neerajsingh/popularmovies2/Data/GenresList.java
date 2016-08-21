package com.neerajsingh.popularmovies2.Data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by neeraj.singh on 06/05/16.
 */
public class GenresList {
    @SerializedName("genres")
    List<Genres> genresList ;

    public List<Genres> getGenresList() {
        return genresList;
    }

    public void setGenresList(List<Genres> genresList) {
        this.genresList = genresList;
    }
}
