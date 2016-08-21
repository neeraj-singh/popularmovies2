package com.neerajsingh.popularmovies2.Data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by neeraj.singh on 21/08/16.
 */
public class MovieContract {
    public static final String CONTENT_AUTHORITY = "com.neerajsingh.android.popularmovies.app";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_FAV_MOVIE = "favmovie";

    public static final class MovieEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAV_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAV_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAV_MOVIE;


        public static final String TABLE_NAME = "MOVIE";
        public static final String COLUMN_MOVIE_ID = "COLUMN_MOVIE_ID";
        public static final String COLUMN_MOVIE = "COLUMN_MOVIE";
        public static final String COLUMN_IS_FAV = "COLUMN_IS_FAV";

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
