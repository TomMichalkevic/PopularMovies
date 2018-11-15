package com.tomasmichalkevic.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import com.tomasmichalkevic.popularmovies.data.FavouritesContract;
import com.tomasmichalkevic.popularmovies.utils.JsonUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.tomasmichalkevic.popularmovies.data.FavouritesContract.FavouriteEntry.COLUMN_ADULT_MOVIE;
import static com.tomasmichalkevic.popularmovies.data.FavouritesContract.FavouriteEntry.COLUMN_BACKDROP_PATH;
import static com.tomasmichalkevic.popularmovies.data.FavouritesContract.FavouriteEntry.COLUMN_ID;
import static com.tomasmichalkevic.popularmovies.data.FavouritesContract.FavouriteEntry.COLUMN_ORIGINAL_LANG;
import static com.tomasmichalkevic.popularmovies.data.FavouritesContract.FavouriteEntry.COLUMN_ORIGINAL_TITLE;
import static com.tomasmichalkevic.popularmovies.data.FavouritesContract.FavouriteEntry.COLUMN_OVERVIEW;
import static com.tomasmichalkevic.popularmovies.data.FavouritesContract.FavouriteEntry.COLUMN_POPULARITY;
import static com.tomasmichalkevic.popularmovies.data.FavouritesContract.FavouriteEntry.COLUMN_POSTER_PATH;
import static com.tomasmichalkevic.popularmovies.data.FavouritesContract.FavouriteEntry.COLUMN_RELEASE_DATE;
import static com.tomasmichalkevic.popularmovies.data.FavouritesContract.FavouriteEntry.COLUMN_TITLE;
import static com.tomasmichalkevic.popularmovies.data.FavouritesContract.FavouriteEntry.COLUMN_VIDEO;
import static com.tomasmichalkevic.popularmovies.data.FavouritesContract.FavouriteEntry.COLUMN_VOTE_AVERAGE;

public class MainActivity extends Activity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final String API_KEY = BuildConfig.MOVIE_DB_API_KEY;

    private MovieAdapter movieAdapter;

    private String choiceOfSort;
    private boolean favouriteView;

    private final String popularMoviesURL = "http://api.themoviedb.org/3/movie/popular?api_key="+API_KEY;
    private final String topRatedMoviesURL = "http://api.themoviedb.org/3/movie/top_rated?api_key="+API_KEY;

    private final ArrayList<Movie> moviesList = new ArrayList<>();

    private SharedPreferences preferences;

    private int listPosition = 0;

    @BindView(R.id.movies_grid)
    GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        if(savedInstanceState != null){
            listPosition = savedInstanceState.getInt("position");
            gridView.smoothScrollToPosition(listPosition);
        }
            preferences = PreferenceManager.getDefaultSharedPreferences(this);

            choiceOfSort = preferences.getString("orderPrefKey", "1");
            favouriteView = preferences.getBoolean("favouriteCheckBox", false);

            Movie[] movies = {};

            if(isNetworkAvailable(this)){
                if(favouriteView){
                    movies = getFavouriteMovies();
                }else{
                    movies = getMovies();
                }

            }else{
                Toast.makeText(this, "Cannot refresh due to no network!", Toast.LENGTH_LONG).show();
            }

            moviesList.clear();
            Collections.addAll(moviesList, movies);

            movieAdapter = new MovieAdapter(this, moviesList);
            gridView.setAdapter(movieAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
        }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        listPosition = gridView.getFirstVisiblePosition();
        outState.putInt("position", listPosition);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!movieAdapter.isEmpty()){
            if(isNetworkAvailable(this)){
                refreshUI();
            }else{
                Toast.makeText(this, "Cannot refresh due to no network!", Toast.LENGTH_LONG).show();
            }

        }
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private void refreshUI(){
        Movie[] moviesArray;
        moviesList.clear();
        choiceOfSort = preferences.getString("orderPrefKey", "1");
        favouriteView = preferences.getBoolean("favouriteCheckBox", false);
        if(!favouriteView){
            moviesArray = getMovies();
        }else{
            moviesArray = getFavouriteMovies();
        }

        Collections.addAll(moviesList, moviesArray);
        movieAdapter.notifyDataSetChanged();
    }

    private String getResponseJSON(){
        String result = "";

        HttpGetRequest httpGetRequest = new HttpGetRequest();

        try {
            switch(choiceOfSort){
                case "1":{
                    result = httpGetRequest.execute(popularMoviesURL).get();
                }
                break;
                case "2": {
                    result = httpGetRequest.execute(topRatedMoviesURL).get();
                }
                break;
            }


        } catch (InterruptedException | ExecutionException e) {
            Log.e(LOG_TAG, "getResponseJSON: ", e);
        } finally {
            return result;
        }
    }

    private Movie[] getFavouriteMovies(){
        Movie[] movies = new Movie[0];

        String sortOrder =
                COLUMN_ID + " ASC";

        Cursor cursor = getContentResolver().query(FavouritesContract.FavouriteEntry.CONTENT_URI,null, null, null, sortOrder);

        if(cursor.moveToFirst()){
            movies = new Movie[cursor.getCount()];
            movies[0] = new Movie(1,
                    cursor.getInt(cursor.getColumnIndex(COLUMN_ID)),
                    cursor.getInt(cursor.getColumnIndex(COLUMN_VIDEO))==1,
                    cursor.getDouble(cursor.getColumnIndex(COLUMN_VOTE_AVERAGE)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)),
                    cursor.getDouble(cursor.getColumnIndex(COLUMN_POPULARITY)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_POSTER_PATH)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_ORIGINAL_LANG)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_ORIGINAL_TITLE)),
                    new int[0], cursor.getString(cursor.getColumnIndex(COLUMN_BACKDROP_PATH)),
                    cursor.getInt(cursor.getColumnIndex(COLUMN_ADULT_MOVIE))==1,
                    cursor.getString(cursor.getColumnIndex(COLUMN_OVERVIEW)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_RELEASE_DATE)));
            int i = 1;
            while(cursor.moveToNext()){
                movies[i] = new Movie(1,
                        cursor.getInt(cursor.getColumnIndex(COLUMN_ID)),
                        cursor.getInt(cursor.getColumnIndex(COLUMN_VIDEO))==1,
                        cursor.getDouble(cursor.getColumnIndex(COLUMN_VOTE_AVERAGE)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)),
                        cursor.getDouble(cursor.getColumnIndex(COLUMN_POPULARITY)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_POSTER_PATH)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_ORIGINAL_LANG)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_ORIGINAL_TITLE)),
                        new int[0], cursor.getString(cursor.getColumnIndex(COLUMN_BACKDROP_PATH)),
                        cursor.getInt(cursor.getColumnIndex(COLUMN_ADULT_MOVIE))==1,
                        cursor.getString(cursor.getColumnIndex(COLUMN_OVERVIEW)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_RELEASE_DATE)));
                i++;
            }
        }
        cursor.close();
        return movies;
    }

    private Movie[] getMovies(){
        String result = getResponseJSON();
        JSONObject jsonObject;
        Movie[] moviesArray;

        try {
            jsonObject = new JSONObject(result);
            JSONArray movies = jsonObject.getJSONArray("results");
            moviesArray = new Movie[movies.length()];
            for(int i = 0; i < movies.length(); i++){
                moviesArray[i] = getMovie((JSONObject) movies.get(i));
            }
            return moviesArray;
        } catch (JSONException e) {
            Log.e(LOG_TAG, "JSON malformed", e);
            return null;
        }
    }

    private Movie getMovie(JSONObject object) throws JSONException {
        JSONArray array = object.getJSONArray("genre_ids");
        int[] genreIDsList = JsonUtils.INSTANCE.getListFromJson(array);

        return new Movie(object.getInt("vote_count"), object.getInt("id"), object.getBoolean("video"),
                object.getDouble("vote_average"), object.getString("title"), object.getDouble("popularity"), object.getString("poster_path"),
                object.getString("original_language"), object.getString("original_title"), genreIDsList, object.getString("backdrop_path"),
                object.getBoolean("adult"), object.getString("overview"), object.getString("release_date"));
    }

}