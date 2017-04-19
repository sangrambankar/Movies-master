package com.movies.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import com.movies.models.Movie;
import com.movies.database.FavoritesContract.FavoritesMovies;

import java.util.ArrayList;
import java.util.List;


public class FavoritesDBHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "favo.db";
    private static final int DATABASE_VERSION = 1;

    public FavoritesDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_FAVORITES_TABLE = "CREATE TABLE " +
                FavoritesMovies.TABLE_NAME + " (" +
                FavoritesMovies._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FavoritesMovies.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                FavoritesMovies.COLUMN_TITLE + " TEXT NOT NULL, " +
                FavoritesMovies.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                FavoritesMovies.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                FavoritesMovies.COLUMN_POPULARITY + " REAL NOT NULL, " +
                FavoritesMovies.COLUMN_VOTE_AVERAGE + " REAL NOT NULL, " +
                FavoritesMovies.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                FavoritesMovies.COLUMN_BACKDROP_PATH + " TEXT NOT NULL, " +
                FavoritesMovies.COLUMN_GENRES + " TEXT NOT NULL" + ");";

        db.execSQL(SQL_CREATE_FAVORITES_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      db.execSQL("DROP TABLE IF EXISTS "+ FavoritesMovies.TABLE_NAME);
      onCreate(db);
    }


    private static final int INDEX_COLUMN_ID = 1;
    private static final int INDEX_COLUMN_TITLE = 2;
    private static final int INDEX_COLUMN_OVERVIEW = 3;
    private static final int INDEX_COLUMN_DATE = 4;
    private static final int INDEX_COLUMN_POPULARITY = 5;
    private static final int INDEX_COLUMN_VOTE = 6;
    private static final int INDEX_COLUMN_POSTER = 7;
    private static final int INDEX_COLUMN_BACKDROP = 8;
    private static final int INDEX_COLUMN_GENRES = 9;

    public static List<Movie> getAllFavoriteMovies(Context context) {
        List<Movie> favoriteList = new ArrayList<>();
        FavoritesDBHelper dbHelper = new FavoritesDBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.query(FavoritesMovies.TABLE_NAME, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Movie movie = new Movie(
                        Integer.parseInt(
                        cursor.getString(INDEX_COLUMN_ID)),
                        cursor.getString(INDEX_COLUMN_TITLE),
                        cursor.getString(INDEX_COLUMN_OVERVIEW),
                        cursor.getString(INDEX_COLUMN_DATE),
                        cursor.getFloat(INDEX_COLUMN_POPULARITY),
                        cursor.getFloat(INDEX_COLUMN_VOTE),
                        cursor.getString(INDEX_COLUMN_POSTER),
                        cursor.getString(INDEX_COLUMN_BACKDROP),
                        cursor.getString(INDEX_COLUMN_GENRES));
                favoriteList.add(movie);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();

        return favoriteList;
    }
}
