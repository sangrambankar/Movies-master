package com.movies.mvp.interactor;


import android.content.Context;
import android.util.Log;

import com.movies.mvp.presenter.MoviesPresenter;
import com.movies.models.Movie;
import com.movies.restclient.IRestClient;
import com.movies.restclient.RestUtils;
import com.movies.utility.SharedPrefsHelper;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MoviesInteractor {
    /***********************************************************************************************
     - This is the model that gets the data from the Api and provides it to the presenter
      {@link MoviesPresenter} to display it on the view.
     ***********************************************************************************************/

    private final IMoviesInteractorFinishedListener mInteractorListener;
    private final IRestClient mRestClient;
    private List<Movie> mTopRatedMovies;


    public MoviesInteractor(IMoviesInteractorFinishedListener mInteractorListener) {
        this.mInteractorListener = mInteractorListener;
        this.mRestClient = RestUtils.createRestClient();
    }

    public void loadPopularMovies(final int page){
        mRestClient.getPopularMovies(page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> {
                            /* if successful, notifies to the presenter so that it can get a list of movies.*/
                            mInteractorListener.onNetworkSuccess(response.getResults());
                        },

                        Throwable -> {
                            //if fails, gets the error message from the server and then notifies to the presenter.
                            mInteractorListener.onNetworkFailure(Throwable.getMessage());
                        }
                );

    }

    public List<Movie> loadTopRatedMovies(final int page){
        if(mTopRatedMovies == null){
            mTopRatedMovies = new ArrayList<>();
        }
        mRestClient.getTopRatedMovies(page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> {
                            mTopRatedMovies.addAll(response.getResults());
                        },

                        Throwable -> {
                            mInteractorListener.onNetworkFailure(Throwable.getMessage());
                        }
                );
        return mTopRatedMovies;
    }

    public void loadGenres(final Context context) {
        mRestClient.getGenres()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> {
                            SharedPrefsHelper.saveListOfGenres(context, response.getGenres());
                        },

                        Throwable -> {
                            Log.d(MoviesInteractor.class.getName(), Throwable.getMessage());
                        }
                );

    }
}
