package com.tomasmichalkevic.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by tomasmichalkevic on 19/02/2018.
 */

public class Movie implements Parcelable{

    final int voteCount;
    final int id;
    final boolean video;
    final double voteAverage;
    final String title;
    final double popularity;
    final String posterPath;
    final String originalLang;
    final String originalTitle;
    final int[] genreIDs;
    final String backdropPath;
    final boolean adultMovie;
    final String overview;
    final String releaseDate;

    public Movie(int voteCount, int id, boolean video, double voteAverage, String title, double popularity, String posterPath, String originalLang, String originalTitle, int[] genreIDs, String backdropPath, boolean adultMovie, String overview, String releaseDate) {
        this.voteCount = voteCount;
        this.id = id;
        this.video = video;
        this.voteAverage = voteAverage;
        this.title = title;
        this.popularity = popularity;
        this.posterPath = posterPath;
        this.originalLang = originalLang;
        this.originalTitle = originalTitle;
        this.genreIDs = genreIDs;
        this.backdropPath = backdropPath;
        this.adultMovie = adultMovie;
        this.overview = overview;
        this.releaseDate = releaseDate;
    }

    @SuppressWarnings("WeakerAccess")
    protected Movie(Parcel in) {
        voteCount = in.readInt();
        id = in.readInt();
        video = in.readByte() != 0;
        voteAverage = in.readDouble();
        title = in.readString();
        popularity = in.readDouble();
        posterPath = in.readString();
        originalLang = in.readString();
        originalTitle = in.readString();
        genreIDs = in.createIntArray();
        backdropPath = in.readString();
        adultMovie = in.readByte() != 0;
        overview = in.readString();
        releaseDate = in.readString();
    }

    @SuppressWarnings("unused")
    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(voteCount);
        dest.writeInt(id);
        dest.writeByte((byte) (video ? 1 : 0));
        dest.writeDouble(voteAverage);
        dest.writeString(title);
        dest.writeDouble(popularity);
        dest.writeString(posterPath);
        dest.writeString(originalLang);
        dest.writeString(originalTitle);
        dest.writeIntArray(genreIDs);
        dest.writeString(backdropPath);
        dest.writeByte((byte) (adultMovie ? 1 : 0));
        dest.writeString(overview);
        dest.writeString(releaseDate);
    }
}