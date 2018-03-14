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

package com.tomasmichalkevic.popularmovies;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import com.tomasmichalkevic.popularmovies.utils.JsonUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

/**
 * Created by tomasmichalkevic on 20/02/2018.
 */
public class MainActivityFragment extends Fragment {

    private static final String LOG_TAG = MainActivityFragment.class.getSimpleName();

    private static final String API_KEY = BuildConfig.MOVIE_DB_API_KEY;

    private MovieAdapter movieAdapter;

    private String choiceOfSort;

    private final String popularMoviesURL = "http://api.themoviedb.org/3/movie/popular?api_key="+API_KEY;
    private final String topRatedMoviesURL = "http://api.themoviedb.org/3/movie/top_rated?api_key="+API_KEY;

    private final ArrayList<Movie> moviesList = new ArrayList<>();

    private GridView gridView;

    private SharedPreferences preferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        choiceOfSort = preferences.getString("orderPrefKey", "1");

        Movie[] movies = {};

        if(isNetworkAvailable(getContext())){
            movies = getMovies();
        }else{
            Toast.makeText(getContext(), "Cannot refresh due to no network!", Toast.LENGTH_LONG).show();
        }

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        moviesList.clear();
        Collections.addAll(moviesList, movies);

        movieAdapter = new MovieAdapter(getActivity(), moviesList);
        gridView = rootView.findViewById(R.id.movies_grid);
        gridView.setAdapter(movieAdapter);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!movieAdapter.isEmpty()){
            if(isNetworkAvailable(getContext())){
                refreshUI();
            }else{
                Toast.makeText(getContext(), "Cannot refresh due to no network!", Toast.LENGTH_LONG).show();
            }

        }

        Log.i(LOG_TAG, "OnResume");
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private void refreshUI(){
        choiceOfSort = preferences.getString("orderPrefKey", "1");
        Movie[] moviesArray = getMovies();
        moviesList.clear();
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
        int[] genreIDsList = JsonUtils.getListFromJson(array);

        return new Movie(object.getInt("vote_count"), object.getInt("id"), object.getBoolean("video"),
                object.getDouble("vote_average"), object.getString("title"), object.getDouble("popularity"), object.getString("poster_path"),
                object.getString("original_language"), object.getString("original_title"), genreIDsList, object.getString("backdrop_path"),
                object.getBoolean("adult"), object.getString("overview"), object.getString("release_date"));
    }

}
