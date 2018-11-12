/*
 * PROJECT LICENSE
 *
 * This project was submitted by Tomas Michalkevic as part of the Nanodegree At Udacity.
 *
 * As part of Udacity Honor code, your submissions must be your own work, hence
 * submitting this project as yours will cause you to break the Udacity Honor Code
 * and the suspension of your account.
 *
 * Me, the author of the project, allow you to check the code as a reference, but if
 * you submit it, it's your own responsibility if you get expelled.
 *
 * Copyright (c) 2018 Tomas Michalkevic
 *
 * Besides the above notice, the following license applies and this license notice
 * must be included in all works derived from this project.
 *
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.tomasmichalkevic.popularmovies

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.Toast

import com.facebook.stetho.Stetho
import com.squareup.picasso.Picasso
import com.tomasmichalkevic.popularmovies.data.FavouritesContract

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.util.ArrayList
import java.util.Collections
import java.util.concurrent.ExecutionException

import kotlinx.android.synthetic.main.activity_detail.*

/**
 * Created by tomasmichalkevic on 21/02/2018.
 */

class DetailsActivity : Activity() {

    private val trailersURL = "http://api.themoviedb.org/3/movie/%d/videos?api_key=$API_KEY"
    private val reviewURL = "http://api.themoviedb.org/3/movie/%d/reviews?api_key=$API_KEY"

    private val trailerList = ArrayList<Trailer>()
    private val reviewList = ArrayList<Review>()

    private var trailerAdapter: TrailerAdapter? = null
    private var reviewAdapter: ReviewAdapter? = null

    private var mLayoutManagerTrailers: RecyclerView.LayoutManager? = null
    private var mLayoutManagerReviews: RecyclerView.LayoutManager? = null

    private var movieTrailerAddress = ""
    private var reviewAddress = ""

    private var alreadyFavourited = false

    private val trailers: Array<Trailer>?
        get() {
            val result: String = getResponseJSON(0)
            val jsonObject: JSONObject
            var trailerArray: MutableList<Trailer> = mutableListOf()

            return try {
                jsonObject = JSONObject(result)
                val trailers: JSONArray = jsonObject.getJSONArray("results")
                var i = trailers.length() - 1
                while (i >= 0) {
                    trailerArray.add(getTrailer(trailers.get(i) as JSONObject))
                    i--
                }
                trailerArray.toTypedArray()
            } catch (e: JSONException) {
                Log.e(LOG_TAG, "JSON malformed", e)
                null
            }

        }

    private val reviews: Array<Review>?
        get() {
            val result: String = getResponseJSON(1)
            val jsonObject: JSONObject
            val reviewArray: MutableList<Review> = mutableListOf()

            return try {
                jsonObject = JSONObject(result)
                val reviews = jsonObject.getJSONArray("results")
                var i = reviews.length() - 1
                while (i >= 0) {
                    reviewArray.add(getReview(reviews.get(i) as JSONObject))
                    i--
                }
                reviewArray.toTypedArray()
            } catch (e: JSONException) {
                Log.e(LOG_TAG, "JSON malformed", e)
                null
            }

        }

    @SuppressLint("DefaultLocale")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Stetho.initializeWithDefaults(this)
        setContentView(R.layout.activity_detail)

        trailerAdapter = TrailerAdapter(trailerList, object : TrailerAdapter.OnItemClickListener {
            override fun onItemClick(item: Trailer) {
                val appIntent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + item.key))
                val webIntent = Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://www.youtube.com/watch?v=" + item.key))
                try {
                    baseContext.startActivity(appIntent)
                } catch (ex: ActivityNotFoundException) {
                    baseContext.startActivity(webIntent)
                }

            }
        })

        reviewAdapter = ReviewAdapter(reviewList)

        review_recycler_view.setHasFixedSize(true)
        trailer_recycler_view.setHasFixedSize(true)

        mLayoutManagerTrailers = LinearLayoutManager(this)
        trailer_recycler_view.layoutManager = mLayoutManagerTrailers

        mLayoutManagerReviews = LinearLayoutManager(this)
        review_recycler_view.layoutManager = mLayoutManagerReviews

        val intent = intent
        if (intent == null) {
            finish()
        }

        val movie = intent!!.getParcelableExtra<Movie>("Movie")
        val voteAverage = movie.voteAverage
        val title = movie.title
        val posterPath = movie.posterPath
        val overview = movie.overview
        val releaseDate = movie.releaseDate
        val backdropPath = movie.backdropPath

        alreadyFavourited = isFavouritedAlready(movie)

        if (alreadyFavourited) {
            favourite_fab.setImageDrawable(resources.getDrawable(android.R.drawable.btn_star_big_on))
        } else {
            favourite_fab.setImageDrawable(resources.getDrawable(android.R.drawable.btn_star_big_off))
        }
        Picasso.with(this@DetailsActivity).load("http://image.tmdb.org/t/p/w185$posterPath").into(poster_iv)
        collapsingDetails.title = title
        Picasso.with(this@DetailsActivity).load("http://image.tmdb.org/t/p/w500$backdropPath").into(title_iv)
        release_tv.text = releaseDate.substring(0, 4)
        rating_tv.text = String.format("%d/10", Math.round(voteAverage))
        description_tv.text = overview

        favourite_fab.setOnClickListener {
            if (alreadyFavourited) {
                Toast.makeText(applicationContext, "The movie is already added to favourites!", Toast.LENGTH_LONG).show()
            } else {
                if (insertData(movie)) {
                    Toast.makeText(applicationContext, "The movie is now added to favourites!", Toast.LENGTH_LONG).show()
                    favourite_fab.setImageDrawable(resources.getDrawable(android.R.drawable.btn_star_big_on))
                }
            }
        }

        trailer_recycler_view.adapter = trailerAdapter
        movieTrailerAddress = String.format(trailersURL, movie.id)
        trailerList.clear()

        Collections.addAll(trailerList, *getFilteredOutTrailers(trailers!!))

        trailerAdapter!!.notifyDataSetChanged()

        review_recycler_view.adapter = reviewAdapter
        reviewAddress = String.format(reviewURL, movie.id)
        reviewList.clear()

        Collections.addAll(reviewList, *reviews!!)


        reviewAdapter!!.notifyDataSetChanged()

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        if (savedInstanceState != null) {
            title_iv.contentDescription = savedInstanceState.getCharSequence("titleBackdrop")
            release_tv.text = savedInstanceState.getCharSequence("releaseTV")
            rating_tv.text = savedInstanceState.getCharSequence("ratingTV")
            description_tv.text = savedInstanceState.getCharSequence("descriptionTV")
        }


    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putCharSequence("titleBackdrop", title_iv.contentDescription)
        outState.putCharSequence("releaseTV", release_tv.text)
        outState.putCharSequence("ratingTV", rating_tv.text)
        outState.putCharSequence("descriptionTV", description_tv.text)
    }

    private fun getFilteredOutTrailers(videos: Array<Trailer>): Array<Trailer> {
        val list: MutableList<Trailer> = mutableListOf()
        for (trailer in videos) {
            if (trailer.type == "Trailer") {
                list.add(trailer)
            }
        }
        return list.toTypedArray()
    }

    private fun getResponseJSON(choice: Int): String {
        var result = ""

        val httpGetRequest = HttpGetRequest()

        try {
            if (choice == 0)
                result = httpGetRequest.execute(movieTrailerAddress).get()
            else if (choice == 1)
                result = httpGetRequest.execute(reviewAddress).get()
        } catch (e: InterruptedException) {
            Log.e(LOG_TAG, "getResponseJSON: ", e)
        } catch (e: ExecutionException) {
            Log.e(LOG_TAG, "getResponseJSON: ", e)
        } finally {
            return result
        }
    }

    @Throws(JSONException::class)
    private fun getTrailer(`object`: JSONObject): Trailer {
        return Trailer(`object`.getString("id"), `object`.getString("iso_639_1"), `object`.getString("iso_3166_1"), `object`.getString("key"), `object`.getString("name"), `object`.getString("site"), `object`.getInt("size"), `object`.getString("type"))
    }

    @Throws(JSONException::class)
    private fun getReview(`object`: JSONObject): Review {
        return Review(`object`.getString("id"), `object`.getString("author"), `object`.getString("content"), `object`.getString("url"))
    }

    fun isFavouritedAlready(movie: Movie): Boolean {
        val selectionArgs = arrayOf(Integer.toString(movie.id))
        val sortOrder = FavouritesContract.FavouriteEntry.COLUMN_ID + " ASC"
        val selection = FavouritesContract.FavouriteEntry.COLUMN_ID + " = ?"
        val cursor = contentResolver.query(FavouritesContract.FavouriteEntry.CONTENT_URI, null, selection, selectionArgs, sortOrder)
        cursor!!.close()
        return cursor.count != 0
    }

    private fun insertData(movie: Movie): Boolean {
        if (!isFavouritedAlready(movie)) {
            val values = ContentValues()
            values.put(FavouritesContract.FavouriteEntry.COLUMN_ID, movie.id)
            values.put(FavouritesContract.FavouriteEntry.COLUMN_VIDEO, movie.video)
            values.put(FavouritesContract.FavouriteEntry.COLUMN_VOTE_AVERAGE, movie.voteAverage)
            values.put(FavouritesContract.FavouriteEntry.COLUMN_TITLE, movie.title)
            values.put(FavouritesContract.FavouriteEntry.COLUMN_POPULARITY, movie.popularity)
            values.put(FavouritesContract.FavouriteEntry.COLUMN_POSTER_PATH, movie.posterPath)
            values.put(FavouritesContract.FavouriteEntry.COLUMN_ORIGINAL_LANG, movie.originalLang)
            values.put(FavouritesContract.FavouriteEntry.COLUMN_ORIGINAL_TITLE, movie.originalTitle)
            values.put(FavouritesContract.FavouriteEntry.COLUMN_BACKDROP_PATH, movie.backdropPath)
            values.put(FavouritesContract.FavouriteEntry.COLUMN_ADULT_MOVIE, movie.adultMovie)
            values.put(FavouritesContract.FavouriteEntry.COLUMN_OVERVIEW, movie.overview)
            values.put(FavouritesContract.FavouriteEntry.COLUMN_RELEASE_DATE, movie.releaseDate)

            contentResolver.insert(FavouritesContract.FavouriteEntry.CONTENT_URI, values)
            return true
        } else {
            return false
        }


    }

    companion object {

        private val LOG_TAG = DetailsActivity::class.java.simpleName

        private val API_KEY = BuildConfig.MOVIE_DB_API_KEY
    }
}
