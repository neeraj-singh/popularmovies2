package com.neerajsingh.popularmovies2.Data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by neeraj.singh on 06/05/16.
 */
public class Genres {
    @SerializedName("id")
    private Integer id;

    @SerializedName("name")
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
