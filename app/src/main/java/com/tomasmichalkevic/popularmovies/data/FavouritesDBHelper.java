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

package com.tomasmichalkevic.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by tomasmichalkevic on 14/03/2018.
 */

public class FavouritesDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "favourites.db";
    private static final int DATABASE_VERSION = 1;

    public FavouritesDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " +
                FavouritesContract.FavouriteEntry.TABLE_FAVOURITES + "(" + FavouritesContract.FavouriteEntry._ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FavouritesContract.FavouriteEntry.COLUMN_ID + " INTEGER NOT NULL, " +
                FavouritesContract.FavouriteEntry.COLUMN_VIDEO + " NUMERIC NOT NULL, " +
                FavouritesContract.FavouriteEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL, " +
                FavouritesContract.FavouriteEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                FavouritesContract.FavouriteEntry.COLUMN_POPULARITY + " REAL NOT NULL, " +
                FavouritesContract.FavouriteEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                FavouritesContract.FavouriteEntry.COLUMN_ORIGINAL_LANG + " TEXT NOT NULL, " +
                FavouritesContract.FavouriteEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                FavouritesContract.FavouriteEntry.COLUMN_BACKDROP_PATH + " TEXT NOT NULL, " +
                FavouritesContract.FavouriteEntry.COLUMN_ADULT_MOVIE + " NUMERIC NOT NULL, " +
                FavouritesContract.FavouriteEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                FavouritesContract.FavouriteEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL);";

        db.execSQL(SQL_CREATE_MOVIE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FavouritesContract.FavouriteEntry.TABLE_FAVOURITES);
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                FavouritesContract.FavouriteEntry.TABLE_FAVOURITES + "'");

        // re-create database
        onCreate(db);
    }
}
