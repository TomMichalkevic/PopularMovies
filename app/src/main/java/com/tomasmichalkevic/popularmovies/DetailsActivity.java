package com.tomasmichalkevic.popularmovies;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tomasmichalkevic on 21/02/2018.
 */

public class DetailsActivity extends Activity {

//    private List<Trailer> trailerList = new ArrayList<>();
//    private RecyclerView recyclerView;
//    private TrailerAdapter trailerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ImageView titleBackdrop = findViewById(R.id.title_iv);
        ImageView posterIV = findViewById(R.id.poster_iv);
        TextView releaseTV = findViewById(R.id.release_tv);
        TextView ratingTV = findViewById(R.id.rating_tv);
        TextView descriptionTV = findViewById(R.id.description_tv);

//        recyclerView = findViewById(R.id.trailer_recycler_view);
//        trailerAdapter = new TrailerAdapter(trailerList);
//
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


    }

//    private void prepareMockData(){
//        Trailer trailer = new Trailer("592199669251414ab10568ec", "en", "US", "fBNpSRtfIUA", "\\\"Offer He Can't Refuse\\\"", "YouTube", 1080, "Clip");
//        trailerList.add(trailer);
//
//        Trailer trailer1 = new Trailer("592199669251414ab10568ec", "en", "US", "fBNpSRtfIUA", "\\\"Offer He CAN Refuse\\\"", "YouTube", 1080, "Clip");
//        trailerList.add(trailer1);
//
//        trailerAdapter.notifyDataSetChanged();
//    }
}
