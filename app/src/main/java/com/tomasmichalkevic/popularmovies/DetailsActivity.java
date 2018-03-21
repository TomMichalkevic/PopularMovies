/*
 * PROJECT LICENSE
 *
 * This project was submitted by Tomas Michalkevic as part of the Nanodegree At Udacity.
 *
 * As part of Udacity Honor code, your submissions must be your own work, hence
 * submitting this project as yours will cause you to break the Udacity Honor Code
 * and the suspension of your account.
 *
 * Me, the author of the project, allow you to check the code as a reference, but if
 * you submit it, it's your own responsibility if you get expelled.
 *
 * Copyright (c) 2018 Tomas Michalkevic
 *
 * Besides the above notice, the following license applies and this license notice
 * must be included in all works derived from this project.
 *
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.tomasmichalkevic.popularmovies;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.squareup.picasso.Picasso;
import com.tomasmichalkevic.popularmovies.data.FavouritesContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by tomasmichalkevic on 21/02/2018.
 */

public class DetailsActivity extends Activity {

    private static final String LOG_TAG = DetailsActivity.class.getSimpleName();

    private static final String API_KEY = BuildConfig.MOVIE_DB_API_KEY;

    private final String trailersURL = "http://api.themoviedb.org/3/movie/%d/videos?api_key="+API_KEY;
    private final String reviewURL = "http://api.themoviedb.org/3/movie/%d/reviews?api_key="+API_KEY;

    private final List<Trailer> trailerList = new ArrayList<>();
    private final List<Review> reviewList = new ArrayList<>();

    private TrailerAdapter trailerAdapter;
    private ReviewAdapter reviewAdapter;

    private RecyclerView.LayoutManager mLayoutManagerTrailers;
    private RecyclerView.LayoutManager mLayoutManagerReviews;

    private String movieTrailerAddress = "";
    private String reviewAddress = "";

    private boolean alreadyFavourited = false;

    @BindView(R.id.title_iv) ImageView titleBackdrop;
    @BindView(R.id.poster_iv) ImageView posterIV;
    @BindView(R.id.release_tv) TextView releaseTV;
    @BindView(R.id.rating_tv) TextView ratingTV;
    @BindView(R.id.description_tv) TextView descriptionTV;
    @BindView(R.id.favourite_fab) FloatingActionButton fab;
    @BindView(R.id.trailer_recycler_view) RecyclerView trailerRecyclerView;
    @BindView(R.id.review_recycler_view) RecyclerView reviewRecyclerView;
    @BindView(R.id.collapsingDetails) CollapsingToolbarLayout detailsLayout;
    @BindView(R.id.detailsCoordinatorLayout) CoordinatorLayout detailsCoordinatorLayout;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Stetho.initializeWithDefaults(this);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        trailerAdapter = new TrailerAdapter(trailerList, new TrailerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Trailer item) {
                Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + item.getKey()))
                        ;
                    Intent webIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://www.youtube.com/watch?v=" + item.getKey()));
                    try {
                        getBaseContext().startActivity(appIntent);
                    } catch (ActivityNotFoundException ex) {
                        getBaseContext().startActivity(webIntent);
                    }
            }
        });

        reviewAdapter = new ReviewAdapter(reviewList);

        trailerRecyclerView.setHasFixedSize(true);
        reviewRecyclerView.setHasFixedSize(true);

        mLayoutManagerTrailers = new LinearLayoutManager(this);
        trailerRecyclerView.setLayoutManager(mLayoutManagerTrailers);

        mLayoutManagerReviews = new LinearLayoutManager(this);
        reviewRecyclerView.setLayoutManager(mLayoutManagerReviews);

        Intent intent = getIntent();
        if (intent == null) {
            finish();
        }

        final Movie movie = intent.getParcelableExtra("Movie");
        double voteAverage = movie.voteAverage;
        String title = movie.title;
        String posterPath = movie.posterPath;
        String overview = movie.overview;
        String releaseDate = movie.releaseDate;
        String backdropPath = movie.backdropPath;

        alreadyFavourited = isFavouritedAlready(movie);

        if(alreadyFavourited){
            fab.setImageDrawable(getResources().getDrawable(android.R.drawable.btn_star_big_on));
        }else{
            fab.setImageDrawable(getResources().getDrawable(android.R.drawable.btn_star_big_off));
        }
        Picasso.with(DetailsActivity.this).load("http://image.tmdb.org/t/p/w185"+posterPath).into(posterIV);
        detailsLayout.setTitle(title);
        Picasso.with(DetailsActivity.this).load("http://image.tmdb.org/t/p/w500"+backdropPath).into(titleBackdrop);
        releaseTV.setText(releaseDate.substring(0, 4));
        ratingTV.setText(String.format("%d/10", Math.round(voteAverage)));
        descriptionTV.setText(overview);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(alreadyFavourited){
                    Toast.makeText(getApplicationContext(), "The movie is already added to favourites!", Toast.LENGTH_LONG).show();
                }else{
                    if(insertData(movie)){
                        Toast.makeText(getApplicationContext(), "The movie is now added to favourites!", Toast.LENGTH_LONG).show();
                        fab.setImageDrawable(getResources().getDrawable(android.R.drawable.btn_star_big_on));
                    }
                }

            }
        });

        trailerRecyclerView.setAdapter(trailerAdapter);
        movieTrailerAddress = String.format(trailersURL, movie.id);
        trailerList.clear();

        Collections.addAll(trailerList, getFilteredOutTrailers(getTrailers()));

        trailerAdapter.notifyDataSetChanged();

        reviewRecyclerView.setAdapter(reviewAdapter);
        reviewAddress = String.format(reviewURL, movie.id);
        reviewList.clear();

        Collections.addAll(reviewList, getReviews());


        reviewAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState != null){
            titleBackdrop.setContentDescription(savedInstanceState.getCharSequence("titleBackdrop"));
            releaseTV.setText(savedInstanceState.getCharSequence("releaseTV"));
            ratingTV.setText(savedInstanceState.getCharSequence("ratingTV"));
            descriptionTV.setText(savedInstanceState.getCharSequence("descriptionTV"));
        }


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence("titleBackdrop", titleBackdrop.getContentDescription());
        outState.putCharSequence("releaseTV", releaseTV.getText());
        outState.putCharSequence("ratingTV", ratingTV.getText());
        outState.putCharSequence("descriptionTV", descriptionTV.getText());
    }

    private Trailer[] getFilteredOutTrailers(Trailer[] videos){
        ArrayList<Trailer> list = new ArrayList<>();
        Trailer[] result;
        for(Trailer trailer: videos){
            if(trailer.getType().equals("Trailer"))
            list.add(trailer);
        }
        result = new Trailer[list.size()];
        return list.toArray(result);
    }

    private String getResponseJSON(int choice){
        String result = "";

        HttpGetRequest httpGetRequest = new HttpGetRequest();

        try {
            if(choice == 0)
                result = httpGetRequest.execute(movieTrailerAddress).get();
            else if(choice == 1)
                result = httpGetRequest.execute(reviewAddress).get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e(LOG_TAG, "getResponseJSON: ", e);
        } finally {
            return result;
        }
    }

    private Trailer[] getTrailers(){
        String result = getResponseJSON(0);
        JSONObject jsonObject;
        Trailer[] trailerArray;

        try {
            jsonObject = new JSONObject(result);
            JSONArray trailers = jsonObject.getJSONArray("results");
            trailerArray = new Trailer[trailers.length()];
            for(int i = 0; i < trailers.length(); i++){
                trailerArray[i] = getTrailer((JSONObject) trailers.get(i));
            }
            return trailerArray;
        } catch (JSONException e) {
            Log.e(LOG_TAG, "JSON malformed", e);
            return null;
        }
    }

    private Review[] getReviews(){
        String result = getResponseJSON(1);
        JSONObject jsonObject;
        Review[] reviewArray;

        try {
            jsonObject = new JSONObject(result);
            JSONArray reviews = jsonObject.getJSONArray("results");
            reviewArray = new Review[reviews.length()];
            for(int i = 0; i < reviews.length(); i++){
                reviewArray[i] = getReview((JSONObject) reviews.get(i));
            }
            return reviewArray;
        } catch (JSONException e) {
            Log.e(LOG_TAG, "JSON malformed", e);
            return null;
        }
    }

    private Trailer getTrailer(JSONObject object) throws JSONException {
        return new Trailer(object.getString("id"), object.getString("iso_639_1"), object.getString("iso_3166_1"), object.getString("key"), object.getString("name"), object.getString("site"), object.getInt("size"), object.getString("type"));
    }

    private Review getReview(JSONObject object) throws JSONException {
        return new Review(object.getString("id"), object.getString("author"), object.getString("content"), object.getString("url"));
    }

    public boolean isFavouritedAlready(Movie movie){
        String[] selectionArgs = { Integer.toString(movie.id) };
        String sortOrder = FavouritesContract.FavouriteEntry.COLUMN_ID + " ASC";
        String selection = FavouritesContract.FavouriteEntry.COLUMN_ID + " = ?";
        Cursor cursor = getContentResolver().query(FavouritesContract.FavouriteEntry.CONTENT_URI, null, selection, selectionArgs, sortOrder);
        cursor.close();
        return !(cursor.getCount()==0);
    }

    private boolean insertData(Movie movie){
        if(!isFavouritedAlready(movie)){
            ContentValues values = new ContentValues();
            values.put(FavouritesContract.FavouriteEntry.COLUMN_ID, movie.id);
            values.put(FavouritesContract.FavouriteEntry.COLUMN_VIDEO, movie.video);
            values.put(FavouritesContract.FavouriteEntry.COLUMN_VOTE_AVERAGE, movie.voteAverage);
            values.put(FavouritesContract.FavouriteEntry.COLUMN_TITLE, movie.title);
            values.put(FavouritesContract.FavouriteEntry.COLUMN_POPULARITY, movie.popularity);
            values.put(FavouritesContract.FavouriteEntry.COLUMN_POSTER_PATH, movie.posterPath);
            values.put(FavouritesContract.FavouriteEntry.COLUMN_ORIGINAL_LANG, movie.originalLang);
            values.put(FavouritesContract.FavouriteEntry.COLUMN_ORIGINAL_TITLE, movie.originalTitle);
            values.put(FavouritesContract.FavouriteEntry.COLUMN_BACKDROP_PATH, movie.backdropPath);
            values.put(FavouritesContract.FavouriteEntry.COLUMN_ADULT_MOVIE, movie.adultMovie);
            values.put(FavouritesContract.FavouriteEntry.COLUMN_OVERVIEW, movie.overview);
            values.put(FavouritesContract.FavouriteEntry.COLUMN_RELEASE_DATE, movie.releaseDate);

            getContentResolver().insert(FavouritesContract.FavouriteEntry.CONTENT_URI, values);
            return true;
        }else{
            return false;
        }


    }
}
