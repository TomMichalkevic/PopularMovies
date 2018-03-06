package com.tomasmichalkevic.popularmovies;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by tomasmichalkevic on 01/03/2018.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.ViewHolder> {

    private List<Trailer> trailerList;

    public TrailerAdapter(List<Trailer> trailerList) {
        this.trailerList = trailerList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Trailer trailer = trailerList.get(position);
        holder.title.setText(trailer.getName());

    }

    @Override
    public int getItemCount() {
        return trailerList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;

        public ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.trailer_title);
        }
    }

}
