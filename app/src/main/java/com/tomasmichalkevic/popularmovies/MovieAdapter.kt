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

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.google.gson.Gson

import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.movie_item.view.*

/**
 * Created by tomasmichalkevic on 19/02/2018.
 */

internal class MovieAdapter(context: Activity, movies: List<Movie>) : ArrayAdapter<Movie>(context, 0, movies) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var returnedView = convertView

        val movie = getItem(position)

        if (returnedView == null) {
            returnedView = LayoutInflater.from(context).inflate(
                    R.layout.movie_item, parent, false)
        }

        Picasso.with(context).load("http://image.tmdb.org/t/p/w185" + movie!!.posterPath).into(returnedView?.movie_image)
        returnedView?.movie_image?.setOnClickListener {
            val intent = Intent(context, DetailsActivity::class.java)
            intent.putExtra("Movie", Gson().toJson(movie))
            context.startActivity(intent)
        }

        return returnedView
    }
}
