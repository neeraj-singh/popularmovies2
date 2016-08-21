package com.neerajsingh.popularmovies2;

import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.neerajsingh.popularmovies2.Data.Movie;
import com.neerajsingh.popularmovies2.Data.MovieContract;
import com.neerajsingh.popularmovies2.Data.MovieList;
import com.neerajsingh.popularmovies2.Network.PopularMoviesApplication;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link DetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ItemListActivity extends AppCompatActivity implements NavigationInterface, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int SPAN_1 = 1;
    private static final int SPAN_2 = 2;
    private static final int SPAN_3 = 3;
    private static final int SPAN_4 = 4;
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    protected final String Loader_Message = "Please wait..";
    RecyclerView recyclerView;
    List<Movie> movieList;
    ProgressDialog progressDialog;
    MoviesAdapter moviesAdapter;
    MovieState state ;
    RecyclerView.LayoutManager layoutManager;
    private int ID = 1;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        if (savedInstanceState != null){
            state = (MovieState) savedInstanceState.getSerializable("State");
        }else{
            state = MovieState.popular;
        }

        recyclerView = (RecyclerView) findViewById(R.id.item_list);
        assert recyclerView != null;


        if (findViewById(R.id.item_detail_container) != null) {
            mTwoPane = true;
            if(movieList!=null) {
                openPage(movieList.get(0));
            }
        }
        setupRecyclerView();
    }

    private void getMovies(Call<MovieList> movieListCall) {
        progressDialog = ProgressDialog.show(this, null, Loader_Message);
        progressDialog.setCancelable(true);
        if (movieListCall != null) {
            movieListCall.enqueue(new Callback<MovieList>() {
                @Override
                public void onResponse(Call<MovieList> call, Response<MovieList> response) {
                    progressDialog.dismiss();
                    if (response != null && response.isSuccessful()) {
                        movieList = response.body().getPopularMovies();
                        setAdapter();
                    }
                }

                @Override
                public void onFailure(Call<MovieList> call, Throwable t) {
                    progressDialog.dismiss();
                    setContentView(R.layout.no_net);
                }
            });
        } else {
            beginLoader(ID);
        }
    }

    private void setAdapter() {
        moviesAdapter = new MoviesAdapter(movieList, ItemListActivity.this);
        recyclerView.setAdapter(moviesAdapter);
        moviesAdapter.notifyDataSetChanged();
        if(mTwoPane && movieList!=null) {
            openPage(movieList.get(0));
        }
    }

    private void beginLoader(int id) {
        if (getLoaderManager().getLoader(id) == null) {
            getLoaderManager().initLoader(id, null, this);
        } else {
            getLoaderManager().restartLoader(id, null, this);
        }
    }

    private void setupRecyclerView() {
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = (size.x / Utils.dptopx(this, 156));

            layoutManager = new GridLayoutManager(this, width);
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutManager = new GridLayoutManager(this, mTwoPane ? SPAN_1 : SPAN_3);
        }
        recyclerView.setLayoutManager(layoutManager);
        getMovies(getMoviesHandler(state));

    }


    private Call<MovieList> getMoviesHandler(MovieState state) {
        if (state == MovieState.popular) {
            return PopularMoviesApplication.getBaseRequestInterface().getPopularMovies();
        } else if (state == MovieState.toprated) {
            return PopularMoviesApplication.getBaseRequestInterface().getTopRated();
        } else {
            return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.popular:
                if (state != MovieState.popular) {
                    state = MovieState.popular;
                    toolbar.setTitle(item.getTitle());
                    getMovies(getMoviesHandler(state));
                } else {
                    Toast.makeText(this, "Already showing top rated movies", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.top_rated:
                if (state != MovieState.toprated) {
                    state = MovieState.toprated;
                    toolbar.setTitle(item.getTitle());
                    getMovies(getMoviesHandler(state));
                } else {
                    Toast.makeText(this, "Already showing popular movies", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.fav:
                if (state != MovieState.fav) {
                    state = MovieState.fav;
                    toolbar.setTitle(item.getTitle());
                    getMovies(getMoviesHandler(state));
                } else {
                    Toast.makeText(this, "Already showing fav movies", Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return false;
        }
    }

    @Override
    public void openPage(Movie movie) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(ItemDetailFragment.MOVIE_DETAIL, movie);
            ItemDetailFragment fragment = new ItemDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.item_detail_container, fragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra(ItemDetailFragment.MOVIE_DETAIL, movie);
            startActivity(intent);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == ID) {
            Uri uri = MovieContract.MovieEntry.CONTENT_URI;
            return new CursorLoader(this, uri, null, null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == ID) {
            handleFav(data);
            progressDialog.dismiss();
        }
    }

    private void handleFav(Cursor data) {
        Gson gson = new Gson();
        movieList = new ArrayList<>();
        if (data != null && data.moveToFirst()) {
            int movieIndex = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE);
            do {
                Movie movie = gson.fromJson(data.getString(movieIndex), Movie.class);
                if (movie != null) {
                    movieList.add(movie);
                }
            }
            while (data.moveToNext());
        }
        if (movieList.size() > 0) {
            setAdapter();
        } else {
            setAdapter();
            Toast.makeText(this, "Nothing in fav section", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    private enum MovieState {
        popular, toprated, fav;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("State",state);
        super.onSaveInstanceState(outState);
    }

    public void retryNetworkCall(View view){
        setupRecyclerView();
    }

}

