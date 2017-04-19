package com.movies.mvp.presenter;

import android.content.Context;
import android.support.annotation.NonNull;


import com.movies.MainActivity;
import com.movies.models.Movie;
import com.movies.mvp.interactor.IMoviesInteractorFinishedListener;
import com.movies.mvp.interactor.MoviesInteractor;
import com.movies.mvp.view.IMoviesView;


import java.lang.ref.WeakReference;
import java.util.List;



public class MoviesPresenter implements IMoviesPresenter, IMoviesInteractorFinishedListener {
    /******************************************************************************************
     MoviesPresenter retrieves data from the model {@link MoviesInteractor } and
     notifies the view {@link  MainActivity } to display it.
     ******************************************************************************************/

    private final MoviesInteractor mInteractor;
    private final WeakReference<IMoviesView> mView;
    private List<Movie> mTopRatedMovies;


    public MoviesPresenter(@NonNull final IMoviesView view) {
        this.mInteractor = new MoviesInteractor(this);
        this.mView = new WeakReference<>(view);
    }

    @Override
    public void makeRequest(final int page, final Context context) {
        mInteractor.loadGenres(context);
        mInteractor.loadPopularMovies(page);
        setTopRatedMovies(mInteractor.loadTopRatedMovies(page));
    }

    @Override
    public void onNetworkSuccess(@NonNull final List<Movie> movies) {
        if(mView.get() != null) mView.get().onLoadedSuccess(movies);
    }

    @Override
    public void onNetworkFailure(final String message) {
        if(mView.get() != null) mView.get().onLoadedFailure(message);
    }


    public List<Movie> getTopRatedMovies() {
        return mTopRatedMovies;
    }

    private void setTopRatedMovies(List<Movie> mTopRatedMovies) {
        this.mTopRatedMovies = mTopRatedMovies;
    }
}
