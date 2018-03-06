package com.tomasmichalkevic.popularmovies;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by tomasmichalkevic on 21/02/2018.
 *
 * Used guidelines from https://medium.com/@JasonCromer/android-asynctask-http-request-tutorial-6b429d833e28
 */

class HttpGetRequest extends AsyncTask<String, Void, String> {

    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();

    private static final String REQUEST_METHOD = "GET";
    private static final int READ_TIMEOUT = 15000;
    private static final int CONNECTION_TIMEOUT = 15000;

    @Override
    protected String doInBackground(String... strings) {
        String urlAsString = strings[0];
        String result;
        String inputLine;

        try {

            URL url = new URL(urlAsString);

            HttpURLConnection connection =(HttpURLConnection) url.openConnection();
            connection.setRequestMethod(REQUEST_METHOD);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.connect();

            InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());

            BufferedReader reader = new BufferedReader(streamReader);
            StringBuilder stringBuilder = new StringBuilder();

            while((inputLine = reader.readLine()) != null){
                stringBuilder.append(inputLine);
            }

            reader.close();
            streamReader.close();
            result = stringBuilder.toString();
        } catch (IOException e){
            Log.e(LOG_TAG, "doInBackground: ", e);
            result = null;
        }
        return result;
    }

    @Override
    protected void onPostExecute(String result){
        super.onPostExecute(result);
    }
}
