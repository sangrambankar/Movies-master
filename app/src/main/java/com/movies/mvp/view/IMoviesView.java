package com.movies.mvp.view;


import com.movies.models.Movie;

import java.util.List;

public interface IMoviesView {

    /**
     * Displays a RecyclerView with a list of movies.
     * @param movies list of movies
     */
    void onLoadedSuccess(final List<Movie> movies);


    /**
     * Shows a TextView
     * @param message An error from the Api.
     */
    void onLoadedFailure(final String message);
}
