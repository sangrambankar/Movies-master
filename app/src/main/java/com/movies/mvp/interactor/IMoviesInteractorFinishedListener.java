package com.movies.mvp.interactor;


import com.movies.models.Movie;

import java.util.List;

public interface IMoviesInteractorFinishedListener {

    /**
     *  Gets the list of movies and then send it to the Presenter.
     * @param movies list of movies.
     */
    void onNetworkSuccess(final List<Movie> movies);

    /**
     *  if there is a problem, it will get an error message.
     * @param message An error from the server.
     */
    void onNetworkFailure(final String message);
}
