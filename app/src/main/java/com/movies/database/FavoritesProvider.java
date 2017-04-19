package com.movies.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.movies.R;


public class FavoritesProvider extends ContentProvider{

    public static final int FAVORITES = 100;
    public static final int FAB_MOVIE_ID = 101;
    public UriMatcher sUriMatcher = buildUriMatcher();
    private FavoritesDBHelper mDBHelper;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mDBHelper = new FavoritesDBHelper(context);

        return true;
    }

    public static UriMatcher buildUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(FavoritesContract.AUTHORITY, FavoritesContract.PATH_FAVORITES, FAVORITES);
        uriMatcher.addURI(FavoritesContract.AUTHORITY, FavoritesContract.PATH_FAVORITES + "/#", FAB_MOVIE_ID);

        return uriMatcher;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = mDBHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor returnCursor;

        switch (match) {
            case FAVORITES:
                returnCursor = db.query(FavoritesContract.FavoritesMovies.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException(getContext().getString(R.string.unknown_uri) + uri);

        }

        returnCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return returnCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mDBHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case FAVORITES:
                long id = db.insert(FavoritesContract.FavoritesMovies.TABLE_NAME, null, values);
                if (id > 0) {
                  returnUri = ContentUris.withAppendedId(FavoritesContract.FavoritesMovies.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException(getContext().getString(R.string.failed_insert)+ uri);
                }
                break;
            default:
                throw new android.database.SQLException(getContext().getString(R.string.unknown_uri)+ uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mDBHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int numRowDeleted;

        switch (match) {
            case FAB_MOVIE_ID:
                numRowDeleted = db.delete(FavoritesContract.FavoritesMovies.TABLE_NAME,
                        FavoritesContract.FavoritesMovies.COLUMN_MOVIE_ID + "=?",
                        new String[]{ uri.getLastPathSegment() });
                break;
            default:
                throw new UnsupportedOperationException(getContext().getString(R.string.unknown_uri)+ uri);

        }

        if (numRowDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numRowDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
