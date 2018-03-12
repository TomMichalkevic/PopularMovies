package com.tomasmichalkevic.popularmovies;

import android.app.Activity;
import android.content.Intent;
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

import com.squareup.picasso.Picasso;

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

    private List<Trailer> trailerList = new ArrayList<>();
    private RecyclerView recyclerView;
    private TrailerAdapter trailerAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private String movieTrailerAddress = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ImageView titleBackdrop = findViewById(R.id.title_iv);
        ImageView posterIV = findViewById(R.id.poster_iv);
        TextView releaseTV = findViewById(R.id.release_tv);
        TextView ratingTV = findViewById(R.id.rating_tv);
        TextView descriptionTV = findViewById(R.id.description_tv);
        FloatingActionButton fab = findViewById(R.id.favourite_fab);

        recyclerView = findViewById(R.id.trailer_recycler_view);
        trailerAdapter = new TrailerAdapter(trailerList);

        recyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        Intent intent = getIntent();
        if (intent == null) {
            finish();
        }

        Movie movie = intent.getParcelableExtra("Movie");
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
                Toast.makeText(getApplicationContext(), "Button Clicked - To be implemented", Toast.LENGTH_LONG).show();
            }
        });

        recyclerView.setAdapter(trailerAdapter);
        movieTrailerAddress = String.format(trailersURL, movie.id);
        trailerList.clear();

        Collections.addAll(trailerList, getFilteredOutTrailers(getTrailers()));

        trailerAdapter.notifyDataSetChanged();

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

    private String getResponseJSON(){
        String result = "";

        HttpGetRequest httpGetRequest = new HttpGetRequest();

        try {
            result = httpGetRequest.execute(movieTrailerAddress).get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e(LOG_TAG, "getResponseJSON: ", e);
        } finally {
            return result;
        }
    }

    private Trailer[] getTrailers(){
        String result = getResponseJSON();
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

    private Trailer getTrailer(JSONObject object) throws JSONException {
        return new Trailer(object.getString("id"), object.getString("iso_639_1"), object.getString("iso_3166_1"), object.getString("key"), object.getString("name"), object.getString("site"), object.getInt("size"), object.getString("type"));
    }
}
