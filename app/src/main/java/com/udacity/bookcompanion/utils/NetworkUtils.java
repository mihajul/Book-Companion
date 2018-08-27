package com.udacity.bookcompanion.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {

    private static final String API_ROOT = "https://www.googleapis.com/";
    private static final String SEARCH_URL = API_ROOT + "books/v1/volumes?q=%s";

    private static final String TAG = NetworkUtils.class.getSimpleName();

    public static String getBooksJson(String keyword) {
        String urlString = String.format(SEARCH_URL, keyword);
        try {
            URL url = new URL(urlString);
            return getResponseFromHttpUrl(url);
        }catch(Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return null;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        }
        finally {
            urlConnection.disconnect();
        }
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}