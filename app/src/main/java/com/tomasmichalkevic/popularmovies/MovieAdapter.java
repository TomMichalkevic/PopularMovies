package com.tomasmichalkevic.popularmovies;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by tomasmichalkevic on 19/02/2018.
 */

class MovieAdapter extends ArrayAdapter<Movie> {

    public MovieAdapter(Activity context, List<Movie> movies) {
        super(context, 0, movies);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        final Movie movie = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.movie_item, parent, false);
        }

        ImageView iconView = convertView.findViewById(R.id.movie_image);
        Picasso.with(getContext()).load("http://image.tmdb.org/t/p/w185"+movie.posterPath).into(iconView);
        iconView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), DetailsActivity.class);
                intent.putExtra("Movie", movie);
                getContext().startActivity(intent);
            }
        });

        return convertView;
    }
}
