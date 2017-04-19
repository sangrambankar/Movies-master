package com.movies;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.movies.adapters.GridMovieAdapter;
import com.movies.database.FavoritesDBHelper;
import com.movies.listeners.RecyclerOnItemClickListener;
import com.movies.listeners.RecyclerViewScrollListener;
import com.movies.models.Movie;
import com.movies.mvp.presenter.MoviesPresenter;
import com.movies.mvp.view.IMoviesView;
import com.movies.restclient.RestUtils;
import com.movies.utility.DeviceHelper;
import com.movies.utility.NetworkHelper;


import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements IMoviesView, RecyclerOnItemClickListener{

    @BindView(R.id.recyclerview_movies)
    RecyclerView mRecyclerView;

    @BindView(R.id.pb_progress)
    ProgressBar mProgress;

    private MoviesPresenter mPresenter;
    private GridMovieAdapter mGridMovieAdapter;
    private GridLayoutManager mGridLayoutManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setUI();
        callAPI();
    }

    private void setUI() {
        mGridLayoutManager = new GridLayoutManager(this, DeviceHelper.calculateNoOfColumns(this));
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        NetworkHelper.addNoInternetView(mRecyclerView, this);
    }

    private void callAPI() {
        mPresenter = new MoviesPresenter(this);
        mPresenter.makeRequest(RestUtils.FIRST_PAGE, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case  R.id.item_popularity:
                setTitle(R.string.app_name);
                callAPI();
                break;

            case R.id.item_rating:
                setTitle(R.string.top_rated_movies);
                if(mPresenter.getTopRatedMovies() != null
                        && !mPresenter.getTopRatedMovies().isEmpty()) {
                    mGridMovieAdapter.updateItems(mPresenter.getTopRatedMovies());
                }
                break;

            case R.id.item_favorite:
                if(FavoritesDBHelper.getAllFavoriteMovies(this).size() > 0) {
                    setTitle(getString(R.string.favorites_movies));
                    mGridMovieAdapter.updateItems(FavoritesDBHelper.getAllFavoriteMovies(this));
                } else {
                    Snackbar.make(mRecyclerView, R.string.no_favorites, Snackbar.LENGTH_LONG).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLoadedSuccess(List<Movie> movies) {
        mProgress.setVisibility(View.GONE);
        if(mGridMovieAdapter == null) {
            mGridMovieAdapter = new GridMovieAdapter(movies, this, getApplicationContext());
        }else{
            mGridMovieAdapter.updateItems(movies);
        }
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mGridMovieAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerViewScrollListener(mGridLayoutManager, mGridMovieAdapter));
    }

    @Override
    public void onLoadedFailure(String message) {
        Log.d(MainActivity.class.getSimpleName(), message);
    }

    @Override
    public void onItemClick(Movie movie) {
        Intent mIntent = new Intent(this, MovieDetailsActivity.class);
        mIntent.putExtra(RestUtils.TAG, movie);
        startActivity(mIntent);
    }
}
