package com.neerajsingh.popularmovies2;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.neerajsingh.popularmovies2.Data.Movie;
import com.neerajsingh.popularmovies2.Data.MovieContract;
import com.neerajsingh.popularmovies2.Data.MovieReviews;
import com.neerajsingh.popularmovies2.Data.MovieVideos;
import com.neerajsingh.popularmovies2.Data.ReviewsList;
import com.neerajsingh.popularmovies2.Data.VideoList;
import com.neerajsingh.popularmovies2.Network.PopularMoviesApplication;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ItemDetailFragment extends Fragment {
    public static final String MOVIE_DETAIL = "MOVIE_DETAIL";
    public static final int TIME_ADJUSTER = 1900;

    private Movie movie;
    boolean isFav = false;
    private ImageView fav;
    private List<MovieReviews> reviews;
    private List<MovieVideos> videos;
    private LinearLayout vidoePanel;
    private LinearLayout reviewPanel;

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

        if (extra != null && extra.containsKey(MOVIE_DETAIL)) {
            movie = extra.getParcelable(MOVIE_DETAIL);
        }
        String title = movie.getTitle();
        try {
            title = getMovieTitle();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        vidoePanel = (LinearLayout) rootView.findViewById(R.id.videoPanel);
        reviewPanel = (LinearLayout) rootView.findViewById(R.id.reviewPanel);
        ((TextView) rootView.findViewById(R.id.movie_title)).setText(title);
        ((TextView) rootView.findViewById(R.id.release_date)).setText(movie.getReleaseDate());
        ((TextView) rootView.findViewById(R.id.rating)).setText(movie.getVoteAverage() + "/10");
        ((TextView) rootView.findViewById(R.id.movie_description)).setText(movie.getOverview());
        TextView textView = ((TextView) rootView.findViewById(R.id.vote_count));
        fav = (ImageView) rootView.findViewById(R.id.favIcon);
        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFav) {
                    isFav = true;
                    changeIcon((ImageView) v, isFav);
                    insertInDb(movie);
                } else {
                    isFav = false;
                    changeIcon((ImageView) v, isFav);
                    deleteFromDb(movie);
                }
            }
        });
        if (textView != null) {
            textView.setText(String.format(getContext().getResources().getString(R.string.vote_count), movie.getVoteCount()));
        }

        StringBuilder genre = new StringBuilder();
        for (Integer gen : movie.getGenreIds()) {
            if (Utils.genresMap.containsKey(gen)) {
                genre.append(" | " + Utils.genresMap.get(gen));
            }
        }
        TextView genreTextView = (TextView) rootView.findViewById(R.id.genre);
        if (genre.length() > 0) {
            genreTextView.setText(genre.toString().substring(3));
        } else {
            genreTextView.setVisibility(View.GONE);
        }

        Picasso.with(getContext()).load(
                PopularMoviesApplication.BASE_IMAGE_URL + PopularMoviesApplication.POSTER_SIZE + "/" + movie.getPosterPath())
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_error)
                .into((ImageView) rootView.findViewById(R.id.imageView));

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                PopularMoviesApplication.getBaseRequestInterface().getReviews(String.valueOf(movie.getId())).enqueue(new Callback<ReviewsList>() {

                    @Override
                    public void onResponse(Call<ReviewsList> call, Response<ReviewsList> response) {
                        if (response != null) {
                            reviews = response.body().getResult();
                            addreviews();
                        }
                    }


                    @Override
                    public void onFailure(Call<ReviewsList> call, Throwable t) {
                    }
                });


                PopularMoviesApplication.getBaseRequestInterface().getVideos(String.valueOf(movie.getId())).enqueue(new Callback<VideoList>() {
                    @Override
                    public void onResponse(Call<VideoList> call, Response<VideoList> response) {
                        videos = response.body().getResult();
                        addVideos();
                    }

                    @Override
                    public void onFailure(Call<VideoList> call, Throwable t) {
                    }
                });
            }
        });

        return rootView;
    }

    private void addVideos() {
        if (videos != null && videos.size() > 0) {
            vidoePanel.addView(addTitle("Videos"),0);
            int index = 1;
            for (MovieVideos video : videos) {
                vidoePanel.addView(getTrailerView(video),index++);
            }
        }
    }

    private View addTitle(String title) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.title_view,null);
        TextView tvt = (TextView) view.findViewById(R.id.groupTitle);
        tvt.setText(title);
        return view;
    }

    private void addreviews() {
        if (reviews != null && reviews.size() > 0) {
            reviewPanel.addView(addTitle("Reviews"),0);
            int index = 1;
            for (MovieReviews review : reviews) {
                reviewPanel.addView(getReviewView(review),index++);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Cursor cursor = getContext().getContentResolver().query(MovieContract.MovieEntry.buildMovieUri(movie.getId()),
                        null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    if (cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_IS_FAV)) == 1) {
                        changeIcon(fav, true);
                    } else {
                        changeIcon(fav, false);
                    }
                    cursor.close();
                }
            }
        });
    }

    private void deleteFromDb(final Movie movie) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                getContext().getContentResolver().delete(MovieContract.MovieEntry.buildMovieUri(movie.getId()),
                        MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ? ",
                        new String[]{String.valueOf(movie.getId())});
            }
        });
    }

    private void changeIcon(ImageView v, boolean set) {
        if (set) {
            v.setImageResource(R.drawable.ic_favorite_black_fill);
        } else {
            v.setImageResource(R.drawable.ic_favorite_black);
        }
    }

    private void insertInDb(final Movie movie) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                ContentValues values = new ContentValues();
                values.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie.getId());
                values.put(MovieContract.MovieEntry.COLUMN_MOVIE, new Gson().toJson(movie));
                values.put(MovieContract.MovieEntry.COLUMN_IS_FAV, isFav ? 1 : 0);
                getContext().getContentResolver().insert(MovieContract.MovieEntry.buildMovieUri(movie.getId()), values);
            }
        });
    }

    private String getMovieTitle() throws ParseException {
        String releaseDate = movie.getReleaseDate();
        DateFormat format = new SimpleDateFormat("yyyy-mm-dd", Locale.ENGLISH);
        Date date = format.parse(releaseDate);
        String name = movie.getOriginalTitle() + " (" + (date.getYear() + TIME_ADJUSTER) + ")";

        return name;
    }

    private View getTrailerView(MovieVideos trailer) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.trailer_row, null);
        ((TextView) view.findViewById(R.id.trailer_title)).setText(trailer.getName());
        final String videoUrl;
        if (trailer.getSite().equalsIgnoreCase("Youtube")) {
            videoUrl = "https://www.youtube.com/watch?v=" + trailer.getKey();
        } else {
            videoUrl = trailer.getSite() + "/" + trailer.getKey();
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(videoUrl));
                startActivity(intent);
            }
        });
        return view;
    }

    private View getReviewView(MovieReviews review) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.review_row, null);
        ((TextView) view.findViewById(R.id.auther)).setText(review.getAuthor());
        ((TextView) view.findViewById(R.id.review)).setText(review.getContent());
        return view;
    }
}
