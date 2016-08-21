package com.neerajsingh.popularmovies2.Data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import com.neerajsingh.popularmovies2.Data.MovieContract.MovieEntry;

/**
 * Created by neeraj.singh on 21/08/16.
 */
public class MovieContentProvider extends ContentProvider {
    private static final int MOVIE_WITH_ID = 2;
    private static final int MOVIE = 1;
    MovieDbHelper dbHelper;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MovieContract.PATH_FAV_MOVIE, MOVIE);
        matcher.addURI(authority, MovieContract.PATH_FAV_MOVIE + "/*", MOVIE_WITH_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        dbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final int match = sUriMatcher.match(uri);
        Cursor retCursor = null;
        switch (match) {
            case MOVIE:
                retCursor = getMovies();
                break;
            case MOVIE_WITH_ID:
                String id = uri.getLastPathSegment();
                retCursor = getFavMovies(id);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    private Cursor getFavMovies(String id) {
        SQLiteDatabase readableDatabase = dbHelper.getReadableDatabase();
        return readableDatabase.query(MovieEntry.TABLE_NAME,null,MovieEntry.COLUMN_MOVIE_ID+" = ? ",new String[]{id},null,null,null);
    }

    private Cursor getMovies() {
        SQLiteDatabase readableDatabase = dbHelper.getReadableDatabase();
        return readableDatabase.query(MovieEntry.TABLE_NAME,null,null,null,null,null,null);
    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIE:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_WITH_ID:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        switch (sUriMatcher.match(uri)) {
            case MOVIE_WITH_ID:
                String id = uri.getLastPathSegment();
                return inertInDb(id,values);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    private Uri inertInDb(String id, ContentValues values) {
        SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();
        long l ;
        try {
            l = writableDatabase.insertOrThrow(MovieContract.MovieEntry.TABLE_NAME, null, values);
        } catch (SQLException e) {
            l = writableDatabase.update(MovieContract.MovieEntry.TABLE_NAME,values,MovieEntry.COLUMN_MOVIE_ID + " = ? ",
                    new String[]{id}  );
        }
        if(l  > 0 ) {
            return MovieContract.MovieEntry.buildMovieUri(l);
        }else {
            return null;
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        switch (sUriMatcher.match(uri)) {
            case MOVIE_WITH_ID:
                return deleteFromDb(uri,selection,selectionArgs);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    private int deleteFromDb(Uri uri, String selection, String[] selectionArgs) {
        return dbHelper.getWritableDatabase().delete(MovieEntry.TABLE_NAME,selection,selectionArgs);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
