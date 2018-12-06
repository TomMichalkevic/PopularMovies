package com.tomasmichalkevic.popularmovies

import com.google.gson.annotations.SerializedName

data class MovieDBResponse(@SerializedName("page")
                           private var _page: Int = 0,
                           @SerializedName("total_results")
                           private var _totalResults: Int = 0,
                           @SerializedName("total_pages")
                           private var _totalPages: Int = 0,
                           @SerializedName("results")
                           private var _results: MutableList<Movie>? = mutableListOf()) {

    val page
        get() = _page

    val totalResults
        get() = _totalResults

    val totalPages
        get() = _totalPages

    val results
        get() = _results ?: arrayListOf()

    init {
        this.page
        this.totalResults
        this.totalPages
        this.results
    }
}