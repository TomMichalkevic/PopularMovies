package com.tomasmichalkevic.popularmovies;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String KEY_ORDER_PREFERENCE = "orderPrefKey";

    private ListPreference orderPreference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.activity_preferences);

        orderPreference = (ListPreference) getPreferenceScreen().findPreference(KEY_ORDER_PREFERENCE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        orderPreference.setSummary("Current: " + orderPreference.getEntry().toString());
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(KEY_ORDER_PREFERENCE)) {
            orderPreference.setSummary("Current: " + orderPreference.getEntry().toString());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_OK, null);
        this.finish();
    }
}
