package com.tomasmichalkevic.popularmovies;

/**
 * Created by tomasmichalkevic on 12/03/2018.
 */

public class Review {

    final String id;
    final String author;
    final String content;
    final String url;

    public Review(String id, String author, String content, String url) {
        this.id = id;
        this.author = author;
        this.content = content;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public String getUrl() {
        return url;
    }

}