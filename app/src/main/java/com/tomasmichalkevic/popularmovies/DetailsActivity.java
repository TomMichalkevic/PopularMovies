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

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
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
import com.tomasmichalkevic.popularmovies.data.FavouritesDBHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by tomasmichalkevic on 21/02/2018.
 */

public class DetailsActivity extends Activity {

    private static final String LOG_TAG = DetailsActivity.class.getSimpleName();

    private static final String API_KEY = BuildConfig.MOVIE_DB_API_KEY;

    private final String trailersURL = "http://api.themoviedb.org/3/movie/%d/videos?api_key="+API_KEY;
    private final String reviewURL = "http://api.themoviedb.org/3/movie/%d/reviews?api_key="+API_KEY;

    private List<Trailer> trailerList = new ArrayList<>();
    private List<Review> reviewList = new ArrayList<>();

    private RecyclerView trailerRecyclerView;
    private TrailerAdapter trailerAdapter;

    private RecyclerView reviewRecyclerView;
    private ReviewAdapter reviewAdapter;

    private RecyclerView.LayoutManager mLayoutManagerTrailers;
    private RecyclerView.LayoutManager mLayoutManagerReviews;

    private String movieTrailerAddress = "";
    private String reviewAddress = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Stetho.initializeWithDefaults(this);
        setContentView(R.layout.activity_detail);

        ImageView titleBackdrop = findViewById(R.id.title_iv);
        ImageView posterIV = findViewById(R.id.poster_iv);
        TextView releaseTV = findViewById(R.id.release_tv);
        TextView ratingTV = findViewById(R.id.rating_tv);
        TextView descriptionTV = findViewById(R.id.description_tv);
        FloatingActionButton fab = findViewById(R.id.favourite_fab);

        trailerRecyclerView = findViewById(R.id.trailer_recycler_view);
        trailerAdapter = new TrailerAdapter(trailerList);

        reviewRecyclerView = findViewById(R.id.review_recycler_view);
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

        CollapsingToolbarLayout detailsLayout = findViewById(R.id.collapsingDetails);
        Picasso.with(DetailsActivity.this).load("http://image.tmdb.org/t/p/w185"+posterPath).into(posterIV);
        detailsLayout.setTitle(title);
        Picasso.with(DetailsActivity.this).load("http://image.tmdb.org/t/p/w500"+backdropPath).into(titleBackdrop);
        releaseTV.setText(releaseDate.substring(0, 4));
        ratingTV.setText(String.format("%d/10", Math.round(voteAverage)));
        descriptionTV.setText(overview);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FavouriteExistenceChecker checker = new FavouriteExistenceChecker();
                FavouriteNewMovieHelper helper = new FavouriteNewMovieHelper();
                boolean result = false;
                try{
                    if(result = checker.execute(movie.id).get()){
                        Toast.makeText(getApplicationContext(), "The movie is already added to favourites!", Toast.LENGTH_LONG).show();
                    }else{
                        if(helper.execute(movie).get()>0){
                            Toast.makeText(getApplicationContext(), "The movie is now added to favourites!", Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (InterruptedException | ExecutionException e) {
                    Log.e(LOG_TAG, "onClick: ", e);
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

    private class FavouriteExistenceChecker extends AsyncTask<Integer, Void, Boolean>{

        @Override
        protected Boolean doInBackground(Integer... integers) {
            FavouritesDBHelper dbHelper = new FavouritesDBHelper(getBaseContext());
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            String selection = FavouritesContract.FavouriteEntry.COLUMN_ID + " = ?";
            String[] selectionArgs = { Integer.toString(integers[0]) };

            String sortOrder =
                    FavouritesContract.FavouriteEntry.COLUMN_ID + " ASC";

            Cursor cursor = db.query(
                    FavouritesContract.FavouriteEntry.TABLE_FAVOURITES,   // The table to query
                    null,             // The array of columns to return (pass null to get all)
                    selection,              // The columns for the WHERE clause
                    selectionArgs,          // The values for the WHERE clause
                    null,                   // don't group the rows
                    null,                   // don't filter by row groups
                    sortOrder               // The sort order
            );

            if(cursor.moveToFirst()!=false){
                cursor.close();
                return true;
            }else{
                cursor.close();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result){
            super.onPostExecute(result);
        }
    }

    private class FavouriteNewMovieHelper extends AsyncTask<Movie, Void, Long>{

        @Override
        protected Long doInBackground(Movie... movies) {
            Movie movie = movies[0];
            FavouritesDBHelper dbHelper = new FavouritesDBHelper(getBaseContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();
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
            long newRowId = db.insert(FavouritesContract.FavouriteEntry.TABLE_FAVOURITES, null, values);
            return newRowId;
        }

        protected void onPostExecute(Long result){
            super.onPostExecute(result);
        }
    }
}
