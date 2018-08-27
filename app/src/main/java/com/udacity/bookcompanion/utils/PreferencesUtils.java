package com.udacity.bookcompanion.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.udacity.bookcompanion.R;

public class PreferencesUtils {

    public static int SORT_BY_TITLE = 0;
    public static int SORT_BY_DATE_CREATED = 1;

    public static int getSort(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        String keyForSortBy = context.getString(R.string.pref_sortby_key);

        return sp.getInt(keyForSortBy, SORT_BY_DATE_CREATED);
    }

    public static void setSort(Context context, int sort) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        String keyForSortBy = context.getString(R.string.pref_sortby_key);

        editor.putInt(keyForSortBy, sort);
        editor.apply();
    }
}