package com.tomasmichalkevic.popularmovies.utils;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by tomasmichalkevic on 21/02/2018.
 */

public class JsonUtils {

    public static int[] getListFromJson(JSONArray jsonArray) throws JSONException {
        int[] data = new int[jsonArray.length()];
        for(int i = 0; i < jsonArray.length(); i++){
            data[i] = jsonArray.getInt(i);
        }
        return data;
    }

}
