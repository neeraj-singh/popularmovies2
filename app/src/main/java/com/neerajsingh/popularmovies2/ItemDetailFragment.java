package com.neerajsingh.popularmovies2;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.neerajsingh.popularmovies2.Data.Movie;
import com.neerajsingh.popularmovies2.Data.MovieContract;
import com.neerajsingh.popularmovies2.Network.PopularMoviesApplication;
import com.neerajsingh.popularmovies2.dummy.DummyContent;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ItemDetailFragment extends Fragment {
    public static final String MOVIE_DETAIL = "MOVIE_DETAIL";
    public static final int TIME_ADJUSTER = 1900;

    private Movie movie;
    boolean isFav = false;
    private ImageView fav;

    public ItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(MOVIE_DETAIL)) {
            movie = getArguments().getParcelable(MOVIE_DETAIL);
            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null && movie != null && !TextUtils.isEmpty(movie.getTitle())) {
                appBarLayout.setTitle(movie.getTitle());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movie_detail, container, false);

        Bundle extra = getArguments();

        if(extra !=null && extra.containsKey(MOVIE_DETAIL) ){
            movie = extra.getParcelable(MOVIE_DETAIL);
        }
        String title = movie.getTitle();
        try {
            title = getMovieTitle();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ((TextView)rootView.findViewById(R.id.movie_title)).setText(title);
        ((TextView)rootView.findViewById(R.id.release_date)).setText(movie.getReleaseDate());
        ((TextView)rootView.findViewById(R.id.rating)).setText(movie.getVoteAverage()+"/10");
        ((TextView)rootView.findViewById(R.id.movie_description)).setText(movie.getOverview());
        TextView textView = ((TextView)rootView.findViewById(R.id.vote_count));
        fav = (ImageView) rootView.findViewById(R.id.favIcon);
        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isFav) {
                    isFav = true;
                    changeIcon(v, isFav);
                    insertInDb(movie);
                }else{
                    isFav = false;
                    changeIcon(v, isFav);
                    deleteFromDb(movie);
                }
            }
        });
        if(textView!=null) {
            textView.setText(String.format(getContext().getResources().getString(R.string.vote_count),movie.getVoteCount()));
        }

        StringBuilder genre = new StringBuilder();
        for(Integer gen : movie.getGenreIds()){
            if(Utils.genresMap.containsKey(gen)){
                genre.append(" | "+Utils.genresMap.get(gen));
            }
        }
        TextView genreTextView = (TextView) rootView.findViewById(R.id.genre);
        if(genre.length()>0) {
            genreTextView.setText(genre.toString().substring(3));
        }else{
            genreTextView.setVisibility(View.GONE);
        }

        Picasso.with(getContext()).load(
                PopularMoviesApplication.BASE_IMAGE_URL+PopularMoviesApplication.POSTER_SIZE+"/"+movie.getPosterPath())
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_error)
                .into((ImageView) rootView.findViewById(R.id.imageView));


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Cursor cursor = getContext().getContentResolver().query(MovieContract.MovieEntry.buildMovieUri(movie.getId()),
                        null,null,null,null);
                if(cursor!=null && cursor.moveToFirst()){
                    if(cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_IS_FAV)) == 1){
                        changeIcon(fav,true);
                    }else{
                        changeIcon(fav,false);
                    }
                }
            }
        });
    }

    private void deleteFromDb(final Movie movie) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                getContext().getContentResolver().delete(MovieContract.MovieEntry.buildMovieUri(movie.getId()),
                        MovieContract.MovieEntry.COLUMN_MOVIE_ID +" = ? " ,
                        new String[]{String.valueOf(movie.getId())});
            }
        });
    }

    private void changeIcon(View v,boolean set) {
        if(set){
            v.setBackgroundColor(Color.RED);
        }else{
            v.setBackgroundColor(Color.WHITE);
        }
    }

    private void insertInDb(final Movie movie) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                ContentValues values = new ContentValues();
                values.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID,movie.getId());
                values.put(MovieContract.MovieEntry.COLUMN_MOVIE,new Gson().toJson(movie));
                values.put(MovieContract.MovieEntry.COLUMN_IS_FAV, isFav ? 1 : 0);
                getContext().getContentResolver().insert(MovieContract.MovieEntry.buildMovieUri(movie.getId()),values);
            }
        });
    }

    private String getMovieTitle() throws ParseException {
        String releaseDate = movie.getReleaseDate();
        DateFormat format = new SimpleDateFormat("yyyy-mm-dd", Locale.ENGLISH);
        Date date = format.parse(releaseDate);
        String name = movie.getOriginalTitle()+" ("+(date.getYear()+ TIME_ADJUSTER)+")";

        return name;
    }

}
