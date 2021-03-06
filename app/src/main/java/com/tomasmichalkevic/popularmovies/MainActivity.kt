package com.tomasmichalkevic.popularmovies

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.gson.Gson

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import org.jetbrains.anko.longToast
import java.net.URL

class MainActivity : Activity(), CoroutineScope {

    override val coroutineContext = Dispatchers.Main + SupervisorJob()

    private var movieAdapter: MovieAdapter? = null

    private var choiceOfSort: String? = null
    private var favouriteView: Boolean = false

    private val popularMoviesURL = "http://api.themoviedb.org/3/movie/popular?api_key=$API_KEY"
    private val topRatedMoviesURL = "http://api.themoviedb.org/3/movie/top_rated?api_key=$API_KEY"

    private var moviesList: MutableList<Movie> = mutableListOf()

    private var preferences: SharedPreferences? = null

    private var listPosition = 0

    private val favouriteMovies: Array<Movie> = arrayOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState != null) {
            listPosition = savedInstanceState.getInt("position")
            movies_grid.smoothScrollToPosition(listPosition)
        }
        preferences = PreferenceManager.getDefaultSharedPreferences(this)

        choiceOfSort = preferences!!.getString("orderPrefKey", "1")
        favouriteView = preferences!!.getBoolean("favouriteCheckBox", false)

        movieAdapter = MovieAdapter(this, moviesList)

        moviesList.clear()
        if (isNetworkAvailable(this)) {
            if (favouriteView) {
                // getFavoriteMovies()
            } else {
                getMovies()
            }
        } else {
            Toast.makeText(this, "Cannot refresh due to no network!", Toast.LENGTH_LONG).show()
        }
    }

    private fun getResponseJSON(): String {
        return when (choiceOfSort) {
            "1" -> URL(popularMoviesURL).readText()
            "2" -> URL(topRatedMoviesURL).readText()
            else -> URL(popularMoviesURL).readText()
        }
    }

    private fun getMovies() {
        launch {
            val result = withContext(Dispatchers.IO) { getResponseJSON() }
            moviesList.addAll(Gson().fromJson(result, MovieDBResponse::class.java).results)
            longToast(moviesList.size.toString())
            movies_grid.adapter = movieAdapter
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineContext[Job]!!.cancel()
    }

//    fun getFavoriteMovies(): Array<Movie> {
//        var movies: MutableList<Movie> = mutableListOf()
//
//        val sortOrder = "$COLUMN_ID ASC"
//
//        val cursor = contentResolver.query(FavouritesContract.FavouriteEntry.CONTENT_URI, null, null, null, sortOrder)
//
//        if (cursor!!.moveToFirst()) {
//            movies.add(Movie(1,
//                    cursor.getInt(cursor.getColumnIndex(COLUMN_ID)),
//                    cursor.getInt(cursor.getColumnIndex(COLUMN_VIDEO)) == 1,
//                    cursor.getDouble(cursor.getColumnIndex(COLUMN_VOTE_AVERAGE)),
//                    cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)),
//                    cursor.getDouble(cursor.getColumnIndex(COLUMN_POPULARITY)),
//                    cursor.getString(cursor.getColumnIndex(COLUMN_POSTER_PATH)),
//                    cursor.getString(cursor.getColumnIndex(COLUMN_ORIGINAL_LANG)),
//                    cursor.getString(cursor.getColumnIndex(COLUMN_ORIGINAL_TITLE)),
//                    IntArray(0), cursor.getString(cursor.getColumnIndex(COLUMN_BACKDROP_PATH)),
//                    cursor.getInt(cursor.getColumnIndex(COLUMN_ADULT_MOVIE)) == 1,
//                    cursor.getString(cursor.getColumnIndex(COLUMN_OVERVIEW)),
//                    cursor.getString(cursor.getColumnIndex(COLUMN_RELEASE_DATE))))
//            while (cursor.moveToNext()) {
//                movies.add(Movie(1,
//                        cursor.getInt(cursor.getColumnIndex(COLUMN_ID)),
//                        cursor.getInt(cursor.getColumnIndex(COLUMN_VIDEO)) == 1,
//                        cursor.getDouble(cursor.getColumnIndex(COLUMN_VOTE_AVERAGE)),
//                        cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)),
//                        cursor.getDouble(cursor.getColumnIndex(COLUMN_POPULARITY)),
//                        cursor.getString(cursor.getColumnIndex(COLUMN_POSTER_PATH)),
//                        cursor.getString(cursor.getColumnIndex(COLUMN_ORIGINAL_LANG)),
//                        cursor.getString(cursor.getColumnIndex(COLUMN_ORIGINAL_TITLE)),
//                        IntArray(0), cursor.getString(cursor.getColumnIndex(COLUMN_BACKDROP_PATH)),
//                        cursor.getInt(cursor.getColumnIndex(COLUMN_ADULT_MOVIE)) == 1,
//                        cursor.getString(cursor.getColumnIndex(COLUMN_OVERVIEW)),
//                        cursor.getString(cursor.getColumnIndex(COLUMN_RELEASE_DATE))))
//            }
//        }
//        cursor.close()
//        return movies.toTypedArray()
//    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        listPosition = movies_grid.firstVisiblePosition
        outState.putInt("position", listPosition)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_settings) {
            startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    public override fun onResume() {
        super.onResume()
        if (!movieAdapter!!.isEmpty) {
            if (isNetworkAvailable(this)) {
                refreshUI()
            } else {
                Toast.makeText(this, "Cannot refresh due to no network!", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun refreshUI() {
        moviesList.clear()
        choiceOfSort = preferences!!.getString("orderPrefKey", "1")
        favouriteView = preferences!!.getBoolean("favouriteCheckBox", false)
        if (!favouriteView) {
            getMovies()
        } else {
            // moviesArray = favouriteMovies
        }

        movieAdapter!!.notifyDataSetChanged()
    }

    companion object {

        private const val API_KEY = BuildConfig.MOVIE_DB_API_KEY

        fun isNetworkAvailable(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = connectivityManager.activeNetworkInfo
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting
        }
    }
}