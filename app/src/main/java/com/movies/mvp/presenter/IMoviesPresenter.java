package com.movies.mvp.presenter;


import android.content.Context;

public interface IMoviesPresenter {

    /**
     * Calls the Api to get the list of movies with a page number given and
     * the list of genres as well.
     * @param page number.
     * @param context {@link Context}
     */
    void makeRequest(final int page, final Context context);
}
