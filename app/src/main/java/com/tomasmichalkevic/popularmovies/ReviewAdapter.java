package com.tomasmichalkevic.popularmovies;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by tomasmichalkevic on 12/03/2018.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder>{

    private List<Review> reviewList;

    public ReviewAdapter(List<Review> reviewList){
        this.reviewList = reviewList;
    }

    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item, parent, false);
        return new ReviewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapter.ViewHolder holder, int position) {
        Review review = reviewList.get(position);
        holder.author.setText(review.getAuthor());
        holder.content.setText(review.getContent());
        holder.url.setText(review.getUrl());
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView author;
        public TextView content;
        public TextView url;

        public ViewHolder(View view) {
            super(view);
            author = view.findViewById(R.id.author);
            content = view.findViewById(R.id.review_content);
            url = view.findViewById(R.id.review_url);
        }
    }
}
