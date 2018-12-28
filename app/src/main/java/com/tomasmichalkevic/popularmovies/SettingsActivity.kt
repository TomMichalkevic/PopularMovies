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
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.CheckBoxPreference
import android.preference.ListPreference
import android.preference.PreferenceActivity

class SettingsActivity : PreferenceActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    private var orderPreference: ListPreference? = null
    private var favouriteViewPreference: CheckBoxPreference? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.activity_preferences)

        orderPreference = preferenceScreen.findPreference(KEY_ORDER_PREFERENCE) as ListPreference
        favouriteViewPreference = preferenceScreen.findPreference(KEY_FAVOURITES_VIEW_KEY) as CheckBoxPreference

        val isEnabled = favouriteViewPreference!!.isChecked
        orderPreference!!.isEnabled = !isEnabled
    }

    override fun onResume() {
        super.onResume()
        orderPreference!!.summary = "Current: " + orderPreference!!.entry.toString()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key == KEY_FAVOURITES_VIEW_KEY) {
            val isEnabled = sharedPreferences.getBoolean(key, false)
            orderPreference!!.isEnabled = !isEnabled
        }

        if (key == KEY_ORDER_PREFERENCE) {
            orderPreference!!.summary = "Current: " + orderPreference!!.entry.toString()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("checkboxChecked", favouriteViewPreference!!.isChecked)
        outState.putBoolean("checkboxEnabled", favouriteViewPreference!!.isEnabled)
        outState.putString("orderValue", orderPreference!!.value)
    }

    override fun onRestoreInstanceState(state: Bundle?) {
        super.onRestoreInstanceState(state)
        if (state != null) {
            favouriteViewPreference!!.isChecked = state.getBoolean("checkboxChecked")
            favouriteViewPreference!!.isEnabled = state.getBoolean("checkboxEnabled")
            orderPreference!!.value = state.getString("orderValue")
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(Activity.RESULT_OK, null)
        this.finish()
    }

    companion object {

        private val KEY_ORDER_PREFERENCE = "orderPrefKey"
        private val KEY_FAVOURITES_VIEW_KEY = "favouriteCheckBox"
    }
}
