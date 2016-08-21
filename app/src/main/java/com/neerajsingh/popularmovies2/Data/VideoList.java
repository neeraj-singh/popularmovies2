package com.neerajsingh.popularmovies2.Data;

import java.util.List;

/**
 * Created by neeraj.singh on 21/08/16.
 */
public class VideoList {
    private List<MovieVideos> results;

    public List<MovieVideos> getResult() {
        return results;
    }

    public void setResult(List<MovieVideos> result) {
        this.results = result;
    }
}
