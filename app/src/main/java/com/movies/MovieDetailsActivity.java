package com.movies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.movies.database.FavoritesContract.FavoritesMovies;
import com.movies.database.FavoritesDBHelper;
import com.movies.models.Movie;
import com.movies.models.Review;
import com.movies.models.Trailer;
import com.movies.restclient.IRestClient;
import com.movies.restclient.RestUtils;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MovieDetailsActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.iv_movie_backdrop)
    ImageView mBackdropImageView;

    @BindView(R.id.iv_poster_movie)
    ImageView mPosterImageView;

    @BindView(R.id.tv_genres_movie)
    TextView mGenresTextView;

    @BindView(R.id.tv_release_date_movie)
    TextView mReleaseDateTextView;

    @BindView(R.id.tv_vote_average_movie)
    TextView mVoteAverageTextView;

    @BindView(R.id.tv_overview_movie)
    TextView mOverViewTextView;

    @BindView(R.id.layout_trailers)
    LinearLayout mListTrailers;

    @BindView(R.id.layout_reviews)
    LinearLayout mListReviews;

    @BindView(R.id.fab)
    FloatingActionButton mFavorite;

    private Movie mMovie;
    private SQLiteDatabase mDb;
    private final IRestClient mRestClient = RestUtils.createRestClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);
        getMovieDetails();
        setCellValues();
        setUpToolbar();
        setDataBase();
        setFloatingActionButton();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(RestUtils.TAG, mMovie);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mMovie = savedInstanceState.getParcelable(RestUtils.TAG);
        setCellValues();
    }

    private void setUpToolbar() {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle((mMovie != null) ? mMovie.getTitle() : getTitle());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setDataBase() {
        FavoritesDBHelper dbHelper = new FavoritesDBHelper(this);
        mDb = dbHelper.getWritableDatabase();
    }

    private void setFloatingActionButton() {
        mFavorite.setOnClickListener(v -> {

            if (!isFavoriteMovie(mMovie.getId())) {
                ContentValues values = new ContentValues();
                values.put(FavoritesMovies.COLUMN_MOVIE_ID, mMovie.getId());
                values.put(FavoritesMovies.COLUMN_TITLE, mMovie.getTitle());
                values.put(FavoritesMovies.COLUMN_OVERVIEW, mMovie.getOverview());
                values.put(FavoritesMovies.COLUMN_RELEASE_DATE, mMovie.getRelease_date());
                values.put(FavoritesMovies.COLUMN_POPULARITY, mMovie.getPopularity());
                values.put(FavoritesMovies.COLUMN_VOTE_AVERAGE, mMovie.getVote_average());
                values.put(FavoritesMovies.COLUMN_POSTER_PATH, mMovie.getPoster());
                values.put(FavoritesMovies.COLUMN_BACKDROP_PATH, mMovie.getBackdrop());
                values.put(FavoritesMovies.COLUMN_GENRES, mMovie.getGenres(this));

                getContentResolver().insert(FavoritesMovies.CONTENT_URI, values);

                Snackbar.make(v, mMovie.getTitle() + getString(R.string.added_favorites), Snackbar.LENGTH_LONG).show();

            } else {
                getContentResolver().delete(FavoritesMovies.CONTENT_URI.buildUpon()
                        .appendPath(String.valueOf(mMovie.getId()))
                        .build(), null, null);
                Snackbar.make(v, mMovie.getTitle() + getString(R.string.deleted), Snackbar.LENGTH_LONG).show();
            }

        });
    }

    public boolean isFavoriteMovie(int movieID) {
        Cursor cursor = getContentResolver().query(
                FavoritesMovies.CONTENT_URI,
                null,
                FavoritesMovies.COLUMN_MOVIE_ID + "=?",
                new String[]{String.valueOf(movieID)},
                null);
        assert cursor != null;
        return (cursor.getCount() > 0);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void getMovieDetails() {
        mMovie = getIntent().getParcelableExtra(RestUtils.TAG);
    }

    private void setCellValues() {
        if (mMovie != null) {
            setBackDropImage(mMovie.getBackdrop_path());
            setPosterImage(mMovie.getPoster_path().replace(RestUtils.THUMBNAIL_SIZE, RestUtils.POSTER_SIZE));
            setGenres(mMovie.getGenres(this));
            setReleaseDate(mMovie.getRelease_date());
            setVoteAverage(mMovie.getVote_average());
            setOverView(mMovie.getOverview());
            setMovieTrailers(mMovie.getId());
            setMovieReviews(mMovie.getId());
        }
    }

    private void setBackDropImage(final String url) {
        Picasso.with(getApplicationContext())
                .load(url)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(mBackdropImageView);
    }

    private void setPosterImage(final String url) {
        Picasso.with(getApplicationContext())
                .load(url)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(mPosterImageView);
    }

    private void setGenres(final String genres) {
        mGenresTextView.setText(genres);
    }

    private void setReleaseDate(final String releaseDate) {
        mReleaseDateTextView.setText(releaseDate);
    }

    private void setVoteAverage(final float popularity) {
        mVoteAverageTextView.setText(String.valueOf(popularity));
    }

    private void setOverView(final String overView) {
        mOverViewTextView.setText(overView);
    }

    private void setYoutubeImageDefault(String url, ImageView view) {
        Picasso.with(getApplicationContext())
                .load(url)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(view);
    }

    /**
     * Creates a LinearLayout view that displays a preview image for the movie trailer.
     * @param trailer {@link Trailer}
     * @return {@link LinearLayout}
     */
    private LinearLayout movieTrailer(Trailer.result trailer) {
        String trailerUrl = RestUtils.createBaseUrlYoutube(trailer.getKey());
        String imageDefault = RestUtils.createThumbnailUrlYoutube(trailer.getKey());

        LinearLayout mLinearlayout = new LinearLayout(getApplicationContext());
        mLinearlayout.setOrientation(LinearLayout.VERTICAL);
        int padding = getResources().getDimensionPixelOffset(R.dimen.size_10_margin);
        mLinearlayout.setPadding(0, padding, padding, padding);

        ImageView mImage = new ImageView(getApplicationContext());
        setYoutubeImageDefault(imageDefault, mImage);
        mLinearlayout.addView(mImage);

        mLinearlayout.setOnClickListener(v -> {
            Intent mIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(trailerUrl));
            if (mIntent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
                startActivity(mIntent);
            }
        });

        return mLinearlayout;
    }


    /**
     * Creates a LinearLayout view that displays a TextView with a movie review.
     * @param review {@link Review}
     * @return {@link LinearLayout}
     */
    private LinearLayout movieReview(Review.Result review) {

        LinearLayout mReviewLinearLayout = new LinearLayout(getApplicationContext());
        mReviewLinearLayout.setOrientation(LinearLayout.VERTICAL);
        int padding = getResources().getDimensionPixelOffset(R.dimen.size_5_margin);

        TextView authorTv = new TextView(getApplicationContext());
        authorTv.setText(fromHtml(String.format(getResources().getString(R.string.review_author), review.getAuthor())));
        authorTv.setPadding(0, padding, 0, padding);
        mReviewLinearLayout.addView(authorTv);

        TextView contentTv = new TextView(getApplicationContext());
        contentTv.setText(review.getContent());
        mReviewLinearLayout.addView(contentTv);

        View divider = new View(getApplicationContext());
        divider.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 3));
        divider.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.grey));
        mReviewLinearLayout.addView(divider);

        return mReviewLinearLayout;
    }

    private void setMovieTrailers(int movieID) {
        mRestClient.getTrailer(movieID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> {
                            for (Trailer.result trailer : response.getResults()) {
                                LinearLayout linearLayout = movieTrailer(trailer);
                                mListTrailers.addView(linearLayout);
                            }
                        },

                        Throwable -> Log.d(MovieDetailsActivity.class.getSimpleName(), getString(R.string.error_loading_trailer))
                );
    }


    private void setMovieReviews(int movieID) {
        mRestClient.getReviews(movieID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> {
                            for (Review.Result review : response.getReviews()) {
                                LinearLayout linearLayout = movieReview(review);
                                mListReviews.addView(linearLayout);
                            }
                        },

                        Throwable -> Log.d(MovieDetailsActivity.class.getSimpleName(), getString(R.string.error_loading_reviews))
                );
    }

    @SuppressWarnings("deprecation")
    private static Spanned fromHtml(String html) {
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }
}


