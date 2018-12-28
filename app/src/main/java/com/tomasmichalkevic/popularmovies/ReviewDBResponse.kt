package com.tomasmichalkevic.popularmovies

import com.google.gson.annotations.SerializedName

class ReviewDBResponse(
    @SerializedName("id")
    private val _id: Int = 0,
    @SerializedName("page")
    private val _page: Int = 0,
    @SerializedName("results")
    private val _review_results: List<ReviewResult> = arrayListOf(),
    @SerializedName("total_pages")
    private val _totalPages: Int = 0,
    @SerializedName("total_results")
    private val _totalResults: Int = 0
) {

    val id
        get() = _id

    val page
        get() = _page

    val reviewResults
        get() = _review_results

    val totalPages
        get() = _totalPages

    val totalResults
        get() = _totalResults

    init {
        this.id
        this.page
        this.reviewResults
        this.totalPages
        this.totalResults
    }
}