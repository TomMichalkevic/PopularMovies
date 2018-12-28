package com.tomasmichalkevic.popularmovies

import com.google.gson.annotations.SerializedName

data class Movie(
    @SerializedName("vote_count")
    private val _voteCount: Int = 0,
    @SerializedName("id")
    private val _id: Int = 0,
    @SerializedName("video")
    private val _isVideo: Boolean = false,
    @SerializedName("vote_average")
    private val _voteAverage: Double = 0.toDouble(),
    @SerializedName("title")
    private val _title: String? = "",
    @SerializedName("popularity")
    private val _popularity: Double = 0.toDouble(),
    @SerializedName("poster_path")
    private val _posterPath: String? = "",
    @SerializedName("original_language")
    private val _originalLanguage: String? = "",
    @SerializedName("original_title")
    private val _originalTitle: String? = "",
    @SerializedName("genre_ids")
    private val _genreIds: List<Int>? = arrayListOf(),
    @SerializedName("backdrop_path")
    private val _backdropPath: String? = "",
    @SerializedName("adult")
    private val _isAdult: Boolean = false,
    @SerializedName("overview")
    private val _overview: String? = "",
    @SerializedName("release_date")
    private val _releaseDate: String? = ""
) {
    val voteCount
        get() = _voteCount

    val id
        get() = _id

    val isVideo
        get() = _isVideo

    val voteAverage
        get() = _voteAverage

    val title
        get() = _title ?: ""

    val popularity
        get() = _popularity

    val posterPath
        get() = _posterPath ?: ""

    val originalLanguage
        get() = _originalLanguage ?: ""

    val originalTitle
        get() = _originalTitle ?: ""

    val genreIds
        get() = _genreIds ?: arrayListOf()

    val backdropPath
        get() = _backdropPath ?: ""

    val isAdult
        get() = _isAdult

    val overview
        get() = _overview ?: ""

    val releaseDate
        get() = _releaseDate ?: ""

    init {
        this.voteCount
        this.id
        this.isVideo
        this.voteAverage
        this.title
        this.popularity
        this.posterPath
        this.originalLanguage
        this.originalTitle
        this.genreIds
        this.backdropPath
        this.isAdult
        this.overview
        this.releaseDate
    }
}
